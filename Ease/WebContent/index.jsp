<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/xhtml1-transitional.dtd">
<html xmlns="http://w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" contentType="text/html; charset=UTF-8"/>
    <meta name="viewport" content="initial-scale=1, maximum-scale=1"/>
    <title>EASE.space</title>
    <link rel="chrome-webstore-item" href="https://chrome.google.com/webstore/detail/echjdhmhmgildgidlcdlepfkaledeokm">
    
    <link rel="icon" type="image/png" href="resources/icons/APPEASE.png" />
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="https://code.jquery.com/ui/1.12.0/jquery-ui.js"></script>
    <link rel="stylesheet" href="css/default_style.css" />
    <link href='https://fonts.googleapis.comcss?family=Source+Sans+Pro' rel='stylesheet' type='textcss' />
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" />
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
	<link rel="manifest" href="manifest.json">
	
	<script src="js/classie.js"></script>
   	<script src="js/owl.carousel.js"></script>
    <script src="js/basic-utils.js" ></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"> </script>
    <script src="js/jquery.mousewheel.min.js"></script>
    
		<link rel="stylesheet" type="text/css" href="css/lib/fonts/font-awesome-4.2.0/css/font-awesome.min.css" />
		<link rel="stylesheet" type="text/css" href="css/lib/dropDownMenu/dropdown.css" />
		<script src="js/snap.svg-min.js"></script>
    	<script src="js/modalEffects.js"></script>
    	<script src="js/selectFx.js"></script>
		<link rel="stylesheet" type="text/css" href="component.css" />
    <!-- Start of Async Drift Code -->
<!-- <script>
!function() {
  var t;
  if (t = window.driftt = window.drift = window.driftt || [], !t.init) return t.invoked ? void (window.console && console.error && console.error("Drift snippet included twice.")) : (t.invoked = !0, 
  t.methods = [ "identify", "track", "reset", "debug", "show", "ping", "page", "hide", "off", "on" ], 
  t.factory = function(e) {
    return function() {
      var n;
      return n = Array.prototype.slice.call(arguments), n.unshift(e), t.push(n), t;
    };
  }, t.methods.forEach(function(e) {
    t[e] = t.factory(e);
  }), t.load = function(t) {
    var e, n, o, r;
    e = 3e5, r = Math.ceil(new Date() / e) * e, o = document.createElement("script"), 
    o.type = "text/javascript", o.async = !0, o.crossorigin = "anonymous", o.src = "https://js.driftt.com/include/" + r + "/" + t + ".js", 
    n = document.getElementsByTagName("script")[0], n.parentNode.insertBefore(o, n);
  });
}();
drift.SNIPPET_VERSION = '0.2.0'
drift.load('syhukkp32g5k')
</script> -->
<!-- End of Async Drift Code -->
</head>
<script type="text/javascript">
function getUserNavigator(){
	var ua = navigator.userAgent;

	var x = ua.indexOf("MSIE");
	var y = "MSIE";
	if (x == -1)
	{
	  x = ua.indexOf("Firefox");
	  y = "Firefox";
	  if(x == -1)
	  {
	    if(x == -1)
	    {
	      x = ua.indexOf("Chrome");
	      y = "Chrome";
	      if(x == -1)
	      {
	        x = ua.indexOf("Opera");
	        y = "Opera";
	        if(x == -1)
	        {
	          x = ua.indexOf("Safari");
	          if( x != -1)
	          {
	            x = ua.indexOf("Version");
	            y = "Safari";
	          }
	        }
	      }
	    }
	  }
	}
	return (y);
}

function deleteOverlay(item){
	var suppDiv = $(item).closest('.logoItem');

	suppDiv.remove();	
}
function onInputFocus( ev ) {
	classie.add( ev.target.parentNode, 'input--filled' );
}

function onInputBlur( ev ) {
	if( ev.target.value.trim() === '' ) {
		classie.remove( ev.target.parentNode, 'input--filled' );
	}
}

function addAppRequest(item){
	var profile = $(item).closest('.MobilePreview');
	var logoItem = $(item).closest('.logoItem');
	var content = $(item).closest('.content');
	var login = $(content).find('#login');
	var password = $(content).find('#password');
	
	
	$(logoItem).find('.imageBox').append($('<i class="fa fa-spinner tmp"></i>'));
	$.post(
			'addApp',
			{
				login: $(login).val(),
				password: $(password).val(),
				profileId: $(profile).attr("index"),
				siteId: $(logoItem).attr("index")
			},
			function (data){
				$(logoItem).find('.tmp').removeClass('fa-spinner');
				if (data[0] == 's'){
					$(logoItem).find('.tmp').addClass('fa-check');
					setTimeout(function(){
						$(logoItem).find('.tmp').remove();
					}, 1000);
				} else {
					$(logoItem).find('.tmp').addClass('fa-times');		
					$(logoItem).find('.tmp').css("color", "red");							
					setTimeout(function() {
						$(logoItem).remove();						
					}, 1000);
				}
			},
			'text'
			);
	$(item).closest('.windowAddApp').remove();
}

$(document).ready(function(){
	$('#helloButton').click(function(){
		$('#loading').addClass("la-animate");
		var          parent = $(this).closest('form');
		var 		 email = $(parent).find("#email").val();
		var 		 password = $(parent).find("#password").val();

		$(parent).find('.alertDiv').removeClass('show');		
        $.post(
                'connection', // Un script PHP que l'on va créer juste après
                {
                    email : email,  // Nous récupérons la valeur de nos inputs que l'on fait passer à connexion.php
                    password : password
                },

                function(data){ // Cette fonction ne fait rien encore, nous la mettrons à jour plus tard
                	if (data[0] == 's'){
                		$('#loading').removeClass("la-animate");
                    	window.location.replace("index.jsp");
                	}else {
                		$('#loading').removeClass("la-animate");
                		$(parent).find('.alertDiv').addClass('show');
                		$(parent).find('#password').val('');
                	}
                },

                'text' // Nous souhaitons recevoir "Success" ou "Failed", donc on indique text !
             );
        });
	$("#loginForm").submit(function(e){ // On sélectionne le formulaire par son identifiant
	    e.preventDefault(); // Le navigateur ne peut pas envoyer le formulaire
	});
});
</script>

<script type="text/javascript">
$(document).ready(function(){
	 $('.owl-carousel').owlCarousel({
		items : 3,
	    itemsCustom : false,
	    itemsDesktop : [1199,3],
	    itemsDesktopSmall : [980,3],
	    itemsTablet: [768,3],
	    itemsTabletSmall: false,
	    itemsMobile : [479,1],
	    singleItem : false,
	    itemsScaleUp : false,
	    pagination: false
	 });
	 $('.owl-carousel').on('mousewheel', '.owl-stage', function (e) {
		    if (e.deltaY>0) {
		    	$('.owl-carousel').trigger('next.owl');
		    } else {
		    	$('.owl-carousel').trigger('prev.owl');
		    }
		    e.preventDefault();
		});
});
</script>
<script type="text/javascript">
$(document).ready(function(){
$('#hideCookies').click(function(){
	setCookie("hideCookies", "true", 365);
	$('.cookiesInfo').css('display', 'none');
});
	var cookie = getCookie('hideCookies');

	if (cookie != 'true')
		$('.cookiesInfo').css('display', 'block');
});	
</script>

<body role="document" class="mainBody">
<%@ page import="java.util.Base64" %>
<%@ page import="java.util.Base64.Encoder" %>
<%@ page import="java.nio.charset.StandardCharsets" %>

<div class="cookiesInfo" style="display: none;">
<p>En poursuivant votre navigation, vous acceptez l'utilisation de cookies dans le cadre de l’authentification, la sécurité et l’intégrité du site et des produits.</p>
<button id="hideCookies"><i class="fa fa-times" aria-hidden="true"></i></button>
</div>
<% if (session.getValue("User") == null){ %>
	<%@ include file="templates/loginBody.jsp"%>
<%}else {%>
	<%@ include file="templates/Header.jsp"%>
	<%@ include file="templates/loggedBody.jsp"%>
<%}%>
<%@ include file="templates/Footer.jsp" %>
<div class="la-anim-10" id="loading"></div>
<%@ include file="templates/SimpleAlertPopup.jsp" %>

<div id="chatButton" >
	<i class="fa fa-comments-o" aria-hidden="true"></i>
	<a href="https://www.facebook.com/messages/easeplatform" target="_blank"></a>
</div>
</body>
</html>