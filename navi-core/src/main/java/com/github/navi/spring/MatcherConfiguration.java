package com.github.navi.spring;

import com.github.navi.core.processor.EqualsMatcherProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Yang Lifan
 */
@Configuration
public class MatcherConfiguration {
    @Bean
    public EqualsMatcherProcessor equalsMatcherProcessor() {
        return new EqualsMatcherProcessor();
    }
}
