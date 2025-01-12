// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.components.module_installer;

import android.app.Activity;

import org.chromium.base.annotations.MainDex;

/**
 * This interface contains all the necessary methods to orchestrate the installation of dynamic
 * feature modules (DFMs).
 */
public interface ModuleInstaller {
    /** Returns the singleton instance from the correct implementation. */
    @MainDex
    static ModuleInstaller getInstance() {
        if (ModuleInstallerConfig.IS_BUNDLE) {
            return ModuleInstallerImpl.getInstance();
        } else {
            return ApkModuleInstaller.getInstance();
        }
    }

    static void setInstanceForTesting(ModuleInstaller moduleInstaller) {
        if (ModuleInstallerConfig.IS_BUNDLE) {
            ModuleInstallerImpl.setInstanceForTesting(moduleInstaller);
        } else {
            ApkModuleInstaller.setInstanceForTesting(moduleInstaller);
        }
    }

    /** Needs to be called before trying to access a module. */
    default void init() {}

    /**
     * Needs to be called in attachBaseContext of the activities that want to have access to
     * splits prior to application restart.
     *
     * For details, see:
     * https://developer.android.com/reference/com/google/android/play/core/splitcompat/SplitCompat.html#install(android.content.Context)
     * @param activity The Activity for which SplitCompat will be run.
     */
    default void initActivity(Activity activity) {}

    /**
     * Records via UMA all modules that have been requested and are currently installed. The intent
     * is to measure the install penetration of each module.
     */
    default void recordModuleAvailability() {}

    /** Writes fully installed and emulated modules to crash keys. */
    default void updateCrashKeys() {}

    /**
     * Requests the install of a module. The install will be performed asynchronously.
     *
     * @param moduleName Name of the module as defined in GN.
     * @param onFinishedListener Listener to be called once installation is finished.
     */
    default void install(String moduleName, OnModuleInstallFinishedListener onFinishedListener) {}

    /**
     * Asynchronously installs module in the background when on unmetered connection and charging.
     * Install is best effort and may fail silently. Upon success, the module will only be available
     * after Chrome restarts.
     *
     * @param moduleName Name of the module.
     */
    default void installDeferred(String moduleName) {}

    /** Called when startup completes to record module overhead during startup. */
    default void recordStartupTime() {}
}
