package monoxide.forgebackup.coremod.asm;

import java.util.Iterator;
import java.util.Map;

import monoxide.forgebackup.BackupLog;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.google.common.collect.Maps;

public class DedicatedServerTransformer extends AsmTransformer {
	public DedicatedServerTransformer() {
		Map<String, String> tmp;
		
		// MCP version.
		tmp = Maps.newHashMap();
		mappings.put("net.minecraft.server.dedicated.DedicatedServer", tmp);
		tmp.put("systemExitNow", "systemExitNow");
		
		// Obfuscated version.
		tmp = Maps.newHashMap();
		mappings.put("hz", tmp);
		tmp.put("systemExitNow", "p");
	}
	
	@Override
	protected void doTransform(ClassNode classNode, Map<String, String> mapping) {
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while (methods.hasNext()) {
			MethodNode method = methods.next();
			if (method.name.equals(mapping.get("systemExitNow")) && method.desc.equals("()V")) {
				BackupLog.info("Found DedicatedServer.systemExitNow()");
				
				method.instructions.clear();
				method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "monoxide/forgebackup/events/ForgeBackupServerEvents", "dedicatedServerShutdown", "()V"));
				method.instructions.add(new InsnNode(Opcodes.RETURN));
				method.maxStack = 0;
				method.maxLocals = 1;
				
				// This causes calls to the method to fail
				BackupLog.info("Injected our event successfully.");
				break;
			}
		}
	}
}
