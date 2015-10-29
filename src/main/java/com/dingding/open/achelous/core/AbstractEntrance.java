package com.dingding.open.achelous.core;

public abstract class AbstractEntrance {

    protected AbstractEntrance() {
        PipelineManager.checkPluginPath(getPluginPath());
    }

    protected abstract String getPluginPath();
}
