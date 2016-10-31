package net.onedaybeard.constexpr.visitor;

interface AnnotationFoundListener<T> {
	void onFound(T descriptor);
}
