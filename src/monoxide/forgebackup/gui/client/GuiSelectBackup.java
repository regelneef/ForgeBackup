package monoxide.forgebackup.gui.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.storage.SaveFormatComparator;

public class GuiSelectBackup extends GuiScreen {
	protected final GuiScreen parentScreen;
	protected String screenTitle;
	
	protected GuiButton cancelButton;
	protected GuiSlotBackup backupList;
	
	public GuiSelectBackup(GuiSelectWorld guiSelectWorld, SaveFormatComparator save) {
		parentScreen = guiSelectWorld;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		StringTranslate var1 = StringTranslate.getInstance();
		screenTitle = "Select Backup To Restore";
		backupList = new GuiSlotBackup(mc, this);
		cancelButton = new GuiButton(0, this.width / 2 + 82, this.height - 28, 72, 20, var1.translateKey("gui.cancel"));
		buttonList.add(cancelButton);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		this.drawDefaultBackground();
		backupList.drawScreen(par1, par2, par3);
		this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 20, 16777215);
		super.drawScreen(par1, par2, par3);
	}
	
	@Override
	protected void actionPerformed(GuiButton buttonClicked) {
		switch(buttonClicked.id) {
		case 0:
			mc.displayGuiScreen(parentScreen);
			break;
		}
	}
}
