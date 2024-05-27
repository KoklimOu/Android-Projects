package com.ahope.ds.cl.mdm_agent.module.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.ahope.ds.cl.mdm_agent.module.whitelist.App;

import java.util.HashMap;
import java.util.List;

public class DsResponse implements Parcelable {
    App[] apps;
    List<String> runnableApps;
    String[] policies;
    boolean bool;
    double agentVersion;
    int errorCode;
    HashMap<String, String> deviceInfo;
    int status;

    public DsResponse() {

    }

    protected DsResponse(Parcel in) {
        apps = in.createTypedArray(App.CREATOR);
        policies = in.createStringArray();
        bool = in.readByte() != 0;
        agentVersion = in.readDouble();
        errorCode = in.readInt();
        status = in.readInt();
        deviceInfo = (HashMap<String, String>) in.readSerializable();
        runnableApps = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(apps, flags);
        dest.writeStringArray(policies);
        dest.writeByte((byte) (bool ? 1 : 0));
        dest.writeDouble(agentVersion);
        dest.writeInt(errorCode);
        dest.writeInt(status);
        dest.writeSerializable(deviceInfo);
        dest.writeStringList(runnableApps);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DsResponse> CREATOR = new Creator<DsResponse>() {
        @Override
        public DsResponse createFromParcel(Parcel in) {
            return new DsResponse(in);
        }

        @Override
        public DsResponse[] newArray(int size) {
            return new DsResponse[size];
        }
    };

    public App[] getApps() {
        return apps;
    }

    public void setApps(App[] apps) {
        this.apps = apps;
    }

    public String[] getPolicies() {
        return policies;
    }

    public void setPolicies(String[] policies) {
        this.policies = policies;
    }

    public boolean getBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    public double getAgentVersion() {
        return agentVersion;
    }

    public void setAgentVersion(double agentVersion) {
        this.agentVersion = agentVersion;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public HashMap<String, String> getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(HashMap<String, String> deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<String> getRunnableApps() {
        return runnableApps;
    }

    public void setRunnableApps(List<String> apps) {
        this.runnableApps = apps;
    }
}
