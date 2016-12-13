function startScrapFacebook(login, password){
    extension.currentWindow(function(window){
        extension.tabs.create(window, "https://www.facebook.com", false, function(tab){
             extension.tabs.onUpdated(tab, function (newTab) {
                tab = newTab;
                extension.tabs.inject(tab, ["tools/extensionLight.js","overlay/overlay.css", "overlay/injectOverlay.js"], function(){});
            });
            extension.tabs.onMessage(tab, "reloaded", function (event, sendResponse1) {
            //extension.tabs.onUpdated(tab, function (tab) {
                extension.tabs.onMessageRemoveListener(tab);
                extension.tabs.sendMessage(tab, "checkFbCo", {}, function(response){
                    if(response==true){
                        logoutFromFb(tab, function(tab){
                            connectToFb(tab,login, password, scrapFb);
                        });
                    } else {
                        connectToFb(tab, login, password,scrapFb);
                    }
                });
            });
        });
    });
}

function logoutFromFb(tab, callback){
    extension.tabs.sendMessage(tab, "logoutFromFb", {}, function(response2){
        extension.tabs.update(tab, "https://www.facebook.com", function(tab){
            extension.tabs.onMessage(tab, "reloaded", function (event, sendResponse1) {
                extension.tabs.onMessageRemoveListener(tab);
                callback(tab);
            });
        });
    });
}

function connectToFb(tab, login, pass, callback){
    extension.tabs.sendMessage(tab, "connectToFb", {login:login, pass:pass}, function(response){
        extension.tabs.onMessage(tab, "reloaded", function (event, sendResponse1) {
            extension.tabs.onMessageRemoveListener(tab);
            extension.tabs.sendMessage(tab, "checkFbCo", {}, function(response){
                if(response == false){
                    console.log("error : wrong fb login or password");
                } else {
                    callback(tab);
                }
            });
        });
    });
}

function scrapFb(tab){
    extension.tabs.update(tab, "https://www.facebook.com/settings?tab=applications", function(tab){
        extension.tabs.onMessage(tab, "reloaded", function (event, sendResponse1) {
            extension.tabs.onMessageRemoveListener(tab);
            extension.tabs.sendMessage(tab, "scrapFb", {}, function(response){
                console.log(response);
            });
        });
    });
}




function startScrapChrome(login, password){
    extension.currentWindow(function(window){
        extension.tabs.create(window, "https://accounts.google.com/Logout", false, function(tab){
             extension.tabs.onUpdated(tab, function (newTab) {
                tab = newTab;
                extension.tabs.inject(tab, ["tools/extensionLight.js","overlay/overlay.css", "overlay/injectOverlay.js"], function(){});
            });
            extension.tabs.onMessage(tab, "reloaded", function (event, sendResponse1) {
                extension.tabs.onMessageRemoveListener(tab);
                extension.tabs.update(tab, "https://accounts.google.com/ServiceLogin?sacu=1#identifier", function(tab){
                    extension.tabs.onMessage(tab, "reloaded", function (event, sendResponse1) {
                        extension.tabs.onMessageRemoveListener(tab);
                        extension.tabs.sendMessage(tab, "connectToChrome", {login:login, pass:password}, function(response){
                            if(response==false){
                                console.log("cant connect to this chrome account");
                            } else {
                                extension.tabs.onMessage(tab, "reloaded", function (event, sendResponse1) {
                                    extension.tabs.onMessageRemoveListener(tab);
                                    extension.tabs.sendMessage(tab, "checkChromeCo", {}, function(isConnected){
                                        if(!isConnected){
                                            console.log("cant connect to this chrome account");
                                        } else {
                                            extension.tabs.update(tab, "https://passwords.google.com/", function(tab){
                                                extension.tabs.onMessage(tab, "reloaded", function (event, sendResponse1) {
                                                    extension.tabs.onMessageRemoveListener(tab);
                                                    extension.tabs.sendMessage(tab, "typePasswordChrome", {pass:password}, function(response){
                                                        extension.tabs.onMessage(tab, "reloaded", function (event, sendResponse1) {
                                                            extension.tabs.onMessageRemoveListener(tab);
                                                                extension.tabs.sendMessage(tab, "scrapChrome", {}, function(response){
                                                                    console.log(response);
                                                                });
                                                            });
                                                        });
                                                    });
                                                });
                                            }
                                    });
                                });
                            }
                        });
                    });
                });
            });
        });
    });
}