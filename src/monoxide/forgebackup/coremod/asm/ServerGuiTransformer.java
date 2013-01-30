package monoxide.forgebackup.coremod.asm;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import monoxide.forgebackup.BackupLog;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

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
		tmp.put("methodName", "initGUI");
		tmp.put("methodDesc", "(Lnet/minecraft/server/dedicated/DedicatedServer;)V");
		
		// Obfuscated version
		tmp = Maps.newHashMap();
		mappings.put("hv", tmp);
		tmp.put("methodName", "a");
		tmp.put("methodDesc", "(Lho;)V");
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
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}
