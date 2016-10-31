package net.onedaybeard.constexpr.visitor;

import net.onedaybeard.constexpr.ConstExpr;
import net.onedaybeard.constexpr.inspect.FieldDescriptor;
import net.onedaybeard.constexpr.inspect.ClassMetadata;
import net.onedaybeard.constexpr.inspect.MethodDescriptor;
import org.objectweb.asm.*;

public class ConstExprScanner extends ClassVisitor implements Opcodes {
	private ClassMetadata metadata;

	public ConstExprScanner(ClassMetadata metadata) {
		super(ASM5);
		this.metadata = metadata;
	}

	@Override
	public MethodVisitor visitMethod(int access,
	                                 String name,
	                                 String desc,
	                                 String signature,
	                                 String[] exceptions) {

		MethodDescriptor descriptor = new MethodDescriptor(access, name, desc, signature, exceptions);
		metadata.add(descriptor);

		MethodVisitor mv = new MethodAnnotationScanner(null, descriptor)
			.scanFor(ConstExpr.class, md -> md.isConstExpr = true);

		return mv;
	}

	@Override
	public FieldVisitor visitField(int access,
	                               String name,
	                               String desc,
	                               String signature,
	                               Object value) {

		FieldDescriptor descriptor = new FieldDescriptor(access, name, desc, signature, value);
		metadata.add(descriptor);

		FieldVisitor fv = super.visitField(access, name, desc, signature, value);
		fv = new FieldAnnotationScanner(fv, descriptor)
			.scanFor(ConstExpr.class, fd -> fd.isConstExpr = true);

		return fv;
	}

	@Override
	public void visitEnd() {
		super.visitEnd();

		metadata.fields.stream()
			.forEach(FieldDescriptor::validate);
		metadata.methods.stream()
			.forEach(MethodDescriptor::validate);
	}
}
