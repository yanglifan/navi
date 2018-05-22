package com.github.navi.core;

import com.github.navi.core.matcher.EqualMatcher;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Yang Lifan
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EqualMatcher(propertyPath = "platform")
@CompositeMatcherType
public @interface PlatformMatcher {
	@AliasProp(annotation = EqualMatcher.class, value = "expectValue")
	String platform();
}
