package com.PMSystems;

import java.util.*;
import java.io.*;
import java.sql.Timestamp;

import com.PMSystems.*;
import com.PMSystems.dbbeans.*;
import com.PMSystems.sejbs.*;
import com.PMSystems.container.*;
import com.PMSystems.mail.*;
import com.PMSystems.util.*;
import com.PMSystems.logger.*;

/**
 *
 * <p>Title: </p>
 * <p>Description:
 *
 * ReportsManager - Contains methods needs to display different reports
 *
 * </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class ReportsManager {

    private static ReportsManager theInstance;
    private static DataQueryServer dataQueryServer;
    private static ComplexQueryServer complexQueryServer;
    private static ReportsQueryServer reportsQueryServer;
    public static int[] monthDays = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    static {
        dataQueryServer = EJBHomesFactory.getDataQueryServerRemote();
        complexQueryServer = EJBHomesFactory.getComplexQueryServerRemote();
        reportsQueryServer = EJBHomesFactory.getReportsQueryServerRemote();
    }

    private ReportsManager() {
    }

    /**
     *
     * @param userID
     * @param customerNumber
     * @param userRole
     * @param fromDate
     * @param toDate
     * @param campaignNumber
     * @param isDateRange
     * @return
     */
    public static Vector getAutoTriggerSnapshot(String userID, long customerNumber, String userRole
                                                , String fromDate, String toDate, long campaignNumber, boolean isDateRange) {

        System.out.println("[ReportsManager]===== userId="+userID+", fromDate="+fromDate+", toDate="+toDate+", CampaignNumber="+campaignNumber+" =======");
        try {
            return reportsQueryServer.getAutoTriggerSnapshot(userID, customerNumber, userRole, fromDate, toDate
                ,campaignNumber, isDateRange);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }

    /**
     *
     * @param campaignNumber
     * @param fromDate
     * @param toDate
     * @return
     */
    public static Vector getAllAutoTriggerResponders(long campaignNumber, String fromDate, String toDate) {

        System.out.println("[ReportsManager]===== campaignNumber="+campaignNumber+", fromDate="+fromDate+", toDate="+toDate+" =======");
        try {
            return reportsQueryServer.getAllAutoTriggerRespondersInDateRange(campaignNumber, fromDate, toDate);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }

    /**
     *
     * @param campaignNumber
     * @return
     */
    public static Vector getAllAutoTriggerResponders(long campaignNumber) {

        System.out.println("[ReportsManager]===== campaignNumber="+campaignNumber+" =======");
        try {
            return reportsQueryServer.getAllAutoTriggerResponders(campaignNumber);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }

    /**
     *
     * @param articleNumber
     * @param fromDate
     * @param toDate
     * @return
     */
    public static Vector getArticleClickSubsInDateRange(long articleNumber, String fromDate, String toDate) {
        System.out.println("[ReportsManager]===== articleNumber="+articleNumber+", fromDate="+fromDate+", toDate="+toDate+" =======");
        try {
            return reportsQueryServer.getArticleClickSubsInDateRange(articleNumber,fromDate,toDate);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }

    /**
     *
     * @param articleNumber
     * @return
     */
    public static Vector getArticleClickSubs(long articleNumber) {
        System.out.println("[ReportsManager]===== articleNumber="+articleNumber+" =======");
        try {
            return reportsQueryServer.getArticleClickSubs(articleNumber);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }

    /**
     *
     * @param campaignNumber
     * @param campaignEvent
     * @param fromDate
     * @param toDate
     * @return
     */
    public static Vector getAutoTriggerRespondersInDateRange(long campaignNumber, String campaignEvent, String fromDate, String toDate) {

        System.out.println("[ReportsManager]===== campaignNumber="+campaignNumber+", campaignEvent='"+campaignEvent+"', fromDate="+fromDate+", toDate="+toDate+" =======");
        try {
            return reportsQueryServer.getAutoTriggerRespondersInDateRange(campaignNumber,campaignEvent,fromDate,toDate);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }

    /**
     *
     * @param campaignNumber
     * @param campaignEvent
     * @return
     */
    public static Vector getAutoTriggerResponders(long campaignNumber, String campaignEvent) {

        System.out.println("[ReportsManager]===== campaignNumber="+campaignNumber+", campaignEvent='"+campaignEvent+"' =======");
        try {
            return reportsQueryServer.getAutoTriggerResponders(campaignNumber,campaignEvent);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }

    /**
     *
     * @param userID
     * @param listName
     * @param campaignNumber
     * @param articleNumber
     * @param campaignEvent
     * @param fromDate
     * @param toDate
     * @param isDateRange
     * @return
     */
    public static int createListAndCopySubsForAutoTrigger(String userID, String listName, long campaignNumber, long articleNumber
                             , String tracked, String fromDate, String toDate, boolean isDateRange) {

        System.out.println("[ReportsManager]===== campaignNumber="+campaignNumber+", campaignEvent='"+tracked+"' =======");
        try {
            ListDataBean list = dataQueryServer.getList(userID, listName);
            if(list!=null)
                return -2;

            ListDataBean listNew = new ListDataBean();
            listNew.setUserID(userID);
            listNew.setName(listName);
            listNew.setStatus(PMSDefinitions.LIST_STATUS_ACTIVE);
            listNew.setCreationDate(new Timestamp(System.currentTimeMillis()));
            listNew.setLocked("N");
            listNew.setAutoReplyMessage("Subscription Successful !");
            listNew.setIsDisplayToSubscriber("");
            listNew.setDescForSubscriber("");
            listNew.setUserIDUpdated(userID);
            listNew.setUpdationDate(new Timestamp(System.currentTimeMillis()));
            listNew.setAutoReplyEmailMessage("Subscription Successful !");
            listNew = dataQueryServer.createList(listNew);
            if(listNew==null || listNew.getListNumber()<=0)
                return -3;

            boolean isTracked = (tracked.equalsIgnoreCase("F") || tracked.equalsIgnoreCase("S") || tracked.equalsIgnoreCase("U"));
            boolean isArticle = (articleNumber>0);
            boolean isCampaignNumber = (campaignNumber>0);

            Vector subVec = new Vector();
            if(isDateRange) {
                if(isTracked && isCampaignNumber)
                    subVec = ReportsManager.getAutoTriggerRespondersInDateRange(campaignNumber, ""+tracked, fromDate, toDate);
                else if (isArticle)
                    subVec = ReportsManager.getArticleClickSubsInDateRange(articleNumber, fromDate, toDate);
                else if (isCampaignNumber)
                    subVec = ReportsManager.getAllAutoTriggerResponders(campaignNumber, fromDate, toDate);

            } else {
                if(isTracked && isCampaignNumber)
                    subVec = ReportsManager.getAutoTriggerResponders(campaignNumber, ""+tracked);
                else if (isArticle)
                    subVec = ReportsManager.getArticleClickSubs(articleNumber);
                else if (isCampaignNumber)
                    subVec = ReportsManager.getAllAutoTriggerResponders(campaignNumber);
            }
            if(subVec==null || subVec.isEmpty())
                return 2;

            Vector subNumVec = new Vector();
            for(int i=0; i<subVec.size(); i++) {
                Vector aVec = new Vector();
                aVec.add(""+(((SubscriberInfo)subVec.get(i)).getSubscriberNumber()));
                subNumVec.add(aVec);
            }

            complexQueryServer.saveSegmentedSubscribers(subNumVec, new Long(listNew.getListNumber()));
            return 1;

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return -1;
    }

    /**
     *
     * @param userId
     * @param fromDate
     * @param toDate
     * @return
     */
    public static Vector getAutoTriggerSummaryReport(String userId, String fromDate, String toDate) {
        System.out.println("==== [AutoTriggerSummary] userId: "+userId+", fromDate: "+fromDate+", toDate: "+toDate);
        try {
            return reportsQueryServer.getAutoTriggerSummaryReport(userId, fromDate, toDate);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }

    /**
     * USED for SYSADMIN Interface
     *
     * @param fromDate
     * @param toDate
     * @return
    public static Vector getNormalCompletedCampaignsForAdmin(String fromDate, String toDate) {

        try {
            Vector vec = reportsQueryServer.getCompletedCampaigns(fromDate, toDate);
            for(int i=0; i<vec.size(); i++) {
                if(((CompletedCampaign_CC)vec.get(i)).isAutoTrigger()) {
                    vec.remove(i); //Remove AutoTrigger_CC
                    --i;
                }
            }
            return vec;

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }*/
    public static Vector getNormalCompletedCampaignsForSysAdmin(String userId, int customerNumber, String fromDate, String toDate) {

        try {
            return reportsQueryServer.getNormalCompletedCampaignsForSysAdmin(userId, customerNumber, fromDate, toDate);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }


    /**
     * USED by SYSADMIN Interface
     *
     * @param resultVec
     * @return
     */
    public static Hashtable groupByUserId(Vector resultVec) {

        Hashtable userHash = new Hashtable();
        try {
            Vector userIdVec = new Vector();
            for(int i=0; i<resultVec.size(); i++) {
                CompletedCampaign_CC cc = (CompletedCampaign_CC)resultVec.get(i);
                if(!userHash.containsKey(""+cc.getUserID())) {
                    Vector vec = new Vector();
                    vec.add(cc);
                    userHash.put(""+cc.getUserID(), vec);
                } else {
                    ((Vector)userHash.get(""+cc.getUserID())).add(cc);
                }
            }
            return userHash;

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return userHash;
    }

    /**
     *
     * @param resultVec
     * @return
     */
    public static Hashtable groupByCustomer(Vector resultVec) {

        Hashtable customerHash = new Hashtable();
        try {
            for(int i=0; i<resultVec.size(); i++) {
                CompletedCampaign_CC cc = (CompletedCampaign_CC)resultVec.get(i);

                if(!customerHash.containsKey(""+cc.getCustomerNumber())) {
                    HashSet userIdSet = new HashSet();
                    userIdSet.add(cc.getUserID());
                    customerHash.put(""+cc.getCustomerNumber(), userIdSet);
                } else {
                    ((HashSet)customerHash.get(""+cc.getCustomerNumber())).add(cc.getUserID());
                }
            }
            return customerHash;

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Hashtable();
    }

    /**
     * Returns completecampaign result vec, in order by campaign name.
     *
     * @param resultVec
     * @return
     */
    public static Vector orderByCustomerName(Vector resultVec) {

        Vector sortedNumVec = new Vector();
        try {
            Vector nameVec = new Vector();
            Hashtable nameCustomerHash = new Hashtable();

            for(int i=0; i<resultVec.size(); i++) {
                CompletedCampaign_CC cc = (CompletedCampaign_CC)resultVec.get(i);
                String customerName = Default.toDefault(cc.getCustomerName()).toLowerCase();

                if(!nameVec.contains(customerName)) {
                    nameVec.add(customerName);
                    Vector numVec = new Vector();
                    numVec.add(""+cc.getCustomerNumber());
                    nameCustomerHash.put(customerName, numVec);
                } else {
                    ((Vector)nameCustomerHash.get(customerName)).add(""+cc.getCustomerNumber());
                }
            }

            Collections.sort(nameVec);
            Hashtable newHash = new Hashtable();

            for(int i=0; i<nameVec.size(); i++) {
                String customerName = (String)nameVec.get(i);
                Vector numVec = (Vector)nameCustomerHash.get(customerName);
                for(int j=0; j<numVec.size(); j++) {
                    if(!sortedNumVec.contains((String)numVec.get(j)))
                        sortedNumVec.add((String)numVec.get(j));
                }
            }

            return sortedNumVec;

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }

    /**
     * USED for SYSADMIN Interface
     *
     * @param monthYear
     * @return
     */
    public static Vector getAutoTriggerCompletedCampaignsForAdmin(String monthYear) {

        try {
            return reportsQueryServer.getAutoTriggerCompletedCampaignsForSysAdmin("", 0, monthYear);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }
    /** USED for SYSADMIN Interface
     *
     * @param userId
     * @param customerNumber
     * @param monthYear
     * @return
     */
    public static Vector getAutoTriggerCompletedCampaignsForAdmin(String userId, int customerNumber, String monthYear) {

        try {
            return reportsQueryServer.getAutoTriggerCompletedCampaignsForSysAdmin(userId, customerNumber, monthYear);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }
    /** USED for SYSADMIN Interface
     *
     * @param userId
     * @param customerNumber
     * @param monthYear
     * @return
     */
    public static Vector getWorkflowCompletedCampaignsForAdmin(String userId, int customerNumber, String monthYear) {

        try {
            return reportsQueryServer.getWorkflowCompletedCampaignsForAdmin(userId, customerNumber, monthYear);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }
    /** USED for SYSADMIN Interface
     *
     * @param userId
     * @param customerNumber
     * @param monthYear
     * @return
     */
    public static Vector getTriggerTrackCompletedCampaignsForAdmin(String userId, int customerNumber, String monthYear) {

        try {
            return reportsQueryServer.getTriggerTrackCompletedCampaignsForAdmin(userId, customerNumber, monthYear);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }
    /** USED for SYSADMIN Interface
     *
     * @return
     */
    public static Vector getAllCustomers() {

        try {
            return reportsQueryServer.getAllCustomers();
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }

    /**
     *
     * @param campaignNumber
     * @return
     */
    public static CompletedCampaignDataBean getAutoTriggerSummaryReport(long campaignNumber) {
        System.out.println("==== [AutoTriggerSummary] campaignNumber: "+campaignNumber);
        try {
            return reportsQueryServer.getAutoTriggerSummaryReport(campaignNumber);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new CompletedCampaignDataBean();
    }

    /**
     *
     * @param campaignNumber
     * @param event
     * @param fromDate
     * @param toDate
     * @param isDateRange
     * @return
     */
    public static Vector getAutoTriggerSummaryResponders(long campaignNumber, String event, String fromDate, String toDate, boolean isDateRange) {
        try {
            return reportsQueryServer.getAutoTriggerSummaryResponders(campaignNumber, event, fromDate, toDate, isDateRange);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }
}