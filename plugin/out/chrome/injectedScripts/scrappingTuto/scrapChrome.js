if (window.location.href.indexOf("https://passwords.google.com/") == 0 || window.location.href.indexOf("https://accounts.google.com/") == 0 || window.location.href.indexOf("https://myaccount.google.com/") == 0) {

    extension.onMessage.addListener("scrapChrome", function (msg, sendResponse) {
        function waitload(callback) {
            if ($(".z-Of.cba.z-op").length != 0) {
                callback();
            } else if ($(".gga").length != 0) {
                sendResponse([]);
            } else {
                setTimeout(function () {
                    waitload(callback);
                }, 100);
            }
        }

        waitload(function () {
            var loadingString = "";
            var waitingTime = 5;
            var nbOfPass = $(".z-Of.cba.z-op").length;
            var results = [];
            function getPass(index) {
                var element = $(".z-Of.cba.z-op").get(index);
                var field = $(element).find(".bba.EW.a-Fa");

                function waitPass() {
                    if ($(field).val() != "••••••••" && $(field).val() != loadingString) {
                        if (waitingTime == 5) { //Get the string "Chargement en cours" whatever language
                            loadingString = $(field).val();
                            waitingTime = 50;
                            setTimeout(waitPass, waitingTime);
                        } else {
                            if ($(element).find(".CW").text() != "") {
                                results.push({
                                    website: $(element).find(".Zaa").text(),
                                    login: $(element).find(".CW").text(),
                                    pass: $(field).val()
                                });
                            }
                            if (index + 1 < nbOfPass)
                                getPass(index + 1);
                            else {
                                $(element).find(".Vaa.AW.aga").click();
                                sendResponse(results);
                            }
                        }
                    } else {
                        setTimeout(waitPass, waitingTime);
                    }
                }
                if ($(element).find(".Vaa.AW.BW").length != 0) {
                    $(element).find(".Vaa.AW.BW").click();
                    waitPass();
                } else {
                    if (index + 1 < nbOfPass)
                        getPass(index + 1);
                    else {
                        $(element).find(".Vaa.AW.aga").click();
                        sendResponse(results);
                    }
                }

            }
            getPass(0);
        });
    });

}
