var listenerFactory = {
    addListener: function (easeFct, chromeFct, userFct, createdFct) {
        if (!easeFct.listeners) {
            easeFct.listeners = [];
        }
        var list = easeFct.listeners;
        var store = {
            "userFct": userFct,
            "createdFct": createdFct,
        }
        list.push(store);
        chromeFct.addListener(createdFct);
    },
    removeListener: function (easeFct, chromeFct, userFct) {
        if (easeFct.listeners) {
            var list = easeFct.listeners;
            if (!userFct) {
                for (var i = 0; i < list.length; i++) {
                    chromeFct.removeListener(list[i].createdFct);
                    list.splice(i, 1);
                    i--;
                }
            } else {
                for (var i in list) {
                    if (list[i].userFct == userFct) {
                        chromeFct.removeListener(list[i].createdFct);
                        list.splice(i, 1);
                        break;
                    }
                }
            }
        }

    }
};



var extension = {
    onInstalled: {
        addListener: function (fct) {
            listenerFactory.addListener(this, chrome.runtime.onInstalled, fct, fct);
        },
        removeListener: function (fct) {
            listenerFactory.removeListener(this, chrome.runtime.onInstalled, fct);
        }
    },
    windows: {
        getAll: function (callback) {
            chrome.windows.getAll({
                populate: true,
                windowTypes: ['normal']
            }, callback);
        },
        getCurrent: function (callback) {
            chrome.windows.getCurrent({
                "populate": true
            }, callback)
        },
        getFromTab: function (tab, callback) {
            chrome.windows.get(tab.windowId, {
                "populate": true
            }, callback)
        },
        onCreated: {
            addListener: function (fct) {
                chrome.windows.onCreated.addListener(fct);
            },
            removeListener: function (fct) {
                chrome.windows.onCreated.removeListener(fct);
            }
        },
        onClosed: {
            addListener: function (fct) {
                chrome.windows.onRemoved.addListener(fct);
            },
            removeListener: function (fct) {
                chrome.windows.onRemoved.removeListener(fct);
            }
        }
    },
    ease: {
        getTabs: function (currWindow, callback) {
            var result = [];
            if (currWindow == null) {
                extension.windows.getAll(function (windows) {
                    for (var j in windows) {
                        var window = windows[j];
                        for (var i in window.tabs) {
                            if (window.tabs[i].url.indexOf("https://ease.space") == 0) {
                                result.push(window.tabs[i]);
                            }
                        }
                    }
                    callback(result);
                });
            } else {
                for (var i in currWindow.tabs) {
                    if (currWindow.tabs[i].url.indexOf("https://ease.space") == 0) {
                        result.push(currWindow.tabs[i]);
                    }
                }
                callback(result);
            }
        },
        reload: function () {
            extension.ease.getTabs(null, function (tabs) {
                for (var i in tabs) {
                    chrome.tabs.reload(tabs[i].id, {}, function () {});
                }
            });
        }

    },
    local_storage: {
        get: function (key, callback) {
            chrome.storage.local.get(key, function (res) {
                callback(res[key]);
            });
        },
        set: function (key, value, callback) {
            var obj = {};
            obj[key] = value;
            chrome.storage.local.set(obj, callback);
        }
    },
    addShortCut: function (fct) {
        chrome.commands.onCommand.addListener(fct);
    },
    runtime: {
        onMessage: {
            addListener: function (messageName, fct) {
                var f = function (event, sender, sendResponse) {
                    if (event.name == messageName) {
                        fct(event.message, sender.tab, sendResponse);
                        return true;
                    }
                };
                listenerFactory.addListener(this, chrome.runtime.onMessage, fct, f);
            },
            removeListener: function (fct) {
                listenerFactory.removeListener(this, chrome.runtime.onMessage, fct);
            }
        }
    },
    tabs: {
        stopLoad: function (tab, callback) {
            chrome.tabs.executeScript(tab.id, {
                code: "window.stop();",
                runAt: "document_start"
            }, callback);
        },
        update: function (tab, url, callback) {
            chrome.tabs.update(tab.id, {
                "url": url
            }, callback);
        },
        create: function (window, url, active, callback) {
            chrome.tabs.create({
                "windowId": window.id,
                "url": url,
                "active": active
            }, callback);
        },
        focus: function (tab, callback) {
            chrome.tabs.highlight({
                "windowId": tab.windowId,
                "tabs": tab.index
            }, callback);
        },
        close: function (tab) {
            chrome.tabs.remove(tab.id, function () {});
        },
        onCreated: {
            addListener: function (fct) {
                listenerFactory.addListener(this, chrome.tabs.onCreated, fct, fct);
            },
            removeListener: function (fct) {
                listenerFactory.removeListener(this, chrome.tabs.onCreated, fct);
            }
        },
        onUpdated: {
            addListener: function (tab, fct) {
                var f = function (event, sender, sendResponse) {
                    if ((tab == null || (sender.tab && sender.tab.id == tab.id)) && event.name == "reloaded") {
                        fct(sender.tab);
                        return true;
                    }
                };
                listenerFactory.addListener(this, chrome.tabs.onUpdated, fct, f);
            },
            removeListener: function (fct) {
                listenerFactory.removeListener(this, chrome.tabs.onUpdated, fct);
            }
        },
        onReloaded: {
            addListener: function (tab, fct) {
                var f = function (event, sender, sendResponse) {
                    if ((tab == null || (sender.tab && sender.tab.id == tab.id)) && event.name == "reloaded") {
                        fct(sender.tab);
                        return true;
                    }
                };
                listenerFactory.addListener(this, chrome.runtime.onMessage, fct, f);
            },
            removeListener: function (fct) {
                listenerFactory.removeListener(this, chrome.runtime.onMessage, fct);
            }
        },
        onClosed: {
            addListener: function (tab, fct) {
                var f = function (tabId, removeInfos) {
                    if (tab.id == tabId) {
                        fct();
                        return true;
                    }
                };
                listenerFactory.addListener(this, chrome.tabs.onRemoved, fct, f);
            },
            removeListener: function (fct) {
                listenerFactory.removeListener(this, chrome.tabs.onRemoved, fct);
            }
        },
        sendMessage: function (tab, name, msg, callback) {
            chrome.tabs.sendMessage(tab.id, {
                "name": name,
                "message": msg
            }, callback);
        },
        onMessage: {
            addListener: function (tab, name, fct) {
                var f = function (event, sender, sendResponse) {
                    if (sender.tab && sender.tab.id == tab.id && event.name == name) {
                        fct(event.message, sendResponse);
                        return true;
                    }
                };
                listenerFactory.addListener(this, chrome.runtime.onMessage, fct, f);
            },
            removeListener: function (fct) {
                listenerFactory.removeListener(this, chrome.runtime.onMessage, fct);
            }
        }
    }
};

extension.runtime.onMessage.addListener("GetChromeUser", function (msg, senderTab, sendResponse) {
    chrome.identity.getProfileUserInfo(function (userInfo) {
        sendResponse(userInfo.email);
    });
});
