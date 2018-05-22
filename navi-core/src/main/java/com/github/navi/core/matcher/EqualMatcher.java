package com.github.navi.core.matcher;

import com.github.navi.core.MatchResult;
import com.github.navi.core.MatcherDescription;
import com.github.navi.core.MatcherType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
		protected String getPropertyPath(EqualMatcher matcherAnnotation) {
			return matcherAnnotation.propertyPath();
		}

		@Override
		protected MatchResult doProcess(Object request,
				MatcherDescription<EqualMatcher> matcherDescription) {
			String[] expectValues = getAliasedAttribute(matcherDescription);
			List<String> expectValueList = Arrays.asList(expectValues);
			boolean isContains = expectValueList.contains(request.toString());
			return isContains ? MatchResult.ACCEPT : MatchResult.REJECT;
		}

		private String[] getAliasedAttribute(MatcherDescription<EqualMatcher> matcherDescription) {
			Map<String, String> aliasedAttributes = matcherDescription.getAliasedAttributes();
			if (aliasedAttributes == null || aliasedAttributes.isEmpty()) {
				return matcherDescription.getMatcher().expectValue();
			} else {
				String value = matcherDescription.getAliasedAttributes().get("expectValue");
				return new String[]{value};
			}
		}
	}
}
