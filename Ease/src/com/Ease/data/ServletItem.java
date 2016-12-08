package com.Ease.data;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.Ease.context.DataBase;
import com.Ease.session.User;

public class ServletItem {
	public enum Type
	{
		AddApp,
		AddAppSso,
		AddLogWith,
		AddProfile,
		AddTag,
		AddWebsite,
		AskForNewApp,
		AskInfo,
		CatalogSearchServlet,
		ConnectionServlet,
		CreateInvitation,
		DeleteApp,
		DeleteProfile,
		EditApp,
		EditProfileColor,
		EditProfileName,
		EditUserEmail,
		EditUserName,
		EditUserPassword,
		EraseRequestedWebsiteServlet,
		GetMailLink,
		GetTags,
		Logout,
		MoveApp,
		MoveProfile,
		NewUser,
		PasswordLost,
		RegistrationByInvitation,
		RequestedWebsitesServlet,
		ResetUser,
		SetTags,
		UploadWebsiteServlet,
		AdminStats,
		ChangeBackground,
		SaveSessionServlet,
		AddEmail,
		DeleteEmail,
		CleanSavedSessions,
		SendVerificationEmail,
		checkVerifiedEmail,
		CheckConnection,
		DeletedTypeKeptForLogsCoherence,
		ChangeSitePosition,
		ReminderEmail,
		TheFamilyInvitation,
		TheFamilyRegistration,
		StockUpdate,
		SendRequestedWebsiteValidation,
		TestWebsites
	}
	public enum Code
	{
		NotConnected,
		DatabaseNotConnected,
		BadParameters,
		LogicError,
		NoPermission,
		AlreadyConnected,
		EMailNotSended,
		Constraints,
		ConnectionLost
	}
	
	protected Map<String, String> 	args;
	protected User					user;
	protected Type 					type;
	protected String 				retMsg;
	protected Integer				retCode;
	protected HttpServletRequest 	request;
	protected HttpServletResponse	response;
	
	
	public ServletItem(Type type, HttpServletRequest request, HttpServletResponse response, User user) {
		args = new HashMap<>();
		this.type = type;
		retMsg = "";
		retCode = 0;
		this.request = request;
		this.response = response;
		this.user = user;
		DataBase db = (DataBase)request.getSession().getServletContext().getAttribute("DataBase");
		try {
			db.connect();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				response.getWriter().print("Sorry an internal problem occurred. We are solving it asap.");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	//Getter
	
	public Map<String, String> getArgs() {
		return args;
	}
	
	public Type getType() {
		return type;
	}
	
	public String getMsg() {
		return retMsg;
	}
	
	public Integer getCode() {
		return retCode;
	}
	
	//Setter
	
	public String getServletParam(String paramName) {
		String param = request.getParameter(paramName);
		args.put(paramName, param);
		return param;
	}
	
	public String[] getServletParamValues(String paramName) {
		String param[] = request.getParameterValues(paramName);
		args.put(paramName, (param != null) ? param.toString() : null);
		return param;
	}
	
	public void setResponse(Code code, String msg){
		this.retCode = code.ordinal();
		this.retMsg = msg;
	}
	public void setResponse(Integer code, String msg){
		this.retCode = code;
		this.retMsg = msg;
	}
	
	protected void saveInDB(DataBase db) throws SQLException {
		String argsString = "";
		Set<Entry<String, String>> setHm = args.entrySet();
	    Iterator<Entry<String, String>> it = setHm.iterator();
	    while (it.hasNext()){
	    	Entry<String, String> e = it.next();
	        argsString += "<" + e.getKey() + ":" + e.getValue() + ">";
	    }
	    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		retMsg = retMsg.replace("'","-");
		argsString = argsString.replaceAll("'", "-");
		System.out.println("insert into logs values(" + type.ordinal() + ", " + retCode + ", " + ((user != null) ? user.getId() : "NULL") + ", '" + argsString + "', '" + retMsg + "', '" + dateFormat.format(date) + "');");
		db.set("insert into logs values(" + type.ordinal() + ", " + retCode + ", " + ((user != null) ? user.getId() : "NULL") + ", '" + argsString + "', '" + retMsg + "', '" + dateFormat.format(date) + "');");
	}
	
	public void sendResponse() throws IOException {
		DataBase db = (DataBase)request.getSession().getServletContext().getAttribute("DataBase");
		user = ((User)request.getSession().getAttribute("User") == null) ? user : (User)request.getSession().getAttribute("User");
		String ret = retMsg;
		if(retMsg.equals("")){
			retMsg="Empty message";
		}
		if (type == Type.AskInfo && retCode == 200) {
			retMsg = "Info sended.";
		} else if (type == Type.SaveSessionServlet && retCode == 200) {
			retMsg = "Session saved for user_id "+user.getId();
		}
		if (retCode != Code.DatabaseNotConnected.ordinal() && type != Type.CatalogSearchServlet && type != Type.RequestedWebsitesServlet && type != Type.CheckConnection)
			try {
				saveInDB(db);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		if (retCode == Code.LogicError.ordinal()) {
			retMsg = "Sorry an internal problem occurred. We are solving it asap.";
		} else {
			retMsg = ret;
		}
		response.setCharacterEncoding("UTF-8");
		String resp = retCode + " " + retMsg;
		response.getWriter().print(resp);
	}
	
	public void sendResponseAndRedirect(String url) throws IOException, ServletException {
		DataBase db = (DataBase)request.getSession().getServletContext().getAttribute("DataBase");
		user = ((User)request.getSession().getAttribute("User") == null) ? user : (User)request.getSession().getAttribute("User");
		String ret = retMsg;
		if (type == Type.AskInfo && retCode == 200) {
			retMsg = "Info sended.";
		} else if(type == Type.TestWebsites && retCode == 200){
			retMsg = "Websites tested";
		} else if (type == Type.SaveSessionServlet && retCode == 200) {
			retMsg = "Session saved for user_id "+user.getId();
		}
		if (retCode != Code.DatabaseNotConnected.ordinal() && type != Type.CatalogSearchServlet && type != Type.RequestedWebsitesServlet && !retMsg.equals(""))
			try {
				saveInDB(db);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		if (retCode == Code.LogicError.ordinal()) {
			retMsg = "Sorry an internal problem occurred. We are solving it asap.";
		} else {
			retMsg = ret;
		}
		response.sendRedirect(url);
	}
	
	public static String getExceptionTrace(Exception e){
		String retMsg = e.toString()+".\nStackTrace :";
		for(int i=0;i<e.getStackTrace().length;i++){
			retMsg = retMsg + "\n"+e.getStackTrace()[i];
		}
		return retMsg;
	}
}