<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ attribute name="app" type="com.Ease.session.App" required="true"%>

<c:choose>
	<c:when test="${app.getType() eq 'NoAccount'}">
	<div class="siteLinkBox emptyApp"
	login=""
	webId="${app.getSite().getId()}"
	name="${app.getName()}"
	move="${app.havePerm('MOVE', servletContext)}"
	id="${app.getAppId()}">
	<div class="linkImage" onclick="sendEvent(this)">
		<div class="emptyAppIndicator" onclick="showModifyAppPopup(this, event)">
			<img src="resources/other/raise-your-hand-to-ask.svg" />
		</div>
		<div class="showAppActionsButton" >
			<i class="fa fa-cog"></i>
			<div class="appActionsPopup">
				<div class="buttonsContainer">
					<div class="modifyAppButton menu-item"
					onclick="showModifyAppPopup(this, event)">
					<p>Modify</p>
				</div>
				<c:if test="${app.havePerm('DELETE', servletContext)}">
				<div class="deleteAppButton menu-item"
				onclick="showConfirmDeleteAppPopup(this, event)">
				<p>Delete</p>
			</div>
		</c:if>
	</div>
</div>
</div>
<img class="logo" src="<c:out value='${app.getSite().getFolder()}logo.png'/>" />
</div>
</c:when>
<c:otherwise>
<c:if test="${app.getType() eq 'ClassicAccount'}">
<div class="siteLinkBox"
	 login="${app.getLogin()}"
	 webId="${app.getSite().getId()}"
	 name="${app.getName()}"
	 id="${app.getAppId()}"
	 ssoId="${app.getSite().getSso()}"
	 move="${app.havePerm('MOVE', servletContext)}"
	 logwith="false">
</c:if>
<c:if test="${app.getType() eq 'LogWithAccount'}">
<div class="siteLinkBox"
	 webId="${app.getSite().getId()}"
	 name="${app.getName()}"
	 id="${app.getAppId()}"
	 move="${app.havePerm('MOVE', servletContext)}"
	 logwith="${app.getAccount().getLogWithApp( user ).getAppId()}">
</c:if>
<div class="linkImage" onclick="sendEvent(this)">
	<div class="showAppActionsButton">
		<i class="fa fa-cog"></i>
		<div class="appActionsPopup">
			<div class="buttonsContainer">
				<div class="modifyAppButton menu-item"
				onclick="showModifyAppPopup(this, event)">
				<p>Modify</p>
			</div>
			<c:if test="${app.havePerm('DELETE', servletContext)}">
			<div class="deleteAppButton menu-item"
			onclick="showConfirmDeleteAppPopup(this, event)">
			<p>Delete</p>
		</div>
	</c:if>
</div>
</div>
</div>
<img class="logo" src="<c:out value='${app.getSite().getFolder()}logo.png'/>" />
</div>
</c:otherwise>
</c:choose>
<div class="siteName">
<p>${app.getName()}</p>
</div>
</div>
