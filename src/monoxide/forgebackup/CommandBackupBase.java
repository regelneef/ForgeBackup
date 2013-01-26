package monoxide.forgebackup;

import java.util.Iterator;
import java.util.logging.Level;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;

public abstract class CommandBackupBase extends CommandBase {
	protected final MinecraftServer server;
	
	public CommandBackupBase(FMLServerStartingEvent event) {
		this.server = event.getServer();
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return !ForgeBackup.instance().config().onlyOperatorsCanManuallyBackup() ? 0 : 
		       (ForgeBackup.instance().config().canCommandBlocksUseCommands() ? 3 : 2);
	}
	
	public void notifyBackupAdmins(ICommandSender sender, String translationKey, Object... parameters) {
		notifyBackupAdmins(sender, Level.INFO, translationKey, parameters);
	}
	
	public void notifyBackupAdmins(ICommandSender sender, Level level, String translationKey, Object... parameters) {
		boolean var5 = true;
		String message = String.format(LanguageRegistry.instance().getStringLocalization(translationKey), parameters);
		
		if (sender instanceof TileEntityCommandBlock && !server.worldServers[0].getGameRules().getGameRuleBooleanValue("commandBlockOutput"))
		{
			var5 = false;
		}
		
		if (var5)
		{
			Iterator var6 = server.getConfigurationManager().playerEntityList.iterator();
			
			while (var6.hasNext())
			{
				EntityPlayerMP var7 = (EntityPlayerMP)var6.next();
				
				if ((server.getConfigurationManager().areCommandsAllowed(var7.username) || !server.isDedicatedServer()) && 
				    (ForgeBackup.instance().config().verboseLogging() || level != Level.FINE))
				{
					var7.sendChatToPlayer("\u00a77\u00a7o[" + sender.getCommandSenderName() + ": " + message + "]");
				}
			}
		}
		
		if (ForgeBackup.instance().config().verboseLogging() && level == Level.FINE) {
			BackupLog.log(Level.INFO, message);
		} else {
			BackupLog.log(level, message);
		}
	}
}
