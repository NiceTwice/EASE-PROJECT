package com.Ease.Servlet.BackOffice;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.Ease.Context.Catalog.WebsitesVisitedManager;
import com.Ease.Dashboard.User.User;
import com.Ease.Utils.GeneralException;
import com.Ease.Utils.ServletManager;

/**
 * Servlet implementation class RemoveFromBlackList
 */
@WebServlet("/RemoveFromBlackList")
public class RemoveFromBlackList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RemoveFromBlackList() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher rd = request.getRequestDispatcher("admin.jsp");
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletManager sm = new ServletManager(this.getClass().getName(), request, response, true);
		User user = sm.getUser();
		try {
			sm.needToBeConnected();
			if (!user.isAdmin())
				throw new GeneralException(ServletManager.Code.ClientError, "You ain't admin");
			String single_id = sm.getServletParam("single_id", true);
			if (single_id == null || single_id.equals(""))
				throw new GeneralException(ServletManager.Code.ClientWarning, "Empty single_id");
			WebsitesVisitedManager websitesVisitedManager = (WebsitesVisitedManager) sm.getContextAttr("websitesVisitedManager");
			websitesVisitedManager.sendBlacklistedWebsiteToWebsitesVisitedWithSingleId(Integer.parseInt(single_id), sm);
			sm.setResponse(ServletManager.Code.Success, "Website added to websites visited");
		} catch(GeneralException e) {
			sm.setResponse(e);
		}
		sm.sendResponse();
	}

}
