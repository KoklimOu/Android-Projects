package com.ahope.ds.cl.mdm_agent.service;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.ahope.ds.cl.mdm_agent.ISvc;
import com.ahope.ds.cl.mdm_agent.MainActivity;
import com.ahope.ds.cl.mdm_agent.R;
import com.ahope.ds.cl.mdm_agent.common.preference.config.DSPreferencesConfig;
import com.ahope.ds.cl.mdm_agent.common.preference.manager.BlackListPreferencesManager;
import com.ahope.ds.cl.mdm_agent.common.preference.manager.DSPreferencesManager;
import com.ahope.ds.cl.mdm_agent.common.preference.manager.RunnableWhiteListPreferencesManager;
import com.ahope.ds.cl.mdm_agent.common.preference.manager.WhiteListPreferencesManager;
import com.ahope.ds.cl.mdm_agent.common.util.CommonUtil;
import com.ahope.ds.cl.mdm_agent.manager.DeviceManager;
import com.ahope.ds.cl.mdm_agent.manager.ErrorHandleManager;
import com.ahope.ds.cl.mdm_agent.manager.KnoxManager;
import com.ahope.ds.cl.mdm_agent.manager.LGGATEManager;
import com.ahope.ds.cl.mdm_agent.manager.MdmManager;
import com.ahope.ds.cl.mdm_agent.module.receiver.LicenseReceiver;
import com.ahope.ds.cl.mdm_agent.module.response.DsResponse;
import com.ahope.ds.cl.mdm_agent.module.whitelist.App;
import com.ahope.ds.cl.mdm_agent.module.whitelist.RunnableWhiteList;
import com.ahope.ds.cl.mdm_agent.module.whitelist.WhiteList;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static androidx.core.app.NotificationCompat.PRIORITY_MIN;
import static com.ahope.ds.cl.mdm_agent.manager.ErrorHandleManager.ERROR_NOT_ACTIVE_ADMIN;
import static com.ahope.ds.cl.mdm_agent.manager.ErrorHandleManager.ERROR_NOT_ACTIVE_KNOX_LICENSE;

public class Svc extends Service {
	private static final String TAG = Svc.class.getSimpleName();
	public static final String POLICY_CAMERA = "camera";
	public static final String POLICY_CAPTURE = "capture";
	public static final String POLICY_BLUETOOTH = "bluetooth";
	public static final String POLICY_TETHERING = "tethering";
	public static final String POLICY_WIFI = "wifi";
	public static final String POLICY_WIFI_DIRECT = "wifiDirect";
	public static final String POLICY_USB = "usb";
	public static final String POLICY_USB_DEBUGGING = "usbDebugging";
	public static final String POLICY_EXTERNAL_STORAGE = "externalStorage";
	public static final String POLICY_MICROPHONE = "microphone";
	public static final String POLICY_NFC = "nfc";
	public static final String POLICY_APPS_FROM_UNKOWN_INSTALL = "appsFromUnkownSourceInstall";
	public static final String POLICY_FACTORY_RESET = "factoryReset";
	public static final String POLICY_UNINSTALL_APP = "uninstallApp";
	public static final String POLICY_REMOVABLE_ADMIN = "removableAdmin";
	public static final String POLICY_GPS = "gps";
	public static final String POLICY_AIRPLANE_MODE = "airplaneMode";
	public static final String POLICY_ROAMING = "roaming";

	public MdmManager mdmManager;
	private KnoxManager knoxManager;
	private LGGATEManager lgGateManager;
	public DeviceManager deviceManager;
	public WhiteList whiteList;
	public RunnableWhiteList runnableWhiteList;
	public static Svc one = null;
	public DsResponse dsResponse;
	LicenseReceiver licenseReceiver;
	PackageManager pm;
	List<String> runnableApps = new ArrayList<String>();

	public IBinder onBind(final Intent intent) {
		// Create the Foreground Service
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
		Notification notification = notificationBuilder.setOngoing(true)
			.setSmallIcon(R.mipmap.ic_launcher)
			.setPriority(PRIORITY_MIN)
			.setContentText("MDM Agent 구동중")
			.setCategory(NotificationCompat.CATEGORY_SERVICE)
			.build();
		startForeground(1, notification);
		return new ISvc.Stub() {
			public DsResponse setPolicy(String[] policys, boolean isDisable, String[] packageNames, String mdmPakageName) throws RemoteException {
				if (mdmPakageName != null) {
					initPreferManager();
					DSPreferencesManager.getInstance().setString(DSPreferencesConfig.MDM_PACKAGE_NAME, mdmPakageName);
				}
				return Svc.one.setPolicy(policys, isDisable, packageNames, false);
			}

			public DsResponse getPolicy() {
				return Svc.one.getPolicy();
			}

			public DsResponse activeLicense(boolean isSync) {
				return Svc.one.activeLicense(isSync);
			}

			public DsResponse isActiveLicense() { return Svc.one.isActiveLicense(); }

			public DsResponse activeAccessibility() {
				return Svc.one.activeAccessibility();
			}

			public DsResponse isActiveAccessibility() {
				return Svc.one.isActiveAccessibility();
			}

			public DsResponse activePermission() {
				return Svc.one.activePermission();
			}

			public DsResponse isActivePermission() {
				return Svc.one.isActivePermission();
			}

			public DsResponse setExceptionApps(App[] apps) {
				return Svc.one.setExceptionApps(apps);
			}

			public DsResponse getExceptionApps() {
				return Svc.one.getExceptionApps();
			}

			public DsResponse checkPassCodeSetting(){ return Svc.one.checkPassCodeSetting(); }

			public DsResponse getDeviceInfo() {
				return Svc.one.getDeviceInfo();
			}

			public DsResponse getAgentVersion() {
				return Svc.one.getAgentVersion();
			}

			public DsResponse activeAdmin() {
				return Svc.one.activeAdmin();
			}

			public DsResponse isActiveAdmin() {
				return Svc.one.isActiveAdmin();
			}

			public void addMessenger(Messenger messenger) {
				Svc.one.addMessenger(messenger);
			}
			public void removeMessenger(Messenger messenger) {
				Svc.one.removeMessenger(messenger);
			}

			public DsResponse disableRunnableApps(String mdmPakageName) throws RemoteException {
				return Svc.one.disableRunnableApps(mdmPakageName);
			}

			public DsResponse enableRunnableApps() throws RemoteException {
				return Svc.one.enableRunnableApps();
			}

			public DsResponse appendWhiteList(List<String> apps) throws RemoteException {
				return Svc.one.appendWhiteList(apps);
			}

			public DsResponse removeWhiteList(List<String> apps) throws RemoteException {
				return Svc.one.removeWhiteList(apps);
			}

			public DsResponse clearWhiteList() throws RemoteException {
				return Svc.one.clearWhiteList();
			}

			public DsResponse getRunnableApps() throws RemoteException {
				return Svc.one.getRunnableApps();
			}

			public DsResponse wipeData() throws RemoteException {
				return Svc.one.wipeData();
			}

			public DsResponse lockScreen() throws RemoteException {
				return Svc.one.lockScreen();
			}
			public DsResponse installApp(String packageName) throws RemoteException {
				return Svc.one.installApp(packageName);
			}

			public DsResponse unInstallApp(String packageName) throws RemoteException {
				return Svc.one.unInstallApp(packageName);
			}

			public DsResponse activeStoragePermission() throws RemoteException {
				return Svc.one.activeStoragePermission();
			}

			public DsResponse isActiveStoragePermission() throws RemoteException {
				return Svc.one.isActiveStoragePermission();
			}

			private IBinder mBinder; //Instance variable
			public void registerProcessDeath(IBinder clientDeathListener) {
				Log.d("Svc", "SvcServer, registerProcessDeath, call ");
				mBinder = clientDeathListener;
				try {
					clientDeathListener.linkToDeath(new IBinder.DeathRecipient() {
						@Override
						public void binderDied() {
							Log.d("Svc", "SvcServer, linkToDeath, call ");
							mBinder.unlinkToDeath(this,0);
						}
					},0);
				} catch (RemoteException e) {
					Log.e("Svc", "SvcServer, registerProcessDeath, remote exception:" + e.getMessage());
				}
			}
		};
	}

	public DsResponse setPolicy(String[] policys, boolean isDisable, String[] packageNames, boolean runFromAccessibility) {
		dsResponse = new DsResponse();
		boolean state = !isDisable;
		int errorCode = 0;

		if (mdmManager.isActiveAdmin() == false) {
			activeAdmin();
			errorCode = ERROR_NOT_ACTIVE_ADMIN;
			dsResponse.setErrorCode(errorCode);
			return dsResponse;
		}
		else if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG) &&
			DSPreferencesManager.getInstance().getBoolean(DSPreferencesConfig.IS_ACTIVATE_KNOX, false) == false) {
			activeLicense(false);
			errorCode = ERROR_NOT_ACTIVE_KNOX_LICENSE;
			dsResponse.setErrorCode(errorCode);
			return dsResponse;
		}
		AccessibilityService accessibilityService = new AccessibilityService();
		List<String> policyList = accessibilityService.currentPolicy();

		for (int i = 0; i < policys.length; i++) {
			if (policys[i] == null) break;
			switch (policys[i]) {
				case POLICY_CAMERA:
					errorCode = mdmManager.setCameraState(state);
					if (errorCode == CommonUtil.STATE_SUCCESS) {
						if (!state && !policyList.contains(POLICY_CAMERA)) policyList.add(POLICY_CAMERA);
						else if(state && policyList.contains(POLICY_CAMERA)) policyList.remove(POLICY_CAMERA);
					}
					break;
				case POLICY_CAPTURE:
					errorCode = mdmManager.setScreenCaptureState(state);
					if (errorCode == CommonUtil.STATE_SUCCESS) {
						if (!state && !policyList.contains(POLICY_CAPTURE)) policyList.add(POLICY_CAPTURE);
						else if(state && policyList.contains(POLICY_CAPTURE)) policyList.remove(POLICY_CAPTURE);
					}
					break;
				case POLICY_BLUETOOTH:
					errorCode = mdmManager.setBluetoothState(state);
					if (errorCode == CommonUtil.STATE_SUCCESS) {
						if (!state && !policyList.contains(POLICY_BLUETOOTH)) policyList.add(POLICY_BLUETOOTH);
						else if(state && policyList.contains(POLICY_BLUETOOTH)) policyList.remove(POLICY_BLUETOOTH);
					}
					break;
				case POLICY_TETHERING:
					errorCode = mdmManager.setTetheringState(state);
					if (errorCode == CommonUtil.STATE_SUCCESS) {
						if (!state && !policyList.contains(POLICY_TETHERING)) policyList.add(POLICY_TETHERING);
						else if(state && policyList.contains(POLICY_TETHERING)) policyList.remove(POLICY_TETHERING);
					}
					break;
				case POLICY_WIFI:
					errorCode = mdmManager.setWifiState(state);
					if (errorCode == CommonUtil.STATE_SUCCESS) {
						if (!state && !policyList.contains(POLICY_WIFI)) policyList.add(POLICY_WIFI);
						else if(state && policyList.contains(POLICY_WIFI)) policyList.remove(POLICY_WIFI);
					}
					break;
				case POLICY_WIFI_DIRECT:
					errorCode = mdmManager.setWifiDirectState(state);
					if (errorCode == CommonUtil.STATE_SUCCESS) {
						if (!state && !policyList.contains(POLICY_WIFI_DIRECT)) policyList.add(POLICY_WIFI_DIRECT);
						else if(state && policyList.contains(POLICY_WIFI_DIRECT)) policyList.remove(POLICY_WIFI_DIRECT);
					}
					break;
				case POLICY_USB:
					errorCode = mdmManager.setUsbState(state);
					if (errorCode == CommonUtil.STATE_SUCCESS) {
						if (!state && !policyList.contains(POLICY_USB)) policyList.add(POLICY_USB);
						else if(state && policyList.contains(POLICY_USB)) policyList.remove(POLICY_USB);
					}
					break;
				case POLICY_USB_DEBUGGING:
					errorCode = mdmManager.setUsbDebuggingState(state);
					if (errorCode == CommonUtil.STATE_SUCCESS) {
						if (!state && !policyList.contains(POLICY_USB_DEBUGGING)) policyList.add(POLICY_USB_DEBUGGING);
						else if(state && policyList.contains(POLICY_USB_DEBUGGING)) policyList.remove(POLICY_USB_DEBUGGING);
					}
					break;
				case POLICY_EXTERNAL_STORAGE:
					errorCode = mdmManager.setSdcardAccessState(state);
					if (errorCode == CommonUtil.STATE_SUCCESS) {
						if (!state && !policyList.contains(POLICY_EXTERNAL_STORAGE)) policyList.add(POLICY_EXTERNAL_STORAGE);
						else if(state && policyList.contains(POLICY_EXTERNAL_STORAGE)) policyList.remove(POLICY_EXTERNAL_STORAGE);
					}
					break;
				case POLICY_MICROPHONE:
					errorCode = mdmManager.setMicrophoneState(state);
					if (errorCode == CommonUtil.STATE_SUCCESS) {
						if (!state && !policyList.contains(POLICY_MICROPHONE)) policyList.add(POLICY_MICROPHONE);
						else if(state && policyList.contains(POLICY_MICROPHONE)) policyList.remove(POLICY_MICROPHONE);
					}
					break;
				case POLICY_NFC:
					errorCode = mdmManager.setNfcState(state);
					if (errorCode == CommonUtil.STATE_SUCCESS) {
						if (!state && !policyList.contains(POLICY_NFC)) policyList.add(POLICY_NFC);
						else if(state && policyList.contains(POLICY_NFC)) policyList.remove(POLICY_NFC);
					}
					break;
				case POLICY_FACTORY_RESET:
					errorCode = mdmManager.setFactoryResetState(state);
					if (errorCode == CommonUtil.STATE_SUCCESS) {
						if (!state && !policyList.contains(POLICY_FACTORY_RESET)) policyList.add(POLICY_FACTORY_RESET);
						else if(state && policyList.contains(POLICY_FACTORY_RESET)) policyList.remove(POLICY_FACTORY_RESET);
					}
					break;
				case POLICY_APPS_FROM_UNKOWN_INSTALL:
					errorCode = mdmManager.setUnkhownAppState(state);
					if (errorCode == CommonUtil.STATE_SUCCESS) {
						if (!state && !policyList.contains(POLICY_APPS_FROM_UNKOWN_INSTALL)) policyList.add(POLICY_APPS_FROM_UNKOWN_INSTALL);
						else if(state && policyList.contains(POLICY_APPS_FROM_UNKOWN_INSTALL)) policyList.remove(POLICY_APPS_FROM_UNKOWN_INSTALL);
					}
					break;
				case POLICY_UNINSTALL_APP:
					errorCode = mdmManager.setUninstallApplicationState(state, packageNames);
					if (errorCode == CommonUtil.STATE_SUCCESS) {
						if (!state && !policyList.contains(POLICY_UNINSTALL_APP)) policyList.add(POLICY_UNINSTALL_APP);
						else if(state && policyList.contains(POLICY_UNINSTALL_APP)) policyList.remove(POLICY_UNINSTALL_APP);
					}
					break;
				case POLICY_REMOVABLE_ADMIN:
					errorCode = mdmManager.setAdminRemovableState(state);
					if (errorCode == CommonUtil.STATE_SUCCESS) {
						if (!state && !policyList.contains(POLICY_REMOVABLE_ADMIN)) policyList.add(POLICY_REMOVABLE_ADMIN);
						else if(state && policyList.contains(POLICY_REMOVABLE_ADMIN)) policyList.remove(POLICY_REMOVABLE_ADMIN);
					}
					break;
				case POLICY_GPS:
					errorCode = mdmManager.setGpsState(state);
					if (errorCode == CommonUtil.STATE_SUCCESS) {
						if (!state && !policyList.contains(POLICY_GPS)) policyList.add(POLICY_GPS);
						else if(state && policyList.contains(POLICY_GPS)) policyList.remove(POLICY_GPS);
					}
					break;
				case POLICY_AIRPLANE_MODE:
					errorCode = mdmManager.setAirplaneModeState(state);
					if (errorCode == CommonUtil.STATE_SUCCESS) {
						if (!state && !policyList.contains(POLICY_AIRPLANE_MODE)) policyList.add(POLICY_AIRPLANE_MODE);
						else if(state && policyList.contains(POLICY_AIRPLANE_MODE)) policyList.remove(POLICY_AIRPLANE_MODE);
					}
					break;
				case POLICY_ROAMING:
					errorCode = mdmManager.setRoamingState(state);
					if (errorCode == CommonUtil.STATE_SUCCESS) {
						if (!state && !policyList.contains(POLICY_ROAMING)) policyList.add(POLICY_ROAMING);
						else if(state && policyList.contains(POLICY_ROAMING)) policyList.remove(POLICY_ROAMING);
					}
					break;
				default:
					errorCode = ErrorHandleManager.ERROR_UNKNOWN;
			}
			if (errorCode != CommonUtil.STATE_SUCCESS) { break; }
		}
		if (!runFromAccessibility)
			DSPreferencesManager.getInstance().setString("currentPolicy", policyList.toString());

		try {
			if (errorCode == ERROR_NOT_ACTIVE_KNOX_LICENSE) {
				if (DSPreferencesManager.getInstance() == null) Svc.one.initPreferManager();
				DSPreferencesManager.getInstance().setBoolean(DSPreferencesConfig.IS_ACTIVATE_KNOX, false);
			}
		}
		catch (Exception e) {
			Log.w("Svc", e.getMessage());
		}

		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	public DsResponse getPolicy() {
		dsResponse = new DsResponse();
		String[] result = new String[20];
		int errorCode = 0;
		int index = 0;
		try {
			if (!mdmManager.getCameraState()) {Log.d("Svc", "POLICY_CAMERA"); result[index] =  POLICY_CAMERA; index++;}
			if (!mdmManager.getScreenCaptureState()) {Log.d("Svc", "POLICY_CAPTURE"); result[index] =  POLICY_CAPTURE; index++;}
			if (!mdmManager.getBluetoothState()) {Log.d("Svc", "POLICY_BLUETOOTH"); result[index] =  POLICY_BLUETOOTH; index++;}
			if (!mdmManager.getTetheringState()) {Log.d("Svc", "POLICY_TETHERING"); result[index] =  POLICY_TETHERING; index++;}
			if (!mdmManager.getWifiState()) {Log.d("Svc", "POLICY_WIFI"); result[index] =  POLICY_WIFI; index++;}
			if (!mdmManager.getWifiDirectState()) {Log.d("Svc", "POLICY_WIFI_DIRECT"); result[index] =  POLICY_WIFI_DIRECT; index++;}
			if (!mdmManager.getUsbState()) {Log.d("Svc", "POLICY_USB"); result[index] =  POLICY_USB; index++;}
			if (!mdmManager.getUsbDebuggingState()) {Log.d("Svc", "POLICY_USB_DEBUGGING"); result[index] =  POLICY_USB_DEBUGGING; index++;}
			if (!mdmManager.getSdcardAccessState()) {Log.d("Svc", "POLICY_EXTERNALSTORAGE"); result[index] = POLICY_EXTERNAL_STORAGE; index++;}
			if (!mdmManager.getMicrophoneState()) {Log.d("Svc", "POLICY_MICROPHONE"); result[index] =  POLICY_MICROPHONE; index++;}
			if (!mdmManager.getNfcState()) {Log.d("Svc", "POLICY_NFC"); result[index] =  POLICY_NFC; index++;}
			if (!mdmManager.getFactoryResetState()) {Log.d("Svc", "POLICY_FACTORY_RESET"); result[index] =  POLICY_FACTORY_RESET; index++;}
			if (!mdmManager.getUnkhownAppState()) {Log.d("Svc", "POLICY_APPS_FROM_UNKOWNINSTALL"); result[index] = POLICY_APPS_FROM_UNKOWN_INSTALL; index++;}
			if (!mdmManager.getUninstallApplicationState()) {Log.d("Svc", "POLICY_UNINSTALL_APP"); result[index] = POLICY_UNINSTALL_APP; index++;}
			if (!mdmManager.getAdminRemovableState()) {Log.d("Svc", "POLICY_REMOVABLE_ADMIN"); result[index] = POLICY_REMOVABLE_ADMIN; index++;}
			if (!mdmManager.getGpsState()) {Log.d("Svc", "POLICY_GPS"); result[index] = POLICY_GPS; index++;}
			if (!mdmManager.getAirPlaneModeState()) {Log.d("Svc", "POLICY_AIRPLANE_MODE"); result[index] = POLICY_AIRPLANE_MODE; index++;}
			if (!mdmManager.getRoamingState()) {Log.d("Svc", "POLICY_ROAMING"); result[index] = POLICY_ROAMING; index++;}
		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
		}

		if (index != 0) dsResponse.setPolicies(result);
		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	public DsResponse activeLicense(boolean isSync) {
		dsResponse = new DsResponse();
		int errorCode = 0;

		if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) {
			dsResponse.setErrorCode(errorCode);
			return dsResponse;
		}

		try {
			if (isSync) errorCode = mdmManager.setActiveKnoxLicenseSync();
			else errorCode = mdmManager.setActiveKnoxLicense();
		} catch (Exception e) {
			Log.d("Svc", e.getMessage());
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
		}
		Log.e("License Code", String.valueOf(errorCode));
		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	public DsResponse isActiveLicense() {
		dsResponse = new DsResponse();
		int errorCode = 0;
		boolean result = false;

		if (Build.MANUFACTURER.equals(CommonUtil.BRAND_LG)) {
			dsResponse.setBool(true);
			dsResponse.setErrorCode(0);
			return dsResponse;
		}

		try {
			if (DSPreferencesManager.getInstance() == null) {
				errorCode = ErrorHandleManager.ERROR_UNKNOWN;
			}
			else {
				result = DSPreferencesManager.getInstance().getBoolean(DSPreferencesConfig.IS_ACTIVATE_KNOX, false);
			}
		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
		}
		dsResponse.setBool(result);
		dsResponse.setErrorCode(errorCode);

		return dsResponse;
	}

	public DsResponse activeAdmin() {
		dsResponse = new DsResponse();
		int errorCode = 0;

		try {
			if (mdmManager.isActiveAdmin()) {
				errorCode = CommonUtil.stateDeviceAdmin = CommonUtil.STATE_SUCCESS;
				dsResponse.setErrorCode(errorCode);
				return dsResponse;
			}
			errorCode = CommonUtil.stateDeviceAdmin = CommonUtil.STATE_DENIED;
			Intent intent = new Intent(getApplication(), MainActivity.class);
			intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
			intent.putExtra("isCallFromDeviceAdminActivation", true);
			startActivity(intent);
		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
		}

		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	public DsResponse isActiveAdmin() {
		dsResponse = new DsResponse();
		int errorCode = 0;
		boolean result = false;

		try {
			result = mdmManager.isActiveAdmin();
			errorCode = CommonUtil.stateDeviceAdmin;
		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
		}
		dsResponse.setBool(result);
		dsResponse.setErrorCode(errorCode);

		return dsResponse;
	}

	public DsResponse activeAccessibility() {
		dsResponse = new DsResponse();
		int errorCode = mdmManager.setAccessibilityPermissions();
		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	public DsResponse isActiveAccessibility() {
		dsResponse = new DsResponse();
		int errorCode = 0;
		boolean result = false;

		try {
			result= mdmManager.checkAccessibilityPermissions();
		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
		}

		dsResponse.setBool(result);
		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	public DsResponse activePermission() {
		dsResponse = new DsResponse();
		int errorCode = 0;
		try {
			if (android.os.Build.VERSION.SDK_INT >= 23) {
				CommonUtil.stateGrantPermissions = CommonUtil.STATE_SUCCESS;
				for (String permission : CommonUtil.RUNTIME_PERMS_LIST) {
					if (!(ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED)) {
						CommonUtil.stateGrantPermissions = CommonUtil.STATE_DENIED;
						Intent intent = new Intent(getApplication(), MainActivity.class);
						intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
						intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
						intent.putExtra("isCallFromActivePermission", true);
						startActivity(intent);
					}
				}
			}
		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
		}
		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	@TargetApi(23)
	public DsResponse isActivePermission() {
		dsResponse = new DsResponse();
		int errorCode = 0;
		try {
			dsResponse.setBool(false);
			if(CommonUtil.stateGrantPermissions == CommonUtil.STATE_SUCCESS) dsResponse.setBool(true);
			else if(CommonUtil.stateGrantPermissions == CommonUtil.STATE_USER_REJECTED) errorCode = CommonUtil.STATE_USER_REJECTED;
			//테스트로 수정
			else if(CommonUtil.stateGrantPermissions == CommonUtil.STATE_KEEP_REJECTED) errorCode = CommonUtil.STATE_KEEP_REJECTED;
		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
		}
		dsResponse.setErrorCode(errorCode);
		CommonUtil.stateGrantPermissions = CommonUtil.STATE_DENIED;
		return dsResponse;
	}

	public DsResponse setExceptionApps(App[] apps) {
		dsResponse = new DsResponse();
		int errorCode = 0;
		int result = 0;
		try {
			if (whiteList == null) {
				errorCode = ErrorHandleManager.ERROR_UNKNOWN;
			}
			else {
				for (int i = 0; i < apps.length; i++) {
					if (apps[i] == null) continue;
					result = whiteList.verifySig(apps[i]);

					if (result != CommonUtil.STATE_SUCCESS) {
						if (result != ErrorHandleManager.ERROR_PACKAGE_NOT_FOUND) {
							dsResponse.setErrorCode(result);
							return dsResponse;
						} else {
							errorCode = result;
						}
					}
				}

				if (!whiteList.setPakageList(apps)) {
					errorCode = ErrorHandleManager.ERROR_UNKNOWN;
				}
			}
		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
		}

		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	public DsResponse getExceptionApps() {
		dsResponse = new DsResponse();
		int errorCode = 0;
		boolean result = false;

		try {
			if (whiteList == null) errorCode = ErrorHandleManager.ERROR_UNKNOWN;
			dsResponse.setApps(whiteList.getPakageList());
		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
		}
		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	public DsResponse checkPassCodeSetting() {
		dsResponse = new DsResponse();
		int errorCode = 0;
		boolean result = false;

		try {
			result = deviceManager.checkPassCodeSetting();

		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
		}

		dsResponse.setBool(result);
		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	public DsResponse getDeviceInfo() {
		dsResponse = new DsResponse();
		int errorCode = 0;
		HashMap<String, String> map;

		try {
			map = deviceManager.getDeviceInfo();
			dsResponse.setDeviceInfo(map);
		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
		}
		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	public DsResponse getAgentVersion() {
		dsResponse = new DsResponse();
		int errorCode = 0;
		try {
			dsResponse.setAgentVersion(CommonUtil.AGENT_VERSION);
		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
		}
		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	final ArrayList<Messenger> messengers = new ArrayList<Messenger>();
	void addMessenger(Messenger messenger) {
		Log.d("Svc", "Svc.addMessenger()");
		synchronized (messengers) {
			messengers.add(messenger);
		}
	}

	void removeMessenger(Messenger messenger) {
		Log.d("Svc", "Svc.removeMessenger()");
		synchronized(messengers) {
			messengers.remove(messenger);
		}
	}

	public DsResponse disableRunnableApps(String mdmPackageName) {
		dsResponse = new DsResponse();
		int errorCode = 0;
		boolean result = false;
		pm = getPackageManager();
		PackageManager pm = getPackageManager();
		List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		List<String> notRunnableApps = new ArrayList<String>();

		runnableApps = getPakageList();

		for (ApplicationInfo packageInfo : packages) {
			if(!isSystemPackage(packageInfo))
				notRunnableApps.add(packageInfo.packageName);
		}
		notRunnableApps.removeAll(runnableApps);
		notRunnableApps.remove(mdmPackageName);
		try {
			for (int i = 0; i < notRunnableApps.size(); i++) {
				if (notRunnableApps.get(i) == null) continue;
				result = isPackageInstalled(notRunnableApps.get(i), pm);

				if (!result) {
					dsResponse.setErrorCode(ErrorHandleManager.ERROR_PACKAGE_NOT_FOUND);
					return dsResponse;
				}
			}

			errorCode = mdmManager.setDisableAppState(true, notRunnableApps);
		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
		}

		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	public DsResponse enableRunnableApps() {
		dsResponse = new DsResponse();
		int errorCode = 0;
		boolean result = false;

		pm = getPackageManager();
		PackageManager pm = getPackageManager();
		List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		List<String> notRunnableApps = new ArrayList<String>();

		runnableApps = getPakageList();

		for (ApplicationInfo packageInfo : packages) {
			if(!isSystemPackage(packageInfo))
				notRunnableApps.add(packageInfo.packageName);
		}
		notRunnableApps.removeAll(runnableApps);

		try {
			for (int i = 0; i < notRunnableApps.size(); i++) {
				if (notRunnableApps.get(i) == null) continue;
				result = isPackageInstalled(notRunnableApps.get(i), pm);

				if (!result) {
					dsResponse.setErrorCode(ErrorHandleManager.ERROR_PACKAGE_NOT_FOUND);
					return dsResponse;
				}
			}

			errorCode = mdmManager.setDisableAppState(false, notRunnableApps);
		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
		}

		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	public DsResponse appendWhiteList(List<String> apps) {
		dsResponse = new DsResponse();
		int errorCode = 0;

		try {
			runnableApps = getPakageList();
			if (runnableApps.size() != 0) {
				for (int i = 0; i < apps.size(); i++) {
					if (apps.get(i) == null) continue;
					if (!runnableApps.contains(apps.get(i))) runnableApps.add(apps.get(i));
				}
				RunnableWhiteListPreferencesManager.getInstance().setString("runnableWhiteList", runnableApps.toString());
			}else {
				RunnableWhiteListPreferencesManager.getInstance().setString("runnableWhiteList", apps.toString());
			}

		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
		}

		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	public DsResponse removeWhiteList(List<String> apps) {
		dsResponse = new DsResponse();
		int errorCode = 0;

		try {
			runnableApps = getPakageList();
			if (runnableApps.size() != 0) {
				for (int i = 0; i < apps.size(); i++) {
					if (apps.get(i) == null) continue;
					runnableApps.remove(apps.get(i));
				}
				RunnableWhiteListPreferencesManager.getInstance().setString("runnableWhiteList", runnableApps.toString());
			}else {
				errorCode = ErrorHandleManager.ERROR_UNKNOWN;
			}

		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
		}

		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	public DsResponse clearWhiteList() {
		dsResponse = new DsResponse();
		int errorCode = 0;

		try {
			RunnableWhiteListPreferencesManager.getInstance().removeAll();
		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
		}

		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	public DsResponse getRunnableApps() {
		dsResponse = new DsResponse();
		int errorCode = 0;
		List<String> runnableApps = new ArrayList<String>();
		try {
			runnableApps = getPakageList();
		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
		}
		dsResponse.setRunnableApps(runnableApps);
		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	public DsResponse wipeData() {
		dsResponse = new DsResponse();
		int errorCode = 0;

		try {
			errorCode = mdmManager.wipeData();
		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
		}

		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	public DsResponse lockScreen() {
		dsResponse = new DsResponse();
		int errorCode = 0;

		try {
			errorCode = mdmManager.lockScreen();
		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
			e.printStackTrace();
		}

		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	public DsResponse installApp(String packageName) {
		dsResponse = new DsResponse();
		int errorCode = 0;

		try {
			errorCode = mdmManager.installApp(packageName);
		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
			e.printStackTrace();
		}

		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	public DsResponse unInstallApp(String packageName) {
		dsResponse = new DsResponse();
		int errorCode = 0;

		try {
			errorCode = mdmManager.unInstallApp(packageName);
		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
			e.printStackTrace();
		}

		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	public DsResponse activeStoragePermission() {
		dsResponse = new DsResponse();
		int errorCode = 0;
		try {
			for(String storagePermission : CommonUtil.RUNTIME_STORAGE_PERMS_LIST) {
				if (ActivityCompat.checkSelfPermission(this, storagePermission) != PackageManager.PERMISSION_GRANTED) {
					Intent intent = new Intent(getApplication(), MainActivity.class);
					intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("isCallFromActiveStoragePermission", true);
					startActivity(intent);
				}
			}
		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
		}
		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	public DsResponse isActiveStoragePermission() {
		dsResponse = new DsResponse();
		int errorCode = 0;
		try {
			dsResponse.setBool(true);
			for(String storagePermission : CommonUtil.RUNTIME_STORAGE_PERMS_LIST) {
				if (ActivityCompat.checkSelfPermission(this, storagePermission) != PackageManager.PERMISSION_GRANTED) {
					dsResponse.setBool(false);
				}
			}
		} catch (Exception e) {
			errorCode = ErrorHandleManager.ERROR_UNKNOWN;
		}
		dsResponse.setErrorCode(errorCode);
		return dsResponse;
	}

	private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
		try {
			packageManager.getPackageInfo(packageName, 0);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}
	private boolean isSystemPackage(ApplicationInfo applicationInfo) {
		return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
	}

	public List<String> getPakageList() {
		List<String> runnableWhiteList = new ArrayList<String>();
		try {
			JSONArray jsonArray = new JSONArray(RunnableWhiteListPreferencesManager.getInstance().getString("runnableWhiteList", ""));
			if (jsonArray != null) {
				for (int i = 0; i < jsonArray.length(); i++) {
					runnableWhiteList.add(jsonArray.get(i).toString());
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return runnableWhiteList;
	}

	public synchronized void onCreate() {
		super.onCreate();
		initPreferManager();
		deviceManager = new DeviceManager(getApplicationContext());
		mdmManager = new MdmManager(getApplication());

		licenseReceiver = new LicenseReceiver();

		if (Build.MANUFACTURER.equals(CommonUtil.BRAND_SAMSUNG)) {
			IntentFilter filter = new IntentFilter();
			filter.addAction("com.samsung.android.knox.intent.action.KNOX_LICENSE_STATUS");
			registerReceiver(licenseReceiver, filter);
		}

		whiteList = new WhiteList(getApplicationContext());
		startSvc();
		one = this;
	}

	public void startSvc() {
		Intent service = new Intent(getApplicationContext(), Svc.class);
		service.setPackage("com.ahope.ds.cl.mdm_agent.ISvc");
//		startService(service);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			startForegroundService(service);
		} else {
			startService(service);
		}
	}

	public void stopSvc() {
		Intent service = new Intent(getApplicationContext(), Svc.class);
		service.setPackage("com.ahope.ds.cl.mdm_agent.ISvc");
		stopService(service);
	}

	public void initPreferManager() {
		if (DSPreferencesManager.getInstance() == null) DSPreferencesManager.initializeInstance(getApplicationContext());
		if (WhiteListPreferencesManager.getInstance() == null) WhiteListPreferencesManager.initializeInstance(getApplicationContext());
		if (BlackListPreferencesManager.getInstance() == null) BlackListPreferencesManager.initializeInstance(getApplicationContext());
		if (RunnableWhiteListPreferencesManager.getInstance() == null) RunnableWhiteListPreferencesManager.initializeInstance(getApplicationContext());
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e("Svc", "onStartCommand");
		return START_STICKY;
	}

	public synchronized void onDestroy() {
		super.onDestroy();
		unregisterReceiver(licenseReceiver);
		Log.e("Svc", "Svc.onDestroy");
		one = null;
	}

	@RequiresApi(Build.VERSION_CODES.O)
	private String createNotificationChannel(NotificationManager notificationManager){
		String channelId = "mdmAgent_channelid";
		String channelName = "MDM Agent Service";
		NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_MIN);
		// omitted the LED color
//		channel.setImportance(NotificationManager.IMPORTANCE_NONE);
//		channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
		notificationManager.createNotificationChannel(channel);
		return channelId;
	}
}
