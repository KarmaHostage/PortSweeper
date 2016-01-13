package com.karmahostage.portsweeper.infrastructure.ui;

import android.app.Activity;
import android.content.Context;

import com.karmahostage.portsweeper.infrastructure.AndroidModule;
import com.karmahostage.portsweeper.ui.PortSweeperActivity;
import com.karmahostage.portsweeper.infrastructure.ForActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                PortSweeperActivity.class
        },
        addsTo = AndroidModule.class,
        library = true
)
public class ActivityModule {
    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    /**
     * Allow the activity context to be injected but require that it be annotated with
     * {@link ForActivity @ForActivity} to explicitly differentiate it from application context.
     */
    @Provides
    @Singleton
    @ForActivity
    Context provideActivityContext() {
        return activity;
    }

    /*
  @Provides @Singleton ActivityTitleController provideTitleController() {
    return new ActivityTitleController(activity);
    */

}