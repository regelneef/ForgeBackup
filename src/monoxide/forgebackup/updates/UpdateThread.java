package monoxide.forgebackup.updates;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;

import monoxide.forgebackup.BackupLog;
import monoxide.forgebackup.ForgeBackup;
import net.minecraft.crash.CallableMinecraftVersion;
import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
//import argo.jdom.JsonNodeDoesNotMatchPathElementsException;
import argo.jdom.JsonRootNode;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.common.versioning.VersionRange;

public class UpdateThread implements Runnable {
	public static final String updateUrl = "https://raw.github.com/monoxide0184/ForgeBackup/master/releases.json"; 
	
	public void run() {
		try {
			URL url = new URL(updateUrl);
			JsonRootNode root = new JdomParser().parse(new InputStreamReader(url.openStream()));
			String mcVersion = new CallableMinecraftVersion(null).minecraftVersion();
			List<JsonNode> releases;
			
			try {
				releases = root.getArrayNode(mcVersion);
			} catch (IllegalArgumentException e) {
				BackupLog.warning("There are no releases at all for this version of Minecraft (%s)", mcVersion);
				return;
			}
			
			boolean urlMissing = false;
			VersionRange upgradeRange = VersionParser.parseRange("("+ForgeBackup.instance().getVersion()+",]");
			String updateVersion = null;
			String updateUrl = null;
			for (JsonNode release : releases) {
				String version = release.getStringValue("version");
				if (upgradeRange.containsVersion(new DefaultArtifactVersion(version))) {
					if (updateVersion == null) {
						updateVersion = version;
						if (release.isNode("url")) {
							updateUrl = release.getStringValue("url");
						}
					}
					if (!release.isNode("url")) {
						urlMissing = true;
					}
				}
			}
			
			if (updateVersion != null) {
				if (urlMissing) {
					BackupLog.warning("There is a major update available for ForgeBackup (%s). Please visit the forum and check the changelog before installing this update.", updateVersion);
				} else {
					BackupLog.info("There is an update available for ForgeBackup (%s). You can download it from: %s", updateVersion, updateUrl);
				}
			}
		} catch (Throwable e) {
			if (ForgeBackup.instance().config().getLoggingLevel() == 2) {
				BackupLog.log(Level.WARNING, e, "Unable to check for updates successfully.");
			} else {
				BackupLog.warning("Unable to check for updates successfully.");
			}
		}
	}
}
