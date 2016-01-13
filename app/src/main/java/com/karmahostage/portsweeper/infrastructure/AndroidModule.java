package com.karmahostage.portsweeper.infrastructure;

import android.content.Context;

import com.karmahostage.portsweeper.PortSweeper;
import com.karmahostage.portsweeper.scanning.model.ScanTargetBuilder;
import com.karmahostage.portsweeper.scanning.service.ScanService;
import com.karmahostage.portsweeper.scanning.service.ScanServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true)
public class AndroidModule {
    private final PortSweeper application;

    public AndroidModule(PortSweeper application) {
        this.application = application;
    }

    /**
     * Allow the application context to be injected but require that it be annotated with
     * {@link ForApplication @Annotation} to explicitly differentiate it from an activity context.
     */
    @Provides
    @Singleton
    @ForApplication
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    @ForApplication
    ScanService scanService() {
        return new ScanServiceImpl();
    }

    @Provides
    @Singleton
    @ForApplication
    ScanTargetBuilder scanTargetBuilder() {
        return new ScanTargetBuilder();
    }

    /*
    @Provides @Singleton
    LocationManager provideLocationManager() {
        return (LocationManager) application.getSystemService(LOCATION_SERVICE);
    } */
}