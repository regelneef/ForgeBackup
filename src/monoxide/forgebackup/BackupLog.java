package monoxide.forgebackup;

import java.util.logging.Level;
import java.util.logging.Logger;

import cpw.mods.fml.common.FMLLog;

public abstract class BackupLog {
	
	private static Logger logger;
	
	static {
		logger = Logger.getLogger("forgebackup");
		logger.setParent(FMLLog.getLogger());	
	}
	
	protected static void setLoggerParent(Logger parent) {
		logger.setParent(parent);
	}
	
	protected static void setLogger(Logger logger) {
		BackupLog.logger = logger;
	}
	
	public static Logger getLogger() {
		return logger;
	}
	
	public static void log(Level logLevel, String message, Object... params) {
		boolean lowLogLevel = logLevel == Level.FINE || logLevel == Level.FINER || logLevel == Level.FINEST;
		Logger old = logger.getParent();
		if (lowLogLevel) {
			logger.setParent(FMLLog.getLogger());
		}
		logger.log(logLevel, String.format(message, params));
		if (lowLogLevel) {
			logger.setParent(old);
		}
	}
	
	public static void log(Level logLevel, Throwable e, String message, Object... params) {
		Logger old = logger.getParent();
		logger.setParent(FMLLog.getLogger());
		logger.log(logLevel, String.format(message, params), e);
		logger.setParent(old);
	}
	
	public static void error(String message, Object... params) {
		log(Level.SEVERE, message, params);
	}
	
	public static void warning(String message, Object... params) {
		log(Level.WARNING, message, params);
	}
	
	public static void info(String message, Object... params) {
		log(Level.INFO, message, params);
	}
	
	public static void fine(String message, Object... params) {
		log(Level.FINE, message, params);
	}
	
	public static void finer(String message, Object... params) {
		log(Level.FINER, message, params);
	}
	
	public static void finest(String message, Object... params) {
		log(Level.FINEST, message, params);
	}
	
	public static void dumpStackTrace(String message, Object... params) {
		String output = String.format(message, params) + "\n";
		
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (int i = 2; i < stackTrace.length; i++) {
			output += "  in " + stackTrace[i].toString() + "\n";
		}
		
		info(output.trim());
	}
}
