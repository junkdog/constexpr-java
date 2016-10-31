package net.onedaybeard.constexpr.model;

import net.onedaybeard.constexpr.ConstExpr;

public class WrongFieldTypeA {
	@ConstExpr
	public final long hmm = System.currentTimeMillis();
}
