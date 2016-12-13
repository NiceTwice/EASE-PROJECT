package com.Ease.Servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.Ease.Context.Catalog.Catalog;
import com.Ease.Dashboard.App.Website;
import com.Ease.Dashboard.App.WebsiteApp.ClassicApp.ClassicApp;
import com.Ease.Dashboard.Profile.Profile;
import com.Ease.Dashboard.User.User;
import com.Ease.Utils.GeneralException;
import com.Ease.Utils.Regex;
import com.Ease.Utils.ServletManager;

/**
 * Servlet implementation class AddClassicApp
 */
@WebServlet("/AddClassicApp")
public class AddClassicApp extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddClassicApp() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) (session.getAttribute("user"));
		ServletManager sm = new ServletManager(this.getClass().getName(), request, response, true);
		
		try {
			sm.needToBeConnected();
			String name = sm.getServletParam("name", true);
			String websiteId = sm.getServletParam("websiteId", true);
			String profileId = sm.getServletParam("profileId", true);
			String password = sm.getServletParam("password", false);
			Website site = null;
			Map<String, String> infos = null;
			if (name == null || name.equals(""))
				throw new GeneralException(ServletManager.Code.ClientWarning, "Empty name.");
			else if (password == null || Regex.isPassword(password) == false)
				throw new GeneralException(ServletManager.Code.ClientWarning, "Wrong password.");
			try {
				Profile profile = user.getProfile(Integer.parseInt(profileId));
				site = ((Catalog)sm.getContextAttr("catalog")).getWebsiteWithSingleId(Integer.parseInt(websiteId));
				infos = site.getNeededInfos(sm);
				ClassicApp newApp = profile.addClassicApp(name, site, password, infos, sm);
				sm.setResponse(ServletManager.Code.Success, "ClassicApp added.");
			} catch (NumberFormatException e) {
				sm.setResponse(ServletManager.Code.ClientError, "Wrong numbers.");
			}
		} catch (GeneralException e) {
			sm.setResponse(e);
		}
		sm.sendResponse();
	}

}