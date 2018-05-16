package com.github.navi.spring;

import com.github.navi.core.matcher.EqualsMatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Yang Lifan
 */
@Configuration
public class MatcherConfiguration {
	@Bean
	public EqualsMatcher.Processor equalsMatcherProcessor() {
		return new EqualsMatcher.Processor();
	}
}
