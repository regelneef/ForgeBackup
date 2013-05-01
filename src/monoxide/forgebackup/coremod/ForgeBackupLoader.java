package monoxide.forgebackup.coremod;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@TransformerExclusions({
	"monoxide.forgebackup.coremod",
	"monoxide.forgebackup.coremod.asm",
})
@MCVersion("1.5.1")
public class ForgeBackupLoader implements IFMLLoadingPlugin {
	@Override
	public String[] getLibraryRequestClass() {
		return new String[] { "monoxide.forgebackup.coremod.LibraryDownloader" };
	}
	
	@Override
	public String[] getASMTransformerClass() {
		return new String[] {
			"monoxide.forgebackup.coremod.asm.EssentialsBackupTransformer",
			"monoxide.forgebackup.coremod.asm.BackupAccessTransformer",
			"monoxide.forgebackup.coremod.asm.ServerGuiTransformer",
			"monoxide.forgebackup.coremod.asm.GuiSelectWorldTransformer",
			"monoxide.forgebackup.coremod.asm.GuiWorldSlotTransformer",
			"monoxide.forgebackup.coremod.asm.DedicatedServerTransformer",
		};
	}
	
	@Override
	public String getModContainerClass() {
		return null;
	}
	
	@Override
	public String getSetupClass() {
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data) {}
}
