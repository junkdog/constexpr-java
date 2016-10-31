package net.onedaybeard.constexpr;

import net.onedaybeard.constexpr.inspect.MethodDescriptor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;

public final class AsmUtil {
	private AsmUtil() {}

	public static ClassNode classNode(Class<?> klazz) {
		return classNode(classReader(klazz));
	}

	public static ClassNode classNode(InputStream in) {
		return classNode(classReader(in));
	}

	public static ClassNode classNode(File file) {
		return classNode(classReader(file));
	}

	public static ClassNode classNode(ClassReader cr) {
		ClassNode cn = new ClassNode(Opcodes.ASM5);
		cr.accept(cn, 0);
		return cn;
	}

	public static ClassReader classReader(Class<?> klazz) {
		String resourceName = "/" + klazz.getName().replace('.', '/') + ".class";
		return classReader(klazz.getResourceAsStream(resourceName));
	}

	public static ClassReader classReader(InputStream in) {
		try {
			return new ClassReader(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static ClassReader classReader(File file) {
		try {
			return classReader(new BufferedInputStream(new FileInputStream(file)));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}


	public static ClassReader classReader(byte[] transform) {
		return classReader(new ByteArrayInputStream(transform));
	}

	public static boolean isStaticInitizalizer(MethodDescriptor md) {
		return isStaticInitizalizer(md.name, md.desc);
	}

	public static boolean isStaticInitizalizer(String name, String desc) {
		return "<clinit>".equals(name) && "()V".equals(desc);
	}
}
