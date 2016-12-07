document.addEventListener("Test", function(event){
    console.log(event);
    extension.runtime.sendMessage("TestConnection", {detail:event.detail}, function(response) {});
}, false);

document.addEventListener("MultipleTests", function(event){
    console.log(event);
    extension.runtime.sendMessage("TestMultipleConnections", {detail:event.detail}, function(response) {});
}, false);