package com.dingding.open.achelous.core;

import java.util.ArrayList;

import com.dingding.open.achelous.core.invoker.Invoker;

public class InvokerCore {
    private ThreadLocal<Integer> currentIndex = new ThreadLocal<Integer>();
    private ArrayList<Invoker> invokers;

    public ThreadLocal<Integer> getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(ThreadLocal<Integer> currentIndex) {
        this.currentIndex = currentIndex;
    }

    public ArrayList<Invoker> getInvokers() {
        return invokers;
    }

    public void setInvokers(ArrayList<Invoker> invokers) {
        this.invokers = invokers;
    }

    public Invoker nextNFromM(int m, int n) {
        return nextNFromM(m, n, true);
    }

    public Invoker nextNFromM(int m, int n, boolean onlyExecOnce) {
        int nextIndex = m + n;
        Invoker invoker = this.getInvokers().get(nextIndex);
        if (onlyExecOnce) {
            this.getCurrentIndex().set(nextIndex);
        }
        return invoker;
    }

    public Invoker nextN(int n) {
        return nextN(n, true);
    }

    public Invoker nextN(int n, boolean onlyExecOnce) {
        int nextIndex = this.getCurrentIndex().get() + 1;
        Invoker invoker = this.getInvokers().get(nextIndex);
        if (onlyExecOnce) {
            this.getCurrentIndex().set(nextIndex);
        }
        return invoker;
    }

}
