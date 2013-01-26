package monoxide.forgebackup.backup;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

public class RegularBackupCleanup implements IBackupCleanup {
	protected final int maxBackups;
	
	public RegularBackupCleanup(int maxBackups) {
		this.maxBackups = maxBackups;
	}
	
	@Override
	public boolean runBackupCleanup(File backupDirectory) {
		if (maxBackups <= 0)
		{ return true; }
		
		List<File> backups = Lists.newArrayList(backupDirectory.listFiles());
		Collections.sort(backups);
		
		while (backups.size() >= maxBackups) {
			File backup = backups.remove(0);
			backup.delete();
		}
		
		return true;
	}

	@Override
	public String getBackupFilename() {
		Date now = new Date();
		return String.format("%TY%Tm%Td-%TH%TM%TS", now, now, now, now, now, now);
	}
}