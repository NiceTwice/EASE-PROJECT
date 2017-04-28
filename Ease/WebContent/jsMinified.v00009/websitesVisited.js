function loadWebsitesBroken(){postHandler.post("GetWebsitesBroken",{},function(){websitesBroken.forEach(function(e){e.removeFromDocument()})},function(e){JSON.parse(e).forEach(function(e){console.log(e);var t=new WebsiteBroken(websitesBrokenRow,e.url,e.count,e.isBlacklisted,e.single_id);websitesBroken.push(t),t.printOnDocument()})},function(e){})}function loadWebsitesVisited(){postHandler.post("GetWebsitesVisited",{},function(){websitesVisited.forEach(function(e){e.removeFromDocument()})},function(e){JSON.parse(e).forEach(function(e){var t=new WebsiteVisited(resultsRow,e.url,e.count,e.single_id);websitesVisited.push(t),t.printOnDocument()})},function(e){})}function loadWebsitesDone(){postHandler.post("GetWebsitesDone",{},function(){websitesDone.forEach(function(e){e.removeFromDocument()})},function(e){JSON.parse(e).forEach(function(e){var t=new WebsiteDone(websitesDoneRow,e.url,e.count,e.single_id);websitesDone.push(t),t.printOnDocument()})},function(e){})}function loadBlacklistedWebsites(){postHandler.post("GetBlacklistedWebsites",{},function(){blacklistedWebsites.forEach(function(e){e.removeFromDocument()})},function(e){JSON.parse(e).forEach(function(e){var t=new BlacklistedWebsite(blacklistRow,e.url,e.count,e.single_id);blacklistedWebsites.push(t),t.printOnDocument()})},function(e){})}var websitesVisited=[],websitesBroken=[],websitesDone=[],blacklistedWebsites=[],resultsRow,websitesDoneRow,websitesBrokenRow,blacklistRow,WebsiteVisited=function(e,t,i,s){var n=this;this.rootEl=e,this.url=t,this.count=i,this.single_id=s,this.elem=null,this.printOnDocument=function(){n.elem=$("<div><button>Blacklist</button>"+n.url+((null==i&&i)>0?"":" ("+n.count+")")+"</div>").appendTo(n.rootEl),$("button",n.elem).click(n.remove)},this.remove=function(){postHandler.post("BlacklistWebsite",{single_id:n.single_id,isInCatalog:"false"},function(){},function(e){var t=websitesVisited.indexOf(n);-1!=t&&(n.removeFromDocument(),websitesVisited.splice(t,1),loadBlacklistedWebsites())},function(e){})},this.removeFromDocument=function(){n.elem.remove()}},WebsiteBroken=function(e,t,i,s,n){var o=this;this.rootEl=e,this.url=t,this.count=i,this.isBlacklisted=s,this.single_id=n,this.printOnDocument=function(){o.elem=$("<div><button class= '"+(o.isBlacklisted?"blacklisted":"whitelisted")+"'>"+(o.isBlacklisted?"Whitelist":"Blacklist")+"</button><button class='repair'>Repaired</button>"+o.url+((null==i&&i)>0?"":" ("+o.count+")")+"</div>").prependTo(o.rootEl),$(".repair",o.elem).click(o.remove),o.elem.on("click",".blacklisted",o.whitelist),o.elem.on("click",".whitelisted",o.blacklist)},this.remove=function(){postHandler.post("TurnOnWebsite",{single_id:o.single_id},function(){},function(e){var t=websitesBroken.indexOf(o);-1!=t&&(o.removeFromDocument(),websitesBroken.splice(t,1),loadWebsitesDone())},function(e){})},this.removeFromDocument=function(){o.elem.remove()},this.blacklist=function(){postHandler.post("BlacklistWebsite",{single_id:o.single_id,isInCatalog:"true"},function(){},function(e){var t=$(".whitelisted",o.elem);t.removeClass("whitelisted"),t.addClass("blacklisted"),t.text("Whitelist")},function(e){})},this.whitelist=function(){postHandler.post("WhitelistWebsite",{single_id:o.single_id,isInCatalog:"true"},function(){},function(e){var t=$(".blacklisted",o.elem);t.removeClass("blacklisted"),t.addClass("whitelisted"),t.text("Blacklist")},function(e){})}},WebsiteDone=function(e,t,i,s){var n=this;this.rootEl=e,this.url=t,this.count=i,this.single_id=s,this.elem=null,this.printOnDocument=function(){n.elem=$("<div><button>Broken</button>"+n.url+((null==i&&i)>0?"":" ("+n.count+")")+"</div>").appendTo(n.rootEl),$("button",n.elem).click(n.remove)},this.remove=function(){postHandler.post("TurnOffWebsite",{single_id:s},function(){},function(e){var t=websitesDone.indexOf(n);-1!=t&&(n.removeFromDocument(),websitesDone.splice(t,1),loadWebsitesBroken())},function(e){})},this.removeFromDocument=function(){n.elem.remove()}},BlacklistedWebsite=function(e,t,i,s){var n=this;this.rootEl=e,this.url=t,this.count=i,this.single_id=s,this.elem=null,this.printOnDocument=function(){n.elem=$("<div><button>Whitelist</button>"+n.url+((null==i&&i)>0?"":" ("+n.count+")")+"</div>").appendTo(n.rootEl),$("button",n.elem).click(n.remove)},this.remove=function(){postHandler.post("WhitelistWebsite",{single_id:n.single_id,isInCatalog:"false"},function(){},function(e){var t=blacklistedWebsites.indexOf(n);-1!=t&&(n.removeFromDocument(),blacklistedWebsites.splice(t,1),loadWebsitesVisited())},function(e){})},this.removeFromDocument=function(){n.elem.remove()}};$(document).ready(function(){websitesDoneRow=$("#WebsitesVisitedTab #websitesDone"),resultsRow=$("#WebsitesVisitedTab #results"),blacklistRow=$("#WebsitesVisitedTab #blacklist"),websitesBrokenRow=$("#WebsitesVisitedTab #websitesBroken"),$(".adminButton[target='WebsitesVisitedTab']").click(function(){loadWebsitesVisited(),loadWebsitesDone(),loadWebsitesBroken(),loadBlacklistedWebsites()})});