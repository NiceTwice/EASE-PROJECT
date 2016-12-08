$(document).ready(function() {
	/* Buttons behavior */
	$(".admin-menu button").click(openSideViewTab);
	$(".RightSideViewTab button#quit").click(closeSideViewTab);

	/* Add Users behavior */
	$('#return').click(returnClick);
	$('#integrate').click(integrateUsers);

	/* Requested sites behavior */
	$("#enterRequestedWebsitesMode").click(function(){
		getRequestedSites();
	});
	
	$("#sendAllMails").click(function(){
		sendEmailsWebsiteIntegrated();
	});

	/* Tags manager behavior */
	$("#setTags").click(setTagsClick);
	
	$("#cleanSavedSessions").click(cleanSavedSessions);
	
	$("#buttonTestWebsites").click(testWebsites);
	
	/* Move websites positions in catalog */
	$(".goTop").click(function() {
		changePositionForm.setPostName("goTop");
		changePositionForm.setSiteToMove($(this).parent());
	});
	$(".goDown").click(function() {
		changePositionForm.setPostName("goDown");
		changePositionForm.setSiteToMove($(this).parent());
	});
	$(".top").click(function() {
		changePositionForm.setPostName("top");
		changePositionForm.setSiteToMove($(this).parent());
	});
	$(".down").click(function() {
		changePositionForm.setPostName("down");
		changePositionForm.setSiteToMove($(this).parent());
	});
});

/* Interface functions */
function openSideViewTab(e) {
	$(".RightSideViewTab.show").removeClass("show");
	var target = $(e.target).closest("button").attr("target");
	$("#" + target).addClass("show");
}

function closeSideViewTab() {
	$(".RightSideViewTab.show").removeClass("show");
}

/* Add users */

function returnClick() {
	$(".addUsersProgress").addClass('hidden');
	$(".addUsers").removeClass('hidden');
	$('#progressStatus .progress p').remove();
}

function integrateUsers() {
	$(".addUsers").addClass('hidden');
	$('#progressStatus .progress').append("<p>Sending to database ... </p>");
	$(".addUsersProgress").removeClass('hidden');
	$('#progressStatus .progress').append(
			"<p id='sent'>" + emailsSent + "/" + emailsToSend
					+ " emails sent. </p>");
	var form = $(this).closest('#addUsersForm');
	var emailsList = $(form).find('#integrateUsers').val().replace(/ /g, '')
			.split(";");

	var emailsToSend = emailsList.length;
	var emailsSent = 0;

	for ( var email in emailsList) {
		var tab = emailsList[email].split(":");
		sendInvitation(tab[0], tab[1], function() {
			emailsSent++;
			$('#sent').text(emailsSent + "/" + emailsToSend + " emails sent.");
		});
	}
}

function sendInvitation(email, group_id, callback) {
	postHandler.post('createInvitation', {
		email : email,
		groupId : group_id
	}, function() {
	}, function(retMsg) {
		$('#progressStatus .progress').append("<p>" + email + " -> success : "+retMsg);
		callback();
	}, function(retMsg) {
		$('#progressStatus .progress').append(
				"<p>" + email + " -> error : " + retMsg);
		callback();
	}, 'text');
}

/* Requested sites */

function getRequestedSites() {
	postHandler.post('requestedWebsites', {}, function() {
	}, function(retMsg) {
		printRequestedWebsites(retMsg);
	}, function(retMsg) {
	}, 'text');
}

function printRequestedWebsites(string) {
	var requests = string.split(";");
	for ( var i in requests) {
		if (i > 0) {
			$('.requestedWebsitesView').append(
					"<div class='requestedWebsite' website='"
							+ requests[i].split("-SENTBY-")[0]
							+ "' email='"
							+ requests[i].split("-SENTBY-")[1].split("-DATE-")[0]
							+"'><input type='checkbox' class='sendEmail'/><button class='quit'>X</button><p>"
							+ requests[i].split("-SENTBY-")[0]
							+ " ("
							+ requests[i].split("-SENTBY-")[1].split("-DATE-")[0]
							+ ", "
							+ requests[i].split("-SENTBY-")[1].split("-DATE-")[1].substring(0,10)
							+ ")</p></div>");
		}
	}
	$('.requestedWebsite .quit').click(function(){
		eraseWebsite($(this).parent(".requestedWebsite"));
	});
}

function eraseWebsite(div) {
	var toErase = div.attr('website');
	var email = div.attr("email");
	postHandler.post('eraseRequestedWebsite', {
		email: email,
		toErase : toErase
	}, function() {
	}, function(retMsg) {
		div.remove();
	}, function(retMsg) {
	}, 'text');
}

function sendEmailsWebsiteIntegrated(){
	var toSend = {};
	$('.requestedWebsite .sendEmail').each(function(){
		if($(this).is(':checked')){
			var email = $(this).parent(".requestedWebsite").attr("email");
			if(!toSend[email])
				toSend[email]=[];
			toSend[email].push($(this).parent(".requestedWebsite").attr("website"));
		}
	});
	$("#PopupSendEmailWebsite").addClass("md-show");
	var emails=[];
	for (var email in toSend){
		emails.push(email);
	}
	nextPopup(0, emails, toSend);
	
	function nextPopup(i, emails, toSend){
		$("#PopupSendEmailWebsite #accept").unbind("click");
		$("#PopupSendEmailWebsite #close").unbind("click");
		$("#PopupSendEmailWebsite .md-content input").remove();
		var email = emails[i];
		var websites = toSend[email];
		$("#PopupSendEmailWebsite .title").text("Send an email to "+email+" for these websites ?");
		for(var webIndex in websites){
			$("<input value='"+websites[webIndex]+"'></input>").insertBefore("#PopupSendEmailWebsite .md-content .buttonSet");
		}
		$("#PopupSendEmailWebsite #close").click(function(e){
			if(emails[i+1])
				nextPopup(i+1, emails, toSend)
			else
				$("#PopupSendEmailWebsite").removeClass("md-show");
		});
		$("#PopupSendEmailWebsite #accept").click(function(e){
			var values = [];
			$("#PopupSendEmailWebsite .md-content input").each(function(){
				values.push($(this).val());
			});
			sendEmails(email, values, websites);
			if(emails[i+1])
				nextPopup(i+1, emails, toSend)
			else
				$("#PopupSendEmailWebsite").removeClass("md-show");
		});
	}
	
	function sendEmails(email, values, initialValues){
		var valuesToSend="";
		for(var i in values){
			valuesToSend += values[i]+"---&---";
		}
		valuesToSend=valuesToSend.substring(0,valuesToSend.length-7);
		postHandler.post(
				"sendWebsitesIntegrated",
				{email:email, websites:valuesToSend},
				function(){},
				function(retMsg){
					$(".requestedWebsite").each(function(){
						if($(this).attr("email")==email && initialValues.indexOf($(this).attr("website"))!=-1)
							eraseWebsite($(this));
					});
				},
				function(retMsg){
					console.log("Could not send email to "+email+", message : retMsg");
				},
				'text'
		);
	}
}

/* Tags functions */

function setTagsClick() {
	var tags = $('#tagsContainer div').map(function() {
		return $(this).attr("tagId");
	}).get();
	tags = JSON.stringify(tags);
	var websiteId = $("#websiteSelector").val();
	postHandler.post("setTags", {
		websiteId : websiteId,
		tagsId : tags
	}, function() {
	}, function(retMsg) {
		$('#setTags').val("Success");
		$('#setTags').prop('disabled', true);
		setTimeout(function() {
			$('#setTags').val("Validate");
			$('#setTags').prop('disabled', false);
		}, 1000);
	}, function(retMsg) {
		$('#setTags').val(retMsg);
		$('#setTags').prop('disabled', true);
		setTimeout(function() {
			$('#setTags').val("Validate");
			$('#setTags').prop('disabled', false);
		}, 1000);
	}, 'text');
}

function showFormTags() {

	var websiteId = $("#websiteSelector").val();
	$('#tagsContainer').empty();

	if (websiteId == 0) {
		$('#completeForm').attr("style", "display:none");

	} else {
		postHandler
				.post(
						"getTags",
						{
							websiteId : websiteId
						},
						function() {
							$('#completeForm').attr("style", "display:visible");
						},
						function(retMsg) {
							var tags = JSON.parse(retMsg);
							for ( var i in tags) {
								$("#tagsContainer")
										.append(
												'<div tagId="'
														+ tags[i].id
														+ '" class="btn-group tags-group" style="margin-left: 1px; margin-top: 3px;">'
														+ '<a href="#" class="tag btn btn-default"'
														+ 'style="background-color: '
														+ tags[i].color
														+ '; border-color: '
														+ tags[i].color
														+ '; color: white;">'
														+ tags[i].name
														+ '</a>'
														+ '<a href="#" onClick="deleteTag('
														+ tags[i].id
														+ ')" class="btn btn-default delete-tag">X</a>'
														+ '</div>');
							}
						}, function(retMsg) {
						}, 'text');
	}
}

function addTag() {
	var tagId = $("#tagSelector").val();
	if (tagId != 0) {
		if ($("#tagsContainer div[tagId=" + tagId + "]").length == 0) {

			var color = $("#tagSelector option[value=" + tagId + "]").attr(
					"tag-color");
			var name = $("#tagSelector option[value=" + tagId + "]").text();

			$("#tagsContainer")
					.append(
							'<div tagId="'
									+ tagId
									+ '" class="btn-group tags-group" style="margin-left: 1px; margin-top: 3px;">'
									+ '<a href="#" class="tag btn btn-default"'
									+ 'style="background-color: '
									+ color
									+ '; border-color: '
									+ color
									+ '; color: white;">'
									+ name
									+ '</a>'
									+ '<a href="#" onClick="deleteTag('
									+ tagId
									+ ')" class="btn btn-default delete-tag">X</a>'
									+ '</div>');
		}
	}
}

function deleteTag(tagId) {
	$("#completeForm div div[tagId=" + tagId + "]").remove();
}

function cleanSavedSessions(){
	postHandler.post(
		"cleanSavedSessions",
		{},
		function(){},
		function(retMsg){
			console.log(retMsg);
		},
		function(retMsg){
			console.log(retMsg);
		},
		'text'
	);
}

function testWebsites(){
	postHandler.post(
		"testWebsites",
		{},
		function(){},
		function(retMsg){
			var json = {};
			json.detail = JSON.parse(retMsg);
    		event = new CustomEvent("MultipleTests", json);
    		document.dispatchEvent(event);
		},
		function(retMsg){
			console.log(retMsg);
		},
		'text'
	);
}