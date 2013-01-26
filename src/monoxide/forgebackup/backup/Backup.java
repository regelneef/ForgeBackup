package monoxide.forgebackup.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import monoxide.forgebackup.BackupLog;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.LanguageRegistry;

public class Backup {
	private final BackupSettings settings;
	
	public Backup(BackupSettings settings) {
		this.settings = settings;
	}
	
	public void run(ICommandSender sender) {
		boolean failure = false;
		notifyAdmins(sender, "ForgeBackup.backup.start");
		
		notifyAdmins(sender, Level.FINE, "ForgeBackup.save.disabled");
		toggleSavability(false);
		
		try
		{
			notifyAdmins(sender, Level.FINE, "ForgeBackup.save.force");
			forceSaveAllWorlds();
			
			notifyAdmins(sender, Level.FINE, "ForgeBackup.backup.progress");
			doBackup(sender);
		}
		catch (MinecraftException e)
		{
			notifyAdmins(sender, Level.SEVERE, "ForgeBackup.backup.aborted");
			BackupLog.log(Level.SEVERE, e, e.getMessage());
			return;
		} catch (IOException e) {
			notifyAdmins(sender, Level.SEVERE, "ForgeBackup.backup.aborted");
			BackupLog.log(Level.SEVERE, e, e.getMessage());
			return;
		} finally {
			notifyAdmins(sender, Level.FINE, "ForgeBackup.save.enabled");
			toggleSavability(true);
		}
		
		notifyAdmins(sender, "ForgeBackup.backup.complete");
	}
	
	private void toggleSavability(boolean canSave) {
		for (int i = 0; i < settings.getServer().worldServers.length; ++i)
		{
			if (settings.getServer().worldServers[i] != null)
			{
				WorldServer worldServer = settings.getServer().worldServers[i];
				worldServer.canNotSave = !canSave;
			}
		}
	}

	private void forceSaveAllWorlds()
	throws MinecraftException 
	{
		if (settings.getServer().getConfigurationManager() != null)
		{
			settings.getServer().getConfigurationManager().saveAllPlayerData();
		}
		
		for (int i = 0; i < settings.getServer().worldServers.length; ++i)
		{
			if (settings.getServer().worldServers[i] != null)
			{
				WorldServer var5 = settings.getServer().worldServers[i];
				boolean var6 = var5.canNotSave;
				var5.canNotSave = false;
				var5.saveAllChunks(true, null);
				var5.canNotSave = var6;
			}
		}
	}

	private void doBackup(ICommandSender sender)
	throws IOException
	{
		ISaveHandler saveHandler = settings.getServer().worldServers[0].getSaveHandler();
		File backupsFolder = new File(settings.getBackupFolder(), saveHandler.getSaveDirectoryName());
		if (backupsFolder.exists() && !backupsFolder.isDirectory()) {
			notifyAdmins(sender, Level.WARNING, "ForgeBackup.backup.folderExists");
			return;
		} else if (!backupsFolder.exists()) {
			backupsFolder.mkdirs();
		}
		
		int maxBackups = settings.getMaximumBackups();
		if (maxBackups > 0) {
			List<File> backups = Lists.newArrayList(backupsFolder.listFiles());
			Collections.sort(backups);
			
			while (backups.size() >= maxBackups) {
				File backup = backups.remove(0);
				backup.delete();
			}
		}
		
		List<File> thingsToSave = settings.getFilesToBackup(saveHandler);
		File backupFile = new File(backupsFolder, settings.getBackupFileName());
		createNewBackup(backupFile, thingsToSave);
	}
	
	private void createNewBackup(File backupFile, List<File> toBackup)
	throws IOException
	{
		ZipOutputStream backup = new ZipOutputStream(new FileOutputStream(backupFile));
		byte[] buffer = new byte[4096];
		int readBytes;
		List<Integer> disabledDimensions = settings.getDisabledDimensions();
		
		while (!toBackup.isEmpty()) {
			File current = toBackup.remove(0);
			if (!current.exists()) { continue; }
			
			if (current.isDirectory()) {
				boolean disabled = false;
				for (int dimension : disabledDimensions) {
					if (current.getName().equals(String.format("DIM%d", dimension))) {
						disabled = true;
					}
				}
				
				if (!disabled) {
					for (File child : current.listFiles()) {
						toBackup.add(child);
					}
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
	
	private String cleanZipPath(String path)
	throws IOException
	{
		String dataDirectory = settings.getServer().getFile(".").getCanonicalPath();
		if (path.substring(0, dataDirectory.length()).equals(dataDirectory)) {
			return path.substring(dataDirectory.length()+1);
		}
		
		return path;
	}
	
	public void notifyAdmins(ICommandSender sender, String translationKey, Object... parameters) {
		notifyAdmins(sender, Level.INFO, translationKey, parameters);
	}
	
	public void notifyAdmins(ICommandSender sender, Level level, String translationKey, Object... parameters) {
		boolean var5 = true;
		String message = String.format(LanguageRegistry.instance().getStringLocalization(translationKey), parameters);
		
		if (sender instanceof TileEntityCommandBlock && !settings.getServer().worldServers[0].getGameRules().getGameRuleBooleanValue("commandBlockOutput"))
		{
			var5 = false;
		}
		
		if (var5)
		{
			for (EntityPlayerMP player : (List<EntityPlayerMP>)settings.getServer().getConfigurationManager().playerEntityList)
			{
				if ((settings.getServer().getConfigurationManager().areCommandsAllowed(player.username) || !settings.getServer().isDedicatedServer()) && 
				    (settings.verboseLogging() || level != Level.FINE))
				{
					player.sendChatToPlayer("\u00a77\u00a7o[" + sender.getCommandSenderName() + ": " + message + "]");
				}
			}
		}
		
		if (settings.verboseLogging() && level == Level.FINE) {
			BackupLog.log(Level.INFO, message);
		} else {
			BackupLog.log(level, message);
		}
	}
}
