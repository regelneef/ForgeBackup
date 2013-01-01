package monoxide.forgebackup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;

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
	public int getRequiredPermissionLevel() {
		return 4;
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
		File backupsFolder = server.getFile("backups");
		if (backupsFolder.exists() && !backupsFolder.isDirectory()) {
			notifyBackupAdmins(sender, Level.WARNING, "ForgeBackup.backup.folderExists");
			return;
		} else if (!backupsFolder.exists()) {
			backupsFolder.mkdir();
		}
		
		File backupFile = new File(backupsFolder, getBackupFileName());
		ZipOutputStream backup = new ZipOutputStream(Files.newOutputStream(backupFile.toPath()));
		
		List<File> saveDirectories = Lists.newArrayList(server.getFile(server.worldServers[0].getSaveHandler().getSaveDirectoryName()), server.getFile("config"));
		byte[] buffer = new byte[4096];
		int readBytes;
		while (!saveDirectories.isEmpty()) {
			File current = saveDirectories.remove(0);
			
			for (File child : current.listFiles()) {
				if (child.isDirectory()) {
					saveDirectories.add(child);
				} else {
					backup.putNextEntry(new ZipEntry(child.getPath().substring(2)));
					
					try {
						InputStream currentStream = Files.newInputStream(child.toPath(), StandardOpenOption.READ);
						while ((readBytes = currentStream.read(buffer)) >= 0) {
							backup.write(buffer, 0, readBytes);
						}
					} catch (IOException e) {
						BackupLog.warning("Couldn't backup file: %s", child.toPath());
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
	
}
