package monoxide.forgebackup.coremod.asm;

import java.util.Iterator;
import java.util.Map;

import monoxide.forgebackup.BackupLog;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
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
		tmp.put("initGuiMethodName", "initGui");
		tmp.put("initGuiMethodDesc", "()V");
		tmp.put("initGuiCallDesc", "(Lnet/minecraft/client/gui/GuiSelectWorld;)V");
		tmp.put("guiButtonJavaName", "net/minecraft/client/gui/GuiButton");
		tmp.put("actionPerformedMethodName", "actionPerformed");
		tmp.put("minecraftJavaName", "net/minecraft/client/Minecraft");
		tmp.put("displayGuiScreen", "displayGuiScreen");
		tmp.put("guiScreenJavaName", "net/minecraft/client/gui/GuiScreen");
		tmp.put("guiScreen.mc", "mc");
		tmp.put("saveList", "saveList");
		tmp.put("idField", "id");
		tmp.put("selectedWorld", "selectedWorld");
		
		// Obfuscated version
		tmp = Maps.newHashMap();
		mappings.put("auo", tmp);
		tmp.put("javaName", "auo");
		tmp.put("initGuiMethodName", "A_");
		tmp.put("initGuiMethodDesc", "()V");
		tmp.put("initGuiCallDesc", "(Lauo;)V");
		tmp.put("guiButtonJavaName", "atb");
		tmp.put("actionPerformedMethodName", "a");
		tmp.put("minecraftJavaName", "net/minecraft/client/Minecraft");
		tmp.put("displayGuiScreen", "a");
		tmp.put("guiScreenJavaName", "aul");
		tmp.put("guiScreen.mc", "f");
		tmp.put("saveList", "o");
		tmp.put("idField", "f");
		tmp.put("selectedWorld", "n");
	}
	
	@Override
	protected void doTransform(ClassNode classNode, Map<String, String> mapping) {
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while (methods.hasNext()) {
			MethodNode method = methods.next();
			if (method.name.equals(mapping.get("initGuiMethodName")) && method.desc.equals(mapping.get("initGuiMethodDesc"))) {
				BackupLog.info("Found GuiSelectWorld.initGui");
				patchInitGui(method, mapping);
			}
			
			else if (method.name.equals(mapping.get("actionPerformedMethodName")) && method.desc.equals(String.format("(L%s;)V", mapping.get("guiButtonJavaName")))) {
				BackupLog.info("Found GuiSelectWorld.actionPerformed");
				patchActionPerformed(method, mapping);
			}
		}
		
		addSelectButtonField(classNode, mapping);
	}
	
	private void patchInitGui(MethodNode method, Map<String, String> mapping) {
		for (int i = 0; i < method.instructions.size(); i++) {
			if (method.instructions.get(i).getOpcode() == Opcodes.RETURN) {
				InsnList toInject = new InsnList();
				
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
				toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "monoxide/forgebackup/events/ForgeBackupEvents", "modifyGuiSelectWorld", mapping.get("initGuiCallDesc")));
				
				method.instructions.insertBefore(method.instructions.get(i), toInject);
				
				BackupLog.fine("Injected our event successfully.");
				return;
			}
		}
	}
	
	private void patchActionPerformed(MethodNode method, Map<String, String> mapping) {
		LabelNode exitIf = null;
		LabelNode endOfElseIf = new LabelNode(new Label());
		AbstractInsnNode insertionPoint = null;
		boolean foundBranchSeven = false;
		
		for (int i = 0; i < method.instructions.size(); i++) {
			AbstractInsnNode instruction = method.instructions.get(i);
			
			if (instruction.getOpcode() == Opcodes.BIPUSH && ((IntInsnNode)instruction).operand == 7) {
				foundBranchSeven = true;
				BackupLog.info("Found branch 7");
			}
			
			if (foundBranchSeven && insertionPoint == null && instruction.getOpcode() == Opcodes.GOTO) {
				BackupLog.info("Found insertion point");
				insertionPoint = instruction;
				while (insertionPoint.getType() != AbstractInsnNode.LABEL) {
					insertionPoint = insertionPoint.getNext();
				}
			}
			
			// If this is a label, then update the exitif label. It's the last label in the function.
			if (instruction.getType() == AbstractInsnNode.LABEL) {
				BackupLog.info("Found a label");
				exitIf = (LabelNode)instruction;
			}
			
			// We break on return as there's a debug label after the return when running in eclipse.
			if (insertionPoint != null && instruction.getOpcode() == Opcodes.RETURN) {
				BackupLog.info("Found return");
				break;
			}
		}
		
		if (insertionPoint == null) {
			BackupLog.error("Unable to patch GuiSelectWorld.actionPerformed. You won't be able to restore backups.");
			return;
		}
		
		InsnList codeToInsert = new InsnList();
		codeToInsert.add(new VarInsnNode(Opcodes.ALOAD, 1));
		codeToInsert.add(new FieldInsnNode(Opcodes.GETFIELD, mapping.get("guiButtonJavaName"), mapping.get("idField"), "I"));
		codeToInsert.add(new IntInsnNode(Opcodes.BIPUSH, 8));
		codeToInsert.add(new JumpInsnNode(Opcodes.IF_ICMPNE, endOfElseIf));
		codeToInsert.add(new VarInsnNode(Opcodes.ALOAD, 0));
		codeToInsert.add(new FieldInsnNode(Opcodes.GETFIELD, mapping.get("javaName"), mapping.get("guiScreen.mc"), String.format("L%s;", mapping.get("minecraftJavaName"))));
		codeToInsert.add(new VarInsnNode(Opcodes.ALOAD, 0));
		codeToInsert.add(new VarInsnNode(Opcodes.ALOAD, 0));
		codeToInsert.add(new FieldInsnNode(Opcodes.GETFIELD, mapping.get("javaName"), mapping.get("saveList"), "Ljava/util/List;"));
		codeToInsert.add(new VarInsnNode(Opcodes.ALOAD, 0));
		codeToInsert.add(new FieldInsnNode(Opcodes.GETFIELD, mapping.get("javaName"), mapping.get("selectedWorld"), "I"));
		codeToInsert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "monoxide/forgebackup/events/ForgeBackupEvents", "getBackupListGui", String.format("(L%s;Ljava/util/List;I)L%s;", mapping.get("javaName"), mapping.get("guiScreenJavaName"))));
		codeToInsert.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, mapping.get("minecraftJavaName"), mapping.get("displayGuiScreen"), String.format("(L%s;)V", mapping.get("guiScreenJavaName"))));
		codeToInsert.add(new JumpInsnNode(Opcodes.GOTO, exitIf));
		codeToInsert.add(endOfElseIf);
		
		method.instructions.insert(insertionPoint, codeToInsert);
		BackupLog.fine("Successfully inserted our hook.");
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
