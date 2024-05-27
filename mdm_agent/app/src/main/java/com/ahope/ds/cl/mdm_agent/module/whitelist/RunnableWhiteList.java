package com.ahope.ds.cl.mdm_agent.module.whitelist;

import android.content.Context;

import com.ahope.ds.cl.mdm_agent.common.preference.manager.DSPreferencesManager;
import com.ahope.ds.cl.mdm_agent.common.preference.manager.RunnableWhiteListPreferencesManager;
import com.ahope.ds.cl.mdm_agent.common.preference.manager.WhiteListPreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class RunnableWhiteList {
	Context context;
	List<String> runnableWhiteList = new ArrayList<String>();

	public RunnableWhiteList(Context context) {
		this.context = context;
	}

	public List<String> getPakageList() {
		try {
			JSONArray jsonArray = new JSONArray(DSPreferencesManager.getInstance().getString("runnableWhiteList", ""));
			if (jsonArray != null) {
				for (int i = 0; i < jsonArray.length(); i++) {
					runnableWhiteList.add(jsonArray.get(i).toString());
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return runnableWhiteList;
	}

	public boolean setPakageList(List<String> runnableWhiteList) {
		try {
			RunnableWhiteListPreferencesManager.getInstance().removeAll();
			for (int i = 0; i < runnableWhiteList.size(); i++) {
				if (runnableWhiteList.get(i) == null) continue;
				RunnableWhiteListPreferencesManager.getInstance().setString("runnableWhiteList", runnableWhiteList.toString());
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public void append(String runnableApp) {
		runnableWhiteList = getPakageList();
		runnableWhiteList.add(runnableApp);
		RunnableWhiteListPreferencesManager.getInstance().setString("runnableWhiteList", runnableWhiteList.toString());
	}

	public void remove(String runnableApp) {
		runnableWhiteList = getPakageList();
		runnableWhiteList.remove(runnableApp);
		RunnableWhiteListPreferencesManager.getInstance().setString("runnableWhiteList", runnableWhiteList.toString());
	}

	public void clear() {
		WhiteListPreferencesManager.getInstance().removeAll();
	}
}
