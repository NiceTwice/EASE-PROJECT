function showExtensionPopup(){return $("#ease_extension").length?"Safari"!=getUserNavigator()||$("#ease_extension").attr("safariversion")&&"2.2.2"==$("#ease_extension").attr("safariversion")?!1:($("#extension .title p").text("Update your extension"),$("#extension #download #line1").text("A new version of the extension is now available."),$("#extension #download #line2").text("We added new features and made it faster !"),$("#extension #download button").text("Update Ease Extension"),$("#extension").addClass("myshow"),!0):waitForExtension?void setTimeout(function(){return showExtensionPopup()},200):($("#extension").addClass("myshow"),!0)}function sendEvent(e){if(testApp){if(!$(e).hasClass("waitingLinkImage")){var n,t=$(e).closest(".siteLinkBox").attr("id"),o=$(e).closest(".siteLinkBox").attr("link"),i=($(e).find(".linkImage"),new Object);$(e).addClass("waitingLinkImage"),$(e).addClass("scaleinAnimation"),setTimeout(function(){$(e).removeClass("waitingLinkImage"),$(e).removeClass("scaleinAnimation")},1e3),"undefined"!=typeof o&&o!==!1||postHandler.post("AskInfo",{appId:t},function(){},function(e){i.detail=JSON.parse(e),n=new CustomEvent("Test",i),document.dispatchEvent(n)},function(e){showAlertPopup(e,!0)},"text")}}else if(!$(e).hasClass("waitingLinkImage")){var n,t=$(e).closest(".siteLinkBox").attr("id"),o=$(e).closest(".siteLinkBox").attr("link"),i=($(e).find(".linkImage"),new Object);if(showExtensionPopup())return;$(e).addClass("waitingLinkImage"),$(e).addClass("scaleinAnimation"),setTimeout(function(){$(e).removeClass("waitingLinkImage"),$(e).removeClass("scaleinAnimation")},1e3),postHandler.post("AskInfo",{appId:t},function(){},function(e){i.detail=JSON.parse(e);var t="NewConnection";if(i.detail[0]&&i.detail[0].url)i.detail=i.detail[0],t="NewLinkToOpen",easeTracker.trackEvent("ClickOnApp",{type:"LinkApp",appName:i.detail.app_name});else{var o=i.detail[i.detail.length-1];easeTracker.trackEvent("ClickOnApp",{type:o.type,appName:o.app_name,websiteName:o.wbesite_name})}var s=""+new Date;easeTracker.setOnce("TutoDateFirstClickOnApp",s),i.detail.highlight=!ctrlDown,n=new CustomEvent(t,i),document.dispatchEvent(n)},function(e){showAlertPopup(e,!0)},"text")}}var waitForExtension=!0;$(document).ready(function(){setTimeout(function(){waitForExtension=!1,showExtensionPopup(),showExtensionPopup()||$("#tutorial").addClass("myshow")},800)}),$(document).ready(function(){$("#homePageSwitch").change(function(){var e=$(this).is(":checked");easeTracker.setHomepage(e),easeTracker.trackEvent("HomepageSwitch");var n=e.toString();postHandler.post("HomepageSwitch",{homepageState:n},function(){},function(){},function(){})}),$("#chrome button[type='submit'], #safari button[type='submit']").click(function(){window.location="/"}),$("#extension #download #showExtensionInfo").click(function(){$("#extension #step1").removeClass("show"),$("#extension #extensionInfo").addClass("show")}),$("#extension #extensionInfo button").click(function(){$("#extension #extensionInfo").removeClass("show"),$("#extension #step1").addClass("show")}),$("#extension #download button[type='submit']").click(function(){$("#extension #step1 #download").removeClass("show"),"Chrome"==NavigatorName?($("#extension #step1 #chrome").addClass("show"),chrome.webstore.install("https://chrome.google.com/webstore/detail/echjdhmhmgildgidlcdlepfkaledeokm",function(){},function(){})):"Safari"==NavigatorName?($("#extension #step1 #safari").addClass("show"),window.location.replace(location.protocol+"//"+location.hostname+"/safariExtension/EaseExtension.safariextz")):$("#extension #step1 #other").addClass("show")})});