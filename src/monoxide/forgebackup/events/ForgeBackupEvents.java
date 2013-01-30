package monoxide.forgebackup.events;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import monoxide.forgebackup.BackupLog;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.gui.ServerGUI;

public abstract class ForgeBackupEvents {
	public static JComponent ServerGuiInitialising(DedicatedServer server) {
		BackupLog.info("Modifying server gui...");
		JTabbedPane tabs = new JTabbedPane();
		tabs.add("Status", new ServerGUI(server));
		return tabs;
	}
}
