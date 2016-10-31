package net.onedaybeard.constexpr.visitor;

import net.onedaybeard.constexpr.inspect.FieldDescriptor;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.annotation.Annotation;

class FieldAnnotationScanner extends FieldVisitor {
	private final FieldDescriptor field;
	private AnnotationScanner<FieldDescriptor> scanner;

	public FieldAnnotationScanner(FieldVisitor fv,
	                              FieldDescriptor field) {

		super(Opcodes.ASM5, fv);
		this.field = field;
	}

	public FieldAnnotationScanner scanFor(Class<? extends Annotation> annotation,
	                                      AnnotationFoundListener<FieldDescriptor> listener) {

		scanner = new AnnotationScanner<>(field, annotation, listener);
		return this;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		scanner.visitAnnotation(desc);
		return super.visitAnnotation(desc, visible);
	}

}
