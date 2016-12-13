package com.Ease.Dashboard.App.LinkApp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.Ease.Context.Group.GroupManager;
import com.Ease.Dashboard.App.App;
import com.Ease.Dashboard.App.AppInformation;
import com.Ease.Dashboard.App.GroupApp;
import com.Ease.Dashboard.Profile.Profile;
import com.Ease.Utils.DataBaseConnection;
import com.Ease.Utils.GeneralException;
import com.Ease.Utils.IdGenerator;
import com.Ease.Utils.ServletManager;

public class LinkApp extends App {
	
	public enum Data {
		NOTHING,
		ID,
		APP_ID,
		LINK_APP_INFO_ID,
		GROUP_LINK_APP_ID
	}
	
	/*
	 * 
	 * Loader And Creator
	 * 
	 */
	
	public static LinkApp loadLinkApp(String appDBid, Profile profile, int position, String insertDate, AppInformation appInfos, GroupApp groupApp, ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		try {
			ResultSet rs = db.get("SELECT * from linkApps WHERE app_id=" + appDBid + ";");
			if (rs.next()) {
				LinkAppInformation linkInfos = LinkAppInformation.loadLinkAppInformation(rs.getString(Data.LINK_APP_INFO_ID.ordinal()), db);
				GroupLinkApp groupLinkApp = (GroupLinkApp) GroupManager.getGroupManager(sm).getGroupAppFromDBid(rs.getString(Data.GROUP_LINK_APP_ID.ordinal()));
				IdGenerator idGenerator = (IdGenerator)sm.getContextAttr("idGenerator");
				return new LinkApp(appDBid, profile, position, appInfos, groupApp, insertDate, idGenerator.getNextId(), linkInfos, groupLinkApp, rs.getString(Data.ID.ordinal()));
			} 
			throw new GeneralException(ServletManager.Code.InternError, "Link app not complete in db.");
		} catch (SQLException e) {
			throw new GeneralException(ServletManager.Code.InternError, e);
		}
	}
	
	public static LinkApp createLinkApp(Profile profile, int position, String name, String url, String imgUrl, ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		int transaction = db.startTransaction();
		Map<String, Object> elevator = new HashMap<String, Object>();
		String appDBid = App.createApp(profile, position, name, "linkApp", elevator, sm);
		LinkAppInformation infos = LinkAppInformation.createLinkAppInformation(url, imgUrl, sm);
		String linkDBid = db.set("INSERT INTO linkApps values(NULL, " + appDBid + ", " + infos.getDb_id() + ", NULL);").toString();
		db.commitTransaction(transaction);
		return new LinkApp(appDBid, profile, position, (AppInformation)elevator.get("appInfos"), null, (String)elevator.get("registrationDate"), ((IdGenerator)sm.getContextAttr("idGenerator")).getNextId(), infos, null, linkDBid);
	}
	
	/*
	 * 
	 * Constructor
	 * 
	 */
	
	protected LinkAppInformation	linkInfos;
	protected GroupLinkApp			groupLinkApp;
	protected String				linkAppDBid;
	
	public LinkApp(String db_id, Profile profile, int position, AppInformation infos, GroupApp groupApp, String insertDate, int single_id, LinkAppInformation linkInfos, GroupLinkApp groupLinkApp, String linkAppDBid) {
		super(db_id, profile, position, infos, groupApp, insertDate, single_id);
		this.linkInfos = linkInfos;
		this.groupLinkApp = groupLinkApp;
		this.linkAppDBid = linkAppDBid;
	}
	
	public void removeFromDB(ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		int transaction = db.startTransaction();
		if (this.groupApp == null || this.groupApp.isCommon() == false)
			linkInfos.removeFromDb(sm);
		db.set("DELETE FROM linkApps WHERE id=" + linkAppDBid + ";");
		super.removeFromDB(sm);
		db.commitTransaction(transaction);
	}
	
	/*
	 * 
	 * Getter And Setter
	 * 
	 */
	
}
	