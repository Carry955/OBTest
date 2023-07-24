package com.carry.obtest;

import android.content.Context;
import android.util.Log;
import io.objectbox.BoxStore;
import io.objectbox.BoxStoreBuilder;
import io.objectbox.android.Admin;
import io.objectbox.android.BuildConfig;
import io.objectbox.exception.FileCorruptException;
import io.objectbox.model.ValidateOnOpenMode;

public class ObjectBox {
    private static BoxStore boxStore;
    static void init(Context context) {
        BoxStoreBuilder storeBuilder = MyObjectBox.builder()
                .validateOnOpen(ValidateOnOpenMode.WithLeaves)
                .validateOnOpenPageLimit(20)
                .androidContext(context.getApplicationContext());
        try {
            boxStore = storeBuilder.build();
        } catch (FileCorruptException e) {
            Log.w(App.TAG, "File corrupt, trying previous data snapshot...", e);
            storeBuilder.usePreviousCommit();
            boxStore = storeBuilder.build();
        }

        if (BuildConfig.DEBUG) {
            Log.d(App.TAG,
                    String.format("Using ObjectBox %s (%s)",
                    BoxStore.getVersion(), BoxStore.getVersionNative()));
            new Admin(boxStore).start(context.getApplicationContext());
        }
    }

    public static BoxStore get() {
        return boxStore;
    }
}
