package net.onedaybeard.constexpr.model;

import javafx.geometry.Rectangle2D;
import net.onedaybeard.constexpr.ConstExpr;

public class WrongFieldTypeB {
	@ConstExpr
	public static final Rectangle2D iDie = new Rectangle2D(0, 0, 0, 0);
}
