package com.github.yanglifan.navi.core.policy;

import com.github.yanglifan.navi.core.BaseTest;
import com.github.yanglifan.navi.core.Handler;
import com.github.yanglifan.navi.core.matcher.EqualMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultRejectPolicyTest extends BaseTest {

	@Rule
	public final SystemErrRule systemErrRule = new SystemErrRule().enableLog();

	@Test
	public void reject_by_no_such_method() {
		// Given
		Object request = new Object();
		registerHandler(new FailHandler());

		// When
		selector.select(request, Handler.class);

		// Then
		assertThat(systemErrRule.getLog())
				.contains("FailHandler was rejected by the following exception:");
	}

	@EqualMatcher(propertyPath = "name", expectValue = "duncan")
	private class FailHandler implements Handler {

	}
}