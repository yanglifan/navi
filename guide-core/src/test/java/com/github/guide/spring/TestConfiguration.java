package com.github.guide.spring;

import com.github.guide.Handler;
import com.github.guide.core.EqualsMatcher;
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

    @EqualsMatcher(propertyPath = "username", expectValue = "foo")
    @Component
    static class FooHandler implements Handler {
    }

    @EqualsMatcher(propertyPath = "username", expectValue = "bar")
    @Component
    static class BarHandler implements Handler {
    }
}
