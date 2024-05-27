package com.ahope.ds.cl.mdm_agent.module.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.ahope.ds.cl.mdm_agent.common.preference.config.DSPreferencesConfig;
import com.ahope.ds.cl.mdm_agent.common.preference.manager.DSPreferencesManager;
import com.ahope.ds.cl.mdm_agent.module.response.DsResponse;
import com.ahope.ds.cl.mdm_agent.service.Svc;

public class PackageInstallReceiver extends BroadcastReceiver {
    PackageInfo packageInfo;
    String market;
    public static boolean isPackageAdd = false;
    public static boolean isPackageRemove = false;

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String packageName = intent.getDataString();
        String ClientPackage = "";
        if (Svc.one == null) return;
        if (packageName.startsWith("package:")) {
            int index = packageName.indexOf(":");
            packageName= packageName.substring(index + 1);
            Log.d("pkgName : ", packageName);
        }

        if(action.equals(Intent.ACTION_PACKAGE_ADDED)) {
            Log.d("PakcageReceiver", "ACTION_PACKAGE_ADDED : " + packageName);
            isPackageAdd = true;
            try {
                if (DSPreferencesManager.getInstance() == null) Svc.one.initPreferManager();
                if (DSPreferencesManager.getInstance().getBoolean(DSPreferencesConfig.IS_UNKHOWN_APP_INSTALL, true) == false) {
                    try {
                        packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA);
                        ClientPackage = DSPreferencesManager.getInstance().getString(DSPreferencesConfig.MDM_PACKAGE_NAME, "");
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        return;
                    }
                    if(!isSystemPackage(packageInfo.applicationInfo) && !ClientPackage.equals(packageInfo.packageName)) {
                        Svc.one.mdmManager.uninstallApplication(packageInfo.packageName);
                        Log.d("PackageInstallReceiver", "no vendor and no System app");
                    }
                }
            } catch (Exception e) {
                Log.d("PackageInstallReceiver", e.getMessage());
                return;
            }
        } else if(action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
            Log.d("PakcageReceiver", "ACTION_PACKAGE_REMOVED");
            isPackageRemove = true;
            try {
                Svc.one.initPreferManager();
                if (packageName.equals(DSPreferencesManager.getInstance().getString(DSPreferencesConfig.MDM_PACKAGE_NAME, ""))) {
                    int i;
                    DsResponse ds = Svc.one.getPolicy();

                    for (i = 0; i < ds.getPolicies().length; i++) {
                        if (ds.getPolicies()[i] == null || ds.getPolicies()[i].equals("")) break;
                    }

                    if (i == 1 && ds.getPolicies()[0].equals(Svc.one.POLICY_REMOVABLE_ADMIN)) {
                        Svc.one.mdmManager.setAdminRemovableState(true);
                    }
                }
                return;
            } catch (Exception e) {
                Log.d("PackageInstallReceiver", e.getMessage());
                return;
            }
        }
    }

    private boolean isSystemPackage(ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }
}
