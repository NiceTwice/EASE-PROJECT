function getNewLogin(e,t){return e.detail[t].user?{user:e.detail[t].user.login,password:e.detail[t].user.password}:e.detail[t].logWith?{user:getNewLogin(e,t-1).user,logWith:getHost(e.detail[t-1].website.loginUrl)}:void 0}function rememberWebsite(e){""!=e.lastLogin&&e.lastLogin&&(extension.storage.get("visitedWebsites",function(t){for(var o in t)if(t[o].name==e.name){if(t[o].lastLogin==e.lastLogin)return;t.splice(o,1);break}("undefined"==typeof t||null==t||void 0==t||0==t.length||t=={})&&(t=[]),t.push(e),extension.storage.set("visitedWebsites",t)}),e.loginUrl||(e.loginUrl=e.home),e.lastLogin.logWith?rememberDirectLogWithConnection(getHost(e.loginUrl),e.lastLogin):rememberConnection(e.lastLogin.user,e.lastLogin.password,getHost(e.loginUrl),!0))}function endConnection(e,t,o){extension.tabs.sendMessage(t,"rmOverlay",o,function(){"Fail"==o.result})}function checkFacebook(e,t){return"Facebook"==e.detail[0].website.name&&e.detail[1]?void extension.storage.get("lastConnections",function(o){return void 0!=o&&o["www.facebook.com"]&&o["www.facebook.com"].user==e.detail[0].user.login?void t(e.bigStep+1):void t(e.bigStep)}):void t(e.bigStep)}extension.reloadEaseTabs(),extension.runtime.bckgrndOnMessage("NewConnection",function(e,t,o){e.todo="checkAlreadyLogged",e.bigStep=0,checkFacebook(e,function(i){e.bigStep=i,e.actionStep=0,e.waitreload=!1,extension.currentWindow(function(i){var n=e.detail[e.bigStep].website.home;if("object"==typeof n){var s=n.http+e.detail[0].user[n.subdomain]+"."+n.domain;e.detail[e.bigStep].website.home=s}extension.tabs.createOrUpdate(i,t,e.detail[e.bigStep].website.home,e.highlight,function(t){extension.tabs.onUpdated(t,function(e){t=e,extension.tabs.inject(t,["tools/extensionLight.js","overlay/overlay.css","overlay/injectOverlay.js"],function(){})}),extension.tabs.onMessage(t,"reloaded",function(){console.log("-- Page reloaded --"),extension.tabs.inject(t,["tools/extension.js","jquery-3.1.0.js","contentScripts/actions.js","contentScripts/connect.js"],function(){extension.storage.get("visitedWebsites",function(n){extension.storage.get("lastConnections",function(s){e.visitedWebsites=n,void 0==s&&(s={}),e.lastConnections=s,extension.tabs.sendMessage(t,"goooo",e,function(n){n&&(console.log("-- Status : "+n.type+" --"),"completed"==n.type?(e.waitreload=n.waitreload,e.todo=n.todo,e.bigStep=n.bigStep,e.actionStep=n.actionStep,e.detail[e.bigStep].website.lastLogin=n.detail[e.bigStep].website.lastLogin,"end"!=e.todo&&"nextBigStep"!=e.todo&&e.actionStep<e.detail[e.bigStep].website[e.todo].todo.length||("logout"==e.todo?(e.todo="undefined"==typeof e.detail[e.bigStep].logWith?"connect":e.detail[e.bigStep].logWith,e.actionStep=0):"nextBigStep"==e.todo?(e.todo="checkAlreadyLogged",extension.tabs.update(t,e.detail[e.bigStep].website.home,function(){})):(e.detail[e.bigStep].website.lastLogin=getNewLogin(e,e.bigStep),rememberWebsite(e.detail[e.bigStep].website),e.actionStep=0,e.bigStep++,e.bigStep<e.detail.length?e.waitreload?(e.todo="nextBigStep",console.log("-- Wait for nextBigStep --")):(e.todo="checkAlreadyLogged",extension.tabs.update(t,e.detail[e.bigStep].website.home,function(){})):(e.todo="checkAlreadyLogged",e.result="Success",endConnection(i,t,e,o),extension.tabs.onUpdatedRemoveListener(t),extension.tabs.onMessageRemoveListener(t))))):void 0!=n&&(e.result="Fail",endConnection(i,t,e,o),extension.tabs.onUpdatedRemoveListener(t),extension.tabs.onMessageRemoveListener(t)))})})})})})})})})});