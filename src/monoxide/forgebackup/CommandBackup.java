package monoxide.forgebackup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;

import com.google.common.collect.Lists;

public class CommandBackup extends CommandBackupBase {
	
	public CommandBackup(MinecraftServer server) {
		super(server);
	}
	
	@Override
	public String getCommandName() {
		return "backup";
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		if (sender instanceof EntityPlayer) {
			if (!server.isDedicatedServer()) {
				return true;
			}
		}
		
		return super.canCommandSenderUseCommand(sender);
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		boolean failure = false;
		notifyBackupAdmins(sender, "ForgeBackup.backup.start");
		
		notifyBackupAdmins(sender, "ForgeBackup.save.disabled");
		toggleSavability(false);
		
		try
		{
			notifyBackupAdmins(sender, "ForgeBackup.save.force");
			forceSaveAllWorlds();
			
			notifyBackupAdmins(sender, "ForgeBackup.backup.progress");
			doBackup(sender);
		}
		catch (MinecraftException e)
		{
			notifyBackupAdmins(sender, Level.SEVERE, "ForgeBackup.backup.aborted");
			BackupLog.log(Level.SEVERE, e, e.getMessage());
			return;
		} catch (IOException e) {
			notifyBackupAdmins(sender, Level.SEVERE, "ForgeBackup.backup.aborted");
			BackupLog.log(Level.SEVERE, e, e.getMessage());
			return;
		} finally {
			notifyBackupAdmins(sender, "ForgeBackup.save.enabled");
			toggleSavability(true);
		}
		
		notifyBackupAdmins(sender, "ForgeBackup.backup.complete");
	}

	private void toggleSavability(boolean canSave) {
		for (int i = 0; i < server.worldServers.length; ++i)
		{
			if (server.worldServers[i] != null)
			{
				WorldServer worldServer = server.worldServers[i];
				worldServer.canNotSave = !canSave;
			}
		}
	}

	private void forceSaveAllWorlds()
	throws MinecraftException 
	{
		if (server.getConfigurationManager() != null)
		{
			server.getConfigurationManager().saveAllPlayerData();
		}
		
		for (int i = 0; i < server.worldServers.length; ++i)
		{
			if (server.worldServers[i] != null)
			{
				WorldServer var5 = server.worldServers[i];
				boolean var6 = var5.canNotSave;
				var5.canNotSave = false;
				var5.saveAllChunks(true, (IProgressUpdate)null);
				var5.canNotSave = var6;
			}
		}
	}

	private void doBackup(ICommandSender sender)
	throws IOException
	{
		ISaveHandler saveHandler = server.worldServers[0].getSaveHandler();
		File backupsFolder = server.getFile(ForgeBackup.instance().getBackupFolderName() + "/" + saveHandler.getSaveDirectoryName());
		if (backupsFolder.exists() && !backupsFolder.isDirectory()) {
			notifyBackupAdmins(sender, Level.WARNING, "ForgeBackup.backup.folderExists");
			return;
		} else if (!backupsFolder.exists()) {
			backupsFolder.mkdirs();
		}
		
		File backupFile = new File(backupsFolder, getBackupFileName());
		ZipOutputStream backup = new ZipOutputStream(new FileOutputStream(backupFile));
		List<File> saveDirectories = Lists.newArrayList(server.getFile("config"));
		
		if (saveHandler instanceof SaveHandler) {
			saveDirectories.add(((SaveHandler)saveHandler).getSaveDirectory());
		} else {
			saveDirectories.add(server.getFile(saveHandler.getSaveDirectoryName()));
		}
		byte[] buffer = new byte[4096];
		int readBytes;
		while (!saveDirectories.isEmpty()) {
			File current = saveDirectories.remove(0);
			
			for (File child : current.listFiles()) {
				if (child.isDirectory()) {
					saveDirectories.add(child);
				} else {
					backup.putNextEntry(new ZipEntry(cleanZipPath(child.getCanonicalPath())));
					
					try {
						InputStream currentStream = new FileInputStream(child);
						while ((readBytes = currentStream.read(buffer)) >= 0) {
							backup.write(buffer, 0, readBytes);
						}
						currentStream.close();
					} catch (IOException e) {
						BackupLog.warning("Couldn't backup file: %s", child.getPath());
					}
					backup.closeEntry();
				}
			}
		}
		
		backup.close();
	}
	
	private String getBackupFileName() {
		Date now = new Date();
		return String.format("%TY%Tm%Td-%TH%TM%TS.zip", now, now, now, now, now, now);
	}
	
	private String cleanZipPath(String path)
	throws IOException
	{
		String dataDirectory = server.getFile(".").getCanonicalPath();
		if (path.substring(0, dataDirectory.length()).equals(dataDirectory)) {
			return path.substring(dataDirectory.length()+1);
		}
		
		return path;
	}
}
