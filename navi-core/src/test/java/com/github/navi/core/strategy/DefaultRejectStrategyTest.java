package com.github.navi.core.strategy;

import com.github.navi.core.BaseTest;
import com.github.navi.core.Handler;
import com.github.navi.core.matcher.EqualMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultRejectStrategyTest extends BaseTest {

	@Rule
	public final SystemErrRule systemErrRule = new SystemErrRule().enableLog();

	@Test
	public void reject_by_no_such_method() {
		// Given
		Object request = new Object();
		registerHandler(new AHandler());

		// When
		selector.select(request, Handler.class);

		// Then
		assertThat(systemErrRule.getLog())
				.contains("AHandler was rejected by the following exception:");
	}

	@EqualMatcher(propertyPath = "name", expectValue = "duncan")
	private class AHandler implements Handler {

	}
}