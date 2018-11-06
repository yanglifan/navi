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

package com.iqiyi.navi.core.benchmark;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Yang Lifan
 */
abstract class BaseBenchmark {
	int testCount = 1000000;
	private int threadNumber = Runtime.getRuntime().availableProcessors();

	private ExecutorService executor = Executors.newFixedThreadPool(threadNumber);

	BaseBenchmark() {
	}

	long doBenchmark(Supplier<Map<String, String>> requestSupplier,
			Consumer<Map<String, String>> benchmarkExecutor) throws InterruptedException {
		Map<String, String> req = requestSupplier.get();

		// Warm up
		for (int i = 0; i < 100; i++) {
			benchmarkExecutor.accept(req);
		}

		CountDownLatch countDownLatch = new CountDownLatch(testCount);
		AtomicLong totalCost = new AtomicLong();
		for (int i = 0; i < testCount; i++) {
			executor.execute(() -> {
				long start = System.nanoTime();
				benchmarkExecutor.accept(req);
				long cost = System.nanoTime() - start;
				totalCost.addAndGet(cost);
				countDownLatch.countDown();
			});
		}

		countDownLatch.await();

		return totalCost.get() / testCount;
	}
}
