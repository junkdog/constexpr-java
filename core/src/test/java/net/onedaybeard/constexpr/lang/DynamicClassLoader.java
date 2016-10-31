package net.onedaybeard.constexpr.lang;

public class DynamicClassLoader extends ClassLoader {
	public DynamicClassLoader(ClassLoader parent) {
		super(parent);
	}

	public DynamicClassLoader() {
		super();
	}

	public Class<?> register(String name, byte[] bytecode) {
		return defineClass(name, bytecode, 0, bytecode.length);
	}

	public Class<?> register(Class<?> type, byte[] bytecode) {
		return register(type.getName(), bytecode);
	}
}
