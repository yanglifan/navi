package com.github.navi.core;

import com.github.navi.core.alias.AliasFor;
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
public class AliasTest {
	private SimpleSelector selector;

	@Before
	public void setUp() {
		selector = new SimpleSelector();
	}

	@Test
	public void alias_all() {
		// Given
		selector.registerCandidate(Handler.class, new AliasAllHandler());
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
		assertThat(correctHandler).isInstanceOf(AliasAllHandler.class);
		assertThat(wrongHandler).isNull();
	}

	@Test
	public void alias_part() {
		// Given
		selector.registerCandidate(Handler.class, new AliasPartHandler());
		Map<String, String> correctReq = new HashMap<>();
		correctReq.put("name", "hulk");
		correctReq.put("clientVersion", "0.1.0");

		Map<String, String> wrongReq = new HashMap<>();
		wrongReq.put("name", "stark");
		wrongReq.put("clientVersion", "0.1.0");

		// When
		Handler correctHandler = selector.select(correctReq, Handler.class);
		Handler wrongHandler = selector.select(wrongReq, Handler.class);

		// Then
		assertThat(correctHandler).isInstanceOf(AliasPartHandler.class);
		assertThat(wrongHandler).isNull();
	}

	@Test
	public void labeled_alias() {
		// Given
		Map<String, String> request = new HashMap<>();
		request.put("name", "stark");
		request.put("department", "avengers");

		selector.registerCandidate(Handler.class, new LabeledAliasHandler());

		// When
		Handler handler = selector.select(request, Handler.class);

		// Then
		assertThat(handler).isInstanceOf(LabeledAliasHandler.class);
	}

	@SuppressWarnings("unused")
	@Retention(RetentionPolicy.RUNTIME)
	@EqualMatcher(propertyPath = "name")
	@VersionMatcher(propertyPath = "clientVersion")
	@CompositeMatcherType
	@interface AliasAll {
		@AliasFor(annotationFor = EqualMatcher.class, attributeFor = "expectValue")
		String name();

		@AliasFor(annotationFor = VersionMatcher.class, attributeFor = "versionRange")
		String clientVersionRange();
	}

	@SuppressWarnings("unused")
	@Retention(RetentionPolicy.RUNTIME)
	@EqualMatcher(propertyPath = "name", expectValue = "hulk")
	@VersionMatcher(propertyPath = "clientVersion")
	@CompositeMatcherType
	@interface AliasPart {
		@AliasFor(annotationFor = VersionMatcher.class, attributeFor = "versionRange")
		String clientVersionRange();
	}

	@SuppressWarnings("unused")
	@Retention(RetentionPolicy.RUNTIME)
	@EqualMatcher(propertyPath = "name", aliasLabel = "name")
	@EqualMatcher(propertyPath = "department", aliasLabel = "department")
	@CompositeMatcherType
	@interface LabeledAlias {
		@AliasFor(annotationFor = EqualMatcher.class, attributeFor = "expectValue", label = "name")
		String name();

		@AliasFor(annotationFor = EqualMatcher.class, attributeFor = "expectValue", label = "department")
		String department();
	}

	@AliasAll(name = "hulk", clientVersionRange = "[1.0.0,2.0.0)")
	private class AliasAllHandler implements Handler {
	}

	@AliasPart(clientVersionRange = "[0.1.0,0.2.0)")
	private class AliasPartHandler implements Handler {
	}

	@LabeledAlias(name = "stark", department = "avengers")
	private class LabeledAliasHandler implements Handler {
	}

}
