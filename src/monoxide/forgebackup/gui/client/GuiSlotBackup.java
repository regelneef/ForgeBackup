package monoxide.forgebackup.gui.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;

public class GuiSlotBackup extends GuiSlot {

	GuiScreen parent;
	
	public GuiSlotBackup(Minecraft mc, GuiSelectBackup parent) {
		super(mc, parent.width, parent.height, 32, parent.height - 40, 36);
		this.parent = parent;
	}

	@Override
	protected int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void elementClicked(int var1, boolean var2) {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean isSelected(int var1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void drawBackground() {
		this.parent.drawDefaultBackground();
	}

	@Override
	protected void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5) {
		// TODO Auto-generated method stub
	}

}
