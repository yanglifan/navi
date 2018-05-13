package com.github.guide.core;

import com.github.guide.core.processor.EqualsMatcherProcessor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@MatcherType(processor = EqualsMatcherProcessor.class)
public @interface EqualsMatcher {
    String propertyPath();

    String expectValue();
}
