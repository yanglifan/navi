package com.github.navi.core;

import com.github.navi.core.matcher.EqualMatcher;
import com.github.navi.core.matcher.VersionMatcher;
import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yang Lifan
 */
public class AliasTests {
	private SimpleSelector selector;

	@Before
	public void setUp() {
		selector = new SimpleSelector();
	}

	@Test
	public void equal_and_version() {
		// Given
		selector.registerCandidate(Handler.class, new EqualVersionAliasHandler());
		Map<String, String> correctReq = new HashMap<>();
		correctReq.put("name", "hulk");
		correctReq.put("clientVersion", "1.5.0");

		Map<String, String> wrongReq = new HashMap<>();
		wrongReq.put("name", "stark");
		wrongReq.put("clientVersion", "1.5.0");


		// When
		Handler correctHandler = selector.select(correctReq, Handler.class);
		Handler wrongHandler = selector.select(wrongReq, Handler.class);

		// Then
		assertThat(correctHandler).isInstanceOf(EqualVersionAliasHandler.class);
		assertThat(wrongHandler).isNull();
	}

	@SuppressWarnings("unused")
	@Retention(RetentionPolicy.RUNTIME)
	@EqualMatcher(propertyPath = "name")
	@VersionMatcher(propertyPath = "clientVersion")
	@CompositeMatcherType
	@interface EqualVersionAlias {
		@AliasAttribute(annotationFor = EqualMatcher.class, attributeFor = "expectValue")
		String name();

		@AliasAttribute(annotationFor = VersionMatcher.class, attributeFor = "versionRange")
		String clientVersionRange();
	}

	@EqualVersionAlias(name = "hulk", clientVersionRange = "[1.0.0,2.0.0)")
	private class EqualVersionAliasHandler implements Handler {
	}
}
