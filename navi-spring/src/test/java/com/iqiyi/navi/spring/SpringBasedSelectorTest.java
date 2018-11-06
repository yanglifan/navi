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
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class SpringBasedSelectorTest {
	private Selector selector;

	@Before
	public void setUp() {
		AnnotationConfigApplicationContext applicationContext =
				new AnnotationConfigApplicationContext();
		applicationContext.scan(
				SpringBasedSelector.class.getPackage().getName(),
				AndroidV900CouponHandler.class.getPackage().getName()
		);
		applicationContext.register(MatcherProcessorAutoRegister.class);
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

	@Test
	public void complex() {
		// Given
		CouponRequest v9010AndroidRequest = new CouponRequest();
		v9010AndroidRequest.setPlatform("android");
		v9010AndroidRequest.setClientVersion("9.0.10");

		CouponRequest v920AndroidRequest = new CouponRequest();
		v920AndroidRequest.setPlatform("android");
		v920AndroidRequest.setClientVersion("9.2.0");

		// When
		CouponHandler androidV900CouponHandler = selector.select(v9010AndroidRequest, CouponHandler.class);
		CouponHandler androidV910CouponHandler = selector.select(v920AndroidRequest, CouponHandler.class);

		// Then
		assertThat(androidV900CouponHandler).isInstanceOf(AndroidV900CouponHandler.class);
		assertThat(androidV910CouponHandler).isInstanceOf(AndroidV910CouponHandler.class);
	}

	public class MyRequest {
		private String username;

		@SuppressWarnings("all")
		public String getUsername() {
			return username;
		}
	}
}