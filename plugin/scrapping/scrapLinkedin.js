extension.runtime.onMessage("checkLnkdnCo", function(msg, sendResponse){
    if($("input[type='password']").length==0){
        sendResponse(true);
    } else {
        sendResponse(false);
    }
});

extension.runtime.onMessage("logoutFromLnkdn", function(msg, sendResponse){
    window.location.href = $(".account-submenu-split-link").attr("href");
    sendResponse();
});

extension.runtime.onMessage("connectToLnkdn", function(msg, sendResponse){
    if($("#session_key-login").length==0){
        sendResponse(false);
    } else {
        $("#session_key-login").val(msg.login);
        $("#session_password-login").val(msg.pass);
        $("#btn-primary").click();
        sendResponse(true);
    }
});

extension.runtime.onMessage("scrapLnkdn", function(msg, sendResponse){
    var results = [];
    var nbOfApps = $(".third-party-apps-name").length;
    if(nbOfApps == 0)
        sendResponse(results);
    else {
        $(".third-party-apps-name").each(function(index){
            results.push($(this).text());
            if(index+1 == nbOfApps){
                sendResponse(results);
            }
        });
    }
});

