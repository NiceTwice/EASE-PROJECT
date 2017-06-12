package com.Ease.API.V1.Teams;

import com.Ease.Dashboard.App.App;
import com.Ease.Dashboard.App.ShareableApp;
import com.Ease.Dashboard.App.SharedApp;
import com.Ease.Team.Team;
import com.Ease.Team.TeamManager;
import com.Ease.Team.TeamUser;
import com.Ease.Utils.HttpServletException;
import com.Ease.Utils.HttpStatus;
import com.Ease.Utils.Servlets.PostServletManager;
import org.json.simple.JSONObject;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by thomas on 06/06/2017.
 */
@WebServlet("/api/v1/teams/EditSharedApp")
public class ServletEditSharedApp extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PostServletManager sm = new PostServletManager(this.getClass().getName(), request, response, true);
        try {
            Integer team_id = sm.getIntParam("team_id", true);
            sm.needToBeTeamUserOfTeam(team_id);
            TeamManager teamManager = (TeamManager) sm.getContextAttr("teamManager");
            Team team = teamManager.getTeamWithId(team_id);
            TeamUser teamUser_connected = sm.getTeamUserForTeam(team);
            Integer team_user_id = sm.getIntParam("team_user_id", true);
            TeamUser teamUser = team.getTeamUserWithId(team_user_id);
            if ((teamUser != teamUser_connected) && !teamUser_connected.isTeamAdmin())
                throw new HttpServletException(HttpStatus.Forbidden, "You are not allowed to do this.");
            Integer sharedApp_id = sm.getIntParam("app_id", true);
            SharedApp sharedApp = teamUser.getSharedAppWithId(sharedApp_id);
            JSONObject params = new JSONObject();
            params.put("account_information", sm.getParam("account_information", false));
            params.put("url", sm.getStringParam("url", true));
            if (teamUser_connected.isTeamAdmin() && ((App)sharedApp.getHolder()).isClassicApp())
                sharedApp.getHolder().modifyShareable(sm.getDB(), params, null);
            else
                sharedApp.modifyShared(sm.getDB(), params);
            sm.setSuccess(sharedApp.getHolder().getShareableJson());
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