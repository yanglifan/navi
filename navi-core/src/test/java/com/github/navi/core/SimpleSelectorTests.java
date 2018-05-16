package com.github.navi.core;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleSelectorTests {
	@Test
	public void noWinnerSinceNoMatcherAnnotation() {
		// Given
		SimpleSelector simpleSelector = new SimpleSelector();
		Set<MyHandler> handlers = new HashSet<>();
		handlers.add(new NoAnno1MyHandler());
		handlers.add(new NoAnno2MyHandler());
		simpleSelector.registerCandidates(MyHandler.class, handlers);

		// When
		MyHandler winner = simpleSelector.select(new Object(), MyHandler.class);

		// Then
		assertThat(winner).isNull();
	}

	interface MyHandler {

	}

	private class NoAnno1MyHandler implements MyHandler {

	}

	private class NoAnno2MyHandler implements MyHandler {

	}

}