<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<div class="RightSideViewTab" id="RequestedWebsitesTab">
	<button id="quit">
		<i class="fa fa-times"></i>
	</button>

	<div class="requestedWebsitesView">
		<div class="requestedWebsitesHeader">
			<p>People asked if we could add these websites</p>
		</div>

	</div>
</div>

<script>
	$(document).ready(function() {
		postHandler.post('requestedWebsites', {}, function() {
		}, function(retMsg) {
			printRequestedWebsites(retMsg);
		}, function(retMsg) {
		}, 'text');
	});

	function printRequestedWebsites(string) {
		var requests = string.split(";");
		for ( var i in requests) {
			if (i > 0) {
				var email = requests[i].split("-SENTBY-")[1];
				var website = requests[i].split("-SENTBY-")[0];
				$('.requestedWebsitesView')
						.append(
								"<div class='requestedWebsite' website='"
										+ requests[i].split("-SENTBY-")[0]
										+ "'><button class='quit'>X</button><p>Website : "
										+ requests[i].split("-SENTBY-")[0]
										+ " 		ASKED BY email : "
										+ requests[i].split("-SENTBY-")[1]
										+ "</p></div>");
			}
		}
		$('.requestedWebsite .quit').click(eraseWebsites);
	}

	function eraseWebsites() {
		var div = $(this).parent();
		var toErase = div.attr('website');
		postHandler.post('eraseRequestedWebsite', {
			toErase : toErase
		}, function() {
		}, function(retMsg) {
			div.remove();
		}, function(retMsg) {
		}, 'text');
	}
</script>