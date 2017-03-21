package com.Ease.Context.Catalog;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONObject;

import com.Ease.Utils.GeneralException;
import com.Ease.Utils.ServletManager;

public class WebsitesVisitedManager {
	
	protected List<WebsiteVisited> websitesVisited;
	protected Map<String, WebsiteVisited> websitesVisitedDbMap;
	protected Map<Integer, WebsiteVisited> websitesVisitedIdMap;
	protected List<BlacklistedWebsite> blacklistedWebsites;
	protected Map<String, BlacklistedWebsite> blacklistedWebsitesDbMap;
	protected Map<Integer, BlacklistedWebsite> blacklistedWebsitesIdMap;
	
	public WebsitesVisitedManager(ServletManager sm) throws GeneralException {
		this.websitesVisited = WebsiteVisited.loadWebsitesVisited(sm);
		for(WebsiteVisited websiteVisited : websitesVisited)
			this.addWebsiteVisitedInMaps(websiteVisited);
		this.blacklistedWebsites = BlacklistedWebsite.loadBlacklistedWebsites(sm);
		for (BlacklistedWebsite blacklistedWebsite : blacklistedWebsites)
			this.addBlacklistedWebsiteInMaps(blacklistedWebsite);
	}
	
	/**
	 * Create or update a WebsiteVisited instance and put it in list and maps
	 * Then sort the websitesVisited list
	 * @param String url
	 * @param int count
	 * @param ServletManager sm
	 * @throws GeneralException when insertion statement fail
	 */
	public void addWebsiteVisited(String url, int count, ServletManager sm) throws GeneralException {
		if (this.isWebsiteBlacklisted(url))
			return;
		WebsiteVisited existingWebsiteVisited = this.findWebsiteVisitedWithUrl(url);
		if (existingWebsiteVisited == null)
			this.addWebsiteVisited(WebsiteVisited.createWebsiteVisited(url, count, sm));
		else
			existingWebsiteVisited.increaseCount(count, sm);
		Collections.sort(this.websitesVisited, new Comparator<WebsiteVisited>() {
			public int compare(WebsiteVisited w1, WebsiteVisited w2) {
				return w1.compareTo(w2);
			}
		});
	}
	
	/**
	 * Add a WebsiteVisited in list and maps
	 * @param WebsiteVisited websiteVisited
	 */
	private void addWebsiteVisited(WebsiteVisited websiteVisited) {
		this.websitesVisited.add(websiteVisited);
		this.addWebsiteVisitedInMaps(websiteVisited);
	}
	
	/**
	 * Add a WebsiteVisited instance in maps
	 * @param WebsiteVisited websiteVisited
	 */
	private void addWebsiteVisitedInMaps(WebsiteVisited websiteVisited) {
		this.websitesVisitedDbMap.put(websiteVisited.getDb_id(), websiteVisited);
		this.websitesVisitedIdMap.put(websiteVisited.getSingle_id(), websiteVisited);
	}
	
	/**
	 * Get a WebsiteVisited instance by db_id
	 * @param String db_id
	 * @return WebsiteVisited
	 * @throws GeneralException when db_id does not correspond to any key of websitesVisitedDbMap
	 */
	public WebsiteVisited getWebsiteVisitedWithDbId(String db_id) throws GeneralException {
		WebsiteVisited websiteVisited = this.websitesVisitedDbMap.get(db_id);
		if (websiteVisited == null)
			throw new GeneralException(ServletManager.Code.ClientError, "This WebsiteVisited does not exist");
		return websiteVisited;
	}
	
	/**
	 * Get a WebsiteVisited instance by single_id
	 * @param int single_id
	 * @return WebsiteVisited
	 * @throws GeneralException when single_id does not correspond to any key of websitesVisitedIdMap
	 */
	public WebsiteVisited getWebsiteVisitedWithSingleId(int single_id) throws GeneralException {
		WebsiteVisited websiteVisited = this.websitesVisitedIdMap.get(single_id);
		if (websiteVisited == null)
			throw new GeneralException(ServletManager.Code.ClientError, "This WebsiteVisited does not exist");
		return websiteVisited;
	}
	
	/**
	 * Get a WebsiteVisited instance by url
	 * @param url
	 * @return WebsiteVisited instance if it exists else return null
	 */
	public WebsiteVisited findWebsiteVisitedWithUrl(String url) {
		for(WebsiteVisited websiteVisited : this.websitesVisited) {
			if (websiteVisited.getUrl().equals(url))
				return websiteVisited;
		}
		return null;
	}
	
	/**
	 * Remove a WebsiteVisited instance from list, maps and database
	 * @param WebsiteVisited websiteVisited
	 * @param ServletManager sm
	 * @throws GeneralException when delete statement fail
	 */
	public void removeWebsiteVisited(WebsiteVisited websiteVisited, ServletManager sm) throws GeneralException {
		websiteVisited.removeFromDb(sm);
		this.websitesVisited.remove(websiteVisited);
		this.websitesVisitedDbMap.remove(websiteVisited.getDb_id());
		this.websitesVisitedIdMap.remove(websiteVisited.getSingle_id());
	}
	
	/**
	 * Remove a WebsiteVisited instance from list, maps and database
	 * @param String db_id
	 * @param ServletManager sm
	 * @throws GeneralException when db_id is incorrect or delete statement fail
	 */
	public void removeWebsiteVisitedWithDbId(String db_id, ServletManager sm) throws GeneralException {
		WebsiteVisited websiteVisited = this.getWebsiteVisitedWithDbId(db_id);
		this.removeWebsiteVisited(websiteVisited, sm);
	}
	
	/**
	 * Remove a WebsiteVisited instance from list, maps and database
	 * @param int single_id
	 * @param ServletManager sm
	 * @throws GeneralException when single_id is incorrect or delete statement fail
	 */
	public void removeWebsiteVisitedWithSingleId(int single_id, ServletManager sm) throws GeneralException {
		WebsiteVisited websiteVisited = this.getWebsiteVisitedWithSingleId(single_id);
		this.removeWebsiteVisited(websiteVisited, sm);
	}

	/**
	 * Create a BlacklistedWebsite instance and call put it in list and maps
	 * @param String url
	 * @param ServletManager sm
	 * @throws GeneralException when database insert fail
	 */
	public void addBlackListedWebsite(String url, ServletManager sm) throws GeneralException {
		this.addBlacklistedWebsite(BlacklistedWebsite.createBlacklistedWebsite(url, sm));
	}
	
	/**
	 * Add a BlacklistedWebsite instance in list and maps
	 * @param BlacklistedWebsite blacklistedWebsite
	 */
	private void addBlacklistedWebsite(BlacklistedWebsite blacklistedWebsite) {
		this.blacklistedWebsites.add(blacklistedWebsite);
		this.addBlacklistedWebsiteInMaps(blacklistedWebsite);
	}
	
	/**
	 * Put a BlacklistedWebsite instance in maps
	 * @param BlacklistedWebsite blacklistedWebsite
	 */
	private void addBlacklistedWebsiteInMaps(BlacklistedWebsite blacklistedWebsite) {
		this.blacklistedWebsitesDbMap.put(blacklistedWebsite.getDb_id(), blacklistedWebsite);
		this.blacklistedWebsitesIdMap.put(blacklistedWebsite.getSingle_id(), blacklistedWebsite);
	}
	
	/**
	 * Delete a BlacklistedWebsite instance from list, maps and database
	 * @param BlacklistedWebsite blacklistedWebsite
	 * @param ServletManager sm
	 * @throws GeneralException when delete statement fail
	 */
	public void removeBlacklistedWebsite(BlacklistedWebsite blacklistedWebsite, ServletManager sm) throws GeneralException {
		blacklistedWebsite.removeFromDb(sm);
		this.blacklistedWebsites.remove(blacklistedWebsite);
		this.blacklistedWebsitesDbMap.remove(blacklistedWebsite.getDb_id());
		this.blacklistedWebsitesIdMap.remove(blacklistedWebsite.getSingle_id());
	}
	
	/**
	 * Delete a BlacklistedWebsite instance from list, maps and database
	 * @param int single_id
	 * @param ServletManager sm
	 * @throws GeneralException when single_id is incorrect or delete statement fail
	 */
	public void removeBlacklistedWebsiteWithSingleId(int single_id, ServletManager sm) throws GeneralException {
		BlacklistedWebsite blacklistedWebsite = this.getBlacklistedeWebsiteWithSingleId(single_id);
		this.removeBlacklistedWebsite(blacklistedWebsite, sm);
	}
	
	/**
	 * Delete a BlacklistedWebsite instance from list, maps and database
	 * @param String db_id
	 * @param ServletManager sm
	 * @throws GeneralException when db_id is incorrect or delete statement fail
	 */
	public void removeBlacklistedWebsiteWithDbId(String db_id, ServletManager sm) throws GeneralException {
		BlacklistedWebsite blacklistedWebsite = this.getBlacklistedWebsiteWithDbId(db_id);
		this.removeBlacklistedWebsite(blacklistedWebsite, sm);
	}
	
	/**
	 * Get a BlacklistedWebsite instance by db_id
	 * @param String db_id
	 * @return BlacklistedWebsite blacklistedWebsite
	 * @throws GeneralException when db_id does not correspond to any key of blacklistedWebsitesDbMap
	 */
	public BlacklistedWebsite getBlacklistedWebsiteWithDbId(String db_id) throws GeneralException {
		BlacklistedWebsite blacklistedWebsite = this.blacklistedWebsitesDbMap.get(db_id);
		if (blacklistedWebsite == null)
			throw new GeneralException(ServletManager.Code.ClientError, "This blacklisted website id does not exist");
		return blacklistedWebsite;
	}
	
	/**
	 * Get a BlacklistedWebsite instance by single_id
	 * @param int single_id
	 * @return BlacklistedWebsite blacklistedWebsite
	 * @throws GeneralException when single_id does not correspond to any key of blacklistedWebsitesIdMap
	 */
	public BlacklistedWebsite getBlacklistedeWebsiteWithSingleId(int single_id) throws GeneralException {
		BlacklistedWebsite blacklistedWebsite = this.blacklistedWebsitesIdMap.get(single_id);
		if (blacklistedWebsite == null)
			throw new GeneralException(ServletManager.Code.ClientError, "This blacklisted website id does not exist");
		return blacklistedWebsite;
	}
	
	/* Utils */

	/**
	 * Check if given url is in blacklist
	 * @param String url
	 * @return boolean
	 */
	private boolean isWebsiteBlacklisted(String url) {
		for(BlacklistedWebsite blacklistedWebsite : this.blacklistedWebsites) {
			if (blacklistedWebsite.getUrl().equals(url))
				return true;
		}
		return false;
	}
	
	/**
	 * Iterate on a maps of url and count to add and/or update websites visited
	 * @param JSONObject websitesVisited which is a map or String and Integer
	 * @param ServletManager sm
	 * @throws GeneralException when insert or update statement fail
	 */
	@SuppressWarnings("unchecked")
	public void addWebsitesVisitedFromJson(JSONObject websitesVisited, ServletManager sm) throws GeneralException {
		for (Object obj : websitesVisited.entrySet()) {
			Entry<Object, Object> entry = (Entry<Object, Object>) obj;
			String url = (String)entry.getKey();
			int count = Integer.parseInt((String)entry.getValue());
			this.addWebsiteVisited(url, count, sm);
		}
	}
	
	/**
	 * Send a websiteVisited to the blacklist and delete it
	 * @param WebsiteVisited websiteVisited
	 * @param ServletManager sm
	 * @throws GeneralException when insert or delete statement fail
	 */
	public void sendWebsiteVistedToBlacklist(WebsiteVisited websiteVisited, ServletManager sm) throws GeneralException {
		this.addBlackListedWebsite(websiteVisited.getUrl(), sm);
		this.removeWebsiteVisited(websiteVisited, sm);
	}
	
	/**
	 * Send a websiteVisited to the blacklist and delete it
	 * @param int single_id
	 * @param ServletManager sm
	 * @throws GeneralException when single_id is not a correct key and when delete or insert statement fail
	 */
	public void sendWebsiteVisitedToBlacklistWithSingleId(int single_id, ServletManager sm) throws GeneralException {
		WebsiteVisited websiteVisited = this.getWebsiteVisitedWithSingleId(single_id);
		this.sendWebsiteVistedToBlacklist(websiteVisited, sm);
	}
	
	/**
	 * Send a websiteVisited to the blacklist and delete it
	 * @param Srting db_id
	 * @param ServletManager sm
	 * @throws GeneralException when db_id is not a correct key and when delete or insert statement fail
	 */
	public void sendWebsiteVisitedToBlacklistWithDbId(String db_id, ServletManager sm) throws GeneralException {
		WebsiteVisited websiteVisited = this.getWebsiteVisitedWithDbId(db_id);
		this.sendWebsiteVistedToBlacklist(websiteVisited, sm);
	}
}
