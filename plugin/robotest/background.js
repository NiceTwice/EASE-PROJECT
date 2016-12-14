var results = {};
var timeout = 17000;

extension.runtime.bckgrndOnMessage("getPopupContent", function(msg, senderTab, sendResponse){
    sendResponse(results);
});

extension.runtime.bckgrndOnMessage("TestConnection", function(msg, senderTab, sendResponse){
    singleTest(msg);
});

function singleTest(msg){
    var resultRow = msg.detail[msg.detail.length-1].website.name;
    if (typeof msg.detail[msg.detail.length-1].logWith === "undefined") {
        resultRow += "-login-"+msg.detail[msg.detail.length-1].user.login;
    } else {
        resultRow += "-loginwth-"+ msg.detail[msg.detail.length-1].logWith;
    }
    msg.resultRow = resultRow;
    results[msg.resultRow]="> "+msg.detail[msg.detail.length-1].website.name+" : Initialize test";
    sendResults();
    connectTo(msg, null, function(tab, status){
        if(status=="end")
            extension.tabs.close(tab, function(){});
        else {
            setTimeout(function(){
                if(msg[status]=="fail")
                    extension.tabs.close(tab, function(){});
            },2*timeout);
        }
    });
}

extension.runtime.bckgrndOnMessage("TestMultipleConnections", function(msg, senderTab, sendResponse){
    results = {};
    multipleTests(msg, null, 0);
});

function multipleTests(msg, tab, i){
    var websiteMsg = {};
    if(i<msg.detail.length){
        websiteMsg.detail = msg.detail[i];
        var resultRow = websiteMsg.detail[websiteMsg.detail.length-1].website.name;
        if (typeof websiteMsg.detail[websiteMsg.detail.length-1].logWith === "undefined") {
            resultRow += "-login-"+websiteMsg.detail[websiteMsg.detail.length-1].user.login;
        } else {
            resultRow += "-loginwth-"+ websiteMsg.detail[websiteMsg.detail.length-1].logWith;
        }
        websiteMsg.resultRow = resultRow;
         results[resultRow]="> "+websiteMsg.detail[websiteMsg.detail.length-1].website.name+" : Initialize test";
        sendResults();
        connectTo(websiteMsg, tab, function(tab, status){
            if(status!="end" && websiteMsg[status]!="success"){
                singleTest(websiteMsg);
            } 
            multipleTests(msg, tab, i+1);
        });
    } else {
        //TODO
    }
   
}

function connectTo(msg, currentTab, callback){
    results[msg.resultRow]="> "+msg.detail[msg.detail.length-1].website.name+" : TESTING connection";
    sendResults();
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
            extension.tabs.onUpdatedRemoveListener(currentTab);
            extension.tabs.onMessageRemoveListener(currentTab);
            extension.tabs.update(currentTab, msg.detail[0].website.home, connection);
        }
       
        function connection(tab){
            extension.tabs.onUpdated(tab, function (newTab) {
                tab = newTab;
                extension.tabs.inject(tab, ["tools/extensionLight.js","overlay/overlay.css", "overlay/injectOverlay.js"], function(){});
            });
            extension.tabs.onMessage(tab, "reloaded", function (event, sendResponse1) {
                if(waitBeforeNext){
                    nextDone = true;
                    checkConnectionStatus(msg, tab, currentWindow,true, "reconnect", callback);
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
                                            waitBeforeNext = true;
                                            setTimeout(function(){
                                                if(!nextDone){
                                                    nextDone = true;
                                                    checkConnectionStatus(msg, tab, currentWindow, true,"reconnect", callback);
                                                }
                                            },timeout);
                                        }
                                    }
                                }
                            } else if (response != undefined){
                                printConsole(false, "CONNECTION", msg); 
                                msg.connectionstatus = "fail";
                                callback(tab, "connectionstatus");
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

function reconnect(msg, oldTab, currentWindow, callback){
    extension.tabs.onUpdatedRemoveListener(oldTab);
    extension.tabs.onMessageRemoveListener(oldTab);
     results[msg.resultRow]="> "+msg.detail[msg.detail.length-1].website.name+" : TESTING reconnection";
    sendResults();
    msg.todo = "checkAlreadyLogged";
    msg.bigStep = msg.detail.length - 1;
    msg.actionStep = 0;
    msg.waitreload= false;
    var waitBeforeNext = false;
    var nextDone = false;
    extension.tabs.update(oldTab, msg.detail[msg.bigStep].website.home, function(tab){
    extension.tabs.onUpdated(tab, function (newTab) {
        tab = newTab;
        extension.tabs.inject(tab, ["tools/extensionLight.js","overlay/overlay.css", "overlay/injectOverlay.js"], function(){});
    });
    extension.tabs.onMessage(tab, "reloaded", function (event, sendResponse1) {
        if(waitBeforeNext){
            nextDone = true;
            checkConnectionStatus(msg, tab, currentWindow, true, "logout", callback);
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
                                            waitBeforeNext = true;
                                            setTimeout(function(){
                                                if(!nextDone){
                                                    nextDone = true;
                                                    checkConnectionStatus(msg, tab, currentWindow, true,"logout", callback);
                                                }
                                            },timeout);
                                        }
                                    }
                                }
                            } else if (response != undefined){
                                printConsole(false, "RECONNECTION", msg);
                                msg.reconnectionstatus = "fail";
                                callback(tab, "reconnectionstatus");
                                //extension.tabs.close(tab, function(){});
                                extension.tabs.onUpdatedRemoveListener(tab);
                                extension.tabs.onMessageRemoveListener(tab);
                            }
                        }
                    });
                });
                }
        });
    });
}

function logoutFrom(msg, oldTab, currentWindow, callback) {
    extension.tabs.onUpdatedRemoveListener(oldTab);
    extension.tabs.onMessageRemoveListener(oldTab);
     results[msg.resultRow]="> "+msg.detail[msg.detail.length-1].website.name+" : TESTING logout";
    sendResults();
    msg.todo = "logout";
    msg.bigStep = msg.detail.length-1;
    msg.actionStep = 0;
    msg.waitreload= false;
    var waitBeforeNext = false;
    var nextDone = false;
         extension.tabs.update(oldTab, msg.detail[msg.bigStep].website.home, function(tab){
            extension.tabs.onUpdated(tab, function (newTab) {
                tab = newTab;
                extension.tabs.inject(tab, ["tools/extensionLight.js","overlay/overlay.css", "overlay/injectOverlay.js"], function(){});
            });
            extension.tabs.onMessage(tab, "reloaded", function (event, sendResponse1) {
                if(waitBeforeNext){
                    nextDone = true;
                    checkConnectionStatus(msg, tab, currentWindow, false,"end", callback);
                } else {
                    extension.tabs.inject(tab, ["tools/extension.js","jquery-3.1.0.js","contentScripts/actions.js"], function(){
                            extension.tabs.sendMessage(tab, "logout", msg, function(response){
                            if(response){
                                if(response.type == "completed"){
                                    msg.actionStep = response.actionStep;
                                    if (msg.actionStep < msg.detail[msg.bigStep].website.logout.todo.length){
                                        //do nothing
                                    } else {
                                        msg.todo = "checkAlreadyLogged";
                                        waitBeforeNext = true;
                                        setTimeout(function(){
                                            if(!nextDone){
                                                nextDone = true;
                                                checkConnectionStatus(msg, tab, currentWindow, false,"end", callback);
                                            }
                                        },timeout);
                                    }
                                } else if (response != undefined){
                                    printConsole(false, "LOGOUT", msg);
                                    msg.logoutstatus = "fail";
                                    callback(tab, "logoustatus");
                                    extension.tabs.onMessageRemoveListener(tab);
                                    extension.tabs.onUpdatedRemoveListener(tab);
                                    //extension.tabs.close(tab, function() {});
                                }
                            }
                        });
                    });
                }
                });
            });   
}

function checkConnectionStatus(msg, tab, currentWindow, checkConnected, nextAction, callback){
    msg.todo = "checkAlreadyLogged";
    msg.bigStep = msg.detail.length-1;
    msg.actionStep = 0;
    msg.waitreload= false;
    msg.checkConnected = checkConnected;
    extension.tabs.inject(tab, ["tools/extensionLight.js","overlay/overlay.css", "overlay/injectOverlay.js", "tools/extension.js","jquery-3.1.0.js","contentScripts/actions.js"], function(){
        extension.tabs.sendMessage(tab, "checkConnectionStatus", msg, function(response){
                if(response){
                    if(response.type == "completed"){
                        extension.tabs.onUpdatedRemoveListener(tab);
                        extension.tabs.onMessageRemoveListener(tab);
                        if(nextAction=="logout"){
                            msg.reconnectionstatus = "success";
                            logoutFrom(msg, tab, currentWindow, callback);
                        } else if(nextAction=="end"){
                            msg.logoutstatus = "success";
                            printConsole(true, "", msg);
                            callback(tab, "end");
                            //extension.tabs.close(tab, function() {});
                        } else if(nextAction=="reconnect"){
                             msg.connectionstatus = "success";
                            reconnect(msg, tab, currentWindow, callback);
                        }
                    } else if(response.type == "fail") {
                        if(nextAction=="logout"){
                            msg.reconnectionstatus = "fail";
                            printConsole(false, "RECONNECTION", msg);
                            callback(tab, "reconnectionstatus");
                        }
                        else if(nextAction=="end"){
                            printConsole(false, "LOGOUT", msg);
                            msg.logoutstatus = "fail";
                            callback(tab, "logoutstatus");
                        } else if(nextAction=="reconnect"){
                            printConsole(false, "CONNECTION", msg);
                            msg.connectionstatus = "fail";
                            callback(tab, "connectionstatus");
                        }
                        //extension.tabs.close(tab, function() {});
                    } else if (response != undefined){
                        printConsole(false, "UNKOWN ERROR", msg);
                        callback(tab, "unkown");
                        msg.unkown = "fail";
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
        var connectionType = "connection with "+ msg.detail[msg.detail.length-1].logWith;
    }
    if(success){
        results[msg.resultRow]="> "+website + " : SUCCESS connection, logout and reconnection for "+connectionType;
    } else {
        if(type != "logout")
            results[msg.resultRow]="> "+website + " : FAIL "+type+" for "+connectionType ;
        else
            results[msg.resultRow]="> "+website + " : FAIL "+type;
    }
    sendResults();
}

function sendResults(){
    chrome.windows.getAll({"populate":true}, function(windows){
        for (var i in windows){
            var window = windows[i];
            for(var j in window.tabs){
                var tab = window.tabs[j];
                if(tab.url.indexOf("https://ease.space")==0){
                     extension.tabs.sendMessage(tab, "printResults", results, function(){});
                }
            }
        }
    });
   
}