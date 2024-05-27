package com.ahope.ds.cl.mdm_agent.manager;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;

import com.ahope.ds.cl.mdm_agent.MainActivity;
import com.ahope.ds.cl.mdm_agent.common.preference.config.DSPreferencesConfig;
import com.ahope.ds.cl.mdm_agent.common.preference.manager.DSPreferencesManager;
import com.ahope.ds.cl.mdm_agent.common.util.CommonUtil;
import com.ahope.ds.cl.mdm_agent.module.receiver.AdminReceiver;
import com.ahope.ds.cl.mdm_agent.service.Svc;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MdmManager extends AppCompatActivity {
    private final String TAG = "MdmManager";
    public final int STATE_SUCCESS = 0;

    private Context context;
    public KnoxManager knoxManager;
    public LGGATEManager lgGateManager;
    private ComponentName mDeviceAdmin;
    DevicePolicyManager dpm;
    ErrorHandleManager errorhandler;

    public MdmManager(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        dpm = ((DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE));
        mDeviceAdmin = new ComponentName(context, AdminReceiver.class);
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) {
            knoxManager = new KnoxManager(context);
        } else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) {
            lgGateManager = new LGGATEManager(context);
        }
    }

    public boolean isActiveAdmin() {
        boolean result = dpm.isAdminActive(mDeviceAdmin);
        if(result) CommonUtil.stateDeviceAdmin = CommonUtil.STATE_SUCCESS;
        else CommonUtil.stateDeviceAdmin = CommonUtil.STATE_DENIED;
        return result;
    }


    public int setActiveKnoxLicense() throws InterruptedException {
        return knoxManager.activateKPELicense();
    }

    public int setActiveKnoxLicenseSync() throws InterruptedException {
        return knoxManager.activateKPELicenseSync();
    }

    public int setCameraState(boolean state) {
        int result = 0;

        Log.d("Svc", "setCameraState : " + state);
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.setCameraState(state);
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.setCameraState(state);
        return result;
    }

    public boolean getCameraState() {
        boolean result = false;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.getCameraState();
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.getCameraState();
        return result;
    }

    public int setScreenCaptureState(boolean state) {
        int result = 0;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result=  knoxManager.setScreenCaptureState(state);
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.setScreenCaptureState(state);
        Log.d("Svc", "setScreenCaptureState : " + state);
        return result;
    }

    public boolean getScreenCaptureState() {
        boolean result = false;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.getScreenCaptureState();
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.getScreenCaptureState();
        return result;
    }

    public int setBluetoothState(boolean state) {
        int result = 0;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result=  knoxManager.setBluetoothState(state);
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.setBlueToothState(state);
        Log.d("Svc", "setBluetoothState : " + state);
        return result;
    }

    public boolean getBluetoothState() {
        boolean result = false;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.getBluetoothState();
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.getBlueToothState();
        return result;
    }

    public int setTetheringState(boolean state) {
        int result = 0;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result=  knoxManager.setTetheringState(state);
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.setTetheringState(state);
        return result;
    }

    public boolean getTetheringState() {
        boolean result = false;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.getTetheringState();
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.getTetheringState();
        return result;
    }

    public int setWifiState(boolean state) {
        int result = 0;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result=  knoxManager.setWifiState(state);
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.setWifiState(state);
        return result;
    }

    public boolean getWifiState() {
        boolean result = false;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.getWifiState();
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.getWifiState();
        return result;
    }

    public int setWifiDirectState(boolean state) {
        int result = 0;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result=  knoxManager.setWifiDirectState(state);
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.setWifiDirectState(state);
        return result;
    }

    public boolean getWifiDirectState() {
        boolean result = false;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.getWifiDirectState();
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.getWifiDirectState();
        return result;
    }

    public int setUsbState(boolean state) {
        int result = 0;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result=  knoxManager.setUsbContextState(state);
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.setUsbState(state);
        return result;
    }

    public boolean getUsbState() {
        boolean result = false;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.getUsbContextState();
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.getUsbState();
        return result;
    }

    public int setUsbDebuggingState(boolean state) {
        int result = 0;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result=  knoxManager.setUsbDebuggingState(state);
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.setUsbDebuggingState(state);
        return result;
    }

    public boolean getUsbDebuggingState() {
        boolean result = false;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.getUsbDebuggingState();
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.getUsbDebuggingState();
        return result;
    }

    public int setNfcState(boolean state) {
        int result = 0;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result=  knoxManager.setNFCState(state);
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.setNFCState(state);
        return result;
    }

    public boolean getNfcState() {
        boolean result = false;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.getNFCState();
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.getNFCState();
        return result;
    }

    public int setSdcardAccessState(boolean state) {
        int result = 0;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result=  knoxManager.setSdcardAccessState(state);
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.setExternalMemoryState(state);
        return result;
    }

    public boolean getSdcardAccessState() {
        boolean result = false;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.getSdcardAccessState();
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.getExternalMemoryState();
        return result;
    }

    public int setMicrophoneState(boolean state) {
        int result = 0;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result=  knoxManager.setMicrophoneState(state);
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.setMicrophoneState(state);
        return result;
    }

    public boolean getMicrophoneState() {
        boolean result = false;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.getMicrophoneState();
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.getMicrophoneState();
        return result;
    }

    public int setGpsState(boolean state) {
        int result = 0;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.setGpsState(state);
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.setGpsState(state);
        return result;
    }

    public boolean getGpsState() {
        boolean result = false;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.getGpsState();
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.getGpsState();
        return result;
    }
    public int setAirplaneModeState(boolean state) {
        int result = 0;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.setAirplaneModeState(state);
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.setAirplaneModeState(state);
        return result;
    }

    public boolean getAirPlaneModeState() {
        boolean result = false;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.getAirplaneModeState();
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.getAirplaneModeState();
        return result;
    }

    public int setRoamingState(boolean state) {
        int result = 0;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.setRoamingState(state);
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.setRoamingState(state);
        return result;
    }

    public boolean getRoamingState() {
        boolean result = false;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.getRoamingState();
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.getRoamingState();
        return result;
    }

    public int setFactoryResetState(boolean state) {
        int result = 0;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result=  knoxManager.setFactoryResetState(state);
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.setFactoryResetState(state);
        return result;
    }

    public boolean getFactoryResetState() {
        boolean result = false;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.getFactoryResetState();
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.getFactoryResetState();
        return result;
    }

    public int setUnkhownAppState(boolean state) {
        int result = 0;
        if (DSPreferencesManager.getInstance() == null) Svc.one.initPreferManager();
        DSPreferencesManager.getInstance().setBoolean(DSPreferencesConfig.IS_UNKHOWN_APP_INSTALL, state);
        return result;
    }

    public boolean getUnkhownAppState() {
        if (DSPreferencesManager.getInstance() == null) Svc.one.initPreferManager();
        return DSPreferencesManager.getInstance().getBoolean(DSPreferencesConfig.IS_UNKHOWN_APP_INSTALL, true);
    }

    public int setUninstallApplicationState(boolean state, String[] pakcageNames) {
        int result = 0;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result=  knoxManager.setUninstallationState(state, pakcageNames);
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.setUninstallationState(state, pakcageNames);
        return result;
    }

    public boolean getUninstallApplicationState() {
        boolean result = false;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result=  knoxManager.getUninstallationState();
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.getUninstallationState();
        return result;
    }

    public int uninstallApplication(String packageName) {
        int result = 0;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.uninstallApplication(packageName);
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.uninstallApplication(packageName);
        return result;
    }

    public boolean checkAccessibilityPermissions() {
        AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService(context.ACCESSIBILITY_SERVICE);
        // getEnabledAccessibilityServiceList는 현재 접근성 권한을 가진 리스트를 가져오게 된다
        List<AccessibilityServiceInfo> list = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.DEFAULT);
        for (int i = 0; i < list.size(); i++) {
            AccessibilityServiceInfo info = list.get(i);
            // 접근성 권한을 가진 앱의 패키지 네임과 패키지 네임이 같으면 현재앱이 접근성 권한을 가지고 있다고 판단함
            if (info.getResolveInfo().serviceInfo.packageName.equals(context.getPackageName())) {
                return true;
            }
        }
        return isAccessibilityService();
    }

    public boolean isAccessibilityService()
    {
        String prefString = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        return prefString!= null && prefString.contains(context.getPackageName());
    }

    public int setAccessibilityPermissions() {
        try {
            context.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(FLAG_ACTIVITY_NEW_TASK));
        } catch (Exception e) {
            Log.w(TAG, e.getMessage());
            return ErrorHandleManager.ERROR_UNKNOWN;
        }
        return STATE_SUCCESS;
    }

    public int setAdminRemovableState(boolean state) {
        int result = 0;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.setAdminRemovableState(state);
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.setAdminRemovableState(state);
        return result;
    }

    public boolean getAdminRemovableState() {
        boolean result = false;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.getAdminRemovableState();
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.getAdminRemovableState();
        return result;
    }

    public int setDisableAppState(boolean state, List<String> apps) {
        int result = 0;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.setDisableAppState(state, apps);
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.setDisableAppState(state, apps);
        return result;
    }

    public boolean getDisableAppState() {
        boolean result = false;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.getDisableAppState();
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.getDisableAppState();
        return result;
    }

    public int wipeData() {
        try {
            dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public int lockScreen() {
        try {
            dpm.lockNow();

        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public int installApp(String packageName) {
        int result = 0;
        if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) result = knoxManager.installApp(packageName);
        else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) result = lgGateManager.installApp(packageName);
        return result;
    }

    public int unInstallApp(String packageName) {
        int result = 0;
         result = uninstallApplication(packageName);
        return result;
    }
}
