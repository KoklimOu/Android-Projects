package com.ahope.ds.cl.mdm_agent.module.whitelist;

import android.os.Parcel;
import android.os.Parcelable;

public class App implements Parcelable {
        public static final int FLAG_CAMERA = 0x1;
        public static final int FLAG_CAPTURE = 0x2;
        public static final int FLAG_BLUETOOTH = 0x4;
        public static final int FLAG_TETHERING = 0x8;
        public static final int FLAG_WIFI = 0x10;
        public static final int FLAG_WIFI_DIRECT = 0x20;
        public static final int FLAG_USB = 0x40;
        public static final int FLAG_USB_DEBUGGING = 0x80;
        public static final int FLAG_EXTERNAL_STORAGE = 0x100;
        public static final int FLAG_MICROPHONE = 0x200;
        public static final int FLAG_NFC = 0x400;
        public static final int FLAG_APPS_FROM_UNKOWN_INSTALL = 0x800;
        public static final int FLAG_FACTORY_RESET = 0x1000;
        public static final int FLAG_UNINSTALL_APP = 0x2000;
        public static final int FLAG_REMOVABLE_ADMIN = 0x4000;
        public static final int FLAG_GPS = 0x8000;
        public static final int FLAG_AIRPLANE_MODE = 0x10000;
        public static final int FLAG_ROAMING = 0x20000;

        private String packageName;
        private String signature;
        private int enableFunctions;

        public App () {
        }

        public App (String packageName) {
                this.packageName = packageName;
        }

        public App (String packageName, String signature, int enableFunctions) {
                this.packageName = packageName;
                this.signature = signature;
                this.enableFunctions = enableFunctions;
        }

        protected App(Parcel in) {
                packageName = in.readString();
                signature = in.readString();
                enableFunctions = in.readInt();
        }

        public static final Creator<App> CREATOR = new Creator<App>() {
                @Override
                public App createFromParcel(Parcel in) {
                        return new App(in);
                }

                @Override
                public App[] newArray(int size) {
                        return new App[size];
                }
        };

        public String getPackageName() {
                return packageName;
        }

        public void setPackageName(String packageName) {
                this.packageName = packageName;
        }

        public String getSignature() {
                return signature;
        }

        public void setSignature(String signature) {
                this.signature = signature;
        }

        public int getEnableFunctions() {
                return enableFunctions;
        }

        public void setEnableFunctions(int enableFunctions) { this.enableFunctions = enableFunctions; }

        @Override
        public int describeContents() {
                return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(packageName);
                dest.writeString(signature);
                dest.writeInt(enableFunctions);
        }
}