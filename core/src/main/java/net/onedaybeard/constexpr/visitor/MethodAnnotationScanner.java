package net.onedaybeard.constexpr.visitor;

import net.onedaybeard.constexpr.inspect.MethodDescriptor;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.annotation.Annotation;

public class MethodAnnotationScanner extends MethodVisitor implements Opcodes {
	private final MethodDescriptor descriptor;
	private AnnotationScanner<MethodDescriptor> scanner;

	public MethodAnnotationScanner(MethodVisitor mv, MethodDescriptor descriptor) {
		super(ASM5, mv);
		this.descriptor = descriptor;
	}

	public MethodAnnotationScanner scanFor(Class<? extends Annotation> annotation,
	                                       AnnotationFoundListener<MethodDescriptor> listener) {

		scanner = new AnnotationScanner<>(descriptor, annotation, listener);
		return this;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		scanner.visitAnnotation(desc);
		return super.visitAnnotation(desc, visible);
	}
}
