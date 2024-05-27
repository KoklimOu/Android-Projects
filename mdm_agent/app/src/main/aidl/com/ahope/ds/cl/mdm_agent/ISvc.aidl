// ISvc.aidl
package com.ahope.ds.cl.mdm_agent;
import com.ahope.ds.cl.mdm_agent.App;
import com.ahope.ds.cl.mdm_agent.DsResponse;
// Declare any non-default types here with import statements

interface ISvc{
    DsResponse setPolicy(inout String[] policys, boolean isDisable, inout String[] packageName, String mdmPakageName);
    DsResponse getPolicy();
    DsResponse activeAdmin();
    DsResponse isActiveAdmin();
    DsResponse activeLicense(boolean isSync);
    DsResponse isActiveLicense();
    DsResponse activeAccessibility();
    DsResponse isActiveAccessibility();
    DsResponse activePermission();
    DsResponse isActivePermission();
    DsResponse setExceptionApps(inout App[] apps);
    DsResponse getExceptionApps();
    DsResponse checkPassCodeSetting();
    DsResponse getDeviceInfo();
    DsResponse getAgentVersion();
    DsResponse disableRunnableApps(String mdmPakageName);
    DsResponse enableRunnableApps();
    DsResponse appendWhiteList(inout List<String> apps);
    DsResponse removeWhiteList(inout List<String> apps);
    DsResponse clearWhiteList();
    DsResponse getRunnableApps();
    DsResponse wipeData();
    DsResponse lockScreen();
    DsResponse installApp(String packageName);
    DsResponse unInstallApp(String packageName);
    DsResponse activeStoragePermission();
    DsResponse isActiveStoragePermission();

    void addMessenger(in Messenger messenger);
    void removeMessenger(in Messenger messenger);
    void registerProcessDeath(in IBinder clientDeathListener);
}
