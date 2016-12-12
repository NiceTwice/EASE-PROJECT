<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<div class="RightSideViewTab" id="TestWebsitesTab">
	<button id="quit">
		<i class="fa fa-times"></i>
	</button>

	<div>
		<div>
			<p>Test if websites work</p>
		</div>

		<div>
			<button id="buttonTestWebsites">Begin test</button>
			<p style="display:inline-block; margin-left:10px;" id="nbOfSuccess">Success : 0/0 (0%)</p>
		</div>
		<div style="margin-top:15px; margin-bottom:75px;" id="testResults">
		</div>
	<script>
		document.addEventListener("PrintTestResult", function(event){
			var res = event.detail;
			$("#testResults p").remove();
			var nbOfTests = 0;
			var nbOfSuccess = 0;
			for(var i in res){
				nbOfTests++;
				if(res[i].indexOf("SUCCESS")!=-1) nbOfSuccess++;
		        $("#testResults").append("<p>"+res[i]+"</p>");
		    }
			$("#nbOfSuccess").text("Success : "+nbOfSuccess+"/"+nbOfTests+" ("+Math.floor(100*nbOfSuccess/nbOfTests)+"%)");
		}, false);
	</script>
	</div>
</div>