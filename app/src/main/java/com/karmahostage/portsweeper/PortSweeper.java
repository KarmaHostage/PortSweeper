package com.karmahostage.portsweeper;

import android.app.Application;

import com.karmahostage.portsweeper.infrastructure.AndroidModule;
import com.karmahostage.portsweeper.infrastructure.PortSweeperModule;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

public class PortSweeper extends Application {
    private ObjectGraph graph;

    @Override
    public void onCreate() {
        super.onCreate();

        graph = ObjectGraph.create(getModules().toArray());
    }

    protected List<Object> getModules() {
        return Arrays.asList(
                new PortSweeperModule(),
                new AndroidModule(this)
        );
    }

    public ObjectGraph getApplicationGraph() {
        return graph;
    }

    public void inject(Object object) {
        graph.inject(object);
    }
}
