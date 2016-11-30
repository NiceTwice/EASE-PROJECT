<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page import="java.util.Base64" %>
<%@ page import="java.util.Base64.Encoder" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.sql.ResultSet" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script src="js/connection.js"></script>
<script src="js/rAF.js"></script>

<script>
$(document).ready(function(){
	setTimeout(function(){
		var event = new CustomEvent("NewEaseUser", {"detail":"anonymous"});
		document.dispatchEvent(event);
	}, 500);
    
});
</script>
<div id="loginBody">
	<canvas id="demo-canvas" style="position: absolute;"></canvas>
	<div id='search-google'>
		<%@ include file="SearchBar.jsp"%>
	</div>
	<%@ include file="Logout.jsp" %>
<% 
Cookie 	cookie = null;
Cookie 	cookies[] = request.getCookies();
String 	fname = "";
String 	email = "";
if (cookies != null){
	for (int i = 0;i < cookies.length ; i++) {
		cookie = cookies[i];
		if ((cookie.getName()).compareTo("email") == 0){
			email = cookie.getValue();
		}
	}
	if(email.length()>0){
		com.Ease.context.DataBase db = (com.Ease.context.DataBase)session.getServletContext().getAttribute("DataBase");
		try {
			db.connect();
			ResultSet rs = db.get("SELECT firstName FROM users WHERE email='"+email+"';");
			if(rs.next()){
				fname = rs.getString(1);
			}
		} catch (SQLException e) {
			email="";
		}
	}
	
}
%>
	<div class="FormsContainer">
	<% if (email.length() > 0 && fname.length() >0){ %>
		<div class="form" id="knownUser">
			
			<img class='ease-logo' src='resources/icons/Ease_Logo_couleur.png'/>
			<div class="savedUser">
				<a class='new-to-ease' href="http://www.ease-app.co">New to Ease</a>
				<a class='forget-password' href="PasswordLost">Password lost ?</a>
				<p>Hello <%= fname %> !</p>
				<span class="input input--minoru">
					<input style="display:none;" id="email" name="email" value="<%= email %>"/>
					<input class="input__field input__field--minoru" id="password" name="password" type="password" id="input-8" placeholder="Password"/>
					<label class="input__label input__label--minoru" for="input-8"></label>
				</span>
				<div class="alertDiv">
					<p>Incorrect password !</p>
				</div> 
	    		<button id="savedUserButton">Login</button>
	    		<!-- <a class='create-account' href='/getEmailLink'>create an account</a> -->
			</div>
			<div class="sk-fading-circle" id="loadingKnownUser" style="display:none;">
				<div class="sk-circle1 sk-circle"></div>
  				<div class="sk-circle2 sk-circle"></div>
  				<div class="sk-circle3 sk-circle"></div>
  				<div class="sk-circle4 sk-circle"></div>
  				<div class="sk-circle5 sk-circle"></div>
  				<div class="sk-circle6 sk-circle"></div>
  				<div class="sk-circle7 sk-circle"></div>
  				<div class="sk-circle8 sk-circle"></div>
  				<div class="sk-circle9 sk-circle"></div>
  				<div class="sk-circle10 sk-circle"></div>
  				<div class="sk-circle11 sk-circle"></div>
  				<div class="sk-circle12 sk-circle"></div>
			</div>
			<div id="changeAccount">Other account <img class='switch-account' src="resources/icons/account.png" /></div>   
		</div>
	<%}%>
		<div class="form" id="unknownUser" <% if (email.length() > 0 && fname.length() >0){ %> style="visibility:hidden;" <% }%>>
			<img class='ease-logo' src='resources/icons/Ease_Logo_couleur.png'/>
			<form action="connection" method="POST" id="loginForm" role="form">
				<a class='new-to-ease' href="http://www.ease-app.co">New to Ease</a>
				<a class='forget-password' href="PasswordLost">Password lost ?</a>
				<span class="input input--minoru">
					<input class="input__field input__field--minoru" id="email" name="email" type="email" id="input-8" placeholder="Email"/>
					<label class="input__label input__label--minoru" for="input-8"></label>
				</span>
				<span class="input input--minoru">
					<input class="input__field input__field--minoru" id="password" name="password" type="password" id="input-8" placeholder="Password"/>
					<label class="input__label input__label--minoru" for="input-8"></label>
				</span>
				<div class="alertDiv">
					<p>Incorrect password or email !</p>
				</div>
    			<button id="helloButton">Login</button>
    			<!-- <a class='create-account' href='/getEmailLink'>create an account</a> -->
			</form>
			<div class="sk-fading-circle" id="loadingUnknownUser" style="display:none;">
				<div class="sk-circle1 sk-circle"></div>
  				<div class="sk-circle2 sk-circle"></div>
  				<div class="sk-circle3 sk-circle"></div>
  				<div class="sk-circle4 sk-circle"></div>
  				<div class="sk-circle5 sk-circle"></div>
  				<div class="sk-circle6 sk-circle"></div>
  				<div class="sk-circle7 sk-circle"></div>
  				<div class="sk-circle8 sk-circle"></div>
  				<div class="sk-circle9 sk-circle"></div>
  				<div class="sk-circle10 sk-circle"></div>
  				<div class="sk-circle11 sk-circle"></div>
  				<div class="sk-circle12 sk-circle"></div>
			</div>
			<% if (email.length() > 0 && fname.length() >0) {%>
				<div id="back"><%= fname %> account <img class='switch-account' src="resources/icons/account.png" /></div>
			<% } %>
		</div>
	</div>
	
	<div class="phrase">
		<p>A simple homepage to access your web without passwords. <a href="https://ease.space/ieseg" target="_blank">Discover</a></p>
	</div>
	
	<p class="homepageOnoffContainer displayedByPlugin">
		<span>Homepage</span>
		<span class="onoffswitch">
			<input	type="checkbox" name="onoffswitch" class="onoffswitch-checkbox" id="homePageSwitch" />
			<label class="onoffswitch-label" for="homePageSwitch"></label>
		</span>
	</p>
	<!-- <div class='univ-presentation'>
		<h2>We are ease.space</h2>
		<p>We built a home page that enables you to easily connect to website's accounts, without using passwords, and regardless of the computer</p>
		<p>This page is the new way to access your school's web services</p>
		<p>If you do not have your account yet, <a target='_blank' href='http://www.ease-app.co'>let's go</a></p>
	</div>
	-->
</div>