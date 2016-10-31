package net.onedaybeard.constexpr;


import net.onedaybeard.constexpr.model.MinimalStaticInitializer;
import net.onedaybeard.constexpr.model.NoStaticInitializer;
import org.junit.Test;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ClinitAssertionTest {

	@Test
	public void test_basic_cinit() throws Exception {
		MethodNode clinit = TestUtil.findMethod(MinimalStaticInitializer.class, "<clinit>", "()V");
		List<AbstractInsnNode> nodes = TestUtil.filterBodyNoDebug(clinit);

		// 2 == INVOKESTATIC + RETURN
		assertEquals(TestUtil.toString(clinit), 2, nodes.size());
	}

	@Test
	public void test_no_cinit() throws Exception {
		assertNull(TestUtil.findMethod(NoStaticInitializer.class, "<clinit>", "()V"));
	}
}
