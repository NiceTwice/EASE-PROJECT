function getNewLogin(msg, i){
	if (msg.detail[i].user){
		return {"user":msg.detail[i].user.login, "password":msg.detail[i].user.password};
	} else if (msg.detail[i].logWith){
		return {"user":getNewLogin(msg, i-1).user, "logWith":getHost(msg.detail[i-1].website.loginUrl)};
	}
}

function rememberWebsite(website){
    if (website.lastLogin == "" || !website.lastLogin)
        return;
    extension.storage.get("visitedWebsites", function(visitedWebsites) {
        for (var i in visitedWebsites){
            if (visitedWebsites[i].name == website.name){
                if (visitedWebsites[i].lastLogin == website.lastLogin){
                    return ;
                }
                else {
                    visitedWebsites.splice(i, 1);
                    break;
                }
            }
        }
        if (typeof visitedWebsites === "undefined" || visitedWebsites == null || visitedWebsites == undefined || visitedWebsites.length == 0 || visitedWebsites.constructor !== Array)
            visitedWebsites = [];
        visitedWebsites.push(website);
        extension.storage.set("visitedWebsites", visitedWebsites);

    });
    //if(!website.loginUrl) website.loginUrl = website.home;
		if (!website.loginUrl)
			website.loginUrl = website.home;
    if(website.lastLogin.logWith){
        rememberDirectLogWithConnection(getHost(website.loginUrl), website.lastLogin);
    } else {
        rememberConnection(website.lastLogin.user, website.lastLogin.password, getHost(website.loginUrl), true);
    }
}

function endConnection(currentWindow, tab, msg, sendResponse){
    extension.tabs.sendMessage(tab, "rmOverlay", msg, function(response){
        if(msg.result == "Fail"){
            //server.post("Could not end process for website "+ msg.detail[0].website.name);
        }
    });
}

function checkFacebook(msg, callback){
     if(msg.detail[0].website.name == "Facebook" && msg.detail[1]){
        extension.storage.get("lastConnections", function(lastConnections){
            if(lastConnections != undefined) {
                if(lastConnections["www.facebook.com"] && lastConnections["www.facebook.com"].user==msg.detail[0].user.login) {
                    callback(msg.bigStep+1);
                    return
                } else {
                    callback(msg.bigStep);
                    return
                }
            } else {
                callback(msg.bigStep);
                return
            }
        });
    } else {
        callback(msg.bigStep);
        return
    }
}

extension.runtime.bckgrndOnMessage("NewConnection", function (msg, senderTab, sendResponse) {
  msg.todo = "checkAlreadyConnected";
  msg.bigStep = 0;
  checkFacebook(msg, function(newBigStep){
    msg.bigStep = newBigStep;
    msg.actionStep = 0;
    msg.waitreload= false;
    extension.currentWindow(function(currentWindow) {
				var home = msg.detail[msg.bigStep].website.home;
				if (typeof home == "object") {
					var tmpUrl = (home.http + msg.detail[0].user[home.subdomain] + "." + home.domain);
					msg.detail[msg.bigStep].website.home = tmpUrl;
				}
        extension.tabs.createOrUpdate(currentWindow, senderTab, msg.detail[msg.bigStep].website.home, msg.highlight, function(tab){
            extension.tabs.onUpdated(tab, function (newTab) {
                tab = newTab;
                extension.tabs.inject(tab, ["tools/extensionLight.js","overlay/overlay.css", "overlay/injectOverlay.js"], function(){});
            });
            extension.tabs.onMessage(tab, "reloaded", function (event, sendResponse1) {
                if(tab.url.indexOf("ease.space")==-1){
                console.log("-- Page reloaded --");
                    extension.tabs.inject(tab, ["tools/extension.js","jquery-3.1.0.js","contentScripts/actions.js", "contentScripts/connect.js"], function(){
                        extension.storage.get("visitedWebsites", function(visitedWebsites) {
                            extension.storage.get("lastConnections", function(lastConnections) {
                            msg.visitedWebsites = visitedWebsites;
                            if(lastConnections == undefined) lastConnections = {};
                            msg.lastConnections = lastConnections;
                            extension.tabs.sendMessage(tab, "goooo", msg, function(response){
                              if (response){
                                console.log("-- Status : "+response.type+" --");
                                if (response.type == "completed") {
                                    msg.waitreload = response.waitreload;
                                    msg.todo = response.todo;
                                    msg.bigStep = response.bigStep;
                                    msg.actionStep = response.actionStep;
                                    msg.detail[msg.bigStep].website.lastLogin = response.detail[msg.bigStep].website.lastLogin;
                                    if (msg.todo != "end" && msg.todo!="nextBigStep" && msg.actionStep < msg.detail[msg.bigStep].website[msg.todo].todo.length){
                                        //do nothing
                                    } else {
                                        if (msg.todo == "logout"){
                                            if (typeof msg.detail[msg.bigStep].logWith === "undefined") {
                                                msg.todo = "connect";
                                            } else {
                                                msg.todo = msg.detail[msg.bigStep].logWith;
                                            }
                                            msg.actionStep = 0;
                                        /*} else if(msg.todo == "end"){
                                            rememberWebsite(msg.detail[msg.bigStep].website);
                                            msg.todo = "checkAlreadyConnected";
                                            msg.actionStep = 0;
                                            msg.bigStep++;
                                            if (msg.bigStep < msg.detail.length){
                                                setTimeout(function () {
                                                    extension.tabs.update(tab, msg.detail[msg.bigStep].website.home, function() {});
                                                }, 1000);
                                            } else {
                                                msg.result = "Success";
                                                extension.tabs.onUpdatedRemoveListener(tab);
                                                extension.tabs.onMessageRemoveListener(tab);
                                                endConnection(currentWindow, tab, msg, sendResponse);
                                            }
                                        } else {
                                            msg.todo = "end";
                                            console.log("Check already log to end connection");
                                        }*/
                                        } else if (msg.todo=="nextBigStep"){
                                            msg.todo = "checkAlreadyConnected";
                                            extension.tabs.update(tab, msg.detail[msg.bigStep].website.home, function() {});
                                        } else {
                                             msg.detail[msg.bigStep].website.lastLogin = getNewLogin(msg, msg.bigStep);
                                            rememberWebsite(msg.detail[msg.bigStep].website);
                                            msg.actionStep = 0;
                                            msg.bigStep++;
                                            if (msg.bigStep < msg.detail.length){
                                                if(msg.waitreload){
                                                    msg.todo="nextBigStep";
                                                    console.log("-- Wait for nextBigStep --");
                                                } else {
                                                    msg.todo = "checkAlreadyConnected";
                                                    extension.tabs.update(tab, msg.detail[msg.bigStep].website.home, function() {});
                                                }
                                            } else {
                                                msg.todo = "checkAlreadyConnected";
                                                msg.result = "Success";
                                                endConnection(currentWindow, tab, msg, sendResponse);
                                                extension.tabs.onUpdatedRemoveListener(tab);
                                                extension.tabs.onMessageRemoveListener(tab);
                                            }
                                        }
                                    }
                                } else if (response != undefined){
                                    msg.result = "Fail";
                                    endConnection(currentWindow, tab, msg, sendResponse);
                                    extension.tabs.onUpdatedRemoveListener(tab);
                                    extension.tabs.onMessageRemoveListener(tab);
                                }
                              }
                              });
                            });
                            });
                        });
                    } else {
                        endConnection(currentWindow, tab, msg, sendResponse);
                    }
                });
            });
        });
    });
});
