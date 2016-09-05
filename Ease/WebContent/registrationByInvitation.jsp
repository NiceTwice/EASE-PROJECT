<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>Ease registration</title>
	<meta http-equiv="Content-Type" contentType="text/html; charset=UTF-8"/>
	<meta name="viewport" content="initial-scale=1, maximum-scale=1"/>
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
</head>
<body id="invitationBody">
	<%
	String invitationCode = request.getParameter("code");
	String emailAddress = request.getParameter("email");
	%>
	<div class="logo">
		<img src="resources/images/Ease_Logo.png"/>
	</div>
	<div id="helpInformations" email="<%= emailAddress %>" code="<%= invitationCode%>" style="display: none;"></div>
	<div id="registrationBlock">
		<div class="title">
			<p>Set up your account !</p>
		</div>
		<form action="registerInv" id="registrationForm">
			<div class="custom-info">
				<p>What's your name ?</p>
			</div>
			<div class="doubleInput">
				<span class="input input--minoru">
					<input class="input__field input__field--minoru" name="fname" type="text" id="input-8" placeholder="First name"/>
					<label class="input__label input__label--minoru" for="input-8">
					</label>
				</span>
				<span class="input input--minoru">
					<input class="input__field input__field--minoru" name="lname" type="text" id="input-8" placeholder="Last name"/>
					<label class="input__label input__label--minoru" for="input-8">
					</label>
				</span>
			</div>
			<div class="custom-info">
				<p>Set up your Ease password</p>
			</div>
			<span class="input input--minoru">
				<input class="input__field input__field--minoru" name="password" type="password" id="input-8" placeholder="Password"/>
				<label class="input__label input__label--minoru" for="input-8">
				</label>
			</span>
			<span class="input input--minoru">
				<input class="input__field input__field--minoru" name="confirmPassword" type="password" id="input-8" placeholder="Confirm password"/>
				<label class="input__label input__label--minoru" for="input-8">
				</label>
			</span>
		</form>
		<div class="alertDiv">
			<p></p>
		</div>
		<p class="registrationTips">We want Ease <span>so safe</span> that if you <span>lose your password</span>, all your data will be lost and we will not be able to recover it. Make sure you will remember it !</p>
		<div class="custom-button">
			<button type="submit" form="registrationForm" value="Submit">GO</button>
		</div>
	</div>
</body>
<script type="text/javascript">
	$(document).ready(function(){
		$('#registrationForm').submit(function(e){
			e.preventDefault();
			var email = $('#helpInformations').attr('email');
			var invitationCode = $('#helpInformations').attr('code');
			var fname = $("#registrationForm input[name='fname']").val();
			var lname = $("#registrationForm input[name='lname']").val();
			var password = $("#registrationForm input[name='password']").val();
			var confirmPassword = $("#registrationForm input[name='confirmPassword']").val();

			$.post(
				$('#registrationForm').attr('action'),
				{
					email: email,
					invitationCode: invitationCode,
					fname: fname,
					lname: lname,
					password: password,
					confirmPassword: confirmPassword
				},
				function(data){
					if (data[0] == 's'){
						$.post(
							'connection', 
							{
								email : email, 
								password : password
							},
							function(data){ 
								window.location.replace("index.jsp");
							},
        			        'text' // Nous souhaitons recevoir "Success" ou "Failed", donc on indique text !
        			        );
					}else {
						var str = data.substring(7, data.length);
						$('.alertDiv').text(str);
						$('.alertDiv').addClass('show');
					}
				},
				'text'
				);
		});
	});
</script>
</html>