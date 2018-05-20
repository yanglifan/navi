package com.github.navi.core.matcher;

import com.github.navi.core.MatchResult;
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
@MatcherType(processor = IntersectionMatcher.Processor.class)
public @interface IntersectionMatcher {
	String propertyPath();

	String[] expectValue();

	String separator() default ",";

	class Processor extends OnePropertyMatcherProcessor<IntersectionMatcher> {

		@Override
		protected String getPropertyPath(IntersectionMatcher matcherAnnotation) {
			return matcherAnnotation.propertyPath();
		}

		@Override
		protected MatchResult doProcess(Object property, IntersectionMatcher matcherAnnotation) {
			List<String> expectValueList = Arrays.asList(matcherAnnotation.expectValue());

			boolean isIntersectedOnCollection = isIntersectOnCollection(property, expectValueList);
			boolean isIntersectionOnString = isIntersectOnString(property, expectValueList,
					matcherAnnotation.separator());
			boolean isIntersected = isIntersectedOnCollection || isIntersectionOnString;
			return isIntersected ? MatchResult.ACCEPT : MatchResult.REJECT;
		}

		private boolean isIntersectOnString(Object property, List<String> expectValueList, String separator) {
			if (!(property instanceof String)) {
				return false;
			}

			String propertyValue = (String) property;
			List<String> propertyValues = Arrays.asList(propertyValue.split(separator));
			return isIntersectOnCollection(propertyValues, expectValueList);
		}

		// TODO Check generic type
		@SuppressWarnings("unchecked")
		private boolean isIntersectOnCollection(Object property, List<String> expectValueList) {
			try {
				if (property instanceof Collection) {
					Collection<String> collection = new ArrayList<>();
					for (String v : (Collection<String>) property) {
						collection.add(v.trim());
					}

					Collection intersectResult = CollectionUtils.intersection(expectValueList, collection);
					return !intersectResult.isEmpty();
				} else {
					return false;
				}
			} catch (Exception e) {
				// TODO Log
				return false;
			}
		}
	}
}
