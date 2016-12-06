package com.Ease.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import com.Ease.Context.Group.Group;
import com.Ease.Utils.Crypto.CodeGenerator;

public class Invitation {

	public static List<Group> verifyInvitation(String email, String invitationCode, ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		List<Group> groups = new LinkedList<Group>();
		@SuppressWarnings("unchecked")
		Map<String, Group> allGroups = (Map<String, Group>) sm.getContextAttr("groups");
		ResultSet rs = db.get("SELECT id FROM invitations WHERE email='" + email + "' AND linkCode='" + invitationCode + "';");
		try {
			if (rs.next()) {
				String id = rs.getString(1);
				ResultSet rs2 = db.get("SELECT group_id FROM invitationsAndGroupsMap WHERE invitation_id=" + id + ";");
				while (rs2.next()) {
					Group group = allGroups.get(rs.getString(1));
					if (group != null) {
						groups.add(group);
					} else {
						throw new GeneralException(ServletManager.Code.InternError, "This group dosen't exist.");
					}
				}
				int transaction = db.startTransaction();
				db.set("DELETE FROM invitationsAndGroupsMap WHERE invitation_id=" + id + ";");
				db.set("DELETE FROM invitations WHERE id=" + id + ";");
				db.commitTransaction(transaction);
			}
			return groups;
		} catch (SQLException e) {
			throw new GeneralException(ServletManager.Code.InternError, e);
		}
	}

	public static void sendInvitation(String email, String name, String group_id, ServletManager sm) throws GeneralException {
		DataBaseConnection db = sm.getDB();
		@SuppressWarnings("unchecked")
		Map<String, Group> groups = (Map<String, Group>) sm.getContextAttr("groups");
		String infraName = groups.get(group_id).getInfra().getName();
		String invitationCode = CodeGenerator.generateNewCode();
		db.set("UPDATE invitations SET linkCode='" + invitationCode + "' WHERE email='" + email + "'");
		Mail mailToSend;
		try {
			mailToSend = new Mail();
			mailToSend.sendInvitationEmail(email, infraName, name, invitationCode);
		} catch (MessagingException e) {
			throw new GeneralException(ServletManager.Code.InternError, e);
		}
	}
}