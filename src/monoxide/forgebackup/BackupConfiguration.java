package monoxide.forgebackup;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;

import com.google.common.collect.Lists;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;

public class BackupConfiguration {
	@ConfigOption(comment = "Interval in minutes between automatic backup attempts.")
	protected int backupInterval = 15;
	
	@ConfigOption(comment = "Maximum backups to keep stored. Older backups will be deleted first. -1 will disable auto-cleanups.")
	protected int maxBackups = -1;
	
	@ConfigOption(comment = "Only operators may manually run backups with /backup.")
	protected boolean opsOnly = true;
	
	@ConfigOption(comment = "Allow command blocks to initiate a backup.")
	protected boolean commandBlocksAllowed = false;
	
	@ConfigOption(comment = "Folder name to store backups in. Each world's backups will be stored in subfolders of this one.")
	protected String backupFolder = "backups";
	
	@ConfigOption(comment = "Only run automated backups when there is a player connected to the server. No effect in SSP.")
	protected boolean backupOnlyWithPlayer = true;
	
	@ConfigOption(comment = "Output extra information while backing up.")
	protected boolean verboseLogging = false;
	
	@ConfigOption(section = "backup", name = "configuration", comment = "Backup config folder.")
	protected boolean backupConfiguration = true;
	
	@ConfigOption(section = "backup", name = "serverConfiguration", comment = "Backup server configuration files. eg. server.properties, whitelist.txt")
	protected boolean backupServerConfiguration = false;
	
	@ConfigOption(section = "backup", name = "world", comment = "Backup world folder.")
	protected boolean backupWorld = true;
	
	@ConfigOption(section = "backup", comment = "List of dimension id's to *not* backup. Use this to disable dimensions that are large or unneeded.")
	protected int[] disabledDimensions = new int[] {};
	
	@ConfigOption(section = "backup", name = "mods", comment = "Backup mods folder.")
	protected boolean backupMods = false;
	
	@ConfigOption(section = "backup", name = "other", comment = "Other files or directories to backup.")
	protected String[] backupOthers = new String[] {};
	
	public int getBackupInterval() {
		return backupInterval;
	}
	
	public boolean onlyOperatorsCanManuallyBackup() {
		return opsOnly;
	}
	
	public boolean onlyRunBackupsWithPlayersOnline() {
		return backupOnlyWithPlayer;
	}
	
	public boolean canCommandBlocksUseCommands() {
		return commandBlocksAllowed;
	}
	
	public String getBackupFolderName() {
		return backupFolder;
	}
	
	public boolean willBackupConfiguration() {
		return backupConfiguration;
	}
	
	public boolean willBackupServerConfiguration() {
		return backupServerConfiguration;
	}
	
	public boolean willBackupWorld() {
		return backupWorld;
	}
	
	public boolean willBackupMods() {
		return backupMods;
	}
	
	public File[] getExtraFilesToBackup(MinecraftServer server) {
		File[] files = new File[backupOthers.length];
		for (int i = 0; i < backupOthers.length; i++) {
			files[i] = server.getFile(backupOthers[i]);
		}
		return files;
	}
	
	public List<Integer> getDisabledDimensions() {
		List<Integer> dimensions = Lists.newArrayList();
		for (int i : disabledDimensions) {
			if (i == 0) {
				BackupLog.warning("Cannot disable overworld from backups.");
				continue;
			}
			dimensions.add(i);
		}
		return dimensions;
	}
	
	public boolean verboseLogging() {
		return verboseLogging;
	}
	
	public int getMaximumBackups() {
		return maxBackups;
	}
	
	public BackupConfiguration(File configFile) {
		try {
			Configuration config = new Configuration(configFile);
			config.load();
			
			for (Field field : this.getClass().getDeclaredFields()) {
				ConfigOption option = field.getAnnotation(ConfigOption.class);
				if (option == null) { continue; }
				
				String name = option.name();
				String comment = option.comment().isEmpty() ? null : option.comment();
				if (name.isEmpty()) {
					name = field.getName();
				}
				
				Class fieldType = field.getType();
				if (fieldType == boolean.class) {
					boolean value = field.getBoolean(this);
					value = config.get(option.section(), name, value, comment).getBoolean(value);
					field.set(this, value);
				} else if (fieldType == boolean[].class) {
					boolean[] value = (boolean[])field.get(this);
					value = config.get(option.section(), name, value, comment).getBooleanList();
					field.set(this, value);
				} else if (fieldType == int.class) {
					int value = field.getInt(this);
					value = config.get(option.section(), name, value, comment).getInt(value);
					field.set(this, value);
				} else if (fieldType == int[].class) {
					int[] value = (int[])field.get(this);
					value = config.get(option.section(), name, value, comment).getIntList();
					field.set(this, value);
				} else if (fieldType == double.class) {
					double value = field.getDouble(this);
					value = config.get(option.section(), name, value, comment).getDouble(value);
					field.set(this, value);
				} else if (fieldType == double[].class) {
					double[] value = (double[])field.get(this);
					value = config.get(option.section(), name, value, comment).getDoubleList();
					field.set(this, value);
				} else if (fieldType == String.class) {
					String value = (String)field.get(this);
					value = config.get(option.section(), name, value, comment).value;
					field.set(this, value);
				} else if (fieldType == String[].class) {
					String[] value = (String[])field.get(this);
					value = config.get(option.section(), name, value, comment).valueList;
					field.set(this, value);
				} else {
					BackupLog.warning("Skipping @ConfigOption \"%s\" with unknown type: %s", field.getName(), fieldType.getCanonicalName());
					continue;
				}
			}
			
			config.save();
		} catch (Exception e) {
			BackupLog.log(Level.SEVERE, e, "There was a problem loading the configuration.");
		}
	}
}
