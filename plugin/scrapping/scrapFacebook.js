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
            if($(this).text().substr(0,3)=="Que" || $(this).text().substr(0,3)=="que"){
                
            }
            results.push($(this).text());
            if(index+1 == nbOfApps){
                sendResponse(results);
            }
        });
        
        function clean(index, apps){
            var app = $(apps[index]);
            if(app.text().substr(0,3)=="Que" || app.text().substr(0,3)=="que"){
                app.find("._25kn").click();
                function waitElem(elem){
                    if($(elem).length == 0){
                        setTimeout(function(){waitElem(elem);},100);
                    } else {
                        
                    }
                }
            }
        }
    });
});