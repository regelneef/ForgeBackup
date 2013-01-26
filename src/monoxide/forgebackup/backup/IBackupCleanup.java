package monoxide.forgebackup.backup;

import java.io.File;

public interface IBackupCleanup {
	boolean runBackupCleanup(File backupDirectory);
}
