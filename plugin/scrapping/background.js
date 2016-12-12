function startScrapFacebook(){
    extension.currentWindow(function(window){
        extension.tabs.create(window, "https://www.facebook.com/settings?tab=applications", false, function(tab){
            extension.tabs.onMessage(tab, "scrapFacebook", function(result){
                console.log(result);
                extension.tabs.close(tab, function(){});
                extension.tabs.onMessageRemoveListener(tab);
            });
        });
    });
}