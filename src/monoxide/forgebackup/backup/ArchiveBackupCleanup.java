package monoxide.forgebackup.backup;

import java.io.File;
import java.util.Date;

public class ArchiveBackupCleanup implements IBackupCleanup {
	protected final int maxDailyBackups;
	protected final int maxWeeklyBackups;
	
	public ArchiveBackupCleanup(int maxDailyBackups, int maxWeeklyBackups) {
		this.maxDailyBackups = maxDailyBackups;
		this.maxWeeklyBackups = maxWeeklyBackups;
	}
	
	@Override
	public String getBackupFilename() {
		Date now = new Date();
		return String.format("%TY%Tm%Td", now, now, now);
	}
	
	@Override
	public boolean runBackupCleanup(File backupDirectory) {
		// TODO Auto-generated method stub
		return false;
	}
}
