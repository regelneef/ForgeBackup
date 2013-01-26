package monoxide.forgebackup;

import java.io.File;
import java.util.TimerTask;

import monoxide.forgebackup.backup.Backup;
import monoxide.forgebackup.backup.BackupSettings;
import monoxide.forgebackup.configuration.BackupConfiguration;
import net.minecraft.server.MinecraftServer;

public class BackupTask extends TimerTask {
	private final MinecraftServer server;
	private static boolean lastRunHadPlayers = false; 
	
	public BackupTask(MinecraftServer server) {
		this.server = server;
	}

	@Override
	public void run() {
		processArchiveBackups();
		processRegularBackups();
	}

	private void processArchiveBackups() {
		BackupConfiguration config = ForgeBackup.instance().config();
		if (!config.longtermBackupsEnabled())
		{ return; }
		
		BackupSettings archiveSettings = config.getArchiveBackupSettings(server);
		File archiveBackup = new File(archiveSettings.getBackupFolder(), archiveSettings.getBackupFileName());
		
		if (archiveBackup.exists())
		{ return; }
		
		Backup backup = new Backup(archiveSettings);
		backup.run(ForgeBackup.instance());
	}

	private void processRegularBackups() {
		BackupConfiguration config = ForgeBackup.instance().config();
		if (config.onlyRunBackupsWithPlayersOnline() && !playerLoggedIn()) {
			boolean realLastRun = lastRunHadPlayers;
			lastRunHadPlayers = false;
			
			if (!realLastRun) { return; }
		} else {
			lastRunHadPlayers = true;
		}
		
		Backup backup = new Backup(config.getRegularBackupSettings(server));
		backup.run(ForgeBackup.instance());
	}
	
	public boolean playerLoggedIn() {
		return !server.getConfigurationManager().playerEntityList.isEmpty();
	}
}
