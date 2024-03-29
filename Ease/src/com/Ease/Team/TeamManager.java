package com.Ease.Team;

import com.Ease.Dashboard.App.App;
import com.Ease.Dashboard.App.SharedApp;
import com.Ease.Dashboard.App.WebsiteApp.ClassicApp.Account;
import com.Ease.Dashboard.App.WebsiteApp.ClassicApp.ClassicApp;
import com.Ease.Hibernate.HibernateQuery;
import com.Ease.Mail.MailJetBuilder;
import com.Ease.Utils.*;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by thomas on 28/04/2017.
 */
public class TeamManager {

    protected List<Team> teams;
    protected HashMap<Integer, Team> teamIdMap;

    public TeamManager(ServletContext context, DataBaseConnection db) throws HttpServletException {
        this.teams = Team.loadTeams(context, db);
        this.teamIdMap = new HashMap<>();
        for (Team team : this.teams)
            this.teamIdMap.put(team.getDb_id(), team);

    }

    public List<Team> getTeams() {
        return teams;
    }

    public Team getTeamWithId(Integer team_id) throws HttpServletException {
        Team team = this.teamIdMap.get(team_id);
        if (team == null)
            throw new HttpServletException(HttpStatus.BadRequest, "No such team");
        for (Channel channel : team.getChannels()) {
            if (!channel.getTeamUsers().isEmpty()) {

            }
        }
        return team;
    }

    public void addTeam(Team team) {
        this.teams.add(team);
        this.teamIdMap.put(team.getDb_id(), team);
    }

    public void removeTeam(Team team) {
        this.teams.remove(team);
        this.teamIdMap.remove(team.getDb_id());
    }

    public void removeTeamWithId(Integer team_id) throws HttpServletException {
        Team team = this.getTeamWithId(team_id);
        this.removeTeam(team);
    }

    public Team getTeamWithName(String team_name) {
        for (Team team : this.getTeams()) {
            if (team.getName().equals(team_name))
                return team;
        }
        return null;
    }

    public void updateTeamsSubscriptions() {
        Date now = new Date();
        HibernateQuery hibernateQuery = new HibernateQuery();
        for (Team team : this.getTeams()) {
            team.updateSubscription(now);
            hibernateQuery.saveOrUpdateObject(team);
        }
        try {
            hibernateQuery.commit();
        } catch (HttpServletException e) {
            e.printStackTrace();
        }
    }

    public void reminderThreeDays() throws HttpServletException {
        System.out.println("Three days reminder start...");
        List<TeamUser> unregistered_teamUsers = new LinkedList<>();
        for (Team team : this.getTeams()) {
            for (TeamUser teamUser : team.getTeamUsers()) {
                if (teamUser.isRegistered() || teamUser.getTeamUserStatus().reminder_three_days_sended())
                    continue;
                if (DateComparator.isOutdated(teamUser.getArrivalDate(), 3))
                    unregistered_teamUsers.add(teamUser);
            }
        }
        if (unregistered_teamUsers.isEmpty()) {
            System.out.println("Three days reminder end...");
            return;
        }

        System.out.println("Three days reminder emails: " + unregistered_teamUsers.size());
        MailJetBuilder mailJetBuilder = new MailJetBuilder();
        mailJetBuilder.setFrom("contact@ease.space", "Ease.space");
        mailJetBuilder.setTemplateId(180262);
        HibernateQuery hibernateQuery = new HibernateQuery();
        for (TeamUser teamUser : unregistered_teamUsers) {
            JSONObject vars = new JSONObject();
            vars.put("first_name", teamUser.getFirstName());
            vars.put("last_name", teamUser.getLastName());
            vars.put("team_name", teamUser.getTeam().getName());
            mailJetBuilder.addRecipient(teamUser.getEmail(), vars);
            teamUser.getTeamUserStatus().setReminder_three_days_sended(true);
            hibernateQuery.saveOrUpdateObject(teamUser.getTeamUserStatus());
        }
        mailJetBuilder.sendEmail();
        hibernateQuery.commit();
        System.out.println("Three days reminder end...");
    }

    public void passwordReminder() throws HttpServletException {
        System.out.println("Password reminder start...");
        Date timestamp = new Date();
        HibernateQuery hibernateQuery = new HibernateQuery();
        for (Team team : this.getTeams()) {
            for (SharedApp sharedApp : team.getAppManager().getSharedApps()) {
                App holder = (App) sharedApp.getHolder();
                if (!holder.isEmpty())
                    continue;
                ClassicApp app = (ClassicApp) sharedApp;
                Account account = app.getAccount();
                if (account.mustUpdatePassword()) {
                    System.out.println("Account: " + account.getDBid() + " must update password.");
                    if (!account.passwordMustBeUpdated()) {
                        hibernateQuery.querySQLString("UPDATE accounts SET passwordMustBeUpdated = 1 WHERE id = ?;");
                        hibernateQuery.setParameter(1, account.getDBid());
                        hibernateQuery.executeUpdate();
                        account.setPasswordMustBeUpdated(true);
                        hibernateQuery.saveOrUpdateObject(sharedApp.getTeamUser_tenant().addNotification("Your password for " + app.getName() + " needs to be updated as soon as possible", timestamp));
                    } else {
                        if (!account.adminNotified() && DateComparator.isOutdated(account.getLastUpdatedDate(), account.getPasswordChangeInterval(), 7)) {
                            System.out.println("Admin must be notified");
                            hibernateQuery.querySQLString("UPDATE accounts SET adminNotified = 1 WHERE id = ?;");
                            hibernateQuery.setParameter(1, account.getDBid());
                            hibernateQuery.executeUpdate();
                            account.setAdminNotified(true);
                            hibernateQuery.saveOrUpdateObject(sharedApp.getHolder().getTeamUser_owner().addNotification("The password of " + sharedApp.getTeamUser_tenant().getUsername() + " for " + app.getName() + " is not up to date for the last 7 days.", timestamp));
                        }
                    }
                }
            }
        }
        hibernateQuery.commit();
        System.out.println("Password reminder end...");
    }

    public void passwordLostReminder() throws HttpServletException {
        System.out.println("Password lost reminder start...");
        HibernateQuery hibernateQuery = new HibernateQuery();
        for (Team team : this.getTeams()) {
            for (TeamUser teamUser : team.getTeamUsers()) {
                if (teamUser.isDisabled()) {
                    hibernateQuery.querySQLString("SELECT DATE_ADD(DATE(?), INTERVAL 7 DAY) = CURDATE();");
                    hibernateQuery.setParameter(1, teamUser.getDisabledDate());
                    if ((Boolean) hibernateQuery.getSingleResult()) {
                        if (teamUser.getAdmin_id() == null)
                            continue;
                        hibernateQuery.saveOrUpdateObject(team.getTeamUserWithId(teamUser.getAdmin_id()).addNotification("Since last week " + teamUser.getUsername() + " lost the password to access your team " + team.getName() + " on Ease.space. Please give again the access to this person.", new Date()));
                    }
                }

            }
        }
        hibernateQuery.commit();
        System.out.println("Password lost reminder end...");
    }
}
