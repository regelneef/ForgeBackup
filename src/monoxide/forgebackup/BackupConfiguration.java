package monoxide.forgebackup;

import java.io.File;
import java.lang.reflect.Field;
import java.util.logging.Level;

import net.minecraftforge.common.Configuration;

public class BackupConfiguration {
	@ConfigOption(comment = "Interval in minutes between automatic backup attempts.")
	protected int backupInterval = 15;
	
	@ConfigOption(comment = "Only operators may manually run backups with /backup.")
	protected boolean opsOnly = true;
	
	@ConfigOption(comment = "Allow command blocks to initiate a backup.")
	protected boolean commandBlocksAllowed = false;
	
	@ConfigOption(comment = "Folder name to store backups in. Each world's backups will be stored in subfolders of this one.")
	protected String backupFolder = "backups";
	
	@ConfigOption(comment = "Only run automated backups when there is a player connected to the server. No effect in SSP.")
	protected boolean backupOnlyWithPlayer = true ;
	
	@ConfigOption(section = "backup", name = "configuration", comment = "Backup config folder.")
	protected boolean backupConfiguration = true;
	
	@ConfigOption(section = "backup", name = "serverConfiguration", comment = "Backup server configuration files. eg. server.properties, whitelist.txt")
	protected boolean backupServerConfiguration = false;
	
	@ConfigOption(section = "backup", name = "world", comment = "Backup world folder.")
	protected boolean backupWorld = true;
	
	@ConfigOption(section = "backup", name = "mods", comment = "Backup mods folder.")
	protected boolean backupMods = false;
	
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
				} else if (fieldType == int.class) {
					int value = field.getInt(this);
					value = config.get(option.section(), name, value, comment).getInt(value);
					field.set(this, value);
				} else if (fieldType == double.class) {
					double value = field.getDouble(this);
					value = config.get(option.section(), name, value, comment).getDouble(value);
					field.set(this, value);
				} else if (fieldType == String.class) {
					String value = (String)field.get(this);
					value = config.get(option.section(), name, value, comment).value;
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
