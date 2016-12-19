package com.Ease.servlet;


import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.Ease.context.DataBase;
import com.Ease.context.Site;
import com.Ease.context.SiteManager;
import com.Ease.data.Regex;
import com.Ease.data.ServletItem;
import com.Ease.session.App;
import com.Ease.session.Profile;
import com.Ease.session.SessionException;
import com.Ease.session.User;

/**
 * Servlet implementation class AddApp
 */
@WebServlet("/addApp")
public class AddApp extends HttpServlet {

       
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * @see HttpServlet#HttpServlet()
     */
    public AddApp() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		User user = (User)(session.getAttribute("User"));
		ServletItem SI = new ServletItem(ServletItem.Type.AddApp, request, response, user);
		
		// Get Parameters
		String profileIdParam = SI.getServletParam("profileId");
		String siteId = SI.getServletParam("siteId");
		String name = SI.getServletParam("name");
		Map<String, String> inputs= new HashMap<String, String>();
		
		// --
		
		Site site = null;
		DataBase db = (DataBase)session.getServletContext().getAttribute("DataBase");
		boolean transaction = false;
		
		try {
			db.connect();
		} catch (SQLException e) {
			SI.setResponse(ServletItem.Code.DatabaseNotConnected, "There is a problem with our Database, please retry in few minutes.");
			SI.sendResponse();
			return ;
		}

		try {
			ResultSet inputsRs = db.get("SELECT information_name FROM websitesInformations WHERE website_id=" + siteId + ";");
			while(inputsRs.next())
				inputs.put(inputsRs.getString(1), request.getParameter(inputsRs.getString(1)));
			
			int profileId = Integer.parseInt(profileIdParam);
			
			Profile profile = null;
			if (user == null) {
				SI.setResponse(ServletItem.Code.NotConnected, "You are not connected.");
			} else if (inputs.get("login") == null || inputs.get("login").equals("")) {
				SI.setResponse(ServletItem.Code.BadParameters, "Login can't be empty.");
			} else if (name.length() > 14) {
				SI.setResponse(ServletItem.Code.BadParameters, "Incorrect app name.");
			} else if (inputs.get("password") == null || inputs.get("password").equals("")) {
				SI.setResponse(ServletItem.Code.BadParameters, "Password can't be empty.");
			} else if ((profile = user.getProfile(profileId)) == null){
				SI.setResponse(ServletItem.Code.BadParameters, "No profileId.");
			} else {
				if ((site = ((SiteManager)session.getServletContext().getAttribute("siteManager")).get(siteId)) == null) {
					SI.setResponse(ServletItem.Code.BadParameters, "This website doesn't exist in the database.");
				} else {
					if (profile.havePerm(Profile.ProfilePerm.ADDAPP, session.getServletContext()) == true){
						transaction = db.start();
						App app = new App(inputs, name, site, profile, user, session.getServletContext());
						profile.addApp(app);
						user.getApps().add(app);
						if (user.getTuto().equals("0")) {
							user.tutoComplete();
							user.updateInDB(session.getServletContext());
						}
						if (Regex.isEmail(inputs.get("login"))) {
							//db.set("CALL AddEmail(" + user.getId() + ", '" + inputs.get("login") + "');");
							user.addEmailIfNotPresent(inputs.get("login"));
						}
						//db.set("CALL increaseRatio(" + siteId + ");");
						SiteManager siteManager = (SiteManager)session.getAttribute("siteManager");
						siteManager.increaseSiteRatio(siteId);
						SI.setResponse(200, Integer.toString(app.getAppId()));
						db.commit(transaction);
					} else {
						SI.setResponse(ServletItem.Code.NoPermission, "You have not the permission.");
					}
				}
			}
		} catch (SessionException | SQLException e) {
			try {
				db.cancel(transaction);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			SI.setResponse(ServletItem.Code.LogicError, ServletItem.getExceptionTrace(e));
		} catch (IndexOutOfBoundsException e){
			try {
				db.cancel(transaction);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			SI.setResponse(ServletItem.Code.LogicError, ServletItem.getExceptionTrace(e));
		} catch (NumberFormatException e) {
			SI.setResponse(ServletItem.Code.BadParameters, "Numbers exception.");
		}
		SI.sendResponse();
	}
}