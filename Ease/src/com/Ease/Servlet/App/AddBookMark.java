package com.Ease.Servlet.App;

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
import com.Ease.Context.Catalog.Website;
import com.Ease.Dashboard.App.App;
import com.Ease.Dashboard.App.LinkApp.LinkApp;
import com.Ease.Dashboard.App.WebsiteApp.ClassicApp.ClassicApp;
import com.Ease.Dashboard.Profile.Profile;
import com.Ease.Dashboard.User.User;
import com.Ease.Utils.GeneralException;
import com.Ease.Utils.ServletManager;
import com.Ease.Utils.Crypto.RSA;

/**
 * Servlet implementation class AddBookMark
 */
@WebServlet("/AddBookMark")
public class AddBookMark extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddBookMark() {
        super();
        // TODO Auto-generated constructor stub
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
		User user = (User) (session.getAttribute("user"));
		ServletManager sm = new ServletManager(this.getClass().getName(), request, response, true);
		try {
			sm.needToBeConnected();
			String name = sm.getServletParam("name", true);
			String websiteId = sm.getServletParam("websiteId", true);
			String profileId = sm.getServletParam("profileId", true);
			String link = sm.getServletParam("link", true);
			
			Website site = null;
			if (name == null || name.equals(""))
				throw new GeneralException(ServletManager.Code.ClientWarning, "Empty name.");
			try {
				Profile profile = user.getDashboardManager().getProfile(Integer.parseInt(profileId));
				site = ((Catalog)sm.getContextAttr("catalog")).getWebsiteWithSingleId(Integer.parseInt(websiteId));
				LinkApp linkApp = LinkApp.createLinkApp(profile, profile.getApps().size(), name, link, site.getFolder() + "logo.png", sm);
				profile.addApp(linkApp);
				sm.setResponse(ServletManager.Code.Success, String.valueOf(linkApp.getSingleId()));
			} catch (NumberFormatException e) {
				sm.setResponse(ServletManager.Code.ClientError, "Wrong numbers.");
			}
		} catch (GeneralException e) {
			sm.setResponse(e);
		} catch (Exception e) {
			sm.setResponse(e);
		}
		sm.sendResponse();
	}

}