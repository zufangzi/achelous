package com.dingding.open.achelous.core.invoker;

import java.util.Iterator;

public interface Invoker {
    void invoke(Iterator<Invoker> iterator);
}
