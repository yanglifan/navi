package com.github.yanglifan.navi.spring;

import com.github.yanglifan.navi.core.Selector;
import com.github.yanglifan.navi.core.matcher.EqualMatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author Yang Lifan
 */
@Configuration
public class TestConfiguration {
	@Bean
	public Selector selector() {
		return new SpringBasedSelector();
	}

	@SuppressWarnings("all")
	@EqualMatcher(property = "username", value = "foo")
	@Component
	static class FooHandler implements Handler {
	}

	@EqualMatcher(property = "username", value = "bar")
	@Component
	static class BarHandler implements Handler {
	}
}
