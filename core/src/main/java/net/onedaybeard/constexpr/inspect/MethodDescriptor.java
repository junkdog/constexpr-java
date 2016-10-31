package net.onedaybeard.constexpr.inspect;

import net.onedaybeard.constexpr.InvalidConstExprException;

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.STATIC;

public class MethodDescriptor {
	public final int access;
	public final String name;
	public final String desc;
	public final String signature;
	public final String[] exceptions;

	public boolean isConstExpr;

	public MethodDescriptor(int access, String name, String desc, String signature, String[] exceptions) {
		this.access = access;
		this.name = name;
		this.desc = desc;
		this.signature = signature;
		this.exceptions = exceptions;
	}


	public void validate() {
		if (!isConstExpr)
			return;

		if ((STATIC & access) != STATIC)
			throw new InvalidConstExprException("method must be 'static'", this);
	}
}
