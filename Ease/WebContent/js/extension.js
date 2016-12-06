var waitForExtension = true;
$(document).ready(function(){
	setTimeout(function(){
		waitForExtension = false;
		if (!($('#ease_extension').length)) {
	        checkForExtension();
	    } else if(getUserNavigator() == "Safari"){
	    	console.log('id:5624 - navigator:Safari - checks done.')
	    	if(!$('#ease_extension').attr("safariversion") || $('#ease_extension').attr("safariversion") !="2.0.2"){
	    		console.log('id:4X32 - extension:wrong version');
	    		safariExtensionUpdate();
	   		}
	   }
	},100);
});

function sendEvent(obj) {
	if(testApp){
		if (!($(obj).hasClass('waitingLinkImage'))) {
	        var appId = $(obj).closest('.siteLinkBox').attr('id');
	        var link = $(obj).closest('.siteLinkBox').attr('link');
	        var logoImage = $(obj).find('.linkImage');
	        var json = new Object();
	        var event;
	        $(obj).addClass('waitingLinkImage');
	        $(obj).addClass('scaleinAnimation');
	        setTimeout(function() {
	            $(obj).removeClass("waitingLinkImage");
	            $(obj).removeClass('scaleinAnimation');
	        }, 1000);
	        if (typeof link !== typeof undefined && link !== false) {
	        } else {
	        	postHandler.post("askInfo", {
	        		appId : appId,
	        	}, function() {
	        	}, function(retMsg) {
	        		json.detail = JSON.parse(retMsg);
	        		event = new CustomEvent("Test", json);
	        		document.dispatchEvent(event);
	        	}, function(retMsg) {
	        		showAlertPopup(retMsg, true);
	        	}, 'text');
	        }
	    }
	} else {
    if (!($(obj).hasClass('waitingLinkImage'))) {
    	var appId = $(obj).closest('.siteLinkBox').attr('id');
        var link = $(obj).closest('.siteLinkBox').attr('link');
        var logoImage = $(obj).find('.linkImage');
        var json = new Object();
        var event;
        
        easeTracker.trackEvent("App clicks");

        if (!($('#ease_extension').length)) {
            if(waitForExtension){
            	setTimeout(function(){
            		sendEvent(obj);
            	},200);
            } else {
            	checkForExtension();
            }
        }
        
        if(getUserNavigator() == "Safari"){
        	if(!$('#ease_extension').attr("safariversion") || $('#ease_extension').attr("safariversion") !="2.0.2"){
        		safariExtensionUpdate();
        		return;
        	}
        }
        
        $(obj).addClass('waitingLinkImage');
        $(obj).addClass('scaleinAnimation');
        setTimeout(function() {
            $(obj).removeClass("waitingLinkImage");
            $(obj).removeClass('scaleinAnimation');
        }, 1000);
        if (typeof link !== typeof undefined && link !== false) {
            json.detail = {"url":link};
            json.detail.highlight = true;
            if (ctrlDown) json.detail.highlight = false;
            easeTracker.trackEvent("App successful clicks");
            easeTracker.trackEvent("link connections");
            event = new CustomEvent("NewLinkToOpen", json);
            document.dispatchEvent(event);
        } else {
        	postHandler.post("askInfo", {
        		appId : appId,
        	}, function() {
        	}, function(retMsg) {
        		json.detail = JSON.parse(retMsg);
        		json.detail.highlight = true;
        		if (ctrlDown) json.detail.highlight = false;
        		easeTracker.trackEvent("App successful clicks");
        		easeTracker.trackEvent(json.detail[json.detail.length - 1].website.name + " connections");

        		event = new CustomEvent("NewConnection", json);
        		
        		document.dispatchEvent(event);
        	}, function(retMsg) {
        		showAlertPopup(retMsg, true);
        	}, 'text');
        }
    }
	}
}

function checkForExtension() {
    var ext = $('#ease_extension');

        $('#downloadExtension').css('display', 'block');
    	$('#downloadExtension').find(".classicContent").css('display', 'block');
    	$('#downloadExtension').find('.install-button').css('display', 'inline-block');
       	$('#downloadExtension').find(".safariUpdate").css('display', 'none');
        //if(getUserNavigator() == "Safari"){$('#safariInfoButton').css('display', 'block');}
        $('#downloadExtension').find('.install-button').click(
            function trytoinstall(){
                var NavigatorName = getUserNavigator();
                if (NavigatorName == "Chrome") {
                    chrome.webstore.install(
                        'https://chrome.google.com/webstore/detail/echjdhmhmgildgidlcdlepfkaledeokm',
                        function() {
                            window.location.replace("index.jsp");
                        },
                        function() {});
                }
                else if (NavigatorName == "Safari"){
                	 window.location.replace("https://ease.space/safariExtension/EaseExtension.safariextz");
                    $('#downloadExtension').find('.popupContent').hide();
                    $('#downloadExtension').find('#installdownloaded').show();
                }
            });
}

function safariExtensionUpdate(){
	 $('#downloadExtension').css('display', 'block');
     $('#downloadExtension').find(".safariUpdate").css('display', 'block');
     $('#downloadExtension').find('.install-button').css('display', 'inline-block');
     $('#downloadExtension').find(".classicContent").css('display', 'none');
     $('#safariInfoButton').css('display', 'block');
     $('#downloadExtension').find('.install-button').click(
         function() {
                 //window.location.replace("https://ease.space/safariExtension/EaseExtension.safariextz");
                 $('#downloadExtension').find('.popupContent').hide();
                 $('#downloadExtension').find('#uninstall.safariHelper').show();
         });
}
