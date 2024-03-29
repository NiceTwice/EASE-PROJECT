package com.Ease.API.V1.Teams;

import com.Ease.Dashboard.App.ShareableApp;
import com.Ease.Dashboard.App.SharedApp;
import com.Ease.Team.Channel;
import com.Ease.Team.Team;
import com.Ease.Team.TeamManager;
import com.Ease.Team.TeamUser;
import com.Ease.Utils.DataBaseConnection;
import com.Ease.Utils.HttpServletException;
import com.Ease.Utils.HttpStatus;
import com.Ease.Utils.Servlets.PostServletManager;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/v1/teams/RemoveUserFromChannel")
public class ServletRemoveTeamUserFromChannel extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PostServletManager sm = new PostServletManager(this.getClass().getName(), request, response, true);
        try {
            Integer team_id = sm.getIntParam("team_id", true);
            sm.needToBeAdminOfTeam(team_id);
            TeamManager teamManager = (TeamManager) sm.getContextAttr("teamManager");
            Team team = teamManager.getTeamWithId(team_id);
            Integer teamUser_id = sm.getIntParam("team_user_id", true);
            TeamUser teamUser_to_remove = team.getTeamUserWithId(teamUser_id);
            TeamUser teamUser_connected = sm.getTeamUserForTeam(team);
            if (!teamUser_connected.isSuperior(teamUser_to_remove) && teamUser_to_remove != teamUser_connected)
                throw new HttpServletException(HttpStatus.Forbidden, "Dudeeee :/");
            Integer channel_id = sm.getIntParam("channel_id", true);
            Channel channel = team.getChannelWithId(channel_id);
            List<ShareableApp> shareableApps = team.getAppManager().getShareableAppsForTeamUser(teamUser_to_remove);
            DataBaseConnection db = sm.getDB();
            int transaction = db.startTransaction();
            for (ShareableApp shareableApp : shareableApps) {
                if (shareableApp.getChannel() == channel)
                    team.getAppManager().removeShareableApp(shareableApp, db);
            }
            List<SharedApp> sharedApps = team.getAppManager().getSharedAppsForTeamUser(teamUser_to_remove);
            for (SharedApp sharedApp : sharedApps) {
                if (sharedApp.getHolder().getChannel() == channel)
                    team.getAppManager().removeSharedApp(sharedApp);
            }
            channel.removeTeamUser(teamUser_to_remove, sm.getDB());
            db.commitTransaction(transaction);
            sm.setSuccess(channel.getJson());
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
