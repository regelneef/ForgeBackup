package monoxide.forgebackup.events;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import monoxide.forgebackup.BackupLog;
import monoxide.forgebackup.gui.server.ServerPane;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.gui.ServerGUI;

public abstract class ForgeBackupEvents {
	@SideOnly(Side.SERVER)
	public static JComponent ServerGuiInitialising(DedicatedServer server) {
		BackupLog.info("Modifying server gui...");
		
		JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
		tabs.add("Status", new ServerGUI(server));
		tabs.add("Backups", new ServerPane(server));
		tabs.setPreferredSize(new Dimension(1200, 600));
		return tabs;
	}
	
	@SideOnly(Side.CLIENT)
	public static void modifyGuiSelectWorld(GuiSelectWorld selectWorld) {
		BackupLog.info("Modifying world-selection screen...");
	}
}
