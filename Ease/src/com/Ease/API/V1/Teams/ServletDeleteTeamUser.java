package com.Ease.API.V1.Teams;

import com.Ease.Dashboard.App.App;
import com.Ease.Dashboard.App.SharedApp;
import com.Ease.Hibernate.HibernateQuery;
import com.Ease.Mail.MailJetBuilder;
import com.Ease.Team.Team;
import com.Ease.Team.TeamManager;
import com.Ease.Team.TeamUser;
import com.Ease.Utils.HttpServletException;
import com.Ease.Utils.HttpStatus;
import com.Ease.Utils.Servlets.PostServletManager;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by thomas on 09/06/2017.
 */
@WebServlet("/api/v1/teams/DeleteTeamUser")
public class ServletDeleteTeamUser extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PostServletManager sm = new PostServletManager(this.getClass().getName(), request, response, true);
        try {
            Integer team_id = sm.getIntParam("team_id", true);
            sm.needToBeAdminOfTeam(team_id);
            TeamManager teamManager = (TeamManager) sm.getContextAttr("teamManager");
            Team team = teamManager.getTeamWithId(team_id);
            Integer team_user_id = sm.getIntParam("team_user_id", true);
            TeamUser teamUser_to_delete = team.getTeamUserWithId(team_user_id);
            TeamUser teamUser_connected = sm.getTeamUserForTeam(team);
            if (!teamUser_connected.isSuperior(teamUser_to_delete))
                throw new HttpServletException(HttpStatus.Forbidden, "You cannot do this");
            JSONArray forEmail = new JSONArray();
            for (SharedApp sharedApp : team.getAppManager().getSharedAppsForTeamUser(teamUser_to_delete)) {
                App holder = (App) sharedApp.getHolder();
                if (holder.isEmpty() || (holder.isClassicApp() && sharedApp.getHolder().getTeamUser_tenants().size() == 1)) {
                    JSONObject app = new JSONObject();
                    app.put("name", holder.getName());
                    forEmail.put(app);
                }
            }
            if (forEmail.length() != 0 && teamUser_to_delete.getAdmin_id() != null && teamUser_to_delete.getAdmin_id() > 0) {
                MailJetBuilder mailJetBuilder = new MailJetBuilder();
                mailJetBuilder.setFrom("contact@ease.space", "Ease.space");
                mailJetBuilder.setTemplateId(180165);
                mailJetBuilder.addTo(teamUser_connected.getEmail());
                mailJetBuilder.addVariable("first_name", teamUser_to_delete.getFirstName());
                mailJetBuilder.addVariable("last_name", teamUser_to_delete.getLastName());
                mailJetBuilder.addVariable("team_name", team.getName());
                mailJetBuilder.addVariable("apps", forEmail);
                mailJetBuilder.sendEmail();
            }
            for (TeamUser teamUser : team.getTeamUsers()) {
                if (teamUser.getAdmin_id().equals(teamUser_to_delete.getAdmin_id())) {
                    teamUser.setAdmin_id(teamUser_connected.getDb_id());
                    sm.saveOrUpdate(teamUser);
                }
            }
            teamUser_to_delete.delete(sm.getDB());
            team.removeTeamUser(teamUser_to_delete);
            sm.deleteObject(teamUser_to_delete);

            sm.setSuccess("TeamUser deleted");
        } catch (Exception e) {
            sm.setError(e);
        }
        sm.sendResponse();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
        rd.forward(request, response);
    }
}
