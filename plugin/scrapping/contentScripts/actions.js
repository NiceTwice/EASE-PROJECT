var actions = {
fillThenSubmit:function(msg, callback, sendResponse) {
  var actionStep = msg.detail[msg.bigStep].website[msg.todo].todo[msg.actionStep];
	var loginInput = $(actionStep.login);
  var passwordInput = $(actionStep.password);
	if (loginInput.length == 0){
		if (actionStep.grave == true){
			msg.type = "error: "+ actionStep.what +" input not found";
			sendResponse(msg);
            errorOverlay(msg);
		} else {
			msg.actionStep++;
			callback(msg, sendResponse);
		}
	} else {
    loginInput.click();
		loginInput.val(msg.detail[0].user.login);
    loginInput.blur();
    passwordInput.click();
		passwordInput.val(msg.detail[0].user.password);
		passwordInput.blur();
    $("body").append("<script type='text/javascript'>$('"+actionStep.login+"').change(); $('"+ actionStep.password +"').change();</script>");
    $(actionStep.submit).click();

    msg.actionStep++;
    callback(msg, sendResponse);
	}
},
erasecookies:function(msg, callback, sendResponse){

    var name = msg.detail[msg.bigStep].website[msg.todo].todo[msg.actionStep].search;

    function deleteCookie(cookieName){
        var domain = document.domain;
        var path = "/";
    	document.cookie = cookieName + "=; expires=" + +new Date + "; domain=" + domain + "; path=" + path;
    }

    if(name) {deleteCookie(name)}
    else {
        var cookies = document.cookie.split(";");
        for (var i = 0; i < cookies.length; i++) {
             var cookie = cookies[i];
             var eqPos = cookie.indexOf("=");
             var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
            deleteCookie(name)
        }
    }
    msg.actionStep++;
    callback(msg, sendResponse);

},
waitfor:function(msg, callback, sendResponse){

    var div = msg.detail[msg.bigStep].website[msg.todo].todo[msg.actionStep].search;
    var time = msg.detail[msg.bigStep].website[msg.todo].todo[msg.actionStep].time;
    if(!time){time = 100;}
    var iteration = 0;

	function waitfor(callback){
    if(typeof div === 'string'){div = [div];}
        var absent = true;
        for(var i in div){
            var obj = $(div[i]);
            if(obj.length>0){
                absent = false;
                break;
            }
        }
        if(iteration % 20 == 0){
            console.log("-- waiting for element "+div[0]+" --");
        }
        if (iteration > 100){
            msg.type = "error: connection too long";
            sendResponse(msg);
            errorOverlay(msg);
        } else if(absent){
            setTimeout(function(){
                iteration ++;
                waitfor(callback);
            }, time);
        } else {
            msg.actionStep++;
            callback(msg, sendResponse);
        }
	}

	waitfor(callback);
},
setattr:function(msg, callback, sendResponse){
	var actionStep = msg.detail[msg.bigStep].website[msg.todo].todo[msg.actionStep];
	var input = $(actionStep.search);
	if (input.length == 0){
		if (actionStep.grave == true){
			msg.type = "error: element not found";
			sendResponse(msg);
            errorOverlay(msg);
		} else {
			msg.actionStep++;
			callback(msg, sendResponse);
		}
	} else {
		input.attr(actionStep.attr, actionStep.content);
        msg.actionStep++;
		callback(msg, sendResponse);
	}
},
simulateKeyPress:function(msg, callback, sendResponse){
	var actionStep = msg.detail[msg.bigStep].website[msg.todo].todo[msg.actionStep];
	var input = $(actionStep.search);
	if (input.length == 0){
		if (actionStep.grave == true){
			msg.type = "error: "+ actionStep.what +" input not found";
			sendResponse(msg);
            errorOverlay(msg);
		} else {
			msg.actionStep++;
			callback(msg, sendResponse);
		}
	} else {
		input.click();
        input.focus();
        var e = jQuery.Event("keydown");
        e.which = actionStep.keyCode;
        e.keyCode = actionStep.keyCode;
        input.trigger(e);
        e = jQuery.Event("keypress");
        e.which = actionStep.keyCode;
        e.keyCode = actionStep.keyCode;
        input.trigger(e);
        input.val(input.val()+String.fromCharCode(e.which));
        e = jQuery.Event("keyup");
        e.which = actionStep.keyCode;
        e.keyCode = actionStep.keyCode;
        input.trigger(e);
		msg.actionStep++;
		callback(msg, sendResponse);
	}
},
fill:function(msg, callback, sendResponse){
	var actionStep = msg.detail[msg.bigStep].website[msg.todo].todo[msg.actionStep];
	var input = $(actionStep.search);
	if (input.length == 0){
		if (actionStep.grave == true){
			msg.type = "error: "+ actionStep.what +" input not found";
			sendResponse(msg);
            errorOverlay(msg);
		} else {
			msg.actionStep++;
			callback(msg, sendResponse);
		}
	} else {
        input.select();
		input.click();
        input.focus();
        input.change();
        input.val(msg.detail[0].user[actionStep.what]);
        input.change();
		input.blur();
		msg.actionStep++;
		callback(msg, sendResponse);
	}
},
val:function(msg, callback, sendResponse){
    var actionStep = msg.detail[msg.bigStep].website[msg.todo].todo[msg.actionStep];
	var input = $(actionStep.search);
	if (input.length == 0){
		if (actionStep.grave == true){
			msg.type = "error: "+ actionStep.what +" input not found";
			sendResponse(msg);
            errorOverlay(msg);
		} else {
			msg.actionStep++;
			callback(msg, sendResponse);
		}
	} else {
        input.val(msg.detail[0].user[actionStep.what]);
        msg.actionStep++;
        callback(msg, sendResponse);
	}
},
checkIfPopup:function(msg, callback, sendResponse){
    /*var popupButton = document.createElement("button");
    popupButton.id = "testtest23";
     document.body.appendChild(popupButton);
    $("#testtest23").click(function(){window.open('http://www.zebest3000.com','lenomdusite','width=300, height=250'); return false;});

    $("#testtest23").click();*/


    msg.actionStep++;
    callback(msg, sendResponse);
},
click:function(msg, callback, sendResponse){
	var actionStep = msg.detail[msg.bigStep].website[msg.todo].todo[msg.actionStep];
	var button = $(actionStep.search);
	if (button.length == 0){
		if (actionStep.grave == true){
			msg.type = "error: button not found";
			sendResponse(msg);
            errorOverlay(msg);
		}
		else {
			msg.actionStep++;
			callback(msg, sendResponse);
		}
	} else {
		button.prop("disabled", false);
		window.setTimeout(function() {button.click();}, 250);
		msg.actionStep++;
		callback(msg, sendResponse);
	}
},
clickona:function(msg, callback, sendResponse){
	var actionStep = msg.detail[msg.bigStep].website[msg.todo].todo[msg.actionStep];
	var button = $(actionStep.search);
	if (button.length == 0){
		if (actionStep.grave == true){
			msg.type = "error: button not found";
			sendResponse(msg);
            errorOverlay(msg);
		}
		else {
			msg.actionStep++;
			callback(msg, sendResponse);
		}
	} else {
		button.prop("disabled", false);
		button.get(0).click();
        button.click();
		msg.actionStep++;
		callback(msg, sendResponse);
	}
},
aclick:function(msg, callback, sendResponse){
	var actionStep = msg.detail[msg.bigStep].website[msg.todo].todo[msg.actionStep];
	var button = $(actionStep.search);
	if (button.length == 0){
		if (actionStep.grave == true){
			msg.type = "error: link not found";
			sendResponse(msg);
            errorOverlay(msg);
		}
		else{
			msg.actionStep++;
			callback(msg, sendResponse);
		}
	} else {
		window.location.href = button.attr('href');
		msg.actionStep++;
		msg.type = "completed";
		sendResponse(msg);
	}
},
submit:function(msg, callback, sendResponse){
	var actionStep = msg.detail[msg.bigStep].website[msg.todo].todo[msg.actionStep];
	var form = $(actionStep.search);
	if (form.length == 0){
		if (actionStep.grave == true){
			msg.type = "error: connection form not found";
			sendResponse(msg);
            errorOverlay(msg);
		}
		else {
			msg.actionStep++;
			callback(msg, sendResponse);
		}
	} else {
		form.submit();
		msg.actionStep++;
		callback(msg, sendResponse);
	}
},
wait:function(msg, callback, sendResponse){
    setTimeout(function(){ msg.actionStep++; callback(msg, sendResponse);}, msg.detail[msg.bigStep].website[msg.todo].todo[msg.actionStep].timeout);
},
waitload:function(msg, callback, sendResponse){
	msg.actionStep++;
	msg.type = "completed";
	sendResponse(msg);
},
search:function(msg, callback, sendResponse){
	var actionStep = msg.detail[msg.bigStep].website[msg.todo].todo[msg.actionStep];
	var obj = $(actionStep.search);
	alert("Found: " + obj.length + " search: " + actionStep.search);
	msg.actionStep++;
	callback(msg, sendResponse);
},
goto:function(msg, callback, sendResponse){
	var actionStep = msg.detail[msg.bigStep].website[msg.todo].todo[msg.actionStep];
    var siteUrl;
    if (typeof actionStep.url == "object") {
      siteUrl = (actionStep.url.http + msg.detail[0].user[actionStep.url.subdomain] + "." + actionStep.url.domain);
    }
    else {
      siteUrl = actionStep.url;
    }
    window.location.href = siteUrl;
    msg.actionStep++;
    msg.type = "completed";
	   sendResponse(msg);
}
};



function doThingsConnect(msg, sendResponse) {
	var todo =  msg.detail[msg.bigStep].website[msg.todo].todo;
	if (msg.actionStep >= todo.length){
		msg.type = "completed";
		sendResponse(msg);
        if(msg.todo == "logout") {
            msg.todo = "connect";
            msg.actionStep = 0;
            actions[todo[msg.actionStep].action](msg, doThingsConnect, sendResponse);
        }
		return ;
	}
    console.log("-- Ease action : "+todo[msg.actionStep].action+" --");
	actions[todo[msg.actionStep].action](msg, doThings, sendResponse);
}

function doThings(msg, sendResponse) {
	var todo =  msg.detail[msg.bigStep].website[msg.todo].todo;
	if (msg.actionStep >= todo.length){
		msg.type = "completed";
		sendResponse(msg);
		return ;
	}
    console.log("-- Ease action : "+todo[msg.actionStep].action+" --");
	actions[todo[msg.actionStep].action](msg, doThings, sendResponse);
}

function getHost(url){
    var getLocation = function(href) {
        var l = document.createElement("a");
        l.href = href;
        return l;
    };
    return getLocation(url).hostname;
}

function isConnected(msg){
    console.log("-- Ease action : check if is connected --");
	var object = $(msg.detail[msg.bigStep].website.checkAlreadyLogged[0].search);
	if (object.length == 0)
		return false;
	return true;
}

if (window.top === window) {
extension.runtime.onMessage("goooo", function(msg, sendResponse) {
    if (msg.todo == "checkAlreadyLogged"){
        checkConnectionOverlay(msg);
        if (isConnected(msg) == true) {
            msg.waitreload=true;
			msg.todo = "logout";
			doThingsConnect(msg, sendResponse);
            logoutOverlay(msg);
		} else {
            msg.waitreload=true;
			if (typeof msg.detail[msg.bigStep].logWith === "undefined") {
				msg.todo = "connect";
			} else {
				msg.todo = msg.detail[msg.bigStep].logWith;
			}
			doThingsConnect(msg, sendResponse);
            if(msg.todo == "logout"){
                logoutOverlay(msg);
            } else if(msg.todo == "connect"){
                loginOverlay(msg);
            }
		}
	} else if(msg.todo == "end"){
        endOverlay();
        msg.type = "completed";
        sendResponse(msg);
    } else if(msg.todo=="nextBigStep"){
        msg.type = "completed";
        sendResponse(msg);
    }
	else {
        doThingsConnect(msg, sendResponse);
        if(msg.todo == "logout"){
            logoutOverlay(msg);
        } else if(msg.todo == "connect"){
            loginOverlay(msg);
        }
    }
});
    
extension.runtime.onMessage("logout", function(msg, sendResponse) {
    if (msg.todo == "logout"){
        logoutOverlay(msg);
        doThings(msg, sendResponse);
	} else if(msg.todo == "end"){
        endOverlay();
        msg.type = "completed";
        sendResponse(msg);
    } else if(msg.todo=="nextBigStep"){
        msg.type = "completed";
        sendResponse(msg);
    }
	else {}
});
    
extension.runtime.onMessage("reconnect", function(msg, sendResponse) {
    if (msg.todo == "checkAlreadyLogged"){
        checkConnectionOverlay(msg);
        if (isConnected(msg) == false) {
			msg.todo = "connect";
			doThings(msg, sendResponse);
            logoutOverlay(msg);
		} else {
            msg.waitreload=true;
			msg.type = "logout fail";
		    sendResponse(msg);
		}
	} else if(msg.todo == "end"){
        endOverlay();
        msg.type = "completed";
        sendResponse(msg);
    } else if(msg.todo=="nextBigStep"){
        msg.type = "completed";
        sendResponse(msg);
    }
	else {
        doThings(msg, sendResponse);
        if(msg.todo == "logout"){
            logoutOverlay(msg);
        } else if(msg.todo == "connect"){
            loginOverlay(msg);
        }
    }
});
    
extension.runtime.onMessage("checkConnected", function(msg, sendResponse) {
    if (msg.todo == "checkAlreadyLogged"){
        checkConnectionOverlay(msg);
        if (isConnected(msg) == true) {
			msg.type = "completed";
		    sendResponse(msg);
		} else {
            endOverlay();
            msg.waitreload=true;
			msg.type = "fail";
		    sendResponse(msg);
		}
	}
});

}