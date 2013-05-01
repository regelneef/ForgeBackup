package monoxide.forgebackup.coremod.asm;

import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.IClassTransformer;

public abstract class AsmTransformer implements IClassTransformer {
	protected final Map<String, Map<String, String>> mappings = Maps.newHashMap();

	@Override
	public final byte[] transform(String name, String transformedName, byte[] bytes) {
		if (bytes == null)
		{
			return null;
		}
		if (mappings.containsKey(name)) {
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(bytes);
			classReader.accept(classNode, 0);

			doTransform(classNode, mappings.get(name));

			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			classNode.accept(writer);
			return writer.toByteArray();
		}
		return bytes;
	}
	
	protected abstract void doTransform(ClassNode classNode, Map<String, String> mapping);
}
