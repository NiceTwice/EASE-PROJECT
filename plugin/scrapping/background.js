chrome.identity.getProfileUserInfo(function(userInfo){console.log(userInfo);});

function testOverlay(){
    extension.currentWindow(function(window){
        extension.tabs.create(window, "http://www.linkedin.com", true, function(tab){
            extension.tabs.onUpdated(tab, function (newTab) {
                tab = newTab;
                extension.tabs.sendMessage(tab, "scrapOverlay", "Facebook", function(){});
            });
        });
    });
}


function startScrapFacebook(login, password){
    extension.currentWindow(function(window){
        extension.tabs.create(window, "https://www.facebook.com", false, function(tab){
            extension.tabs.onUpdated(tab, function (newTab) {
                tab = newTab;
                extension.tabs.sendMessage(tab, "scrapOverlay", "Facebook", function(){});
            });
            extension.tabs.onMessage(tab, "scrapReloaded", function (event, sendResponse1) {
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
            extension.tabs.onMessage(tab, "scrapReloaded", function (event, sendResponse1) {
                extension.tabs.onMessageRemoveListener(tab);
                callback(tab);
            });
        });
    });
}

function connectToFb(tab, login, pass, callback){
    extension.tabs.sendMessage(tab, "connectToFb", {login:login, pass:pass}, function(response){
        extension.tabs.onMessage(tab, "scrapReloaded", function (event, sendResponse1) {
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
        extension.tabs.onMessage(tab, "scrapReloaded", function (event, sendResponse1) {
            extension.tabs.onMessageRemoveListener(tab);
            extension.tabs.sendMessage(tab, "scrapFb", {}, function(response){
                console.log(response);
            });
        });
    });
}

////////////////////////////////////////////////////////////////////////////////////////////////////

function startScrapChrome(login, password){
    extension.currentWindow(function(window){
        extension.tabs.create(window, "https://accounts.google.com/Logout", false, function(tab){
             extension.tabs.onUpdated(tab, function (newTab) {
                tab = newTab;
                 extension.tabs.sendMessage(tab, "scrapOverlay", "Chrome", function(){});
            });
            extension.tabs.onMessage(tab, "scrapReloaded", function (event, sendResponse1) {
                extension.tabs.onMessageRemoveListener(tab);
                extension.tabs.update(tab, "https://accounts.google.com/ServiceLogin?sacu=1#identifier", function(tab){
                    extension.tabs.onMessage(tab, "scrapReloaded", function (event, sendResponse1) {
                        extension.tabs.onMessageRemoveListener(tab);
                        extension.tabs.sendMessage(tab, "connectToChrome", {login:login, pass:password}, function(response){
                            if(response==false){
                                console.log("cant connect to this chrome account");
                            } else {
                                extension.tabs.onMessage(tab, "scrapReloaded", function (event, sendResponse1) {
                                    extension.tabs.onMessageRemoveListener(tab);
                                    extension.tabs.sendMessage(tab, "checkChromeCo", {}, function(isConnected){
                                        if(!isConnected){
                                            console.log("cant connect to this chrome account");
                                        } else {
                                            extension.tabs.update(tab, "https://passwords.google.com/", function(tab){
                                                extension.tabs.onMessage(tab, "scrapReloaded", function (event, sendResponse1) {
                                                    extension.tabs.onMessageRemoveListener(tab);
                                                    extension.tabs.sendMessage(tab, "typePasswordChrome", {pass:password}, function(response){
                                                        extension.tabs.onMessage(tab, "scrapReloaded", function (event, sendResponse1) {
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

////////////////////////////////////////////////////////////////////////////////

function startScrapLinkedin(login, password){
    extension.currentWindow(function(window){
        extension.tabs.create(window, "https://www.linkedin.com/psettings/third-party-applications", false, function(tab){
             extension.tabs.onUpdated(tab, function (newTab) {
                tab = newTab;
                extension.tabs.sendMessage(tab, "scrapOverlay", "Linkedin", function(){});
            });
            extension.tabs.onMessage(tab, "scrapReloaded", function (event, sendResponse1) {
                extension.tabs.onMessageRemoveListener(tab);
                extension.tabs.sendMessage(tab, "checkLnkdnCo", {}, function(response){
                    if(response==true){
                        logoutFromLnkdn(tab, function(tab){
                            connectToLnkdn(tab,login, password, scrapLnkdn);
                        });
                    } else {
                        connectToLnkdn(tab, login, password,scrapLnkdn);
                    }
                });
            });
        });
    });
}

function logoutFromLnkdn(tab, callback){
    extension.tabs.sendMessage(tab, "logoutFromLnkdn", {}, function(response2){
        extension.tabs.onMessage(tab, "scrapReloaded", function (event, sendResponse1) {
            extension.tabs.onMessageRemoveListener(tab);
            extension.tabs.update(tab, "https://www.linkedin.com/psettings/third-party-applications", function(tab){
                extension.tabs.onMessage(tab, "scrapReloaded", function (event, sendResponse1) {
                    extension.tabs.onMessageRemoveListener(tab);
                        callback(tab);
                });
            });
        });
    });
}

function connectToLnkdn(tab, login, pass, callback){
    extension.tabs.sendMessage(tab, "connectToLnkdn", {login:login, pass:pass}, function(response){
        extension.tabs.onMessage(tab, "scrapReloaded", function (event, sendResponse1) {
            extension.tabs.onMessageRemoveListener(tab);
            extension.tabs.sendMessage(tab, "checkLnkdnCo", {}, function(response){
                if(response == false){
                    console.log("error : wrong lnkdn login or password");
                } else {
                    callback(tab);
                }
            });
        });
    });
}

function scrapLnkdn(tab){
    extension.tabs.sendMessage(tab, "scrapLnkdn", {}, function(response){
        console.log(response);
    });
}