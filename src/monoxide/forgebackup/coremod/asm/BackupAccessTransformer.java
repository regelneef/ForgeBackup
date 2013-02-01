package monoxide.forgebackup.coremod.asm;

import java.io.IOException;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;

public class BackupAccessTransformer extends AccessTransformer {
	public BackupAccessTransformer() throws IOException {
		super("forgebackup_at.cfg");
	}
}
