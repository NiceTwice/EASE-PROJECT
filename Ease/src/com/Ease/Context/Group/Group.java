package com.Ease.Context.Group;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;


import com.Ease.Dashboard.App.GroupApp;
import com.Ease.Dashboard.Profile.GroupProfile;
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
		Group group = new Group(String.valueOf(db_id), name, parent, infra, idGenerator.getNextId());
		GroupManager.getGroupManager(sm).add(group);
		return group;
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
			List<GroupApp> groupApps;
			while (rs.next()) {
				db_id = rs.getString(Data.ID.ordinal());
				System.out.println(db_id);
				name = rs.getString(Data.NAME.ordinal());
				single_id = idGenerator.getNextId();
				Group child = new Group(db_id, name, parent, infra, single_id);
				child.setChildrens(loadGroup(db, child, infra, context));
				groupProfiles = GroupProfile.loadGroupProfiles(child, db, context);
				child.setGroupProfiles(groupProfiles);	
				groupApps = GroupApp.loadGroupApps(child, db, context);
				child.setGroupApps(groupApps);
				groups.add(child);
				GroupManager.getGroupManager(context).add(child);
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
	protected List<GroupApp> groupApps;
	protected Infrastructure infra;
	protected int	single_id;
	
	public Group(String db_id, String name, Group parent, Infrastructure infra, int single_id) {
		this.db_id = db_id;
		this.name = name;
		this.children = null;
		this.parent = parent;
		this.infra = infra;
		this.single_id = single_id;
		this.groupProfiles = null;
		this.groupApps = null;
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
	private void setGroupApps(List<GroupApp> groupApps) {
		this.groupApps = groupApps;
	}
	public List<Group> getChildren() {
		return this.children;
	}
	public List<GroupProfile> getGroupProfiles() {
		return this.groupProfiles;
	}
	public List<GroupApp> getGroupApps() {
		return this.groupApps;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name, ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		db.set("UPDATE groups SET name='" + name + "' WHERE id=" + this.db_id + ";");
		this.name = name;
	}
	
	public Infrastructure getInfra() {
		return infra;
	}
	
	/*
	 * 
	 * Utils 
	 * 
	 */
	
	public void removeFromDb(ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		int transaction = db.startTransaction();
		for (Group group : children) {
			group.removeFromDb(sm);
		}
		try {	
			Map<String, User> users = (Map<String, User>) sm.getContextAttr("users");
			ResultSet rs = db.get("SELECT email, user_id FROM groupsAndUsersMap JOIN users ON user_id = users.id WHERE group_id=" + this.db_id + ";");
			while (rs.next()) {
				String email  = rs.getString(1);
				String user_id = rs.getString(2);
				User user;
				if ((user = users.get(email)) != null) {
					this.removeContentForConnectedUser(user, sm);
				} else {
					this.removeContentForUnconnectedUser(user_id, sm);
				}
			}
			rs = db.get("SELECT user_id FROM groupsAndUsersMap WHERE group_id = " + this.db_id + ";");
			while (rs.next()) {
				if (this.parent != null) {
					db.set("UPDATE groupsAndUsersMap SET group_id=" + this.parent.db_id + " WHERE group_id=" + this.db_id + " AND user_id = " + rs.getString(1) + ";");
				} else {
					db.set("DELETE FROM groupsAndUsersMap WHERE group_id=" + this.db_id + " AND user_id = " + rs.getString(1) + ";");
				}
			}
			for (GroupProfile groupProfile : this.groupProfiles) {
				groupProfile.removeFromDb(sm);
			}
			for (GroupApp groupApp : this.groupApps) {
				groupApp.removeFromDb(sm);
			}
			GroupManager.getGroupManager(sm).remove(this);
			db.set("DELETE FROM groups WHERE id=" + this.db_id + ";");
			db.commitTransaction(transaction);
		} catch (SQLException e) {
			throw new GeneralException(ServletManager.Code.InternError, e);
		}
	}
	
	private void loadContentForConnectedUser(User user, ServletManager sm) throws GeneralException {
		for (GroupProfile groupProfile: this.groupProfiles) {
			groupProfile.loadContentForConnectedUser(user, sm);
		}
		for (GroupApp groupApp: this.groupApps) {
			groupApp.loadContentForConnectedUser(user, sm);
		}
	}
	private void loadContentForUnconnectedUser(String db_id, ServletManager sm) throws GeneralException {
		for (GroupProfile groupProfile: this.groupProfiles) {
			groupProfile.loadContentForUnconnectedUser(db_id, sm);
		}
		for (GroupApp groupApp: this.groupApps) {
			groupApp.loadContentForUnconnectedUser(db_id, sm);
		}
	}
	private void removeContentForConnectedUser(User user, ServletManager sm) throws GeneralException {
		for (GroupProfile groupProfile: this.groupProfiles) {
			groupProfile.removeContentForConnectedUser(user, sm);
		}
		for (GroupApp groupApp: this.groupApps) {
			groupApp.removeContentForConnectedUser(user, sm);
		}
	}
	private void removeContentForUnconnectedUser(String db_id, ServletManager sm) throws GeneralException {
		for (GroupProfile groupProfile: this.groupProfiles) {
			groupProfile.removeContentForUnconnectedUser(db_id, sm);
		}
		for (GroupApp groupApp: this.groupApps) {
			groupApp.removeContentForUnconnectedUser(db_id, sm);
		}
	}
	
	private void loadAllContentUnconnected(String db_id, ServletManager sm) throws GeneralException {
		if (this.parent != null)
			this.parent.loadAllContentUnconnected(db_id, sm);
		this.loadContentForUnconnectedUser(db_id, sm);
	}
	
	private void loadAllContentConnected(User user, ServletManager sm) throws GeneralException {
		if (this.parent != null)
			this.parent.loadAllContentConnected(user, sm);
		this.loadContentForConnectedUser(user, sm);
	}
	
	public void addUser(String email, ServletManager sm) throws GeneralException {
		Map<String, User> users = (Map<String, User>) sm.getContextAttr("users");
		DataBaseConnection db = sm.getDB();
		int transaction = db.startTransaction();
		String db_id = User.findDBid(email, sm);
		User user;
		Group group;
		if ((user = users.get(email)) == null) {
			this.loadAllContentConnected(user, sm);
		} else {
			this.loadAllContentUnconnected(db_id, sm);
		}
		db.set("INSERT INTO groupsAndUsersMap VALUES(NULL, " + this.db_id + ", " + db_id + ");");
		db.commitTransaction(transaction);
	}
}
