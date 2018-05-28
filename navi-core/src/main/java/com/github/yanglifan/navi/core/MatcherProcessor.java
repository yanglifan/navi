package com.github.yanglifan.navi.core;

import java.lang.annotation.Annotation;

/**
 * @author Yang Lifan
 */
public interface MatcherProcessor<A extends Annotation> {
	MatchResult process(Object request, MatcherDefinition<A> matcherDefinition);
}
