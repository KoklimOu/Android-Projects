package com.ahope.ds.cl.mdm_agent.manager;

import android.os.Build;

import com.ahope.ds.cl.mdm_agent.common.util.CommonUtil;

public class ErrorHandleManager {
    public static final int ERROR_NOT_ACTIVE_ADMIN = 100;
    public static final int ERROR_IS_ALREADY_ACTIVE_ADMIN = 101;
    public static final int ERROR_NOT_ACTIVE_KNOX_LICENSE = 200;
    public static final int ERROR_IS_ALREADY_ACTIVE_LICENSE = 201;
    public static final int ERROR_NOT_SIGHED_APK_FROM_LG = 300;
    public static final int ERROR_NOT_PERMISSION = 400;
    public static final int ERROR_UNKNOWN = 500;
    public static final int ERROR_SVC_IS_NULL = 600;
    public static final int ERROR_AGENT_IS_NOT_INSTALL = 601;
    public static final int ERROR_SIG_NOT_MATCH = 700;
    public static final int ERROR_PACKAGE_NOT_FOUND = 701;

    public int getErrorState() {
        return ERROR_UNKNOWN;
    }

    public int getErrorState(Exception e) {
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) {
            if (e.getMessage().contains("No active admin owned by uid")) { return ERROR_NOT_ACTIVE_ADMIN; }
            else if (e.getMessage().contains("does not have android.permission")) { return ERROR_NOT_ACTIVE_KNOX_LICENSE; }
        } else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) {
            if (e.getMessage().contains("No active admin owned by uid")) { return ERROR_NOT_ACTIVE_ADMIN; }
        }
        return ERROR_UNKNOWN;
    }
}
