function printEmailsFromJson(e){var i=JSON.parse(e);i.forEach(function(e){$("#UnregisteredUsersTab #results").append("<div email='"+e.email+"'><i class='fa fa-times delete-unregistered-email'></i>"+e.date+" "+e.email+"</div>")})}$(document).ready(function(){postHandler.post("GetUnregisteredEmails",{},function(){$("#UnregisteredUsersTab #results div").remove()},printEmailsFromJson,function(){}),$("#UnregisteredUsersTab #results").on("click",".delete-unregistered-email",function(){var e=$(this),i=e.parent().attr("email");postHandler.post("DeleteUnregisteredEmail",{email:i},function(){},function(){e.parent().remove()},function(){})})});