package com.PMSystems;

import java.rmi.RemoteException;
import java.util.*;
import java.io.*;

import com.PMSystems.*;
import com.PMSystems.sejbs.*;
import com.PMSystems.template.TemplateUtils;
import com.PMSystems.beans.*;
import com.PMSystems.dbbeans.*;
import com.PMSystems.util.*;
import com.PMSystems.logger.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: </p>
 * @author Ahmad Suhaib
 * @version 1.0
 */

public class FilterManager {

    private static AlphaQueryServer alphaServer;
    private static DataQueryServer dataServer;
    private static ComplexQueryServer complexQueryServer;
    private static ComplexQueryServer2 complexQueryServer2;

    static{
	alphaServer = EJBHomesFactory.getAlphaQueryServerRemote();
	dataServer = EJBHomesFactory.getDataQueryServerRemote();
	complexQueryServer = EJBHomesFactory.getComplexQueryServerRemote();
	complexQueryServer2 = EJBHomesFactory.getComplexQueryServer2Remote();
    }

    private FilterManager() {
    }
    
    /**
     * 
     * @param userId
     * @param filterNumber
     * @return
     */
    public static boolean runBehaviourFilterPopulationCount(String userId, int filterNumber) {
	
	try {
	    return complexQueryServer2.runBehaviourFilterPopulationCount(userId, filterNumber);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }
    /**
     * 
     * @param userId
     * @param filterNumber
     * @return
     */
    public static boolean stopBehaviourFilterPopulationCount(String userId, int filterNumber) {
	
	try {
	    return complexQueryServer2.stopBehaviourFilterPopulationCount(userId, filterNumber);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }
    /**
     * 
     * @param userId
     * @param filterNumber
     * @return
     */
    public static Vector countFilterRespondersPercentage(String userId, int filterNumber) {
	
	try {
	    return alphaServer.countFilterRespondersPercentage(userId, filterNumber);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     * 
     * @param userId
     * @param filterNumber
     * @param searchText
     * @return
     */
    public static int countSubsByFilterNumber(String userId, int filterNumber, String searchText) {
	
	try {
	    return alphaServer.countSubsByFilterNumber(userId, filterNumber, searchText);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;
    }
    /**
     * 
     * @param userId
     * @param filterNumber
     * @param searchText
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector getSubsByFilterNumber(String userId, int filterNumber, String searchText, int offset, int bucket) {
	
	try {
	    return alphaServer.getSubsByFilterNumber(userId, filterNumber, searchText, offset, bucket);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }

    /**
     * @param userId
     * @param name
     * @param filterNumber
     * @return
     */
    public static boolean isBehaviourFilterExists(String userId, String name, int filterNumber) {

	try {
	    return complexQueryServer2.isBehaviourFilterExists(userId, name, filterNumber);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }

    /**
     * @param bean
     * @return
     */
    public static boolean createBehaviourFilter(BehaviourFilterBean bean) {

	try {
	    int filterNumber = complexQueryServer2.createBehaviourFilter(bean);
	    if(filterNumber>0) {
		bean.setFilterNumber(filterNumber);
		return true;
	    }

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }

    /**
     * @param filterNumber
     * @param filterVec
     * @return
     */
    public static boolean createUpdateBehaviourFilterDetail(int filterNumber, Vector filterVec) {

	try {
	    return complexQueryServer2.createUpdateBehaviourFilterDetail(filterNumber, filterVec);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }

    /**
     * @param bean
     * @return
     */
    public static boolean updateBehaviourFilter(BehaviourFilterBean bean) {

	try {
	    return complexQueryServer2.updateBehaviourFilter(bean);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }
    /**
     * 
     * @param filterNumber
     * @param tagVec
     * @return
     */
    public static boolean createUpdateBehaviorFilterTags(int filterNumber, Vector tagVec) {

	try {
	    return complexQueryServer2.createUpdateBehaviorFilterTags(filterNumber, tagVec);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }

    /**
     * 
     * @param userId
     * @param newName
     * @param filterNumber
     * @return
     */
    public static boolean changeBehaviourFilterName(String newName, int filterNumber) {

	try {
	    return complexQueryServer2.changeBehaviourFilterName(newName, filterNumber);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }
    /**
     *
     * @param filterNum
     * @return
     * @throws RemoteException
     */
    public static BehaviourFilterBean getBehaviourFilterByNum(int filterNum) {

	try {
	    return complexQueryServer2.getBehaviourFilterByNum(filterNum);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return null;
    }
    /**
     * 
     * @param filterNumVec
     * @return
     */
    public static Vector getBehaviourFilterByNum(Vector filterNumVec) {
	
	try {
	    return complexQueryServer2.getBehaviourFilterByNum(filterNumVec);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     * @param filterNum
     * @return
     */
    public static WorkflowSegmentFilterBean getWorkflowSegmentFilterBean(int filterNum) {

	if(filterNum<=0)
	    return null;
	try {
	    BehaviourFilterBean filterBean = complexQueryServer2.getBehaviourFilterByNum(filterNum);
	    if(filterBean==null)
		return null;

	    WorkflowSegmentFilterBean segFilterBean = new WorkflowSegmentFilterBean();
	    segFilterBean.setBehaviorFilterBean(filterBean);
	    return segFilterBean;

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return null;
    }
    /**
     * @param filterBean
     * @return
     */
    public static int createUpdateWorkflowSegmentFilterBean(WorkflowSegmentFilterBean segFilterBean) {

	if(segFilterBean==null || segFilterBean.getBehaviorFilterBean()==null)
	    return 0;
	try {
	    if(segFilterBean.getBehaviorFilterBean().getFilterNumber()>0) {
		if(updateBehaviourFilter(segFilterBean.getBehaviorFilterBean())) {
		    return segFilterBean.getBehaviorFilterBean().getFilterNumber();
		} else {
		    System.out.println("[FilterManager]: WF_SEG_BEHV_FILTER + UPDATE_ERROR unable to update 'Workflow Segment Behavior Filter', filterNum:"
			    +segFilterBean.getBehaviorFilterBean().getFilterNumber());
		    return segFilterBean.getBehaviorFilterBean().getFilterNumber();
		}
	    } else {
		if(createBehaviourFilter(segFilterBean.getBehaviorFilterBean())) {
		    return segFilterBean.getBehaviorFilterBean().getFilterNumber();
		} else {
		    System.out.println("[FilterManager]: WF_SEG_BEHV_FILTER + CREATE_ERROR unable to create 'Workflow Segment Behavior Filter'");
		    return 0;
		}
	    }

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;
    }
    /**
     *
     * @param userId
     * @return
     */
    public static Vector getBehaviourFilterByUserId(String userId) {
	return getBehaviourFilterByUserId(userId, PMSDefinitions.BEHAVIOR_FILTER_FOR_NURTURE_TRACKS);
    }
    /**
     * @param userId
     * @return
     */
    public static Vector getBehaviourFilterByUserId(String userId, String filterFor) {

	try {
	    return complexQueryServer2.getBehaviourFilterByUserId(userId, filterFor);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     * 
     * @param userId
     * @param filterFor
     * @param searchText
     * @return
     */
    public static int countBehaviourFilterByUserId(String userId, String filterFor, String searchText) {
	
	try {
	    return complexQueryServer2.countBehaviourFilterByUserId(userId, filterFor, searchText);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;
    }
    /**
     * 
     * @param userId
     * @param filterFor
     * @param searchText
     * @param offset
     * @param BUCKET_SIZE
     * @return
     */
    public static Vector getBehaviourFilterByUserId(String userId, String filterFor, String searchText, int offset, int BUCKET_SIZE) {
	
	try {
	    return complexQueryServer2.getBehaviourFilterByUserId(userId, filterFor, searchText, offset, BUCKET_SIZE);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     * @param filterNum
     * @return
     */
    public static boolean deleteBehaviourFilter(int filterNum) {

	try {
	    return complexQueryServer2.deleteBehaviourFilter(filterNum);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }

    /**
     * Return default valid type, if passed type is invalid.
     *
     * @param addFilterType
     * @return
     */
    public static String getValidFilterType(String addFilterType) {

	addFilterType = Default.toDefault(addFilterType).trim();

	if(addFilterType.equalsIgnoreCase(PMSDefinitions.BEHAVIOUR_FILTER_FORM_SUBMISSION))
	    return PMSDefinitions.BEHAVIOUR_FILTER_FORM_SUBMISSION;

	else if(addFilterType.equalsIgnoreCase(PMSDefinitions.BEHAVIOUR_FILTER_LIST))
	    return PMSDefinitions.BEHAVIOUR_FILTER_LIST;

	else if(addFilterType.equalsIgnoreCase(PMSDefinitions.BEHAVIOUR_FILTER_SCORE))
	    return PMSDefinitions.BEHAVIOUR_FILTER_SCORE;

	else if(addFilterType.equalsIgnoreCase(PMSDefinitions.BEHAVIOUR_FILTER_WEB_VISIT))
	    return PMSDefinitions.BEHAVIOUR_FILTER_WEB_VISIT;

	else if(addFilterType.equalsIgnoreCase(PMSDefinitions.BEHAVIOUR_FILTER_SEGMENT))
	    return PMSDefinitions.BEHAVIOUR_FILTER_SEGMENT;

	else if(addFilterType.equalsIgnoreCase(PMSDefinitions.BEHAVIOUR_FILTER_PROFILE))
	    return PMSDefinitions.BEHAVIOUR_FILTER_PROFILE;

	else
	    return PMSDefinitions.BEHAVIOUR_FILTER_EMAIL;
    }
    /**
     *
     * @param filterNumber
     * @param deleteTriggerOrder
     * @return
     */
    public static boolean deleteFilterDetail(int filterNumber, int deleteTriggerOrder) {

	try {
	    BehaviourFilterBean bean = getBehaviourFilterByNum(filterNumber);
	    if(bean==null)
		return false;

	    BehaviourFilterDetailBean detailBean = bean.getFilterByTriggerOrder(deleteTriggerOrder);
	    if(detailBean==null)
		return true;//considering a new filterType which was not saved.

	    return complexQueryServer2.deleteBehaviourFilterDetail(filterNumber, deleteTriggerOrder);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }
    /**
     *
     * @param detailBean
     * @return
     */
    public static String getBehaviorFilterSummaryHTML(String userId, BehaviourFilterBean filterBean) {

	//===== Add Segment Filter's HTML output logic ============

	StringBuffer buff = new StringBuffer("");
	if(filterBean==null || filterBean.getFilterVec().isEmpty() || filterBean.getUserID().equals("")) {
	    System.out.println("[FitlerManager]: HTML_CODE invalid values found");
	    return buff.toString();
	}

	Vector groupVec = LinkIDFilterManager.getLinkIDFilterGroupByUser(filterBean.getUserID());
	Vector userFilterVec = LinkIDFilterManager.getUserLinkIDFilters(filterBean.getUserID());
	String vam = "vertical-align:middle;";
	String info = "color:BLUE;";

	try {
	    buff.append("\n");
	    buff.append("<table width=100% cellpadding=1 cellspacing=1 class='listSegPageWrapper'>\n");
	    buff.append("<tr><td style='padding-bottom:7px;"+info+vam+"'>");
	    buff.append(filterBean.getApplyRuleCount().equalsIgnoreCase("A")? "All":"One");
	    buff.append(" of the advanced filters below were met.<br>&nbsp;</td></tr>\n");
	    buff.append("\n");

	    for(int i=0; filterBean!=null && i<filterBean.getFilterVec().size(); i++) {

		BehaviourFilterDetailBean detailBean = (BehaviourFilterDetailBean)filterBean.getFilterVec().get(i);
		buff.append("<tr><td style='"+vam+"background-color:#E8E8E8;height:28px;border-bottom:2px solid #EEE;'> ");

		//==== Email Filter ======
		if(detailBean.getFilterType().equalsIgnoreCase(PMSDefinitions.BEHAVIOUR_FILTER_EMAIL)) {

		    CampaignDataBean campBean = CampaignManager.getCampaignData(detailBean.getCampaignNumber());
		    String campName = (campBean==null)? "[campaign not found]": Default.secureInput(campBean.getCampaignName());
		    campName = (detailBean.getCampaignNumber()==-1)? "Any Campaign": campName;

		    ArticleDataBean artBean = new ArticleDataBean();
		    if(detailBean.getEmailFilterBy().equalsIgnoreCase("CK") && detailBean.getArticleNumber()>0) {
			artBean = fetchArticle(detailBean.getArticleNumber(), CampaignManager.getArticles(detailBean.getCampaignNumber()));
		    }

		    buff.append("&nbsp;&nbsp;<b>Email Filter:</b> '"+campName+"'&nbsp;\n\n");

		    if(detailBean.getCampaignNumber()>0) {
			buff.append("\n<a href=\"javascript:preview('"+PMSEncoding.encode(""+detailBean.getCampaignNumber()));
			buff.append("');\" title=\"Preview Email\">\n");
			buff.append("<img src=\"/pms/img/newui/magnify_sub.gif\" title=\"Preview Email\" style='"+vam+"'></a>\n&nbsp;");
		    }
		    buff.append("\n\n\t\n\t<b>");
		    buff.append(detailBean.getEmailFilterBy().equalsIgnoreCase("CK")? "clicked "
			    :(detailBean.getEmailFilterBy().equalsIgnoreCase("NC")? "non-clickers":"opened"));
		    buff.append('\n');

		    if(detailBean.getEmailFilterBy().equalsIgnoreCase("CK") && detailBean.getArticleNumber()==-1) {
			buff.append("\n&nbsp;on any article&nbsp;\n\n");

		    } else if(detailBean.getEmailFilterBy().equalsIgnoreCase("CK") && detailBean.getArticleNumber()>0) {
			buff.append("\non <a href=\""+Default.secureInput(artBean.getArticleURL())+"\"\n");
			buff.append("  title=\""+Default.secureInput(artBean.getLinkID()+" --- "+artBean.getArticleURL()).trim());
			buff.append("\" target=\"_blank\">\n"+Default.secureInput(Default.subString(artBean.getArticleURL(), 40, " ...")));
			buff.append("</a>\n\n");
		    }
		    buff.append("</b>\n\t<span style='color:BLUE;'>\n\t");
		    buff.append(" "+(detailBean.isFrequency()? detailBean.getFrequency()+" or more times":"")).append("\n");
		    buff.append("	");
		    buff.append(detailBean.isTimeSpanApplied()? " in last "+detailBean.getTimeSpanInDays()+" day(s)":"");
		    buff.append("</span>\n").append("       ");

		    //==== ScoreChange Filter ======
		} else if(detailBean.getFilterType().equalsIgnoreCase(PMSDefinitions.BEHAVIOUR_FILTER_SCORE)) {

		    buff.append("&nbsp;&nbsp;\n\t");
		    buff.append("<b>Score</b> "+getBehaviorScoreFilterLabel(detailBean.getScoreRuleType())+" "+detailBean.getScoreMatchValue());
		    buff.append("\n\t<span style='color:BLUE;'>\n\t");
		    buff.append(detailBean.getScoreRuleType().equalsIgnoreCase("incmore")? " in last "+detailBean.getScoreRangeInDays()+" day(s)": "");
		    buff.append("</span>\n\t");

		    //==== WebVisit Filter ======
		} else if(detailBean.getFilterType().equalsIgnoreCase(PMSDefinitions.BEHAVIOUR_FILTER_WEB_VISIT)) {

		    buff.append("&nbsp;&nbsp;<b>Web visit</b> performed\n");
		    buff.append("\n\t");
		    buff.append(detailBean.getWebFilterBy().equalsIgnoreCase("WV")? " ":" against <b>"
			+getBehaviorWebFilterLable(detailBean.getWebFilterBy())+"</b>");
		    buff.append("\n\n").append("	");

		    if(detailBean.getWebFilterBy().equalsIgnoreCase("PU")) {
			buff.append("\n\t   <a href=\""+Default.secureInput(detailBean.getWebFilterURL())+"\" title=\"");
			buff.append(Default.secureInput(detailBean.getWebFilterURL())+"\" target=\"_blank\">\n").append("\t      ");
			buff.append(Default.secureInput(Default.subString(detailBean.getWebFilterURL(), 40, " ...")));
			buff.append("</a>\n").append("\t");
		    }
		    buff.append("\n\n	");
		    buff.append(detailBean.getWebFilterBy().equalsIgnoreCase("PT")? " '"+getLinkIDGroupFilterName(detailBean.getWebLinkFilterGroupId(), groupVec)+"'":"");
		    buff.append("\n	");
		    buff.append(detailBean.getWebFilterBy().equalsIgnoreCase("LF")? " '"+getLinkIDFilterName(detailBean.getWebLinkIDFilterNum(), userFilterVec)+"'":"");
		    buff.append("\n").append("\t<span style='color:BLUE;'>\n\t");
		    buff.append(" "+(detailBean.isFrequency()? detailBean.getFrequency()+" or more times":""));
		    buff.append("\n	");
		    buff.append(detailBean.isTimeSpanApplied()? " in last "+detailBean.getTimeSpanInDays()+" day(s)":"");
		    buff.append("</span>\n\n\t");

		    //==== List Membership Filter ======
		} else if(detailBean.getFilterType().equalsIgnoreCase(PMSDefinitions.BEHAVIOUR_FILTER_LIST)) {

		    Vector listVec = ListManager.getLists(detailBean.getListFilterListNum());

		    buff.append("&nbsp;&nbsp;\n\t");
		    buff.append("<b>List Filter:</b> Subscriber is "+(detailBean.isSubscribedToList()? "Member":"Non Member"));
		    buff.append(" of "+(detailBean.isMatchAll()? " <b>all</b> ":" <b>any</b> ")+" of the following list(s):&nbsp;&nbsp;");
		    buff.append("<select name='etc' style='width:180px;' class=vam>");
		    for(int k=0; k<listVec.size(); k++) {
			ListDataBean listBean = (ListDataBean)listVec.get(k);
			buff.append("<option value=''>"+Default.secureInput(listBean.getName())+"</option>");
		    }
		    buff.append("</select>");


		    //==== Segment Member Filter ======
		} else if(detailBean.getFilterType().equalsIgnoreCase(PMSDefinitions.BEHAVIOUR_FILTER_SEGMENT)) {

		    Vector segmentVec = ListSegmentManager.getListSegmentBeanVec(detailBean.getSegmentFilterNums(), false);

		    buff.append("&nbsp;&nbsp;\n\t");
		    buff.append("<b>Segment Filter:</b> Subscriber is "+(detailBean.isSegmentMember()? "Member":"Non Member"));
		    buff.append(" of "+(detailBean.isMatchAll()? " <b>all</b> ":" <b>any</b> ")+" of the following segment(s):&nbsp;&nbsp;");
		    buff.append("<select name='etc' style='width:180px;' class=vam>");
		    for(int k=0; k<segmentVec.size(); k++) {
			ListSegmentBean segBean = (ListSegmentBean)segmentVec.get(k);
			buff.append("<option value=''>"+Default.secureInput(segBean.getName())+"</option>");
		    }
		    buff.append("</select>");


		    //==== SignupForm Filter ======
		} else if(detailBean.getFilterType().equalsIgnoreCase(PMSDefinitions.BEHAVIOUR_FILTER_FORM_SUBMISSION)) {

		    buff.append("\n\t");
		    buff.append("&nbsp;&nbsp;<b>Sign Up Form</b> '"+getSignUpFromName(detailBean.getFormNumber())+"' submitted");
		    buff.append("\n\t<span style='color:BLUE;'>\n\t");
		    buff.append(detailBean.isTimeSpanApplied()? " in last "+detailBean.getTimeSpanInDays()+" day(s)": "");
		    buff.append("</span>\n").append("  ");
		}
		buff.append("\n\n").append("</td></tr>\n\n");
	    }
	    buff.append("</table>");

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return buff.toString();
    }

    /**
     *
     * @param artNum
     * @param articleVec
     * @return
     */
    private static ArticleDataBean fetchArticle(long artNum, Vector articleVec) {

	for(int i=0; i<articleVec.size(); i++) {
	    ArticleDataBean ab = (ArticleDataBean)articleVec.get(i);
	    if(ab.getArticleNumber()==artNum) {
		return ab;
	    }
	}
	return new ArticleDataBean();
    }
    /**
     *
     * @param score
     * @return
     */
    private static String getScoreLabel(int score) {
	if(score==0)
	    return "0";
	else if(score<0)
	    return "<span class='scoreMinus'>"+score+"</span>";
	else
	    return "<span class='scorePlus'>+"+score+"</span>";
    }
    /**
     *
     * @param formId
     * @return
     */
    private static String getSignUpFromName(long formId) {

	SignupFormDataBean bean = SignupFormManager.getForm(formId);
	return bean==null? "[not found]": Default.secureInput(bean.getName());
    }
    /**
     *
     * @param groupId
     * @param groupVec
     * @return
     */
    private static String getLinkIDGroupFilterName(int groupId, Vector groupVec) {

	for(int i=0; i<groupVec.size(); i++) {
	    LinkIDFilterGroupBean bean = (LinkIDFilterGroupBean)groupVec.get(i);
	    if(bean.getGroupID()==groupId)
		return Default.secureInput(bean.getName());
	}
	return "[not found]";
    }
    /**
     *
     * @param filterNum
     * @param filterVec
     * @return
     */
    private static String getLinkIDFilterName(int filterNum, Vector filterVec) {

	for(int i=0; i<filterVec.size(); i++) {
	    LinkIDFilterBean bean = (LinkIDFilterBean)filterVec.get(i);
	    if(bean.getFilterNumber()==filterNum)
		return Default.secureInput(bean.getLabel());
	}
	return "[not found]";
    }
    /**
     *
     * @param keyword
     * @return
     */
    private static String getBehaviorScoreFilterLabel(String keyword) {

	keyword = Default.toDefault(keyword);
	if(keyword.equalsIgnoreCase("eq"))
	    return "equals";
	else if(keyword.equalsIgnoreCase("less"))
	    return "less than";
	else if(keyword.equalsIgnoreCase("gr8"))
	    return "greater than";
	else if(keyword.equalsIgnoreCase("btw"))
	    return "between";
	else if(keyword.equalsIgnoreCase("incmore"))
	    return "increase more than";

	return "n/a";
    }
    /**
     *
     * @param keyword
     * @return
     */
    private static String getBehaviorWebFilterLable(String keyword) {

	keyword = Default.toDefault(keyword);
	if(keyword.equalsIgnoreCase("WV"))
	    return "Web Visit";
	else if(keyword.equalsIgnoreCase("PU"))
	    return "Page URL";
	else if(keyword.equalsIgnoreCase("PT"))
	    return "Page Type";
	else if(keyword.equalsIgnoreCase("LF"))
	    return "Link Filter";

	return "n/a";
    }
    /**
     * 
     * @param userId
     * @param possibleNewName
     * @return
     */
    public static String createPossibleNewNameForTargets(String userId, String possibleNewName) {

	if(userId.equals("") || possibleNewName.equals(""))
	    return "";

	if(!isBehaviourFilterExists(userId, possibleNewName, 0))
	    return possibleNewName;

	//==== Try 20 times to create & verify that new name exists ==== 
	for(int i=1; i<20; i++) {

	    if(i<=10) {//try some basic logic first.
		possibleNewName += ("_"+i);
		if(!isBehaviourFilterExists(userId, possibleNewName, 0))
		    return possibleNewName;

	    } else {//try something more complex
		possibleNewName += ("_1_"+i);
		if(!isBehaviourFilterExists(userId, possibleNewName, 0))
		    return possibleNewName;
	    }
	}
	return PMSEncoding.encode(""+Math.random());//return random value for name.
    }
    /**
     * 
     * @param targetUserId
     * @param filterNumber
     * @param newName
     * @param isSysAdminFilter
     * @return
     */
    public static int cloneBehaviorFilter(String targetUserId, int filterNumber, String newName, boolean isSysAdminFilter) {
	return cloneBehaviorFilter(targetUserId, filterNumber, newName, "", isSysAdminFilter);
    }
    /**
     *
     * @param filterNumber
     * @return
     */
    public static int cloneBehaviorFilter(String targetUserId, int filterNumber, String newName, String filterFor, boolean isSysAdminFilter) {

	BehaviourFilterBean filterBean = FilterManager.getBehaviourFilterByNum(filterNumber);
	if(filterNumber<=0 || filterBean==null || filterBean.getFilterNumber()<=0 || newName.equals(""))
	    return 0;

	try {
	    BehaviourFilterBean copyBean = new BehaviourFilterBean();
	    copyBean.setUserID(targetUserId);
	    copyBean.setName(newName);
	    copyBean.setApplyRuleCount(filterBean.getApplyRuleCount());
	    copyBean.setFilterFor(Default.toDefault(filterFor).equals("")? filterBean.getFilterFor(): filterFor);
	    copyBean.setTagVec(filterBean.getTagVec());

	    Vector copyDetailVec = new Vector();
	    for(int i=0; i<filterBean.getFilterVec().size(); i++) {
		BehaviourFilterDetailBean detailBean = (BehaviourFilterDetailBean)filterBean.getFilterVec().get(i);

		BehaviourFilterDetailBean sBean = new BehaviourFilterDetailBean();
		sBean.setFilterType(detailBean.getFilterType());
		sBean.setTriggerOrder(detailBean.getTriggerOrder());
		sBean.setDataVec(Default.cloneNameValueVector(detailBean.getDataVec()));

		if(isSysAdminFilter && sBean.getFilterType().equalsIgnoreCase(PMSDefinitions.BEHAVIOUR_FILTER_SCORE)) {
		    ;//all set here.

		} else if(isSysAdminFilter && sBean.getFilterType().equalsIgnoreCase(PMSDefinitions.BEHAVIOUR_FILTER_EMAIL)) {
		    if(sBean.getCampaignNumber()>0) {
			sBean.setDataVec(new Vector());
			sBean.setIsAnyCampaign(true);
		    }
		    
		} else if(isSysAdminFilter && sBean.getFilterType().equalsIgnoreCase(PMSDefinitions.BEHAVIOUR_FILTER_WEB_VISIT)) {
		    if(!sBean.getWebFilterBy().equalsIgnoreCase("WV")) {
			sBean.setDataVec(new Vector());
			sBean.setWebFilterBy("WV");
		    }

		} else if(isSysAdminFilter && sBean.getFilterType().equalsIgnoreCase(PMSDefinitions.BEHAVIOUR_FILTER_LIST)) {
		    sBean.setListFilterListNum(new Vector());

		} else if(isSysAdminFilter && sBean.getFilterType().equalsIgnoreCase(PMSDefinitions.BEHAVIOUR_FILTER_SEGMENT)) {
		    sBean.setSegmentFilterNums(new Vector());

		} else if(isSysAdminFilter && sBean.getFilterType().equalsIgnoreCase(PMSDefinitions.BEHAVIOUR_FILTER_FORM_SUBMISSION)) {
		    sBean.setFormNumber(0);

		} else if(isSysAdminFilter && sBean.getFilterType().equalsIgnoreCase(PMSDefinitions.BEHAVIOUR_FILTER_PROFILE)) {
		    ;//all set here.
		}
		copyDetailVec.add(sBean);
	    }
	    copyBean.setFilterVec(copyDetailVec);

	    if(FilterManager.createBehaviourFilter(copyBean))
		return copyBean.getFilterNumber();

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return 0;
    }

    /**
     * 
     * @param dvBean
     * @return
     */
    public static DynamicVariationBean createDynamicVariation(DynamicVariationBean dvBean) {

	try {
	    return alphaServer.createDynamicVariation(dvBean);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return null;
    }
    /**
     * 
     * @param dynamicNumber
     * @param contentVec
     * @return
     */
    public static Vector createDynamicVariationContents(long dynamicNumber, Vector contentVec) {

	try {
	    return alphaServer.createDynamicVariationContents(dynamicNumber, contentVec);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     * 
     * @param dynamicNumber
     * @param dvcb
     * @return
     */
    public static DynamicVariationContentBean createDynamicVariationContents(long dynamicNumber, DynamicVariationContentBean dvcb)  {

	try {
	    return alphaServer.createDynamicVariationContents(dynamicNumber, dvcb);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return null;
    }
    /**
     * 
     * @param dvBean
     * @return
     */
    public static boolean updateDynamicVariation(DynamicVariationBean dvBean)  {

	try {
	    return alphaServer.updateDynamicVariation(dvBean);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }
    /**
     * 
     * @param dynamicNumber
     * @param campaignNumber
     * @return
     */
    public static DynamicVariationBean getDynamicVariation(long dynamicNumber, long campaignNumber)  {

	try {
	    return alphaServer.getDynamicVariation(dynamicNumber, campaignNumber);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return null;
    }
    /**
     * 
     * @param dynamicNumber
     * @return
     */
    public static DynamicVariationBean getDynamicVariation(long dynamicNumber)  {

	try {
	    return alphaServer.getDynamicVariation(dynamicNumber);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return null;
    }
    /**
     * 
     * @param userId
     * @return
     */
    public static Vector getDynamicVariationBeanByUserId(String userId)  {

	try {
	    return alphaServer.getDynamicVariationBeanByUserId(userId);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     * 
     * @param userId
     * @param keyword
     * @return
     */
    public static DynamicVariationBean getDynamicVariationBeanByKeyword(String userId, String keyword)  {
	return getDynamicVariationBeanByKeyword(userId, keyword, 0);
    }
    
    /**
     * 
     * @param userId
     * @param keyword
     * @param campNum
     * @return
     */
    public static DynamicVariationBean getDynamicVariationBeanByKeyword(String userId, String keyword, long campNum)  {

	try {
	    return alphaServer.getDynamicVariationBeanByKeyword(userId, keyword, campNum);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return null;
    }

    /**
     * 
     * @param contentNumber
     * @param campNum
     * @return
     */
    public static DynamicVariationContentBean getDynamicVariationContent(long contentNumber, long campNum)  {

	try {
	    return alphaServer.getDynamicVariationContent(contentNumber, campNum);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return null;
    }
    /**
     * 
     * @param userId
     * @return
     */
    public static Vector getDynamicVariationsListByUserId(String userId) {
	return getDynamicVariationsListByUserId(userId, "");
    }
    /**
     * 
     * @param userId
     * @return
     */
    public static Vector getDynamicVariationsListByUserId(String userId, String contentType)  {

	try {
	    return alphaServer.getDynamicVariationsListByUserId(userId, contentType);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     * 
     * @param userId
     * @param name
     * @param dynamicNumber
     * @return
     */
    public static boolean isDynamicVariationExists(String userId, String name, long dynamicNumber) {

	try {
	    return alphaServer.isDynamicVariationExists(userId, name, dynamicNumber);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }
    /**
     * 
     * @param dvcb
     * @return
     */
    public static boolean updateynamicVariationContents(DynamicVariationContentBean dvcb)  {

	try {
	    return alphaServer.updateynamicVariationContents(dvcb);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }

    /**
     * 
     * @param contentNumber
     * @param applyRuleCount
     * @param ruleVec
     * @return
     */
    public static boolean createUpdateDynamicVariationRules(long contentNumber, String applyRuleCount, Vector ruleVec)  {

	try {
	    return alphaServer.createUpdateDynamicVariationRules(contentNumber, applyRuleCount, ruleVec);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }
    /**
     * 
     * @param contentNumber
     * @param campNum
     * @return
     */
    public static boolean deleteDynamicVariationContent(long contentNumber, long campNum)  {

	try {
	    return alphaServer.deleteDynamicVariationContent(contentNumber, campNum);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }
    /**
     * 
     * @param dynamicNumber
     * @return
     */
    public static boolean deleteDynamicVariation(long dynamicNumber)  {

	try {
	    return alphaServer.deleteDynamicVariation(dynamicNumber);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }
    /**
     * 
     * @param dynamicNumber
     * @param dvcb
     * @param campaignNumber
     * @param contentType
     * @return
     */
    public static DynamicVariationContentBean addDynamicVariationContentToCampaign(long dynamicNumber, DynamicVariationContentBean dvcb, long campaignNumber, String contentType)  {

	try {
	    return alphaServer.addDynamicVariationContentToCampaign(dynamicNumber, dvcb, campaignNumber, contentType);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return null;
    }
    /**
     * 
     * @param userId
     * @return
     */
    public static Vector getDynamicVariationsKeywordsByUserId(String userId) {

	try {
	    return alphaServer.getDynamicVariationsKeywordsByUserId(userId, false);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     * 
     * @param userId
     * @return
     */
    public static Vector getDynamicVariationsSubjectKeywordsByUserId(String userId) {

	try {
	    return alphaServer.getDynamicVariationsKeywordsByUserId(userId, true);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }

    /**
     * 
     * @param bean
     * @param subNum
     * @param subDataHash
     * @param subListHash
     * @param lastStepExecDate
     * @return
     */
    public static boolean applyMyRules(Vector ruleVec, String applyRuleCount, long subNum, Hashtable subDataHash, Hashtable subListHash) {

	if(ruleVec.isEmpty())
	    return false;

	try {
	    int verifyCounter = 0;
	    int matchCounter = applyRuleCount.equalsIgnoreCase("A")? ruleVec.size(): 1;

	    for(int i=0; ruleVec!=null && i<ruleVec.size(); i++) {

		MyRuleBean ruleBean = (MyRuleBean)ruleVec.get(i);
		long listNumber = ruleBean.getListNumber();
		long subscribeTime = (listNumber>0)? Default.defaultLong((String)subListHash.get(subNum+"##"+listNumber)): 0;

		String rule = ruleBean.getRule();
		String format = ruleBean.getDateFormat();//date Format
		String ruleFieldName = ruleBean.getFieldName();
		String subsRuleFieldData = "";


		if(ruleFieldName.equals(TemplateUtils.SUB_SUBSCRIPTION_DATE_KEY) && (listNumber<=0 || subscribeTime<=0))
		    continue;//Rule has failed, check next rule.

		if(ruleFieldName.equals(TemplateUtils.SUB_SUBSCRIPTION_DATE_KEY))
		    subDataHash.put(ruleFieldName, new java.util.Date(subscribeTime));//update value in hash for {{SUBSCIRBE_DATE}}.


		//if rule is based on BMS.BirthDate or BMS.SubscribeDate
		if(ruleFieldName.equals(TemplateUtils.SUB_BIRTHDATE_KEY) || ruleFieldName.equals(TemplateUtils.SUB_SUBSCRIPTION_DATE_KEY)) {

		    if(subDataHash.get(ruleFieldName)!=null) {
			format = format.equals("")? "yyyy-MM-dd": format;
			subsRuleFieldData = "" + Default.formatDate(((java.util.Date)subDataHash.get(ruleFieldName)).getTime(), format);

		    } else {
			subsRuleFieldData="";//** continue;//Rule has failed, check next rule.
		    }
		} else {
		    subsRuleFieldData = (String)subDataHash.get(ruleFieldName);
		}
		String matchValues = ruleBean.getMatchValue();

		subsRuleFieldData = Default.toDefault(subsRuleFieldData).trim();
//		if(subsRuleFieldData==null)
//		continue;//Rule has failed, check next rule.

		int gap = ruleBean.getDayGap();
		Vector matchValuesVec = Default.fromCSV(matchValues);

		//-- Equals To --
		if(rule.equals("=") && Default.isVecContains(matchValuesVec, subsRuleFieldData)) {
		    ++verifyCounter;//Increment match rule counter && continue with next rule.

		    //-- NOT Equals To --
		} else if(rule.equals("!=") && !Default.isVecContains(matchValuesVec, subsRuleFieldData)) {
		    ++verifyCounter;//Increment match rule counter && continue with next rule.

		    //-- Is EMPTY --
		} else if(rule.equals("empty") && subsRuleFieldData.trim().equals("")) {
		    ++verifyCounter;//Increment match rule counter && continue with next rule.

		    //-- Is Not EMPTY --
		} else if(rule.equals("notempty") && !subsRuleFieldData.trim().equals("")) {
		    ++verifyCounter;//Increment match rule counter && continue with next rule.

		    //-- Numeric Range --
		} else if(rule.equals("nr") && Default.isWithInNumericRanges(matchValuesVec, subsRuleFieldData)) {
		    ++verifyCounter;//Increment match rule counter && continue with next rule.

		    //-- Date Range --
		} else if(rule.equals("dr") && Default.isWithInDateRanges(matchValuesVec, subsRuleFieldData, format)) {
		    ++verifyCounter;//Increment match rule counter && continue with next rule.

		    //-- Contains --
		} else if(rule.equals("ct") && Default.isContains(matchValuesVec, subsRuleFieldData)) {
		    ++verifyCounter;//Increment match rule counter && continue with next rule.

		    //-- NOT Contains --
		} else if(rule.equals("!ct") && !Default.isContains(matchValuesVec, subsRuleFieldData)) {
		    ++verifyCounter;//Increment match rule counter && continue with next rule.

		    //-- Day OF --
		} else if(rule.equals("dayof") && Default.isDayOfRuleApplied(subsRuleFieldData, format)) {

		    long parsedTime = Default.removeHourPart(Default.parseDate(subsRuleFieldData, format));
		    if(parsedTime==Default.removeHourPart(System.currentTimeMillis()))
			++verifyCounter;//Increment match rule counter.


		    //-- Day After --
		} else if(rule.equals("after") && Default.isDayAfterRuleApplied(subsRuleFieldData, format, gap)) {

		    long parsedTime = Default.removeHourPart(Default.parseDate(subsRuleFieldData, format));
		    if( (parsedTime+(Default.MILLIES_FOR_ONE_DAY*gap)) == Default.removeHourPart(System.currentTimeMillis()))
			++verifyCounter;//Increment match rule counter.


		    //-- Day Prior --
		} else if(rule.equals("prior") && Default.isDayPriorRuleApplied(subsRuleFieldData, format, gap)) {

		    long parsedTime = Default.removeHourPart(Default.parseDate(subsRuleFieldData, format));
		    if((parsedTime-(Default.MILLIES_FOR_ONE_DAY*gap)) == Default.removeHourPart(System.currentTimeMillis()))
			++verifyCounter;//Increment match rule counter.

		    //-- Birthday --
		} else if(rule.equals("birthday") && Default.isValidDate(subsRuleFieldData, format)) {

		    long bDayTime = Default.getBirthDay(Default.parseDate(subsRuleFieldData, format));
		    if(Default.removeHourPart(bDayTime)==Default.removeHourPart(System.currentTimeMillis()))
			++verifyCounter;//Increment match rule counter.


		    //-- Day(s) Prior Birthday --
		} else if(rule.equals("pbday") && Default.isValidDate(subsRuleFieldData, format)) {

		    long bDayTime = Default.getBirthDay(Default.parseDate(subsRuleFieldData, format));
		    if((bDayTime -(Default.MILLIES_FOR_ONE_DAY*gap)) == Default.removeHourPart(System.currentTimeMillis()))
			++verifyCounter;//Increment match rule counter.

		} else {
		    ;//do nothing, rule has failed, check next rule.
		}
	    }

	    //Check if the rule criteria has matched, then return scheduled execDate.
	    return verifyCounter >= matchCounter;

	} catch (Exception e) {
	    AppServerLogger.getLogger().log(e);
	    e.printStackTrace();
	}
	return false;
    }
    /**
     * ======== FOR WEB_VERSION ONLY ========
     * 
     * @param dvBean
     * @param campNum
     * @param subNum
     * @param subHash
     * @param subListHash
     * @param keywordsVec
     * @param artNumVec
     * @return
     */
    public static String getDynamicVariationContent(DynamicVariationBean dvBean, long campNum, long subNum, Hashtable subHash, Hashtable subListHash
	    , Vector keywordsVec, Vector articleBeanVec, boolean isPreview) {

	if(dvBean==null)
	    return "";

	try {
	    long contentNumber = dvBean.getDefaultContent()!=null? dvBean.getDefaultContent().getContentNumber(): 0;

	    for(int i=0; i<dvBean.getDynamicContentVec().size(); i++) {
		DynamicVariationContentBean dvcb = (DynamicVariationContentBean)dvBean.getDynamicContentVec().get(i);
		
		if(!dvcb.isDefault() && applyMyRules(dvcb.getRuleVec(), dvcb.getApplyRuleCount(), subNum, subHash, subListHash)) {
		    contentNumber = dvcb.getContentNumber();
		    break;
		}

	    }

	    if(contentNumber<=0) {
		    System.out.println("[CampaignManager - replaceKeywordsWithData - WV]  DV_NOT_MATCHED  dynamicNumber:"+dvBean.getDynamicNumber()+", keyword:"+dvBean.getKeyword()
			    +", campNum:"+campNum+", subNum:"+subNum);
		return "";
	    }
	    return getMatchedContents(subNum, subHash, dvBean, contentNumber, keywordsVec, articleBeanVec, isPreview);

	} catch(Exception e) {
	    WebServerLogger.getLogger().log(e);
	    e.printStackTrace();
	}
	return "";
    }

    /**
     * ======== FOR WEB_VERSION ONLY ========
     *
     * @param subHash
     * @param dParasBean
     * @param contentNumber
     * @return
     */
    private static String getMatchedContents(long subNum, Hashtable subHash, DynamicVariationBean dvBean, long contentNumber, Vector keywordsVec, Vector articleBeanVec, boolean isPreview) {

	if(contentNumber<=0l)
	    return "";

	try {	    
	    DynamicVariationContentBean dvcb = dvBean.getDynamicVariationContent(contentNumber);
	    articleBeanVec = isPreview? dataServer.getArticle(dvcb.getArticleNumbers()): articleBeanVec;//Load Non-Campaign Articles, IF PREVIEW REQUEST.
	    	    
	    String html = CampaignManager.parseArticles_Opt(isPreview? 0: subNum, articleBeanVec, dvcb.getParsedContents());

	    //==== Collect Keyword Indexes if found any ======
	    Vector keywordIndexVec = new Vector();
	    CampaignManager.collectKeywordsIndexes(html, keywordsVec, keywordIndexVec);

	    //===== Replace Subscriber data with Keywords in Content HTML =====
	    StringBuffer newPage = new StringBuffer();
	    int lastIndex = 0;

	    for(int i=0; keywordIndexVec!=null && i<keywordIndexVec.size(); i++) {

		NameValue nv = (NameValue)keywordIndexVec.get(i);
		int index = Default.defaultInt(nv.getName());
		String keyword = nv.getValue();


		newPage.append(html.substring(lastIndex, index));//append part before keyword
		newPage.append(Default.toDefault((String)subHash.get(keyword)));//replace keyword with subscriber data

		lastIndex = index+keyword.length();//jump the html index after the keyword
	    }

	    /**
	     * Adding the remainig part at the end of page.
	     */
	    if(lastIndex<html.length())
		newPage.append(html.substring(lastIndex, html.length()));

	    
	    System.out.println("[CampaignManager - replaceKeywordsWithData - WV]  DV_MATCHED  dynamicNumber:"+dvBean.getDynamicNumber()+", keyword:"+dvBean.getKeyword()+", contentNumber:"+dvcb.getContentNumber()
		    +", contentLength:"+newPage.length()+", subNum:"+subNum);

	    return newPage.toString();

	} catch(Exception e) {
	    WebServerLogger.getLogger().log(e);
	    e.printStackTrace();
	}
	return "";
    }
    /**
     * ===== Call_FROM_WEB :: in case of changes to contents of a variation =======
     * 
     * @param dParaBean
     */
    public static void processDynamicVariationContentForArticles(DynamicVariationContentBean dvcb, String contentType) {

	if(contentType.equals("") || dvcb==null)
	    return;

	//==== Delete Existing Articles, As Contents Has Been Changed ====
	CampaignManager.deleteArticles(dvcb.getArticleNumbers());

	if(!contentType.equalsIgnoreCase("S")) {

	    dvcb.setArticleNumbers(new Vector());

	    Vector articleNumVec = new Vector();
	    if(contentType.equalsIgnoreCase("H")) {//Parse html contents.

		String parsedCont = CampaignManager.processHTMLLinks(0, dvcb.getContents(), articleNumVec);
		dvcb.setParsedContents(parsedCont);
		dvcb.setArticleNumbers(articleNumVec);

	    } else if(contentType.equalsIgnoreCase("T")) {//Parse plain text contents.

		String parsedCont = CampaignManager.processPlainTextLinks(0, dvcb.getContents(), articleNumVec);
		dvcb.setParsedContents(parsedCont);
		dvcb.setArticleNumbers(articleNumVec);
	    }

	    System.out.println("[FilterManager.processDVCArticles]:  contentType:"+contentType+", articleNums:"+Default.toCSV(dvcb.getArticleNumbers()));

	    /**
	     * If content-type is CampaignSubject OR
	     * NoMatchCase is Supress Email & ContentOption is 'Default'
	     * Then delete all previously created (if any) articles.
	     */
	} else {
	    dvcb.setParsedContents("");
	    dvcb.setArticleNumbers(new Vector());
	}
    }

    /**
     * 
     * @param userId
     * @param dynamicNumber
     * @return
     */
    public static DynamicVariationBean cloneDynamicVariation(String userId, DynamicVariationBean dvBean, String newLabel) {

	if(dvBean==null || newLabel.equals("") || !dvBean.getUserId().equalsIgnoreCase(userId))
	    return null;

	try {
	    dvBean.setDynamicNumber(0);
	    dvBean.setLabel(newLabel);
	    dvBean.setKeyword("");

	    for(int i=0; i<dvBean.getDynamicContentVec().size(); i++) {

		DynamicVariationContentBean dvcb = (DynamicVariationContentBean)dvBean.getDynamicContentVec().get(i);
		dvcb.setContentNumber(0);
		dvcb.setArticleNumbers(new Vector());

		Vector articleNumVec = new Vector();
		if(dvBean.getContentType().equalsIgnoreCase("H")) {//Parse html contents.

		    String parsedCont = CampaignManager.processHTMLLinks(0, dvcb.getContents(), articleNumVec);
		    dvcb.setParsedContents(parsedCont);
		    dvcb.setArticleNumbers(articleNumVec);

		} else if(dvBean.getContentType().equalsIgnoreCase("T")) {//Parse plain text contents.

		    String parsedCont = CampaignManager.processPlainTextLinks(0, dvcb.getContents(), articleNumVec);
		    dvcb.setParsedContents(parsedCont);
		    dvcb.setArticleNumbers(articleNumVec);
		}
	    }

	    return createDynamicVariation(dvBean);

	} catch(Exception e) {
	    WebServerLogger.getLogger().log(e);
	    e.printStackTrace();
	}
	return null;
    }
    /**
     * 
     * @param bucket
     * @return
     */
    public static Vector fetchToProcessTargetPopulation(int bucket) {

	try {
	    return complexQueryServer2.fetchToProcessTargetPopulation(bucket);
	    
	} catch(Exception e) {
	    WebServerLogger.getLogger().log(e);
	    e.printStackTrace();
	}
	return new Vector();
    }
    /**
     * 
     * @param filterNumber
     * @param subNumVec
     * @param status
     * @param isMatched
     * @return
     */
    public static boolean updateProcessedTargetPopulation(int filterNumber, Vector subNumVec, String status, boolean isMatched) {
	
	try {
	    return complexQueryServer2.updateProcessedTargetPopulation(filterNumber, subNumVec, status, isMatched);
	    
	} catch(Exception e) {
	    WebServerLogger.getLogger().log(e);
	    e.printStackTrace();
	}
	return false;
    }

}

