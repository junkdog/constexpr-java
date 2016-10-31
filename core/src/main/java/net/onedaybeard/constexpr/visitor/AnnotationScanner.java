package net.onedaybeard.constexpr.visitor;

import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;

class AnnotationScanner<T> {
	private final T descriptor;
	private final AnnotationFoundListener<T> listener;
	private final Class<? extends Annotation> annotation;

	public AnnotationScanner(T descriptor,
	                         Class<? extends Annotation> annotation,
	                         AnnotationFoundListener<T> onFound) {

		this.descriptor = descriptor;
		this.listener = onFound;
		this.annotation = annotation;
	}

	public void visitAnnotation(String desc) {
		if (Type.getDescriptor(annotation).equals(desc))
			listener.onFound(descriptor);
	}
}
