$(document).ready(function(){isMac&&("Firefox"==getUserNavigator()?ctrlCode=224:"Chrome"!=getUserNavigator()&&"Safari"!=getUserNavigator()||(ctrlCode=91),$(".shortcutInfo").text("Hold cmd and click to open multiple apps."));var t=$(".shortcutInfo").text();$("body").keydown(function(t){16==t.which&&($(".shortcutInfo").text("Hold shift and click to test an app."),$(".shortcutInfo").show(),testApp=!0)}),$(window).blur(function(o){$(".shortcutInfo").hide(),$(".shortcutInfo").text(t),testApp=!1}),$("body").keyup(function(o){16==o.which&&($(".shortcutInfo").hide(),$(".shortcutInfo").text(t),testApp=!1)})});