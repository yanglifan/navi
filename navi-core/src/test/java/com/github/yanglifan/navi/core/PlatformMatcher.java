package com.github.yanglifan.navi.core;

import com.github.yanglifan.navi.core.alias.AliasFor;
import com.github.yanglifan.navi.core.matcher.EqualMatcher;

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
	@AliasFor(annotationFor = EqualMatcher.class, attributeFor = "expectValue")
	String platform();
}
