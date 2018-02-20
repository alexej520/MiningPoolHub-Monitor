package ru.lextop.miningpoolhub.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import ru.lextop.miningpoolhub.App

object AppInjector {
    fun init(app: App) {
        DaggerAppComponent.builder()
            .application(app)
            .build()
            .inject(app)
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityDestroyed(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle?) {}
            override fun onActivityStopped(activity: Activity) {}
            // try inject all Activities was created
            override fun onActivityCreated(activity: Activity, p1: Bundle?) {
                if (activity is Injectable || activity is HasSupportFragmentInjector) {
                    AndroidInjection.inject(activity)
                }
                if (activity is FragmentActivity) {
                    activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                        object : FragmentManager.FragmentLifecycleCallbacks() {

                            override fun onFragmentCreated(
                                fm: FragmentManager?,
                                f: android.support.v4.app.Fragment?,
                                savedInstanceState: Bundle?
                            ) {
                                if (f is Injectable) {
                                    AndroidSupportInjection.inject(f)
                                }
                            }
                        }, true
                    )
                }
            }
        })
    }
}