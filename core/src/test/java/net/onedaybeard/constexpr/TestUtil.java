package net.onedaybeard.constexpr;

import net.onedaybeard.constexpr.inspect.ClassMetadata;
import net.onedaybeard.constexpr.inspect.MethodDescriptor;
import net.onedaybeard.constexpr.visitor.ConstExprScanner;
import net.onedaybeard.constexpr.visitor.ConstExprScannerTest;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class TestUtil {
	private TestUtil() {}

	public static String toString(Class<?> type) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		AsmUtil.classReader(type).accept(
			new TraceClassVisitor(pw), ClassReader.EXPAND_FRAMES);

		return sw.toString();
	}

	public static String toString(Class<?> type, String method) {
		return toString(type, method, null);
	}

	public static String toString(Class<?> type, String method, String desc) {
		MethodNode mn = findMethod(type, method, desc);

		String s = toString(mn);
		return s;
	}

	public static String toString(MethodNode mn) {
		TraceMethodVisitor tcv = new TraceMethodVisitor(null, new Textifier());
		mn.accept(tcv);

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println(mn.name + " " + mn.desc);

		tcv.p.print(pw);
		pw.flush();

		return sw.toString();
	}

	public static MethodNode findMethod(Class<?> type, String method, String desc) {
		List<MethodNode> methods = AsmUtil.classNode(type).methods;
		return methods.stream()
			.filter(m -> m.name.equals(method) && (desc == null || m.desc.equals(desc)))
			.findFirst()
			.orElse(null);
	}

	public static void verifyClass(byte[] bytes) {
		StringWriter sw = new StringWriter();
		PrintWriter printer = new PrintWriter(sw);

		CheckClassAdapter.verify(new ClassReader(bytes), false, printer);
		String result = sw.toString();
		if (result.length() > 0) {
			throw new RuntimeException(result);
		}
	}

	public static ClassMetadata scan(Class<?> klazz) throws Exception {
		String classResource = "/" + klazz.getName().replace('.', '/') + ".class";
		return scan(klazz, ConstExprScannerTest.class.getResourceAsStream(classResource));
	}

	public static ClassMetadata scan(byte[] klazz) throws Exception {
		verifyClass(klazz);
		return scan(null, new ByteArrayInputStream(klazz));
	}

	private static ClassMetadata scan(Class<?> klazz, InputStream stream) throws IOException {
		ClassReader cr = new ClassReader(stream);

		ClassMetadata info = new ClassMetadata(klazz);
		ClassVisitor cv = new ConstExprScanner(info);

		cr.accept(cv, 0);
		stream.close();

		return info;
	}

	public static <T> T readStaticField(Class<?> type, String field) throws Exception {
		for (;; type = type.getSuperclass()) {
			Field f = Arrays.stream(type.getDeclaredFields())
				.filter(fd -> field.equals(fd.getName()))
				.findFirst()
				.orElse(null);

			if (f != null) {
				f.setAccessible(true);
				return (T) f.get(null);
			}
		}
	}

	public static List<AbstractInsnNode> filterBodyNoDebug(MethodNode mn) {
		Iterable<AbstractInsnNode> iterable = () -> mn.instructions.iterator();
		return StreamSupport.stream(iterable.spliterator(), false)
			.filter(i -> !(i instanceof LabelNode))
			.filter(i -> !(i instanceof LineNumberNode))
			.collect(Collectors.toList());
	}

	public static MethodDescriptor method(ClassMetadata metadata, String method) throws Exception {
			return metadata.methods.stream()
				.filter(md -> method.equals(md.name))
				.findFirst()
				.orElse(null);
	}
}
