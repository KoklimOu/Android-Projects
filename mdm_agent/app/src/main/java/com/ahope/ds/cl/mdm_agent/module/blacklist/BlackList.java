package com.ahope.ds.cl.mdm_agent.module.blacklist;

import android.content.Context;

import com.ahope.ds.cl.mdm_agent.common.preference.manager.BlackListPreferencesManager;
import com.ahope.ds.cl.mdm_agent.module.whitelist.App;

import java.util.Iterator;
import java.util.Map;

public class BlackList extends  Object{

	Context context;

	public BlackList(Context context) {
		this.context = context;
	}

	public App[] getPackageList() {
		try {
			int i = 0;
			String blackApp = "";
			Map<String, Integer> getBlackList = (Map<String, Integer>) BlackListPreferencesManager.getInstance().getAll();
			App[] outApps = new App[getBlackList.size()];
			Iterator iterator = getBlackList.keySet().iterator();

			while (iterator.hasNext()) {
				blackApp = (String) iterator.next();
				if (blackApp != null) {
					outApps[i] = new App(blackApp);
					outApps[i].setPackageName(blackApp);
				}
				i++;
			}
			if (blackApp.equals(""))
				return null;
			else
				return outApps;
		} catch (Exception e) {
			return null;
		}
	}

	public boolean setPackageList(App[] blackList) {
		try {
			BlackListPreferencesManager.getInstance().removeAll();
			for (int i = 0; i < blackList.length; i++) {
				if (blackList[i] == null) continue;
				BlackListPreferencesManager.getInstance().setString(blackList[i].getPackageName(), "blackList");
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
