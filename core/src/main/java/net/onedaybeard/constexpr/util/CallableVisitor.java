package net.onedaybeard.constexpr.util;

import net.onedaybeard.constexpr.AsmUtil;
import org.objectweb.asm.ClassReader;

import java.io.*;
import java.util.concurrent.Callable;

public abstract class CallableVisitor<T> implements Callable<T> {
	private final ClassReader cr;

	protected CallableVisitor(ClassReader cr) {
		this.cr = cr;
	}

	protected CallableVisitor(Class<?> type) {
		cr = AsmUtil.classReader(type);
	}

	protected CallableVisitor(byte[] bytes) {
		cr = AsmUtil.classReader(new ByteArrayInputStream(bytes));
	}

	protected CallableVisitor(File file) {
		try {
			cr = AsmUtil.classReader(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract T process(ClassReader cr) throws IOException;

	@Override
	public final T call() throws Exception {
		return process(cr);
	}
}
