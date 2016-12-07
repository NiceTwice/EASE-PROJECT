package com.Ease.servlet.backOffice;

import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.Ease.context.DataBase;
import com.Ease.data.ServletItem;
import com.Ease.session.App;
import com.Ease.session.ClassicAccount;
import com.Ease.session.LogWithAccount;
import com.Ease.session.User;


/**
 * Servlet implementation class AskInfo
 */
@WebServlet("/testWebsites")
public class TestWebsites extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestWebsites() {
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
		ServletItem SI = new ServletItem(ServletItem.Type.AskInfo, request, response, user);
		DataBase db = (DataBase)session.getServletContext().getAttribute("DataBase");
				
		try {
			if (user == null) {
				SI.setResponse(ServletItem.Code.NotConnected, "You are not connected.");
			} else {
			List<App> apps = user.getApps();
			JSONArray responseJson = new JSONArray();
			for(App app : apps){
				if (app.isEmpty() != true){
					JSONObject classicAccountObject = new JSONObject();					
					boolean again = true;
					JSONArray ja = new JSONArray();
					while (again){
						JSONObject obj = new JSONObject();
						if (app.getType().equals("ClassicAccount")) {
							ResultSet accountInformationsRs = db.get("SELECT information_name, information_value FROM ClassicAccountsInformations WHERE account_id=" + app.getAccount().getId() + ";");
							while (accountInformationsRs.next()) {
								String information_name = accountInformationsRs.getString(1);
								String information_value = accountInformationsRs.getString(2);
								/*if (information_name.equals("password")){
									classicAccountObject.put(information_name, app.getAccount().getPassword());
								} else*/
								classicAccountObject.put(information_name, ((ClassicAccount)app.getAccount()).getInfo(information_name));
							}
							obj.put("user", classicAccountObject);
						} else if (app.getType().equals("LogWithAccount")) {
							obj.put("logWith", ((LogWithAccount)app.getAccount()).getLogWithApp(user).getSite().getName());
						}
						JSONParser parser = new JSONParser();
						JSONObject a = (JSONObject) parser.parse(new FileReader(session.getServletContext().getRealPath(app.getSite().getFolder() + "connect.json")));
						a.put("loginUrl", app.getSite().getUrl());
						a.put("siteId", app.getSite().getId());
						a.put("siteSrc", app.getSite().getFolder());
						obj.put("website", a);
						ja.add(0, obj);
						if (app.getType().equals("ClassicAccount")) {
							again = false;
						} else {
							app = ((LogWithAccount)app.getAccount()).getLogWithApp(user);
						}
					}
					responseJson.add(ja);
				}
			}
			SI.setResponse(200, responseJson.toString());
		}
		} catch (NumberFormatException e) {
			SI.setResponse(ServletItem.Code.BadParameters, "Numbers exception.");
		} catch (ParseException e) {
			SI.setResponse(ServletItem.Code.LogicError, ServletItem.getExceptionTrace(e));
		} catch (SQLException e) {
			SI.setResponse(ServletItem.Code.LogicError, "SQL error");
			e.printStackTrace();
		}
		SI.sendResponse();
	}
}
