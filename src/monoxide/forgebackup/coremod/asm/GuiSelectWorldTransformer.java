package monoxide.forgebackup.coremod.asm;

import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.IClassTransformer;

public class GuiSelectWorldTransformer implements IClassTransformer {
	private final Map<String, Map<String, String>> mappings;
	
	public GuiSelectWorldTransformer() {
		mappings = Maps.newHashMap();
		Map<String, String> tmp;
		
		// MCP version
		tmp = Maps.newHashMap();
		mappings.put("net.minecraft.client.gui.GuiSelectWorld", tmp);
		tmp.put("javaName", "net/minecraft/client/gui/GuiSelectWorld");
		tmp.put("methodName", "initGui");
		tmp.put("methodDesc", "()V");
		tmp.put("callDesc", "(Lnet/minecraft/client/gui/GuiSelectWorld;)V");
		
		// Obfuscated version
		tmp = Maps.newHashMap();
		mappings.put("auo", tmp);
		tmp.put("javaName", "auo");
		// TODO: Not 100% on this, but didn't get an answer in #mcp if this is intended or not. Will need testing.
		tmp.put("methodName", "A_");
		tmp.put("methodDesc", "()V");
		tmp.put("callDesc", "(Lauo;)V;");
	}
	
	@Override
	public byte[] transform(String name, byte[] bytes) {
		if (mappings.containsKey(name)) {
			return transformServerGui(bytes, mappings.get(name));
		}
		return bytes;
	}
	
	public byte[] transformServerGui(byte[] bytes, Map<String, String> mapping) {
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}
