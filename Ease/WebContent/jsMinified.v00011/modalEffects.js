var ModalEffects=function(){function e(){var e=document.querySelector(".md-overlay");[].slice.call(document.querySelectorAll(".md-trigger")).forEach(function(t){function c(e){classie.remove(o,"md-show"),e&&classie.remove(document.documentElement,"md-perspective")}function n(){c(classie.has(t,"md-setperspective"))}var o=document.querySelector("#"+t.getAttribute("data-modal")),i=o.querySelector(".md-close");t.addEventListener("click",function(){classie.add(o,"md-show"),e.removeEventListener("click",n),e.addEventListener("click",n),classie.has(t,"md-setperspective")&&setTimeout(function(){classie.add(document.documentElement,"md-perspective")},25)}),i.addEventListener("click",function(e){e.stopPropagation(),n()})})}e()}();