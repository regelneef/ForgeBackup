package monoxide.forgebackup.compression;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import monoxide.forgebackup.BackupLog;
import net.minecraft.server.MinecraftServer;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

public abstract class TarCompressionHandler extends CompressionHandler {
	protected ArchiveOutputStream tarStream;
	
	public TarCompressionHandler(MinecraftServer server) {
		super(server);
	}
	
	@Override
	public void addCompressedFile(File file) throws IOException {
		TarArchiveEntry entry = new TarArchiveEntry(file);
		tarStream.putArchiveEntry(entry);
		
		// Tar supports adding directory entries, but they shouldn't have any data!
		if (!file.isDirectory()) { 
			try {
				byte[] buffer = new byte[16384];
				int readBytes;
				InputStream inputStream = new FileInputStream(file);
				while ((readBytes = inputStream.read(buffer)) >= 0) {
					tarStream.write(buffer, 0, readBytes);
				}
				inputStream.close();
			} catch (IOException e) {
				BackupLog.warning("Couldn't backup file: %s", file.getPath());
			}
		}
		
		tarStream.closeArchiveEntry();
	}
	
	@Override
	public void closeFile() throws IOException {
		tarStream.close();
	}
}
