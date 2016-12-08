<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/xhtml1-transitional.dtd">
<html xmlns="http://w3.org/1999/xhtml">
<head>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
	<meta http-equiv="Content-Type" contentType="text/html; charset=UTF-8" />
	<meta name="viewport" content="initial-scale=1, maximum-scale=1" />
	<meta name="description"
	content="The platform Ease allows you to gather all your university web services and your favorites websites (social networks, medias, tools, streaming etc.) in order to connect to them in 1 click, without using neither ids nor passwords! (only one remains: your Ease password)." />
	<title>Ease</title>
	<link rel="chrome-webstore-item"
	href="https://chrome.google.com/webstore/detail/echjdhmhmgildgidlcdlepfkaledeokm">
	<link rel="icon" type="image/png" href="resources/icons/APPEASE.png" />
	<script src="js/jquery1.12.4.js"></script>
	<script src="js/jquery-ui-1.12.0.js"></script>
	<link rel="stylesheet" href="css/default_style.css" />
	<link href='https://fonts.googleapis.comcss?family=Source+Sans+Pro'
	rel='stylesheet' type='textcss' />
	<link rel="stylesheet" href="css/bootstrap.css" />
	<link rel="stylesheet" href="css/owl.carousel.css" />
	<link rel="stylesheet" href="css/owl.theme.css" />
	<link rel="stylesheet" href="css/owl.transitions.css" />

	<link rel="stylesheet" href="css/lib/vicons-font/vicons-font.css">
	<link rel="stylesheet" href="css/lib/vicons-font/buttons.css">
	<link rel="stylesheet" href="css/lib/textInputs/set1.css">
	<link rel="stylesheet" href="css/lib/borderLoading/component.css">
	<link rel="stylesheet" href="css/lib/niftyPopupWindow/component.css">
	<link rel="stylesheet" href="css/lib/ColorSelect/cs-select.css">
	<link rel="stylesheet" href="css/lib/ColorSelect/cs-skin-boxes.css">
	<link rel="stylesheet" href="css/hover.css">
	<link rel="manifest" href="manifest.json">

	<script src="js/postHandler.js"></script>
	<script src="js/form/form.js"></script>
	<script src="js/form/errorMsg.js"></script>
	<script src="js/form/input.js"></script>
	<script src="js/form/popup.js"></script>
	<script src="js/form/emailSuggestions.js"></script>

	<script src="js/classie.js"></script>
	<script src="js/Sortable.js"></script>
	<script src="js/jquery.binding.js"></script>
	<script src="js/owl.carousel.js"></script>
	<script src="js/basic-utils.js" ></script>
	<script src="js/jquery.mousewheel.min.js"></script>
	<script src="js/footer.js"></script>
	<script src="js/tutorial.js"></script>
	<script src="js/catalog/catalogApp.js"></script>
	<script src="js/catalog/updates/update.js"></script>
	<script src="js/catalog/updates/updatesManager.js"></script>
	<script src="js/catalog/catalog.js"></script>
	<script src="js/profiles.js"></script>
	<script src="js/alertPopup.js"></script>
	<script src="js/loading.js"></script>
	<script src="js/header.js"></script>
	<script src="js/app.js"></script>
	<script src="js/shortcut.js"></script>
	<script src="js/dashboard.js"></script>
	<script src="js/tracker.js"></script>	
	<link rel="stylesheet" type="text/css" href="css/lib/fonts/font-awesome-4.2.0/css/font-awesome.min.css" />
	<link rel="stylesheet" type="text/css" href="css/lib/dropDownMenu/dropdown.css" />
	<script src="js/snap.svg-min.js"></script>
	<script src="js/modalEffects.js"></script>
	<script src="js/selectFx.js"></script>
	<script src="js/checkConnection.js"></script>
	<link rel="stylesheet" type="text/css" href="component.css" />
	
	<%
	com.Ease.session.SessionSave sessionSave = (com.Ease.session.SessionSave) (session.getAttribute("SessionSave"));
	com.Ease.session.User user = (com.Ease.session.User) (session.getAttribute("User"));
	%>
	<% if (user != null){
		if(user.isAdmin(session.getServletContext())){%>
			<script src="js/robotest.js"></script>
		<%}
	} else { 
		Cookie 	cookie = null;
		Cookie 	cookies[] = request.getCookies();
		String	sessionId = "";
		String 	token = "";
		if (cookies != null){
			for (int i = 0;i < cookies.length ; i++) {
				cookie = cookies[i];
				if((cookie.getName()).compareTo("sId") == 0){
					sessionId = cookie.getValue();
				} else if((cookie.getName()).compareTo("sTk") == 0){
					token = cookie.getValue();
				}
			}
		}
		if(sessionId.length() > 0 && token.length() > 0){ %>
			<jsp:forward page="connectionWithCookies"> 
				<jsp:param name="sessionId" value="<%=sessionId%>" /> 
				<jsp:param name="token" value="<%=token%>" /> 
			</jsp:forward>
		<%}
	}%>
	<script src="js/isMobile.js"></script>
	<script src="js/getNavigator.js"></script>
	<!-- start Mixpanel --><script type="text/javascript">(function(e,a){if(!a.__SV){var b=window;try{var c,l,i,j=b.location,g=j.hash;c=function(a,b){return(l=a.match(RegExp(b+"=([^&]*)")))?l[1]:null};g&&c(g,"state")&&(i=JSON.parse(decodeURIComponent(c(g,"state"))),"mpeditor"===i.action&&(b.sessionStorage.setItem("_mpcehash",g),history.replaceState(i.desiredHash||"",e.title,j.pathname+j.search)))}catch(m){}var k,h;window.mixpanel=a;a._i=[];a.init=function(b,c,f){function e(b,a){var c=a.split(".");2==c.length&&(b=b[c[0]],a=c[1]);b[a]=function(){b.push([a].concat(Array.prototype.slice.call(arguments,
0)))}}var d=a;"undefined"!==typeof f?d=a[f]=[]:f="mixpanel";d.people=d.people||[];d.toString=function(b){var a="mixpanel";"mixpanel"!==f&&(a+="."+f);b||(a+=" (stub)");return a};d.people.toString=function(){return d.toString(1)+".people (stub)"};k="disable time_event track track_pageview track_links track_forms register register_once alias unregister identify name_tag set_config reset people.set people.set_once people.increment people.append people.union people.track_charge people.clear_charges people.delete_user".split(" ");
for(h=0;h<k.length;h++)e(d,k[h]);a._i.push([b,c,f])};a.__SV=1.2;b=e.createElement("script");b.type="text/javascript";b.async=!0;b.src="undefined"!==typeof MIXPANEL_CUSTOM_LIB_URL?MIXPANEL_CUSTOM_LIB_URL:"file:"===e.location.protocol&&"//cdn .mxpnl.com/libs/mixpanel-2-latest.min.js".match(/^\/\//)?"https://cdn.mxpnl.com/libs/mixpanel-2-latest.min.js":"//cdn.mxpnl.com/libs/mixpanel-2-latest.min.js";c=e.getElementsByTagName("script")[0];c.parentNode.insertBefore(b,c)}})(document,window.mixpanel||[]);
mixpanel.init("e87ca36e156107ebbd9a672735e6612c");</script><!-- end Mixpanel -->

</head>

<body role="document" class=<%= ((user != null && user.getBackground()=="logo") ? "'mainBody disableSelection logoBckgrnd'" : "'mainBody disableSelection picBckgrnd'") %>>
	<%@ page import="java.util.Base64" %>
	<%@ page import="java.util.Base64.Encoder" %>
	<%@ page import="java.nio.charset.StandardCharsets" %>
	
	<div id="userEmail" data-content=<%= (user != null) ? user.getEmail() : null %>></div>
	
	<div id="onMobile" style="display:none;">
		<%@ include file="templates/Mobile.jsp" %>
	</div>
	
	<div id="onComputer">
		<div class="cookiesInfo" style="display: none;">
			<p>En poursuivant votre navigation, vous acceptez l'utilisation de cookies dans le cadre de l’authentification, la sécurité et l’intégrité du site et des produits.</p>
			<button id="hideCookies"><i class="fa fa-times" aria-hidden="true"></i></button>
		</div>
		<% if (user == null){ %>
			<%@ include file="templates/loginBody.jsp"%>
		<%} else {%>
			<%@ include file="templates/Header.jsp"%>
			<%@ include file="templates/loggedBody.jsp"%>
		<%}%>
		<%@ include file="templates/Footer.jsp" %>
		<%@ include file="templates/ChatButton.jsp" %>
	</div>
	
</body>
</html>
