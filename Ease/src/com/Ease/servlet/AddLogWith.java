package com.Ease.servlet;

import java.io.IOException;
import java.sql.SQLException;

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
import com.Ease.data.ServletItem;
import com.Ease.session.App;
import com.Ease.session.Profile;
import com.Ease.session.SessionException;
import com.Ease.session.User;

/**
 * Servlet implementation class AddLogWith
 */
@WebServlet("/addLogWith")
public class AddLogWith extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddLogWith() {
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
		ServletItem SI = new ServletItem(ServletItem.Type.AddLogWith, request, response, user);
		
		// Get Parameters
		String siteId = SI.getServletParam("siteId");
		String profileIdParam = SI.getServletParam("profileId");
		String appIdParam = SI.getServletParam("appId");
		String name = SI.getServletParam("name");
		// --
				
		Site site = null;
		boolean transaction = false;
		DataBase db = (DataBase)session.getServletContext().getAttribute("DataBase");
		
		try {
			db.connect();
		} catch (SQLException e) {
			SI.setResponse(ServletItem.Code.DatabaseNotConnected, "There is a problem with our Database, please retry in few minutes.");
			SI.sendResponse();
			return ;
		}
		
		try {
			int profileId = Integer.parseInt(profileIdParam);
			int appId = Integer.parseInt(appIdParam);
			
			Profile profile = null;
			
			if (user == null) {
				SI.setResponse(ServletItem.Code.NotConnected, "You are not connected.");
			} else if ((profile = user.getProfile(profileId)) == null){
				SI.setResponse(ServletItem.Code.BadParameters, "No profileId.");
			} else if (name == null || name.length() > 14) {
				SI.setResponse(ServletItem.Code.BadParameters, "Incorrect app name.");
			} else if (user.getApp(appId) == null) {
				SI.setResponse(ServletItem.Code.BadParameters, "No appId.");
			} else if (user.getApp(appId).getType().equals("ClassicAccount") == false){
				SI.setResponse(ServletItem.Code.LogicError, "This account is not a classicAccount.");
			} else {
				
				if ((site = ((SiteManager)session.getServletContext().getAttribute("siteManager")).get(siteId)) == null) {
					SI.setResponse(ServletItem.Code.BadParameters, "This website doesn't exist in the database.");
				} else {
					if (profile.havePerm(Profile.ProfilePerm.ADDAPP, session.getServletContext())){
						//transaction = db.start();
						App logWith = new App(name, user.getApp(appId).getId(), site, profile, user, session.getServletContext());
						profile.addApp(logWith);
						user.getApps().add(logWith);
						if (user.getTuto().equals("0")) {
							user.tutoComplete();
							user.updateInDB(session.getServletContext());
						}
						db.set("CALL increaseRatio(" + siteId + ");");
						SI.setResponse(200, Integer.toString(logWith.getAppId()));
						//db.commit(transaction);
					} else {
						SI.setResponse(ServletItem.Code.NoPermission, "You have not the permission.");
					}
				}
			}
		} catch (SessionException e) {
			try {
				db.cancel(transaction);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			SI.setResponse(ServletItem.Code.LogicError, ServletItem.getExceptionTrace(e));
		} catch (IndexOutOfBoundsException | SQLException e){
			try {
				db.cancel(transaction);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			SI.setResponse(ServletItem.Code.LogicError, ServletItem.getExceptionTrace(e));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			SI.setResponse(ServletItem.Code.BadParameters, "Bad numbers");
		}
		SI.sendResponse();
	}

}
