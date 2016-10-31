package net.onedaybeard.constexpr.visitor;

import net.onedaybeard.constexpr.InvalidConstExprException;
import net.onedaybeard.constexpr.TestUtil;
import net.onedaybeard.constexpr.model.*;
import net.onedaybeard.constexpr.inspect.ClassMetadata;
import net.onedaybeard.constexpr.inspect.FieldDescriptor;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static net.onedaybeard.constexpr.TestUtil.scan;
import static org.junit.Assert.*;

public class ConstExprScannerTest {
	@Test
	public void test_scan_simple() throws Exception {
		ClassMetadata scan = scan(PlainPrimitive.class);
		assertEquals(2, scan.fields.size());

		List<FieldDescriptor> constExprFields = scan.fields.stream()
			.filter(fd -> fd.isConstExpr)
			.collect(Collectors.toList());
		assertEquals(2, constExprFields.size());
		assertEquals("timestamp", constExprFields.get(0).name);
		assertEquals(1, scan.methods.stream()
			.filter(md -> md.isConstExpr)
			.count());
	}

	@Test
	public void test_scan_string() throws Exception {
		ClassMetadata scan = scan(PlainString.class);

		List<FieldDescriptor> fields = scan.fields;
		assertEquals(2, fields.size());

		assertEquals(2, fields.size());
		assertEquals("s1", fields.get(0).name);
	}

	@Test(expected = InvalidConstExprException.class)
	public void test_fail_scan_annotated_non_static_field() throws Exception {
		scan(WrongFieldTypeA.class);
	}

	@Test(expected = InvalidConstExprException.class)
	public void test_fail_scan_object_field() throws Exception {
		scan(WrongFieldTypeB.class);
	}

	@Test(expected = InvalidConstExprException.class)
	public void test_fail_scan_instance_method() throws Exception {
		scan(WrongMethodTypeA.class);
	}
}