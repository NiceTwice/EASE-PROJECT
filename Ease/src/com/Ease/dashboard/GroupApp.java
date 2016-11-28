package com.Ease.dashboard;

public class GroupApp {
	public enum Data {
		NOTHING,
		ID,
		GROUP_PROFILE_ID,
		GROUP_ID,
		PERMISSION_ID,
		POSITION,
		TYPE,
		APP_INFO_ID,
		COMMON
	}
	
	protected String db_id;
	protected GroupProfile groupProfile;
	protected Group group;
	protected AppPermissions permissions;
	protected String type;
	protected AppInformation app_informations;
	protected boolean common;
	
	public GroupApp(String db_id, GroupProfile groupProfile, Group group, AppPermissions permissions, String type, AppInformation app_informations, boolean common) {
		this.db_id = db_id;
		this.groupProfile = groupProfile;
		this.group = group;
		this.permissions = permissions;
		this.type = type;
		this.app_informations = app_informations;
		this.common = common;
	}
	
	public String getDb_id() {
		return this.db_id;
	}
}
