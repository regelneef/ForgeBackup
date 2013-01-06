package monoxide.forgebackup;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.TimerTask;

import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class BackupTask extends TimerTask {
	
	private final MinecraftServer server;
	public static boolean lastRunHadPlayers = false; 
	
	public BackupTask(MinecraftServer server) {
		this.server = server;
	}

	@Override
	public void run() {
		if (ForgeBackup.instance().config().onlyRunBackupsWithPlayersOnline() && !playerLoggedIn()) {
			boolean realLastRun = lastRunHadPlayers;
			lastRunHadPlayers = false;
			
			if (!realLastRun) { return; }
		} else {
			lastRunHadPlayers = true;
		}
		
		server.getCommandManager().executeCommand(ForgeBackup.instance(), "backup");
	}
	
	public boolean playerLoggedIn() {
		Field entitySetField = null;
		
		for (Field field : EntityTracker.class.getDeclaredFields()) {
			if (field.getType() == Set.class) {
				entitySetField = field;
				entitySetField.setAccessible(true);
				break;
			}
		}
		
		if (entitySetField == null) {
			BackupLog.warning("Couldn't reflect on EntityTracker correctly; continuing and assuming there are players on the server.");
			return true;
		}
		
		for (WorldServer world : server.worldServers) {
			try {
				Set entities = (Set)entitySetField.get(world.getEntityTracker());
				
				for (Object entry : entities) {
					if (((EntityTrackerEntry)entry).myEntity instanceof EntityPlayer) {
						return true;
					}
				}
			}
			catch (IllegalArgumentException e) {}
			catch (IllegalAccessException e) {}
		}
		return false;
	}

}
