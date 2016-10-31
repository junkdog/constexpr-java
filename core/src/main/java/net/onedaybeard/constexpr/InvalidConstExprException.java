package net.onedaybeard.constexpr;

import net.onedaybeard.constexpr.inspect.FieldDescriptor;
import net.onedaybeard.constexpr.inspect.MethodDescriptor;

public class InvalidConstExprException extends RuntimeException {
	public InvalidConstExprException(String message, FieldDescriptor descriptor) {
		super(message + ": " + descriptor);
	}

	public InvalidConstExprException(String message, MethodDescriptor descriptor) {
		super(message + ": " + descriptor);
	}
}
