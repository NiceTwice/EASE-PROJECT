extension.runtime.bckgrndOnMessage("NewLinkToOpen",function(n,e){console.log("okok2"),extension.currentWindow(function(t){extension.tabs.createOrUpdate(t,e,n.detail.url,n.highlight,function(n){extension.tabs.onUpdated(n,function(e){n=e})})})});