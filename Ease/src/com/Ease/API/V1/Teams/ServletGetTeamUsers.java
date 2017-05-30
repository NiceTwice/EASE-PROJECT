package com.Ease.API.V1.Teams;

import com.Ease.Team.TeamManager;
import com.Ease.Team.TeamUser;
import com.Ease.Utils.ServletManager;
import org.json.simple.JSONArray;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by thomas on 30/05/2017.
 */
@WebServlet("/api/v1/teams/GetTeamUsers")
public class ServletGetTeamUsers extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletManager sm = new ServletManager(this.getClass().getName(), request, response, true);
        try {
            String team_id = sm.getServletParam("team_id", true);
            sm.needToBeTeamUserOfTeam(team_id);
            TeamManager teamManager = (TeamManager) sm.getContextAttr("teamManager");
            JSONArray jsonArray = new JSONArray();
            for (TeamUser teamUser : teamManager.getTeamWithId(Integer.parseInt(team_id)).getTeamUsers())
                jsonArray.add(teamUser.getSimpleJson());
            sm.setResponse(ServletManager.Code.Success, jsonArray.toString());
            sm.setLogResponse("GetTeamUsersDone");
        } catch (Exception e) {
            sm.setResponse(e);
        }
        sm.sendResponse();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
        rd.forward(request, response);
    }
}
