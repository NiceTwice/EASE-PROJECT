function waitload(callback){
    if($("._ikh._4na3").length == 0){
        setTimeout(function(){waitload(callback);},100);
    } else {
        callback();
    }
}

waitload(function(){
    $(".fsl").click();
    $("._4n9u.ellipsis").each(function(index){
        console.log(index+". "+$(this).text());
    });
});