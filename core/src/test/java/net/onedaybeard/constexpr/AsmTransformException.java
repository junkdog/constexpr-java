package net.onedaybeard.constexpr;

public class AsmTransformException extends RuntimeException {
	public AsmTransformException(Class<?> type, Throwable cause) {
		super(toMessage(type), cause);
	}

	private static String toMessage(Class<?> type) {
		try {
			return TestUtil.toString(type);
		} catch (Exception e) {
			return type.getName() + ": unable to decompile";
		}
	}
}
