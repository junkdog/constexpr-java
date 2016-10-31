package net.onedaybeard.constexpr.inspect;

import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

public class ClassMetadata {
	public Type type;
	public Class<?> classType;
	public String path;
	public List<FieldDescriptor> fields = new ArrayList<>();
	public List<MethodDescriptor> methods = new ArrayList<>();
	public boolean emptyClinit;

	public ClassMetadata(Class<?> type) {
		this(type != null ? Type.getType(type) : null, type);
	}

	public ClassMetadata(Type type, Class<?> klazz) {
		this.type = type;
		this.classType = klazz;
	}

	public FieldDescriptor field(String f) {
		return fields.stream()
			.filter(fd -> fd.name.equals(f))
			.findFirst()
			.get();
	}

	public void add(FieldDescriptor fieldDescriptor) {
		fields.add(fieldDescriptor);
	}

	public void add(MethodDescriptor descriptor) {
		methods.add(descriptor);
	}

	public boolean containsConstExpr() {
		return fields.stream().anyMatch(fd -> fd.isConstExpr)
			|| methods.stream().anyMatch(md -> md.isConstExpr);
	}
}
