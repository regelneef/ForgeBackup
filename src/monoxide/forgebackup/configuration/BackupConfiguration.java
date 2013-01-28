package monoxide.forgebackup.configuration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.logging.Level;

import monoxide.forgebackup.BackupLog;
import monoxide.forgebackup.backup.ArchiveBackupCleanup;
import monoxide.forgebackup.backup.BackupSettings;
import monoxide.forgebackup.backup.RegularBackupCleanup;
import monoxide.forgebackup.compression.CompressionType;
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
	
	@Option(section = Sections.BACKUP, comment = "Type of compression to use when storing backups. Valid values: zip, tgz, tbz2, git, none")
	protected CompressionType compression = CompressionType.getDefault();
	
	@Option(section = Sections.BACKUP, name = "configuration", comment = "Backup config folder.")
	protected boolean backupConfiguration = true;
	
	@Option(section = Sections.BACKUP, name = "mods", comment = "Backup mods folder.")
	protected boolean backupMods = false;
	
	@Option(section = Sections.BACKUP, name = "serverConfiguration", comment = "Backup server configuration files. eg. server.properties, whitelist.txt")
	protected boolean backupServerConfiguration = false;
	
	@Option(section = Sections.BACKUP, name = "world", comment = "Backup world folder.")
	protected boolean backupWorld = true;
	
	@Option(section = Sections.BACKUP, comment = "List of dimension id's to *not* backup. Use this to disable dimensions that are large or unneeded.")
	protected int[] disabledDimensions = new int[] {};
	
	@Option(section = Sections.BACKUP, name = "other", comment = "Other files or directories to backup.")
	protected String[] backupOthers = new String[] {};
	
	////////////////////////////////////////////////////////
	//                   LONGTERM                         //
	////////////////////////////////////////////////////////
	@Section(section = Sections.LONGTERM_BACKUP, comment =
			"These settings control what and how things are backed up when doing an archival backup.\n" +
			"The file group settings are cumulative with the regular backups. If you select to backup your world in\n" +
			"the regular backup, it will be enabled for longterm backups no matter what. Disabled dimensions however\n" +
			"do totally override the default settings."
	)
	protected ConfigCategory longtermBackup;
	
	@Option(section = Sections.LONGTERM_BACKUP, name = "enabled", comment = "Whether to enable separate long-term backups.")
	protected boolean longtermEnabled = false;
	
	@Option(section = Sections.LONGTERM_BACKUP, name = "backupFolder", comment = "Folder name to store long-term backups in. Each world's archives will be stored in subfolders of this one.")
	protected String longtermBackupFolder = "archives";
	
	@Option(section = Sections.LONGTERM_BACKUP, name = "compression", comment = "Type of compression to use when storing backups. Valid values: zip, tgz, tbz2, git, none")
	protected CompressionType longtermCompression = CompressionType.getDefault();
	
	@Option(section = Sections.LONGTERM_BACKUP, name = "configuration", comment = "Backup config folder.")
	protected boolean longtermBackupConfiguration = true;
	
	@Option(section = Sections.LONGTERM_BACKUP, name = "mods", comment = "Backup mods folder.")
	protected boolean longtermBackupMods = false;
	
	@Option(section = Sections.LONGTERM_BACKUP, name = "serverConfiguration", comment = "Backup server configuration files. eg. server.properties, whitelist.txt")
	protected boolean longtermBackupServerConfiguration = false;
	
	@Option(section = Sections.LONGTERM_BACKUP, name = "world", comment = "Backup world folder.")
	protected boolean longtermBackupWorld = true;
	
	@Option(section = Sections.LONGTERM_BACKUP, name = "disabledDimensions", comment = "List of dimension id's to *not* backup. Use this to disable dimensions that are large or unneeded.")
	protected int[] longtermDisabledDimensions = new int[] {};
	
	@Option(section = Sections.LONGTERM_BACKUP, comment = "The number of daily archival backups to keep.")
	protected int maxDailyBackups = 7;
	
	@Option(section = Sections.LONGTERM_BACKUP, comment = "The number of weekly archival backups to keep.")
	protected int maxWeeklyBackups = 2;
	
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
	
	public boolean verboseLogging() {
		return verboseLogging;
	}
	
	public boolean longtermBackupsEnabled() {
		return longtermEnabled;
	}

	public BackupSettings getRegularBackupSettings(MinecraftServer server) {
		return new BackupSettings(
				server, backupFolder, verboseLogging, new RegularBackupCleanup(maxBackups),
				backupWorld, backupConfiguration, backupMods, backupServerConfiguration, backupOthers,
				disabledDimensions, compression.getCompressionHandler(server));
	}

	public BackupSettings getFullBackupSettings(MinecraftServer server) {
		return new BackupSettings(server, backupFolder, verboseLogging, new RegularBackupCleanup(maxBackups),
				true, true, true, true, backupOthers,
				new int[] {}, compression.getCompressionHandler(server));
	}

	public BackupSettings getArchiveBackupSettings(MinecraftServer server) {
		return new BackupSettings(server, longtermBackupFolder, verboseLogging, new ArchiveBackupCleanup(maxDailyBackups, maxWeeklyBackups),
				backupWorld || longtermBackupWorld, backupConfiguration || longtermBackupConfiguration,
				backupMods || longtermBackupMods, backupServerConfiguration || longtermBackupServerConfiguration, backupOthers,
				longtermDisabledDimensions, longtermCompression.getCompressionHandler(server));
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
			} else if (fieldType == CompressionType.class) {
				String value = ((CompressionType)field.get(this)).getName();
				value = config.get(option.section().getName(), name, value, comment).value;
				field.set(this, CompressionType.getByName(value));
			} else {
				BackupLog.warning("Skipping @Option \"%s\" with unknown type: %s", field.getName(), fieldType.getCanonicalName());
				continue;
			}
		}
	}
}
