package net.onedaybeard.constexpr.visitor;

import net.onedaybeard.constexpr.AsmUtil;
import net.onedaybeard.constexpr.inspect.ClassMetadata;
import net.onedaybeard.constexpr.inspect.MethodDescriptor;
import net.onedaybeard.constexpr.transformer.CinitConstExprTransformer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ConstExprMethodWeaver extends ClassVisitor implements Opcodes {
	private ClassMetadata metadata;

	public ConstExprMethodWeaver(ClassMetadata metadata, ClassVisitor cv) {
		super(ASM5, cv);
		this.metadata = metadata;
	}

	@Override
	public MethodVisitor visitMethod(int access,
	                                 String name,
	                                 String desc,
	                                 String signature,
	                                 String[] exceptions) {

		boolean isConstExpr = metadata.methods.stream()
			.anyMatch(md -> md.isConstExpr
				&& md.access == access
				&& md.name.equals(name)
				&& md.desc.equals(desc)
				&& (md.signature == null || md.signature.equals(signature))
				&& Arrays.equals(md.exceptions, exceptions));

		if (AsmUtil.isStaticInitizalizer(name, desc)) {
			MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
			MethodDescriptor descriptor = metadata.methods.stream()
				.filter(AsmUtil::isStaticInitizalizer)
				.findFirst()
				.get();

			return new CinitConstExprTransformer(metadata, descriptor, mv);
		} else if (isConstExpr) {
			return null; // removing "compile-time function"
		} else {
			return super.visitMethod(access, name, desc, signature, exceptions);
		}
	}


	static List<AbstractInsnNode> filterBodyNoDebug(MethodNode mn) {
		Iterable<AbstractInsnNode> iterable = () -> mn.instructions.iterator();
		return StreamSupport.stream(iterable.spliterator(), false)
			.filter(i -> !(i instanceof LabelNode))
			.filter(i -> !(i instanceof LineNumberNode))
			.collect(Collectors.toList());
	}
}
