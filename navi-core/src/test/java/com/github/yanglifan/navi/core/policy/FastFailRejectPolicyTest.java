package com.github.yanglifan.navi.core.policy;

import com.github.yanglifan.navi.core.BaseTest;
import com.github.yanglifan.navi.core.Handler;
import com.github.yanglifan.navi.core.exception.MatchRejectException;
import com.github.yanglifan.navi.core.matcher.EqualMatcher;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FastFailRejectPolicyTest extends BaseTest {
	@SuppressWarnings("all")
	@Test
	public void reject_by_no_such_method() {
		// Given
		Object request = new Object();
		registerHandler(new FailHandler());
		selector.setRejectPolicy(new FastFailRejectPolicy());

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

	@EqualMatcher(property = "name", value = "duncan")
	private class FailHandler implements Handler {
	}
}