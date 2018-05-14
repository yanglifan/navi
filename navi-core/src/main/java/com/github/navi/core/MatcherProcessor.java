package com.github.navi.core;

import java.lang.annotation.Annotation;

/**
 * @author Yang Lifan
 */
public interface MatcherProcessor<A extends Annotation> {
   <T> MatchResult<T> process(Object request, A indicatorAnnotation);
}
