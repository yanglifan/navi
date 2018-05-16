package com.github.navi.core;

import java.lang.annotation.*;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MatcherType {
	Class<? extends MatcherProcessor> processor();
}
