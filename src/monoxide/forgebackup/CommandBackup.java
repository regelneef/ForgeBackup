package monoxide.forgebackup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
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
		
		notifyBackupAdmins(sender, Level.FINE, "ForgeBackup.save.disabled");
		toggleSavability(false);
		
		try
		{
			notifyBackupAdmins(sender, Level.FINE, "ForgeBackup.save.force");
			forceSaveAllWorlds();
			
			notifyBackupAdmins(sender, Level.FINE, "ForgeBackup.backup.progress");
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
			notifyBackupAdmins(sender, Level.FINE, "ForgeBackup.save.enabled");
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
		File backupsFolder = new File(getBackupFolder(), saveHandler.getSaveDirectoryName());
		if (backupsFolder.exists() && !backupsFolder.isDirectory()) {
			notifyBackupAdmins(sender, Level.WARNING, "ForgeBackup.backup.folderExists");
			return;
		} else if (!backupsFolder.exists()) {
			backupsFolder.mkdirs();
		}
		
		int maxBackups = ForgeBackup.instance().config().getMaximumBackups();
		if (maxBackups > 0) {
			List<File> backups = Lists.newArrayList(backupsFolder.listFiles());
			Collections.sort(backups);
			
			while (backups.size() >= maxBackups) {
				File backup = backups.remove(0);
				backup.delete();
			}
		}
		
		List<File> thingsToSave = Lists.newArrayList();
		
		if (ForgeBackup.instance().config().willBackupWorld()) {
			if (saveHandler instanceof SaveHandler) {
				thingsToSave.add(((SaveHandler)saveHandler).getSaveDirectory());
			} else {
				thingsToSave.add(server.getFile(saveHandler.getSaveDirectoryName()));
			}
		}
		
		if (ForgeBackup.instance().config().willBackupConfiguration()) {
			thingsToSave.add(server.getFile("config"));
		}
		
		if (ForgeBackup.instance().config().willBackupMods()) {
			thingsToSave.add(server.getFile("mods"));
			thingsToSave.add(server.getFile("coremods"));
		}
		
		if (ForgeBackup.instance().config().willBackupServerConfiguration()) {
			thingsToSave.add(server.getFile("banned-ips.txt"));
			thingsToSave.add(server.getFile("banned-players.txt"));
			thingsToSave.add(server.getFile("ops.txt"));
			thingsToSave.add(server.getFile("server.properties"));
			thingsToSave.add(server.getFile("white-list.txt"));
		}

		File backupFile = new File(backupsFolder, getBackupFileName());
		createNewBackup(backupFile, thingsToSave);
	}
	
	private void createNewBackup(File backupFile, List<File> toBackup)
	throws IOException
	{
		ZipOutputStream backup = new ZipOutputStream(new FileOutputStream(backupFile));
		byte[] buffer = new byte[4096];
		int readBytes;
		while (!toBackup.isEmpty()) {
			File current = toBackup.remove(0);
			if (!current.exists()) { continue; }
			
			if (current.isDirectory()) {
				for (File child : current.listFiles()) {
					toBackup.add(child);
				}
			} else {
				backup.putNextEntry(new ZipEntry(cleanZipPath(current.getCanonicalPath())));
				
				try {
					InputStream currentStream = new FileInputStream(current);
					while ((readBytes = currentStream.read(buffer)) >= 0) {
						backup.write(buffer, 0, readBytes);
					}
					currentStream.close();
				} catch (IOException e) {
					BackupLog.warning("Couldn't backup file: %s", current.getPath());
				}
				backup.closeEntry();
			}
		}
		
		backup.close();
	}
	
	private File getBackupFolder() {
		String backupFolder = ForgeBackup.instance().config().getBackupFolderName();
		File absoluteFile = new File(backupFolder);
		return absoluteFile.getAbsolutePath() == backupFolder ? absoluteFile : server.getFile(backupFolder);
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
