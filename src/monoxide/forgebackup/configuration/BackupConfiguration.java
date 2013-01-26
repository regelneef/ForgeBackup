package monoxide.forgebackup.configuration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;

import com.google.common.collect.Lists;

import monoxide.forgebackup.BackupLog;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import net.minecraftforge.common.Property.Type;

public class BackupConfiguration {
	////////////////////////////////////////////////////////
	//                   GENERAL                          //
	////////////////////////////////////////////////////////
	@Section(section = Sections.GENERAL, comment = "General configuration options are here")
	protected ConfigCategory general;
	
	@Option(comment = "Interval in minutes between automatic backup attempts.")
	protected int backupInterval = 15;
	
	@Option(comment = "Maximum backups to keep stored. Older backups will be deleted first. -1 will disable auto-cleanups.")
	protected int maxBackups = -1;
	
	@Option(comment = "Only operators may manually run backups with /backup.")
	protected boolean opsOnly = true;
	
	@Option(comment = "Allow command blocks to initiate a backup.")
	protected boolean commandBlocksAllowed = false;
	
	@Option(comment = "Only run automated backups when there is a player connected to the server. No effect in SSP. No effect on long-term backups.")
	protected boolean backupOnlyWithPlayer = true;
	
	@Option(comment = "Output extra information while backing up.")
	protected boolean verboseLogging = false;
	
	////////////////////////////////////////////////////////
	//                   BACKUP                           //
	////////////////////////////////////////////////////////
	@Section(section = Sections.BACKUP, comment = "These settings control what and how things are backed up.")
	protected ConfigCategory backup;
	
	@Option(section = Sections.BACKUP, comment = "Folder name to store backups in. Each world's backups will be stored in subfolders of this one.")
	protected String backupFolder = "backups";
	
	@Option(section = Sections.BACKUP, name = "configuration", comment = "Backup config folder.")
	protected boolean backupConfiguration = true;
	
	@Option(section = Sections.BACKUP, name = "serverConfiguration", comment = "Backup server configuration files. eg. server.properties, whitelist.txt")
	protected boolean backupServerConfiguration = false;
	
	@Option(section = Sections.BACKUP, name = "world", comment = "Backup world folder.")
	protected boolean backupWorld = true;
	
	@Option(section = Sections.BACKUP, comment = "List of dimension id's to *not* backup. Use this to disable dimensions that are large or unneeded.")
	protected int[] disabledDimensions = new int[] {};
	
	@Option(section = Sections.BACKUP, name = "mods", comment = "Backup mods folder.")
	protected boolean backupMods = false;
	
	@Option(section = Sections.BACKUP, name = "other", comment = "Other files or directories to backup.")
	protected String[] backupOthers = new String[] {};
	
	////////////////////////////////////////////////////////
	//                   LONGTERM                         //
	////////////////////////////////////////////////////////
	@Section(section = Sections.LONGTERM_BACKUP, comment = "These settings control what and how things are backed up when doing an archival backup.")
	protected ConfigCategory longtermBackup;
	
	@Option(section = Sections.LONGTERM_BACKUP, name = "enabled", comment = "Whether to enable separate long-term backups.")
	protected boolean longtermEnabled = false;
	
	@Option(section = Sections.LONGTERM_BACKUP, name = "backupFolder", comment = "Folder name to store long-term backups in. Each world's archives will be stored in subfolders of this one.")
	protected String longtermBackupFolder = "archives";
	
	@Option(section = Sections.LONGTERM_BACKUP, name = "disabledDimensions", comment = "List of dimension id's to *not* backup. Use this to disable dimensions that are large or unneeded.")
	protected int[] longtermDisabledDimensions = new int[] {};
	
	@Option(section = Sections.LONGTERM_BACKUP, comment = "The number of daily archival backups to keep.")
	protected int maxDailyBackups = 7;
	
	@Option(section = Sections.LONGTERM_BACKUP, comment = "The number of weekly archival backups to keep.")
	protected int maxWeeklyBackups = 14;
	
	////////////////////////////////////////////////////////
	//                   GETTERS                          //
	////////////////////////////////////////////////////////
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
	
	////////////////////////////////////////////////////////
	//                 /options section                   //
	////////////////////////////////////////////////////////
	
	private Configuration config;
	
	public BackupConfiguration(File configFile) {
		try {
			config = new Configuration(configFile);
			config.load();
			Field[] fields = this.getClass().getDeclaredFields();
			
			migrateOption(Sections.GENERAL, "backupFolder", Sections.BACKUP, "backupFolder");
			
			processSections(fields);
			processOptions(fields);
			
			config.save();
		} catch (Exception e) {
			BackupLog.log(Level.SEVERE, e, "There was a problem loading the configuration.");
		}
	}

	private void migrateOption(Sections oldSection, String oldKey, Sections newSection, String newKey) {
		if (config.getCategory(oldSection.getName()).containsKey(oldKey)) {
			String folder = config.getCategory(oldSection.getName()).get(oldKey).value;
			config.getCategory(newSection.getName()).set(newKey, new Property(newKey, folder, Type.STRING));
			config.getCategory(oldSection.getName()).remove(oldKey);
		}
	}

	private void processSections(Field[] fields) 
	throws IllegalAccessException {
		for (Field field : fields) {
			Section section = field.getAnnotation(Section.class);
			if (section == null) { continue; }
			
			String comment = section.comment().isEmpty() ? null : section.comment();
			
			config.addCustomCategoryComment(section.section().getName(), comment);
			if (field.getType() == ConfigCategory.class) {
				field.set(this, config.getCategory(section.section().getName()));
			}
		}
	}

	private void processOptions(Field[] fields)
	throws IllegalAccessException {
		for (Field field : fields) {
			Option option = field.getAnnotation(Option.class);
			if (option == null) { continue; }
			
			String name = option.name();
			String comment = option.comment().isEmpty() ? null : option.comment();
			if (name.isEmpty()) {
				name = field.getName();
			}
			
			Class fieldType = field.getType();
			if (fieldType == boolean.class) {
				boolean value = field.getBoolean(this);
				value = config.get(option.section().getName(), name, value, comment).getBoolean(value);
				field.set(this, value);
			} else if (fieldType == boolean[].class) {
				boolean[] value = (boolean[])field.get(this);
				value = config.get(option.section().getName(), name, value, comment).getBooleanList();
				field.set(this, value);
			} else if (fieldType == int.class) {
				int value = field.getInt(this);
				value = config.get(option.section().getName(), name, value, comment).getInt(value);
				field.set(this, value);
			} else if (fieldType == int[].class) {
				int[] value = (int[])field.get(this);
				value = config.get(option.section().getName(), name, value, comment).getIntList();
				field.set(this, value);
			} else if (fieldType == double.class) {
				double value = field.getDouble(this);
				value = config.get(option.section().getName(), name, value, comment).getDouble(value);
				field.set(this, value);
			} else if (fieldType == double[].class) {
				double[] value = (double[])field.get(this);
				value = config.get(option.section().getName(), name, value, comment).getDoubleList();
				field.set(this, value);
			} else if (fieldType == String.class) {
				String value = (String)field.get(this);
				value = config.get(option.section().getName(), name, value, comment).value;
				field.set(this, value);
			} else if (fieldType == String[].class) {
				String[] value = (String[])field.get(this);
				value = config.get(option.section().getName(), name, value, comment).valueList;
				field.set(this, value);
			} else {
				BackupLog.warning("Skipping @ConfigOption \"%s\" with unknown type: %s", field.getName(), fieldType.getCanonicalName());
				continue;
			}
		}
	}
}
