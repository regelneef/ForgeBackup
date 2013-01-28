package monoxide.forgebackup.compression;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import net.minecraft.server.MinecraftServer;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public class TarBzCompressionHandler extends TarCompressionHandler {
	public TarBzCompressionHandler(MinecraftServer server) {
		super(server);
	}
	
	@Override
	public void openFile(File backupFile) throws IOException {
		try {
			CompressorOutputStream bzipStream = new CompressorStreamFactory().createCompressorOutputStream(CompressorStreamFactory.BZIP2, new FileOutputStream(backupFile));
			tarStream = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.TAR, bzipStream);
		} catch (ArchiveException e) {
			throw new IOException("Unable to create tar stream.", e);
		} catch (CompressorException e) {
			throw new IOException("Unable to create bzip stream.", e);
		}
	}
	
	@Override
	public String getFileExtension() {
		return "tar.bz2";
	}
}
