package net.onedaybeard.constexpr.visitor;

import net.onedaybeard.constexpr.AsmUtil;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClinitRemoverWeaver extends ClassVisitor implements Opcodes {

	public ClinitRemoverWeaver(ClassVisitor cv) {
		super(ASM5, cv);
	}

	@Override
	public MethodVisitor visitMethod(int access,
	                                 String name,
	                                 String desc,
	                                 String signature,
	                                 String[] exceptions) {


		if (AsmUtil.isStaticInitizalizer(name, desc)) {
			return null;
		} else {
			return super.visitMethod(access, name, desc, signature, exceptions);
		}
	}
}
