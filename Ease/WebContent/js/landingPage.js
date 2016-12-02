/*!
 * Start Bootstrap - Agency v3.3.7+1 (http://startbootstrap.com/template-overviews/agency)
 * Copyright 2013-2016 Start Bootstrap
 * Licensed under MIT (https://github.com/BlackrockDigital/startbootstrap/blob/gh-pages/LICENSE)
 */
 ! function(t) {
    "use strict";
    t("a.page-scroll").bind("click", function(a) {
        var o = t(this);
        t("html, body").stop().animate({
            scrollTop: t(o.attr("href")).offset().top - 50
        }, 1250, "easeInOutExpo"), a.preventDefault()
    }), t(".navbar-collapse ul li a").click(function() {
        
    }), t("#mainNav").affix({
        offset: {
            top: 100
        }
    })
}(jQuery);

$(document).ready(function(){
    var videoTag = $('video').get(0);
    videoTag.addEventListener("loadedmetadata", function(event) {
     videoRatio = videoTag.videoWidth / videoTag.videoHeight;
     targetRatio = $(videoTag).width() / $(videoTag).height();
     if (videoRatio < targetRatio) {
      $(videoTag).css("transform", "scaleX(" + (targetRatio / videoRatio) + ")");
  } else if (targetRatio < videoRatio) {
      $(videoTag).css("transform", "scaleY(" + (videoRatio / targetRatio) + ")");
  } else {
      $(videoTag).css("transform", "");
  }
});
});