extension.runtime.bckgrndOnMessage('newConnectionToRandomWebsite', function(msg, sendResponse){
    rememberConnection(msg.username, msg.website, false);
});

var lastNavigatedWebsite = "";

extension.tabs.onNavigation(function(url){
    if(matchFacebookConnectUrl(url) && !matchFacebookConnectUrl(lastNavigatedWebsite)){
        rememberLogWithConnection(lastNavigatedWebsite, "www.facebook.com");
    } else if(matchLinkedinConnectUrl(url) && !matchLinkedinConnectUrl(lastNavigatedWebsite)){
        rememberLogWithConnection(lastNavigatedWebsite, "www.linkedin.com");
    }
    lastNavigatedWebsite = getHost(url);
    console.log("-- Last navigated website : "+lastNavigatedWebsite+" --");
});

function rememberConnection(username, website){
    console.log("-- Connection for email " + username + " on website " + website + " remembered --");
    extension.storage.get('allConnections', function(res){
        if(!res) res = {};
        res[website] = username;
        extension.storage.set('allConnections', res, function(){});
    });    
}

function rememberLogWithConnection(website, logWithWebsite){
    extension.storage.get('allConnections', function(res){
        if(!res) res = {};
        if(res[logWithWebsite]){
            res[website] = {"user":res[logWithWebsite], "logWith":logWithWebsite};
        }
        console.log("-- Connection with " +logWithWebsite + " on website " + website + " remembered --");
        extension.storage.set('allConnections', res, function(){});
    });    
}

function matchFacebookConnectUrl(url){
    if(url.indexOf("facebook")!==-1 && url.substr(12,8) != "facebook" && url.substr(4,8) != "facebook") {
        return "true";
    }
    return false;
}

function matchLinkedinConnectUrl(url){
    if(url.indexOf("linkedin")!==-1 && url.substr(12,8) != "linkedin") {
        return "true";
    }
    return false;
}

function getHost(url){
    var getLocation = function(href) {
        var l = document.createElement("a");
        l.href = href;
        return l;
    };
    return getLocation(url).hostname;
}