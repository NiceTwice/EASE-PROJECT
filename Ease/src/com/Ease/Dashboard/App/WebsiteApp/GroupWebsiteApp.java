package com.Ease.Dashboard.App.WebsiteApp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import com.Ease.Context.Catalog.Catalog;
import com.Ease.Context.Group.Group;
import com.Ease.Context.Group.GroupManager;
import com.Ease.Dashboard.App.AppInformation;
import com.Ease.Dashboard.App.AppPermissions;
import com.Ease.Dashboard.App.GroupApp;
import com.Ease.Dashboard.App.Website;
import com.Ease.Dashboard.App.WebsiteApp.ClassicApp.GroupClassicApp;
import com.Ease.Dashboard.App.WebsiteApp.LogwithApp.GroupLogwithApp;
import com.Ease.Dashboard.Profile.GroupProfile;
import com.Ease.Utils.DataBaseConnection;
import com.Ease.Utils.GeneralException;
import com.Ease.Utils.IdGenerator;
import com.Ease.Utils.ServletManager;

public class GroupWebsiteApp extends GroupApp {
public enum Data {
		NOTHING,
		ID,
		GROUP_APP_ID,
		WEBSITE_ID,
		TYPE
	}

	/*
	 * 
	 * Loader And Creator
	 * 
	 */
	
	public static GroupWebsiteApp loadGroupWebsiteApp(String db_id, Group group, GroupProfile groupProfile, AppPermissions perms, AppInformation info, boolean common, int single_id, DataBaseConnection db, ServletContext context) throws GeneralException {
		try {
			ResultSet rs = db.get("SELECT * FROM groupWebsiteApps WHERE group_app_id=" + db_id + ";");
			if (rs.next()) {
				Website website = ((Catalog)context.getAttribute("catalog")).getWebsiteWithDBid(rs.getString(Data.WEBSITE_ID.ordinal()));
				String db_id2 = rs.getString(Data.ID.ordinal());
				switch (rs.getString(Data.TYPE.ordinal())) {
					case "groupEmptyApp":
						GroupWebsiteApp groupWebsiteApp = new GroupWebsiteApp(db_id, groupProfile, group, perms, info, common, single_id, website, db_id2);
						GroupManager.getGroupManager(context).add(groupWebsiteApp);
						return groupWebsiteApp;
					case "groupClassicApp":
						return GroupClassicApp.loadGroupClassicApp(db_id, groupProfile, group, perms, info, common, single_id, website, db_id2, db, context);
					case "groupLogwithApp":
						return GroupLogwithApp.loadGroupLogwithApp(db_id, groupProfile, group, perms, info, common, single_id, website, db_id2, db, context);
					default:
						throw new GeneralException(ServletManager.Code.InternError, "This GroupWebsiteApp type dosen't exist.");
				}
			} else {
				throw new GeneralException(ServletManager.Code.InternError, "This GroupWebsiteApp dosen't exist.");
			}
		} catch (SQLException e) {
			throw new GeneralException(ServletManager.Code.InternError, e);
		}
	}
	
	public static GroupWebsiteApp createGroupEmptyApp(GroupProfile groupProfile, Group group, int perms, String name, boolean common, Website site, ServletManager sm) throws GeneralException {
		Map<String, Object> elevator = new HashMap<String, Object>();
		DataBaseConnection db = sm.getDB();
		int transaction = db.startTransaction();
		String appDBid = GroupApp.createGroupApp(groupProfile, group, perms, name, common, "groupWebsiteApp", elevator, sm);
		AppPermissions permissions = (AppPermissions) elevator.get("perms");
		AppInformation appInfos = (AppInformation) elevator.get("appInfos");
		String db_id = db.set("INSERT INTO groupWebsiteApps VALUES(NULL, " + appDBid + ", " + site.getDb_id() + ");").toString();
		int single_id = ((IdGenerator)sm.getContextAttr("idGenerator")).getNextId();
		GroupWebsiteApp groupWebsiteApp = new GroupWebsiteApp(appDBid, groupProfile, group, permissions, appInfos, common, single_id, site, db_id);
		GroupManager.getGroupManager(sm).add(groupWebsiteApp);
		db.commitTransaction(transaction);
		return groupWebsiteApp;
	}
	
	public static String createGroupWebsiteApp(GroupProfile groupProfile, Group group, int perms, String name, boolean common, Website site, String type, Map<String, Object> elevator, ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		int transaction = db.startTransaction();
		String appDBid = GroupApp.createGroupApp(groupProfile, group, perms, name, common, "groupWebsiteApp", elevator, sm);
		elevator.put("appDBid", appDBid);
		String db_id = db.set("INSERT INTO groupWebsiteApps VALUES(NULL, " + appDBid + ", " + site.getDb_id() + ");").toString();
		db.commitTransaction(transaction);
		return db_id;
	}

	/*
	 * 
	 * Constructor
	 * 
	 */

	protected Website 	site;
	protected String	db_id2;
	
	public GroupWebsiteApp(String db_id, GroupProfile groupProfile, Group group, AppPermissions permissions, AppInformation app_informations, boolean common, int single_id, Website website, String db_id2) {
		super(db_id, groupProfile, group, permissions, app_informations, common, single_id);
		this.site = website;
		this.db_id2 = db_id2;
	}

	/*
	 * 
	 * Getter And Setter
	 * 
	 */
	
	public Website getWebsite() {
		return site;
	}

}