package com.Ease.servlet;


import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
import com.Ease.data.RSA;
import com.Ease.data.ServletItem;
import com.Ease.session.App;
import com.Ease.session.Profile;
import com.Ease.session.SessionException;
import com.Ease.session.User;

/**
 * Servlet implementation class AddUpdate
 */
@WebServlet("/addUpdate")
public class AddUpdate extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * @see HttpServlet#HttpServlet()
     */
    public AddUpdate() {
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
		String login = SI.getServletParam("login");
		String cryptedPassword = request.getParameter("cryptedPassword");
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
			
			Profile profile = null;
			if (user == null) {
				SI.setResponse(ServletItem.Code.NotConnected, "You are not connected.");
			} else if (login == null || login.equals("")) {
				SI.setResponse(ServletItem.Code.BadParameters, "Login can't be empty");
			} else if (name == null || name.length() > 14) {
				SI.setResponse(ServletItem.Code.BadParameters, "Incorrect app name");
			} else if (!user.getEmails().get(login)){
				SI.setResponse(ServletItem.Code.BadParameters, "Email not verified, check your mailbox before add this update.");
			} else if (cryptedPassword == null || cryptedPassword.equals("")) {
				SI.setResponse(ServletItem.Code.BadParameters, "Password can't be empty");
			} else if ((profile = user.getProfile(profileId)) == null){
				SI.setResponse(ServletItem.Code.BadParameters, "No profileId");
			} else {
				if ((site = ((SiteManager)session.getServletContext().getAttribute("siteManager")).get(siteId)) == null) {
					SI.setResponse(ServletItem.Code.BadParameters, "This site dosen't exist");
				} else {
					if (profile.havePerm(Profile.ProfilePerm.ADDAPP, session.getServletContext()) == true){
						String privateK = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIMD9dN2sYKTvReHaqpdUmlqE/iae8OX/jU/WgU22du4cDDFkKWc2t/D0NSDqXWwYoGbFtenwyoyu4Gu2WygwysryAz//prIEZBhOnIP9BBtTsjomL+3etoeIuf2d42r4WdCPBmyfI6ZAu2tSTPmGgA3+GDMhPjOW4Sd6rBte5M3AgMBAAECgYBukpYTPXZ9HNEXHYvRcueN3iAyzbaskgfSyr/f7LYbcWMGVodIrsQu3CXQZbpMgGvytICny4e0gpKr1XTc8CUmdi1DN249pt3iil0Q/PdpGpNnqjGlJlcMlp3KlrsfMttGIWCCHTz9d/j781/GD9S7WFMbi6aez8Xi3rzY0y6JQQJBAPHNlp3M7ZqhA09OhNZI9N64GBrFdMzgngsOgKwhSz57lCgiy0ZQKkcKlJcYMHCACNQBR6EVH1O1Sq7t+HJ0TFcCQQCKtS7gaY3wqqdeBewwOFQJf1SC7qUN1408D+wZYkxXZsrBBKlZH151+g01HDrnjY6P6zaX5u951zuxrlkzWqQhAkEA7Bd1KSwvDpyJs8SRlPx1AoUzG+iRq3zhMyB86BQ1+JMGzM10NnoNXYHqJUD8AswwUnfRbWlHRh8sBXLa8z20TwJAI0pJrOruJAcnIfLbzsDmEKyGsfFJqSXoVxmt9h9eUPZkK4umEni6rcV6ysJt8i+/z7oGX8tvrk4mb+Rt6XTsQQJBANTIeRpLnM9EEqrTXWY/o8nhksnuVzavY3D1DE8CCjmbkugc+SHs6NsqABRnzYz5+6AIKWG2zmZVJFEkC7xV5jY=";
						transaction = db.start();
						App app = new App(name, login, RSA.Decrypt(cryptedPassword, privateK), site, profile, user, session.getServletContext());
						profile.addApp(app);
						user.getApps().add(app);
						if (user.getTuto().equals("0")) {
							user.tutoComplete();
							user.updateInDB(session.getServletContext());
						}
						db.set("CALL increaseRatio(" + siteId + ");");
						SI.setResponse(200, Integer.toString(app.getAppId()));
						db.commit(transaction);
					} else {
						SI.setResponse(ServletItem.Code.NoPermission, "You have not the permission");
					}
				}
			}
		} catch (SessionException | IndexOutOfBoundsException | SQLException e) {
			try {
				db.cancel(transaction);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			SI.setResponse(ServletItem.Code.LogicError, ServletItem.getExceptionTrace(e));
		} catch (NumberFormatException e) {
			SI.setResponse(ServletItem.Code.BadParameters, "Numbers exception.");
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException e) {
			try {
				db.cancel(transaction);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			SI.setResponse(ServletItem.Code.LogicError, ServletItem.getExceptionTrace(e));
		}
		SI.sendResponse();
	}
}