package net.onedaybeard.constexpr.transformer;


import net.onedaybeard.constexpr.inspect.ClassMetadata;
import net.onedaybeard.constexpr.inspect.FieldDescriptor;
import net.onedaybeard.constexpr.inspect.MethodDescriptor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.*;
import java.util.stream.StreamSupport;

public class CinitConstExprTransformer extends MethodNode implements Opcodes {
	private final ClassMetadata metadata;
	private final MethodVisitor mv;

	private static final Class[] INSN_NODE_DECREMENTORS = new Class[] {
		LdcInsnNode.class,
		MethodInsnNode.class
	};

	public CinitConstExprTransformer(ClassMetadata metadata, MethodDescriptor md, MethodVisitor mv) {
		super(ASM5, md.access, md.name, md.desc, md.signature, md.exceptions);
		this.metadata = metadata;
		this.mv = mv;
	}

	@Override
	public void visitEnd() {
		metadata.fields.stream()
			.filter(fd -> fd.isConstExpr && fd.value == null)
			.forEach(this::removeInitializer);

		Iterable<AbstractInsnNode> iterable = () -> instructions.iterator();
		metadata.emptyClinit = 1 == StreamSupport.stream(iterable.spliterator(), false)
			.filter(i -> !(i instanceof LabelNode))
			.filter(i -> !(i instanceof LineNumberNode))
			.count();

		accept(mv);
	}

	private void removeInitializer(FieldDescriptor fd) {
		int last = endIndexOf(fd);
		int begin = "Ljava/lang/String;".equals(fd.desc)
			? beginStringIndexOf(last)
			: beginIndexOf(last);
		AbstractInsnNode lastNode = instructions.get(last);

		ListIterator<AbstractInsnNode> it = instructions.iterator(begin);
		while (it.hasNext()) {
			AbstractInsnNode node = it.next();
			it.remove();

			if (node == lastNode)
				break;
		}
	}


	private int endIndexOf(FieldDescriptor fd) {
		ListIterator<AbstractInsnNode> it = instructions.iterator();
		while (it.hasNext()) {
			AbstractInsnNode node = it.next();
			if (node instanceof FieldInsnNode) {
				FieldInsnNode fin = (FieldInsnNode) node;
				if (fd.name.equals(fin.name)) {
					return instructions.indexOf(fin);
				}
			}
		}

		throw new RuntimeException("what the...");
	}

	private int beginIndexOf(int endIndex) {
		int i = endIndex - 1;
		for (int remaining = 1; remaining != 0; i--) {
			AbstractInsnNode insn = instructions.get(i);
			if (insn instanceof InsnNode) {
				remaining++;
			} else if (isInsnNodeDecrementing(insn)) {
				remaining--;
			}
		}

		return i + 1; // +1 due to final decrement in loop
	}

	private int beginStringIndexOf(int endIndex) {
		int i = endIndex - 1;
		while (i > 0) {
			AbstractInsnNode insn = instructions.get(i);
			if (insn instanceof TypeInsnNode) {
				TypeInsnNode tin = (TypeInsnNode) insn;
				if (NEW == tin.getOpcode() && "java/lang/StringBuilder".equals(tin.desc)) {
					return i;
				}
			} else {
				i--;
			}
		}

		throw new RuntimeException();
	}

	private static boolean isInsnNodeDecrementing(AbstractInsnNode insn) {
		return Arrays.stream(INSN_NODE_DECREMENTORS).anyMatch(t -> t == insn.getClass());
	}
}
