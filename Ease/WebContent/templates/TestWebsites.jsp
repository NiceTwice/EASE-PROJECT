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
			<p style="display:inline-block;" id="nbOfSuccess"></p>
		</div>
		<div style="margin-top:15px; margin-bottom:50px;" id="testResults">
		</div>
	<script>
		document.addEventListener("PrintTestResult", function(event){
			var res = event.detail;
			$("#testResults p").remove();
			for(var i in res){
		        $("#testResults").append("<p>"+res[i]+"</p>");
		    }
		}, false);
	</script>
	</div>
</div>