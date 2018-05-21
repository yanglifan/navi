package com.github.navi.spring;

import com.github.navi.core.MatcherType;
import com.github.navi.core.matcher.EqualMatcher;
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
