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
import cpw.mods.fml.common.Mod.ServerStarted;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(name = "ForgeBackup", modid = "forgebackup", useMetadata = true)
public class ForgeBackup implements ICommandSender {
	
	private Timer backupTimer;
	private int timeBetween = 20 * 1000;
	
	@Instance("forgebackup")
	private static ForgeBackup instance;
	public static ForgeBackup instance() {
		return instance;
	}
	
	@Init
	public void initialisation(FMLInitializationEvent event) {
		BackupLog.setLoggerParent(FMLCommonHandler.instance().getMinecraftServerInstance().logger);
		
		LanguageRegistry.instance().addStringLocalization("ForgeBackup.backup.start", "en_US", "Starting a new backup.");
		LanguageRegistry.instance().addStringLocalization("ForgeBackup.backup.progress", "en_US", "Creating new backup of your world...");
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
		backupTimer.scheduleAtFixedRate(new BackupTask(server), timeBetween, timeBetween);
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
