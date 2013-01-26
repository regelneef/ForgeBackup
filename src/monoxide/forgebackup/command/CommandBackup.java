package monoxide.forgebackup.command;

import monoxide.forgebackup.ForgeBackup;
import monoxide.forgebackup.backup.Backup;
import monoxide.forgebackup.backup.BackupSettings;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommandBackup extends CommandBackupBase {
	
	public CommandBackup(FMLServerStartingEvent event) {
		super(event);
	}
	
	@Override
	public String getCommandName() {
		return "backup";
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		if (sender instanceof EntityPlayer) {
			if (!server.isDedicatedServer()) {
				return true;
			}
		}
		
		return super.canCommandSenderUseCommand(sender);
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length == 0) {
			server.getCommandManager().executeCommand(sender, "help " + this.getCommandName());
			return;
		}
		
		Backup backup;
		if ("run".equals(args[0])) {
			backup = new Backup(ForgeBackup.instance().config().getRegularBackupSettings(server));
		} else if ("full".equals(args[0])) {
			backup = new Backup(ForgeBackup.instance().config().getFullBackupSettings(server));
		} else {
			server.getCommandManager().executeCommand(sender, "help " + this.getCommandName());
			return;
		}
		
		backup.run(sender);
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + this.getCommandName() + " [run|full]";
	}
}
