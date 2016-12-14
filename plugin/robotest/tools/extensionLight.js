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