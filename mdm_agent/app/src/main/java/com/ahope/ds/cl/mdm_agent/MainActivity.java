package com.ahope.ds.cl.mdm_agent;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import com.ahope.ds.cl.mdm_agent.common.util.CommonUtil;
import com.ahope.ds.cl.mdm_agent.manager.MdmManager;
import com.ahope.ds.cl.mdm_agent.module.receiver.AdminReceiver;

import java.util.HashSet;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static final String[] REQUIRED_STORAGE_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE/*, Manifest.permission.WRITE_EXTERNAL_STORAGE*/};
    private static final int REQUEST_PROVISION_MANAGED_PROFILE = 1;
    DevicePolicyManager dpm;
    ComponentName mDeviceAdmin;
    boolean isCallFromDeviceAdminActivation = false;
    boolean isCallFromActivePermission = false;
    boolean isCallFromActiveStoragePermission = false;
    boolean isCallFromSvcOncreate = false;
    boolean isWaitAdminActivation = false;
    boolean isWaitPermission = false;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent = getIntent();
        isCallFromDeviceAdminActivation = intent.getBooleanExtra("isCallFromDeviceAdminActivation", false);
        isCallFromSvcOncreate = intent.getBooleanExtra("isCallFromSvcOncreate", false);
        isCallFromActivePermission = intent.getBooleanExtra("isCallFromActivePermission", false);
        isCallFromActiveStoragePermission = intent.getBooleanExtra("isCallFromActiveStoragePermission", false);

        if (isCallFromDeviceAdminActivation) {
            activeAdmin();
            isWaitAdminActivation = true;
            isCallFromDeviceAdminActivation = false;
        }
        else if (isCallFromActivePermission) {
            activePermissions();
            isWaitPermission = true;
            isCallFromActivePermission = false;
        }
        else if (isCallFromActiveStoragePermission) {
            activeStoragePermissions();
            isWaitPermission = true;
            isCallFromActiveStoragePermission = false;
        }
        else if (isCallFromSvcOncreate) {
            moveTaskToBack(true);
        }
        else {
            Toast.makeText(getApplication(), "MDM Agent는 백그라운드 전용 앱입니다", Toast.LENGTH_LONG).show();
            moveTaskToBack(true);
        }
    }

    public void activeStoragePermissions() {
        for(String storagePermission : CommonUtil.RUNTIME_STORAGE_PERMS_LIST) {
            if (ActivityCompat.checkSelfPermission(this, storagePermission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, REQUIRED_STORAGE_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    public void activePermissions() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            HashSet<String> REQUEST_PERMS_LIST = new HashSet<String>();
            for (String permission : CommonUtil.RUNTIME_PERMS_LIST) {
                if (!(ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED)) {
                    REQUEST_PERMS_LIST.add(permission);
                }
            }
            requestPermissions(REQUEST_PERMS_LIST.toArray(new String[REQUEST_PERMS_LIST.size()]), PERMISSIONS_REQUEST_CODE);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissions() {
        requestPermissions(CommonUtil.RUNTIME_PERMS_LIST.toArray(new String[CommonUtil.RUNTIME_PERMS_LIST.size()]),
            PERMISSIONS_REQUEST_CODE);
    }

    public void activeAdmin() {
        dpm = ((DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE));
        mDeviceAdmin = new ComponentName(getApplicationContext(), AdminReceiver.class);
        boolean adminActive = dpm.isAdminActive(mDeviceAdmin);
        if (!adminActive) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getApplication().getString(R.string.amdm_explanation_on_activating_device_admin));
            startActivityForResult(intent, 1);
        }
    }

    public void onStart() {
        super.onStart();
    }

    public void onRestart() {
        super.onRestart();
    }

    public void onResume() {
        super.onResume();
        if (!isWaitAdminActivation && !isWaitPermission) {
            moveTaskToBack(true);
        }
        MdmManager mdmManager = new MdmManager(this);
    }

    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isWaitAdminActivation = false;
        moveTaskToBack(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                Log.d(TAG, "PERMISSIONS_REQUEST_CODE");
                isWaitPermission = false;
                if(grantResults.length > 0) {
                    Log.d(TAG, "length : " + grantResults.length);
                    for(int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "1");
                            if(shouldShowRequestPermissionRationale(permissions[i])) {
                                Log.d(TAG, "2");
                                CommonUtil.isFirstRejected = true;
                                CommonUtil.stateGrantPermissions = CommonUtil.STATE_USER_REJECTED;
                            } else {
                                Log.d(TAG, "3");
                                if(CommonUtil.isFirstRejected) CommonUtil.stateGrantPermissions = CommonUtil.STATE_USER_REJECTED;
                                else CommonUtil.stateGrantPermissions = CommonUtil.STATE_KEEP_REJECTED;

                            }
                            return;
                        }
                    }
                    CommonUtil.stateGrantPermissions = CommonUtil.STATE_SUCCESS;
                } else {
                    Log.d(TAG, "4");
                    for(int i = 0; i<permissions.length; i++) {
                        if(shouldShowRequestPermissionRationale(permissions[i])) {
                            Log.d(TAG, "5");
                            CommonUtil.isFirstRejected = true;
                            CommonUtil.stateGrantPermissions = CommonUtil.STATE_USER_REJECTED;
                        } else {
                            Log.d(TAG, "6");
                            if(CommonUtil.isFirstRejected) CommonUtil.stateGrantPermissions = CommonUtil.STATE_USER_REJECTED;
                            else CommonUtil.stateGrantPermissions = CommonUtil.STATE_DENIED;

                        }
                    }
                }
            }
        }
    }

    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }
}
