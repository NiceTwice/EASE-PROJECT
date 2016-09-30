extension.runtime.bckgrndOnMessage("Logout", function (msg, sendResponse) {
    extension.storage.get("visitedWebsites", function(visitedWebsites) {        
        var finished = 0;
        for(var i in visitedWebsites){
            logOutFrom(visitedWebsites[i], sendResponse);
        }
        extension.storage.set("visitedWebsites", []);
                                            
    });
});

function logOutFrom(website, sendResponse){
            var msg = {};
            msg.detail = [];
            msg.detail.push({});
            msg.detail[0].website = website;
            msg.todo = "logout";
            msg.bigStep = 0;
            msg.actionStep = 0;
            extension.currentWindow(function(currentWindow) {
                if(msg.detail[0].website.logout.url){
                    msg.detail[0].website.home = msg.detail[0].website.logout.url;
                }
                extension.tabs.create(currentWindow, msg.detail[0].website.home, false, function(tab){
                    extension.tabs.onUpdated(tab, function (newTab) {
                        tab = newTab;
                        extension.tabs.inject(tab, ["tools/extensionLight.js","overlay/injectOverlay.js", "overlay/overlay.css"], function() {});
                    });
                    extension.tabs.onMessage(tab, "reloaded", function (event, sendResponse1) {
                        console.log("-- Page reloaded --");
                        extension.tabs.inject(tab, ["tools/extension.js","jquery-3.1.0.js","contentScripts/actions.js", "contentScripts/logout.js"], function() {
                                extension.tabs.sendMessage(tab, "logout", msg, function(response){
                                        console.log("-- Status : "+response.type+" --");
                                        if(response){
                                            if(response.type == "completed"){
                                                msg.actionStep = response.actionStep;
                                                if (msg.actionStep < msg.detail[0].website.logout.todo.length){
                                                    //do nothing
                                                } else {
                                                    console.log("-- Log out done --");
                                                    
                                                    extension.tabs.onUpdatedRemoveListener(tab);
                                                    extension.tabs.onMessageRemoveListener(tab);
                                                    setTimeout(function() {
                                                        extension.tabs.close(tab, function() {});
                                                    }, 1000);
                                                    sendResponse("Good");
                                                }
                                            } else {
                                                console.log("-- Log out fail --");
                                                extension.tabs.onMessageRemoveListener(tab);
                                                extension.tabs.onUpdatedRemoveListener(tab);
                                                setTimeout(function() {
                                                    extension.tabs.close(tab, function() {});
                                                }, 1000);
                                                sendResponse(response);
                                            }
                                        }
                                    });
                            });
                    });
                });
            });
}