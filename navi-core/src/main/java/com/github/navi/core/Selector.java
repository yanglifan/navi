package com.github.navi.core;

public interface Selector {
    <T> T select(Object request, Class<T> targetClass);
}
