package com.byteshaft.callnote;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.WindowManager;

public class AppGlobals extends Application {

    private static LayoutInflater sLayoutInflater;
    private static WindowManager sWindowManager;
    private static SharedPreferences sPreferences;
    private static Context sContext;
    private static boolean isNoteEditModeFirst;
    private static boolean sIsCheckedAll;
    private static boolean sIsUnCheckedAll;

    @Override
    public void onCreate() {
        super.onCreate();
        sLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        sWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        sPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    static LayoutInflater getLayoutInflater() {
        return sLayoutInflater;
    }

    static WindowManager getWindowManager() {
        return sWindowManager;
    }

    static SharedPreferences getSharedPreferences() {
        return sPreferences;
    }

    static void setIsNoteEditModeFirst(boolean first) {
        isNoteEditModeFirst = first;
    }

    static boolean isIsNoteEditModeFirst() {
        return isNoteEditModeFirst;
    }

    static void setCheckedAll(boolean checkedAll) {
        sIsCheckedAll = checkedAll;
    }

    static boolean isCheckedAll() {
        return sIsCheckedAll;
    }

    static void setUncheckedAll(boolean unchecked) {
        sIsUnCheckedAll = unchecked;
    }

    static boolean isUnCheckedAll() {
        return sIsUnCheckedAll;
    }
}
