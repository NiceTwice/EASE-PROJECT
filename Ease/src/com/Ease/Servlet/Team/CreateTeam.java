package com.Ease.Servlet.Team;

import com.Ease.Dashboard.User.User;
import com.Ease.Team.Team;
import com.Ease.Utils.GeneralException;
import com.Ease.Utils.ServletManager;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by thomas on 12/04/2017.
 */
@WebServlet("/CreateTeam")
public class CreateTeam extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletManager sm = new ServletManager(this.getClass().getName(), request, response, true);
        try {
            sm.needToBeConnected();
            User user = sm.getUser();
            if (!user.isAdmin())
                throw new GeneralException(ServletManager.Code.ClientError, "You ain't admin dude");
            String teamName = sm.getServletParam("teamName", true);
            String firstName = sm.getServletParam("firstName", true);
            String lastName = sm.getServletParam("lastName", true);
            String email = sm.getServletParam("email", true);
            Team team = Team.createTeam(teamName, email, firstName, lastName, sm);
            Map<String, Team> teamMap = (Map<String, Team>) sm.getContextAttr("teamMap");
            teamMap.put(team.getDb_id(), team);
            sm.setResponse(ServletManager.Code.Success, team.getJson().toString());
            sm.setLogResponse("Team created");
        } catch (GeneralException e) {
            sm.setResponse(e);
        } catch (Exception e) {
            sm.setResponse(e);
        }
        sm.sendResponse();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
        rd.forward(request, response);
    }
}