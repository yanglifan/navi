package com.github.yanglifan.navi.core.matcher;

import com.github.yanglifan.navi.core.MatchResult;
import com.github.yanglifan.navi.core.MatcherType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Repeatable(EqualMatchers.class)
@MatcherType(processor = EqualMatcher.Processor.class)
public @interface EqualMatcher {
	String property();

	String[] value() default "";

	String aliasLabel() default "";

	class Processor extends OnePropertyMatcherProcessor<EqualMatcher> {

		@Override
		protected MatchResult doProcess(Object request, EqualMatcher matcher,
				String[] expectValues) {
			List<String> expectValueList = Arrays.asList(expectValues);
			boolean isContains = expectValueList.contains(request.toString());
			return isContains ? MatchResult.accept() : MatchResult.reject();
		}

		@Override
		protected String getPropertyPath(EqualMatcher matcherAnnotation) {
			return matcherAnnotation.property();
		}

		@Override
		protected String[] getMatcherValue(EqualMatcher matcher) {
			return matcher.value();
		}

		@Override
		protected String aliasName() {
			return "value";
		}
	}
}
