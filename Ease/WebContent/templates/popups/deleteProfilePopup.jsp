<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<div class="easePopup" id="deleteProfilePopup">
	<div class="title">
		<p>Delete profile</p>
	</div>
	<div class="bodysHandler">
		<div class="popupBody show" id="deleteProfile">
			<div class="handler">
				<div class="row infoRow">
					<p class="simpleText">
						By deleting your profile, you will lose all related information and associated accounts.
					</p>
					<p class="simpleText">
						Confirm by entering your Ease password.
					</p>
				</div>
				<form class="row" method="POST" id="deleteProfileForm">
					<div class="row">
						<span class="input password">
							<i class="fa fa-lock placeholderIcon" aria-hidden="true"></i>
							<div class="showPassDiv">
								<i class="fa fa-eye centeredItem" aria-hidden="true"></i>
								<i class="fa fa-eye-slash centeredItem" aria-hidden="true"></i>
							</div>
							<input id="password" type="password" name="password" placeholder="Password">
						</span>
					</div>
					<div class="row text-center errorText errorHandler">
						<p>
						</p>
					</div>
					<div class="row text-center">
						<button class="btn" type="submit">Delete</button>
					</div>
				</form>
				<div class="row text-center">
					<a id="goBack" class="liteTextButton">Go back</a>
				</div>
			</div>
		</div>
	</div>
</div>

<script src="js/popups/deleteProfilePopup.js"></script>