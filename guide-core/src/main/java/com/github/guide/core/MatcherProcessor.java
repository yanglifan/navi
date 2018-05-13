package com.github.guide.core;

import java.lang.annotation.Annotation;

/**
 * @author Yang Lifan
 */
public interface MatcherProcessor<A extends Annotation> {
    MatchResult process(Object request, A indicatorAnnotation);
}
