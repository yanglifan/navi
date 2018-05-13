package com.github.guide.spring;

import com.github.guide.Handler;
import com.github.guide.core.BasicMatcher;
import com.github.guide.core.Selector;
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

    @BasicMatcher(paramPath = "username", targetValue = "foo")
    @Component
    public static class FooHandler implements Handler {
    }

    @BasicMatcher(paramPath = "username", targetValue = "bar")
    @Component
    public static class BarHandler implements Handler {
    }
}
