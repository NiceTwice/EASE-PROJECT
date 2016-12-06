package com.Ease.Context.Group;

import com.Ease.Dashboard.App.AppInformation;
import com.Ease.Dashboard.App.Website;

public class GroupWebsiteApp extends GroupApp {
	
	public enum Data {
		NOTHING,
		ID,
		GROUP_APP_ID,
		WEBSITE_ID
	}
	
	protected GroupApp group_app;
	protected Website site;
	
	public GroupWebsiteApp(String db_id, GroupProfile groupProfile, Group group, AppPermissions permissions, String type, AppInformation app_informations, boolean common, GroupApp group_app, Website site) {
		super(db_id, groupProfile, group, permissions, type, app_informations, common);
		this.group_app = group_app;
		this.site = site;
	}
	
	public String getGroupAppId() {
		return this.group_app.getDb_id();
	}
	
	public GroupApp getGroupApp() {
		return this.group_app;
	}
}
