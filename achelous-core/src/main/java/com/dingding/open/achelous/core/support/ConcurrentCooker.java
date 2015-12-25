package com.dingding.open.achelous.core.support;

import rx.functions.Function;

public interface ConcurrentCooker<T1, T2, T3, R> extends Function {
	public R call(T1 t1, T2 t2, T3 t3);
}