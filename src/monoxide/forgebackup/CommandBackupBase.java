package monoxide.forgebackup;

import java.util.Iterator;

import cpw.mods.fml.common.registry.LanguageRegistry;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityCommandBlock;

public abstract class CommandBackupBase extends CommandBase {
	protected final MinecraftServer server;
	
	public CommandBackupBase(MinecraftServer server) {
		this.server = MinecraftServer.getServer();
		((ServerCommandManager) this.server.getCommandManager()).registerCommand(this);
	}
	
	public void notifyBackupAdmins(ICommandSender sender, String translationKey, Object... parameters) {
		boolean var5 = true;
		String message = String.format(LanguageRegistry.instance().getStringLocalization(translationKey), parameters);
		
		if (sender instanceof TileEntityCommandBlock && !server.worldServers[0].getGameRules().getGameRuleBooleanValue("commandBlockOutput"))
		{
			var5 = false;
		}
		
		if (var5)
		{
			Iterator var6 = MinecraftServer.getServer().getConfigurationManager().playerEntityList.iterator();
			
			while (var6.hasNext())
			{
				EntityPlayerMP var7 = (EntityPlayerMP)var6.next();
				
				if (var7 != sender && server.getConfigurationManager().areCommandsAllowed(var7.username))
				{
					var7.sendChatToPlayer("\u00a77\u00a7o[" + sender.getCommandSenderName() + ": " + message + "]");
				}
			}
		}
		
		if (sender != MinecraftServer.getServer())
		{
			MinecraftServer.logger.info(sender.getCommandSenderName() + ": " + message);
		}
	}
}
