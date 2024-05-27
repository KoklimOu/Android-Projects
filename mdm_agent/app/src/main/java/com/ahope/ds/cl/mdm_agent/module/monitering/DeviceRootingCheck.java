package com.ahope.ds.cl.mdm_agent.module.monitering;

import android.content.Context;
import com.ahope.app_shields.RootingCheck;

public class DeviceRootingCheck {
    Context context;

    public DeviceRootingCheck(Context context) {
        this.context = context;
    }

    public boolean rootCheck() {
        RootingCheck rc = new RootingCheck(context);
        return rc.dectetRooting();
    }
}
