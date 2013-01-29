package monoxide.forgebackup.compression;

import java.lang.reflect.Constructor;
import java.util.logging.Level;

import monoxide.forgebackup.BackupLog;
import net.minecraft.server.MinecraftServer;

public enum CompressionType {
	ZIP("zip", ZipCompressionHandler.class),
	TAR_GZ("tgz", TarGzCompressionHandler.class),
	TAR_BZ2("tbz2", TarBzCompressionHandler.class),
	GIT("git", GitCompressionHandler.class),
	NONE("none", FolderCompressionHandler.class)
	;
	
	private String name;
	private Class<? extends ICompressionHandler> handlerType;
	
	CompressionType(String name, Class<? extends ICompressionHandler> handlerType) {
		this.name = name;
		this.handlerType = handlerType;
	}
	
	public String getName() {
		return name;
	}

	public ICompressionHandler getCompressionHandler(MinecraftServer server) {
		try {
			Constructor<? extends ICompressionHandler> constructor = handlerType.getConstructor(MinecraftServer.class);
			return constructor.newInstance(server);
		} catch (Throwable e) {
			BackupLog.log(Level.SEVERE, e, "Failed to create a new compression handler of type: %s", handlerType.getCanonicalName());
			return null;
		}
	}
	
	public static CompressionType getByName(String name) {
		for (CompressionType type : values()) {
			if (type.name.equals(name)) {
				return type;
			}
		}
		return getDefault();
	}
	
	/**
	 * Get the default compression type on a given operating system.
	 * 
	 * Basically, this boils down to a check for Windows. We use zip by default
	 * on Windows, tgz on everything else.
	 */
	public static CompressionType getDefault() {
		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
			return ZIP;
		} else {
			return TAR_GZ;
		}
	}
}
