/*
 * Copyright 2018 Yang Lifan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iqiyi.navi.spring;

import com.iqiyi.navi.core.Selector;
import com.iqiyi.navi.core.matcher.EqualMatcher;
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
