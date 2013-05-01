package monoxide.forgebackup.command;

import java.io.File;
import java.lang.reflect.Field;

import monoxide.forgebackup.ForgeBackup;
import monoxide.forgebackup.backup.Backup;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
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
	public void processCommand(final ICommandSender sender, String[] args) {
		final Backup backup;
		
		if (args.length == 0) {
			server.getCommandManager().executeCommand(sender, "help " + this.getCommandName());
			return;
		}
		
		if ("reload".equals(args[0])) {
			ForgeBackup.instance().reloadConfiguration();
			sender.sendChatToPlayer("Configuration for forgebackup has been reloaded.");
			return;
		}
		
		if ("run".equals(args[0])) {
			backup = new Backup(ForgeBackup.instance().config().getRegularBackupSettings(server));
		} else if ("full".equals(args[0])) {
			backup = new Backup(ForgeBackup.instance().config().getFullBackupSettings(server));
		} else if ("restore".equals(args[0])) {
			backup = null;
			
			for (Field f : MinecraftServer.class.getDeclaredFields()) {
				if (f.getType() == File.class) {
					try {
						f.setAccessible(true);
						ForgeBackup.instance().oldWorld = (File)f.get(server);
					}
					catch (IllegalArgumentException e) {}
					catch (IllegalAccessException e) {}
				}
			}
			
			server.initiateShutdown();
		} else {
			backup = null;
			server.getCommandManager().executeCommand(sender, "help " + this.getCommandName());
		}
		
		if (backup != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					backup.run(sender);
				}
			}).start();
		}
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + this.getCommandName() + " [run|full|reload]";
	}
}
