package com.PMSystems;

import java.rmi.RemoteException;
import java.util.*;
import java.io.*;

import com.PMSystems.*;
import com.PMSystems.sejbs.*;
import com.PMSystems.beans.*;
import com.PMSystems.dbbeans.*;
import com.PMSystems.util.*;
import com.PMSystems.template.*;
import com.PMSystems.logger.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: </p>
 * @author Ahmad Suhaib
 * @version 1.0
 */

public class TriggerManager {

    public static final String CUSTOME_FIELD_PADDER = "9*9_";

    public static Vector pmsFieldVec = new Vector();
    public static Vector pmsRuleVec = new Vector();
    public static Vector pmsFormatVec = new Vector();
    private static Vector triggerTypeNameVec = new Vector();

    private static DataQueryServer dataServer;
    private static ComplexQueryServer complexQueryServer;
    private static ComplexQueryServer2 complexQueryServer2;
    private static ReportsQueryServer reportQueryServer;

    static{
        dataServer = EJBHomesFactory.getDataQueryServerRemote();
        complexQueryServer = EJBHomesFactory.getComplexQueryServerRemote();
        complexQueryServer2 = EJBHomesFactory.getComplexQueryServer2Remote();
        reportQueryServer = EJBHomesFactory.getReportsQueryServerRemote();
        
        pmsFieldVec.add(new NameValue(TemplateUtils.SUB_EMAIL_KEY, "Email"));

        pmsFieldVec.add(new NameValue(TemplateUtils.SUB_ADDR_KEY, "Address Line 1"));
        pmsFieldVec.add(new NameValue(TemplateUtils.SUB_ADDRESS2_KEY, "Address Line 2"));

        pmsFieldVec.add(new NameValue(TemplateUtils.SUB_CITY_KEY, "City"));
        pmsFieldVec.add(new NameValue(TemplateUtils.SUB_STATE_KEY, "State"));
        pmsFieldVec.add(new NameValue(TemplateUtils.SUB_ZIP_KEY, "Zip"));
        pmsFieldVec.add(new NameValue(TemplateUtils.SUB_COUNTRY_KEY, "Country"));
        pmsFieldVec.add(new NameValue(TemplateUtils.SUB_PHONE_KEY, "Telephone"));
        pmsFieldVec.add(new NameValue(TemplateUtils.SUB_SUBSCRIPTION_DATE_KEY, "Subscribe Date"));

        pmsFieldVec.add(new NameValue(TemplateUtils.SUB_BIRTHDATE_KEY, "Birth Date"));

        pmsFieldVec.add(new NameValue(TemplateUtils.SUB_OCCUPATION_KEY, "Occupation"));
        pmsFieldVec.add(new NameValue(TemplateUtils.SUB_INDUSTRY_KEY, "Industry"));
        pmsFieldVec.add(new NameValue(TemplateUtils.SUB_COMPANY_KEY, "Company"));
        pmsFieldVec.add(new NameValue(TemplateUtils.SUB_SOURCE_KEY, "Source"));
        pmsFieldVec.add(new NameValue(TemplateUtils.SUB_SALESREP_KEY, "Sales Rep"));
        pmsFieldVec.add(new NameValue(TemplateUtils.SUB_SALESSTATUS_KEY, "Sales Status"));

        pmsRuleVec.add(new NameValue("=", "equal to"));
        pmsRuleVec.add(new NameValue("!=", "not equal to"));
        pmsRuleVec.add(new NameValue("ct", "contains"));
        pmsRuleVec.add(new NameValue("!ct", "does not contain"));
        pmsRuleVec.add(new NameValue("dr", "within date range"));
        pmsRuleVec.add(new NameValue("nr", "within numeric range"));
        pmsRuleVec.add(new NameValue("prior", "day(s) prior"));
        pmsRuleVec.add(new NameValue("after", "day(s) after"));
        pmsRuleVec.add(new NameValue("dayof", "day of"));
        pmsRuleVec.add(new NameValue("birthday", "birthday"));
        pmsRuleVec.add(new NameValue("pbday", "day(s) prior birthday"));

        pmsFormatVec.add(new NameValue("MM-dd-yy", ""));
        pmsFormatVec.add(new NameValue("MM-dd-yyyy", ""));
        pmsFormatVec.add(new NameValue("dd-MM-yy", ""));
        pmsFormatVec.add(new NameValue("dd-MM-yyyy", ""));

        pmsFormatVec.add(new NameValue("yyyy-MM-dd", ""));

        pmsFormatVec.add(new NameValue("MM/dd/yy", ""));
        pmsFormatVec.add(new NameValue("MM/dd/yyyy", ""));
        pmsFormatVec.add(new NameValue("dd/MM/yyyy", ""));
        pmsFormatVec.add(new NameValue("dd/MM/yyyy", ""));
        pmsFormatVec.add(new NameValue("yyyy/MM/dd", ""));

        triggerTypeNameVec.add(new NameValue("B", "Basic Trigger Track"));
        triggerTypeNameVec.add(new NameValue("S", "Sales Nurturing Trigger Track"));
        triggerTypeNameVec.add(new NameValue("M", "Web Seminar Trigger Track"));
        triggerTypeNameVec.add(new NameValue("T", "Trial Trigger Track"));

    }

    /**
     */
    private TriggerManager() {
    }

    /**
     * @param trackId
     * @return
     */
    public static TriggerTrackBean getTriggerTrack(int trackId) {

        try {
            return complexQueryServer2.getTriggerTrackById(trackId);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return null;
    }
    /**
     *
     * @param trackIdVec
     * @return
     */
    public static Vector getTriggerTracksByIdVec(Vector trackIdVec) {

        if(trackIdVec.isEmpty())
            return new Vector();
        try {
            return complexQueryServer2.getTriggerTracksByIdVec(trackIdVec);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }

    /**
     * @param trackNumVec
     * @return
     */
    public static Vector getTriggerMessageSummaryByTrackId(Vector trackNumVec, boolean isSAM) {

        if(trackNumVec.isEmpty())
            return new Vector();

        try {
            return complexQueryServer2.getTriggerMessageSummaryByTrackId(trackNumVec, isSAM);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }
    /**
     *
     * @param samUserBean
     * @return
     */
    public static Vector getTriggerMessageSummaryForSAMUser(DashboardSAMBean samUserBean) {

        if(samUserBean==null || samUserBean.getUserId().equals(""))
            return new Vector();

        try {
            return complexQueryServer2.getTriggerMessageSummaryForSAMUser(samUserBean);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }

    /**
     * @param userId
     * @return
     */
    public static Vector getTriggerTrackByUser(String userId) {

        try {
            return complexQueryServer2.getTriggerTrackByUser(userId, false);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }

    public static Vector getNewTriggerTrackByUser(String userId) {

        try {
            return complexQueryServer2.getTriggerTrackByUser(userId, true);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }

    /**
     * @param userId
     * @param trackId
     * @param trackName
     * @return
     */
    public static boolean isTriggerTrackExists(String userId, int trackId, String trackName) {

        try {
            return complexQueryServer2.isTriggerTrackExists(userId, trackId, trackName);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Update Each Message's CampaignName in response to; A). change in Track's name or B). one of the track messages deleted.
     *
     * @param trackBean
     * @return
     */
    public static boolean updateMessageCampaignNames(TriggerTrackBean trackBean) {
/*
        if(trackBean==null || trackBean.getTrackMessageVec()==null || trackBean.getTrackMessageVec().isEmpty())
            return false;

        try {
            Vector vec = trackBean.getTrackMessageVec();
            for(int i=0; i<vec.size(); i++) {
                TriggerMessageBean msgBean = (TriggerMessageBean)vec.get(i);
                long campNum = msgBean.getCampaignNumber();

                if(campNum>0) {
                    String cName = buildTrackMsgCampaignName(trackBean.getName(), msgBean.getCampaignNumber());
                    CampaignManager.updateCampaignName(campNum, cName);
                    CampaignManager.updateCompletedCampaignName(campNum, cName);
                }
            }
            return true;

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
*/
        return false;
    }

    /**
     * @param trackName
     * @param msgOrder
     * @return
     */
    
    public static String buildTrackMsgCampaignName(String trackName, int msgOrder) {
        return buildTrackMsgCampaignName(trackName, 0l);
    }
    /**
     * 
     * @param trackName
     * @param campNum
     * @return
     */
    public static String buildTrackMsgCampaignName(String trackName, long random) {
        return trackName+"-"+Default.toMD5CheckSum(""+WebSecurityManager.getCSRFToken());
    }

    /**
     * @param userId
     * @param trackId
     * @param trackName
     * @return
     */
    public static Vector getTriggerTrackLists(String userId, int trackId) {

        try {
            return complexQueryServer2.getTriggerTrackLists(trackId);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }
    /**
     * @param userId
     * @param trackId
     * @param listNumVec
     * @return
     */
    public static boolean createUpdateTriggerTrackLists(String userId, int trackId, Vector listNumVec) {

        try {
            return complexQueryServer2.createUpdateTriggerTrackLists(trackId, listNumVec);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * @param userId
     * @param trackBean
     * @return
     */
    public static boolean updateTriggerTrack(TriggerTrackBean trackBean, boolean updateListVec) {

        try {
            return complexQueryServer2.updateTriggerTrack(trackBean, updateListVec);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param userId
     * @param msgBean
     * @return
     */
    public static boolean updateTriggerMessage(String userId, TriggerMessageBean msgBean) {

        try {
            return complexQueryServer2.updateTriggerMessage(msgBean);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param trackId
     * @return
     */
    public static boolean playTriggerTrack(String userId, int trackId) {

        if(trackId<=0)
            return false;
        try {
            TriggerTrackBean trackBean = getTriggerTrack(trackId);
            if(trackBean==null || !trackBean.getUserID().equals(userId) || !trackBean.isEditable())
                return false;

            return complexQueryServer2.markTriggerTrackActive(trackId);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     *
     * @param trackId
     * @return
     */
    public static boolean pauseTriggerTrack(String userId, int trackId) {

        if(trackId<=0)
            return false;
        try {
            TriggerTrackBean trackBean = getTriggerTrack(trackId);
            if(trackBean==null || !trackBean.getUserID().equals(userId) || trackBean.isEditable())
                return false;

            return complexQueryServer2.markTriggerTrackInactive(trackId);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * @param userId
     * @param msgBean
     * @return
     */
    public static TriggerMessageBean createTriggerMessage(String userId, TriggerTrackBean trackBean, TriggerMessageBean msgBean) {

	if(userId.equals("") || trackBean==null || msgBean==null)
	    return null;
	    
        try {
            if(trackBean.getTriggerMsgByOrder(msgBean.getTriggerOrder())!=null && !makeRoomForNewTriggerMessageAt(trackBean.getTrackID(), msgBean.getTriggerOrder())) {
        	return null;
            }
            return complexQueryServer2.createTriggerMessage(userId, msgBean);
            
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return null;
    }
    /**
     * 
     * @param userId
     * @param trackBean
     * @param msgBean
     * @return
     */
    public static boolean moveTriggerMessageTo(String userId, TriggerTrackBean trackBean, TriggerMessageBean msgBean, int newTriggerOrder) {

	if(userId.equals("") || trackBean==null || msgBean==null || newTriggerOrder<=0)
	    return false;
	    
        try {
            int oldTriggerOrder = msgBean.getTriggerOrder();
            
            msgBean.setTriggerOrder(newTriggerOrder);
            if(complexQueryServer2.updateTrackMsgOrdersAfterMsgDelete(trackBean.getTrackID(), oldTriggerOrder)
        	    && complexQueryServer2.makeRoomForNewTriggerMessageAt(trackBean.getTrackID(), newTriggerOrder)
        	    && complexQueryServer2.updateTriggerMessage(msgBean))
        	return true;        	    
        	            
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * @param userId
     * @param trackId
     * @return
     */
    public static boolean deleteTriggerTrack(String userId, int trackId) {

        try {
            TriggerTrackBean trackBean = getTriggerTrack(trackId);
            if(trackBean==null || !trackBean.getUserID().equals(userId) || !trackBean.isEditable())
                return false;

            for(int i=0; i<trackBean.getTrackMessageVec().size(); i++)
                complexQueryServer2.deleteTriggerMessage(trackId, ((TriggerMessageBean)trackBean.getTrackMessageVec().get(i)).getMessageID());

            return complexQueryServer2.deleteTriggerTrack(userId, trackId);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     * 
     * @param trackId
     * @param messageId
     * @param label
     * @return
     */
    public static boolean updateTriggerMessageLabel(int trackId, int messageId, String label) {
	
        try {
            return complexQueryServer2.updateTriggerMessageLabel(trackId, messageId, label);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     * @param trackId
     * @param messageId
     * @return
     */
    public static boolean deleteTriggerMessage(int trackId, int messageId) {

        try {
            TriggerTrackBean trackBean = getTriggerTrack(trackId);
            if(trackBean==null || !trackBean.isEditable())
                return false;

            TriggerMessageBean deleteMsgBean = trackBean.getTriggerMessageByID(messageId);
            if(deleteMsgBean==null)
                return false;

            //If Msg deleted successfully, update triggerOrder & name for remaining messages.
            if(complexQueryServer2.deleteTriggerMessage(trackId, messageId)) {

                complexQueryServer2.updateTrackMsgOrdersAfterMsgDelete(trackId, deleteMsgBean.getTriggerOrder());
                System.out.println("[TriggerManager] TRIGGER_ORDER_SHIFTED  userId:"+trackBean.getUserID()+", trackId:"+trackId+", del.MsgId:"
                                   +messageId+", del.TriggerOrder:"+deleteMsgBean.getTriggerOrder());

                //reload TrackBean & update Message CampaignName's if any campaign exists for messages.
                //updateMessageCampaignNames(getTriggerTrack(trackId));
                //System.out.println("[TriggerManager] CAMPAIGN_NAMES_UPDATED  userId:"+trackBean.getUserID()+", trackId:"+trackId+", del.MsgId:"+messageId);
                return true;
            }

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     * 
     * @param trackId
     * @param insertAtOrder
     * @return
     */
    public static boolean makeRoomForNewTriggerMessageAt(int trackId, int insertAtOrder) {
	
	if(trackId<=0 || insertAtOrder<=0)
	    return false;
	
	try {
	    return complexQueryServer2.makeRoomForNewTriggerMessageAt(trackId, insertAtOrder);
	    
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     * @param msgBean
     * @param viewType
     * @return
     */
    public static int countTriggerMessageSubsByViewType(TriggerMessageBean msgBean, int viewType, boolean isSAM) {

        if(msgBean==null || viewType<=0 || msgBean.getMessageID()<=0)
            return 0;

        try {
            return complexQueryServer2.countTriggerMessageSubsByViewType(msgBean.getMessageID(), msgBean.getCampaignNumber(), viewType, isSAM);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return 0;
    }
    /**
     *
     * @param samUserBean
     * @param msgBean
     * @param viewType
     * @param isSAM
     * @return
     */
    public static int countTriggerMessageSubsByViewTypeForSAM(DashboardSAMBean samUserBean, TriggerMessageBean msgBean, int viewType) {

        if(msgBean==null || viewType<=0 || msgBean.getMessageID()<=0)
            return 0;

        try {
            return complexQueryServer2.countTriggerMessageSubsByViewTypeForSAM(samUserBean, msgBean.getMessageID()
                , msgBean.getCampaignNumber(), viewType);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     *
     * @param msgBean
     * @param viewType
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector viewTriggerMessageSubsByViewType(TriggerMessageBean msgBean, int viewType, int offset, int bucket, boolean isSAM) {

        if(msgBean==null || viewType<=0 || msgBean.getMessageID()<=0)
            return new Vector();

        try {
            return complexQueryServer2.viewTriggerMessageSubsByViewType(msgBean.getMessageID(), msgBean.getCampaignNumber(), viewType
                , offset, bucket, isSAM);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }
    /**
     *
     * @param samUserBean
     * @param msgBean
     * @param viewType
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector viewTriggerMessageSubsByViewTypeForSAM(DashboardSAMBean samUserBean, TriggerMessageBean msgBean
        , int viewType, int offset, int bucket) {

        if(msgBean==null || viewType<=0 || msgBean.getMessageID()<=0)
            return new Vector();

        try {
            return complexQueryServer2.viewTriggerMessageSubsByViewTypeForSAM(samUserBean, msgBean.getMessageID(), msgBean.getCampaignNumber()
                , viewType, offset, bucket);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }

    /**
     * @param messageId
     * @return
     */
    public static int fetchPendingSubsCountForTriggerMessage(int messageId, boolean isSAM) {

        if(messageId<=0)
            return 0;
        try {
            return complexQueryServer2.fetchPendingSubsCountForTriggerMessage(messageId, isSAM);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return 0;
    }
    /**
     * @param messageId
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector fetchPendingSubsForTriggerMessage(int messageId, int offset, int bucket, boolean isSAM) {

        if(messageId<=0 || offset<0 || bucket<=0)
            return new Vector();
        try {
            return complexQueryServer2.fetchPendingSubsForTriggerMessage(messageId, offset, bucket, isSAM);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }

    /**
     * @param userId
     * @param trackBean
     * @return
     */
    public static TriggerTrackBean createTriggerTrack(String userId, TriggerTrackBean trackBean) {

        try {
            return complexQueryServer2.createTriggerTrack(trackBean);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return null;
    }
    /**
     * 
     * @param userInfo
     * @param trackBean
     * @param name
     * @return
     */
    public static TriggerTrackBean createCopyTrack(UserInfo userInfo, TriggerTrackBean trackBean, String name) {

	if(userInfo==null || trackBean==null || name.equals(""))
	    return null;
	
	try {
	    //==== clone all targets =====
	    Vector cloneTargetVec = trackBean.getUserID().equalsIgnoreCase(userInfo.getUserID())? new Vector(trackBean.getTargetFilterNumVec()): new Vector();
	    
	    for(int i=0; trackBean.getUserID().equalsIgnoreCase(SysAdminManager.SYSADMIN_USER_ID) && i<trackBean.getTargetFilterNumVec().size(); i++) {
		int filterNumber = Default.defaultInt((String)trackBean.getTargetFilterNumVec().get(i));
		
		String newName = FilterManager.createPossibleNewNameForTargets(userInfo.getUserID(), trackBean.getName());
		
		int newFilterNum = FilterManager.cloneBehaviorFilter(userInfo.getUserID(), filterNumber, newName, trackBean.getUserID().equalsIgnoreCase(SysAdminManager.SYSADMIN_USER_ID));
		if(newFilterNum>0)
		    cloneTargetVec.add(""+newFilterNum);
	    }
	    
	    //==== clone & create TriggerTrack =====
	    TriggerTrackBean newTrackBean = new TriggerTrackBean();
	    newTrackBean.setName(name);
	    newTrackBean.setUserID(userInfo.getUserID());
	    newTrackBean.setIsNewTriggerTrack(true);    
	    newTrackBean.setTargetFilterNumVec(cloneTargetVec);
	    newTrackBean.setTagVec(trackBean.getTagVec());
	    
	    if(trackBean.getUserID().equalsIgnoreCase(userInfo.getUserID()) || trackBean.getUserID().equalsIgnoreCase(SysAdminManager.SYSADMIN_USER_ID))
		newTrackBean.setImageId(trackBean.getImageId());
	    
	    //==== Create Nurture Track ======
	    newTrackBean = createTriggerTrack(userInfo.getUserID(), newTrackBean);
	    if(newTrackBean==null)
		return null;//copying failed
	    
	    
	    //==== Clone all TriggerMessages =====    
	    for(int triggerOrder=1; triggerOrder<=trackBean.getTrackMessageVec().size(); triggerOrder++) {
		TriggerMessageBean msgBean = trackBean.getTriggerMsgByOrder(triggerOrder);
		
		long campNum = msgBean.getCampaignNumber();
		msgBean.setTrackID(newTrackBean.getTrackID());
		msgBean.setMessageID(0);
		msgBean.setCampaignNumber(0);
		
		if(!(trackBean.getUserID().equalsIgnoreCase(userInfo.getUserID()) || trackBean.getUserID().equalsIgnoreCase(SysAdminManager.SYSADMIN_USER_ID)))
		    msgBean.setImageId(0);

		long newCampNum = 0;
		String newCampName = TriggerManager.buildTrackMsgCampaignName(newTrackBean.getName(), 0);
		
		if(campNum>0 && trackBean.getUserID().equalsIgnoreCase(SysAdminManager.SYSADMIN_USER_ID)) {
		    newCampNum = CampaignManager.copyCampaignForSysAdmin(userInfo.getUserID(), newCampName, campNum);
		    
		} else if(campNum>0) {
		    newCampNum = CampaignManager.createCopyCampaign(userInfo.getUserID(), campNum, newCampName);
		}
		
		if(newCampNum>0)
		    msgBean.setCampaignNumber(newCampNum);
		
		createTriggerMessage(userInfo.getUserID(), newTrackBean, msgBean);
	    }
	    
	    //==== Return newly created NurtureTrack ====
	    return getTriggerTrack(newTrackBean.getTrackID());
	    
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return null;
    }
    
    /**
     * @param type
     * @return
     */
    public static String getTriggerTypeName(String type) {

        int index = triggerTypeNameVec.indexOf(new NameValue(type, ""));
        if(index!=-1) {

            return ((NameValue)triggerTypeNameVec.get(index)).getValue();
        }
        return "Basic Trigger Track";
    }

    /**
     * @param str
     * @return
     */
    public static String getFieldName(String str) {

        int index = pmsFieldVec.indexOf(new NameValue(str, ""));
        if(index!=-1) {
            return ((NameValue)pmsFieldVec.get(index)).getValue();
        }
        return removeBraces(str);
    }
    /**
     * @param str
     * @return
     */
    public static String getRuleName(String str) {
        int index = pmsRuleVec.indexOf(new NameValue(str, ""));
        if(index!=-1) {
            return ((NameValue)pmsRuleVec.get(index)).getValue();
        }
        return "n/a";
    }
    /**
     * @param str
     * @return
     */
    public static String getFormatName(String str) {
        int index = pmsFormatVec.indexOf(new NameValue(str, ""));
        if(index!=-1) {
            return str.toUpperCase();
        }
        return "n/a";
    }
    /**
     * @param str
     * @return
     */
    public static String removeBraces(String str) {

        String customPadder = "{{"+CUSTOME_FIELD_PADDER;
        if(str==null)
            return "";

        if(str.startsWith(customPadder))
            str = str.substring(customPadder.length(), str.length());

        if(str.startsWith("{{"))
            str = str.substring(2, str.length());

        if(str.endsWith("}}"))
            str = str.substring(0, str.indexOf("}}"));

        return str;
    }

    /**
     * Returns true, if any new articles found.
     *
     * @param newBean
     */
    public static boolean processForArticles(CampaignDataBean newBean) {

        if(newBean==null || newBean.getCampaignNumber()<=0)
            return false;
        
        try {            
            if(newBean.getCampaignType().equalsIgnoreCase(PMSDefinitions.CAMPAIGN_TYPE_NORMAL)) {
        	CampaignManager.deleteArticles(newBean.getCampaignNumber());//Delete old articles if Normal Campaign.
            }

            String parseHTML = CampaignManager.processHTMLLinks(newBean.getCampaignNumber(), newBean.getAdvanceEditorOrignalHtmlText());
            newBean.setAdvanceEditorHtmlText(parseHTML);

            String parsePlainText = CampaignManager.processPlainTextLinks(newBean.getCampaignNumber(), newBean.getAdvanceEditorOrignalPlainText());
            newBean.setAdvanceEditorPlainText(parsePlainText);

            CampaignManager.saveAdvanceEditorTextsForCampaign(newBean);
            return true;
            
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * @return
     */
    public static String getWebVersionURLSuffix() {

        String webURL = "http://www.bridgemailsystem.com/";
        try {
            webURL = PMSResources.getInstance().getWebServerURL();
            return webURL + PMSDefinitions.WEB_VERSION_DIR;

        } catch (Exception ex) {}
        return webURL;
    }

    /**
     * @param campaignNumber
     * @param userInfo
     * @return
     */
    public static String createTriggerMessageWebVersion(CampaignDataBean campBean, UserInfo userInfo) {

        String url = "";
        if(campBean==null || campBean.getCampaignNumber()<=0)
            return url;

        try {
            // Creating Web Version of Campaign
            String fileName = PMSEncoding.encode(""+campBean.getCampaignNumber())+".html";
            if(!campBean.getWebVersionFileName().equals("")) {
                fileName = campBean.getWebVersionFileName();
                fileName = PMSUtils.removeChar(fileName,' ');
            }
//            Template template = CampaignManager.getTemplate(campBean.getCampaignNumber(),userInfo);
//            if(template==null)
//                return "";
//
//            String view = "";
//            if(template.getCampain().getUseAdvanceEditor() == null || template.getCampain().getUseAdvanceEditor().equalsIgnoreCase("N")) {
//                view = template.build(Template.WEB_VERSION);
//            }else{
//                view = template.build(Template.ADVANCE_EDITOR_WEB_VERSION);
//            }
            String view = campBean.getAdvanceEditorOrignalHtmlText();

            String webVersioDIR = PMSResources.getInstance().getWebVersionPath();
            String dirFileName = webVersioDIR + fileName;

            File file = new File(dirFileName);
            file.delete();
            file.createNewFile();
            DataOutputStream dataOut = new DataOutputStream(new FileOutputStream(file));
            dataOut.writeBytes(view);

            if(campBean.getWebVersionFileName().equals("")) {
                campBean.setWebVersionFileName(fileName);
                //Update CampaignDetail as WebVersionFileName is stored as custom campaign field.
                CampaignManager.createUpdateCampainDetails(campBean.getCampaignNumber(), campBean.getCampaignDetail());
            }

            System.out.println("[TriggerManager]: WEB_VERSION_SAVED campNum:"+campBean.getCampaignNumber()+", userId:"+userInfo.getUserID()+", fileName:"+dirFileName);
            return fileName;

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * @param messageId
     * @return
     */
    public static TriggerMessageBean getTriggerMessageById(int messageId) {

        try {
            return complexQueryServer2.getTriggerMessageById(messageId);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return null;
    }
    /**
     * @param userId
     * @return
     */
    public static Vector getTriggerTrackCampaignsNV(String userId) {

        try {
            return complexQueryServer2.getTriggerTrackCampaignsNV(userId);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return new Vector();
    }

    /**
     * Search & process subscribers for TriggerTracks that are using Behavior Filter matching the activityType provided.
     *
     * @param subActivityHistoryVec - Vector of SubscriberActivityHistoryBean
     * @param activityType - 'WV', 'CK' etc.
     *
    public static void processForBehaviorTriggerTracks(Vector subAHVec, String activityType) {

        if(subAHVec.isEmpty() || activityType.equals(""))
            return;

        String behaviorFilterType = "";
        //==== Convert Subscriber activityType into BehaviourFilter type. ====
        if(activityType.equalsIgnoreCase(SubscriberManager.ACTIVITY_ARTICLE_CLICK))
            behaviorFilterType = PMSDefinitions.BEHAVIOUR_FILTER_EMAIL;

        else if(activityType.equalsIgnoreCase(SubscriberManager.ACTIVITY_CAMPAIGN_OPEN))
            behaviorFilterType = PMSDefinitions.BEHAVIOUR_FILTER_EMAIL;

        else if(activityType.equalsIgnoreCase(SubscriberManager.ACTIVITY_SCORE_CHANGE))
            behaviorFilterType = PMSDefinitions.BEHAVIOUR_FILTER_SCORE;

        else if(activityType.equalsIgnoreCase(SubscriberManager.ACTIVITY_SIGN_UP))
            behaviorFilterType = PMSDefinitions.BEHAVIOUR_FILTER_FORM_SUBMISSION;

        else if(activityType.equalsIgnoreCase(SubscriberManager.ACTIVITY_WEB_VISIT))
            behaviorFilterType = PMSDefinitions.BEHAVIOUR_FILTER_WEB_VISIT;

        else {
            return;//Others SubscriberActivity types either not implemented as BehaviorFilters OR unknown activityType found.
        }

        Hashtable userSubNumHash = new Hashtable();
        try {
            //===== Group all activities by UserId. =====
            for(int i=0; i<subAHVec.size(); i++) {
                SubscriberActivityHistoryBean bean = (SubscriberActivityHistoryBean)subAHVec.get(i);

                if(userSubNumHash.containsKey(bean.getUserId())) {
                    ((Vector)userSubNumHash.get(bean.getUserId())).add(""+bean.getSubscriberNumber());
                } else {
                    Vector vec = new Vector();
                    vec.add(""+bean.getSubscriberNumber());
                    userSubNumHash.put(bean.getUserId(), vec);
                }
            }

            //===== Process subscribers every user. =====
            Enumeration userEnum = userSubNumHash.keys();
            while(userEnum.hasMoreElements()) {

                String userId = (String)userEnum.nextElement();
                if(userId==null || userId.trim().equals(""))
                    continue;

                Vector subNumVec = (Vector)userSubNumHash.get(userId);

                //--- send group to process
                if(!behaviorFilterType.equals("") && subNumVec.size()>0)
                    complexQueryServer2.processSubsForBehaviourTriggerTracks(userId, subNumVec, behaviorFilterType);
            }

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
    }*/
    /**
     *
     * @param trackId
     * @param subNumVec
     * @return
     */
    public static Hashtable isManuallyRemovedFromTriggerTrack(int trackId, Vector subNumVec) {

        try {
            return complexQueryServer2.isManuallyRemovedFromTriggerTrack(trackId, subNumVec);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Hashtable();
    }
    /**
     *
     * @param trackId
     * @param subNumVec
     * @param userIdUpdated
     * @param isPaused
     */
    public static void manuallyRemoveFromTriggerTrack(int trackId, Vector subNumVec, String userIdUpdated, boolean isPaused) {

        try {
            complexQueryServer2.manuallyRemoveFromTriggerTrack(trackId, subNumVec, userIdUpdated, isPaused);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
    }
    /**
     *
     * @param trackId
     * @param subNumVec
     * @return
     */
    public static boolean undoManualRemovalFromTriggerTrack(int trackId, Vector subNumVec) {

        try {
            return complexQueryServer2.undoManualRemovalFromTriggerTrack(trackId, subNumVec);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     *
     * @param trackId
     * @param type
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector getManuallyRemovedFromTriggerTrack(int trackId, String type, int offset, int bucket) {

        try {
            return complexQueryServer2.getManuallyRemovedFromTriggerTrack(trackId, type, offset, bucket);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }
    /**
     *
     * @param trackId
     * @return
     */
    public static Vector countManuallyRemovedFromWorkflow(int trackId) {

        try {
            return complexQueryServer2.countManuallyRemovedFromTriggerTrack(trackId);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }
    /**
     *
     * @param subNum
     * @param trackId
     * @param messageId
     * @return
     */
    public static boolean skipSubsForTriggerMessage(long subNum, int trackId, int messageId) {

        try {
            return complexQueryServer2.skipSubsForTriggerMessage(subNum, trackId, messageId);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     *
     * @param subNum
     * @param trackId
     * @param messageId
     * @return
     */
    public static boolean unskipSubsForTriggerMessage(long subNum, int trackId, int messageId) {

        try {
            return complexQueryServer2.unskipSubsForTriggerMessage(subNum, trackId, messageId);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param subNum
     * @return
     */
    public static Vector getTriggerTracksBySubNum(long subNum) {

        try {
            return complexQueryServer2.getTriggerTracksBySubNum(subNum);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return new Vector();
    }
    /**
     *
     * @param workflowIDVec
     * @param subNum
     * @return
     */
    public static Hashtable getTriggerTrackSummaryBySubNum(Vector trackIDVec, long subNum) {

        try {
            return complexQueryServer2.getTriggerTrackSummaryBySubNum(trackIDVec, subNum);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Hashtable();
    }
    /**
     *
     * @param subNum
     * @return
     */
    public static Hashtable getSkippedTriggerMessagesForSubs(long subNum) {

        try {
            return complexQueryServer2.getSkippedTriggerMessagesForSubs(subNum);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Hashtable();
    }
    /**
     *
     * @param trackId
     * @param stepId
     * @param subNumVec
     * @return
     */
    public static Hashtable getSkippedSubsForTriggerMessage(int trackId, int messageId, Vector subNumVec) {

        try {
            return complexQueryServer2.getSkippedSubsForTriggerMessage(trackId, messageId, subNumVec);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Hashtable();
    }
    /**
     *
     * @param subNumVec
     * @return
     */
    public static Hashtable isTriggerTrackSubs(Vector subNumVec) {

        try {
            return complexQueryServer2.isTriggerTrackSubs(subNumVec);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return new Hashtable();
    }
    /**
     *
     * @param samUserBean
     * @param startDate
     * @param endDate
     * @param offset
     * @param bucket
     * @param orderBy
     * @return
     */
    public static Vector triggerTrackSnapShotBySAMUser(DashboardSAMBean samUserBean, long startDate, long endDate, int offset, int bucket, String orderBy) {

        try {
            return complexQueryServer2.triggerTrackSnapShotBySAMUser(samUserBean, startDate, endDate, offset, bucket, orderBy);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }
    /**
     *
     * @param userId
     * @param startDate
     * @param endDate
     * @param offset
     * @param bucket
     * @param orderBy
     * @return
     */
    public static Vector triggerTrackSnapShotByUserId(String userId, long startDate, long endDate, int offset, int bucket, String orderBy) {

        try {
            return complexQueryServer2.triggerTrackSnapShotByUserId(userId, startDate, endDate, offset, bucket, orderBy);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }
    /**
     *
     * @param samUserBean
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
    public static int countForTriggerTrackSnapShot(DashboardSAMBean samUserBean, String userId, long startDate, long endDate) {

        try {
            return complexQueryServer2.countForTriggerTrackSnapShot(samUserBean, userId, startDate, endDate);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return 0;
    }
    /**
     * 
     * @param userId
     * @param searchType
     * @param searchTxt
     * @return
     */
    public static Vector getTriggerTracksBySearchTxt(String userId, String searchType, String searchTxt) {
	
        try {
            return complexQueryServer2.getTriggerTracksBySearchTxt(userId, searchType, searchTxt);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }
    /**
     * 
     * @param userId
     * @param searchType
     * @param searchTxt
     * @return
     */
    public static int getCountTriggerTrackBySearchTxt(String userId, String searchType, String searchTxt) {

	try {
	    return complexQueryServer2.getCountTriggerTrackBySearchTxt(userId, searchType, searchTxt);

	} catch(Exception ex) {
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
    public static Vector getTriggerTrackHeaderStats(String userId) {

        try {
            return complexQueryServer2.getTriggerTrackHeaderStats(userId);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }
    /**
     * 
     * @param userId
     * @param trackId
     * @return
     */
    public static Vector getTrackDispensingStats(String userId, int trackId) {

        try {
            return complexQueryServer2.getTrackDispensingStats(userId, trackId);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }
    /**
     * 
     * @param userId
     * @param trackId
     * @param messageId
     * @param viewType
     * @param searchValue
     * @param skipType
     * @param startDate
     * @param endDate
     * @return
     */
    public static int countTriggerMessagePopulation(String userId, int trackId, int messageId, String viewType, String searchValue, String skipType, long startDate, long endDate) {

	try {
	    return reportQueryServer.countTriggerMessagePopulation(userId, trackId, messageId, viewType, searchValue, skipType, startDate, endDate);

	} catch(Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;
    }
    /**
     * 
     * @param userId
     * @param trackId
     * @param messageId
     * @param viewType
     * @param searchValue
     * @param skipType
     * @param startDate
     * @param endDate
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector getTriggerMessagePopulation(String userId, int trackId, int messageId, String viewType, String searchValue, String skipType, long startDate, long endDate
	    , int offset, int bucket) {

	try {
	    return reportQueryServer.getTriggerMessagePopulation(userId, trackId, messageId, viewType, searchValue, skipType, startDate, endDate, offset, bucket);

	} catch(Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }

}
