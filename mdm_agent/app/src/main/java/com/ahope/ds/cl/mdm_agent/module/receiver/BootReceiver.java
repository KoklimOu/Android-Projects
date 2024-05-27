package com.ahope.ds.cl.mdm_agent.module.receiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ahope.ds.cl.mdm_agent.service.Svc;

public class BootReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.e("MDMAgent", "Svc Re onCreate");
            Intent service = new Intent(context, Svc.class);
            service.setPackage("com.ahope.ds.cl.mdm_agent.ISvc");
            context.startService(service);
        }
    }
}
