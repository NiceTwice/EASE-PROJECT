<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<div class="MenuButtonSet middleTopLeft">
	<button id="enterRequestedWebsitesMode" state="off" class="button adminButton">
		<img src="resources/icons/requested_websites.png" />
	</button>
</div>

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
	function enterRequestedWebsitesMode() {
		$('#RequestedWebsitesTab').addClass('show');
	}
	
	$(document).ready(function(){
		$.post(
				'requestedWebsites',
				{},
				function(data) {					
					if(data[0]=='s'){
						printRequestedWebsites(data);
					}
				}, 'text'
		);
	});
	
	function printRequestedWebsites(string){
		var requests = string.split(";");
		for(var i in requests){
			if(i>0){
				var email = requests[i].split("-SENTBY-")[1];
				var website = requests[i].split("-SENTBY-")[0];
				$('.requestedWebsitesView').append("<div class='requestedWebsite' website='"+requests[i].split("-SENTBY-")[0]+"'><button class='quit'>X</button><p>Website : "+requests[i].split("-SENTBY-")[0]+" 		ASKED BY email : "+requests[i].split("-SENTBY-")[1]+"</p></div>");
				$('.requestedWebsite .quit').click(eraseWebsites);
			}
		}
	}

	function leaveRequestedWebsitesMode() {
		$('#RequestedWebsitesTab').removeClass('show');
	}

	$(document).ready(function() {
		$('#enterRequestedWebsitesMode').click(function() {
			leaveTagsManagerMode();
			leaveAddSiteMode();
			leaveChangeBackMode();
			leaveAddUsersMode();
			enterRequestedWebsitesMode();
		});
	});
	
	function eraseWebsites() {
			var div = $(this).parent();
			var toErase = div.attr('website');
			$.post(
					'eraseRequestedWebsite',
					{
						toErase : toErase
					},
					function(data) {					
						if(data[0]=='s'){
							div.remove();
						}
					}, 'text'
			);
		}

	$(document).ready(function() {
		$('#RequestedWebsitesTab #quit').click(function() {
			leaveRequestedWebsitesMode();
		});
	});
	
	function sendInvitation(email, callback){
		$.post(
				'createInvitation',
				{
					email : email
				},
				function(data) {					
					$('#progressStatus .progress').append("<p>"+ email + " -> " + data);
					callback();
				}, 'text'
		);
	}
</script>