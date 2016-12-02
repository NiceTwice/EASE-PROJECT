<!--<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ attribute name="app" type="com.Ease.session.App" required="true"%>
<%@ attribute name="informations" type="java.util.Map" required="false" %>
<c:choose>
	<c:when test="${app.getType() eq 'NoAccount'}">
	<div class="siteLinkBox emptyApp"
	login=""
	webId="${app.getSite().getId()}"
	name="${app.getName()}"
	move="${app.havePerm('MOVE', servletContext)}"
	logWith="${app.getDataLogin()}"
	ssoId="${app.getSite().getSso()}"
	
	id="${app.getAppId()}">
	<div class="linkImage">
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
						<div class="deleteAppButton menu-item" onclick="showConfirmDeleteAppPopup(this, event)">
							<p>Delete</p>
						</div>
					</c:if>
				</div>
			</div>
		</div>
		<img class="logo" src="<c:out value='${app.getSite().getFolder()}logo.png'/>" />
	</div>
</c:when>
<c:when test="${app.getType() eq 'LinkAccount'}">
	<div class="siteLinkBox"
	 name="${app.getName()}"
	 id="${app.getAppId()}"
	 move="${app.havePerm('MOVE', servletContext)}"
	 link="${app.getAccount().getLink()}">
	<div class="linkImage">
	<c:if test="${app.havePerm('DELETE', servletContext)}">
	<div class="showAppActionsButton">
		<i class="fa fa-cog"></i>
		<div class="appActionsPopup">
			<div class="buttonsContainer">
			
				<div class="deleteAppButton menu-item"
					onclick="showConfirmDeleteAppPopup(this, event)">
					<p>Delete</p>
				</div>
			
			</div>
		</div>
	</div>
	</c:if>
	<img class="logo" src="resources/websites/Calendrier/logo.png" />
	</div>
</c:when>
<c:otherwise>
<c:if test="${app.getType() eq 'ClassicAccount'}">
<div class="siteLinkBox"
	<c:forEach items="${informations}" var="entry">
		${entry.key}="${entry.value}"
	</c:forEach>
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
<div class="linkImage">
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
-->