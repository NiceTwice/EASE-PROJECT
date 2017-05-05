var addInput=function(s,e,o,t,n,a){var i=this;this.qRoot=$(s),this.name=o,this.qRoot.append('<span class="input '+o+'"><i class="fa '+a+' placeholderIcon" aria-hidden="true"></i></span>'),"password"===t&&$("span.input."+o,this.qRoot).append('<div class="showPassDiv"><i class="fa fa-eye centeredItem" aria-hidden="true"></i><i class="fa fa-eye-slash centeredItem" aria-hidden="true"></i></div>'),$("span.input."+o,this.qRoot).append('<input autocomplete="new-password" type="'+t+'" name="'+o+'" id="'+o+'" placeholder="'+n+'"/>'),this.inputField=$("#"+o,this.qRoot),this.inputField.keyup(function(s){13==s.which&&e.submitButton.click()}),this.val=function(){return i.inputField.val()}};addAppPopup=function(s){var e=this;this.qRoot=$(s),this.parentHandler=this.qRoot.closest("#easePopupsHandler"),this.errorRowHandler=this.qRoot.find(".errorHandler"),this.goBackButtonHandler=this.qRoot.find("#goBack"),this.submitButton=this.qRoot.find("button[type='submit']"),this.shortcutLinkButton=this.qRoot.find("#shortcutLink"),this.appNameHolder=this.qRoot.find("input#appName"),this.appNameInTitle=this.qRoot.find(".titleRow p span"),this.appLogoHandler=this.qRoot.find("#appLogo img"),this.loginPasswordRow=this.qRoot.find(".loginPasswordRow"),this.loginInput=this.loginPasswordRow.find("input[name='login']"),this.passwordInput=this.loginPasswordRow.find("input[name='password']"),this.orDelimiter=this.qRoot.find(".orDelimiter"),this.signInChooseRow=this.qRoot.find(".signInChooseRow"),this.signInAccountSelectRow=this.qRoot.find(".signInAccountSelectRow"),this.signInDetectionErrorHandler=this.signInAccountSelectRow.find(".SignInErrorHandler"),this.signInAccountSelectButtonBack=this.signInAccountSelectRow.find(".buttonBack"),this.sameSsoAppsRow=this.qRoot.find(".sameSsoAppsRow"),this.ssoSelectAccountRow=this.qRoot.find(".ssoSelectAccountRow"),this.ssoNewAccountButton=this.ssoSelectAccountRow.find(".newAccountAdder"),this.accountNameHelper=this.qRoot.find(".accountNameHelper"),this.accountNameHelperImg=this.accountNameHelper.find(".accountLogo img"),this.accountNameHelperName=this.accountNameHelper.find(".accountName"),this.accountNameHelperBackButton=this.accountNameHelper.find(".backButton"),this.currentProfile=null,this.currentApp=null,this.shortcutLinkButton.click(function(){e.close(),easeAddBookmarkPopup.open(e.currentApp,e.currentProfile,e.appNameHolder.val()),easeAddBookmarkPopup.goBackButtonHandler.one("click",function(){e.parentHandler.addClass("myshow"),e.qRoot.addClass("show")})}),this.signInAccountSelectRow.find(".selectable").selectable({classes:{"ui-selected":"selected"}}),this.signInAccountSelectButtonBack.click(function(){e.resetSignInAccounts(),e.resetSimpleInputs(),e.submitButton.addClass("locked"),e.signInAccountSelectRow.addClass("hide");for(var s=0;s<e.activeSections.length;s++)e.activeSections[s].removeClass("hide")}),this.choosenSignInName="",this.signInChooseRow.find(".signInButton").click(function(){if(!$(this).hasClass("selected")){e.signInChooseRow.find(".selected").removeClass("selected"),$(this).addClass("selected");var s=catalog.getAppByName($(this).attr("data"));e.choosenSignInName=$(this).attr("data"),e.signInDetectionErrorHandler.removeClass("show"),e.signInDetectionErrorHandler.find("span").text(e.choosenSignInName),e.showSignInAccounts(s.id)}}),this.createSignInSelectorDiv=function(s){var e=$('<div class="accountLine" appId='+s.id+'><div class="checkBoxInput"><span class="fa-stack"><i class="fa fa-square-o fa-stack-2x"></i><i class="fa fa-check fa-stack-1x"></i></span></div><p class="accountName">'+s.getAccountInformationValue("login")+"</p></div>");return e},this.showSignInButtons=function(s){e.signInChooseRow.find(".signInButton").removeClass("show");for(var o,t=0;t<s.length;t++)o=catalog.getAppById(s[t]).name,e.signInChooseRow.find(".signInButton[data="+o+"]").addClass("show")},this.showSignInAccounts=function(s){for(var o=0;o<e.activeSections.length;o++)e.activeSections[o].addClass("hide");var t=e.signInAccountSelectRow.find(".accountsHolder");t.find(".accountLine").remove();for(var n,a=easeAppsManager.getAppsByWebsiteIdNotEmpty(s),o=0;o<a.length;o++)n=e.createSignInSelectorDiv(a[o]),o||(n.addClass("selected"),n.addClass("ui-selected"),n.addClass("ui-selectee")),t.append(n);a.length?e.submitButton.removeClass("locked"):(e.submitButton.addClass("locked"),e.signInDetectionErrorHandler.addClass("show")),e.signInAccountSelectRow.removeClass("hide")},this.activeSections=[],this.createSsoAccountSelectDiv=function(s,e,o){var t=$('<div class="accountLine"><div class="accountLogo"><img src="'+o+'"/></div><p class="accountName">'+e+"</p></div>");return t},this.sameAccountsVar=[],this.createSameAccountDiv=function(s,e){var o=$('<div class="appHandler checkable"><div class="appLogo"><img src="'+s+'"></div><div class="appName"><p>'+e+"</p></div></div>");return o},this.resetSameAccountsRow=function(){for(var s=0;s<e.sameAccountsVar.length;s++)e.sameAccountsVar[s].qRoot.remove();this.sameAccountsVar=[]},this.resetSimpleInputs=function(){e.loginInput.val(""),e.passwordInput.val("")},this.resetSignInAccounts=function(){e.signInAccountSelectRow.find(".accountLine").remove(),e.signInDetectionErrorHandler.removeClass("show"),e.signInChooseRow.find(".selected").removeClass("selected")},this.resetPasswordShows=function(){e.qRoot.find("input[name='password']").attr("type","password"),e.qRoot.find(".showPassDiv.show").removeClass("show")},this.setupSameAccountsDiv=function(s){var o,t=catalog.getAppsBySsoId(s);e.resetSameAccountsRow();var n=e.sameSsoAppsRow.find(".selectHandler");n.find(".appHandler").remove();for(var a=t.length-1;a>=0;a--)t[a].id!=e.currentApp.id&&(o={},o.name=t[a].name,o.ssoId=t[a].ssoId,o.imgSrc=t[a].imgSrc,o.websiteId=t[a].id,o.qRoot=e.createSameAccountDiv(o.imgSrc,o.name),e.sameAccountsVar.push(o));for(var a=0;a<e.sameAccountsVar.length;a++)n.append(e.sameAccountsVar[a].qRoot)},this.choosenSsoAccountLogin=null,this.selectExistingSsoAccounts=function(){for(var s=easeAppsManager.getAppsByLoginAndSsoId(e.choosenSsoAccountLogin,e.currentApp.ssoId),o=0;o<e.sameAccountsVar.length;o++){e.sameAccountsVar[o].qRoot.removeClass("checked"),e.sameAccountsVar[o].qRoot.addClass("checkable");for(var t=0;t<s.length;t++)if(e.sameAccountsVar[o].websiteId==s[t].websiteId){e.sameAccountsVar[o].qRoot.addClass("checked"),e.sameAccountsVar[o].qRoot.removeClass("checkable");break}}},this.accountNameHelperBackButton.click(function(){e.resetSimpleInputs(),e.choosenSsoAccountLogin=null,e.submitButton.addClass("locked"),e.accountNameHelper.addClass("hide"),e.sameSsoAppsRow.addClass("hide"),e.ssoSelectAccountRow.removeClass("hide")}),this.ssoNewAccountButton.click(function(){e.ssoSelectAccountRow.addClass("hide"),e.initializeInputsRow();for(var s=0;s<e.sameAccountsVar.length;s++)e.sameAccountsVar[s].qRoot.addClass("checkable"),e.sameAccountsVar[s].qRoot.removeClass("checked");e.sameSsoAppsRow.removeClass("hide")}),this.setupSsoAccountChooseRow=function(s){var o=e.ssoSelectAccountRow.find(".accountsHolder"),t=o.find(".newAccountAdder");o.find(".accountLine:not(.newAccountAdder)").remove();for(var n,a=e.getLoginList(easeAppsManager.getAppsBySsoId(s.ssoId)),i=catalog.getSsoById(e.currentApp.ssoId),c=0;c<a.length;c++)n=e.createSsoAccountSelectDiv(s.ssoId,a[c],null!=i?i.imgSrc:""),n.click(function(){e.choosenSsoAccountLogin=$(this).find(".accountName").text(),e.accountNameHelperName.text(e.choosenSsoAccountLogin).val(e.choosenSsoAccountLogin),e.accountNameHelperImg.attr("src",$(this).find(".accountLogo img").attr("src")),e.accountNameHelper.removeClass("hide"),e.setupSameAccountsDiv(s.ssoId),e.selectExistingSsoAccounts(),e.sameSsoAppsRow.removeClass("hide"),e.ssoSelectAccountRow.addClass("hide"),e.submitButton.removeClass("locked")}),t.before(n)},this.getLoginList=function(s){for(var e=[],o=0;o<s.length;o++)-1==e.indexOf(s[o].getAccountInformationValue("login"))&&e.push(s[o].getAccountInformationValue("login"));return e},this.open=function(s,o){currentEasePopup=e,e.reset(),e.currentApp=s,e.currentProfile=o,e.setName(s.name),e.appLogoHandler.attr("src",s.imgSrc),s.canLoginWith.length&&(e.showSignInButtons(s.canLoginWith),e.signInChooseRow.removeClass("hide"),e.orDelimiter.removeClass("hide"),e.activeSections.push(e.orDelimiter)),-1==s.ssoId?(e.loginPasswordRow.removeClass("hide"),e.initializeInputsRow(),e.activeSections.push(e.loginPasswordRow)):(e.setupSameAccountsDiv(s.ssoId),easeAppsManager.getAppsBySsoId(s.ssoId).length?(e.setupSsoAccountChooseRow(s),e.ssoSelectAccountRow.removeClass("hide"),e.activeSections.push(e.ssoSelectAccountRow)):(e.loginPasswordRow.removeClass("hide"),e.initializeInputsRow(),e.sameSsoAppsRow.removeClass("hide"),e.activeSections.push(e.loginPasswordRow))),e.parentHandler.addClass("myshow"),e.qRoot.addClass("show")},this.currentInputs=[],this.initializeInputsRow=function(){e.loginPasswordRow.removeClass("hide"),e.currentApp.inputs.forEach(function(s){e.currentInputs.push(new addInput(e.loginPasswordRow,e,s.name,s.type,s.placeholder,s.placeholderIcon))}),$("input",e.loginPasswordRow).on("input",function(){e.checkInputsRow()?e.submitButton.removeClass("locked"):e.submitButton.addClass("locked")})},this.resetInputsRow=function(){$(".input",e.loginPasswordRow).remove(),e.currentInputs=[]},this.checkInputsRow=function(){for(var s=0;s<e.currentInputs.length;s++)if(!e.currentInputs[s].val().length)return!1;return!0},this.close=function(){e.qRoot.removeClass("show"),e.parentHandler.removeClass("myshow")},this.reset=function(){e.currentProfile=null,e.currentApp=null,e.activeSections=[],e.choosenSsoAccountLogin=null,e.resetSimpleInputs(),e.resetSameAccountsRow(),e.resetSignInAccounts(),e.resetPasswordShows(),e.submitButton.addClass("locked"),e.errorRowHandler.removeClass("show"),e.loginPasswordRow.addClass("hide"),e.signInChooseRow.addClass("hide"),e.signInAccountSelectRow.addClass("hide"),e.sameSsoAppsRow.addClass("hide"),e.ssoSelectAccountRow.addClass("hide"),e.orDelimiter.addClass("hide"),e.accountNameHelper.addClass("hide"),e.resetInputsRow()},this.setName=function(s){e.appNameHolder.val(s),e.appNameInTitle.text(s)},this.submitButton.click(function(){e.errorRowHandler.removeClass("show");var s=e.appNameHolder.val(),o=e.currentProfile.id,t=e.signInAccountSelectRow.find(".selected"),n=t.length?$(t[0]).attr("appid"):"",a=[],i=[],c=easeAppsManager.getAppByLoginAndSsoId(e.choosenSsoAccountLogin,e.currentApp.ssoId);null!=c&&i.push({name:"login",value:e.choosenSsoAccountLogin});var r=null!=c?c.id:"";a.push(e.currentApp.id.toString());for(var d=0;d<e.sameAccountsVar.length;d++)e.sameAccountsVar[d].qRoot.hasClass("checkable")&&e.sameAccountsVar[d].qRoot.hasClass("checked")&&a.push(e.sameAccountsVar[d].websiteId.toString());var l="AddClassicApp",u="ClassicApp";if(n.length&&(l="AddLogwithApp",u="LogwithApp"),null!=e.choosenSsoAccountLogin&&(l="AddClassicAppSameAs"),"AddClassicApp"!=l||e.checkInputsRow()){e.submitButton.addClass("loading");var p=JSON.stringify(a),h={name:s,profileId:o,websiteIds:p,logwithId:n,appId:r};e.currentInputs.forEach(function(s){if(h[s.name]=s.val(),"password"!=s.name){var e={};e.name=s.name,e.value=s.val(),i.push(e)}}),postHandler.post(l,h,function(){e.submitButton.removeClass("loading")},function(o){for(var t,c=JSON.parse(o),r=0;r<c.length;++r){t=catalog.getAppById(a[r]);var d=new MyApp;d.init(n,i,t.id,0==r?s:t.name,c[r],t.ssoId,!0,t.imgSrc,"",u),t.increaseCount(),e.currentProfile.addApp(d),d.scaleAnimate()}e.close()},function(s){e.errorRowHandler.find("p").text(s),e.errorRowHandler.addClass("show"),setTimeout(function(){e.errorRowHandler.removeClass("show")},3e3)},"text")}}),this.goBackButtonHandler.click(function(){e.close()})};var easeAddAppPopup;$(document).ready(function(){easeAddAppPopup=new addAppPopup($("#addAppPopup"))});