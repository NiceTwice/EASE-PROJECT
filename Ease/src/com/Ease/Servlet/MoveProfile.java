package com.Ease.Servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.Ease.Dashboard.User.User;
import com.Ease.Utils.GeneralException;
import com.Ease.Utils.ServletManager;

/**
 * Servlet implementation class MoveProfile
 */
@WebServlet("/MoveProfile")
public class MoveProfile extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MoveProfile() {
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
			String profileId = sm.getServletParam("profileId", true);
			String columnIdx = sm.getServletParam("columnIdx", true);
			String position = sm.getServletParam("position", true);
			if (profileId == null || profileId.isEmpty())
				throw new GeneralException(ServletManager.Code.ClientError, "Wrong profileId.");
			if (columnIdx == null || columnIdx.isEmpty())
				throw new GeneralException(ServletManager.Code.ClientError, "Wrong columnIdx.");
			if (position == null || position.isEmpty())
				throw new GeneralException(ServletManager.Code.ClientError, "Wrong position.");
			user.moveProfile(Integer.parseInt(profileId), Integer.parseInt(columnIdx), Integer.parseInt(position), sm);
			sm.setResponse(ServletManager.Code.Success, "Profile moved.");
		} catch (GeneralException e) {
			sm.setResponse(e);
		} catch (NumberFormatException e) {
			sm.setResponse(ServletManager.Code.ClientError, "Wrong numbers.");
		}
		sm.sendResponse();
	}

}
