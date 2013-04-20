package monoxide.forgebackup.coremod;

import java.util.Map;

import monoxide.forgebackup.BackupLog;

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
		};
	}
	
	@Override
	public String getModContainerClass() {
		return "monoxide.forgebackup.coremod.BackupModContainer";
	}
	
	@Override
	public String getSetupClass() {
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data) {}
}
