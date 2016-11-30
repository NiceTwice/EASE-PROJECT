		<div id="downloadExtension" class="centeredItem" style="display:none;">
		<div class="popupContent">
			<p class="title classicContent">You are almost done ...<i class="fa fa-heart" aria-hidden="true"></i></p>
			<p class="title safariUpdate">Your addon is too old ! <i class="fa fa-recycle" aria-hidden="true"></i></p>
			<p class="info classicContent">Download the addon to make Ease automagically work on this computer</br>For now, it only works on Chrome and Safari.</p>
			<p class="info safariUpdate">The new addon version is now available. We fixed several bugs and made it faster !<br> Update it to make Ease automagically work on this computer. </p>
			<p><i class="fa fa-angle-down" aria-hidden="true"></i></p>
			<button class="install-button classicContent">Get Ease Addon</button>
			<button class="install-button safariUpdate">Update Ease Addon</button>
			<p id="moreInfoButton" style="margin-top: 5px;">Why is the addon necessary.</p>
			<p id="safariInfoButton" style="margin-top: 5px; display:none;">I already have the addon !</p>
		</div>
		<div class="safariHelper" id="afterdownload" style="display: none;">
			<h1 style="margin-top: 0px;">Final step</h1>
			<p style="font-size: 1.3em;">Once you have downloaded our addon <a href="https://safari-extensions.apple.com/details/?id=com.ease.easeextension-45P859NKD6" target="_blank">on the safari store</a>, just <a href="/">reload this page</a> :)</p>
		</div>
		<div class="safariHelper" id="alreadydownloaded" style="display: none;">
			<p style="font-size: 1.2em; margin-top:5px;">If you have already downloaded the addon, try to double click on "EaseExtension.safariextz" in your downloads folder to install it. Then <a href="/">reload this page</a>.
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
					<p>Ease uses an Ä<span>Addon</span> to <span>log you</span> on your favorite websitesÄù. It is a small piece of software that you add to your browser to customize its capabilities.
					</p>
				</div>
			</div>
			<div style="text-align:left;">
			<p>Without this addon, Ease can't fill the input to log you. The few datas stored in it are <span>fully encrypted</span> and are <span>never used for commercial purposes</span>.
			</p>
			<p>Downloading the addon is a <span>1 step</span> process directly from Ease.</p>
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
			
			/*$('#downloadExtension #safariInfoButton').click(function(){
				$('#downloadExtension .popupContent').css('display', 'none');
				$('#downloadExtension #alreadydownloaded').css('display', 'block');
			});
			$('#downloadExtension #returnButtonSafari').click(function(){
				$('#downloadExtension .popupContent').css('display', 'block');
				$('#downloadExtension #alreadydownloaded').css('display', 'none');				
			});*/
		</script>
