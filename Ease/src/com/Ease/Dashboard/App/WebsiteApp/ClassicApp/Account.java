package com.Ease.Dashboard.App.WebsiteApp.ClassicApp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.Ease.Utils.Crypto.AES;
import com.Ease.Utils.Crypto.RSA;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.Ease.Dashboard.User.User;
import com.Ease.Utils.DataBaseConnection;
import com.Ease.Utils.DatabaseRequest;
import com.Ease.Utils.DatabaseResult;
import com.Ease.Utils.GeneralException;
import com.Ease.Utils.ServletManager;

public class Account {

    public enum Data {
        NOTHING,
        ID,
        SHARED,
        LAST_UPDATED_DATE,
        REMINDER_VALUE,
        REMINDER_TYPE
    }

	/*
     *
	 * Loader And creator
	 * 
	 */

    public static Account loadAccount(String db_id, DataBaseConnection db) throws GeneralException {
        DatabaseRequest request = db.prepareRequest("SELECT * FROM accounts WHERE id = ?;");
        request.setInt(db_id);
        DatabaseResult rs = request.get();
        if (!rs.next())
            throw new GeneralException(ServletManager.Code.InternError, "This account doesn't exist.");
        boolean shared = rs.getBoolean(Data.SHARED.ordinal());
        String publicKey = rs.getString("publicKey");
        String ciphered_privateKey = rs.getString("privateKey");
        Boolean mustBeReciphered = rs.getBoolean("mustBeReciphered");
        List<AccountInformation> infos = AccountInformation.loadInformations(db_id, db);
        Integer reminderValue = rs.getInt(Data.REMINDER_VALUE.ordinal());
        String reminderType = rs.getString(Data.REMINDER_TYPE.ordinal());
        String lastUpdatedDate = rs.getString(Data.LAST_UPDATED_DATE.ordinal());
        Account account = null;
        if (reminderType == null || reminderType.equals("")) {
            account = new Account(db_id, shared, publicKey, ciphered_privateKey, infos, mustBeReciphered);
            account.setLastUpdatedDate(lastUpdatedDate);
            return account;
        }

        request = db.prepareRequest("SELECT lastUpdateDate + INTERVAL ? " + reminderType + " FROM accounts WHERE id = ?;");
        request.setInt(reminderValue);
        request.setInt(db_id);
        rs = request.get();
        if (!rs.next())
            throw new GeneralException(ServletManager.Code.ClientError, "I don't know what to say");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date deadLine = dateFormat.parse(rs.getString(1));
            Date now = new Date();
            account = new Account(db_id, shared, publicKey, ciphered_privateKey, infos, (now.compareTo(deadLine) >= 0), mustBeReciphered, reminderValue);
            account.setLastUpdatedDate(lastUpdatedDate);
            return account;
        } catch (ParseException e) {
            throw new GeneralException(ServletManager.Code.InternError, "Parse error");
        }

    }

    public static Account createAccount(boolean shared, Map<String, String> informations, ServletManager sm) throws GeneralException {
        return createAccount(shared, informations, null, null, sm);
    }

    public static Account createAccount(boolean shared, Map<String, String> informations, Integer reminderValue, String reminderType, ServletManager sm) throws GeneralException {
        DataBaseConnection db = sm.getDB();
        int transaction = db.startTransaction();
        Map.Entry<String, String> publicAndPrivateKey = RSA.generateKeys();
        String publicKey = publicAndPrivateKey.getKey();
        String privateKey = publicAndPrivateKey.getValue();
        String ciphered_key = sm.getUser().encrypt(privateKey);
        DatabaseRequest request = db.prepareRequest("INSERT INTO accounts values (null, ?, default, ?, ?, ?, ?, 0);");
        request.setBoolean(shared);
        if (reminderValue != null && reminderType != null) {
            request.setInt(reminderValue);
            request.setString(reminderType);
        } else {
            request.setNull();
            request.setNull();
        }
        request.setString(publicKey);
        request.setString(ciphered_key);
        String db_id = request.set().toString();
        List<AccountInformation> infos = AccountInformation.createAccountInformations(db_id, informations, publicKey, sm);
        db.commitTransaction(transaction);
        Account account = new Account(db_id, shared, publicKey, ciphered_key, infos, (reminderValue == null ? 0 : reminderValue));
        account.setPrivateKey(privateKey);
        account.setLastUpdatedDate(new Date());
        return account;
    }

    public static Account createShareableAccount(Map<String, String> accountInformationMap, String deciphered_teamKey, Integer reminderValue, ServletManager sm) throws GeneralException {
        Map.Entry<String, String> publicAndPrivateKey = RSA.generateKeys();
        String publicKey = publicAndPrivateKey.getKey();
        String privateKey = publicAndPrivateKey.getValue();
        String ciphered_key = AES.encrypt(privateKey, deciphered_teamKey);
        DataBaseConnection db = sm.getDB();
        int transaction = db.startTransaction();
        DatabaseRequest request = db.prepareRequest("INSERT INTO accounts values (null, 0, default, ?, ?, ?, ?, 0);");
        if (reminderValue != null) {
            request.setInt(reminderValue);
            request.setString("MONTH");
        } else {
            request.setNull();
            request.setNull();
        }
        request.setString(publicKey);
        request.setString(ciphered_key);
        String db_id = request.set().toString();
        List<AccountInformation> accountInformationList = AccountInformation.createAccountInformations(db_id, accountInformationMap, publicKey, sm);
        db.commitTransaction(transaction);
        Account account = new Account(db_id, false, publicKey, ciphered_key, accountInformationList, (reminderValue == null ? 0 : reminderValue));
        account.setPrivateKey(privateKey);
        account.setLastUpdatedDate(new Date());
        return account;
    }

    public static Account createSharedAccount(List<AccountInformation> information, String deciphered_teamKey, ServletManager sm) throws GeneralException {
        DataBaseConnection db = sm.getDB();
        Map.Entry<String, String> publicAndPrivateKey = RSA.generateKeys();
        String publicKey = publicAndPrivateKey.getKey();
        String privateKey = publicAndPrivateKey.getValue();
        String ciphered_key = AES.encrypt(privateKey, deciphered_teamKey);
        int transaction = db.startTransaction();
        DatabaseRequest request = db.prepareRequest("INSERT INTO accounts values (null, 0, default, null, null, ?, ?, 1);");
        request.setString(publicKey);
        request.setString(ciphered_key);
        String db_id = request.set().toString();
        List<AccountInformation> accountInformationList = AccountInformation.createSharedAccountInformationList(db_id, information, publicKey, sm);
        db.commitTransaction(transaction);
        Account account = new Account(db_id, false, publicKey, ciphered_key, accountInformationList, true);
        account.setPrivateKey(privateKey);
        account.setLastUpdatedDate(new Date());
        return account;
    }

    public static Account createAccountSameAs(Account sameAccount, boolean shared, ServletManager sm) throws GeneralException {
        DataBaseConnection db = sm.getDB();
        int transaction = db.startTransaction();
        Map.Entry<String, String> publicAndPrivateKey = RSA.generateKeys();
        String publicKey = publicAndPrivateKey.getKey();
        String privateKey = publicAndPrivateKey.getValue();
        String ciphered_key = sm.getUser().encrypt(privateKey);
        DatabaseRequest request = db.prepareRequest("INSERT INTO accounts values (null, ?, default, null, null, ?, ?, 0);");
        request.setBoolean(shared);
        request.setString(publicKey);
        request.setString(ciphered_key);
        String db_id = request.set().toString();
        List<AccountInformation> infos = AccountInformation.createAccountInformationFromAccountInformations(db_id, sameAccount.getAccountInformations(), publicKey, sm);
        db.commitTransaction(transaction);
        Account account = new Account(db_id, shared, publicKey, ciphered_key, infos, sameAccount.getPasswordChangeInterval());
        account.setPrivateKey(privateKey);
        return account;
    }

	/*
     *
	 * Constuctor
	 * 
	 */

    protected String db_id;
    protected boolean shared;
    protected List<AccountInformation> infos;
    protected boolean passwordMustBeUpdated;
    protected String publicKey;
    protected String ciphered_key;
    protected String privateKey;
    protected Boolean mustBeReciphered;
    protected Integer passwordChangeInterval = 0; /* In month */
    protected String lastUpdatedDate;
    protected DateFormat databaseLastUpdatedDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected DateFormat printLastUpdatedDateFormat = new SimpleDateFormat("MMMM dd, HH:mm", Locale.US);

    public Account(String db_id, boolean shared, String publicKey, String ciphered_key, List<AccountInformation> infos, Integer passwordChangeInterval) {
        this.db_id = db_id;
        this.shared = shared;
        this.infos = infos;
        this.publicKey = publicKey;
        this.ciphered_key = ciphered_key;
        this.mustBeReciphered = false;
        this.passwordMustBeUpdated = false;
        this.privateKey = null;
        this.passwordChangeInterval = passwordChangeInterval;
    }

    public Account(String db_id, boolean shared, String publicKey, String ciphered_key, List<AccountInformation> infos, boolean mustBeReciphered) {
        this.db_id = db_id;
        this.shared = shared;
        this.infos = infos;
        this.publicKey = publicKey;
        this.ciphered_key = ciphered_key;
        this.mustBeReciphered = mustBeReciphered;
        this.passwordMustBeUpdated = false;
        this.privateKey = null;
        this.passwordChangeInterval = 0;
    }

    public Account(String db_id, boolean shared, String publicKey, String ciphered_key, List<AccountInformation> infos, boolean passwordMustBeUpdated, boolean mustBeReciphered, Integer passwordChangeInterval) {
        this.db_id = db_id;
        this.shared = shared;
        this.infos = infos;
        this.publicKey = publicKey;
        this.ciphered_key = ciphered_key;
        this.passwordMustBeUpdated = passwordMustBeUpdated;
        this.mustBeReciphered = mustBeReciphered;
        this.privateKey = null;
        this.passwordChangeInterval = passwordChangeInterval;
    }

    public void removeFromDB(ServletManager sm) throws GeneralException {
        DataBaseConnection db = sm.getDB();
        int transaction = db.startTransaction();
        for (AccountInformation info : infos) {
            info.removeFromDb(sm);
        }
        DatabaseRequest request = db.prepareRequest("DELETE FROM accounts WHERE id = ?;");
        request.setInt(db_id);
        request.set();
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

    public String getPublicKey() {
        return publicKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public boolean mustBeReciphered() {
        return this.mustBeReciphered;
    }

    public void decipher(User user) throws GeneralException {
        if (this.privateKey != null)
            return;
        this.privateKey = user.decrypt(this.ciphered_key);
        for (AccountInformation accountInformation : this.getAccountInformations())
            accountInformation.decipher(this.privateKey);
    }

    public void decipherWithTeamKeyIfNeeded(String deciphered_teamKey) throws GeneralException {
        if (this.privateKey != null)
            return;
        this.privateKey = AES.decrypt(this.ciphered_key, deciphered_teamKey);
        for (AccountInformation accountInformation : this.getAccountInformations())
            accountInformation.decipher(this.privateKey);
    }

    public List<AccountInformation> getAccountInformations() {
        return infos;
    }

    public List<AccountInformation> getAccountInformationsWithoutPassword() {
        List<AccountInformation> res = new LinkedList<AccountInformation>();
        for (AccountInformation info : this.infos) {
            if (info.getInformationName().equals("password"))
                continue;
            res.add(info);
        }
        return res;
    }

    public void setPassword(String password, User user, ServletManager sm) throws GeneralException {
        String cryptedPassword = user.encrypt(password);
        if (this.getInformationNamed("password") == null)
            throw new GeneralException(ServletManager.Code.ClientError, "This account does not have password field");
        for (AccountInformation info : this.infos) {
            if (info.getInformationName().equals("password"))
                info.setInformation_value(cryptedPassword, this.publicKey, sm);
        }
    }

    public String getCryptedPassword() throws GeneralException {
        String password = this.getInformationNamed("password");
        if (password == null)
            throw new GeneralException(ServletManager.Code.ClientError, "This account does not have password field");
        return password;
    }

    public Integer getPasswordChangeInterval() {
        return this.passwordChangeInterval;
    }

    public void setLastUpdatedDate(String lastUpdatedDate) throws GeneralException {
        try {
            this.lastUpdatedDate = printLastUpdatedDateFormat.format(databaseLastUpdatedDateFormat.parse(lastUpdatedDate));
        } catch (ParseException e) {
            throw new GeneralException(ServletManager.Code.InternError, e);
        }
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = printLastUpdatedDateFormat.format(lastUpdatedDate);
    }

    public String getLastUpdatedDate() {
        return this.lastUpdatedDate;
    }
	
	/*
	 * 
	 * Utils
	 * 
	 */

    public JSONObject getJSON(ServletManager sm) throws GeneralException {
        this.update_ciphering_if_needed(sm);
        this.decipher(sm.getUser());
        JSONObject obj = new JSONObject();
        for (AccountInformation info : this.infos)
            obj.put(info.getInformationName(), info.getInformationValue());
        return obj;
    }

    public JSONArray getInformationJson() {
        JSONArray jsonArray = new JSONArray();
        for (AccountInformation info : this.infos) {
            JSONObject tmp = new JSONObject();
            tmp.put("info_name", info.getInformationName());
            tmp.put("info_value", info.getInformationValue());
            jsonArray.add(tmp);
        }
        return jsonArray;
    }

    public JSONArray getJSON() {
        JSONArray res = new JSONArray();
        JSONObject tmp;
        for (AccountInformation info : this.infos) {
            if (info.getInformationName().equals("password"))
                continue;
            tmp = new JSONObject();
            tmp.put(info.getInformationName(), info.getInformationValue());
            res.add(tmp);
        }
        return res;
    }

    public JSONArray getJson() {
        JSONArray res = new JSONArray();
        JSONObject tmp;
        for (AccountInformation info : this.infos) {
            if (info.getInformationName().equals("password"))
                continue;
            tmp = new JSONObject();
            tmp.put("name", info.getInformationName());
            tmp.put("value", info.getInformationValue());
            res.add(tmp);
        }
        return res;
    }

    public void editInfos(Map<String, String> infos, ServletManager sm) throws GeneralException {
        String value;
        DataBaseConnection db = sm.getDB();
        int transaction = db.startTransaction();
        for (AccountInformation info : this.infos) {
            if ((value = infos.get(info.getInformationName())) != null) {
                if (info.getInformationName().equals("password")) {
                    DatabaseRequest request = db.prepareRequest("UPDATE accounts SET lastUpdateDate = NOW() WHERE id = ?;");
                    request.setInt(this.db_id);
                    request.set();
                    this.passwordMustBeUpdated = false;
                }
                info.setInformation_value(value, this.publicKey, sm);
            }
        }
        db.commitTransaction(transaction);
    }

    public void edit(JSONObject editJson, ServletManager sm) throws GeneralException {
        for (AccountInformation accountInformation : this.getAccountInformations()) {
            String new_info_value = (String) editJson.get(accountInformation.getInformationName());
            if (new_info_value == null)
                continue;
            accountInformation.setInformation_value(new_info_value, this.publicKey, sm);
        }
    }

    public String getInformationNamed(String info_name) {
        for (AccountInformation info : this.infos) {
            if (info.getInformationName().equals(info_name))
                return info.getInformationValue();
        }
        return null;
    }

    public JSONArray getInformationWithoutPasswordJson() {
        JSONArray res = new JSONArray();
        for (AccountInformation info : this.infos) {
            if (!info.getInformationName().equals("password")) {
                JSONObject tmp = new JSONObject();
                tmp.put("name", info.getInformationName());
                tmp.put("value", info.getInformationValue());
                res.add(tmp);
            }
        }
        return res;
    }

    public boolean mustUpdatePassword() {
        return this.passwordMustBeUpdated;
    }

    public void update_ciphering_if_needed(ServletManager sm) throws GeneralException {
        if (this.publicKey != null && this.ciphered_key != null) {
            return;
        }
        Map.Entry<String, String> publicAndPrivateKey = RSA.generateKeys();
        this.publicKey = publicAndPrivateKey.getKey();
        this.privateKey = publicAndPrivateKey.getValue();
        this.ciphered_key = sm.getUser().encrypt(this.privateKey);
        DataBaseConnection db = sm.getDB();
        int transaction = db.startTransaction();
        DatabaseRequest request = db.prepareRequest("UPDATE accounts SET publicKey = ?, privateKey = ? WHERE id = ?;");
        request.setString(this.publicKey);
        request.setString(this.ciphered_key);
        request.setInt(this.db_id);
        request.set();
        for (AccountInformation accountInformation : this.getAccountInformations())
            accountInformation.update_ciphering(this.publicKey, sm);
        db.commitTransaction(transaction);

    }
}
