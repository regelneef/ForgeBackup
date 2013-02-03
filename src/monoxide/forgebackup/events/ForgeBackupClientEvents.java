package monoxide.forgebackup.events;

import java.util.List;

import monoxide.forgebackup.BackupLog;
import monoxide.forgebackup.gui.client.GuiSelectBackup;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.world.storage.SaveFormatComparator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ForgeBackupClientEvents {
	public static final int RESTORE_BUTTON_ID = 8;
	
	public static void modifyGuiSelectWorld(GuiSelectWorld selectWorld) {
		BackupLog.fine("Modifying world-selection screen...");
		GuiButton cancelButton = null;
		
		for (Object element : selectWorld.controlList) {
			if (element instanceof GuiButton) {
				GuiButton button = ((GuiButton)element);
				button.yPosition -= 24;
				
				if (button.id == 0) {
					cancelButton = button;
				}
			}
		}
		
		selectWorld.worldSlotContainer.height -= 24;
		selectWorld.worldSlotContainer.bottom -= 24;
		
		selectWorld.field_82316_w.width = selectWorld.buttonSelect.width;
		cancelButton.width = selectWorld.buttonSelect.width;
		cancelButton.xPosition = selectWorld.field_82316_w.xPosition;
		cancelButton.yPosition += 24;
		
		GuiButton restoreButton = new GuiButton(RESTORE_BUTTON_ID, selectWorld.buttonDelete.xPosition, cancelButton.yPosition, 150, 20, "Restore Backup");
		selectWorld.controlList.add(restoreButton);
		restoreButton.enabled = false;
		try {
			GuiSelectWorld.class.getField("buttonRestore").set(selectWorld, restoreButton);
		}
		catch (IllegalAccessException e) {}
		catch (IllegalArgumentException e) {}
		catch (SecurityException e) {}
		catch (NoSuchFieldException e) {}
	}
	
	public static GuiScreen getBackupListGui(GuiSelectWorld guiSelectWorld, List<SaveFormatComparator> saves, int selectedWorld) {
		SaveFormatComparator save = saves.get(selectedWorld);
		
		return new GuiSelectBackup(guiSelectWorld, save);
	}
}
