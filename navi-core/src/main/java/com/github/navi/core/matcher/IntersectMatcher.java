package com.github.navi.core.matcher;

import com.github.navi.core.MatchResult;
import com.github.navi.core.MatcherDescription;
import com.github.navi.core.MatcherType;
import org.apache.commons.collections.CollectionUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@MatcherType(processor = IntersectMatcher.Processor.class)
public @interface IntersectMatcher {
	String propertyPath();

	String[] expectValue();

	String separator() default ",";

	class Processor extends OnePropertyMatcherProcessor<IntersectMatcher> {

		@Override
		protected String getPropertyPath(IntersectMatcher matcherAnnotation) {
			return matcherAnnotation.propertyPath();
		}

		@Override
		protected MatchResult doProcess(Object property,
				MatcherDescription<IntersectMatcher > matcherDescription) {
			IntersectMatcher matcherAnnotation = matcherDescription.getMatcher();

			List<String> expectValueList = Arrays.asList(matcherAnnotation.expectValue());

			boolean isIntersected;
			if (property instanceof String) {
				isIntersected = isIntersectOnString(property, expectValueList, matcherAnnotation.separator());
			} else if (isStringCollection(property)) {
				isIntersected = isIntersectOnCollection(property, expectValueList);
			} else {
				isIntersected = false;
			}

			return isIntersected ? MatchResult.ACCEPT : MatchResult.REJECT;
		}

		private boolean isStringCollection(Object property) {
			if (!(property instanceof Collection)) {
				return false;
			}

			Collection collection = (Collection) property;
			for (Object o : collection) {
				if (!String.class.isInstance(o)) {
					return false;
				}
			}

			return true;
		}

		private boolean isIntersectOnString(Object property, List<String> expectValueList, String separator) {
			if (!(property instanceof String)) {
				return false;
			}

			String propertyValue = (String) property;
			List<String> propertyValues = Arrays.asList(propertyValue.split(separator));
			return isIntersectOnCollection(propertyValues, expectValueList);
		}

		@SuppressWarnings("unchecked")
		private boolean isIntersectOnCollection(Object property, List<String> expectValueList) {
			Collection<String> stringCollection = (Collection<String>) property;
			stringCollection = trimStringCollection(stringCollection);
			Collection intersectResult = CollectionUtils.intersection(expectValueList, stringCollection);
			return !intersectResult.isEmpty();
		}

		private Collection<String> trimStringCollection(Collection<String> property) {
			Collection<String> collection = new ArrayList<>();
			for (String v : property) {
				collection.add(v.trim());
			}
			return collection;
		}
	}
}
