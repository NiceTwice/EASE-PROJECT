if (window.top === window) {
    extension.runtime.onMessage("scrapOverlay", function(msg, sendResponse){
        if (!document.getElementById("ease_overlay_scrap")){
            var overlay = document.createElement('div');
            overlay.id = "ease_overlay_scrap";
            document.body.appendChild(overlay);
            
            extension.runtime.sendMessage('scrapReloaded',{}, function(){});
            
            var textWebsite;
            var logoWebsite;
            var titleWebsite;
            var titleWebsite2
            var websiteColor;
            
            if(msg == "Linkedin"){
                titleWebsite = "Importing accounts";
                titleWebsite2 = "you connected with";
                textWebsite = "Sign in with Linkedin";
                logoWebsite = "linkedin.png";
                websiteColor = "3b5998";
            } else if (msg == "Facebook"){
                titleWebsite = "Importing accounts";
                titleWebsite2 = "you connected with";
                textWebsite = "Sign in with Facebook";
                logoWebsite = "facebook.png";
                websiteColor = "3b5998";
            } else if (msg == "Chrome"){
                titleWebsite = "Importing accounts saved in";
                titleWebsite2 = false;
                textWebsite = "Google Chrome";
                logoWebsite = "chrome.png";
                websiteColor = false;
            }
            
            overlay.className = "overlayScrap";
            var container = document.createElement('div');
            container.className = "containerScrap";
            overlay.appendChild(container);
            
            var logoEase = document.createElement('img');
            logoEase.src = chrome.extension.getURL('logo.png');
            logoEase.className = "logoEase";
            container.appendChild(logoEase);
            
            var titleContainer = document.createElement('div');
            titleContainer.className = "titleContainer";
            container.appendChild(titleContainer);
            var title = document.createElement('p');
            title.className = "title";
            title.textContent = titleWebsite;
            titleContainer.appendChild(title);
            if(titleWebsite2){
                var title2 = document.createElement('p');
                title2.className = "title";
                title2.textContent = titleWebsite2;
                titleContainer.appendChild(title2);
            }
            
            var websiteContainer = document.createElement('div');
            websiteContainer.className = "websiteContainer";
            if(websiteColor){
                 websiteContainer.style = "background-color:#"+websiteColor+"; border-color:#"+websiteColor;
            }
            container.appendChild(websiteContainer);
            var websiteLogo = document.createElement('img');
            websiteLogo.src = chrome.extension.getURL(logoWebsite);
            websiteContainer.appendChild(websiteLogo);
            var websiteDescription = document.createElement('span');
            websiteDescription.textContent = textWebsite;
            if(websiteColor){
                websiteDescription.style = "color:white;";
            }
            websiteContainer.appendChild(websiteDescription);
            
            var loader = document.createElement('div');
            loader.className="loader";
            container.appendChild(loader);
            
            var infoContainer = document.createElement('div');
            infoContainer.className = "infoContainer";
            container.appendChild(infoContainer);
            var info = document.createElement('p');
            info.textContent = "You’ll select the ones you want to";
            infoContainer.append(info);
            var info2 = document.createElement('p');
            info2.textContent = "keep right after this.";
            infoContainer.append(info2);
            
        }
    });
    
    
}