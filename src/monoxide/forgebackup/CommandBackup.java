package monoxide.forgebackup;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class CommandBackup extends CommandBackupBase {
	
	public CommandBackup(MinecraftServer server) {
		super(server);
	}
	
	@Override
	public String getCommandName() {
		return "backup";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		MinecraftServer server = MinecraftServer.getServer();
		notifyBackupAdmins(sender, "ForgeBackup.backup.start");
		
		notifyBackupAdmins(sender, "ForgeBackup.save.disabled");
		for (int i = 0; i < server.worldServers.length; ++i)
		{
			if (server.worldServers[i] != null)
			{
				WorldServer var5 = server.worldServers[i];
				var5.canNotSave = true;
			}
		}
		
		notifyBackupAdmins(sender, "ForgeBackup.save.force");
		notifyBackupAdmins(sender, "ForgeBackup.save.enabled");
		notifyBackupAdmins(sender, "ForgeBackup.backup.complete");
	}
	
}
