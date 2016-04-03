package com.queen.counter.cache;

interface ServiceLocator {
    Object get(Class clazz, Object reference);
}
