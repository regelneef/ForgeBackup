package monoxide.forgebackup.events;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import monoxide.forgebackup.BackupLog;
import monoxide.forgebackup.ForgeBackup;
import monoxide.forgebackup.gui.server.ServerPane;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.gui.ServerGUI;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public abstract class ForgeBackupServerEvents {
	public static JComponent ServerGuiInitialising(DedicatedServer server) {
		BackupLog.fine("Modifying server gui...");
		
		JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
		tabs.add("Status", new ServerGUI(server));
		tabs.add("Backups", new ServerPane(server));
		tabs.setPreferredSize(new Dimension(1200, 600));
		return tabs;
	}
	
	public static void dedicatedServerShutdown() {
		BackupLog.fine("Attempting to shutdown server...");
		File worldToReload = ForgeBackup.instance().oldWorld;
		if (worldToReload != null) {
			BackupLog.info("Reloading world...");
			ForgeBackup.instance().oldWorld = null;
			new DedicatedServer(worldToReload).run();
		} else {
			System.exit(0);
		}
	}
}
