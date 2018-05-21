package com.github.navi.core;

import com.github.navi.core.matcher.EqualMatcher;
import com.github.navi.core.matcher.VersionMatcher;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleSelectorTests {
	@Test
	public void no_winner_when_no_matcher_annotation() {
		// Given
		SimpleSelector simpleSelector = new SimpleSelector();
		Set<Handler> handlers = new HashSet<>();
		handlers.add(new NoAnno1Handler());
		handlers.add(new NoAnno2Handler());
		simpleSelector.registerCandidates(Handler.class, handlers);

		// When
		Handler winner = simpleSelector.select(new Object(), Handler.class);

		// Then
		assertThat(winner).isNull();
	}

	@Test
	public void no_winner_when_all_reject() {
		// Given
		SimpleSelector simpleSelector = new SimpleSelector();
		Set<Handler> handlers = new HashSet<>();
		handlers.add(new StarkHandler());
		simpleSelector.registerCandidates(Handler.class, handlers);

		Map<String, String> req = new HashMap<>();
		req.put("username", "thor");
		req.put("version", "1.0.0");

		// When
		Handler winner = simpleSelector.select(req, Handler.class);

		// Then
		assertThat(winner).isNull();
	}

	interface Handler {

	}

	private class NoAnno1Handler implements Handler {

	}

	private class NoAnno2Handler implements Handler {

	}

	@EqualMatcher(propertyPath = "username", expectValue = "stark")
	@VersionMatcher(versionRange = "[1.0.0,2.0.0)")
	private class StarkHandler implements Handler {

	}

}