package com.ahope.ds.cl.mdm_agent.manager;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.ahope.ds.cl.mdm_agent.BuildConfig;
import com.ahope.ds.cl.mdm_agent.common.preference.config.DSPreferencesConfig;
import com.ahope.ds.cl.mdm_agent.common.preference.manager.DSPreferencesManager;
import com.ahope.ds.cl.mdm_agent.common.util.CommonUtil;
import com.ahope.ds.cl.mdm_agent.service.Svc;
import com.samsung.android.knox.EnterpriseDeviceManager;
import com.samsung.android.knox.EnterpriseKnoxManager;
import com.samsung.android.knox.application.ApplicationPolicy;
import com.samsung.android.knox.bluetooth.BluetoothPolicy;
import com.samsung.android.knox.container.AuthenticationConfig;
import com.samsung.android.knox.container.KnoxContainerManager;
import com.samsung.android.knox.devicesecurity.PasswordPolicy;
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager;
import com.samsung.android.knox.location.LocationPolicy;
import com.samsung.android.knox.nfc.NfcPolicy;
import com.samsung.android.knox.restriction.RestrictionPolicy;
import com.samsung.android.knox.restriction.RoamingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class KnoxManager {
    private final String TAG = "KnoxManager";
    private final int STATE_SUCCESS = 0;
    public static List<String> appList = new ArrayList<String>();

    ErrorHandleManager errorhandler;
    Context context;
    public static Semaphore semaphore = new Semaphore(0);
    public static int ERROR_CODE = 0;

    public KnoxManager(Context context) {
        this.context = context;
        errorhandler = new ErrorHandleManager();
    }

    public int activateKPELicenseSync() throws InterruptedException {
        int result = activateKPELicense();
        if (result != STATE_SUCCESS) return result;

        Thread timeoutWorker = new Thread(new ApduTimeoutRunnable());
        timeoutWorker.start();
        try {
            semaphore.acquire();
            semaphore.acquire();
            semaphore.acquire();
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
        result = ERROR_CODE;
        timeoutWorker.interrupt();
        semaphore.release();
        return result;
    }

    public int activateKPELicense() throws InterruptedException {
        KnoxEnterpriseLicenseManager klmManager = KnoxEnterpriseLicenseManager.getInstance(context);

        try {
            // KPE License Activation TODO Add license key to Constants.java
            klmManager.activateLicense((BuildConfig.BUILD_TYPE.equals("debug")) ? "KLM06-GVNSI-LDB9L-7WJH7-ZT3C1-98093" : "KLM09-IKO86-H1CP4-C9BM3-2IDLY-0OQ4M");
            Log.d(TAG, "Activating KPE license...");
        } catch (Exception e) {
            Log.w(TAG, e.getMessage());
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public int setCameraState(boolean state) {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RestrictionPolicy restrictionPolicy = edm.getRestrictionPolicy();
        try {
            boolean result = restrictionPolicy.setCameraState(state);
            if (result) Log.d(TAG, "set Camera State success");
            else Log.d(TAG, "set Camera State fail");
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getCameraState() {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RestrictionPolicy restrictionPolicy = edm.getRestrictionPolicy();
        try {
            return restrictionPolicy.isCameraEnabled(false);
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return false;
        }
    }

    public int setScreenCaptureState(boolean state) {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RestrictionPolicy restrictionPolicy = edm.getRestrictionPolicy();
        try {
            boolean result = restrictionPolicy.setScreenCapture(state);
            if (result) Log.d(TAG, "set ScreenCapture State success");
            else Log.d(TAG, "set ScreenCapture State fail");
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getScreenCaptureState() {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RestrictionPolicy restrictionPolicy = edm.getRestrictionPolicy();
        try {
            return restrictionPolicy.isScreenCaptureEnabled(false);
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return false;
        }
    }

    public int setUninstallationState(boolean state, String[] packageNames) {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        ApplicationPolicy appPolicy = edm.getApplicationPolicy();

        try {
            if (packageNames == null || packageNames.length < 1) return ErrorHandleManager.ERROR_UNKNOWN;
            for (int i = 0; i < packageNames.length; i++) {
                if (packageNames[i].equals("")) return STATE_SUCCESS;
                if (state) appPolicy.setApplicationUninstallationEnabled(packageNames[i]);
                else appPolicy.setApplicationUninstallationDisabled(packageNames[i]);

                if (DSPreferencesManager.getInstance() == null) Svc.one.initPreferManager();
                DSPreferencesManager.getInstance().setBoolean(DSPreferencesConfig.IS_UNINSTALL_POLICY, state);
            }
            Log.d(TAG, "set Uninstallation State success");
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getUninstallationState() {
        try {
            if (DSPreferencesManager.getInstance() == null) Svc.one.initPreferManager();
            return DSPreferencesManager.getInstance().getBoolean(DSPreferencesConfig.IS_UNINSTALL_POLICY, true);
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return false;
        }
    }


    public int setWifiState(boolean state) {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RestrictionPolicy restrictionPolicy = edm.getRestrictionPolicy();

        try {
            boolean result = restrictionPolicy.allowWiFi(state);
            if (result) Log.d(TAG, "set Wifi State success");
            else Log.d(TAG, "set Wifi State fail");
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getWifiState() {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RestrictionPolicy restrictionPolicy = edm.getRestrictionPolicy();
        try {
            return restrictionPolicy.isWiFiEnabled(false);
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return false;
        }
    }

    public int setWifiDirectState(boolean state) {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RestrictionPolicy restrictionPolicy = edm.getRestrictionPolicy();

        try {
            boolean result = restrictionPolicy.allowWifiDirect(state);
            if (result) Log.d(TAG, "set Wifi Direct State success");
            else Log.d(TAG, "set Wifi Direct State fail");
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getWifiDirectState() {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RestrictionPolicy restrictionPolicy = edm.getRestrictionPolicy();
        try {
            return restrictionPolicy.isWifiDirectAllowed();
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return false;
        }
    }

    public int setBluetoothState(boolean state) {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        BluetoothPolicy bluetoothPolicy = edm.getBluetoothPolicy();
        try {
            boolean result;
            result = bluetoothPolicy.setPairingState(state); if (!result) return ErrorHandleManager.ERROR_UNKNOWN;
            result = bluetoothPolicy.setDiscoverableState(state); if (!result) return ErrorHandleManager.ERROR_UNKNOWN;
            result = bluetoothPolicy.setDesktopConnectivityState(state); if (!result) return ErrorHandleManager.ERROR_UNKNOWN;
            result = bluetoothPolicy.setAllowBluetoothDataTransfer(state); if (!result) return ErrorHandleManager.ERROR_UNKNOWN;

            boolean audioProfile = bluetoothPolicy.isProfileEnabled(BluetoothPolicy.BluetoothProfile.BLUETOOTH_AVRCP_PROFILE);
            boolean phoneBookProfile = bluetoothPolicy.isProfileEnabled(BluetoothPolicy.BluetoothProfile.BLUETOOTH_PBAP_PROFILE);
            boolean deviceRestriction = bluetoothPolicy.isBluetoothDeviceRestrictionActive();
            if(!audioProfile || !phoneBookProfile || deviceRestriction) {
                bluetoothPolicy.setProfileState(true, BluetoothPolicy.BluetoothProfile.BLUETOOTH_AVRCP_PROFILE);
                bluetoothPolicy.setProfileState(true, BluetoothPolicy.BluetoothProfile.BLUETOOTH_PBAP_PROFILE);
                bluetoothPolicy.activateBluetoothDeviceRestriction(false);
            }
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getBluetoothState() {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        BluetoothPolicy bluetoothPolicy = edm.getBluetoothPolicy();
        try {
            return bluetoothPolicy.isPairingEnabled();
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return true;
        }
    }

    public int setNFCState(boolean state) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)  return STATE_SUCCESS;
        try {
            EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
            NfcPolicy nfcPolicy = edm.getNfcPolicy();
            boolean result;
            if (!state) {
                result = nfcPolicy.startNFC(false);
                if (result) Log.d(TAG, " start NFC success");
                else Log.d(TAG, " start NFC fail");
            }

            result = nfcPolicy.allowNFCStateChange(state);
            if (result) Log.d(TAG, "set NFC State success");
            else Log.d(TAG, "set NFC State fail");

        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException : " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getNFCState() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)  return true;
        try {
            EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
            NfcPolicy nfcPolicy = edm.getNfcPolicy();
            return nfcPolicy.isNFCStateChangeAllowed();
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return false;
        }
    }

    public int setUsbContextState(boolean state) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)  return STATE_SUCCESS;
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RestrictionPolicy restrictionPolicy = edm.getRestrictionPolicy();
        try {
            boolean result = restrictionPolicy.setUsbMediaPlayerAvailability(state) && restrictionPolicy.setUsbTethering(state) && restrictionPolicy.allowUsbHostStorage(true);
            if (result) {
                Log.d(TAG, "set UsbContext State success");
                if (!state) {
                    result = restrictionPolicy.setUsbExceptionList(RestrictionPolicy.USBInterface.AUD.getValue() | RestrictionPolicy.USBInterface.HID.getValue());
                }
                else {
                    result = restrictionPolicy.setUsbExceptionList(RestrictionPolicy.USBInterface.OFF.getValue());
                }
                if (result) Log.d(TAG, "set Usb State success");
                else Log.d(TAG, "set Usb State fail");
            }
            else Log.d(TAG, "set UsbContext State fail");
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getUsbContextState() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)  return true;
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RestrictionPolicy restrictionPolicy = edm.getRestrictionPolicy();
        try {
            return restrictionPolicy.isUsbMediaPlayerAvailable(false) || restrictionPolicy.isUsbTetheringEnabled();
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return false;
        }
    }

    public int setUsbDebuggingState(boolean state) {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RestrictionPolicy restrictionPolicy = edm.getRestrictionPolicy();

        try {
            boolean result = restrictionPolicy.setUsbDebuggingEnabled(state);
            if (result) Log.d(TAG, "set Usb Debugging State success");
            else Log.d(TAG, "set Usb Debugging State fail");
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getUsbDebuggingState() {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RestrictionPolicy restrictionPolicy = edm.getRestrictionPolicy();
        try {
            return restrictionPolicy.isUsbDebuggingEnabled();
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return false;
        }
    }

    public int setTetheringState(boolean state) {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RestrictionPolicy restrictionPolicy = edm.getRestrictionPolicy();

        try {
            boolean result = restrictionPolicy.setTethering(state);
            if (result) Log.d(TAG, "set Tethering State success");
            else Log.d(TAG, "set Tethering State  fail");
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getTetheringState() {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RestrictionPolicy restrictionPolicy = edm.getRestrictionPolicy();
        try {
            return restrictionPolicy.isTetheringEnabled();
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return false;
        }
    }

    public int setSdcardAccessState(boolean state) {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RestrictionPolicy restrictionPolicy = edm.getRestrictionPolicy();

        try {
            boolean result = restrictionPolicy.setSdCardState(state);
            if (result) Log.d(TAG, "set Sdcared State success");
            else Log.d(TAG, "set Sdcared State fail");
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException : " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getSdcardAccessState() {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RestrictionPolicy restrictionPolicy = edm.getRestrictionPolicy();
        try {
            return restrictionPolicy.isSdCardEnabled();
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return false;
        }
    }

    public int setAirplaneModeState(boolean state) {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RestrictionPolicy restrictionPolicy = edm.getRestrictionPolicy();

        try {
            boolean result = restrictionPolicy.allowAirplaneMode(state);
            if (result) Log.d(TAG, "set AirplaneMode State success");
            else Log.d(TAG, "set AirplaneMode State fail");
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getAirplaneModeState() {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RestrictionPolicy restrictionPolicy = edm.getRestrictionPolicy();
        try {
            return restrictionPolicy.isAirplaneModeAllowed();
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return false;
        }
    }

    public int setMicrophoneState(boolean state) {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RestrictionPolicy restrictionPolicy = edm.getRestrictionPolicy();
        try {
            boolean result = restrictionPolicy.setMicrophoneState(state);
            if (result) Log.d(TAG, "set Microphone State success");
            else Log.d(TAG, "set Microphone State fail");
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getMicrophoneState() {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RestrictionPolicy restrictionPolicy = edm.getRestrictionPolicy();
        try {
            return restrictionPolicy.isMicrophoneEnabled(false);
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return false;
        }
    }

    public int setFactoryResetState(boolean state) {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RestrictionPolicy restrictionPolicy = edm.getRestrictionPolicy();
        try {
            boolean result = restrictionPolicy.allowFactoryReset(state);
            if (result) Log.d(TAG, "set FactoryReset State success");
            else Log.d(TAG, "set FactoryReset State fail");
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getFactoryResetState() {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RestrictionPolicy restrictionPolicy = edm.getRestrictionPolicy();
        try {
            return restrictionPolicy.isFactoryResetAllowed();
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return false;
        }
    }

    public int uninstallApplication(String packageName) {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        ApplicationPolicy applicationPolicy = edm.getApplicationPolicy();
        try {
            boolean result = applicationPolicy.uninstallApplication(packageName, false);
            if (true == result) {
                Log.d(TAG, "Uninstallation of an application package has been successful!");
            } else {
                Log.w(TAG, "Uninstallation of an application package has failed.");
                return errorhandler.getErrorState();
            }
        } catch (SecurityException E) {
            Log.e(TAG, "SecurityException: " + E);
            return errorhandler.getErrorState(E);
        }
        return STATE_SUCCESS;
    }

    public void setInstallationDisable() {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        ApplicationPolicy appPolicy = edm.getApplicationPolicy();
        try {
            for (String i : appList) {
                appPolicy.setApplicationInstallationDisabled(i);
            }
            Log.w(TAG, "setInstallationDisable result = True");
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
        }
    }

    public int setAdminRemovableState(boolean state) {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        try {
            boolean result = edm.setAdminRemovable(state, CommonUtil.AGENT_PKG_NAME);
            if (true == result) {
                Log.d(TAG, "setAdminRemovableState has been successful!");
            } else {
                Log.w(TAG, "setAdminRemovableState has failed.");
            }
        } catch (SecurityException E) {
            Log.e(TAG, "SecurityException: " + E);
            return errorhandler.getErrorState(E);
        }
        return STATE_SUCCESS;
    }

    public boolean getAdminRemovableState() {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        try {
            return edm.getAdminRemovable();
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return false;
        }
    }

    public int setDisableAppState(boolean state, List<String>apps) {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        ApplicationPolicy appPolicy = edm.getApplicationPolicy();
        try {
            if (state) {
                List<String> addedList = appPolicy.addPackagesToPreventStartBlackList(apps);
            } else {
                boolean result = appPolicy.removePackagesFromPreventStartBlackList(apps);
            }
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getDisableAppState() {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        ApplicationPolicy appPolicy = edm.getApplicationPolicy();
        try {
            List<String> pkgList = appPolicy.getPackagesFromPreventStartBlackList();
            if (pkgList.size() > 0) {
                return true;
            }
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
        }
        return false;
    }

    public int installApp(String packageName) {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        ApplicationPolicy appPolicy = edm.getApplicationPolicy();
        try {
            boolean result = appPolicy.installApplication(packageName, false);
            if (true == result) {
                Log.d(TAG, "Installing an application package has been successful!");
                return STATE_SUCCESS;
            } else {
                Log.w(TAG, "Installing an application package has failed.");
                return ErrorHandleManager.ERROR_UNKNOWN;
            }

        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
    }

    public int setGpsState(boolean state) {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        LocationPolicy locationPolicy = edm.getLocationPolicy();
        try {
            if (!locationPolicy.startGPS(state)) {
                Log.d(TAG, "setGPSDisable - startGPS failed");
            }
            if (!locationPolicy.setGPSStateChangeAllowed(state)) {
                Log.d(TAG, "setGPSDisable - setGPSStateChangeAllowed failed");
            }
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException : " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getGpsState() {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        LocationPolicy locationPolicy = edm.getLocationPolicy();
        try {
            return locationPolicy.isGPSOn();
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException : " + e);
            return false;
        }
    }

    public int setRoamingState(boolean state) {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RoamingPolicy roamingPolicy = edm.getRoamingPolicy();
        try {
            roamingPolicy.setRoamingData(state);
            roamingPolicy.setRoamingPush(state);
            roamingPolicy.setRoamingSync(state);
            roamingPolicy.setRoamingVoiceCalls(state);
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return errorhandler.getErrorState(e);
        }
        return STATE_SUCCESS;
    }

    public boolean getRoamingState() {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(context);
        RoamingPolicy roamingPolicy = edm.getRoamingPolicy();
        try {
            return roamingPolicy.isRoamingDataEnabled() && roamingPolicy.isRoamingPushEnabled()
                && roamingPolicy.isRoamingSyncEnabled() && roamingPolicy.isRoamingVoiceCallsEnabled();
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException: " + e);
            return false;
        }
    }
}

class ApduTimeoutRunnable implements Runnable {
    @Override
    public void run() {
        int count = 0;
        while(true){
            try {
                Thread.sleep(100);
                if (count++ > 100) {
                    KnoxManager.semaphore.release();
                    KnoxManager.semaphore.release();
                }
            } catch (InterruptedException e) {
                Log.d("ApduTimeoutRunnable", "InterruptedException");
                break;
            }
        }
    }
}
