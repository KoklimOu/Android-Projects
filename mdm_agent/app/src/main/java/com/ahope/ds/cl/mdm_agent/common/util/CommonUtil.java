package com.ahope.ds.cl.mdm_agent.common.util;

import android.Manifest;
import android.os.Build;

import java.util.HashSet;

public class CommonUtil {
    public static final String AGENT_PKG_NAME = "com.ahope.ds.cl.mdm_agent";
    public static final String TAG = "MDMLibrary";
    public static final String BRAND_SAMSUNG = "samsung";
    public static final String BRAND_LG = "LGE";
    public static final int STATE_SUCCESS = 0;
    public static final int STATE_DENIED = 1;
    public static final int STATE_USER_REJECTED = 2;
    public static final int STATE_KEEP_REJECTED = -1;
    public static final double AGENT_VERSION = 1.5;

    public static int stateGrantPermissions = CommonUtil.STATE_DENIED;
    public static int stateDeviceAdmin = CommonUtil.STATE_DENIED;
    public static boolean isFirstRejected = false;

    public static final HashSet<String> RUNTIME_PERMS_LIST = new HashSet<String>();
    public static final HashSet<String> RUNTIME_STORAGE_PERMS_LIST = new HashSet<String>();

    static {
//		RUNTIME_PERMS_LIST.add(Manifest.permission.ACCESS_FINE_LOCATION);
//		RUNTIME_PERMS_LIST.add(Manifest.permission.ACCESS_COARSE_LOCATION);
//        RUNTIME_PERMS_LIST.add(Manifest.permission.RECORD_AUDIO);
//		RUNTIME_PERMS_LIST.add(Manifest.permission.CAMERA);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) RUNTIME_PERMS_LIST.add(Manifest.permission.READ_PHONE_STATE);
        else RUNTIME_PERMS_LIST.add(Manifest.permission.READ_PHONE_NUMBERS);
        RUNTIME_PERMS_LIST.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        RUNTIME_PERMS_LIST.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    static {
        RUNTIME_STORAGE_PERMS_LIST.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        RUNTIME_PERMS_LIST.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
}
