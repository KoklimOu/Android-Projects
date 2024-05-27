package com.ahope.ds.cl.mdm_agent.manager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.ahope.ds.cl.mdm_agent.common.util.CommonUtil;
import com.ahope.ds.cl.mdm_agent.module.monitering.DeviceRootingCheck;
import com.ahope.ds.cl.mdm_agent.module.monitering.PassCodeCheck;
import com.ahope.ds.cl.mdm_agent.module.receiver.PackageInstallReceiver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Objects;

public class DeviceManager {
    public static final String PHONE_NUMBER = "phone_number";
    public static final String DEVICE_NAME = "device_name";
    public static final String OS_VERSION = "os_version";
    public static final String OS = "os";
    public static final String IP = "ip";
    public static final String PLATFORM_VERSION = "platform_version";
    public static final String BUILD_VERSiON = "build_version";
    public static final String KERNEL_VERSION = "kernel_version";
    public static final String BASEBAND_VERSION = "baseband_version";
    public static final String MODEL = "model";
    public static final String SERIAL_NUMBER = "serial_number";
    public static final String IMEI = "imei";
    public static final String WIFI_MAC = "wifi_mac";
    public static final String DATA_OF_INSTALLED = "date_of_installed";

    Context context;
    PackageInstallReceiver pReceiver;

    public DeviceManager(Context context) {
        this.context = context;
        // Create AirPlainMode Receiver

        // create PackageInstall Receiver
        pReceiver = new PackageInstallReceiver();
        IntentFilter pFilter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        pFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        pFilter.addDataScheme("package");
        context.registerReceiver(pReceiver, pFilter);
    }

    public boolean checkRootedDevice() {
        Log.d("DeviceManager", "checkRootedDevice call");
        DeviceRootingCheck deviceRootingCheckTest = new DeviceRootingCheck(context);
        boolean isRooted = deviceRootingCheckTest.rootCheck();
        Log.d("Main", "isRooted = " + isRooted);
        return isRooted;
    }

    public boolean checkPassCodeSetting() {
        Log.d("DeviceManager", "checkPassCodeSetting call");
        PassCodeCheck passCodeTest = new PassCodeCheck();
        return passCodeTest.isDeviceLocked(context);
    }

    public boolean checkAppInstall() {
        Log.d("DeviceManager", "checkAppInstall call");
        if (PackageInstallReceiver.isPackageAdd) {
            PackageInstallReceiver.isPackageAdd = false;
            return true;
        }
        else {
            return PackageInstallReceiver.isPackageAdd;
        }
    }

    public boolean checkAppUnInstall() {
        Log.d("DeviceManager", "checkAppUnInstall call");
        if (PackageInstallReceiver.isPackageRemove) {
            PackageInstallReceiver.isPackageRemove = false;
            return true;
        }
        else {
            return PackageInstallReceiver.isPackageRemove;
        }
    }

    public HashMap<String, String> getDeviceInfo() {
        HashMap<String, String> map = new HashMap<>();
        boolean isCheckReadPhoneState = Build.VERSION.SDK_INT < 30 && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
        boolean isCheckReadPhoneNumber = Build.VERSION.SDK_INT >= 30 && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED;

        if (isCheckReadPhoneState || isCheckReadPhoneNumber) {
            map.put(PHONE_NUMBER, getPhoneNumber());
            map.put(MODEL, getModelName());
            map.put(IMEI, getIMEINumber());
            map.put(IP, getIpAddress());
            map.put(PLATFORM_VERSION, getPlatformVersion());
            map.put(BUILD_VERSiON, getBuildVersion());
            map.put(DEVICE_NAME, getUserDeviceName());
            map.put(OS, "android");
            map.put(OS_VERSION, getDeviceOsVersion());
            map.put(BASEBAND_VERSION, getDeviceBaseBandVersion());
            map.put(SERIAL_NUMBER, getSerialNumber());
            map.put(WIFI_MAC, getMACAddress());
            map.put(DATA_OF_INSTALLED, getDateOfInstalled());
            map.put(KERNEL_VERSION, getKernelVersion());
        }
        return map;
    }

    public String getPlatformVersion() {
        Field[] fields = Build.VERSION_CODES.class.getFields();
        String osName = fields[Build.VERSION.SDK_INT].getName();
        Log.d("Android OsName:",osName);
        return osName;
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public String getPhoneNumber() {
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = "";
        try {
            phoneNumber += telManager.getLine1Number();
        } catch (Exception e) {
            Log.d("MainActivity", Objects.requireNonNull(e.getMessage()));
            phoneNumber = "";
        }
        return phoneNumber;
    }

    public String getModelName() {
        return Build.MODEL;
    }

    @SuppressLint("HardwareIds")
    private String getIMEINumber() {
        String IMEINumber = "";
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager telephonyMgr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                IMEINumber = telephonyMgr.getImei();
            }
            else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                IMEINumber = telephonyMgr.getDeviceId();
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            }
        }
        return IMEINumber;
    }

    public String getIpAddress() {
        try {
            for(Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces(); enumeration.hasMoreElements();) {
                NetworkInterface intface = enumeration.nextElement();
                for(Enumeration<InetAddress> enumIpAddr = intface.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if(inetAddress.isLoopbackAddress()) {
                        Log.i("IPAddress", intface.getDisplayName() + "(loopback) | " + inetAddress.getHostAddress());
                    } else {
                        Log.i("IPAddress", intface.getDisplayName() + " | " + inetAddress.getHostAddress());
                    }
                    if(!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getBuildVersion() {
        return Build.ID;
    }

    public String getDeviceOsVersion() {
        return Build.VERSION.RELEASE;
    }

    public String getDeviceBaseBandVersion() {
        return Build.getRadioVersion();
    }

    @SuppressLint("HardwareIds")
    public String getSerialNumber() {
        String serialNumber = "";

        if (Build.VERSION.SDK_INT < 28) {
            serialNumber += Build.SERIAL;
        }
        return serialNumber;
    }

    @SuppressLint({"WifiManagerLeak", "HardwareIds"})
    public String getMACAddress() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getMacAddress();
    }

    public String getUserDeviceName() {
        BluetoothAdapter userDevice = BluetoothAdapter.getDefaultAdapter();
        if(userDevice == null) {
            return "";
        }
        return userDevice.getName();
    }

    public String getDateOfInstalled() {
        try {
            PackageManager packageManager = context.getPackageManager();
            long installed = packageManager.getPackageInfo(CommonUtil.AGENT_PKG_NAME, 0).firstInstallTime;
            return getData(installed);
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    public static String getData(long datetime) {
        DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(datetime);
        return formatter.format(calendar.getTime());
    }

    //Kernel Version 확인
    public static String getKernelVersion() {
        try {
            Process process = Runtime.getRuntime().exec("uname -a");
            InputStream inputStream;
            if (process.waitFor() == 0) {
                inputStream = process.getInputStream();
            } else {
                inputStream = process.getErrorStream();
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = bufferedReader.readLine();
            bufferedReader.close();
            return line;
        } catch (Exception e) {
            return "";
        }
    }
}
