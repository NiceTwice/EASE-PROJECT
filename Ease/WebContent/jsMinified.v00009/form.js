var constructorForm=function(t,e){var s=this;this.qRoot=t,this.oParent=e,this.oInputs=[],this.params={},this.qRoot.find("input").each(function(t,e){this.oErrorMsg;var a=$(e).attr("oClass");null!=a&&s.oInputs.push(new Input[a]($(e),s))}),this.qRoot.find("div").each(function(t,e){var a=$(e).attr("oClass");null!=a&&(s.oErrorMsg=new ErrorMsg[a]($(e),s))}),this.qButton=this.qRoot.find("button[type='submit']"),this.qButton.click(function(t){s.submit(t)}),this.isEnabled=!1;for(var a=0;a<this.oInputs.length;++a)this.oInputs[a].listenBy(this.qRoot);s.isEnabled=!1,s.qButton.prop("disabled",!0),s.qButton.removeClass("Active"),this.enable=function(){s.isEnabled=!0,s.qButton.prop("disabled",!1),s.qButton.addClass("Active")},this.disable=function(){s.isEnabled=!1,s.qButton.prop("disabled",!0),s.qButton.removeClass("Active")},this.qRoot.on("StateChanged",function(){s.checkInputs()?s.enable():s.disable()}),this.checkInputs=function(){for(var t=0;t<s.oInputs.length;++t)if(0==s.oInputs[t].isValid)return!1;return!0},this.findByName=function(t){var e=null;return s.oInputs.forEach(function(s){if(s.qInput.attr("name")==t)return void(e=s.qInput)}),e},this.reset=function(){s.disable();for(var t=0;t<s.oInputs.length;++t)s.oInputs[t].reset();s.afterReset()},this.afterReset=function(){},this.submit=function(t){t.preventDefault(),s.oInputs.forEach(function(t){s.params[t.qInput.attr("name")]=t.getVal()}),s.beforeSubmit(),postHandler.post(s.qRoot.attr("action"),s.params,s.afterSubmit,s.successCallback,s.errorCallback)},this.beforeSubmit=function(){},this.afterSubmit=function(){},this.successCallback=function(){},this.errorCallback=function(){}},Form={EditUserNameForm:function(t){constructorForm.apply(this,arguments);var e=this;this.successCallback=function(){var t=e.oInputs[0].getVal();$("#userSettingsButton span").html(t);var s=t.split(" ").length;easeTracker.trackEvent("SettingsEditUserName",{UserNamesCount:s}),e.disable()},this.errorCallback=function(t){$(".errorMessage",e.qRoot).addClass("show")}},EditUserPasswordForm:function(t){constructorForm.apply(this,arguments);var e=this;this.successCallback=function(t){easeTracker.trackEvent("SettingsEditUserPassword"),$("p.response",e.qRoot).removeClass("error"),$("p.response",e.qRoot).addClass("success"),$("p.response",e.qRoot).text(t)},this.errorCallback=function(t){$("p.response",e.qRoot).removeClass("success"),$("p.response",e.qRoot).addClass("error"),$("p.response",e.qRoot).text(t)},this.afterSubmit=function(){e.reset()}},AddAppForm:function(t){constructorForm.apply(this,arguments);var e=this;this.newAppItem=null,this.appsContainer=null,this.helper=null,this.attributesToSet={},this.site_id=null,this.profile_id=null,this.app_id=null,this.postName="AddClassicApp",this.helper=null,this.setHelper=function(t){e.helper=t,e.site_id=e.helper.attr("idx");var s=t.attr();for(var a in s)if(s.hasOwnProperty(a))switch(a){case"team":var i=($(".classicLogin",e.qRoot).append("<input type='"+s[a]+"' name='"+a+"' oClass='NoEmptyInput' placeholder='Team name' />"),new Input.NoEmptyInput($("input[name='"+a+"']",e.qRoot),e));i.listenBy(e.qRoot),e.oInputs.push(i)}e.checkInputs()},this.setAppsContainer=function(t){e.appsContainer=t,e.profile_id=e.appsContainer.closest(".item").attr("id")},this.setNewAppItem=function(t){e.newAppItem=t},this.removeAddedFields=function(){e.oInputs.forEach(function(t){if("login"!=t.qInput.attr("name")&&"password"!=t.qInput.attr("name")&&"name"!=t.qInput.attr("name")){t.qInput.remove();var s=e.oInputs.indexOf(t);e.oInputs.splice(s,1)}})},this.afterReset=function(){e.removeAddedFields()},this.beforeSubmit=function(){e.oParent.close(),e.appsContainer.append(e.newAppItem),e.params={profileId:e.profile_id,websiteId:e.site_id,logwithId:e.app_id},e.oInputs.forEach(function(t){var s=t.qInput.attr("name"),a=t.qInput.val();e.params[s]=a,"password"!=s&&(e.attributesToSet[s]=a)})},this.afterSubmit=function(){e.removeAddedFields()},this.successCallback=function(t){var s=parseInt($(".catalogApp[idx='"+e.site_id+"'] span.apps-integrated i.count").html());$(".catalogApp[idx='"+e.site_id+"'] span.apps-integrated i.count").html(s+1),$(".catalogApp[idx='"+e.site_id+"'] span.apps-integrated").addClass("showCounter"),e.newAppItem.find(".linkImage").addClass("scaleOutAnimation"),setTimeout(function(){e.newAppItem.find(".linkImage").removeClass("scaleOutAnimation"),e.newAppItem=null},1e3),e.newAppItem.find(".linkImage").attr("onclick","sendEvent(this)"),e.newAppItem.attr("webId",e.helper.attr("idx")),e.newAppItem.attr("logwith",null==e.app_id?"false":e.app_id);var a=e.oInputs[0].getVal();e.newAppItem.find(".siteName p").text(a),e.newAppItem.attr("id",t),e.newAppItem.attr("ssoid",e.helper.attr("data-sso"));for(var i in e.attributesToSet)e.attributesToSet.hasOwnProperty(i)&&"password"!=i&&e.newAppItem.attr(i,e.attributesToSet[i]);var o="AddClassicApp"==e.postName?"ClassicApp":"LogWithApp",n=null!=$(".catlogApp[name='"+a+"']").attr("newApp");easeTracker.trackEvent("AddApp",{appType:o,appName:a,AppNewYN:n}),easeTracker.increaseAppCounter(),e.reset(),e.appsContainer=null,e.helper=null,e.attributesToSet={},e.site_id=null,e.profile_id=null,e.app_id=null,e.setPostName("AddClassicApp"),e.helper=null,cleanEmails()},this.errorCallback=function(t){e.newAppItem.remove(),e.reset(),$(parent).find(".alertDiv").addClass("show"),showAlertPopup(t,!0)},this.setPostName=function(t){e.postName=t,e.qRoot.attr("action",e.postName),e.qRoot.trigger("StateChanged")},this.checkInputs=function(){if("AddClassicApp"!=e.postName)return!0;for(var t=0;t<e.oInputs.length;++t)if(0==e.oInputs[t].isValid)return!1;return!0},e.oInputs[1].onEnter(function(t){t.preventDefault(),e.oInputs[2].focus()})},DeleteProfileForm:function(t){constructorForm.apply(this,arguments);var e=this;this.beforeSubmit=function(){$("#loading").addClass("la-animate")},this.afterSubmit=function(){$("#loading").removeClass("la-animate"),e.qRoot.find(".AccountApp.selected").removeClass("selected")},this.successCallback=function(t){e.oParent.close(),e.oParent.targetProfile.remove()},this.errorCallback=function(t){$(parent).find(".alertDiv").addClass("show"),$(parent).find("#password").val(""),showAlertPopup(t,!0)}},ModifyAppForm:function(t){constructorForm.apply(this,arguments);var e=this;this.oPopup=null,this.app=null,this.password=null,this.appId=null,this.aId=null,this.attributesToSet={},this.setApp=function(t){e.app=t,e.appId=e.app.attr("id"),e.aId=e.app.attr("aid");var s=t.attr();for(var a in s)if(s.hasOwnProperty(a))switch(a){case"login":e.oInputs[1].val(s[a]);break;case"name":e.oInputs[0].val(s[a]);break;case"team":var i=$(".classicLogin input[name='"+a+"']",e.qRoot);if(i.length)i.val(s[a]),i.change();else{var o=($(".classicLogin",e.qRoot).append("<input type='"+s[a]+"' name='"+a+"' oClass='NoEmptyInput' placeholder='Team name' />"),new Input.NoEmptyInput($("input[name='"+a+"']",e.qRoot),e));o.listenBy(e.qRoot),e.oInputs.push(o),o.val(s[a])}}},this.removeAddedFields=function(){e.oInputs.forEach(function(t){if("login"!=t.qInput.attr("name")&&"password"!=t.qInput.attr("name")&&"name"!=t.qInput.attr("name")){t.qInput.remove();var s=e.oInputs.indexOf(t);e.oInputs.splice(s,1)}})},this.afterReset=function(){e.removeAddedFields()},this.submit=function(t){t.preventDefault();var s=e.oInputs[0].getVal();e.params={name:s,appId:e.appId};var a=e.qRoot.find(".AccountApp.selected");if(a.length){e.params.logwithId=a.attr("aid");var i="EditLogwithApp";e.app.hasClass("emptyApp")&&(i="WebsiteAppToLogwithApp"),postHandler.post(i,e.params,function(){},function(t){var s=(t.substring(4),e.app.find(".linkImage"));e.oParent.close(),s.addClass("scaleOutAnimation"),setTimeout(function(){s.removeClass("scaleOutAnimation")},1e3);for(var a in e.attributesToSet)e.attributesToSet.hasOwnProperty(a)&&e.app.attr(a,e.attributesToSet[a]);e.app.attr("logwith",e.params.logwithId),e.app.find(".siteName p").text(e.oInputs[0].getVal()),e.app.find(".emptyAppIndicator").remove(),e.app.removeClass("emptyApp"),e.qRoot.find(".AccountApp.selected").removeClass("selected"),easeTracker.trackEvent("EditAppDone"),e.removeAddedFields()},function(){},"text")}else{e.oInputs.forEach(function(t){var s=t.qInput.attr("name"),a=t.qInput.val();e.params[s]=a,"password"!=s?e.attributesToSet[s]=a:null!=a&&""!=a&&(e.password=a)}),null!=e.password&&(e.params.password=e.password);for(var o in e.attributesToSet)e.attributesToSet.hasOwnProperty(o)&&(e.params[o]=e.attributesToSet[o]);var i="EditClassicApp";e.app.hasClass("emptyApp")&&(i="WebsiteAppToClassicApp"),console.log("modify"),postHandler.post(i,e.params,function(){},function(t){var s=(t.substring(4),e.app.find(".linkImage"));e.oParent.close(),s.addClass("scaleOutAnimation"),setTimeout(function(){s.removeClass("scaleOutAnimation")},1e3);for(var a in e.attributesToSet)e.attributesToSet.hasOwnProperty(a)&&e.app.attr(a,e.attributesToSet[a]);e.app.attr("logwith","false"),e.app.find(".siteName p").text(e.oInputs[0].getVal()),e.app.find(".emptyAppIndicator").remove(),e.app.removeClass("emptyApp"),console.log(e.params.login),e.qRoot.find(".AccountApp.selected").removeClass("selected"),e.removeAddedFields(),cleanEmails()},function(){},"text")}}},DeleteAppForm:function(t){constructorForm.apply(this,arguments);var e=this;this.beforeSubmit=function(){$(e.oParent.app).find(".linkImage").addClass("easyScaling")},this.afterSubmit=function(){e.oParent.close(),$(e.oParent.app).find(".linkImage").removeClass("easyScaling")},this.successCallback=function(t){var s=e.oParent.app.attr("webid"),a=parseInt($(".catalogApp[idx='"+s+"'] span.apps-integrated i.count").html());$(".catalogApp[idx='"+s+"'] span.apps-integrated i.count").html(a-1);var i=$(e.oParent.app).attr("name");easeTracker.trackEvent("DeleteApp",{appName:i}),easeTracker.decreaseAppCounter(),1==a&&$(".catalogApp[idx='"+s+"'] span.apps-integrated").removeClass("showCounter"),$(e.oParent.app).find(".linkImage").addClass("deletingApp"),setTimeout(function(){e.oParent.app.remove()},500),cleanEmails()},this.errorCallback=function(t){showAlertPopup(t,!0)}},AddEmailForm:function(t){constructorForm.apply(this,arguments);var e=this;this.addEmail=function(t){$("#editVerifiedEmails").append("<div class='emailLine'><input type='email' name='email' oClass='HiddenInput' value='"+t+'\'readonly /> <span class=\'unverifiedEmail\'><span class=\'verify\'>Verified ?</span><span class=\'sendVerificationEmail\'>Send verification email</span></span><div class=\'sk-fading-circle email-loading\'>\t<span>We are sending you an email</span>\t<div class=\'sk-circle1 sk-circle\'></div>\t<div class="sk-circle2 sk-circle"></div>\t<div class="sk-circle3 sk-circle"></div>\t<div class="sk-circle4 sk-circle"></div>\t<div class="sk-circle5 sk-circle"></div>\t<div class="sk-circle6 sk-circle"></div>\t<div class="sk-circle7 sk-circle"></div>\t<div class="sk-circle8 sk-circle"></div>\t<div class="sk-circle9 sk-circle"></div> <div class="sk-circle10 sk-circle"></div>\t<div class="sk-circle11 sk-circle"></div>\t<div class="sk-circle12 sk-circle"></div></div></div>'),$(".suggested-emails").append("<p class='email-suggestion' email='"+t+"'><span>"+t+"</span></p>")},this.beforeSubmit=function(){$("#AddEmailPopup").addClass("md-show"),$("#AddEmailPopup .waiting").addClass("show")},this.successCallback=function(t){$("#AddEmailPopup .waiting").removeClass("show"),$("#AddEmailPopup .email-sent").addClass("show"),setTimeout(function(){$("#AddEmailPopup").removeClass("md-show"),$("#AddEmailPopup .waiting, #AddEmailPopup .email-sent").removeClass("show")},2e3),e.addEmail(e.oInputs[0].getVal()),$(".newEmail").addClass("show"),$(".newEmailInput").removeClass("show"),easeTracker.trackEvent("EmailAdded"),easeTracker.increaseEmailCount(),e.reset()}},DeleteEmailForm:function(t){constructorForm.apply(this,arguments);var e=this;this.successCallback=function(t){$(".emailLine input[value='"+e.oInputs[0].getVal()+"']").parent().remove(),$(".email-suggestion[email='"+e.oInputs[0].getVal()+"']").remove();var s=$(".verifiedEmail").length;s>1?$(".integrated-emails-count span").html(s+" validated emails"):$(".integrated-emails-count span").html(s+" validated email"),e.reset(),e.oParent.close()}},SendVerificationEmailForm:function(t){constructorForm.apply(this,arguments);var e=this;this.setEmail=function(t){e.oInputs[0].val(t)},this.beforeSubmit=function(){$(".emailLine").has("input[value='"+e.oInputs[0].getVal()+"']").find(".unverifiedEmail").addClass("wait"),$(".emailLine").has("input[value='"+e.oInputs[0].getVal()+"']").find(".email-loading").addClass("show")},this.afterSubmit=function(){setTimeout(function(){$(".emailLine").has("input[value='"+e.oInputs[0].getVal()+"']").find(".email-loading").removeClass("show"),$(".emailLine").has("input[value='"+e.oInputs[0].getVal()+"']").find(".email-sent").addClass("show")},2e3)},this.successCallback=function(t){easeTracker.trackEvent("EmailVerificationSent")}},DeleteAccountForm:function(t){constructorForm.apply(this,arguments);var e=this;this.beforeSubmit=function(){e.qRoot.removeClass("show"),$("#DeleteAccountWait").addClass("md-show"),$(".wait",e.oParent.qRoot).addClass("show")},this.successCallback=function(t){easeTracker.trackEvent("SettingsEaseAccountDeleted"),setTimeout(function(){window.location="index.jsp"},1e3)},this.errorCallback=function(t){$(".wait",e.oParent.qRoot).removeClass("show"),e.qRoot.addClass("show"),$(".errorMessage",e.oParent.qRoot).addClass("show")}},AddUpdateForm:function(t){constructorForm.apply(this,arguments);var e=this;this.beforeSubmit=function(){},this.successCallback=function(t){e.oParent.close(),profiles.forEach(function(s){s.id==e.params.profileId&&(s.addApp(e.params.login,e.params.siteId,e.params.name,t,"",!1,e.qRoot.find("img").attr("src")),console.log(e.oParent.updateIndex),catalog.oUpdate.updates[e.oParent.updateIndex].remove())})},this.errorCallback=function(t){e.oParent.error.html(t)}},RegisterForm:function(t){constructorForm.apply(this,arguments);var e=this;this.email=$("input[name='email']",e.qRoot).val(),this.invitationCode=$("input[name='invitationCode']",e.qRoot).val(),this.params={email:e.email,invitationCode:e.invitationCode},this.beforeSubmit=function(){$(".successHelper",e.qRoot).removeClass("success"),$(".errorHelper",e.qRoot).removeClass("error"),$(".loadHelper, button[type='submit']",e.qRoot).addClass("loading")},this.successCallback=function(t){$(".loadHelper",e.qRoot).removeClass("loading"),$(".successHelper p",e.qRoot).text(t),$(".successHelper",e.qRoot).addClass("success"),setTimeout(function(){window.location="index.jsp"},750)},this.errorCallback=function(t){$(".loadHelper, button[type='submit']",e.qRoot).removeClass("loading"),$(".errorHelper p",e.qRoot).text(t),$(".errorHelper",e.qRoot).addClass("error")}},GetEmailForm:function(t){constructorForm.apply(this,arguments);var e=this;this.beforeSubmit=function(){$(".loadHelper",e.qRoot).addClass("loading"),$(".errorHelper",e.qRoot).removeClass("error"),$(".successHelper",e.qRoot).removeClass("success"),e.qRoot.addClass("loading")},this.successCallback=function(t){$(".loadHelper",e.qRoot).removeClass("loading"),e.qRoot.removeClass("loading"),$(".successHelper p",e.qRoot).text(t),$(".successHelper",e.qRoot).addClass("success"),e.reset()},this.errorCallback=function(t){$(".loadHelper",e.qRoot).removeClass("loading"),e.qRoot.removeClass("loading"),$(".errorHelper p",e.qRoot).text(t),$(".errorHelper",e.qRoot).addClass("error"),e.reset()}},ChangePositionForm:function(t){constructorForm.apply(this,arguments);var e=this;this.jqSites=$(".movable-sites"),this.siteToMove=null,e.enable(),this.setPostName=function(t){e.qRoot.attr("action",t)},this.setSiteToMove=function(t){e.siteToMove=t,e.params.siteId=t.attr("siteId"),e.params.position=t.attr("position")},this.increaseWebsitesPositions=function(t,s){for(var a=t;a<s;a++)$(".website[position='"+a+"']",e.jqSites).attr("position",a+1)},this.decreaseWebsitesPositions=function(t,s){for(var a=t;a<s;a++)$(".website[position='"+a+"']",e.jqSites).attr("position",a-1)},this.successCallback=function(t){var s=parseInt($(".website-position",e.siteToMove).text());switch(e.qRoot.attr("action")){case"goTop":e.increaseWebsitesPositions(1,s),$(".website-position",e.siteToMove).text("1"),e.siteToMove.attr("position","1"),e.jqSites.prepend(e.siteToMove);break;case"top":var a=$(".website[position='"+(s-1)+"']",e.jqSites);a.insertAfter(e.siteToMove),a.attr("position",s),$(".website-position",a).text(s),$(".website-position",e.siteToMove).text(s-1),e.siteToMove.attr("position",s-1);break;case"down":var a=$(".website[position='"+(s+1)+"']",e.jqSites);a.insertBefore(e.siteToMove),a.attr("position",s),$(".website-position",a).text(s),$(".website-position",e.siteToMove).text(s+1),e.siteToMove.attr("position",s+1);break;case"goDown":var i=parseInt(e.jqSites.last().attr("position"));e.decreaseWebsitesPositions(s+1,i),$(".website-position",e.siteToMove).text(i),e.siteToMove.attr("position",i),e.jqSites.append(e.siteToMove)}}}};