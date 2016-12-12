extension.runtime.sendMessage("getPopupContent", {}, function(res){
    for(var i in res){
        $("body").append("<p>"+res[i]+"</p>");
    }
});