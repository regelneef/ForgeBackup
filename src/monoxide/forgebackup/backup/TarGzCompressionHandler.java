package monoxide.forgebackup.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import monoxide.forgebackup.BackupLog;
import net.minecraft.server.MinecraftServer;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public class TarGzCompressionHandler extends CompressionHandler {
	ArchiveOutputStream tarStream;
	
	public TarGzCompressionHandler(MinecraftServer server) {
		super(server);
	}
	
	@Override
	public void openFile(File backupFile) throws IOException {
		try {
			CompressorOutputStream gzipStream = new CompressorStreamFactory().createCompressorOutputStream(CompressorStreamFactory.GZIP, new FileOutputStream(backupFile));
			tarStream = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.TAR, gzipStream);
		} catch (ArchiveException e) {
			throw new IOException("Unable to create tar stream.", e);
		} catch (CompressorException e) {
			throw new IOException("Unable to create gzip stream.", e);
		}
	}
	
	@Override
	public void addCompressedFile(File file) throws IOException {
		TarArchiveEntry entry = new TarArchiveEntry(file);
		tarStream.putArchiveEntry(entry);
		
		// Tar supports adding directory entries, but they shouldn't have any data!
		if (!file.isDirectory()) { 
			try {
				byte[] buffer = new byte[4096];
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
	
	@Override
	public String getFileExtension() {
		return "tar.gz";
	}
}
