package net.onedaybeard.constexpr;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * <p>Simulating <strong>constexpr</strong> from c++11, with some slight
 * modifications for methods. This annotation works
 * on static fields and static methods:</p>
 *
 * <ul>
 *     <li>Fields are resolved at build-time, removing any runtime execution.</li>
 *     <li>Methods are removed at build-time. This way, methods can be exclusive
 *         to the build-time environment.</li>
 * </ul>
 *
 * <p>This feature only works with primitive and string type fields.</p>
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({FIELD, METHOD})
public @interface ConstExpr {}
