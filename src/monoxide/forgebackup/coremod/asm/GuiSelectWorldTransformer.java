package monoxide.forgebackup.coremod.asm;

import java.util.Iterator;
import java.util.Map;

import monoxide.forgebackup.BackupLog;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.collect.Maps;

public class GuiSelectWorldTransformer extends AsmTransformer {
	public GuiSelectWorldTransformer() {
		Map<String, String> tmp;
		
		// MCP version
		tmp = Maps.newHashMap();
		mappings.put("net.minecraft.client.gui.GuiSelectWorld", tmp);
		tmp.put("javaName", "net/minecraft/client/gui/GuiSelectWorld");
		tmp.put("methodName", "initGui");
		tmp.put("methodDesc", "()V");
		tmp.put("callDesc", "(Lnet/minecraft/client/gui/GuiSelectWorld;)V");
		tmp.put("guiButtonJavaName", "net/minecraft/client/gui/GuiButton");
		
		// Obfuscated version
		tmp = Maps.newHashMap();
		mappings.put("auo", tmp);
		tmp.put("javaName", "auo");
		tmp.put("methodName", "A_");
		tmp.put("methodDesc", "()V");
		tmp.put("callDesc", "(Lauo;)V");
		tmp.put("guiButtonJavaName", "atb");
	}
	
	@Override
	protected void doTransform(ClassNode classNode, Map<String, String> mapping) {
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while (methods.hasNext()) {
			MethodNode method = methods.next();
			if (method.name.equals(mapping.get("methodName")) && method.desc.equals(mapping.get("methodDesc"))) {
				BackupLog.fine("Found GuiSelectWorld.initGui");
				
				for (int i = 0; i < method.instructions.size(); i++) {
					// Check for the call to initialise a new ServerGUI
					if (method.instructions.get(i).getOpcode() == Opcodes.RETURN) {
						InsnList toInject = new InsnList();
						
						toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
						toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "monoxide/forgebackup/events/ForgeBackupEvents", "modifyGuiSelectWorld", mapping.get("callDesc")));
						
						method.instructions.insertBefore(method.instructions.get(i), toInject);
						
						BackupLog.fine("Injected our event successfully.");
						break;
					}
				}
			}
		}
		
		addSelectButtonField(classNode, mapping);
	}

	private void addSelectButtonField(ClassNode classNode, Map<String, String> mapping) {
		classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "buttonRestore", String.format("L%s;", mapping.get("guiButtonJavaName")), null, null));
		
		MethodNode getter = new MethodNode(Opcodes.ACC_STATIC, "getRestoreButton", String.format("(L%s;)L%s;", mapping.get("javaName"), mapping.get("guiButtonJavaName")), null, null);
		getter.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		getter.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, mapping.get("javaName"), "buttonRestore", String.format("L%s;", mapping.get("guiButtonJavaName"))));
		getter.instructions.add(new InsnNode(Opcodes.ARETURN));
		
		classNode.methods.add(getter);
	}
}
