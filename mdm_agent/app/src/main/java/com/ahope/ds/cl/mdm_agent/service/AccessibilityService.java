package com.ahope.ds.cl.mdm_agent.service;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.ahope.ds.cl.mdm_agent.common.preference.manager.DSPreferencesManager;
import com.ahope.ds.cl.mdm_agent.module.response.DsResponse;
import com.ahope.ds.cl.mdm_agent.module.whitelist.App;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccessibilityService extends android.accessibilityservice.AccessibilityService {
	private static final String TAG = "AccessibilityService";
	boolean isActiveWhiteListPolicy = false;
	String[] exceptionPolicy = null;
	PackageInfo packageInfo;
	Intent camera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	Intent dialer = new Intent(Intent.ACTION_DIAL);
	String defaultDialAppStr = "";
	String defaultCameraAppStr = "";
	List<String> currentPolicyList = null;

	public void onRebind(Intent intent) {
		super.onRebind(intent);
		Log.i("Agent", "--------onRebind--------");
	}

	public boolean onUnbind(Intent intent) {
		Log.i("Agent", "--------onUnbind--------");
		return super.onUnbind(intent);
	}

	public void onCreate() {
		super.onCreate();
		Log.i("Agent", "--------onCreate--------");
		ResolveInfo defaultCameraApp = null;
		ResolveInfo defaultDialApp = null;
		List<ResolveInfo> cameraList = getPackageManager().queryIntentActivities(camera, PackageManager.MATCH_DEFAULT_ONLY);
		List<ResolveInfo> dialerList = getPackageManager().queryIntentActivities(dialer, PackageManager.MATCH_DEFAULT_ONLY);

		if (cameraList != null) {
			defaultCameraApp = cameraList.get(0);
			defaultCameraAppStr = defaultCameraApp.activityInfo.packageName;
		}

		if (dialerList != null) {
			defaultDialApp = dialerList.get(0);
			defaultDialAppStr = defaultDialApp.activityInfo.packageName;
		}
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("Agent", "--------onStartCommand--------");

		int i = super.onStartCommand(intent, flags, startId);
		Log.e("Agent", "--------onStartCommand--------, i = " + i + ",START_NOT_STICKY=" + START_NOT_STICKY);

		return START_NOT_STICKY;
	}

	// 이벤트가 발생할때마다 실행되는 부분
	public void onAccessibilityEvent(AccessibilityEvent event) {
		if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
			Log.d(TAG, "event.getPackageName : " + event.getPackageName() + "\ncontent Change Type : " + event.getContentChangeTypes());

			if (event.getPackageName() == null) return;
			try {
				packageInfo = getPackageManager().getPackageInfo(String.valueOf(event.getPackageName()), PackageManager.GET_META_DATA);
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}

			if (Svc.one == null) {
				Log.e(TAG, "Svc is Null");
				Intent service = new Intent(getApplicationContext(), Svc.class);
				service.setPackage("com.ahope.ds.cl.mdm_agent.ISvc");
				startService(service);
				return;
			}

			if ((isSystemPackage(packageInfo.applicationInfo) && !event.getPackageName().equals(defaultCameraAppStr) &&
				!event.getPackageName().equals(defaultDialAppStr) &&
				!event.getPackageName().equals("com.skt.prod.dialer") &&
				!event.getPackageName().equals("com.google.android.apps.tachyon") &&
				!event.getPackageName().equals("com.samsung.android.arzone") &&
				!event.getPackageName().equals("com.samsung.android.incallui") &&
				!event.getPackageName().equals("com.lge.signboard") &&
				!event.getPackageName().equals("com.android.settings")) ||
				event.getContentChangeTypes() == AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_APPEARED ||
				event.getContentChangeTypes() == AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_DISAPPEARED)
				return;

			if (Svc.one.whiteList != null && Svc.one.whiteList.getPakageList() != null) {
				App whitelist[] = Svc.one.whiteList.getPakageList();
				if (whitelist.length < 1) return;
				for (App app : whitelist) {
					if (app == null) return;
					if (app.getPackageName().equals(event.getPackageName())) {
						String[] checkPolicyResult = checkPolicy(app.getEnableFunctions());
						if (checkPolicyResult == null) return;
						else exceptionPolicy = checkPolicyResult;
						DsResponse dsResponse = Svc.one.getPolicy();
						dsResponse.getPolicies();
						Svc.one.setPolicy(exceptionPolicy, false, new String[]{""}, true);
						isActiveWhiteListPolicy = true;
						return;
					}
				}

				if (isActiveWhiteListPolicy) {
					currentPolicyList = currentPolicy();
					List<String> exceptionPolicyList = new ArrayList(Arrays.asList(exceptionPolicy));

					exceptionPolicyList.retainAll(currentPolicyList);
					exceptionPolicy = exceptionPolicyList.toArray(new String[exceptionPolicyList.size()]);

					Svc.one.setPolicy(exceptionPolicy, true, new String[]{""}, true);
				}
				isActiveWhiteListPolicy = false;
			}
		}
	}

	public String[] checkPolicy(int policys) {
		List<String> result = new ArrayList<>();

		if ((policys & App.FLAG_CAMERA) == App.FLAG_CAMERA) {
			if (!Svc.one.mdmManager.getCameraState()) result.add(Svc.POLICY_CAMERA);
		}
		if ((policys & App.FLAG_CAPTURE) == App.FLAG_CAPTURE) {
			if (!Svc.one.mdmManager.getScreenCaptureState()) result.add(Svc.POLICY_CAPTURE);
		}
		if ((policys & App.FLAG_BLUETOOTH) == App.FLAG_BLUETOOTH) {
			if (!Svc.one.mdmManager.getBluetoothState()) result.add(Svc.POLICY_BLUETOOTH);
		}
		if ((policys & App.FLAG_TETHERING) == App.FLAG_TETHERING) {
			if (!Svc.one.mdmManager.getTetheringState()) result.add(Svc.POLICY_TETHERING);
		}
		if ((policys & App.FLAG_WIFI) == App.FLAG_WIFI) {
			if (!Svc.one.mdmManager.getWifiState()) result.add(Svc.POLICY_WIFI);
		}
		if ((policys & App.FLAG_WIFI_DIRECT) == App.FLAG_WIFI_DIRECT) {
			if (!Svc.one.mdmManager.getWifiDirectState()) result.add(Svc.POLICY_WIFI_DIRECT);
		}
		if ((policys & App.FLAG_USB) == App.FLAG_USB) {
			if (!Svc.one.mdmManager.getUsbState()) result.add(Svc.POLICY_USB);
		}
		if ((policys & App.FLAG_USB_DEBUGGING) == App.FLAG_USB_DEBUGGING) {
			if (!Svc.one.mdmManager.getUsbDebuggingState()) result.add(Svc.POLICY_USB_DEBUGGING);
		}
		if ((policys & App.FLAG_EXTERNAL_STORAGE) == App.FLAG_EXTERNAL_STORAGE) {
			if (!Svc.one.mdmManager.getSdcardAccessState())
				result.add(Svc.POLICY_EXTERNAL_STORAGE);
		}
		if ((policys & App.FLAG_MICROPHONE) == App.FLAG_MICROPHONE) {
			if (!Svc.one.mdmManager.getMicrophoneState()) result.add(Svc.POLICY_MICROPHONE);
		}
		if ((policys & App.FLAG_NFC) == App.FLAG_NFC) {
			if (!Svc.one.mdmManager.getNfcState()) result.add(Svc.POLICY_NFC);
		}
		if ((policys & App.FLAG_FACTORY_RESET) == App.FLAG_FACTORY_RESET) {
			if (!Svc.one.mdmManager.getFactoryResetState()) result.add(Svc.POLICY_FACTORY_RESET);
		}
		if ((policys & App.FLAG_APPS_FROM_UNKOWN_INSTALL) == App.FLAG_APPS_FROM_UNKOWN_INSTALL) {
			if (!Svc.one.mdmManager.getUnkhownAppState())
				result.add(Svc.POLICY_APPS_FROM_UNKOWN_INSTALL);
		}
		if ((policys & App.FLAG_GPS) == App.FLAG_GPS) {
			if (!Svc.one.mdmManager.getGpsState())
				result.add(Svc.POLICY_GPS);
		}
		if ((policys & App.FLAG_AIRPLANE_MODE) == App.FLAG_AIRPLANE_MODE) {
			if (!Svc.one.mdmManager.getAirPlaneModeState());
				result.add(Svc.POLICY_AIRPLANE_MODE);
		}
		if ((policys & App.FLAG_ROAMING) == App.FLAG_ROAMING) {
			if (!Svc.one.mdmManager.getRoamingState());
			result.add(Svc.POLICY_ROAMING);
		}
		if (result == null || result.size() == 0) return null;
		else return result.toArray(new String[result.size()]);
	}

	public List<String> currentPolicy() {
		List<String> policyList = new ArrayList<String>();
		try {
			JSONArray currentPolicyArray = new JSONArray(DSPreferencesManager.getInstance().getString("currentPolicy", ""));
			if (currentPolicyArray != null) {
				for (int i = 0; i < currentPolicyArray.length(); i++) {
					policyList.add(currentPolicyArray.get(i).toString());
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return policyList;
	}

	public void onServiceConnected() {
		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
		info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
		info.feedbackType = AccessibilityServiceInfo.DEFAULT | AccessibilityServiceInfo.FEEDBACK_HAPTIC;
		info.notificationTimeout = 100;
		setServiceInfo(info);
	}

	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub
		Log.e(TAG, "OnInterrupt");
	}

	private boolean isSystemPackage(ApplicationInfo applicationInfo) {
		return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
	}
}
