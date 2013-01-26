package monoxide.forgebackup;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.TimerTask;

import monoxide.forgebackup.backup.Backup;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class BackupTask extends TimerTask {
	private final MinecraftServer server;
	private static boolean lastRunHadPlayers = false; 
	
	public BackupTask(MinecraftServer server) {
		this.server = server;
	}

	@Override
	public void run() {
		if (ForgeBackup.instance().config().onlyRunBackupsWithPlayersOnline() && !playerLoggedIn()) {
			boolean realLastRun = lastRunHadPlayers;
			lastRunHadPlayers = false;
			
			if (!realLastRun) { return; }
		} else {
			lastRunHadPlayers = true;
		}
		
		Backup backup = new Backup(ForgeBackup.instance().config().getRegularBackupSettings(server));
		backup.run(ForgeBackup.instance());
	}
	
	public boolean playerLoggedIn() {
		return !server.getConfigurationManager().playerEntityList.isEmpty();
	}
}
