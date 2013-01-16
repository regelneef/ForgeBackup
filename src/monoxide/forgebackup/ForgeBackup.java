package monoxide.forgebackup;

import java.util.Timer;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.StringTranslate;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarted;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.Mod.ServerStopping;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(name = "ForgeBackup", modid = "forgebackup", useMetadata = true)
public class ForgeBackup implements ICommandSender {
	
	private BackupConfiguration config;
	private Timer backupTimer;
	
	@Instance("forgebackup")
	private static ForgeBackup instance;
	public static ForgeBackup instance() {
		return instance;
	}
	
	public BackupConfiguration config() {
		return config;
	}
	
	@PreInit
	public void preInitialisation(FMLPreInitializationEvent event) {
		if (event.getSide() == Side.SERVER) {
			// Only assign ourselves to the Minecraft logger if we're on the server
			// If we do this in SSP, our logs will be completely hidden.
			BackupLog.setLoggerParent(FMLCommonHandler.instance().getMinecraftServerInstance().logger);
		}
		
		config = new BackupConfiguration(event.getSuggestedConfigurationFile());
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
	
	@ServerStarting
	public void serverStarting(FMLServerStartingEvent event) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		event.registerServerCommand(new CommandBackup(event));
	}
	
	@ServerStarted
	public void serverStarted(FMLServerStartedEvent event) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		BackupLog.info("ForgeBackup starting for world: %s...", server.worldServers[0].getSaveHandler().getSaveDirectoryName());
		backupTimer = new Timer(true);
		backupTimer.scheduleAtFixedRate(new BackupTask(server), config.getBackupInterval() * 60 * 1000, config.getBackupInterval() * 60 * 1000);
	}
	
	@ServerStopping
	public void serverStopping(FMLServerStoppingEvent event) {
		backupTimer.cancel();
		BackupLog.info("ForgeBackup stopped.");
	}

	@Override
	public String getCommandSenderName() {
		return "Backup";
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
