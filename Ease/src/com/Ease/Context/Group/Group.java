package com.Ease.Context.Group;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.json.simple.JSONObject;

import com.Ease.Dashboard.Profile.Profile;
import com.Ease.Dashboard.User.User;
import com.Ease.Utils.DataBaseConnection;
import com.Ease.Utils.GeneralException;
import com.Ease.Utils.IdGenerator;
import com.Ease.Utils.ServletManager;

public class Group {
	enum Data {
		NOTHING,
		ID,
		NAME,
		PARENT
	}
	
	/*
	 * 
	 * Loader and Creator
	 * 
	 */
	
	public static Group createGroup(String name, Group parent, Infrastructure infra, ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		String parent_id = (parent == null) ? "null" : parent.getDBid();
		String infra_id = (infra == null) ? "null" : infra.getDBid();
		IdGenerator idGenerator = (IdGenerator)sm.getContextAttr("idGenerator");
		int db_id = db.set("INSERT INTO groups values (null, '" + name + "', " + parent_id + ", " + infra_id + ");");
		return new Group(String.valueOf(db_id), name, parent, infra, idGenerator.getNextId());
	}
	
	public static List<Group> loadGroups(DataBaseConnection db, Infrastructure infra, ServletContext context)throws GeneralException {
		return loadGroup(db, null, infra, context);
	}
	
	@SuppressWarnings("unchecked")
	public static List<Group> loadGroup(DataBaseConnection db, Group parent, Infrastructure infra, ServletContext context) throws GeneralException {
		try {
			IdGenerator idGenerator = (IdGenerator)context.getAttribute("idGenerator");
			List<Group> groups = new LinkedList<Group>();
			ResultSet rs = db.get("SELECT * FROM groups WHERE parent " + ((parent == null) ? " IS NULL" : ("="+parent.getDBid())) + " AND infra_id=" + infra.getDBid() + ";");
			String db_id;
			String name;
			int single_id;
			List<GroupProfile> groupProfiles;
			while (rs.next()) {
				db_id = rs.getString(Data.ID.ordinal());
				System.out.println(db_id);
				name = rs.getString(Data.NAME.ordinal());
				single_id = idGenerator.getNextId();
				Group child = new Group(db_id, name, parent, infra, single_id);
				groupProfiles = GroupProfile.loadGroupProfiles(child, db, context);
				child.setChildrens(loadGroup(db, child, infra, context));
				child.setGroupProfiles(groupProfiles);
				groups.add(child);
				((Map<Integer, Group>)context.getAttribute("groups")).put(single_id, child);
			}
			return groups;
		} catch (SQLException e) {
			throw new GeneralException(ServletManager.Code.InternError, e);
		}
	}
	
	
	/*
	 * 
	 * Constructor
	 * 
	 */

	protected String db_id;
	protected String name;
	protected List<Group> children;
	protected Group		parent;
	protected List<GroupProfile> groupProfiles;
	protected Infrastructure infra;
	protected int	single_id;
	
	public Group(String db_id, String name, Group parent, Infrastructure infra, int single_id) {
		this.db_id = db_id;
		this.name = name;
		this.children = null;
		this.parent = parent;
		this.infra = infra;
		this.single_id = single_id;
	}
	
	/*
	 * 
	 * Getter and Setter
	 * 
	 */
	
	public String getDBid() {
		return this.db_id;
	}
	
	public int getSingleId() {
		return this.single_id;
	}
	
	public void setChildrens(List<Group> children) {
		this.children = children;
	}
	private void setGroupProfiles(List<GroupProfile> groupProfiles) {
		this.groupProfiles = groupProfiles;
	}
	public List<Group> getChildren() {
		return this.children;
	}
	public List<GroupProfile> getGroupProfiles() {
		return this.groupProfiles;
	}
	
	public String getName() {
		return name;
	}
	public Infrastructure getInfra() {
		return infra;
	}
	
	/*
	 * 
	 * Utils 
	 * 
	 */
	
	/*public static Map<String, Group> getGroupMap(List<Group> groupTrees) {
		Map<String, Group> groupsMap = new HashMap<String, Group>();
		for (Group group : groupTrees) {
			groupsMap.put(group.getDBid(), group);
			groupsMap.putAll(getGroupMap(group.getChildren()));
		}
		return groupsMap;
	}
	
	public static Map<String, GroupProfile> getGroupProfileMap(List<Group> groupTrees) {
		Map<String, GroupProfile> groupProfileMap = new HashMap<String, GroupProfile>();
		for (Group group : groupTrees) {
			for (GroupProfile groupProfile : group.getGroupProfiles()) {
				groupProfileMap.put(groupProfile.getDBid(), groupProfile);
			}
			groupProfileMap.putAll(getGroupProfileMap(group.getChildren()));
		}
		return groupProfileMap;
	}*/

	public void loadContentForConnectedUser(User user, ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		int transaction = db.startTransaction();
		if (this.parent != null)
			parent.loadContentForConnectedUser(user, sm);
		int mostEmptyColumn;
		for (GroupProfile gProfile : this.groupProfiles) {
			mostEmptyColumn = user.getMostEmptyProfileColumn();
			user.getProfilesColumn().get(mostEmptyColumn).add(Profile.createProfileWithGroup(user, mostEmptyColumn, user.getProfilesColumn().get(mostEmptyColumn).size(), gProfile, sm));
		}
		db.commitTransaction(transaction);
	}
	
	public void loadContentForUnconnectedUser(String db_id, ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		int transaction = db.startTransaction();
		if (this.parent != null)
			this.parent.loadContentForUnconnectedUser(db_id, sm);
		for (GroupProfile groupProfile : this.groupProfiles) {
			int columnIdx = User.getMostEmptyProfileColumnForUnconnected(db_id, sm);
			int posIdx = User.getColumnNextPositionForUnconnected(db_id, columnIdx, sm);
			Profile.createProfileWithGroupForUnconnected(db_id, columnIdx, posIdx, groupProfile, sm);
		}
		db.commitTransaction(transaction);
	}
	
	public void removeFromDb(ServletManager sm) throws GeneralException, SQLException {
		DataBaseConnection db = sm.getDB();
		int transaction = db.startTransaction();
		Map<String, User> users = (Map<String, User>) sm.getContextAttr("users");
		Iterator<User> it = users.values().iterator();
		while (it.hasNext())
			removeContentForConnectedUser(it.next(), sm);
		ResultSet rs = db.get("SELECT email, user_id FROM groupsAndUsersMap JOIN users ON user_id = users.id WHERE group_id=" + this.db_id + ";");
		while (rs.next()) {
			String email = rs.getString(1);
			String user_id = rs.getString(2);
			if (users.containsKey(email))
				this.removeContentForConnectedUser(users.get(email), sm, false);
			else
				this.removeContentForUnconnectedUser(user_id, sm, false);
		}
		db.commitTransaction(transaction);
	}
	
	public void removeContentForConnectedUser(User user, ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		int transaction = db.startTransaction();
		for (List<Profile> column : user.getProfilesColumn()) {
			for (Profile profile : column) {
				GroupProfile groupProfile = profile.getGroupProfile();
				if (groupProfile != null && groupProfile.getGroup() == this) {
					profile.removeFromDB(sm);
					column.remove(profile);
				}
			}
		}
		user.updateProfilesIndex(sm);
		if (this.parent != null)
			this.parent.removeContentForConnectedUser(user, sm);
		db.commitTransaction(transaction);
	}
	
	public void removeContentForUnconnectedUser(String db_id, ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		int transaction = db.startTransaction();
		try {
			for (GroupProfile groupProfile : this.groupProfiles) {	
				ResultSet rs = db.get("SELECT * FROM profiles WHERE user_id=" + db_id + " AND group_profile_id=" + groupProfile.getDBid() + ";");
				Profile.removeProfileForUnconnected(rs.getString(Profile.Data.ID.ordinal()), (groupProfile.isCommon()) ? null : rs.getString(Profile.Data.PROFILE_INFO_ID.ordinal()), sm);
			}
		} catch (SQLException e) {
			throw new GeneralException(ServletManager.Code.InternError, e);
		}
		if (this.parent != null)
			this.removeContentForUnconnectedUser(db_id, sm);
		db.commitTransaction(transaction);
	}
	
	public String toString() {
		return (this.db_id + " : " + this.name);
	}
	
	@SuppressWarnings("unchecked")
	public void addUser(String email, ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		User user;
		String userDBid;
		int transaction = db.startTransaction();
		if ((user = ((Map<String, User>)sm.getContextAttr("users")).get(email)) == null) {
			userDBid = User.findDBid(email, sm);
			this.loadContentForUnconnectedUser(userDBid, sm);
		} else {
			userDBid = user.getDBid();
			this.loadContentForConnectedUser(user, sm);
		}
		db.set("INSERT INTO groupsAndUsersMap values (null, " + this.db_id + ", " + userDBid + ");");
		db.commitTransaction(transaction);
	}
	
	@SuppressWarnings("unchecked")
	public void removeUser(String email, ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		User user;
		String userDBid;
		int transaction = db.startTransaction();
		if ((user = ((Map<String, User>)sm.getContextAttr("users")).get(email)) == null) {
			userDBid = User.findDBid(email, sm);
			this.removeContentForUnconnectedUser(userDBid, sm);
		} else {
			userDBid = user.getDBid();
			this.removeContentForConnectedUser(user, sm);
		}
		db.set("DELETE FROM groupsAndUsersMap WHERE group_id=" + this.db_id + " AND user_id=" + userDBid + ";");
		db.commitTransaction(transaction);
	}
	
	public JSONObject getJSON() {
		JSONObject res = new JSONObject();
		
		return res;
	}
	
	public String getJSONString() {
		return this.getJSON().toString();
	}
}
