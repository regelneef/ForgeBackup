package monoxide.forgebackup;

import java.util.logging.Level;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
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
		boolean failure = false;
		notifyBackupAdmins(sender, "ForgeBackup.backup.start");
		
		notifyBackupAdmins(sender, "ForgeBackup.save.disabled");
		for (int i = 0; i < server.worldServers.length; ++i)
		{
			if (server.worldServers[i] != null)
			{
				WorldServer worldServer = server.worldServers[i];
				worldServer.canNotSave = true;
			}
		}
		
		try
		{
			notifyBackupAdmins(sender, "ForgeBackup.save.force");
			if (server.getConfigurationManager() != null)
			{
				server.getConfigurationManager().saveAllPlayerData();
			}
			
			for (int i = 0; i < server.worldServers.length; ++i)
			{
				if (server.worldServers[i] != null)
				{
					WorldServer var5 = server.worldServers[i];
					boolean var6 = var5.canNotSave;
					var5.canNotSave = false;
					var5.saveAllChunks(true, (IProgressUpdate)null);
					var5.canNotSave = var6;
				}
			}
			
			notifyBackupAdmins(sender, "ForgeBackup.backup.progress");
		}
		catch (MinecraftException ex)
		{
			notifyBackupAdmins(sender, Level.SEVERE, "ForgeBackup.backup.aborted");
			BackupLog.log(Level.SEVERE, ex, ex.getMessage());
			return;
			
		} finally {
			notifyBackupAdmins(sender, "ForgeBackup.save.enabled");
			for (int i = 0; i < server.worldServers.length; ++i)
			{
				if (server.worldServers[i] != null)
				{
					WorldServer worldServer = server.worldServers[i];
					worldServer.canNotSave = true;
				}
			}
		}
		
		notifyBackupAdmins(sender, "ForgeBackup.backup.complete");
	}
	
}
