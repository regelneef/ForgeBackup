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
import cpw.mods.fml.common.registry.LanguageRegistry;

public abstract class CommandBackupBase extends CommandBase {
	protected final MinecraftServer server;
	
	public CommandBackupBase(MinecraftServer server) {
		this.server = MinecraftServer.getServer();
		((ServerCommandManager) this.server.getCommandManager()).registerCommand(this);
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
				
				if (var7 != sender && server.getConfigurationManager().areCommandsAllowed(var7.username))
				{
					var7.sendChatToPlayer("\u00a77\u00a7o[" + sender.getCommandSenderName() + ": " + message + "]");
				}
			}
		}
		
		if (sender != server)
		{
			if (ForgeBackup.instance().config().verboseLogging() && level == Level.FINE) {
				BackupLog.log(Level.INFO, message);
			} else {
				BackupLog.log(level, message);
			}
		}
		if (sender instanceof EntityPlayer && !server.isDedicatedServer()) {
			if (ForgeBackup.instance().config().verboseLogging() || level != Level.FINE) {
				((EntityPlayer)sender).sendChatToPlayer("\u00a77\u00a7o" + message);
			}
		}
	}
}
