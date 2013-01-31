package monoxide.forgebackup.coremod.asm;

import java.util.Map;

import org.objectweb.asm.tree.ClassNode;

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
	protected void doTransform(ClassNode classNode, Map<String, String> mapping) {
		// TODO Auto-generated method stub
	}
}
