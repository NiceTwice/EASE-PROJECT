package com.Ease.servlet;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.annotation.WebServlet;

import com.Ease.context.DataBase;
import com.Ease.data.Regex;
import com.Ease.data.ServletItem;

@WebServlet("/directInvitation")
public class directInvitation extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public directInvitation() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
		rd.forward(request, response);
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		ServletItem SI = new ServletItem(ServletItem.Type.TheFamilyInvitation, request, response, null);
		
		// Get Parameters
		String	email = SI.getServletParam("email");
		if(email!=null){
			email.replaceAll(" ", "");
			email.replaceAll("\r", "");
			email.replaceAll("\n", "");
			email.replaceAll("\t", "");
		}
		// --
		
		String			alphabet = "azertyuiopqsdfghjklwxcvbnm1234567890AZERTYUIOPQSDFGHJKLMWXCVBN";
		String			invitationCode = "";
		Properties props = new Properties();
		ResultSet		rs;
		Random r = new Random();
		DataBase db = (DataBase)session.getServletContext().getAttribute("DataBase");
		db.connect();

			try {
				if (email == null || Regex.isEmail(email) == false){
					SI.setResponse(ServletItem.Code.BadParameters, "This is not an email.");
				} else {					
					rs = db.get("select * from users where email='" + email + "';");
					if (rs.next()) {
						SI.setResponse(199, "You already have an account.");
					} else {
						rs = db.get("select * from invitations where email='" + email + "';");
						if (rs.next()) {
							invitationCode = rs.getString(2);
						} else {
							for (int i = 0;i < 126 ; ++i) {
								invitationCode += alphabet.charAt(r.nextInt(alphabet.length()));			
							}
							db.set("insert into invitations values ('" + email + "', '" + invitationCode + "', NULL);");
						}

						props.put("mail.smtp.host", "smtp.gmail.com");
						props.put("mail.smtp.socketFactory.port", "465");
						props.put("mail.smtp.socketFactory.class",
								"javax.net.ssl.SSLSocketFactory");
						props.put("mail.smtp.auth", "true");
						props.put("mail.smtp.port", "465");
						Session msession = Session.getDefaultInstance(props,
								new javax.mail.Authenticator() {
									protected PasswordAuthentication getPasswordAuthentication() {
										return new PasswordAuthentication("benjamin@ease-app.co","bpease.P2211");
									}
								});
						MimeMessage message = new MimeMessage(msession);
						message.setFrom(new InternetAddress("benjamin@ease-app.co", "Ease Team"));
						message.setRecipients(Message.RecipientType.TO,
								InternetAddress.parse(email));
						message.setSubject(MimeUtility.encodeText("Active ton compte Ease !", "utf-8", null));
						String link = "https://ease.space/thefamily?email=" + email + "&invitationCode=" + invitationCode;
						message.setContent("<p>*French version below*</p>" +
								"<p></p>" +
								"<p>Hello & welcome to Ease !</p>" +
								"<p></p>" +
								"<p>Ease.space gathers the websites on which you have an account and allows you to login & logout to them in 1 click. All this from a secured personal homepage on your browser. For now, it works on Chrome and Safari.</p>" +
								"<p></p>" +
								"<p>This means that once you have added the credentials of an account on your space, you’ll never have to use them again, regardless of the computer or device you have in front of you. The only thing you need is Internet.</p>" +
								"<p></p>" +
								"<p>To activate your space : click on the link and follow the steps : <a href='"+link+"'>https://ease.space/...</a></p>" +
								"<p></p>" +
								"<p>See you soon !</p>" +
								"<p></p>" +
								"<p>The Ease team</p>" +
								"<p></p>" +
								"<p></p>" +
								"<hr>" +
								"<p></p>" +
								"<p>Hello & bienvenue sur Ease.space !</p>" +
								"<p></p>" +	
								"<p>Ease est ton espace personnel intelligent et sécurisé qui regroupe l'ensemble des sites sur lesquels tu as un compte, et qui t’y connecte et déconnecte automatiquement! Ease fonctionne sur Chrome et Safari.</p>" +
								"<p></p>" +
								"<p>Autrement dit, une fois tes identifiants enregistrés sur ton espace Ease, tu n’auras plus jamais à les utiliser. Et ce, où que tu sois, quelque soit l’ordinateur et en toute sécurité. Seul pré-requis: avoir internet !</p>" +
								"<p></p>" +
								"<p>Pour activer ton espace, clique sur le lien suivant et laisse toi guider: <a href='"+link+"'>https://ease.space/...</a></p>" +
								"<p></p>" +
								"<p>A bientôt !</p>" +
								"<p></p>" +
								"<p>La team Ease</p>"
								, "text/html;charset=utf-8");
						Transport.send(message);
						SI.setResponse(200, "Please, go check your mails at "+ email +" ;)");
					}
				}
			} catch (SQLException e) {
				SI.setResponse(ServletItem.Code.LogicError, "SQL Exception");
			} catch (MessagingException e) {
				SI.setResponse(ServletItem.Code.EMailNotSended, "Error when sending email. Check if the email is correct, or reload the page and try again.");
			}
		SI.sendResponse();
	}
}