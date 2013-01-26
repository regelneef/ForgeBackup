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
		Backup backup = new Backup(ForgeBackup.instance().config().getRegularBackupSettings(server));
		backup.run(sender);
	}
}
