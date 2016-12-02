function isConnected(msg){
    console.log("-- Ease action : checking connnection --");
	var object = $(msg.detail[msg.bigStep].website.checkAlreadyLogged[0].search);
	if (object.length == 0){
        console.log("-- Ease action : not connected Intialize connection --");
		return false;
    }
    console.log("-- Ease action : connected. Intialize logout --");
	return true;
}

function whoIsConnected(msg){
    console.log("-- Ease action : checking connnection --");
    
}

function getNewLogin(msg, i){
	if (msg.detail[i].user){
		return {"user":msg.detail[i].user.login, "password":msg.detail[i].user.password};
	} else if (msg.detail[i].logWith){
		return {"user":getNewLogin(msg, i-1).user, "logWith":getHost(msg.detail[i-1].website.loginUrl)};
	}
}

function alreadyVisited(msg){
    var lastConnection = msg.lastConnections[getHost(msg.detail[msg.bigStep].website.loginUrl)];
    if(lastConnection){
        var NewLogin = getNewLogin(msg, msg.bigStep);
        if(NewLogin.logWith){
            if(NewLogin.user == lastConnection.user && NewLogin.logWith == lastConnection.logWith)
                return true
        } else {
            if(lastConnection.user){
                 if(lastConnection.user == getNewLogin(msg, msg.bigStep).user)
                     return true;
            } else {
                 if(lastConnection == getNewLogin(msg, msg.bigStep).user)
                     return true;
            }       
        }
    }
	return false;
}

if (window.top === window) {

extension.runtime.onMessage("goooo", function(msg, sendResponse) {
	if (msg.todo == "checkAlreadyConnected"){
        checkConnectionOverlay(msg);
		if (isConnected(msg) == true) {
			if (alreadyVisited(msg) == true){
				msg.todo = "connect";
				msg.actionStep = msg.detail[msg.bigStep].website[msg.todo].todo.length;
				msg.type = "completed";
				sendResponse(msg);
			} else {
                msg.waitreload=true;
				msg.todo = "logout";
				doThings(msg, sendResponse);
                logoutOverlay(msg);
			}
		} else {
            msg.waitreload=true;
			if (typeof msg.detail[msg.bigStep].logWith === "undefined") {
				msg.todo = "connect";
			} else {
				msg.todo = msg.detail[msg.bigStep].logWith;
			}
			doThings(msg, sendResponse);
            if(msg.todo == "logout"){
                logoutOverlay(msg);
            } else if(msg.todo == "connect"){
                loginOverlay(msg);
            }
		}
	} else if(msg.todo == "end"){
        endOverlay();
        /*if(isConnected(msg) == true){
            msg.type = "completed";
            sendResponse(msg);
        } else {
            msg.type = "error: not connected";
            sendResponse(msg);
        }*/
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

}