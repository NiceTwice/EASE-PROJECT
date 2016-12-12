var listenersUpdates = [];
var listenersMessages = [];

var extension = {
	storage:{
		get:function(key, callback){
			chrome.storage.local.get(key, function(res){
                if(res[key]== undefined) var ans = {};
                else var ans = res[key];
                callback(ans);
            });
		},
		set:function(key, value, callback){
			var obj = {};
			obj[key] = value;
			chrome.storage.local.set(obj, callback);
		}
	},
    currentWindow:function(callback){
        chrome.windows.getCurrent({"populate":true}, callback)
    },
    hasMultipleEaseTabs:function(window){
        var nb=0;
        for(var i in window.tabs) {
            if(window.tabs[i].url.indexOf("ease.space")!=-1){
                nb++;
            }
        }
        return nb>1;
    },
	addShortCut:function(fct){
		chrome.commands.onCommand.addListener(fct);
	},
	onWindowCreated:function(fct){
		chrome.windows.onCreated.addListener(fct);
	},
	onWindowRemoved:function(fct){
		chrome.windows.onRemoved.addListener(fct);
	},
    onNewWindow:function(fct){
            chrome.windows.onCreated.addListener(function(window){
               chrome.tabs.onCreated.addListener(function newWindow(tab){
                if(tab.windowId == window.id){
                chrome.tabs.onCreated.removeListener(newWindow);
                chrome.tabs.onUpdated.addListener(function newtab(tabId, params, newTab){
                    if(tabId == tab.id){
                        chrome.tabs.onUpdated.removeListener(newtab);
                        if(params.url=="chrome://newtab/"){
                            fct(newTab);
                        }
                    }                    
                });
                }
            }); 
        });
    },
	runtime:{
		sendMessage:function(name, msg, callback){
			chrome.runtime.sendMessage({"name":name, "message":msg}, callback);
		},
		onMessage:function(name, fct){
			chrome.runtime.onMessage.addListener(function(event, sender, sendResponse){
				if(event.name == name){
					fct(event.message, sendResponse);
                    return true;
				}
			});
		},
        bckgrndOnMessage:function(name, fct){
            chrome.runtime.onMessage.addListener(function(event, sender, sendResponse){
                if(event.name == name){
					fct(event.message, sender.tab, sendResponse);
                    return true;
				}
			});
        },
        tempBckgrndOnMessage:function(name, fct){
            chrome.runtime.onMessage.addListener(function temp(event, sender, sendResponse){
                if(event.name == name){
					fct(event.message, sender.tab, sendResponse);
                    chrome.runtime.onMessage.removeListener(temp);
                    return true;
				}
			});
        }
    },
	tabs:{
        stopLoad:function(tab, callback){
            chrome.tabs.executeScript(tab.id, {
                code: "window.stop();",
                runAt: "document_start"
            }, callback);
        },
		update:function(tab, url, callback){
			chrome.tabs.update(tab.id, {"url":url}, callback);
		},
		create:function(window, url, active, callback){
			chrome.tabs.create({"windowId":window.id, "url":url, "active":active}, callback);
		},
        createOrUpdate:function(window, tab, url, active, callback){
            if(active){
                if(extension.hasMultipleEaseTabs(window)){
                    this.update(tab, url, callback);
                } else {
                    this.create(window, url, active, callback);
                }
            } else {
                this.create(window, url, active, callback);
            }
        },
        highlight:function(window, tab, callback){
            chrome.tabs.highlight({"windowId":window.id, "tabs":tab.index}, callback);
        },
        focus:function(window, tab, callback){
            chrome.tabs.highlight({"windowId":window.id, "tabs":tab.index}, callback);
        },
		close:function(tab, callback){
   			chrome.tabs.remove(tab.id, callback);
		},
		onUpdated:function(tab, fct){
			listenersUpdates[tab.id] = function (tabId, params, newTab){
				if(tab.id==tabId){
				    fct(newTab);
				}
			}
			chrome.tabs.onUpdated.addListener(listenersUpdates[tab.id]);
		},
        onNewTab:function(fct){
            chrome.tabs.onCreated.addListener(function(tab){
                chrome.tabs.onUpdated.addListener(function newtab(tabId, params, newTab){
                    if(tabId == tab.id){
                        chrome.tabs.onUpdated.removeListener(newtab);
                        if(params.url=="chrome://newtab/"){
                            fct(newTab);
                        }
                    }                    
                });
            });
        },
        onUpdatedRemoveListener:function(tab){
            chrome.tabs.onUpdated.removeListener(listenersUpdates[tab.id]);
        },
        onNavigation:function(fct){
            chrome.windows.onFocusChanged.addListener(function (windowId){
                if(windowId != -1) chrome.windows.get(windowId, {"populate":true}, function(window){
                    for(var i in window.tabs){
                        if(window.tabs[i].active && window.tabs[i].url){
                           fct(window.tabs[i].url);
                        }
                    }
                });
            });

            chrome.tabs.onActivated.addListener(function (infos){
                chrome.tabs.get(infos.tabId, function(activatedTab){
                    if(activatedTab.url){
                       fct(activatedTab.url);
                    } else {
                        chrome.tabs.onUpdated.addListener(function tabIsReady(tabId, params, tab){
                            if(tabId == infos.tabId){
                                chrome.tabs.onUpdated.removeListener(tabIsReady);
                                fct(tab.url);
                            }
                        });
                    }
                });       
            });

            chrome.tabs.onUpdated.addListener(function (tabId, params, tab){
                if(tab.active) {
                    fct(tab.url);
                }                  
            });
        },
		sendMessage:function(tab, name, msg, callback){
			chrome.tabs.sendMessage(tab.id, {"name":name, "message":msg}, callback);
		},
        onMessage:function(tab, name, fct){
        	listenersMessages[tab.id] = function (event, sender, sendResponse){
				if(event.name == name && sender.tab.id == tab.id){
					fct(event.message, sendResponse);
				}
			}
            chrome.runtime.onMessage.addListener(listenersMessages[tab.id]);
        },
        onMessageRemoveListener:function(tab){
            chrome.runtime.onMessage.removeListener(listenersMessages[tab.id]);  
        },
		injectScript:function(tab, fileName, callback){
			chrome.tabs.executeScript(tab.id, {"file":fileName}, callback);
		},
        injectCSS: function(tab, fileName, callback){
            chrome.tabs.insertCSS(tab.id, {"file":fileName}, callback);
        },
        inject:function(tab, files, callback){
            function injectonefile(i){
                if(files[i].substring(files[i].length-3, files[i].length)==".js"){
                    files[i]="/"+files[i];
                    chrome.tabs.executeScript(tab.id, {"file":files[i]}, function(){
                        i++;
                        if(i>=files.length){
                            callback();
                        } else {
                            injectonefile(i);
                        }
                    });
                } else if(files[i].substring(files[i].length-3, files[i].length)=="css") {
                    files[i]="/"+files[i];
                    chrome.tabs.insertCSS(tab.id, {"file":files[i]}, function(){
                        i++;
                        if(i>=files.length){
                            callback();
                        } else {
                            injectonefile(i);
                        }
                    });
                }
            }
            injectonefile(0);
               
        }
    }
}
