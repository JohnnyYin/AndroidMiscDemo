package com.johnnyyin.nightmode;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * Created by Johnny on 15/8/15.
 */
public class NightModeManager {

    public static boolean isNightMode(Context context) {
        return (context.getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    public static void setNightMode(Context context, boolean isNightMode) {
        Resources resources = context.getApplicationContext().getResources();
        int oldNightMode = resources.getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        int newNightMode = isNightMode ? Configuration.UI_MODE_NIGHT_YES : Configuration.UI_MODE_NIGHT_NO;
        if (oldNightMode == newNightMode) {
            return;
        }

        setNightModeInternal(resources, newNightMode);
    }

    public static void updateConfiguration(Context context) {
        Resources resources = context.getResources();
        int nightMode = context.getApplicationContext().getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        setNightModeInternal(resources, nightMode);
    }

    private static void setNightModeInternal(Resources resources, int nightMode) {
        Configuration newConfig = new Configuration(resources.getConfiguration());
        newConfig.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK;
        newConfig.uiMode |= nightMode;
        resources.updateConfiguration(newConfig, null);
    }

    static public void setLanguage(String languageCountry, Context context) {
        Resources res = context.getResources();
        if(res == null){
            return;
        }

        Configuration config = res.getConfiguration();
        if(config == null){
            return;
        }

        Locale locale = new Locale(languageCountry, "CN");
        config.locale = locale;

        DisplayMetrics dm = res.getDisplayMetrics();
        res.updateConfiguration(config, dm);
    }
}
