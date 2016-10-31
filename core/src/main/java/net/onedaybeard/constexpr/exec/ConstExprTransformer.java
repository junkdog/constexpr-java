package net.onedaybeard.constexpr.exec;

import net.onedaybeard.constexpr.AsmUtil;
import net.onedaybeard.constexpr.ClassUtil;
import net.onedaybeard.constexpr.inspect.ClassMetadata;
import net.onedaybeard.constexpr.util.CallableVisitor;
import net.onedaybeard.constexpr.visitor.ClinitRemoverWeaver;
import net.onedaybeard.constexpr.visitor.ConstExprFieldWeaver;
import net.onedaybeard.constexpr.visitor.ConstExprMethodWeaver;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.IOException;

public class ConstExprTransformer extends CallableVisitor<byte[]> {
	private final ClassMetadata metadata;

	public ConstExprTransformer(ClassMetadata metadata) {
		super(metadata.classType != null
			? AsmUtil.classReader(metadata.classType)
			: AsmUtil.classReader(new File(metadata.path)));
		this.metadata = metadata;
	}

	@Override
	protected byte[] process(ClassReader cr) throws IOException {
		if (!metadata.containsConstExpr())
			return null;

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

		ClassVisitor cv = cw;
		cv = new ConstExprFieldWeaver(metadata, cv);
		cv = new ConstExprMethodWeaver(metadata, cv);

		cr.accept(cv, ClassReader.EXPAND_FRAMES);

		byte[] bytes = getBytes(cw);
		if (metadata.path != null) // pretty null-y when testing
			ClassUtil.writeClass(bytes, metadata.path);

		return bytes;
	}

	private byte[] getBytes(ClassWriter cw) {
		if (metadata.emptyClinit) {
			//only the shell remains - remove it all
			return withRemovedClinit(cw.toByteArray());
		} else {
			return cw.toByteArray();
		}
	}

	private byte[] withRemovedClinit(byte[] klazz) {
		ClassReader cr = AsmUtil.classReader(klazz);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		ClassVisitor cv = new ClinitRemoverWeaver(cw);

		cr.accept(cv, ClassReader.EXPAND_FRAMES);
		return cw.toByteArray();
	}
}
