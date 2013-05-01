package monoxide.forgebackup.coremod.asm;

import java.util.Iterator;
import java.util.Map;

import monoxide.forgebackup.BackupLog;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.collect.Maps;

public class GuiWorldSlotTransformer extends AsmTransformer {
	public GuiWorldSlotTransformer() {
		Map<String, String> tmp;
		
		// MCP version
		tmp = Maps.newHashMap();
		mappings.put("net.minecraft.client.gui.GuiWorldSlot", tmp);
		tmp.put("javaName", "net/minecraft/client/gui/GuiWorldSlot");
		tmp.put("methodName", "elementClicked");
		tmp.put("methodDesc", "(IZ)V");
		tmp.put("markerMethodName", "func_82312_f");
		tmp.put("parentWorldGui", "parentWorldGui");
		tmp.put("guiSelectWorldJavaName", "net/minecraft/client/gui/GuiSelectWorld");
		tmp.put("guiButtonJavaName", "net/minecraft/client/gui/GuiButton");
		tmp.put("enabledField", "enabled");
		
		// Obfuscated version
		tmp = Maps.newHashMap();
		mappings.put("axv", tmp);
		tmp.put("javaName", "axv");
		tmp.put("methodName", "a");
		tmp.put("methodDesc", "(IZ)V");
		tmp.put("markerMethodName", "f");
		tmp.put("parentWorldGui", "a");
		tmp.put("guiSelectWorldJavaName", "axu");
		tmp.put("guiButtonJavaName", "awg");
		tmp.put("enabledField", "g");
	}

	@Override
	protected void doTransform(ClassNode classNode, Map<String, String> mapping) {
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while (methods.hasNext()) {
			MethodNode method = methods.next();
			if (method.name.equals(mapping.get("methodName")) && method.desc.equals(mapping.get("methodDesc"))) {
				BackupLog.info("Found GuiWorldSlot.elementClicked");
				patchElementClicked(method, mapping);
			}
		}
	}

	private void patchElementClicked(MethodNode method, Map<String, String> mapping) {
		boolean lastFieldSet = false;
		
		for (int i = 0; i < method.instructions.size(); i++) {
			if (
				method.instructions.get(i).getOpcode() == Opcodes.INVOKESTATIC && 
				((MethodInsnNode)method.instructions.get(i)).name.equals(mapping.get("markerMethodName"))
			) {
				BackupLog.fine("Found our marker method call.");
				lastFieldSet = true;
			}
			
			else if (lastFieldSet && method.instructions.get(i).getOpcode() == Opcodes.PUTFIELD) {
				InsnList toInject = new InsnList();
				
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
				toInject.add(new FieldInsnNode(Opcodes.GETFIELD, mapping.get("javaName"), mapping.get("parentWorldGui"), String.format("L%s;", mapping.get("guiSelectWorldJavaName"))));
				toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, mapping.get("guiSelectWorldJavaName"), "getRestoreButton", String.format("(L%s;)L%s;", mapping.get("guiSelectWorldJavaName"), mapping.get("guiButtonJavaName"))));
				toInject.add(new VarInsnNode(Opcodes.ILOAD, 3));
				toInject.add(new FieldInsnNode(Opcodes.PUTFIELD, mapping.get("guiButtonJavaName"), mapping.get("enabledField"), "Z"));
				
				method.instructions.insert(method.instructions.get(i), toInject);
				
				BackupLog.info("Injected our callback successfully.");
				return;
			}
		}
		
		BackupLog.error("Failed to inject our callback into GuiWorldSlot.elementClicked(). Your minecraft may be very unstable. You should report this bug.");
	}
}
