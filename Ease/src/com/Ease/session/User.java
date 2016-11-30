package com.Ease.session;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import com.Ease.context.DataBase;
import com.Ease.context.SiteManager;
import com.Ease.data.AES;
import com.Ease.data.Hashing;
import com.Ease.session.update.Update;

public class User {
	public enum UserData {
		NOTHING,
		ID,
		FIRSTNAME,
		LASTNAME,
		EMAIL,
		TEL,
		PASSWORD,
		SALTEASE,
		SALTPERSO,
		KEYUSER,
		TUTO,
		BACKGROUND
	}
	String			id;
	String			firstName;
	String			lastName;
	String			email;
	String			tel;
	String			hashedPassword;
	String			saltEase;
	String			saltPerso;
	String			keyUser;
	String			tuto;
	String 			background;
	List<Profile>	profiles;
	List<List<Profile>>		profilesDashboard;
	List<String>	group_ids;
	Map<String,Boolean>	emails;
	PersonalSiteManager personalSiteManager;
	
	int				maxProfileId;
	int				maxAppId;
	List<App>		apps;
	List<Update>	updates;
	Integer			maxKnowId = 0;
	
	//Use this to create a new user and set it in database
	public User(String fName, String lName, String email, String tel, String pass, ServletContext context) throws SessionException {
		DataBase db = (DataBase)context.getAttribute("DataBase");
		String hashedPassword;
		String keyCrypted;
		if ((saltEase = Hashing.generateSalt()) == null) {
			throw new SessionException("Can't create salt.");
		} else if ((saltPerso = AES.generateSalt()) == null) {
			throw new SessionException("Can't create salt.");
		} else if ((keyUser = AES.keyGenerator()) == null) {
			throw new SessionException("Can't create key.");
		} else if ((keyCrypted = AES.encryptUserKey(keyUser, pass, saltPerso)) == null) {
			throw new SessionException("Can't encrypt key.");
		} else if ((hashedPassword = Hashing.SHA(pass, saltEase)) == null) {
			throw new SessionException("Can't hash password.");
		} 
		try {
			db.set("INSERT INTO users VALUES (NULL, '" + fName + "', '" + lName + "', '" + email + "', '" + tel
					+ "', '" + hashedPassword + "', '" + saltEase + "', '" + saltPerso + "', '" + keyCrypted
					+ "', 0, 1);");
			this.firstName = fName;
			this.lastName = lName;
			this.email = email;
			this.tel = tel;
			this.hashedPassword = hashedPassword;
			this.tuto = "0";
			this.background = "logo";
			profiles = new LinkedList<Profile>();
			profilesDashboard = new LinkedList<List<Profile>>();
			for (int i = 0; i < 5; ++i) {
				profilesDashboard.add(new LinkedList<Profile>());
			}
			maxProfileId = 0;
			maxAppId = 0;
			apps = new LinkedList<App>();
			updates = new LinkedList<Update>();
			group_ids = new LinkedList<String>();
			ResultSet rs = db.get("SELECT user_id FROM users WHERE email='" + email + "';");
			
			rs.next();
			this.id = rs.getString(1);
			Profile profile = new Profile("Side", "#FFFFFF", "", this, null, context, true);
			this.profiles.add(profile);
			this.profilesDashboard.get(0).add(profile);
			profile = new Profile("Perso", "#35a7ff", "", this, null, context, false);
			this.profiles.add(profile);
			this.profilesDashboard.get(1).add(profile);
			loadEmails(context);
		} catch (SQLException e) {
			throw new SessionException("Impossible to insert new user in data base.");
		}
	}

	// Use this to load user with a ResultSet from database
	public User(ResultSet rs, String pass, ServletContext context) throws SessionException {
		try {
			id = rs.getString(UserData.ID.ordinal());
			firstName = rs.getString(UserData.FIRSTNAME.ordinal());
			lastName = rs.getString(UserData.LASTNAME.ordinal());
			email = rs.getString(UserData.EMAIL.ordinal());
			tel = rs.getString(UserData.TEL.ordinal());
			tuto = rs.getString(UserData.TUTO.ordinal());
			background = (rs.getString(UserData.BACKGROUND.ordinal()).equals("1")) ? "picture" : "logo";
			profiles = new LinkedList<Profile>();
			profilesDashboard = new LinkedList<List<Profile>>();
			for (int i = 0; i < 5; ++i) {
				profilesDashboard.add(new LinkedList<Profile>());
			}
			maxProfileId = 0;
			maxAppId = 0;
			apps = new LinkedList<App>();
			updates = new LinkedList<Update>();
			group_ids = new LinkedList<String>();

			hashedPassword = rs.getString(UserData.PASSWORD.ordinal());
			saltEase = rs.getString(UserData.SALTEASE.ordinal());
			saltPerso = rs.getString(UserData.SALTPERSO.ordinal());

			if (!hashedPassword.equals(Hashing.SHA(pass, saltEase))) {
				throw new SessionException("Wrong password.");
			} else if ((keyUser = AES.decryptUserKey(rs.getString(UserData.KEYUSER.ordinal()), pass,
					saltPerso)) == null) {
				throw new SessionException("Can't decrypt key.");
			}

			loadProfiles(context);
			if (this.profilesDashboard.get(0).isEmpty()) {
				Profile profile = new Profile("Side", "#FFFFFF", "", this, null, context, true);
				this.profiles.add(profile);
				this.profilesDashboard.get(0).add(profile);
			}
			checkForGroup(context);
			loadEmails(context);
		} catch (SQLException e) {
			throw new SessionException("Impossible to get all User info.");
		}
	}

	// Use this to load user from result set but can't decrypt (direct set of
	// keyuser)
	public User(ResultSet rs, String keyUser, String userId, ServletContext context) throws SessionException {// userId
		try {
			id = rs.getString(UserData.ID.ordinal());
			if (!userId.equals(id)) {
				System.out.println(userId);
				System.out.println(id);
				throw new SessionException("Wrong user");
			}
			firstName = rs.getString(UserData.FIRSTNAME.ordinal());
			lastName = rs.getString(UserData.LASTNAME.ordinal());
			email = rs.getString(UserData.EMAIL.ordinal());
			tel = rs.getString(UserData.TEL.ordinal());
			hashedPassword = rs.getString(UserData.PASSWORD.ordinal());
			saltEase = rs.getString(UserData.SALTEASE.ordinal());
			saltPerso = rs.getString(UserData.SALTPERSO.ordinal());
			tuto = rs.getString(UserData.TUTO.ordinal());
			background = (rs.getString(UserData.BACKGROUND.ordinal()).equals("1")) ? "picture" : "logo";
			profiles = new LinkedList<Profile>();
			profilesDashboard = new LinkedList<List<Profile>>();
			for (int i = 0; i < 5; ++i) {
				profilesDashboard.add(new LinkedList<Profile>());
			}
			maxProfileId = 0;
			maxAppId = 0;
			apps = new LinkedList<App>();
			updates = new LinkedList<Update>();
			group_ids = new LinkedList<String>();

			this.keyUser = keyUser;

			loadProfiles(context);
			if (this.profilesDashboard.get(0).isEmpty()) {
				Profile profile = new Profile("Side", "#FFFFFF", "", this, null, context, true);
				this.profiles.add(profile);
				this.profilesDashboard.get(0).add(profile);
			}
			checkForGroup(context);
			loadEmails(context);
		} catch (SQLException e) {
			throw new SessionException("Impossible to get all User info.");
		}
	}

	// GETTER

	public String getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getTel() {
		return tel;
	}

	public String getHashedPassword() {
		return hashedPassword;
	}

	public List<Profile> getProfiles() {
		return profiles;
	}

	public String getSaltEase() {
		return saltEase;
	}

	public String getSaltPerso() {
		return saltPerso;
	}

	public String getUserKey() {
		return keyUser;
	}

	public String getTuto() {
		return tuto;
	}

	public String getBackground() {
		return background;
	}

	public int getNextProfileId() {
		maxProfileId++;
		return maxProfileId - 1;
	}

	public int getNextAppId() {
		maxAppId++;
		return maxAppId;
	}
	public String getNextKnowId() {
		maxKnowId++;
		return maxKnowId.toString();
	}

	public List<App> getApps() {
		return apps;
	}
	public List<Update> getUpdates() {
		return updates;
	}

	public App getApp(int id) {
		int i = 0;
		while (i < apps.size()) {
			if (id == apps.get(i).getAppId())
				return apps.get(i);
			i++;
		}
		return null;
	}

	public Profile getProfile(int id) {
		int i = 0;
		while (i < profiles.size()) {
			if (id == profiles.get(i).getProfileId())
				return profiles.get(i);
			i++;
		}
		return null;
	}

	public List<String> getGroupIds() {
		return group_ids;
	}
	
	public Map<String, Boolean> getEmails() {
		return emails;
	}
	
	public List<String> getVerifiedEmails() {
		List<String> res = new LinkedList<String>();
		Iterator<Map.Entry<String, Boolean>> it = emails.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, Boolean> entry = it.next();
			if (entry.getValue())
				res.add(entry.getKey());
		}
		return res;
	}
	public List<String> getUnverifiedEmails() {
		List<String> res = new LinkedList<String>();
		Iterator<Map.Entry<String, Boolean>> it = emails.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, Boolean> entry = it.next();
			if (!entry.getValue())
				res.add(entry.getKey());
		}
		return res;
	}
	public List<List<Profile>> getProfilesDashboard () {
		return this.profilesDashboard;
	}

	// SETTER

	public void setFirstName(String fName) {
		firstName = fName;
	}

	public void setLastName(String lName) {
		lastName = lName;
	}

	public void setEmail(String mail) {
		email = mail;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public void setHashedPassword(String pass) {
		hashedPassword = pass;
	}

	public void tutoComplete() {
		this.tuto = "1";
	}

	public void changeBackground() {
		if (this.background == "picture") {
			this.background = "logo";
		} else {
			this.background = "picture";
		}
	}

	// UTILS

	public void addEmailIfNotPresent(String newEmail) {
		if (!emails.containsKey(newEmail))
			emails.put(newEmail, false);
	}
	
	public void validateEmail(String email) {
		emails.put(email, true);
	}
	
	public void removeEmail(String email) {
		emails.remove(email);
	}
	
	public void loadProfiles(ServletContext context) throws SessionException {
		DataBase db = (DataBase) context.getAttribute("DataBase");
		try {
			ResultSet rs = db.get("SELECT * FROM profiles WHERE user_id='" + id + "';");
			while (rs.next()) {
				Profile profile = new Profile(rs, this, context);
				profiles.add(profile);
				profilesDashboard.get(profile.getColumnIdx()).add(profile);
			}
			profiles.sort(new Comparator<Profile>() {
				@Override
				public int compare(Profile a, Profile b) {
					return a.getIndex() - b.getIndex();
				}
			});
			for (int i = 1; i < 5; ++i) {
				profilesDashboard.get(i).sort(new Comparator<Profile>() {
					@Override
					public int compare(Profile a, Profile b) {
						return a.getProfileIdx() - b.getProfileIdx();
					}
				});
			}
			updateIndex(context);
		} catch (SQLException e) {
			throw new SessionException("Impossible to load all profiles.");
		}
	}

	public void addProfile(Profile profile) {
		profiles.add(profile);
		int column = -1;
		int minSize = -1;
		int size;
		for (int j = 1; j < 5; ++j) {
			size = 0;
			for (int i = 0; i < profilesDashboard.get(j).size(); ++i){
				size += profilesDashboard.get(j).get(i).getYSize();
			}
			if (minSize == -1 || minSize > size) {
				column = j;
				minSize = size;
			}
		}
		profilesDashboard.get(column).add(profile);
	}
	public int getMostEmptyColumn() {
		int column = -1;
		int minSize = -1;
		int size;
		for (int j = 1; j < 5; ++j) {
			size = 0;
			for (int i = 0; i < profilesDashboard.get(j).size(); ++i){
				size += profilesDashboard.get(j).get(i).getYSize();
			}
			if (minSize == -1 || minSize > size) {
				column = j;
				minSize = size;
			}
		}
		return column;
	}

	public void updateInDB(ServletContext context, String pass) throws SessionException {
		DataBase db = (DataBase) context.getAttribute("DataBase");
		String cryptedKeyUser = AES.encryptUserKey(keyUser, pass, saltPerso);
		try {
			db.set("UPDATE users SET firstName='" + firstName + "', `lastName`='" + lastName + "', email='" + email
				+ "', `tel`='" + tel + "', `password`='" + hashedPassword + "', `keyUser`='" + cryptedKeyUser
				+ "', saltEase='" + saltEase + "', saltPerso='" + saltPerso + "' WHERE `user_id`='" + id + "';");
		} catch (SQLException e) {
			throw new SessionException("Impossible to update user in data base.");
		}
	}

	public void updateInDB(ServletContext context) throws SessionException {
		DataBase db = (DataBase) context.getAttribute("DataBase");
		
		try {
			db.set("UPDATE users SET firstName='" + firstName + "', `lastName`='" + lastName + "', email='" + email
				+ "', `tel`='" + tel + "', `tuto`='" + tuto + "', bckgrndPic=" + ((background == "picture") ? "1" : "0")
				+ " WHERE `user_id`='" + id + "';");
		} catch (SQLException e) {
			throw new SessionException("Impossible to update user in data base.");
		}
	}

	public void deleteFromDB(ServletContext context) throws SessionException {
		DataBase db = (DataBase) context.getAttribute("DataBase");
		for (int i = 0; i < profiles.size(); ++i) {
			profiles.get(i).deleteFromDB(context);
		}
		try {
			db.set("DELETE FROM users WHERE user_id='" + id + "';");
		} catch (SQLException e) {
			throw new SessionException("Impossible to delete user in data base.");
		}
	}

	public void updateIndex(ServletContext context) throws SessionException {
		for (int i = 0; i < profiles.size(); ++i) {
			if (profiles.get(i).getIndex() != i) {
				profiles.get(i).setIndex(i);
				profiles.get(i).updateInDB(context);
			}
		}
		for (int j = 1; j < 5; ++j) {
			for (int i = 0; i < profilesDashboard.get(j).size(); ++i) {
				if (profilesDashboard.get(j).get(i).getProfileIdx() != i || profilesDashboard.get(j).get(i).getColumnIdx() != j) {
					profilesDashboard.get(j).get(i).setProfileIdx(i);
					profilesDashboard.get(j).get(i).setColumnIdx(j);
					profilesDashboard.get(j).get(i).updateInDB(context);
				}
			}
		}
	}

	public void moveProfileAt(ServletContext context, int profileIndex, int index) throws SessionException {

		Profile profile = profiles.get(profileIndex);
		profiles.remove(profileIndex);
		profiles.add(index, profile);
		updateIndex(context);
	}
	
	public void moveProfileAt(ServletContext context, Profile profile, int columnIdx, int profileIdx) throws SessionException {

		profilesDashboard.get(profile.getColumnIdx()).remove(profile);
		profilesDashboard.get(columnIdx).add(profileIdx, profile);
		updateIndex(context);
	}

	public Boolean isAdmin(ServletContext context) {
		DataBase db = (DataBase) context.getAttribute("DataBase");

		try {
			db.connect();
			ResultSet rs;
			rs = db.get("select * from admins where email = '" + email + "';");
			if(rs.next())
				return true;
			else
				return false;
		} catch (SQLException e) {
			return false;
		}
	}

	public void checkForGroup(ServletContext context) throws SessionException {
		DataBase db = (DataBase) context.getAttribute("DataBase");

		try {
			db.connect();
			ResultSet rs;
			rs = db.get("select group_id from GroupAndUserMap where user_id=" + id + ";");
				while (rs.next()) {
					String group_id = rs.getString(1);
					loadGroup(context, group_id);
					group_ids.add(group_id);
				}
		} catch (SQLException e) {
			throw new SessionException("Can't get groups. 0");
		}
	}

	public Profile haveThisCustomProfile(String cust) {
		for (int i = 0; i < profiles.size(); ++i) {
			if (profiles.get(i).isCustom(cust) == true)
				return profiles.get(i);
		}
		return null;
	}

	public App haveThisCustomApp(String cust) {
		for (int i = 0; i < apps.size(); ++i) {
			if (apps.get(i).isCustom(cust) == true)
				return apps.get(i);
		}
		return null;
	}

	public Profile loadGroup(ServletContext context, String group_id) throws SessionException {
		DataBase db = (DataBase) context.getAttribute("DataBase");

		try {
			ResultSet rs;
			if ((rs = db.get("select * from groups where id=" + group_id + ";")) == null) {
				throw new SessionException("Can't get groups. 1");
			}
			rs.next();
			String parent = rs.getString(3);
			Profile profile = null;
			if (parent != null && !parent.equals("null")) {
				profile = loadGroup(context, parent);
			}
			if ((rs = db.get("select * from customProfiles where group_id=" + group_id + ";")) == null) {
				throw new SessionException("Can't get groups. 2");
			}
			if (rs.next()) {
				String customProfileId = rs.getString(1);
				if ((profile = haveThisCustomProfile(customProfileId)) == null) {
					profile = new Profile(rs.getString(2), rs.getString(3), "", this, customProfileId, context, false);
					addProfile(profile);
				}
			}
			if ((rs = db.get("select * from AppAndGroupMap where group_id=" + group_id + ";")) == null) {
				throw new SessionException("Can't get groups. 3");
			}
			while (rs.next()) {
				String customAppId = rs.getString(2);
				App app = null;
				if ((app = haveThisCustomApp(customAppId)) == null) {
					ResultSet rs2 = db.get("select * from customApps where id=" + customAppId + ";");
					rs2.next();
					String websiteId = rs2.getString(2);
					if (websiteId != null) {
						app = new App(rs2.getString(4), ((SiteManager) context.getAttribute("siteManager")).get(websiteId), profile, customAppId, this, context);
					} else {
						ResultSet rs3 = db.get("select * from celcatId where email='" + email + "';");
						if (rs3.next())
							app = new App(rs2.getString(4), rs3.getString(2), profile, customAppId, this, context);
					}
					if (app != null) {
						apps.add(app);
						profile.addApp(app);
					}
				}
			}
			return profile;
		} catch (SQLException e) {
			throw new SessionException("Can't get groups. 3");
		}
	}
	
	public void loadEmails(ServletContext context) throws SessionException {
		DataBase db = (DataBase)context.getAttribute("DataBase");
		emails = new HashMap<>();
		emails.put(this.email, true);
		try{
			ResultSet rs;
			rs = db.get("select email, verified from usersEmails where user_id=" + this.id + ";");
			while (rs.next()) {
				emails.put(rs.getString(1), (rs.getString(2).equals("0")) ? false : true);
			}
		} catch (SQLException e) {
			throw new SessionException("Can't get emails.");
		}
	}
	
	public void setPersonalSiteManager(ServletContext context) throws SessionException {
		SiteManager siteManager = (SiteManager)context.getAttribute("siteManager");
		personalSiteManager = new PersonalSiteManager(siteManager);
	}
	
	public PersonalSiteManager getPersonalSiteManager() {
		return personalSiteManager;
	}
}