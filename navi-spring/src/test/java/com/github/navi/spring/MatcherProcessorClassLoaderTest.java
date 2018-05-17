package com.github.navi.spring;

import com.github.navi.core.matcher.EqualsMatcher;
import com.github.navi.core.matcher.VersionMatcher;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MatcherProcessorClassLoaderTest {
	@Test
	public void find_all_matcher_processor_classes() {
		List<Class> classes = MatcherProcessorClassFinder.findAll();
		System.out.println(classes.get(0).getPackage().getName());
		assertThat(classes).contains(EqualsMatcher.Processor.class);
		assertThat(classes).contains(VersionMatcher.Processor.class);
	}
}