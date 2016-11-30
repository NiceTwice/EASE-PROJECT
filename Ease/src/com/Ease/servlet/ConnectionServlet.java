package com.Ease.servlet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.annotation.WebServlet;

import com.Ease.context.DataBase;
import com.Ease.data.Hashing;
import com.Ease.data.Regex;
import com.Ease.data.ServletItem;
import com.Ease.session.SessionException;
import com.Ease.session.SessionSave;
import com.Ease.session.User;
import com.Ease.session.User.UserData;

/**
 * Servlet implementation class ConnectionServlet
 */
@WebServlet("/connection")
public class ConnectionServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int max_attempts = 10;
	private static final long expiration_time = 5; // 5 minutes
	private static final long ONE_MINUTE_IN_MILLIS = 60000;

	public ConnectionServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		User user = (User) (session.getAttribute("User"));
		ServletItem SI = new ServletItem(ServletItem.Type.ConnectionServlet, request, response, user);

		// Get Parameters
		String email = SI.getServletParam("email");
		String password = request.getParameter("password");
		// --

		DataBase db = (DataBase) session.getServletContext().getAttribute("DataBase");
		String client_ip = getIpAddr(request, email);

		if (user != null) {
			session.setAttribute("User", null);
		}

		try {
			db.connect();
		} catch (SQLException e) {
			SI.setResponse(ServletItem.Code.DatabaseNotConnected,
					"There is a problem with our database, please retry in few minutes.");
			SI.sendResponse();
			return;
		}

		try {
			// Put current ip in db
			addIpInDataBase(client_ip, db);
			int attempts = 0;
			if (canConnect(client_ip, db)) {
				if (email == null || Regex.isEmail(email) == false) {
					//attempts = incrementAttempts(client_ip, db);
					SI.setResponse(ServletItem.Code.BadParameters, "Incorrect email.");
				} else if (password == null || Regex.isPassword(password) == false) {
					attempts = incrementAttempts(client_ip, db);
					SI.setResponse(ServletItem.Code.BadParameters,"Incorrect password. " + (max_attempts - attempts) + " attempts left.");
				} else {

					ResultSet rs;

					if ((rs = db.get("select * from users where email = '" + email + "';")) == null) {
						SI.setResponse(ServletItem.Code.LogicError, "Impossible to access data base.");
					} else if (rs.next()) {

						String saltEase = rs.getString(UserData.SALTEASE.ordinal());
						String hashedPass = Hashing.SHA(password, saltEase);
						if (rs.getString(UserData.PASSWORD.ordinal()).equals(hashedPass)) {
							user = new User(rs, password, session.getServletContext());
							SessionSave sessionSave = new SessionSave(user, session.getServletContext());
							session.setAttribute("User", user);
							session.setAttribute("SessionSave", sessionSave);
							removeIpFromDataBase(client_ip, db);
							SI.setResponse(200, "Connected.");
						} else {
							attempts = incrementAttempts(client_ip, db);
							SI.setResponse(199, "Wrong password. " + (max_attempts - attempts) + " attempts left.");
						}
					} else {
						//attempts = incrementAttempts(client_ip, db);
						SI.setResponse(199, "Your email does not match any of our users.");
					}
				}
			} else {
				SI.setResponse(199, "Too much attempts to connect. Please retry in 5 minutes.");
			}
		} catch (SessionException e) {
			SI.setResponse(ServletItem.Code.LogicError, ServletItem.getExceptionTrace(e));
		} catch (SQLException e) {
			SI.setResponse(ServletItem.Code.LogicError, ServletItem.getExceptionTrace(e));
		}
		SI.sendResponse();
	}

	public String getIpAddr(HttpServletRequest request, String email) {

		/*String[] HEADERS_TO_TRY = { "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR",
				"HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR",
				"HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR" };

		for (String header : HEADERS_TO_TRY) {
			String ip = request.getHeader(header);
			if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}
		return request.getRemoteAddr();*/
		String ip = request.getHeader("X-Real-IP");
		if (null != ip && !"".equals(ip.trim()) && !"unknown".equalsIgnoreCase(ip)) {
			return ip+email;
		}
		ip = request.getHeader("X-Forwarded-For");
		if (null != ip && !"".equals(ip.trim()) && !"unknown".equalsIgnoreCase(ip)) {
			// get first ip from proxy ip
			int index = ip.indexOf(',');
			if (index != -1) {
				return ip.substring(0, index)+email;
			} else {
				return ip+email;
			}
		}
		return request.getRemoteAddr()+email;
		/*
		 * InetAddress ipAddr; try { ipAddr = InetAddress.getLocalHost(); }
		 * catch (UnknownHostException e) { // TODO Auto-generated catch block
		 * return "oups"; } return ipAddr.getHostAddress();
		 */
	}

	public void addIpInDataBase(String client_ip, DataBase db) throws SQLException {
		ResultSet rs = db.get("SELECT * FROM askingIps WHERE ip='" + client_ip + "';");
		if (rs.next())
			return;
		db.set("INSERT INTO askingIps values (NULL, '" + client_ip + "', 0, '" + getCurrentTime() + "', '"
				+ getExpirationTime() + "');");
	}

	public String getCurrentTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public String getExpirationTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(new Date(date.getTime() + (expiration_time * ONE_MINUTE_IN_MILLIS)));
	}

	public void removeIpFromDataBase(String client_ip, DataBase db) throws SQLException {
		db.set("DELETE FROM askingIps WHERE ip = '" + client_ip + "';");
	}

	public int incrementAttempts(String client_ip, DataBase db) throws SQLException {
		db.set("UPDATE askingIps SET attempts = attempts + 1, attemptDate = '" + getCurrentTime()
				+ "', expirationDate = '" + getExpirationTime() + "' WHERE ip = '" + client_ip + "';");
		ResultSet rs = db.get("select attempts from askingIps where ip='" + client_ip + "';");
		try {
			rs.next();
			return Integer.parseInt(rs.getString(1));
		} catch (NumberFormatException | SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public boolean canConnect(String client_ip, DataBase db) throws SQLException {
		ResultSet rs = db.get("SELECT attempts, expirationDate FROM askingIps WHERE ip='" + client_ip + "';");
		int attempts = 0;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date expirationDate = new Date();
		try {
			if (rs.next()) {
				attempts = Integer.parseInt(rs.getString(1));
				expirationDate = dateFormat.parse(rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return attempts < max_attempts || expirationDate.compareTo(new Date()) <= 0;
	}
}