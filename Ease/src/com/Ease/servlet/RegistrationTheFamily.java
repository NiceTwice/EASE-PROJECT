package com.Ease.servlet;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.Ease.context.DataBase;
import com.Ease.data.Regex;
import com.Ease.data.ServletItem;
import com.Ease.session.SessionException;
import com.Ease.session.SessionSave;
import com.Ease.session.User;


/**
 * Servlet implementation class NewUser
 */
@WebServlet("/thefamily")
public class RegistrationTheFamily extends HttpServlet {
       
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * @see HttpServlet#HttpServlet()
     */
    public RegistrationTheFamily() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		User user = (User)(session.getAttribute("User"));
		ServletItem SI = new ServletItem(ServletItem.Type.TheFamilyRegistration, request, response, user);
		
		// Get Parameters
		String invitationCode = SI.getServletParam("invitationCode");
		String fname = SI.getServletParam("fname");
		String lname = SI.getServletParam("lname");
		String email = SI.getServletParam("email");
		// --
		
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirmPassword");
		
		ResultSet rs = null;
		DataBase db = (DataBase)session.getServletContext().getAttribute("DataBase");
		
		try {
			db.connect();
		} catch (SQLException e) {
			SI.setResponse(ServletItem.Code.DatabaseNotConnected, "There is a problem with our Database, please retry in few minutes.");
			SI.sendResponse();
			return ;
		}
		
		if (user != null) {
			SI.setResponse(ServletItem.Code.AlreadyConnected, "You are logged on Ease.");
		} else if (fname == null || fname.length() < 2){
			SI.setResponse(ServletItem.Code.BadParameters, "Your name is too short.");	
		} else if (email == null || Regex.isEmail(email) == false){
			SI.setResponse(ServletItem.Code.BadParameters, "Incorrect email.");
		} else if (password == null || Regex.isPassword(password) == false) {
			SI.setResponse(ServletItem.Code.BadParameters, "Password is too short (at least 8 characters).");
		} else if (confirmPassword == null || password.equals(confirmPassword) == false) {
			SI.setResponse(ServletItem.Code.BadParameters, "Passwords are not the same.");
		} else {
			try {
				String group;
				rs = db.get("select * from invitations where email ='" + email + "' and linkCode = '" + invitationCode + "';");
				if (rs.next()) {
					group = rs.getString(3);
					rs = db.get("select * from users where email = '" + email + "' limit 0, 1;");
					if (rs.next()) {
						SI.setResponse(ServletItem.Code.BadParameters, "You already have an account.");
					} else {
						lname = "";
						user = new User(fname, lname, email, "0606060606", password, session.getServletContext());
						if (group != null && group.equals("null") == false)
							db.set("insert into GroupAndUserMap values (NULL, " + group + ", " + user.getId() + ");");
						db.set("delete from invitations where email = '" + email + "' and linkCode = '" + invitationCode + "';");
						session.setAttribute("User", user);
						db.set("CALL addEmail(" + user.getId() + ", '" + user.getEmail() + "');");
						db.set("UPDATE usersEmails SET verified = 1 WHERE user_id = " + user.getId() + " AND email = '" + user.getEmail() + "';");
						SessionSave sessionSave = new SessionSave(user, session.getServletContext());
						session.setAttribute("User", user);
						session.setAttribute("SessionSave", sessionSave);
						SI.setResponse(200, "Successfull registration! You will be redirect in few seconds :)");
					}
				} else {
					SI.setResponse(ServletItem.Code.BadParameters, "You have no invitation or you already have an account.");
				}
			} catch (SessionException e) {
				e.printStackTrace();
				SI.setResponse(ServletItem.Code.LogicError, ServletItem.getExceptionTrace(e));
			} catch (SQLException e) {
				e.printStackTrace();
				SI.setResponse(ServletItem.Code.LogicError, ServletItem.getExceptionTrace(e));
			}
		}
		SI.sendResponse();
	}
}