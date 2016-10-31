package net.onedaybeard.constexpr.model;

import net.onedaybeard.constexpr.ConstExpr;

import java.util.Random;

public class AlmostPlainPrimitive {
	public static final long another = 14;

	public static final Object OBJECT = new Object();

	@ConstExpr public static final long timestamp = System.currentTimeMillis();
	@ConstExpr public static final int seed = generateSeed();

	private static final int[] ints = new int[] {0, 3, 5};

	@ConstExpr
	private static int generateSeed() {
		String s = "hellooooo";
		int sum = 0;
		for (char c : s.toCharArray())
			sum += c;
		return new Random(sum).nextInt();
	}
}
