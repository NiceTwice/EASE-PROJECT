package com.Ease.Servlet.Update;

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.Ease.Dashboard.User.User;
import com.Ease.Dashboard.User.UserEmail;
import com.Ease.Utils.GeneralException;
import com.Ease.Utils.Regex;
import com.Ease.Utils.ServletManager;

/**
 * Servlet implementation class CreateUpdate
 */
@WebServlet("/CreateUpdate")
public class CreateUpdate extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateUpdate() {
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

		String sessionId = sm.getServletParam("sessionId", true);
		String extensionId = sm.getServletParam("extensionId", true);
		String jsonUpdate = sm.getServletParam("updates", true);
		
		try {
			Map<String, User> sessionIdUserMap = (Map<String, User>) sm.getContextAttr("sessionIdUserMap");
			if ((user = sessionIdUserMap.get(sessionId)) == null) {
				sm.setResponse(ServletManager.Code.Success, "1 Please stock update.");
			} else {
				if (user.getExtensionKeys().haveThisKey(extensionId)) {
					user.createUpdate(jsonUpdate, sm);
					sm.setResponse(ServletManager.Code.Success, "2 Update sended.");
				} else {
					sm.setResponse(ServletManager.Code.Success, "3 Don't stock update.");
				}
			}
		} catch (GeneralException e) {
			sm.setResponse(e);
		} catch (Exception e) {
			sm.setResponse(e);
		}
		sm.sendResponse();
	}

}