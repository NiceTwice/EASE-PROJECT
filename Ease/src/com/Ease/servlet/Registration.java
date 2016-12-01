package com.Ease.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.Ease.dashboard.User;
import com.Ease.data.Regex;
import com.Ease.utils.GeneralException;
import com.Ease.utils.ServletManager;

/**
 * Servlet implementation class NewUser
 */
@WebServlet("/register")
public class Registration extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Registration() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("poeutte");
		HttpSession session = request.getSession();
		String invitationCode = request.getParameter("invitationCode");
		String email = request.getParameter("email");
		User user = (User) session.getAttribute("User");
		RequestDispatcher rd = null;
		String dispatch;
		if (invitationCode == null || email == null)
			dispatch = "TheFamilyInvitation.jsp";
		else if (user != null)
			dispatch = "index.jsp";
		else
			dispatch = "TheFamilyRegistration.jsp";
		rd = request.getRequestDispatcher(dispatch);
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		User user = (User) (session.getAttribute("User"));
		ServletManager sm = new ServletManager(this.getClass().getName(), request, response, true);
		try {
			String invitationCode = sm.getServletParam("invitationCode", false);
			String fname = sm.getServletParam("fname", true);
			String email = sm.getServletParam("email", true);
			String password = sm.getServletParam("password", false);
			String confirmPassword = sm.getServletParam("confirmPassword", false);

			if (user != null)
				throw new GeneralException(ServletManager.Code.ClientWarning, "You are logged on Ease.");
			else if (fname == null || fname.length() < 2)
				throw new GeneralException(ServletManager.Code.ClientWarning, "Your name is too short.");
			else if (email == null || Regex.isEmail(email) == false)
				throw new GeneralException(ServletManager.Code.ClientWarning, "Incorrect email.");
			else if (password == null || Regex.isPassword(password) == false)
				throw new GeneralException(ServletManager.Code.ClientWarning, "Password is too short (at least 8 characters).");
			else if (confirmPassword == null || password.equals(confirmPassword) == false)
				throw new GeneralException(ServletManager.Code.ClientWarning, "Passwords are not the same.");
			else {
				User newUser = User.createUser(email, fname, "", confirmPassword, invitationCode, sm);
				session.setAttribute("user", newUser);
			}
		} catch (GeneralException e) {
			sm.setResponse(e);
		}
		sm.sendResponse();
	}
}