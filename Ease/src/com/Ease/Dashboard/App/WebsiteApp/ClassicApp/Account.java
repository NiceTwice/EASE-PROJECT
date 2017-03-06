package com.Ease.Dashboard.App.WebsiteApp.ClassicApp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.Ease.Context.Group.Infrastructure;
import com.Ease.Dashboard.User.User;
import com.Ease.Utils.DataBaseConnection;
import com.Ease.Utils.GeneralException;
import com.Ease.Utils.ServletManager;

public class Account {
	public enum Data {
		NOTHING,
		ID,
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
				List<AccountInformation> infos = AccountInformation.loadInformations(db_id, db);
				boolean shared = rs.getBoolean(Data.SHARED.ordinal());
				return new Account(db_id, shared, infos);
			}
		} catch (SQLException e) {
			throw new GeneralException(ServletManager.Code.InternError, e);
		}
		throw new GeneralException(ServletManager.Code.InternError, "This account doesn't exist.");
	}
	
	public static Account createAccount(boolean shared, Map<String, String> informations, ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		int transaction = db.startTransaction();
		String db_id = db.set("INSERT INTO accounts values (null, " + (shared ? 1 : 0) + ");").toString();
		List<AccountInformation> infos = AccountInformation.createAccountInformations(db_id, informations, sm);
		db.commitTransaction(transaction);
		return new Account(db_id, shared, infos);
	}
	
	public static Account createAccountSameAs(Account sameAccount, boolean shared, User user, ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		int transaction = db.startTransaction();
		String db_id = db.set("INSERT INTO accounts values (null, " + (shared ? 1 : 0) + ");").toString();
		List<AccountInformation> infos = AccountInformation.createAccountInformationFromAccountInformations(db_id, sameAccount.getAccountInformations(), sm);
		db.commitTransaction(transaction);
		return new Account(db_id, shared, infos);
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
	protected boolean 				shared;
	protected List<AccountInformation>	infos;
	
	public Account(String db_id, boolean shared, List<AccountInformation> infos) {
		this.db_id = db_id;
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
	
	public List<AccountInformation> getAccountInformations(){
		return infos;
	}
	
	public void setPassword(String password, User user, ServletManager sm) throws GeneralException {
		String cryptedPassword = user.encrypt(password);
		if (this.getInformationNamed("password") == null)
			throw new GeneralException(ServletManager.Code.ClientError, "This account does not have password field");
		for (AccountInformation info : this.infos) {
			if (info.getInformationName().equals("password"))
				info.setInformation_value(cryptedPassword, sm);
		}
	}
	
	public String getCryptedPassword() throws GeneralException {
		String password = this.getInformationNamed("password");
		if (password == null)
			throw new GeneralException(ServletManager.Code.ClientError, "This account does not have password field");
		return password;
	}
	
	/*
	 * 
	 * Utils
	 * 
	 */
	
	public JSONObject getJSON(ServletManager sm) throws GeneralException{
		JSONObject obj = new JSONObject();
		//obj.put("password", sm.getUser().decrypt(this.crypted_password));
		for(AccountInformation info : this.infos){
			String value;
			if (info.getInformationName().equals("password")) {
				value = sm.getUser().decrypt(info.getInformationValue());
			} else {
				value = info.getInformationValue();
			}
			obj.put(info.getInformationName(), value);
		}
		return obj;
	}
	
	public void editInfos(Map<String, String> infos, ServletManager sm) throws GeneralException {
		String value;
		DataBaseConnection db = sm.getDB();
		int transaction = db.startTransaction();
		for (AccountInformation info : this.infos) {
			if ((value = infos.get(info.getInformationName())) != null) {
				 info.setInformation_value(value, sm);
			}
		}
		db.commitTransaction(transaction);
	}
	
	public String getInformationNamed(String info_name) {
		for (AccountInformation info : this.infos) {
			if (info.getInformationName().equals(info_name))
				return info.getInformationValue();
		}
		return null;
	}

	public String getPassword() {
		return this.crypted_password;
	}
}
