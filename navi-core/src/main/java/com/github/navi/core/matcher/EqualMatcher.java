package com.github.navi.core.matcher;

import com.github.navi.core.MatchResult;
import com.github.navi.core.MatcherType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@MatcherType(processor = EqualMatcher.Processor.class)
public @interface EqualMatcher {
	String propertyPath();

	String[] expectValue() default "";

	class Processor extends OnePropertyMatcherProcessor<EqualMatcher> {

		@Override
		protected MatchResult doProcess(Object request, EqualMatcher matcher,
				String[] expectValues) {
			List<String> expectValueList = Arrays.asList(expectValues);
			boolean isContains = expectValueList.contains(request.toString());
			return isContains ? MatchResult.ACCEPT : MatchResult.REJECT;
		}

		@Override
		protected String getPropertyPath(EqualMatcher matcherAnnotation) {
			return matcherAnnotation.propertyPath();
		}

		@Override
		protected String[] getMatcherValue(EqualMatcher matcher) {
			return matcher.expectValue();
		}

		@Override
		protected String aliasName() {
			return "expectValue";
		}
	}
}
