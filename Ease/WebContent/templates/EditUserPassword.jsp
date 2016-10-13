<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<form oClass="EditUserPasswordForm" action="editUserPassword" id="ModifyPasswordForm" class="settingsForm">
	<p>For safety reasons, your password needs to be at least 8
						characters long,  including upper-case and lower-case letters,
						plus at least one numerical digit.</p>
	<input oClass="PasswordInput" type="password" name="currentPassword" placeholder="Current password" />
	<input oClass="PasswordInput" type="password" name="newPassword" placeholder="New password" />
	<input oClass="PasswordInput" type="password" name="confirmNewPassword" placeholder="Confirm new password" />
	<div class="buttonSet">
		<button type="submit">Validate</button>
		<button type="button" oClass="">Cancel</button>
	</div>
</form>

<script>
	var formla = new Form["EditUserPasswordForm"]($("#ModifyPasswordForm"));
</script>