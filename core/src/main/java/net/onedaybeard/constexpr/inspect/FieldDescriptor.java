package net.onedaybeard.constexpr.inspect;

import net.onedaybeard.constexpr.InvalidConstExprException;
import org.objectweb.asm.Type;

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.STATIC;

public class FieldDescriptor {
	public final int access;
	public final String name;
	public final String desc;
	public final String signature;
	public final Object value;

	public boolean isConstExpr;

	public FieldDescriptor(int access, String name, String desc, String signature, Object value) {
		this.access = access;
		this.name = name;
		this.desc = desc;
		this.signature = signature;
		this.value = value;
	}

	public void validate() {
		if (!isConstExpr)
			return;

		int access = this.access & (STATIC | FINAL);
		if (access != (STATIC | FINAL))
			throw new InvalidConstExprException("fields must be 'static final'", this);

		boolean isPrimitive = desc.length() == 1;
		if (!isPrimitive && !isString())
			throw new InvalidConstExprException("fields can only be primitive values or strings", this);
	}

	private boolean isString() {
		return desc.equals(Type.getDescriptor(String.class));
	}

	@Override
	public String toString() {
		return "FieldDescriptor[" +
			"access=" + access +
			" name='" + name + '\'' +
			" desc='" + desc + '\'' +
			" signature='" + signature + '\'' +
			" value=" + value +
			" isConstExpr=" + isConstExpr +
			']';
	}
}
