package monoxide.forgebackup.coremod.asm;

import java.util.Map;

import monoxide.forgebackup.BackupLog;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.google.common.collect.Maps;

public class EssentialsBackupTransformer extends AsmTransformer {
	public EssentialsBackupTransformer() {
		Map<String, String> tmp;
		// MCP/obfuscated version. Nothing needs to change in a live MC instance.
		tmp = Maps.newHashMap();
		mappings.put("com.ForgeEssentials.backup.ModuleBackup", tmp);
	}
	
	@Override
	protected void doTransform(ClassNode classNode, Map<String, String> mapping) {
		BackupLog.info("Mangling ForgeEssentials for its own good. Their Backups module was not actually loaded.");
		
		classNode.fields.clear();
		for (int i = 0; i < classNode.methods.size();) {
			MethodNode node = (MethodNode)classNode.methods.get(i);
			
			BackupLog.info("Inspecting method: %s", node.name);
			if (!node.name.equals("<init>")) {
				classNode.methods.remove(i);
			} else {
				i++;
			}
		}
	}
}
