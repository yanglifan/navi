package com.github.navi.core.strategy;

import com.github.navi.core.BaseTest;
import com.github.navi.core.Handler;
import com.github.navi.core.exception.MatchRejectException;
import com.github.navi.core.matcher.EqualMatcher;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FastFailRejectStrategyTest extends BaseTest {
	@SuppressWarnings("all")
	@Test
	public void reject_by_no_such_method() {
		// Given
		Object request = new Object();
		registerHandler(new FailHandler());
		selector.setRejectStrategy(new FastFailRejectStrategy());

		// When
		Exception ex = null;
		try {
			selector.select(request, Handler.class);
		} catch (Exception e) {
			ex = e;
		}

		// Then
		assertThat(ex).isInstanceOf(MatchRejectException.class);
		assertThat(ex.getCause()).isInstanceOf(NoSuchMethodException.class);
	}

	@EqualMatcher(propertyPath = "name", expectValue = "duncan")
	private class FailHandler implements Handler {
	}
}