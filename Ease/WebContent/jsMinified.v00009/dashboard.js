var profileAdder=function(o){var t=this;if(this.qRoot=$(o),this.inputNameHandler=this.qRoot.find("input[name='name']"),this.inputNameDiv=this.qRoot.find(".input"),this.openButton=this.qRoot.find(".opener"),this.closeButton=this.qRoot.find(".closer"),this.adderBody=this.qRoot.find(".adder"),this.submitButton=this.qRoot.find("button[type='submit']"),!o.length)return void this.qRoot;this.colors=[],this.choosenColor=null,this.inputNameHandler.keyup(function(o){13==o.which&&t.submitButton.click()}),this.qRoot.find(".colorHolder").each(function(o,e){var n=new Object;n.color=$(e).find(".color").attr("color"),n.qRoot=$(e),n.qRoot.click(function(){t.scaleInInput(),t.setColor(n.color)}),t.colors.push(n)}),this.setColor=function(o){t.choosenColor=o,t.inputNameHandler.css("background-color",o)},this.suggestionsDivHandler=this.qRoot.find(".suggestionsRow .suggestions"),this.greyColors=["#E2E2E2","#D5D5D5","#D4D0D0"],this.suggestions=["Me","Work","Internship","School","Shopping","Team work","Freelance","Love it","Travel","Discover","Learning","Tools"],this.suggestionsDiv=[],this.createSuggestionDiv=function(o,e){var n=$('<div class="suggestion"><p class="name" style="background-color:'+e+';">'+o+"</p></div>");return n.find("p").click(function(){t.inputNameHandler.focus(),t.inputNameHandler.val(o),t.scaleInInput(),t.setColor(e)}),n};for(var e=0;e<t.suggestions.length;e++){var n=t.createSuggestionDiv(t.suggestions[e],t.colors[e%(t.colors.length-1)].color);this.suggestionsDiv.push(n),t.suggestionsDivHandler.append(n)}this.isValid=function(){return!(!t.inputNameHandler.val().length||!t.choosenColor)||(easeAnimations.animateOnce(t.inputNameDiv,"shake-anim"),!1)},this.scaleInInput=function(){easeAnimations.animateOnce(t.inputNameDiv,"scaleinAnimation")},this.getName=function(){return t.inputNameHandler.val()},this.getColor=function(){return t.choosenColor},this.reset=function(){t.inputNameHandler.val(""),t.choosenColor="#373B60",t.inputNameHandler.css("background-color","")},this.open=function(){t.reset(),t.openButton.addClass("hide"),t.adderBody.addClass("show"),$(".col-left").stop().animate({scrollTop:$(".col-left")[0].scrollHeight},800)},this.close=function(){t.openButton.removeClass("hide"),t.adderBody.removeClass("show")},t.openButton.click(function(){t.open()}),t.closeButton.click(function(){t.close()})},Dashboard=function(o){var t=this;this.rootEl=o,this.columns=this.rootEl.find(".dashboardColumn"),this.isEditMode=!1,this.adder=new profileAdder(this.rootEl.find("#profileAdder")),this.enterEditMode=function(){t.rootEl.addClass("editMode"),t.isEditMode=!0},this.leaveEditMode=function(){t.rootEl.removeClass("editMode"),t.adder.close(),t.isEditMode=!1},this.reinitColumns=function(){this.columns.each(function(){$(this).find(".item").length||$(this).css("width","0px")})},$(".ProfilesHandler .item").length>=15&&this.adder.qRoot.css("display","none"),this.adder.qRoot&&this.adder.submitButton.on("click",function(){if(t.adder.isValid()){for(var o=$($("#profileHelper").html()),e=1e3,n=0,i=0;i<t.columns.length;i++){var s=0;$(t.columns[i]).find(".item").each(function(){var o=$(this).find(".siteLinkBox").length;s+=o<7?2:(o+2)/3}),s<e&&(e=s,n=i)}$(o).addClass("scaleOut12"),setTimeout(function(){$(o).removeClass("scaleOut12")},500),profiles.length+1>=15&&t.adder.qRoot.css("display","none");var r=t.adder.getName(),l=t.adder.choosenColor,a=new Profile($(o).find(".ProfileBox"));a.setName(r),a.setColor(l),profiles.push(a),$(t.columns[n]).css("width",""),$(t.columns[n]).append($(o)),t.adder.close(),postHandler.post("AddProfile",{name:r,color:l},function(){},function(o){a.setId(o),easeTracker.trackEvent("AddProfile",{profieName:r,profileColor:l})},function(o){a.remove(),delete a,t.adder.rootEl.css("display","")},"text")}}),this.columns.each(function(){$(this).find(".item").length||$(this).width(0),$(this).sortable({animation:300,group:"profiles",handle:".ProfileName",filter:".ProfileSettingsButton",forceFallback:!0,onStart:function(o){$(o.item).css("transition","transform 0s"),$("body").css("cursor","move"),$(".ProfileBox .ProfileContent").css("pointer-events","none"),t.columns.each(function(){$(this).find(".item").length||$(this).width("24.8%")})},onEnd:function(o){var e=$(o.item);e.css("transition",""),$("body").css("cursor",""),$(".ProfileBox .ProfileContent").css("pointer-events",""),t.columns.each(function(){$(this).find(".item").length||$(this).width(0)}),postHandler.post("MoveProfile",{columnIdxDest:e.parent().index()+1,positionDest:e.index(),profileId:e.attr("id")},function(){},function(o){easeTracker.trackEvent("MoveProfile")},function(o){},"text")}})})},easeDashboard;$(document).ready(function(){easeDashboard=new Dashboard($(".ProfilesHandler"));var o=window.location.search.substring(1,window.location.search.length).split("&");for(var t in o){var e=o[t].split("=")[0],n=o[t].split("=")[1];"openCatalog"==e&&"true"==n&&enterEditMode()}});