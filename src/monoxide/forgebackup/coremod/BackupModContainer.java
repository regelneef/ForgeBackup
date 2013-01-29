package monoxide.forgebackup.coremod;

import java.io.File;
import java.lang.reflect.Field;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import monoxide.forgebackup.ForgeBackup;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.InvalidVersionSpecificationException;
import cpw.mods.fml.common.versioning.VersionRange;

public class BackupModContainer implements ModContainer {
	private Object mod;
	private final String modid = "forgebackup";
	private ModMetadata meta;
	private boolean enabled = true;
	private EventBus eventBus;
	
	public BackupModContainer() throws InvalidVersionSpecificationException {
		meta = new ModMetadata();
		meta.modId = modid;
		meta.name = "ForgeBackup";
		meta.description = "";
		meta.version = "1.0.0";
		meta.requiredMods = Sets.newHashSet(
			(ArtifactVersion)new DefaultArtifactVersion("Forge", VersionRange.createFromVersionSpec("[6.6.0,)")),
			(ArtifactVersion)new DefaultArtifactVersion("FML", VersionRange.createFromVersionSpec("[4.7.3,)"))
		);
		meta.dependencies = Lists.newArrayList(
			(ArtifactVersion)new DefaultArtifactVersion("ForgeEssentials", true)
		);
		meta.dependants = Lists.newArrayList();
	}
	
	@Override
	public void bindMetadata(MetadataCollection mc) {}
	
	@Override
	public boolean registerBus(EventBus bus, LoadController controller)
	{
		if (enabled)
		{
			FMLLog.fine("Enabling mod %s", getModId());
			eventBus = bus;
			bus.register(this);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public Object getMod() {
		return mod;
	}
	
	@Override
	public String getModId() {
		return modid;
	}
	
	@Override
	public boolean matches(Object mod) {
		return this.mod == mod;
	}

	@Override
	public String getName() {
		return meta.name;
	}

	@Override
	public String getVersion() {
		return meta.version;
	}

	@Override
	public File getSource() {
		return null;
	}

	@Override
	public ModMetadata getMetadata() {
		return meta;
	}

	@Override
	public void setEnabledState(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public Set<ArtifactVersion> getRequirements() {
		return meta.requiredMods;
	}

	@Override
	public List<ArtifactVersion> getDependencies() {
		return meta.dependencies;
	}

	@Override
	public List<ArtifactVersion> getDependants() {
		return meta.dependants;
	}

	@Override
	public String getSortingRules() {
		return "";
	}

	@Override
	public ArtifactVersion getProcessedVersion() {
		return new DefaultArtifactVersion(meta.modId, getVersion());
	}

	@Override
	public boolean isImmutable() {
		return true;
	}

	@Override
	public boolean isNetworkMod() {
		return false;
	}

	@Override
	public String getDisplayVersion() {
		return getVersion();
	}

	@Override
	public VersionRange acceptableMinecraftVersionRange() {
		try {
			return VersionRange.createFromVersionSpec("[1.4.6,1.4.7]");
		} catch (InvalidVersionSpecificationException e) {}
		return null;
	}

	@Override
	public Certificate getSigningCertificate() {
		return null;
	}
	
	@Subscribe
	public void constructMod(FMLConstructionEvent event) {
		mod = new ForgeBackup();
		eventBus.register(mod);
		
		for (Field f : mod.getClass().getDeclaredFields()) {
			Instance annotation = f.getAnnotation(Instance.class);
			f.setAccessible(true);
			
			if (annotation != null) {
				try {
					f.set(mod, mod);
				} catch (Throwable e) {
					FMLLog.log(Level.SEVERE, e, "Unable to set the instance field for %s!", modid);
				}
			}
		}
	}
}
