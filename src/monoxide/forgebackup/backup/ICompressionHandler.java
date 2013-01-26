package monoxide.forgebackup.backup;

import java.io.File;
import java.io.IOException;

public interface ICompressionHandler {
	void openFile(File backupFile) throws IOException;
	void addCompressedFile(File file) throws IOException;
	void closeFile() throws IOException;
	String getFileExtension();
}
