package monoxide.forgebackup.coremod;

import cpw.mods.fml.relauncher.ILibrarySet;

public class LibraryDownloader implements ILibrarySet {
	@Override
	public String[] getLibraries() {
		return new String[] {
			"commons-compress-1.4.1.jar",
			"org.eclipse.jgit-2.2.0.201212191850-r.jar"
		};
	}

	@Override
	public String[] getHashes() {
		return new String[] {
			"a045661155918847e731465645e23ad915d5fc71",
			"97d0761b9dd618d1f9f6c16c35c3ddf045ba536c",
		};
	}

	@Override
	public String getRootURL() {
		return "https://github.com/monoxide0184/forgebackup/raw/master/libs/%s";
	}
}
