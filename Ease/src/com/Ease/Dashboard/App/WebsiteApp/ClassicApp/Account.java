package com.Ease.Dashboard.App.WebsiteApp.ClassicApp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.Ease.Context.Group.Infrastructure;
import com.Ease.Dashboard.User.User;
import com.Ease.Utils.DataBaseConnection;
import com.Ease.Utils.GeneralException;
import com.Ease.Utils.ServletManager;

public class Account {
	public enum Data {
		NOTHING,
		ID,
		PASSWORD,
		SHARED
	}
	
	/*
	 * 
	 * Loader And creator
	 * 
	 */
	
	public static Account loadAccount(String db_id, DataBaseConnection db) throws GeneralException {
		ResultSet rs = db.get("SELECT * FROM accounts WHERE id = " + db_id + ";");
		try {
			if (rs.next()) {
				String password = rs.getString(Data.PASSWORD.ordinal());
				List<AccountInformation> infos = AccountInformation.loadInformations(db_id, db);
				boolean shared = rs.getBoolean(Data.SHARED.ordinal());
				return new Account(db_id, password, shared, infos);
			}
		} catch (SQLException e) {
			throw new GeneralException(ServletManager.Code.InternError, e);
		}
		throw new GeneralException(ServletManager.Code.InternError, "This account dosen't exist.");
	}
	
	public static Account createAccount(String password, boolean shared, Map<String, String> informations, User user, ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		int transaction = db.startTransaction();
		String db_id = db.set("INSERT INTO accounts values (null, '" + user.encrypt(password) + "', " + (shared ? 1 : 0) + ");").toString();
		List<AccountInformation> infos = AccountInformation.createAccountInformations(db_id, informations, sm);
		db.commitTransaction(transaction);
		return new Account(db_id, password, shared, infos);
	}
	
	public static Account createGroupAccount(String password, boolean shared, Map<String, String> informations, Infrastructure infra, ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		int transaction = db.startTransaction();
		String crypted_password = infra.encrypt(password, sm);
		String db_id = db.set("INSERT INTO accounts values (null, '" + crypted_password + "', " + (shared ? 1 : 0) + ");").toString();
		List<AccountInformation> infos = AccountInformation.createAccountInformations(db_id, informations, sm);
		db.commitTransaction(transaction);
		return new Account(db_id, crypted_password, shared, infos);
	}
	
	/*
	 * 
	 * Constuctor
	 * 
	 */
	
	protected String 				db_id;
	protected String 				crypted_password;
	protected boolean 				shared;
	protected List<AccountInformation>	infos;
	
	public Account(String db_id, String crypted_password, boolean shared, List<AccountInformation> infos) {
		this.db_id = db_id;
		this.crypted_password = crypted_password;
		this.shared = shared;
		this.infos = infos;
	}
	
	public void removeFromDB(ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		int transaction = db.startTransaction();
		for (AccountInformation info : infos) {
			info.removeFromDb(sm);
		}
		db.set("DELETE FROM accounts WHERE id=" + db_id + ";");
		db.commitTransaction(transaction);
	}
	
	/*
	 * 
	 * Getter And Setter
	 * 
	 */
	
	public String getDBid() {
		return db_id;
	}
}