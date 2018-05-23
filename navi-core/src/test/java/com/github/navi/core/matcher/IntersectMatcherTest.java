package com.github.navi.core.matcher;

import com.github.navi.core.AliasAttribute;
import com.github.navi.core.CompositeMatcherType;
import com.github.navi.core.Handler;
import com.github.navi.core.SimpleSelector;
import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class IntersectMatcherTest {
	private SimpleSelector selector;

	@Before
	public void setUp() {
		selector = new SimpleSelector();
	}

	@Test
	public void intersect_string() {
		// Given
		Map<String, String> request = new HashMap<>();
		request.put("name", "stark, thor");

		selector.registerCandidate(Handler.class, new IntersectHandler());

		// When
		Handler handler = selector.select(request, Handler.class);

		// Then
		assertThat(handler).isInstanceOf(IntersectHandler.class);
	}

	@Test
	public void intersect_collection() {
		// Given
		Map<String, List<String>> request = new HashMap<>();
		request.put("name", Arrays.asList("stark", "thor"));

		selector.registerCandidate(Handler.class, new IntersectHandler());

		// When
		Handler handler = selector.select(request, Handler.class);

		// Then
		assertThat(handler).isInstanceOf(IntersectHandler.class);
	}

	@Test
	public void alias_test() {
		// Given
		Map<String, List<String>> request = new HashMap<>();
		request.put("name", Arrays.asList("stark", "rogers"));

		selector.registerCandidate(Handler.class, new AliasIntersectHandler());

		// When
		Handler handler = selector.select(request, Handler.class);

		// Then
		assertThat(handler).isInstanceOf(AliasIntersectHandler.class);
	}

	@SuppressWarnings("unused")
	@Retention(RetentionPolicy.RUNTIME)
	@IntersectMatcher(propertyPath = "name")
	@CompositeMatcherType
	public @interface AliasIntersectMatcher {
		@AliasAttribute(annotationFor = IntersectMatcher.class, attributeFor = "expectValue")
		String[] names();
	}

	@IntersectMatcher(propertyPath = "name", expectValue = {"stark", "rogers"})
	private class IntersectHandler implements Handler {

	}

	@AliasIntersectMatcher(names = {"stark", "thor"})
	private class AliasIntersectHandler implements Handler {

	}
}