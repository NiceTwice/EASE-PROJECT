var extensionLight = {
    runtime:{
        sendMessage:function(name, msg, callback){
		  chrome.runtime.sendMessage({"name":name, "message":msg}, callback);
        },
	   onMessage:function(name, fct){
           chrome.runtime.onMessage.addListener(function(event, sender, sendResponse){
	           if(event.name == name){
                   fct(event.message, sendResponse);
                   return true;
               }
            });
        }
    }
}

if (window.top === window) {

    if (!document.getElementById("ease_overlay_mamene")){
        var overlay = document.createElement('div');
        overlay.id = "ease_overlay_mamene";
        var popup = document.createElement('div');
        document.body.appendChild(overlay);
        extensionLight.runtime.sendMessage('reloaded',{}, function(){});
        startOverlay();
    }
    
}



function startOverlay(){
    
    var overlay = document.getElementById("ease_overlay_mamene");
    
    overlay.className = "overlayEase";
    
    var spinner = document.createElement('div');
    spinner.id = "div-loading-ease-mamene";
    spinner.className = "spinnerElementEase";
    spinner.style = "background-color: #6C7FA0";
    overlay.appendChild(spinner);
    
    var close = document.createElement('a');
    close.href = "javascript:void(0)";
    close.className = "closeEaseOverlay";
    close.setAttribute('onClick',"document.getElementById('ease_overlay_mamene').style='display: none';");
    close.innerHTML = '&times;';
    spinner.appendChild(close);
    
    var text = document.createElement('div');
    text.id = "loading-text-ease-mamene";
    text.className = "spinnerElementEase";
    text.innerHTML = "Loading";
    spinner.appendChild(text);
     
    var spaceship = document.createElement('div');
    spaceship.id = "loading-background-ease-mamene";
    spaceship.className = "spinnerElementEase";
    spinner.appendChild(spaceship);
            
    /*var spinner = document.createElement('div');
    spinner.id = "ease_spinner_mamene";
    spinner.className = "spinnerEase";
    
    popup.appendChild(spinner);*/
}

function checkConnectionOverlay(msg){
    spinner = document.getElementById("div-loading-ease-mamene");
    text = document.getElementById("loading-text-ease-mamene");
    spinner.style = "background-color: #D4C60F";
    text.innerHTML = "Analysing";
    
}

function loginOverlay(msg){ //quand todo = login
    setTimeout(function(){
        spinner = document.getElementById("div-loading-ease-mamene");
        text = document.getElementById("loading-text-ease-mamene");
        spinner.style = "background-color: #266A2E";
        text.innerHTML = "Logging in";
    }, 200);
}

function logoutOverlay(msg){ //quand todo = logout
    setTimeout(function(){
        spinner = document.getElementById("div-loading-ease-mamene");
        text = document.getElementById("loading-text-ease-mamene");
        spinner.style = "background-color: #ec555b";
        text.innerHTML = "Logging out";
    }, 200);
}

function endOverlay(msg){
    setTimeout(function(){overlay.style = "display: none"}, 300);
}

function errorOverlay(msg){ //quand grave ou fin waitfor
    spinner = document.getElementById("div-loading-ease-mamene");
    text = document.getElementById("loading-text-ease-mamene");
    spinner.style = "background-color: #000000";
    text.innerHTML = "Error";
    setTimeout(function(){overlay.style = "display: none"},300);
}

extensionLight.runtime.onMessage( "rmOverlay", function(message, sendResponse){
    endOverlay({});
});