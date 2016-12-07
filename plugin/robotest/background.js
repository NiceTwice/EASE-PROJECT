var results = {};

extension.runtime.bckgrndOnMessage("getPopupContent", function(msg, senderTab, sendResponse){
    sendResponse(results);
});

extension.runtime.bckgrndOnMessage("TestConnection", function(msg, senderTab, sendResponse){
    results[msg.detail[msg.detail.length-1].website.name]="*** Testing website "+msg.detail[msg.detail.length-1].website.name+" ***";
    connectTo(msg, null, function(){
        //TODO
    });
});

extension.runtime.bckgrndOnMessage("TestMultipleConnections", function(msg, senderTab, sendResponse){
    multipleTests(msg, null, 0);
});

function multipleTests(msg, tab, i){
    var websiteMsg = {};
    if(i<msg.detail.length){
        websiteMsg.detail = msg.detail[i];
         results[websiteMsg.detail[websiteMsg.detail.length-1].website.name]="*** Testing website "+websiteMsg.detail[websiteMsg.detail.length-1].website.name+" ***";
        connectTo(websiteMsg, tab, function(tab){
            multipleTests(msg, tab, i+1);
        });
    } else {
        //TODO
    }
   
}

function connectTo(msg, currentTab, callback){
    msg.todo = "checkAlreadyLogged";
    msg.bigStep = 0;
    msg.actionStep = 0;
    msg.waitreload= false;
    var waitBeforeNext = false;
    var nextDone = false;
    extension.currentWindow(function(currentWindow) {
        if(currentTab == null){
            extension.tabs.create(currentWindow, msg.detail[0].website.home, false, connection);
        } else {
            extension.tabs.update(currentTab, msg.detail[0].website.home, connection);
        }
        function connection(tab){
            extension.tabs.onUpdated(tab, function (newTab) {
                tab = newTab;
                extension.tabs.inject(tab, ["tools/extensionLight.js","overlay/overlay.css", "overlay/injectOverlay.js"], function(){});
            });
            extension.tabs.onMessage(tab, "reloaded", function (event, sendResponse1) {
                if(waitBeforeNext){
                    if(!nextDone){
                        nextDone = true;
                        checkConnected(msg, tab, currentWindow, "logout", callback);
                        extension.tabs.onUpdatedRemoveListener(tab);
                        extension.tabs.onMessageRemoveListener(tab);
                    }
                } else {
                extension.tabs.inject(tab, ["tools/extension.js","jquery-3.1.0.js","contentScripts/actions.js"], function(){
                    extension.tabs.sendMessage(tab, "goooo", msg, function(response){
                        if (response){
                            if (response.type == "completed") {
                                msg.waitreload = response.waitreload;
                                msg.todo = response.todo;
                                msg.bigStep = response.bigStep;
                                msg.actionStep = response.actionStep;
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
                                    } else if (msg.todo=="nextBigStep"){
                                        msg.todo = "checkAlreadyLogged";
                                        extension.tabs.update(tab, msg.detail[msg.bigStep].website.home, function(){});
                                    } else {
                                        msg.actionStep = 0;
                                        msg.bigStep++;
                                        if (msg.bigStep < msg.detail.length){
                                            if(msg.waitreload){
                                                msg.todo="nextBigStep";
                                            } else {
                                                msg.todo = "checkAlreadyLogged";
                                                extension.tabs.update(tab, msg.detail[msg.bigStep].website.home, function(){});
                                            }
                                        } else {
                                            msg.todo = "checkAlreadyLogged";
                                            msg.result = "Success";
                                            waitBeforeNext = true;
                                            setTimeout(function(){
                                                if(!nextDone){
                                                    nextDone = false;
                                                    checkConnected(msg, tab, currentWindow, "logout", callback);
                                                    extension.tabs.onUpdatedRemoveListener(tab);
                                                    extension.tabs.onMessageRemoveListener(tab);
                                                }
                                            },20000);
                                        }
                                    }
                                }
                            } else if (response != undefined){
                                printConsole(false, "connection", msg);
                                callback(tab);
                                msg.result = "Fail";
                                //extension.tabs.close(tab, function(){});
                                extension.tabs.onUpdatedRemoveListener(tab);
                                extension.tabs.onMessageRemoveListener(tab);
                            }
                        }
                    });
                });
                }
            });
        }
    });   
}

function logoutFrom(msg, oldTab, currentWindow, callback) {
    msg.todo = "logout";
    msg.bigStep = msg.detail.length-1;
    msg.actionStep = 0;
    msg.waitreload= false;
         extension.tabs.update(oldTab, msg.detail[msg.bigStep].website.home, function(tab){
            extension.tabs.onUpdated(tab, function (newTab) {
                tab = newTab;
                extension.tabs.inject(tab, ["tools/extensionLight.js","overlay/overlay.css", "overlay/injectOverlay.js"], function(){});
            });
            extension.tabs.onMessage(tab, "reloaded", function (event, sendResponse1) {
                    extension.tabs.inject(tab, ["tools/extension.js","jquery-3.1.0.js","contentScripts/actions.js"], function(){
                            extension.tabs.sendMessage(tab, "logout", msg, function(response){
                            if(response){
                                if(response.type == "completed"){
                                    msg.actionStep = response.actionStep;
                                    if (msg.actionStep < msg.detail[msg.bigStep].website.logout.todo.length){
                                        //do nothing
                                    } else {
                                        extension.tabs.onUpdatedRemoveListener(tab);
                                        extension.tabs.onMessageRemoveListener(tab);
                                        reconnect(msg, tab, currentWindow, callback);
                                    }
                                } else if (response != undefined){
                                    printConsole(false, "logout", msg);
                                    callback(tab);
                                    extension.tabs.onMessageRemoveListener(tab);
                                    extension.tabs.onUpdatedRemoveListener(tab);
                                    //extension.tabs.close(tab, function() {});
                                }
                            }
                        });
                    });
                });
            });   
}

function reconnect(msg, tab, currentWindow, callback){
    msg.todo = "checkAlreadyLogged";
    msg.bigStep = msg.detail.length - 1;
    msg.actionStep = 0;
    msg.waitreload= false;
    var waitBeforeNext = false;
    var nextDone = false;
    extension.tabs.onUpdated(tab, function (newTab) {
        tab = newTab;
        extension.tabs.inject(tab, ["tools/extensionLight.js","overlay/overlay.css", "overlay/injectOverlay.js"], function(){});
    });
    extension.tabs.onMessage(tab, "reloaded", function (event, sendResponse1) {
        if(waitBeforeNext){
            if(!nextDone){
                nextDone = true;
                checkConnected(msg, tab, currentWindow, "end", callback);
                extension.tabs.onUpdatedRemoveListener(tab);
                extension.tabs.onMessageRemoveListener(tab);
            }
        } else {
        extension.tabs.inject(tab, ["tools/extension.js","jquery-3.1.0.js","contentScripts/actions.js"], function(){
                extension.tabs.sendMessage(tab, "reconnect", msg, function(response){
                        if (response){
                            if (response.type == "completed") {
                                msg.waitreload = response.waitreload;
                                msg.todo = response.todo;
                                msg.bigStep = response.bigStep;
                                msg.actionStep = response.actionStep;
                                if (msg.todo != "end" && msg.todo!="nextBigStep" && msg.actionStep < msg.detail[msg.bigStep].website[msg.todo].todo.length){
                                    //do nothing
                                } else {
                                    if (msg.todo=="nextBigStep"){
                                        msg.todo = "checkAlreadyLogged";
                                        extension.tabs.update(tab, msg.detail[msg.bigStep].website.home, function(){});
                                    } else {
                                        msg.actionStep = 0;
                                        msg.bigStep++;
                                        if (msg.bigStep < msg.detail.length){
                                            if(msg.waitreload){
                                                msg.todo="nextBigStep";
                                            } else {
                                                msg.todo = "checkAlreadyLogged";
                                                extension.tabs.update(tab, msg.detail[msg.bigStep].website.home, function(){});
                                            }
                                        } else {
                                            msg.todo = "checkAlreadyLogged";
                                            msg.result = "Success";
                                            waitBeforeNext = true;
                                            setTimeout(function(){
                                                if(!nextDone){
                                                    nextDone = true;
                                                    checkConnected(msg, tab, currentWindow, "end", callback);
                                                    extension.tabs.onUpdatedRemoveListener(tab);
                                                    extension.tabs.onMessageRemoveListener(tab);
                                                }
                                            }, 20000);
                                        }
                                    }
                                }
                            } else if(response.type == "logout fail") {
                                        printConsole(false, "logout", msg);
                                        callback(tab);
                                        extension.tabs.onMessageRemoveListener(tab);
                                        extension.tabs.onUpdatedRemoveListener(tab);
                                        //extension.tabs.close(tab, function() {});
                            } else if (response != undefined){
                                        printConsole(false, "reconnection", msg);
                                        callback(tab);
                                        extension.tabs.onMessageRemoveListener(tab);
                                        extension.tabs.onUpdatedRemoveListener(tab);
                                        //extension.tabs.close(tab, function() {});
                            }
                        } 
                            });
                        });
                }
        });  
}

function checkConnected(msg, tab, currentWindow, nextAction, callback){
    msg.todo = "checkAlreadyLogged";
    msg.bigStep = msg.detail.length-1;
    msg.actionStep = 0;
    msg.waitreload= false;
    extension.tabs.inject(tab, ["tools/extensionLight.js","overlay/overlay.css", "overlay/injectOverlay.js", "tools/extension.js","jquery-3.1.0.js","contentScripts/actions.js"], function(){
        extension.tabs.sendMessage(tab, "checkConnected", msg, function(response){
                if(response){
                    if(response.type == "completed"){
                        extension.tabs.onUpdatedRemoveListener(tab);
                        extension.tabs.onMessageRemoveListener(tab);
                        if(nextAction=="logout")
                            logoutFrom(msg, tab, currentWindow, callback);
                        else if(nextAction=="end"){
                            printConsole(true, "", msg);
                            callback(tab);
                            //extension.tabs.close(tab, function() {});
                        }
                    } else if(response.type == "fail") {
                        if(nextAction=="logout"){
                            printConsole(false, "connection", msg);
                            callback(tab);
                        }
                        else if(nextAction="end"){
                            printConsole(false, "reconnection", msg);
                            callback(tab);
                        }
                        extension.tabs.onMessageRemoveListener(tab);
                        extension.tabs.onUpdatedRemoveListener(tab);
                        //extension.tabs.close(tab, function() {});
                    } else if (response != undefined){
                        printConsole(false, "unknown error", msg);
                        callback(tab);
                        extension.tabs.onMessageRemoveListener(tab);
                        extension.tabs.onUpdatedRemoveListener(tab);
                        //extension.tabs.close(tab, function() {});
                    }
                }
        });
    });
  
}

function printConsole(success, type, msg){
    var website = msg.detail[msg.detail.length-1].website.name;
    if (typeof msg.detail[msg.detail.length-1].logWith === "undefined") {
        var connectionType = "classic connection (login : "+msg.detail[msg.detail.length-1].user.login+")";
    } else {
        var connectionType = "connection with website "+ msg.detail[msg.detail.length-1].logWith;
    }
    if(success){
        results[website]="> "+website + " : SUCCESS connection, logout and reconnection for "+connectionType;
    } else {
        if(type != "logout")
            results[website]="> "+website + " : FAIL "+type+" for "+connectionType ;
        else
            results[website]="> "+website + " : FAIL "+type;
    }
}
