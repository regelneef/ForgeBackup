package monoxide.forgebackup;

import java.util.TimerTask;

import net.minecraft.server.MinecraftServer;

public class BackupTask extends TimerTask {
	
	private final MinecraftServer server;
	
	public BackupTask(MinecraftServer server) {
		this.server = server;
	}

	@Override
	public void run() {
		server.getCommandManager().executeCommand(ForgeBackup.instance(), "backup");
	}

}
