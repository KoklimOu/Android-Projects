package com.ahope.ds.cl.mdm_agent.module.monitering;

import android.app.KeyguardManager;
import android.content.Context;

public class PassCodeCheck {

    public boolean isDeviceLocked(Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE); //api 23+
        return keyguardManager.isDeviceSecure();
    }
}
