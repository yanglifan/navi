package com.github.navi.core;

import com.github.navi.core.matcher.EqualMatcher;
import com.github.navi.core.matcher.VersionMatcher;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This tests will not run with Maven.
 *
 * @author Yang Lifan
 */
public class PerfTests {

	private static final int TEST_COUNT = 100000;

	@Test
	public void test_version_matcher() {
		SimpleSelector simpleSelector = new SimpleSelector();
		Set<TestHandler> handlers = new HashSet<>();
		handlers.add(new V1TestHandler());
		simpleSelector.registerCandidates(TestHandler.class, handlers);

		Map<String, String> req = new HashMap<>();
		req.put("version", "1.0.0");
		simpleSelector.select(req, TestHandler.class);

		String baseVersion = "1.0.";
		long start = System.nanoTime();
		for (int i = 0; i < TEST_COUNT; i++) {
			req.put("version", baseVersion + i);
			simpleSelector.select(req, TestHandler.class);
		}

		long total = System.nanoTime() - start;
		System.out.println("Per Version Match cost: " + total / TEST_COUNT + "ns");
	}

	@Test
	public void test_equals_matcher() {
		SimpleSelector simpleSelector = new SimpleSelector();
		Set<TestHandler> handlers = new HashSet<>();
		handlers.add(new HelloTestHandler());
		simpleSelector.registerCandidates(TestHandler.class, handlers);

		Map<String, String> req = new HashMap<>();
		req.put("text", "hello");
		simpleSelector.select(req, TestHandler.class);

		long start = System.nanoTime();
		for (int i = 0; i < TEST_COUNT; i++) {
			simpleSelector.select(req, TestHandler.class);
		}

		long total = System.nanoTime() - start;
		System.out.println("Per Equals Match cost: " + total / TEST_COUNT + "ns");
	}

	interface TestHandler {
	}

	@VersionMatcher(versionRange = "[1.0.0,1.1.0)")
	private class V1TestHandler implements TestHandler {

	}

	@EqualMatcher(propertyPath = "text", expectValue = "hello")
	private class HelloTestHandler implements TestHandler {

	}
}

