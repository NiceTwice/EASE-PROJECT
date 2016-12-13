extension.runtime.onMessage("checkFbCo", function(msg, sendResponse){
    if($("input[type='password']").length==0){
        sendResponse(true);
    } else {
        sendResponse(false);
    }
});

extension.runtime.onMessage("logoutFromFb", function(msg, sendResponse){
    var domain = document.domain;
    var path = "/";
    document.cookie = "c_user=; expires=" + +new Date + "; domain=" + domain + "; path=" + path;
    sendResponse();
});

extension.runtime.onMessage("connectToFb", function(msg, sendResponse){
    if($("#email").length==0){
        sendResponse(false);
    } else {
        $("#email").val(msg.login);
        $("#pass").val(msg.pass);
        $("#loginbutton").click();
        sendResponse(true);
    }
});

extension.runtime.onMessage("scrapFb", function(msg, sendResponse){
    function waitload(callback){
        if($("._ikh._4na3").length == 0){
            setTimeout(function(){waitload(callback);},100);
        } else {
            callback();
        }
    }
    var results=[];
    waitload(function(){
        $(".fsl").click();
        var nbOfApps = $("._4n9u.ellipsis").length;
        $("._4n9u.ellipsis").each(function(index){
            results.push($(this).text());
            if(index+1 == nbOfApps){
                sendResponse(results);
            }
        });
    });
});