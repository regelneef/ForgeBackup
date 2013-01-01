package monoxide.forgebackup;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.logging.Level;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.StringTranslate;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarted;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(name = "ForgeBackup", modid = "mono_backup", useMetadata = true)
public class ForgeBackup implements ICommandSender {
	
	private Configuration config;
	private Timer backupTimer;
	
	@Instance("forgebackup")
	private static ForgeBackup instance;
	public static ForgeBackup instance() {
		return instance;
	}
	
	@ConfigOption(comment = "Interval in minutes between automatic backup attempts.")
	protected int backupInterval = 15;
	
	public int getBackupInterval() {
		return backupInterval;
	}
	
	@PreInit
	public void preInitialisation(FMLPreInitializationEvent event) {
		if (event.getSide() == Side.SERVER) {
			// Only assign ourselves to the Minecraft logger if we're on the server
			// If we do this in SSP, our logs will be completely hidden.
			BackupLog.setLoggerParent(FMLCommonHandler.instance().getMinecraftServerInstance().logger);
		}
		
		BackupLog.info("Loading configuration...");
		try {
			config = new Configuration(event.getSuggestedConfigurationFile());
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
	
	@Init
	public void initialisation(FMLInitializationEvent event) {
		LanguageRegistry.instance().addStringLocalization("ForgeBackup.backup.start", "en_US", "Starting a new backup.");
		LanguageRegistry.instance().addStringLocalization("ForgeBackup.backup.progress", "en_US", "Creating new backup of your world...");
		LanguageRegistry.instance().addStringLocalization("ForgeBackup.backup.folderExists", "en_US", "Backup failed. Backup directory already exists and is not a directory.");
		LanguageRegistry.instance().addStringLocalization("ForgeBackup.backup.aborted", "en_US", "Backup failed. Please check your server log for more information.");
		LanguageRegistry.instance().addStringLocalization("ForgeBackup.backup.complete", "en_US", "Backup complete!");
		
		LanguageRegistry.instance().addStringLocalization("ForgeBackup.save.force", "en_US", "Forcing an updated save...");
		LanguageRegistry.instance().addStringLocalization("ForgeBackup.save.disabled", "en_US", "Disabling saving...");
		LanguageRegistry.instance().addStringLocalization("ForgeBackup.save.enabled", "en_US", "Re-enabling saving...");
	}
	
	@ServerStarted
	public void serverStarted(FMLServerStartedEvent event) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		BackupLog.info("ForgeBackup starting...");
		new CommandBackup(server);
		backupTimer = new Timer(true);
		backupTimer.scheduleAtFixedRate(new BackupTask(server), getBackupInterval() * 60 * 1000, getBackupInterval() * 60 * 1000);
	}

	@Override
	public String getCommandSenderName() {
		return "ForgeBackup";
	}

	@Override
	public void sendChatToPlayer(String message) {
		BackupLog.info("Recieved message: %s", message);
	}

	@Override
	public boolean canCommandSenderUseCommand(int var1, String var2) {
		return true;
	}

	@Override
	public String translateString(String var1, Object... var2) {
		return StringTranslate.getInstance().translateKeyFormat(var1, var2);
	}

	@Override
	public ChunkCoordinates getPlayerCoordinates() {
		return new ChunkCoordinates(0, 0, 0);
	}
	
}
