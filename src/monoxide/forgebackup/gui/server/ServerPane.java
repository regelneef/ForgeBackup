package monoxide.forgebackup.gui.server;

import javax.swing.JComponent;

import net.minecraft.server.dedicated.DedicatedServer;

public class ServerPane extends JComponent {
	protected final DedicatedServer server;
	
	public ServerPane(DedicatedServer server) {
		this.server = server;
	}
}
