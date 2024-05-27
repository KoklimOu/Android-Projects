package com.ahope.ds.cl.mdm_agent.common.preference.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.ahope.ds.cl.mdm_agent.common.preference.config.DSPreferencesConfig;

import java.util.HashSet;

/**
 * Created by kim on 2016-09-29.
 */

public class DSPreferencesManager {
    private static final String TAG = DSPreferencesManager.class.getSimpleName();
    private static DSPreferencesManager sInstance;
    private final SharedPreferences mPref;

    private DSPreferencesManager(Context context) {
        mPref = context.getSharedPreferences(DSPreferencesConfig.PERF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void initializeInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DSPreferencesManager(context);
        }
    }

    public static synchronized DSPreferencesManager getInstance() {
        if (sInstance == null) {
           return null;
        }
        return sInstance;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mPref.getBoolean(key, defaultValue);
    }

    public void setBoolean(String key, boolean value) {
        mPref.edit().putBoolean(key, value).commit();
    }

    public String getString(String key, String defaultValue) {
        return mPref.getString(key, defaultValue);
    }

    public void setString(String key, String value) {
        mPref.edit().putString(key, value).commit();
    }

    public Double getDouble(String key, Double defaultValue) {
        if (!mPref.contains(key)) {
            return defaultValue;
        }
        return Double.longBitsToDouble(mPref.getLong(key, 0));
    }

    public void setDouble(String key, Double value) {
        mPref.edit().putLong(key, Double.doubleToRawLongBits(value)).commit();
    }

    public int getInt(String key, int defaultValue) {
        return mPref.getInt(key, defaultValue);
    }

    public void setInt(String key, int value) {
        mPref.edit().putInt(key, value).commit();
    }

    public long getLong(String key, long defaultValue) {
        return mPref.getLong(key, defaultValue);
    }

    public void setLong(String key, long value) {
        mPref.edit().putLong(key, value).commit();
    }

    public boolean isContain(String key) {
        return mPref.contains(key);
    }

    public void setHashSet(String key, HashSet<String> hashSet) {
        mPref.edit().putStringSet(key, hashSet).commit();
    }

    public HashSet<String> getHashSet(String key) {
        return (HashSet<String>) mPref.getStringSet(key, new HashSet<String>());
    }

    public void removeKey(String key) {
        if (isContain(key)) {
            mPref.edit().remove(key).apply();
        }
    }

    public void removeAll() {
        mPref.edit().clear().commit();
    }

}
