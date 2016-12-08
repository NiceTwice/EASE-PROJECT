document.addEventListener("Test", function(event){
    extension.runtime.sendMessage("TestConnection", {detail:event.detail}, function(response) {});
}, false);

document.addEventListener("MultipleTests", function(event){
    extension.runtime.sendMessage("TestMultipleConnections", {detail:event.detail}, function(response) {});
}, false);

extension.runtime.onMessage("printResults", function(results){
    var response = {};
    response.detail = results;
    var event = new CustomEvent("PrintTestResult", response);
    document.dispatchEvent(event);
});