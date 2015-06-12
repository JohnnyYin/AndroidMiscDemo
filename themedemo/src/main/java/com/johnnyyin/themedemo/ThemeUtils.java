package com.johnnyyin.themedemo;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

public class ThemeUtils {
    public static class Theme {
        public String getTheme() {
            return "thm";
        }
    }

    public static void setTheme(Theme theme, Context context) {
        Resources res = context.getResources();
        if (res == null) {
            return;
        }

        Configuration config = res.getConfiguration();
        if (config == null) {
            return;
        }

        Locale locale = new Locale(theme.getTheme());
        config.locale = locale;

        DisplayMetrics dm = res.getDisplayMetrics();
        res.updateConfiguration(config, dm);
    }
}
