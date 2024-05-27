package com.ahope.ds.cl.mdm_agent.manager;

import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.ahope.ds.cl.mdm_agent.common.preference.config.DSPreferencesConfig;
import com.ahope.ds.cl.mdm_agent.common.preference.manager.DSPreferencesManager;
import com.ahope.ds.cl.mdm_agent.common.util.CommonUtil;
import com.ahope.ds.cl.mdm_agent.module.receiver.AdminReceiver;
import com.ahope.ds.cl.mdm_agent.service.Svc;
import com.lge.mdm.LGMDMManager;
import com.lge.mdm.config.LGMDMAppState;
import com.lge.mdm.config.LGMDMApplicationState;
import com.samsung.android.knox.EnterpriseDeviceManager;
import com.samsung.android.knox.application.ApplicationPolicy;

import java.util.ArrayList;
import java.util.List;

public class LGGATEManager {
    private final String TAG = "LGGATEManager";
    private LGMDMManager manager;
    private final int STATE_SUCCESS = 0;

    ErrorHandleManager errorhandler;
    ComponentName cn;
    Context context;

    public LGGATEManager(Context context) {
        manager = LGMDMManager.getInstance();
        cn = new ComponentName(context, AdminReceiver.class);
        this.context = context;
        errorhandler = new ErrorHandleManager();
    }

    public int setCameraState(boolean state) {
        try {
            List<String> list = new ArrayList<String>();
            manager.setAllowCameraWithWhitelist(cn, state, list);
        } catch (Exception e) {
            Log.e(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getCameraState() {
        return manager.getAllowCameraWithWhitelist(cn);
    }

    public int setScreenCaptureState(boolean state) {
        try {
            manager.setAllowScreenCaptureWithoutADB(cn, state);
        } catch (Exception e) {
            Log.e(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getScreenCaptureState() {
        return manager.getAllowScreenCaptureWithoutADB(cn);
    }

    public int setWifiState(boolean state) {
        try {
            manager.setAllowWifi(cn, state);
        } catch (Exception e) {
            Log.e(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getWifiState() {
        return manager.getAllowWifi(cn);
    }

    public int setWifiDirectState(boolean state) {
        try {
            manager.setAllowWifiDirect(cn, state);
        } catch (Exception e) {
            Log.e(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getWifiDirectState() {
        return manager.getAllowWifiDirect(cn);
    }

    public int setBlueToothState(boolean state) {
        try {
            if (state) manager.setAllowBluetooth(cn, manager.LGMDMBluetooth_ALLOW);
            else manager.setAllowBluetooth(cn, manager.LGMDMBluetooth_ALLOW_AUDIOONLY);
        } catch (Exception e) {
            Log.e(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getBlueToothState() {
        if (manager.getAllowBluetooth(cn) != manager.LGMDMBluetooth_ALLOW_AUDIOONLY && manager.getAllowBluetooth(cn) != manager.LGMDMBluetooth_DISALLOW) return true;
        else return false;
    }

    public int setNFCState(boolean state) {
        int state_;
        if (state) state_ = manager.NFC_ALLOW;
        else state_ = manager.NFC_DISALLOW;
        try {
            manager.setAllowNfc(cn, state_);
        } catch (Exception e) {
            Log.e(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getNFCState() {
        if (manager.getAllowNfc(cn) != 0) { return true; }
        else return false;
    }

    public int setUsbState(boolean state) {
        try {
            manager.setAllowUSBMtp(cn, state);
            manager.setAllowUSBHostStorage(cn, state);
            manager.setAllowUSBPtp(cn, state);
            manager.setAllowUSBTethering(cn, state);
        } catch (Exception e) {
            Log.e(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getUsbState() {
        if (manager.getAllowUSBMtp(cn) || manager.getAllowUSBHostStorage(cn) || manager.getAllowUSBTethering(cn) || manager.getAllowUSBPtp(cn))
            return true;
        else
            return false;
    }

    public int setUsbDebuggingState(boolean state) {
        try {
            manager.setAllowUSBDebugging(cn, state);
        } catch (Exception e) {
            Log.e(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getUsbDebuggingState() {
        return manager.getAllowUSBDebugging(cn);
    }

    public int setTetheringState(boolean state) {
        try {
            manager.setAllowTethering(cn, state);
        } catch (Exception e) {
            Log.e(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getTetheringState() {
        return manager.getAllowTethering(cn);
    }

    public int setExternalMemoryState(boolean state) {
        try {
            manager.setAllowExternalMemorySlot(cn, state);
        } catch (Exception e) {
            Log.e(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getExternalMemoryState() {
        return manager.getAllowExternalMemorySlot(cn);
    }

    public int setMicrophoneState(boolean state) {
        try {
            manager.setAllowMicrophone(cn, state);
        } catch (Exception e) {
            Log.e(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getMicrophoneState() {
        return manager.getAllowMicrophone(cn);
    }

    public int setFactoryResetState(boolean state) {
        try {
            manager.setAllowHardwareFactoryreset(cn, state);
        } catch (Exception e) {
            Log.e(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getFactoryResetState() {
        return manager.getAllowHardwareFactoryreset(cn);
    }

    public int setUninstallationState(boolean state, String[] packageNames) {
        List<LGMDMApplicationState> list = new ArrayList<>();
        LGMDMApplicationState appState = new LGMDMApplicationState();

        try {
            if (packageNames == null || packageNames.length < 1) return ErrorHandleManager.ERROR_UNKNOWN;
            for (int i = 0; i < packageNames.length; i++) {
                appState.setPackageName(packageNames[i]);
                if (state) appState.setAllowUninstallation(LGMDMAppState.ENABLED);
                else appState.setAllowUninstallation(LGMDMAppState.DISABLED);
                list.add(appState);
                if (DSPreferencesManager.getInstance() == null) Svc.one.initPreferManager();
                DSPreferencesManager.getInstance().setBoolean(DSPreferencesConfig.IS_UNINSTALL_POLICY, state);
                manager.setApplicationState(cn, list);
            }
        } catch (Exception e) {
            Log.e(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getUninstallationState() {
        try {
            if (DSPreferencesManager.getInstance() == null) Svc.one.initPreferManager();
            return DSPreferencesManager.getInstance().getBoolean(DSPreferencesConfig.IS_UNINSTALL_POLICY, true);
        } catch (Exception e) {
            Log.e(TAG, "SecurityException: " + e);
            return false;
        }
    }

    public int uninstallApplication(String packageName) {
        try {
            manager.uninstallApplicationSyncTask(cn, packageName);
        } catch (Exception e) {
            Log.e(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public int setAdminRemovableState(boolean state) {
        try {
            manager.setAllowRemoveDeviceAdmin(cn, state);
        } catch (Exception e) {
            Log.e(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getAdminRemovableState() {
        try {
            return manager.getAllowRemoveDeviceAdmin(cn, CommonUtil.AGENT_PKG_NAME);
        } catch (Exception e) {
            Log.e(TAG, "SecurityException: " + e);
            return false;
        }
    }

    public int setGpsState(boolean state) {
        try {
            manager.setAllowGPSLocation(cn, state);
        } catch (Exception e) {
            Log.e(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getGpsState() {
        return manager.getAllowGPSLocation(cn);
    }

    public int setAirplaneModeState(boolean state) {
        try {
            manager.setAllowAirplaneModeOn(cn, state);
        } catch (Exception e) {
            Log.e(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getAirplaneModeState() {
        return manager.getAllowAirplaneModeOn(cn);
    }

    public int setRoamingState(boolean state) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                manager.setAllowCallInRoaming(cn, state);
            } else {
                manager.setAllowDataRoaming(cn, state);
            }
        } catch (Exception e) {
            Log.e(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getRoamingState() {
        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
            ? manager.getAllowCallInRoaming(cn) : manager.getAllowDataRoaming(cn);
    }

    public int setDisableAppState(boolean state, List<String> apps) {
        List<LGMDMApplicationState> runnableApps = new ArrayList<>();
        try {
//            manager.setAllowRunningApplication(cn, state);
            if (apps == null || apps.size() < 1) return ErrorHandleManager.ERROR_UNKNOWN;
            for (int i = 0; i < apps.size(); i++) {
                LGMDMApplicationState appState = new LGMDMApplicationState();
                appState.setPackageName(apps.get(i));
                if (state) appState.setEnable(LGMDMAppState.DISABLED);
                else appState.setEnable(LGMDMAppState.ENABLED);
                runnableApps.add(appState);
            }
            manager.setApplicationState(cn, runnableApps);
        } catch (Exception e) {
            Log.e(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getDisableAppState() {
        try {
            return manager.getAllowRunningApplication(cn);
        } catch (Exception e) {
            Log.e(TAG, "SecurityException: " + e);
            return false;
        }
    }

    public int installApp(String packageName) {
        try {
            manager.installApplicationAsyncTask(cn, packageName, false);
        } catch (Exception e) {
            Log.e(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }
}
