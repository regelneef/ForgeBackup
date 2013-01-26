package monoxide.forgebackup.backup;

import java.io.File;
import java.util.Date;
import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;

import com.google.common.collect.Lists;

public class BackupSettings {
	private final int maximumBackups;
	private final String backupFolder;
	private final boolean backupWorld;
	private final boolean backupConfiguration;
	private final boolean backupMods;
	private final boolean backupServerConfiguration;
	private final String[] extraFiles;
	private final Integer[] disabledDimensions;
	private final boolean verboseLogging;
	private final MinecraftServer server;

	public BackupSettings(
			MinecraftServer server, String backupFolder, boolean verboseLogging, int maximumBackups,
			boolean backupWorld, boolean backupConfiguration, boolean backupMods, boolean backupServerConfiguration, String[] extraFiles,
			int[] disabledDimensions
	) {
		this.server = server;
		this.backupFolder = backupFolder;
		this.verboseLogging = verboseLogging;
		this.maximumBackups = maximumBackups;
		this.backupWorld = backupWorld;
		this.backupConfiguration = backupConfiguration;
		this.backupMods = backupMods;
		this.backupServerConfiguration = backupServerConfiguration;
		this.extraFiles = extraFiles;
		this.disabledDimensions = new Integer[disabledDimensions.length];
		
		for (int i = 0; i < disabledDimensions.length; i++) {
			this.disabledDimensions[i] = (Integer)disabledDimensions[i];
		}
	}
	
	public BackupSettings(
			MinecraftServer server, String backupFolder, boolean verboseLogging, int maximumBackups,
			boolean backupWorld, boolean backupConfiguration, boolean backupMods, boolean backupServerConfiguration, String[] extraFiles,
			Integer[] disabledDimensions
	) {
		this.server = server;
		this.backupFolder = backupFolder;
		this.verboseLogging = verboseLogging;
		this.maximumBackups = maximumBackups;
		this.backupWorld = backupWorld;
		this.backupConfiguration = backupConfiguration;
		this.backupMods = backupMods;
		this.backupServerConfiguration = backupServerConfiguration;
		this.extraFiles = extraFiles;
		this.disabledDimensions = disabledDimensions;
	}
	
	public int getMaximumBackups() {
		return maximumBackups;
	}
	
	public boolean willBackupWorld() {
		return backupWorld;
	}
	
	public boolean willBackupConfiguration() {
		return backupConfiguration;
	}
	
	public boolean willBackupMods() {
		return backupMods;
	}
	
	public boolean willBackupServerConfiguration() {
		return backupServerConfiguration;
	}
	
	public List<File> getExtraFilesToBackup(MinecraftServer server) {
		List<File> files = Lists.newArrayList();
		for (String file : extraFiles) {
			files.add(server.getFile(file));
		}
		return files;
	}
	
	public List<Integer> getDisabledDimensions() {
		return Lists.newArrayList(disabledDimensions);
	}
	
	public boolean verboseLogging() {
		return verboseLogging;
	}
	
	public MinecraftServer getServer() {
		return server;
	}
	
	public File getBackupFolder() {
		File absoluteFile = new File(backupFolder);
		return absoluteFile.getAbsolutePath() == backupFolder ? absoluteFile : server.getFile(backupFolder);
	}
	
	public String getBackupFileName() {
		Date now = new Date();
		return String.format("%TY%Tm%Td-%TH%TM%TS.zip", now, now, now, now, now, now);
	}

	public List<File> getFilesToBackup(ISaveHandler saveHandler) {
		List<File> thingsToSave = Lists.newArrayList(getExtraFilesToBackup(server));
	
		if (willBackupWorld()) {
			if (saveHandler instanceof SaveHandler) {
				thingsToSave.add(((SaveHandler)saveHandler).getSaveDirectory());
			} else {
				thingsToSave.add(server.getFile(saveHandler.getSaveDirectoryName()));
			}
		}
		
		if (willBackupConfiguration()) {
			thingsToSave.add(server.getFile("config"));
		}
		
		if (willBackupMods()) {
			thingsToSave.add(server.getFile("mods"));
			thingsToSave.add(server.getFile("coremods"));
		}
		
		if (willBackupServerConfiguration()) {
			thingsToSave.add(server.getFile("banned-ips.txt"));
			thingsToSave.add(server.getFile("banned-players.txt"));
			thingsToSave.add(server.getFile("ops.txt"));
			thingsToSave.add(server.getFile("server.properties"));
			thingsToSave.add(server.getFile("white-list.txt"));
		}
		
		return thingsToSave;
	}
}
