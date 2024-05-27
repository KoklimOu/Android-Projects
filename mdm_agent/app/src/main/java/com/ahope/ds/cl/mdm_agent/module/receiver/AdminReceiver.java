/**
 * DISCLAIMER: PLEASE TAKE NOTE THAT THE SAMPLE APPLICATION AND
 * SOURCE CODE DESCRIBED HEREIN IS PROVIDED FOR TESTING PURPOSES ONLY.
 * <p>
 * Samsung expressly disclaims any and all warranties of any kind,
 * whether express or implied, including but not limited to the implied warranties and conditions
 * of merchantability, fitness for com.samsung.knoxsdksample particular purpose and non-infringement.
 * Further, Samsung does not represent or warrant that any portion of the sample application and
 * source code is free of inaccuracies, errors, bugs or interruptions, or is reliable,
 * accurate, complete, or otherwise valid. The sample application and source code is provided
 * "as is" and "as available", without any warranty of any kind from Samsung.
 * <p>
 * Your use of the sample application and source code is at its own discretion and risk,
 * and licensee will be solely responsible for any damage that results from the use of the sample
 * application and source code including, but not limited to, any damage to your computer system or
 * platform. For the purpose of clarity, the sample code is licensed “as is” and
 * licenses bears the risk of using it.
 * <p>
 * Samsung shall not be liable for any direct, indirect or consequential damages or
 * costs of any type arising out of any action taken by you or others related to the sample application
 * and source code.
 */

package com.ahope.ds.cl.mdm_agent.module.receiver;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ahope.ds.cl.mdm_agent.common.util.CommonUtil;
import com.ahope.ds.cl.mdm_agent.service.Svc;
import com.ahope.ds.cl.mdm_agent.common.preference.manager.DSPreferencesManager;
import com.ahope.ds.cl.mdm_agent.common.preference.config.DSPreferencesConfig;

public class AdminReceiver extends DeviceAdminReceiver {
    public static final int IS_ENABLE_ADMIN = 1;
    public static final int IS_DISABLE_ADMIN = -1;

    @Override
    public void onEnabled(Context context, Intent intent) {
        CommonUtil.stateDeviceAdmin = CommonUtil.STATE_SUCCESS;
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        CommonUtil.stateDeviceAdmin = CommonUtil.STATE_USER_REJECTED;
        if (Svc.one == null) return;
        Log.w("AdminReceiver", "Device admin disabled");
        try {
            if (Svc.one != null) {
                if (DSPreferencesManager.getInstance() == null) Svc.one.initPreferManager();
                DSPreferencesManager.getInstance().setBoolean(DSPreferencesConfig.IS_ACTIVATE_KNOX, false);
            }
        } catch (Exception e) {
            Log.w("AdminReceiver", e.getMessage());
        }
    }
}
