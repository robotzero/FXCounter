package com.king.counter.cache;

interface ServiceLocator {
    Object get(Class clazz, Object reference);
}
