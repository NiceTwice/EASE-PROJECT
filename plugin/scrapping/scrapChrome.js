function waitload(callback){
    if($(".fba").length == 0){
        setTimeout(function(){waitload(callback);},100);
    } else {
        callback();
    }
}

waitload(function(){
    var loadingString = "";
    var waitingTime = 5;
    console.log($(".z-Of.cba.z-op").length);
    var nbOfPass = $(".z-Of.cba.z-op").length;    
    function getPass(index){
        var element = $(".z-Of.cba.z-op").get(index);
        var field = $(element).find(".bba.EW.a-Fa");
        $(element).find(".Vaa.AW.BW").click();
        function waitPass(){
            if($(field).val()!="••••••••" && $(field).val()!=loadingString){
                if(waitingTime == 5){//Get the string "Chargement en cours" whatever language
                    loadingString = $(field).val();
                    waitingTime = 50;
                    setTimeout(waitPass, waitingTime);
                } else {
                    console.log(index+". "+$(element).find(".Zaa").text()+" - "+$(element).find(".CW").text()+" - "+$(field).val());
                    if(index+1 < nbOfPass)
                        getPass(index+1);
                    else
                        $(element).find(".Vaa.AW.aga").click();
                }
            } else {
                setTimeout(waitPass, waitingTime);
            }
        }
        waitPass();
    }
    getPass(0);
});