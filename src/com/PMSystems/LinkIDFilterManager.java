package com.PMSystems;

import java.rmi.RemoteException;
import java.util.*;
import java.io.*;

import com.PMSystems.*;
import com.PMSystems.sejbs.*;
import com.PMSystems.beans.*;
import com.PMSystems.dbbeans.*;
import com.PMSystems.util.*;
import com.PMSystems.logger.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: </p>
 * @author Ahmad Suhaib
 * @version 1.0
 */

public class LinkIDFilterManager {

    public static Vector pmsRuleVec = new Vector();
    public static final String EVENT_TYPE_WEB_VISIT = "1";
    public static final String EVENT_TYPE_CAMPAIGN_URL = "2";
    public static final String EVENT_TYPE_BOTH = "3";//for webvisit & campaign urls

    private static AlphaQueryServer alphaServer;
    private static DataQueryServer dataServer;
    private static ComplexQueryServer complexQueryServer;
    private static ReportsQueryServer reportQueryServer;
    private static BMSQueryServer bmsServer;

    static{
	alphaServer = EJBHomesFactory.getAlphaQueryServerRemote();
	bmsServer = EJBHomesFactory.getBMSQueryServerRemote();
	dataServer = EJBHomesFactory.getDataQueryServerRemote();
	complexQueryServer = EJBHomesFactory.getComplexQueryServerRemote();
	reportQueryServer = EJBHomesFactory.getReportsQueryServerRemote();

	pmsRuleVec.add(new NameValue("=", "equal to"));
	//pmsRuleVec.add(new NameValue("!=", "not equal to"));
	pmsRuleVec.add(new NameValue("ct", "contains"));
    }

    private LinkIDFilterManager() {
    }

    /**
     * @param rule
     * @return
     */
    public static String getRuleName(String rule) {

	for(int i=0; i<pmsRuleVec.size(); i++) {
	    NameValue nv = (NameValue)pmsRuleVec.get(i);
	    if(nv.getName().equals(rule))
		return nv.getValue();
	}
	return "";
    }

    /**
     * @param eventType
     * @return
    public static String getEvenTypeName(String eventType) {

        eventType = Default.toDefault(eventType);

        if(eventType.equals(EVENT_TYPE_WEB_VISIT))
            return "Web Visit";
        else if(eventType.equals(EVENT_TYPE_CAMPAIGN_URL))
            return "Campaign URL";
        else if(eventType.equals(EVENT_TYPE_BOTH))
            return "WebVisit & CampaignURL";
        else
            return "";
    }     */

    /**
     * @param bean
     * @return
     */
    public static boolean createLinkIDFilter(LinkIDFilterBean bean) {

	try {
	    int filterNumber = dataServer.createLinkIDFilter(bean);
	    if(filterNumber>0) {
		bean.setFilterNumber(filterNumber);
		if(bean.getCampaignNumber()<=0)
		    reevaluateLinkLibraryToFilterMapping(bean.getUserID());//re-check the url-filter mapping.
		return true;
	    }
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return false;
    }

    /**
     * @param bean
     * @return
     */
    public static boolean updateLinkIDFilter(LinkIDFilterBean bean) {

	if(bean==null || bean.getFilterNumber()<=0)
	    return false;
	try {
	    if(dataServer.updateLinkIDFilter(bean)) {
		if(bean.getCampaignNumber()<=0) {
		    reevaluateLinkLibraryToFilterMapping(bean.getUserID());//re-check the url-filter mapping.
		}
		return true;
	    }
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return false;
    }

    /**
     * @param filterNumber
     */
    public static void reevaluateLinkLibraryToFilterMapping(String userId) {

	try {
	    Vector linkVec = dataServer.getLinkLibraryByUser(userId);
	    Vector filterVec = orderFiltersByMatchValueLengthDESC(dataServer.getLinkIDFilter(userId));


	    System.out.println("[LinkIDFilterManager]: Reevaluate URL-Filter Mapping, userID: "+userId);

	    for(int i=0; i<linkVec.size(); i++) {

		LinkLibraryBean libBean = (LinkLibraryBean)linkVec.get(i);
		processURLForFilters(libBean.getURL(), libBean.getWebTitle(), filterVec, userId, false);
	    }

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
    }

    /**
     * IMP: filterVec should be ordered via orderFiltersByMatchValueLengthDESC(), for sharpest filters at the top.
     *
     * @param url
     * @param filterVec
     */
    public static void processURLForFilters(String url, String webTitle, Vector filterVec, String userId, boolean ifMatchThenAdd) {

	try {
	    int filterNumber = 0;
	    int matchLength = 0;
	    for(int i=0; i<filterVec.size(); i++) {
		LinkIDFilterBean bean = (LinkIDFilterBean)filterVec.get(i);
		NameValue ruleNV = applyFilterRules(url, bean.getRuleVec());

		if(ruleNV!=null) {
		    filterNumber = bean.getFilterNumber();
		    matchLength = ruleNV.getValue().length();
		    break;
		}
	    }

	    if(ifMatchThenAdd)
		addToLinkLibrary(userId, url, webTitle, filterNumber);
	    else
		updateToLinkLibrary(userId, url, webTitle, filterNumber);

	    //System.out.println("["+filterNumber+"]: "+url);

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
    }

    /**
     * IMP: filterVec should be ordered via orderFiltersByMatchValueLengthDESC(), for sharpest filters at the top.
     *
     * @param url
     * @param filterVec
     * @return
     */
    public static LinkIDFilterBean matchFilterForWebURL(String url, Vector filterVec) {


	try {
	    int matchLength = 0;
	    LinkIDFilterBean matchBean = null;

	    for(int i=0; i<filterVec.size(); i++) {
		LinkIDFilterBean bean = (LinkIDFilterBean)filterVec.get(i);
		NameValue ruleNV = applyFilterRules(url, bean.getRuleVec());

		if(ruleNV!=null) {
		    matchLength = ruleNV.getValue().length();
		    matchBean = bean;
		    break;
		}
	    }
	    return matchBean;

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return null;
    }

    /**
     * @param url
     * @param ruleVec
     * @return
     */
    public static NameValue applyFilterRules(String url, Vector ruleVec) {

	try {
	    /**
	     * IMP: for now, ONLY ONE rule per filter is under consideration
	     */
	    for(int i=0; i<ruleVec.size(); i++) {
		NameValue nv = (NameValue)ruleVec.get(i);
		if(nv.getName().equals("=") && url.equalsIgnoreCase(nv.getValue())) {
		    return nv;

		} else if(nv.getName().equalsIgnoreCase("ct") && url.toLowerCase().indexOf(nv.getValue().toLowerCase())>=0) {
		    return nv;
		}
	    }
	} catch (Exception ex) { ; }
	return null;
    }

    /**
     * @param bean
     * @return
     */
    public static boolean deleteLinkIDFilter(int filterNum, String userId) {

	try {
	    LinkIDFilterBean filterBean = getLinkIDFilter(filterNum);
	    if(filterBean==null)
		return false;

	    if(dataServer.deleteLinkIDFilter(filterNum)) {

		if(filterBean.getCampaignNumber()<=0)
		    reevaluateLinkLibraryToFilterMapping(userId);
		return true;
	    }

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return false;
    }


    /**
     * @param userId
     * @return
     */
    public static Vector getUserLinkIDFilters(String userId) {

	try {
	    return dataServer.getLinkIDFilter(userId);

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }

    /**
     * @param userId
     * @param label
     * @return
     */
    public static boolean isLabelExists(String userId, String label, int filterNum) {

	try {
	    return dataServer.isLinkIDFilterExists(userId, label, filterNum);

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return false;
    }

    /**
     *
     * @param userId
     * @return
     */
    public static Vector getLinkIDFilterGroupByUser(String userId) {

	try {
	    return dataServer.getLinkIDFilterGroups(userId);

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }

    /**
     * @param filterNumber
     * @return
     */
    public static LinkIDFilterBean getLinkIDFilter(int filterNumber) {

	if(filterNumber<=0)
	    return null;
	try {
	    return dataServer.getLinkIDFilter(filterNumber);

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return null;
    }

    /**
     * @param bean
     * @return
     */
    public static boolean createLinkIDGroup(LinkIDFilterGroupBean bean) {

	try {
	    int groupId = dataServer.createLinkIDFilterGroup(bean);
	    if(groupId>0) {
		bean.setGroupID(groupId);
		return true;
	    }

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return false;
    }

    /**
     * @param bean
     * @return
     */
    public static boolean updateGroupScore(LinkIDFilterGroupBean bean) {

	try {
	    return dataServer.updateFilterGroupScore(bean);
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return false;
    }

    /**
     * @param userId
     * @param groupID
     * @return
     */
    public static boolean deleteLinkIDFilterGroup(String userId, int groupID) {

	try {
	    Vector filterVec = getLinkIDFilterGroupByUser(userId);
	    for(int i=0; i<filterVec.size(); i++) {

		LinkIDFilterGroupBean bean = (LinkIDFilterGroupBean)filterVec.get(i);
		if(bean.getGroupID()==groupID) {

		    if(dataServer.deleteLinkIDFilterGroup(bean.getGroupID())) {
			reevaluateLinkLibraryToFilterMapping(userId);
			return true;
		    }
		}
	    }
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return false;
    }

    /**
     * @param userId
     * @return
     */
    public static Vector getUserLinkLibrary(String userId) {

	try {
	    return dataServer.getLinkLibraryByUser(userId);

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }
    /**
     * 
     * @param userId
     * @param searchText
     * @param onlyUnassigned
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector searchLinkLibraryURLs(String userId, String searchText, boolean onlyUnassigned, int offset, int bucket) {
	
	if(userId.equals(""))
	    return new Vector();
	
	try {
	    return alphaServer.searchLinkLibraryURLs(userId, searchText, onlyUnassigned, offset, bucket);

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }
    /**
     * 
     * @param userId
     * @param searchText
     * @param onlyUnassigned
     * @return
     */
    public static int countLinkLibraryURLs(String userId, String searchText, boolean onlyUnassigned) {
	
	if(userId.equals(""))
	    return 0;
	
	try {
	    return alphaServer.countLinkLibraryURLs(userId, searchText, onlyUnassigned);

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return 0;
    }

    /**
     * @param userId
     * @param url
     * @param filterNumber
     * @return
     */
    public static boolean addToLinkLibrary(String userId, String url, String webTitle, int filterNumber) {

	try {
	    return dataServer.addToLinkLibraryByUser(userId, url, webTitle, filterNumber);

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return false;
    }

    /**
     * @param userId
     * @param url
     * @param filterNumber
     * @return
     */
    public static boolean updateToLinkLibrary(String userId, String url, String webTitle, int filterNumber) {

	try {
	    return dataServer.updateLinkLibrary(userId, url, webTitle, filterNumber);

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return false;
    }

    /**
     *
     * @param linkVec
     * @return
     */
    public static Vector getUnAssignedLinks(Vector linkVec) {

	Vector vec = new Vector();
	for(int i=0; linkVec!=null && i<linkVec.size(); i++) {
	    LinkLibraryBean bean = (LinkLibraryBean)linkVec.get(i);
	    if(bean.getFilterNumber()<=0) {
		vec.add(bean);
		linkVec.remove(i);
		i--;//object removed above, changed vec.size
	    }
	}
	return vec;
    }

    /**
     * @param userURLHash
     */
    public static void processForFilters(Hashtable userURLHash) {

	if(userURLHash==null && userURLHash.size()==0)
	    return;


	try {
	    Enumeration urlEnum = userURLHash.keys();
	    while(urlEnum.hasMoreElements()) {

		String userId = (String)urlEnum.nextElement();
		Vector urlNVVec = (Vector)userURLHash.get(userId);
		Vector urlVec = getNameVector(urlNVVec);
		Vector existingURLVec = new Vector();

		/**@todo parse new urls here, instead in DQSB. */
		urlVec = dataServer.fetchNonExistingLinkLibraryURL(userId, urlVec);

		//truncate urlNVVec using truncated urlVec.
		for(int i=0; i<urlNVVec.size(); i++) {
		    NameValue nv = (NameValue)urlNVVec.get(i);
		    if(!urlVec.contains(nv.getName())) {
			existingURLVec.add(urlNVVec.remove(i));
			i--;
		    }
		}

		if(urlNVVec!=null && urlNVVec.size()>0) {
		    Vector filterVec = orderFiltersByMatchValueLengthDESC(dataServer.getLinkIDFilter(userId));

		    for(int i=0; filterVec!=null && i<urlNVVec.size(); i++) {
			NameValue nv = (NameValue)urlNVVec.get(i);
			processURLForFilters(nv.getName(), nv.getValue(), filterVec, userId, true);
		    }
		}

		if(existingURLVec.size()>0)
		    dataServer.updateWebTitleForLinkLibrary(userId, existingURLVec);
	    }

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
    }

    private static Vector getNameVector(Vector nvVec) {

	Vector vec = new Vector();
	for(int i=0; i<nvVec.size(); i++)
	    vec.add(((NameValue)nvVec.get(i)).getName());

	return vec;
    }
    /**
     *
     * @param articleNumber
     * @param subNumVec
     * @return
     */
    public static Vector getWebVisitsForArticle(long articleNumber, Vector subNumVec) {
	return getWebVisitsForArticleByDateRange(articleNumber, subNumVec, 0, 0);
    }
    public static Vector getWebVisitsForArticleByDateRange(long articleNumber, Vector subNumVec, long startDate, long endDate) {

	try {
	    return dataServer.getWebVisitsForArticle(articleNumber, subNumVec, startDate, endDate);
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }

    /**
     *
     * @param subNum
     * @param campNum
     * @return
     */
    public static Vector getWebVisitsForCampaign(long subNum, long campNum) {
	return getWebVisitsForCampaignByDateRange(subNum, campNum, 0, 0);
    }
    public static Vector getWebVisitsForCampaignByDateRange(long subNum, long campNum, long startDate, long endDate) {

	try {
	    return dataServer.getWebVisitsForCampaign(subNum, campNum, startDate, endDate);
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }

    /**
     *
     * @param articleNumber
     * @param subNumVec
     * @return
     */
    public static Vector getWebVisitsCountForArticle(Vector subArtNumVec) {
	return getWebVisitsCountForArticleByDateRange(subArtNumVec, 0, 0);
    }
    public static Vector getWebVisitsCountForArticleByDateRange(Vector subArtNumVec, long startDate, long endDate) {

	try {
	    return dataServer.getWebVisitsCountForArticle(subArtNumVec, startDate, endDate);
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }
    /**
     * @param campaignNumer
     * @return
     */
    public static Vector getWebVisitsCountForCampaign(Vector campNumVec) {
	return getWebVisitsCountForCampaignByDateRange(campNumVec, 0, 0);
    }
    public static Vector getWebVisitsCountForCampaignByDateRange(Vector campNumVec, long startDate, long endDate) {

	try {
	    return dataServer.getWebVisitsCountForCampaign(campNumVec, startDate, endDate);
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }

    /**
     * @param campNum
     * @return
     */
    public static Vector getWebVisitSubscribersForCampaign(long campNum) {
	return getWebVisitSubscribersForCampaignByDateRange(campNum, 0, 0);
    }
    public static Vector getWebVisitSubscribersForCampaignByDateRange(long campNum, long startDate, long endDate) {

	try {
	    return dataServer.getWebVisitSubscribersForCampaign(campNum, startDate, endDate);
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }
    /**
     *
     * @param campNum
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector getWebVisitSubscribersForCampaign(long campNum, int offset, int bucket) {
	return getWebVisitSubscribersForCampaignByDateRange(campNum, offset, bucket, 0, 0);
    }
    public static Vector getWebVisitSubscribersForCampaignByDateRange(long campNum, int offset, int bucket, long startDate, long endDate) {

	try {
	    return dataServer.getWebVisitSubscribersForCampaign(campNum, offset, bucket, startDate, endDate);
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }
    /**
     *
     * @param campNumVec
     * @return
     */
    public static Hashtable getConvertedCountForCampaign(Vector campNumVec) {

	try {
	    return bmsServer.getConvertedCountForCampaign(campNumVec);
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Hashtable();
    }
    /**
     *
     * @param campNum
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector getConvertedSubscriberForCampaign(long campNum, int offset, int bucket) {

	try {
	    return bmsServer.getConvertedSubscriberForCampaign(campNum, offset, bucket);
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }

    /**
     *
     * @param subNum
     * @return
     */
    public static SubscriberInfo getSubscriber(String userId, long subNum) {

	try {
	    return dataServer.getSubscriber(userId, subNum);
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return null;
    }
    /**
     * @param userId
     * @param subscriberNumber
     * @param fromDate
     * @param toDate
     * @return
     */
    public static Vector getSubscriberWebVisitByDate(String userId, long subscriberNumber, Date fromDate, Date toDate) {

	try {
	    return dataServer.getSubscriberWebVisitByDate(userId,subscriberNumber,fromDate,toDate);

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }

    public static Vector getSubscriberWebVisitByDateAndCamp(String userId, long subscriberNumber, Date fromDate, Date toDate,boolean isWeb) {

	try {
	    return dataServer.getSubscriberWebVisitByDateAndCamp(userId,subscriberNumber,fromDate,toDate,isWeb);

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }

    /**
     *
     * @param userId
     * @return
     */
    public static int getWebVisitUniqueSubscriberCount(DashboardSAMBean samUserBean) {
	try {
	    if(samUserBean == null)
		return 0;
	    return reportQueryServer.getWebVisitSubscriberCount(samUserBean);
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return 0;
    }

    /**
     * @param userId
     * @return
     */
    public static Vector getSAMUserLinkLibrary(DashboardSAMBean samBean) {

	try {
	    if(samBean == null || samBean.getUserId() == null)
		return new Vector();

	    Vector vec = dataServer.getLinkLibraryByUser(samBean.getUserId());
	    if(samBean.getMasterUserId() == null || samBean.getMasterUserId().equals(""))
		return vec;

	    Vector masterLinkVec = dataServer.getLinkLibraryByUser(samBean.getMasterUserId());
	    vec.addAll(masterLinkVec);
	    return vec;

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }

    /**
     *
     * @param userId
     * @param subscriberNumber
     * @param offset
     * @param batch
     * @return
     */
    public static Vector getSubscriberWebVisitByLimit(String userId, long subscriberNumber, int offset, int batch) {

	try {
	    return dataServer.getSubscriberWebVisitByLimit(userId,subscriberNumber,offset,batch);

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }

    /**
     *
     * @param filterVec
     * @return
     */
    public static Vector orderFiltersByMatchValueLengthDESC(Vector filterVec) {

	Vector orderVec = new Vector();
	if(filterVec==null || filterVec.isEmpty())
	    return filterVec;

	try {
	    Vector nvVec = new Vector();
	    for(int i=0; i<filterVec.size(); i++) {
		LinkIDFilterBean bean = (LinkIDFilterBean)filterVec.get(i);
		if(bean.getRuleVec().size()>0)
		    nvVec.add(new NameValue(""+((NameValue)bean.getRuleVec().get(0)).getValue().length(), ""+bean.getFilterNumber()));
		else
		    nvVec.add(new NameValue("0", ""+bean.getFilterNumber()));
	    }

	    Collections.sort(nvVec, Collections.reverseOrder());

	    for(int i=0; i<nvVec.size(); i++) {
		NameValue nv = (NameValue)nvVec.get(i);
		for(int j=0; j<filterVec.size(); j++) {
		    LinkIDFilterBean bean = (LinkIDFilterBean)filterVec.get(j);
		    if(bean.getFilterNumber()==Default.defaultInt(nv.getValue()))
			orderVec.add(bean);
		}
	    }

	    return orderVec;

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return filterVec;
    }
    /**
     *
     * @param campNum
     * @return
     */
    public static LinkIDFilterBean getLinkIDFilterByCampNum(long campNum) {

	if(campNum<=0)
	    return null;
	try {
	    return dataServer.getLinkIDFilterByCampNum(campNum);

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return null;
    }
    /**
     * @param campNumVec
     * @return
     */
    public static Hashtable getLinkIDFilterNumByCampNum(Vector campNumVec) {

	if(campNumVec.isEmpty())
	    return new Hashtable();
	try {
	    return dataServer.getLinkIDFilterNumByCampNum(campNumVec);

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Hashtable();
    }
    /**
     * Remove Conversin Filters
     */
    public static void removeConversionFilters(Vector filterVec) {

	if(filterVec.isEmpty())
	    return;

	for(int i=0; i<filterVec.size(); i++) {
	    LinkIDFilterBean bean = (LinkIDFilterBean)filterVec.get(i);
	    if(bean.getCampaignNumber()>0) {
		filterVec.remove(i);
		--i;
	    }
	}
    }
    /**
     * @param userId
     * @param urlVec
     * @return
     */
    public static Hashtable getURLTitleHash(String userId, Vector urlVec) {

	if(userId.equals("") || urlVec.isEmpty())
	    return new Hashtable();

	try {
	    return dataServer.getURLTitleHash(userId, urlVec);

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Hashtable();
    }
    /**
     *
     * @param userId
     * @param pageNameVec
     * @return
     */
    public static Hashtable getPageNameURLHash(String userId, Vector pageNameVec) {

	if(userId.equals("") || pageNameVec.isEmpty())
	    return new Hashtable();

	try {
	    return alphaServer.getPageNameURLHash(userId, pageNameVec);

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Hashtable();
    }

    /**
     * 
     * @param subNum
     * @param campNum
     * @param articleNum
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getSubscriberWebVisitsCount(long subNum, long campNum, long articleNum, long startDate, long endDate) {

	if(subNum<=0)
	    return 0;

	try {  		
	    return dataServer.getSubscriberWebVisitsCount(subNum, campNum, articleNum, startDate, endDate);

	} catch(Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;    	   	
    }

    /**
     * 
     * @param subNum
     * @param campNum
     * @param articleNum
     * @param startDate
     * @param endDate
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector getSubscriberWebVisits(long subNum, long campNum, long articleNum, long startDate, long endDate, int offset, int bucket) {

	if(subNum<1)
	    return new Vector();

	try {  		
	    return dataServer.getSubscriberWebVisits(subNum, campNum, articleNum, startDate, endDate,offset,bucket);

	} catch(Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();    	   	
    }


}