		<div id="downloadExtension" class="centeredItem" style="display:none;">
		<div class="popupContent">
			<p class="title classicContent">Get the Ease extension ! <i class="fa fa-heart" aria-hidden="true"></i></p>
			<p class="title safariUpdate">Sorry, you must uninstall and reinstall the extension. <i class="fa fa-recycle" aria-hidden="true"></i></p>
			<p class="info classicContent">Download the extension to make Ease automagically work on this computer</br>For now, it only works on Chrome and Safari.</p>
			<p class="info safariUpdate">We have detected some minor bugs with the last extension version.</br>We recommend you <span>to uninstall the Ease extension</span> and redownload it !</p>
			<!--  <p><i class="fa fa-angle-down" aria-hidden="true"></i></p>-->
			<button class="install-button classicContent">Get Ease Extension</button>
			<button class="install-button safariUpdate">Follow the steps</button>
			<p id="moreInfoButton" style="margin-top: 5px;">Why is the extension necessary.</p>
			<!-- <p id="safariInfoButton" style="margin-top: 5px; display:none;">I already have the addon !</p> -->
		</div>
		<div class="safariHelper" id="uninstall" style="display: none;">
			<h1 style="margin-top: 2px; font-size: 37px;">How to uninstall the extension ?</h1>
			<p style="font-size: 16px;">Go to "Safari" -> "Preferences" -> "Extensions" and click on "uninstall" on the Ease extension.</p>
			<div style="width: 40%;margin:5px auto 5px auto;display:inline-block;"><img style="width: 100%" src="resources/other/preferences-menu.png"/></div>
			<div style="width: 50%;margin:5px auto 5px auto;display:inline-block;"><img style="width: 100%" src="resources/other/extensions-menu.png"/></div>
			<button class="safariUninstall">Next step</button>
		</div>
		<div class="safariHelper" id="installdownloaded" style="display: none;">
			<h1 style="margin-top: 2px; font-size: 37px;">Install the extension</h1>
			<p style="font-size: 16px;">Now that you have downloaded the EaseExtension.safariextz file, you have to click on it in your downloads folder to install the extension.</p>
			<div style="width: 55%;margin:5px auto 5px auto;"><img style="width: 100%" src="resources/other/safari-addon-example.png"/></div>
			<p style="font-size: 16px;">When the installation is completed, this page should reload automatically.</br><span>You will then be able to fully use Ease ;)</span></p>
		</div>
		<div class="safariHelper" id="afterdownload" style="display: none;">
			<h1 style="margin-top: 2px; font-size: 37px;">Final step</h1>
			<p style="font-size: 16px;">Once you have downloaded our extension, just <a href="/">reload this page</a> :)</p>
		</div>
		<div class="safariHelper" id="alreadydownloaded" style="display: none;">
			<p style="font-size: 16px; margin-top:5px;">If you have already downloaded the addon, try to double click on "EaseExtension.safariextz" in your downloads folder to install it. Then <a href="/">reload this page</a>.
			<br>If it's still not working, you can contact us on <a href="https://www.facebook.com/EasePlatform/?fref=ts" target="_blank">our facebook page.</a></p>
			<div style="width: 55%;margin:5px auto 5px auto;"><img style="width: 100%" src="resources/other/safari-addon-example.png"/></div>
			<button id="returnButtonSafari">OK, got it.</button>
		</div>
		<div class="popupHelper" style="display: none;">
			<div class="firstLine" style="margin-bottom: 10px;">
				<div class="imageHandler">
					<img src="resources/other/extension_example.png" />
				</div>

				<div class="textHelper">
					<p style="font-size: 20px; font-weight: 600;">What ease this ?</p>
					<p>Ease uses an Ä<span>extension</span> to <span>log you</span> on your favorite websitesÄù. It is a small piece of software that you add to your browser to customize its capabilities.
					</p>
				</div>
			</div>
			<div style="text-align:left;">
			<p>Without this extension, Ease can't fill the input to log you. The few datas stored in it are <span>fully encrypted</span> and are <span>never used for commercial purposes</span>.
			</p>
			<p>Downloading the extension is a <span>1 step</span> process directly from Ease.</p>
			</div>
			<button id="returnButton">OK, got it.</button>
		</div>
		</div>
		<script type="text/javascript">
			$('#downloadExtension #moreInfoButton').click(function(){
				$('#downloadExtension .popupContent').css('display', 'none');
				$('#downloadExtension .popupHelper').css('display', 'block');
			});
			$('#downloadExtension #returnButton').click(function(){
				$('#downloadExtension .popupContent').css('display', 'block');
				$('#downloadExtension .popupHelper').css('display', 'none');				
			});
			
			$('#uninstall .safariUninstall').click(function(){
				window.location.replace("index.jsp");
			});
			/*$('#downloadExtension #safariInfoButton').click(function(){
				$('#downloadExtension .popupContent').css('display', 'none');
				$('#downloadExtension #alreadydownloaded').css('display', 'block');
			});
			$('#downloadExtension #returnButtonSafari').click(function(){
				$('#downloadExtension .popupContent').css('display', 'block');
				$('#downloadExtension #alreadydownloaded').css('display', 'none');				
			});*/
		</script>
