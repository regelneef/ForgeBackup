package monoxide.forgebackup.events;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import monoxide.forgebackup.BackupLog;
import monoxide.forgebackup.gui.server.ServerPane;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.gui.ServerGUI;

public abstract class ForgeBackupEvents {
	public static JComponent ServerGuiInitialising(DedicatedServer server) {
		BackupLog.info("Modifying server gui...");
		JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
		tabs.add("Status", new ServerGUI(server));
		tabs.add("Backups", new ServerPane(server));
		tabs.setPreferredSize(new Dimension(1200, 600));
		return tabs;
	}
}
