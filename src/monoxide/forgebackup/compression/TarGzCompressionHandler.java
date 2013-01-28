package monoxide.forgebackup.compression;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.minecraft.server.MinecraftServer;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public class TarGzCompressionHandler extends TarCompressionHandler {
	public TarGzCompressionHandler(MinecraftServer server) {
		super(server);
	}
	
	@Override
	public void openFile(File backupFile) throws IOException {
		try {
			OutputStream fileStream = new BufferedOutputStream(new FileOutputStream(backupFile));
			CompressorOutputStream gzipStream = new CompressorStreamFactory().createCompressorOutputStream(CompressorStreamFactory.GZIP, fileStream);
			tarStream = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.TAR, gzipStream);
		} catch (ArchiveException e) {
			throw new IOException("Unable to create tar stream.", e);
		} catch (CompressorException e) {
			throw new IOException("Unable to create gzip stream.", e);
		}
	}
	
	@Override
	public String getFileExtension() {
		return ".tar.gz";
	}
}
