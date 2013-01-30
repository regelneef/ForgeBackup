package monoxide.forgebackup.gui.server;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import net.minecraft.server.dedicated.DedicatedServer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class ServerPane extends JComponent {
	protected final DedicatedServer server;
	
	public ServerPane(DedicatedServer server) {
		this.server = server;
		this.setLayout(new BorderLayout());
		
		JPanel backupsPane = new JPanel(new BorderLayout());
		backupsPane.setBorder(new TitledBorder("Existing backups"));
		backupsPane.add(new JScrollPane(new BackupList(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), "Center");
		backupsPane.add(new JButton("Restore"), "South");
		this.add(backupsPane, "Center");
	}
}
