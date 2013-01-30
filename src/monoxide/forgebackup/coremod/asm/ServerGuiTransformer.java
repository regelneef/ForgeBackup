package monoxide.forgebackup.coremod.asm;

import java.util.Iterator;
import java.util.Map;

import monoxide.forgebackup.BackupLog;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.IClassTransformer;

public class ServerGuiTransformer implements IClassTransformer {
	private final Map<String, Map<String, String>> mappings;
	
	public ServerGuiTransformer() {
		mappings = Maps.newHashMap();
		Map<String, String> tmp;
		
		// MCP version
		tmp = Maps.newHashMap();
		mappings.put("net.minecraft.server.gui.ServerGUI", tmp);
		tmp.put("javaName", "net/minecraft/server/gui/ServerGUI");
		tmp.put("methodName", "initGUI");
		tmp.put("methodDesc", "(Lnet/minecraft/server/dedicated/DedicatedServer;)V");
		tmp.put("callDesc", "(Lnet/minecraft/server/dedicated/DedicatedServer;)Ljavax/swing/JComponent;");
		
		// Obfuscated version
		tmp = Maps.newHashMap();
		mappings.put("hv", tmp);
		tmp.put("javaName", "hv");
		tmp.put("methodName", "a");
		tmp.put("methodDesc", "(Lho;)V");
		tmp.put("callDesc", "(Lho;)Ljavax/swing/JComponent;");
	}
	
	@Override
	public byte[] transform(String name, byte[] bytes) {
		if (mappings.containsKey(name)) {
			return transformServerGui(bytes, mappings.get(name));
		}
		return bytes;
	}
	
	private byte[] transformServerGui(byte[] bytes, Map<String, String> mapping) {
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while (methods.hasNext()) {
			MethodNode method = methods.next();
			if (method.name.equals(mapping.get("methodName")) && method.desc.equals(mapping.get("methodDesc"))) {
				BackupLog.fine("Found ServerGUI.initGUI");
				
				for (int i = 0; i < method.instructions.size(); i++) {
					// Check for the call to initialise a new ServerGUI
					if (
						method.instructions.get(i).getOpcode() == Opcodes.NEW &&
						((TypeInsnNode)method.instructions.get(i)).desc.equals(mapping.get("javaName"))
					) {
						for (int j = 0; j < 4; j++) {
							method.instructions.remove(method.instructions.get(i+j));
						}
						
						InsnList toInject = new InsnList();
						
						toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
						toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "monoxide/forgebackup/events/ForgeBackupEvents", "ServerGuiInitialising", mapping.get("callDesc")));
						
						method.instructions.insertBefore(method.instructions.get(i), toInject);
						
						BackupLog.fine("Injected our event successfully.");
						break;
					}
				}
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}
