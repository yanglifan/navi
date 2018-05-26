package com.github.navi.core;

import com.github.navi.core.Handler;
import com.github.navi.core.SimpleSelector;
import org.junit.Before;

/**
 * @author Yang Lifan
 */
public abstract class BaseTest {
	protected SimpleSelector selector;

	@Before
	public void setUp() {
		selector = new SimpleSelector();
	}

	protected void registerHandler(Handler handler) {
		selector.registerCandidate(Handler.class, handler);
	}
}
