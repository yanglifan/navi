package com.github.navi.core.matcher;

import com.github.navi.core.MatchResult;
import com.github.navi.core.MatcherType;
import com.github.navi.core.OnePropertyMatcherProcessor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@MatcherType(processor = EqualsMatcher.Processor.class)
public @interface EqualsMatcher {
	String propertyPath();

	String expectValue();

	class Processor extends OnePropertyMatcherProcessor<EqualsMatcher> {

		@Override
		protected String getPropertyPath(EqualsMatcher matcherAnnotation) {
			return matcherAnnotation.propertyPath();
		}

		@Override
		protected MatchResult doProcess(Object request, EqualsMatcher matcherAnnotation) {
			boolean isEquals = matcherAnnotation.expectValue().equals(request.toString());
			return isEquals ? MatchResult.ACCEPT : MatchResult.REJECT;
		}
	}
}
