package com.github.guide.spring;

import com.github.guide.Handler;
import com.github.guide.core.Selector;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class SpringBasedSelectorTests {
    private Selector selector;

    @Before
    public void setUp() {
        AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext();
        applicationContext.register(TestConfiguration.class);
        applicationContext.refresh();
        selector = applicationContext.getBean(SpringBasedSelector.class);
    }

    @Test
    public void simple() {
        MyRequest request = new MyRequest();
        request.username = "bar";
        Handler handler = selector.select(request, Handler.class);
        assertThat(handler).isInstanceOf(TestConfiguration.BarHandler.class);
    }


    public class MyRequest {
        private String username;

        public String getUsername() {
            return username;
        }
    }
}