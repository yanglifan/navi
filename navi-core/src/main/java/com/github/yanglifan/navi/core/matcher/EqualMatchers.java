package com.github.yanglifan.navi.core.matcher;

import com.github.yanglifan.navi.core.MatcherContainer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Yang Lifan
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@MatcherContainer
public @interface EqualMatchers {
	EqualMatcher[] value();
}
