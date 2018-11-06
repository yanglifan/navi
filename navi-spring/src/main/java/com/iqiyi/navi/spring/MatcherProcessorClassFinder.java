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

import com.iqiyi.navi.core.MatcherType;
import com.iqiyi.navi.core.matcher.EqualMatcher;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Yang Lifan
 */
public class MatcherProcessorClassFinder {

	public static List<Class> findAll() {
		ClassPath classPath = buildClassPath();
		String matcherPackageName = getMatcherPackageName();
		Collection<ClassPath.ClassInfo> classInfoSet = findAllTopLevelClasses(classPath, matcherPackageName);

		List<Class> classList = new ArrayList<>();
		for (ClassPath.ClassInfo classInfo : classInfoSet) {
			Class<?> clazz = classInfo.load();
			MatcherType matcherType = clazz.getAnnotation(MatcherType.class);
			if (clazz.isAnnotation() && matcherType != null) {
				Class matcherProcessorClass = matcherType.processor();
				classList.add(matcherProcessorClass);
			}
		}

		return classList;
	}

	private static ImmutableSet<ClassPath.ClassInfo> findAllTopLevelClasses(ClassPath classPath, String matcherPackageName) {
		return classPath.getTopLevelClassesRecursive(matcherPackageName);
	}

	private static String getMatcherPackageName() {
		return EqualMatcher.class.getPackage().getName();
	}

	private static ClassPath buildClassPath() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		ClassPath classPath;
		try {
			classPath = ClassPath.from(loader);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return classPath;
	}
}
