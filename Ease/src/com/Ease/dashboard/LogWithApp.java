package com.Ease.dashboard;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.Ease.utils.DataBaseConnection;
import com.Ease.utils.GeneralException;
import com.Ease.utils.ServletManager;

public class LogWithApp extends WebsiteApp {
	
	public enum LogWithAppData {
		NOTHING,
		ID,
		WEBSITE_APP_ID,
		LOGWITH_APP_ID,
		WEBSITE_ID
	}
	

	public static LogWithApp createLogWithApp(String name, Profile profile, Website site, WebsiteApp logWithApp, ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		int transaction = db.startTransaction();
		int position = profile.getNextPosition();
		AppInformation app_information = AppInformation.createAppInformation(name, sm);
		String app_id = App.insertNewAppInDb(profile, position, "LogWithApp", app_information, null, sm);
		String website_app_id = WebsiteApp.insertNewWebsiteAppInDb(profile, position, "LogWithApp", app_information, null, site, sm);
		db.set("INSERT INTO logWithApps values (null, " + website_app_id + ", " + logWithApp.getDb_id() + ", null);");
		db.commitTransaction(transaction);
		return new LogWithApp(profile, position, sm.getNextSingleId(), app_id, true, site, logWithApp.getDb_id(), app_information);
	}
	
	protected String logWithApp_id;
	
	public LogWithApp(Profile profile, int position, int single_id, String db_id, boolean working, Website site, String logWithApp_id, AppInformation app_information) {
		super(profile, position, single_id, db_id, working, site, app_information);
		this.logWithApp_id = logWithApp_id;
	}
	
	public LogWithApp(String db_id, Profile profile, ServletManager sm) throws GeneralException {
		super(db_id, profile, sm);
		DataBaseConnection db = sm.getDB();
		ResultSet rs = db.get("SELECT logWith_app_id FROM logWithApps JOIN websiteApps ON website_app_id = websiteApps.id WHERE websiteApps.app_id = " + db_id + ";");
		try {
			this.logWithApp_id = rs.getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GeneralException(ServletManager.Code.InternError, e);
		}
	}
	
	public String getLogWithApp_id() {
		return this.logWithApp_id;
	}
	
	public void setLogWithApp(String logWithApp_id, ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		db.set("UPDATE logWithApps SET website_app_id = " + logWithApp_id + " WHERE id = " + this.getDb_id() + ";");
		this.logWithApp_id = logWithApp_id;
	}
	
	public void removeFromDb(ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		int transaction = db.startTransaction();
		db.set("DELETE FROM logWithApps WHERE id = " + this.getDb_id() + ";");
		super.removeFromDb(sm);
		db.commitTransaction(transaction);
	}
}
