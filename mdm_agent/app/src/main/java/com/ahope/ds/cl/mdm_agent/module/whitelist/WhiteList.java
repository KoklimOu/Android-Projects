package com.ahope.ds.cl.mdm_agent.module.whitelist;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import com.ahope.ds.cl.mdm_agent.common.preference.manager.WhiteListPreferencesManager;
import com.ahope.ds.cl.mdm_agent.common.util.CommonUtil;
import com.ahope.ds.cl.mdm_agent.manager.ErrorHandleManager;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;

public class WhiteList {
    Context context;
    public static final String HASH_ALG = "SHA256";

    public WhiteList(Context context) {
        this.context = context;
    }

    public App[] getPakageList() {
        try {
            int i = 0;
            String key = "";
            Map<String, Integer> getWhiteList = (Map<String, Integer>) WhiteListPreferencesManager.getInstance().getAll();
            App[] outApps = new App[getWhiteList.size()];
            Iterator iterator = getWhiteList.keySet().iterator();

            while (iterator.hasNext()) {
                key = (String) iterator.next();
                if (key != null) {
                    outApps[i] = new App(key, getSig(HASH_ALG, key), getWhiteList.get(key));
                    outApps[i].setPackageName(key);
                    outApps[i].setEnableFunctions(getWhiteList.get(key));
                }
                i++;
            }
            if (key.equals(""))
                return null;
            else
                return outApps;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean setPakageList(App[] whiteList) {
        try {
            WhiteListPreferencesManager.getInstance().removeAll();
            for (int i = 0; i < whiteList.length; i++) {
                if (whiteList[i] == null) continue;
                WhiteListPreferencesManager.getInstance().setInt(whiteList[i].getPackageName(), whiteList[i].getEnableFunctions());
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public int verifySig(App app) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(app.getPackageName(), PackageManager.GET_SIGNATURES);
			Signature[] result = packageInfo.signatures;
//			Log.d("APK_SIGN", "SHA256 \n" + getCert(HASH_ALG, result[0]));
			if (getCert(HASH_ALG, result[0]).equals(app.getSignature()) != true) {
			    return ErrorHandleManager.ERROR_SIG_NOT_MATCH;
            }
		} catch (Exception e) {
		    if (e.toString().contains("NameNotFoundException")) return ErrorHandleManager.ERROR_PACKAGE_NOT_FOUND;
		    else return ErrorHandleManager.ERROR_UNKNOWN;
		}
        return CommonUtil.STATE_SUCCESS;
    }

    public String getCert(String algorithm, Signature input) {
        try {
            MessageDigest msgDigest = MessageDigest.getInstance(algorithm);
            msgDigest.update(input.toByteArray());
            byte[] result = msgDigest.digest();
            return byteArrayToHexString(result);
        } catch (Exception e) {
            return "";
        }
    }

    public String getSig(String algorithm, String pakcageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(pakcageName, PackageManager.GET_SIGNATURES);
            Signature[] result = packageInfo.signatures;
            return getCert(HASH_ALG, result[0]);
        } catch (Exception e) {
            return "";
        }
    }

    public static String byteArrayToHexString(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for(byte b : bytes){
            sb.append(String.format("%02X", b&0xff));
        }
        return sb.toString();
    }
}
