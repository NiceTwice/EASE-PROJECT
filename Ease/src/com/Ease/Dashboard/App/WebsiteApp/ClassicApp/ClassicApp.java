package com.Ease.Dashboard.App.WebsiteApp.ClassicApp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.Ease.Context.Catalog.Website;
import com.Ease.Dashboard.App.AppInformation;
import com.Ease.Dashboard.App.GroupApp;
import com.Ease.Dashboard.App.WebsiteApp.GroupWebsiteApp;
import com.Ease.Dashboard.App.WebsiteApp.WebsiteApp;
import com.Ease.Dashboard.Profile.Profile;
import com.Ease.Dashboard.User.User;
import com.Ease.Utils.DataBaseConnection;
import com.Ease.Utils.GeneralException;
import com.Ease.Utils.IdGenerator;
import com.Ease.Utils.ServletManager;

public class ClassicApp extends WebsiteApp {
	
	public enum Data {
		NOTHING,
		ID,
		WEBSITE_APP_ID,
		ACCOUNT_ID,
		GROUP_CLASSIC_APP_ID
	}
	
	/*
	 *
	 * Loader And Creator
	 * 
	 */
	
	public static ClassicApp loadClassicApp(String db_id, Profile profile, int position, AppInformation infos, GroupApp groupApp, String insertDate, Website site, String websiteAppDBid, GroupWebsiteApp groupWebsiteApp, ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		try {
			ResultSet rs = db.get("SELECT * from classicApps WHERE website_app_id=" + websiteAppDBid + ";");
			if (rs.next()) {
				Account account = Account.loadAccount(rs.getString(Data.ACCOUNT_ID.ordinal()), db);
				String classicDBid = rs.getString(Data.ID.ordinal());
				IdGenerator idGenerator = (IdGenerator)sm.getContextAttr("idGenerator");
				return new ClassicApp(db_id, profile, position, infos, groupApp, insertDate, idGenerator.getNextId(), site, websiteAppDBid, groupWebsiteApp, account, classicDBid);
			} 
			throw new GeneralException(ServletManager.Code.InternError, "Classic app not complete in db.");
		} catch (SQLException e) {
			throw new GeneralException(ServletManager.Code.InternError, e);
		}
	}
	
	public static ClassicApp createClassicApp(Profile profile, int position, String name, Website site, String password, Map<String, String> infos, ServletManager sm, User user) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		int transaction = db.startTransaction();
		Map<String, Object> elevator = new HashMap<String, Object>();
		String websiteAppDBid = WebsiteApp.createWebsiteApp(profile, position, name, "classicApp", site, elevator, sm);
		Account account = Account.createAccount(password, false, infos, user, sm);
		String classicDBid = db.set("INSERT INTO classicApps VALUES(NULL, " + websiteAppDBid + ", " + account.getDBid() + ", NULL);").toString();
		db.commitTransaction(transaction);
		return new ClassicApp((String)elevator.get("appDBid"), profile, position, (AppInformation)elevator.get("appInfos"), null, (String)elevator.get("registrationDate"), ((IdGenerator)sm.getContextAttr("idGenerator")).getNextId(), site, websiteAppDBid, null, account, classicDBid);
	}
	
	
	/*
	 * 
	 * Constructor
	 * 
	 */
	
	protected Account account;
	protected String classicDBid;
	
	public ClassicApp(String db_id, Profile profile, int position, AppInformation infos, GroupApp groupApp, String insertDate, int single_id, Website site, String websiteAppDBid, GroupWebsiteApp groupWebsiteApp, Account account, String classicDBid) {
		super(db_id, profile, position, infos, groupApp, insertDate, single_id, site, websiteAppDBid, groupWebsiteApp);
		this.account = account;
		this.classicDBid = classicDBid;
	}
	
	public void removeFromDB(ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		int transaction = db.startTransaction();
		if (this.groupApp == null || this.groupApp.isCommon() == false)
			account.removeFromDB(sm);
		db.set("DELETE FROM classicApps WHERE id=" + classicDBid + ";");
		super.removeFromDB(sm);
		db.commitTransaction(transaction);
	}
	
}
