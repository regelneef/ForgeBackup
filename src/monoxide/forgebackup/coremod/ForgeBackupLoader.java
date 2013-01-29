package monoxide.forgebackup.coremod;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@TransformerExclusions("monoxide.forgebackup.coremod")
public class ForgeBackupLoader implements IFMLLoadingPlugin {
	@Override
	public String[] getLibraryRequestClass() {
		return new String[] { "monoxide.forgebackup.coremod.LibraryDownloader" };
	}
	
	@Override
	public String[] getASMTransformerClass() {
		return new String[] {};
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
