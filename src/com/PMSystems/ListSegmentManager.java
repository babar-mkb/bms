package com.PMSystems;

import java.util.*;

import java.net.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;

import com.PMSystems.*;
import com.PMSystems.beans.*;
import com.PMSystems.dbbeans.*;
import com.PMSystems.util.*;
import com.PMSystems.template.*;
import com.PMSystems.sejbs.*;
import com.PMSystems.logger.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: </p>
 *
 * @author Ahmad Suhaib
 * @version 1.0
 */
public class ListSegmentManager {

    private static AlphaQueryServer alphaServer;
    private static DataQueryServer dataServer;
    private static ComplexQueryServer complexQueryServer;

    public static Vector pmsFieldVec = new Vector();
    public static Vector pmsRuleVec = new Vector();
    public static Vector pmsFormatVec = new Vector();
    public static Vector pmsRecurPeriodVec = new Vector();

    static {
      complexQueryServer = EJBHomesFactory.getComplexQueryServerRemote();
      dataServer = EJBHomesFactory.getDataQueryServerRemote();
      alphaServer = EJBHomesFactory.getAlphaQueryServerRemote();

      pmsFieldVec.add(new NameValue("{{EMAIL_ADDR}}", "Email"));
      pmsFieldVec.add(new NameValue("{{CITY}}", "City"));
      pmsFieldVec.add(new NameValue("{{STATE}}", "State"));
      pmsFieldVec.add(new NameValue("{{ZIP}}", "Zip"));
      pmsFieldVec.add(new NameValue("{{COUNTRY}}", "Country"));
      pmsFieldVec.add(new NameValue("{{PHONE}}", "Telephone"));

      pmsFieldVec.add(new NameValue("{{OCCUPATION}}", "Occupation"));
      pmsFieldVec.add(new NameValue("{{INDUSTRY}}", "Industry"));
      pmsFieldVec.add(new NameValue("{{COMPANY}}", "Company"));
      pmsFieldVec.add(new NameValue("{{SOURCE}}", "Source"));
      pmsFieldVec.add(new NameValue("{{SALESREP}}", "Sales Rep"));
      pmsFieldVec.add(new NameValue("{{SALESSTATUS}}", "Sales Status"));

      pmsRuleVec.add(new NameValue("=", "equal to"));
      pmsRuleVec.add(new NameValue("!=", "not equal to"));
      pmsRuleVec.add(new NameValue("ct", "contains"));
      pmsRuleVec.add(new NameValue("nct", "does not contain"));

      pmsRuleVec.add(new NameValue("dr", "within date range"));
      pmsRuleVec.add(new NameValue("nr", "within numeric range"));

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

      pmsRecurPeriodVec.add(new NameValue("1", "Daily"));
      pmsRecurPeriodVec.add(new NameValue("2", "Every 2 Days"));
      pmsRecurPeriodVec.add(new NameValue("3", "Every 3 Days"));
      pmsRecurPeriodVec.add(new NameValue("4", "Every 4 Days"));
      pmsRecurPeriodVec.add(new NameValue("5", "Every 5 Days"));
      pmsRecurPeriodVec.add(new NameValue("6", "Every 6 Days"));
      pmsRecurPeriodVec.add(new NameValue("7", "Weekly"));
      pmsRecurPeriodVec.add(new NameValue("30", "Monthly"));

    }

    private ListSegmentManager() {
    }

    private static void debug(String str) { AppServerLogger.getLogger().log(new LogEntry("", "", str)); }
    private static void error(Throwable th) { AppServerLogger.getLogger().log(th); }

    /**
     *
     * @param segmentNumber
     * @param userId
     * @return
     */
    public static ListSegmentBean getListSegmentByNum(int segmentNumber) {

        try {
            return dataServer.getListSegmentByNum(segmentNumber);
        } catch (Exception ex) {
            error(ex);
        }
        return null;
    }

    /**
     *
     * @param segmentNumberVec
     * @param isParentOnly
     * @return
     */
    public static Vector getListSegmentBeanVec(Vector segNumVec, boolean tmpOnly) {

        Vector segVec = new Vector();
        if(segNumVec==null || segNumVec.isEmpty())
            return segVec;

        try {
            return dataServer.getListSegmentByNum(segNumVec);
        } catch (Exception ex) {
            error(ex);
        }
        return segVec;
    }

    /**
     *
     * @param segmentNumber
     * @param userId
     * @return
     */
    public static boolean deleteListSegmentBean(int segmentNumber, String userId) {
        try {
            return dataServer.deleteSegment(segmentNumber, userId);
        } catch (Exception ex) {
            error(ex);
        }
        return false;
    }

    /**
     * @param userId
     * @return
     */
    public static Vector getListSegmentByUser(String userId) {

        try {
            return dataServer.getListSegmentByUserId(userId);
        } catch (Exception ex) {
            error(ex);
        }
        return new Vector();
    }
    /**
     * @param samUserBean
     * @return
     */
    public static Vector getListSegmentsForSAM(DashboardSAMBean samUserBean) {

        try {
            return dataServer.getListSegmentsForSAM(samUserBean);
        } catch (Exception ex) {
            error(ex);
        }
        return new Vector();
    }

    /**
     * @param bean
     * @return
     */
    public static boolean updateListSegmentBean(ListSegmentBean bean) {

        try {
            return dataServer.updateListSegment(bean);
        } catch (Exception ex) {
            error(ex);
        }
        return false;
    }
    /**
     * @param segmentNumber
     * @param filterNum
     * @return
     */
    public static boolean setListSegmentBehaviorFilterNum(int segmentNumber, int filterNum) {

        try {
            return dataServer.setListSegmentBehaviorFilterNum(segmentNumber, filterNum);
        } catch (Exception ex) {
            error(ex);
        }
        return false;
    }

    /**
     * Check
     * @param userId
     * @param segName
     * @return
     */
    public static boolean isListSegmentExists(String userId, int originalSegmentNumber, String segName) {
        try {
            return dataServer.isListSegmentExists(userId, originalSegmentNumber, segName);
        } catch (Exception ex) {
            error(ex);
        }
        return false;
    }

    /**
     *
     * @param bean
     * @return
     */
    public static ListSegmentBean createListSegmentBean(ListSegmentBean bean) {
        try {
            return dataServer.createListSegment(bean);
        } catch (Exception ex) {
            error(ex);
        }
        return null;
    }
    /**
     * @param campNum
     * @param segNumVec
     * @return
     */
    public static boolean updateCampListSegments(long campNum, Vector segNumVec) {
        try {
            return dataServer.updateCampListSegments(campNum, segNumVec);
        } catch (Exception ex) {
            error(ex);
        }
        return false;
    }

    /**
     *
     * @param campNum
     * @return
     */
    public static Vector getCampListSegments(long campNum) {
        try {
            return dataServer.getCampListSegments(campNum);
        } catch (Exception ex) {
            error(ex);
        }
        return new Vector();
    }

    /**
    *
    */
    public static String getFieldName(String str) {

        int index = ListSegmentManager.pmsFieldVec.indexOf(new NameValue(str, ""));
        if(index!=-1) {
            return ((NameValue)ListSegmentManager.pmsFieldVec.get(index)).getValue();
        }
        return removeBraces(str);
    }

    /**
    *
    */
    public static String getRuleName(String str) {
        int index = ListSegmentManager.pmsRuleVec.indexOf(new NameValue(str, ""));
        if(index!=-1) {
            return ((NameValue)ListSegmentManager.pmsRuleVec.get(index)).getValue();
        }
        return "n/a";
    }

    public static String getFormatName(String str) {
        int index = ListSegmentManager.pmsFormatVec.indexOf(new NameValue(str, ""));
        if(index!=-1) {
            return str.toUpperCase();
        }
        return "n/a";
    }

    public static String removeBraces(String str) {

        if(str==null)
            return "";

        if(str.startsWith("{{9*9_"))
            str = str.substring("{{9*9_".length(), str.length());

        if(str.startsWith("{{"))
            str = str.substring(2, str.length());

        if(str.endsWith("}}"))
            str = str.substring(0, str.indexOf("}}"));

        return str;
    }

    public static String getRecurPeriodName(String str) {
        int index = ListSegmentManager.pmsRecurPeriodVec.indexOf(new NameValue(str, ""));
        if(index!=-1) {
            return ((NameValue)ListSegmentManager.pmsRecurPeriodVec.get(index)).getValue();
        }
        return "-";
    }

    /**
    *
    */
    public static String getListName(String number, Vector listVec) {

        for(int i=0; i<listVec.size(); i++) {
            ListDataBean listBean = (ListDataBean)listVec.get(i);
            if(Default.defaultInt(number)==listBean.getListNumber())
                return listBean.getName();
        }
        return "-";
    }

    /**
     *
     * @param segVec
     * @param inAscendingOrder
     * @return
     */
    public static Vector orderSegmentsByName(Vector segVec, boolean inAscendingOrder) {

        Vector vec = new Vector();
        try {
            Vector nameVec = new Vector();
            Hashtable segHash = new Hashtable();
            for(int i=0; i<segVec.size(); i++) {
                ListSegmentBean bean = (ListSegmentBean)segVec.get(i);
                nameVec.add(bean.getName());
                segHash.put(bean.getName(), bean);
            }
            Collections.sort(nameVec);

            if(inAscendingOrder) {
                for(int i=0; i<nameVec.size(); i++) {
                    String name = (String)nameVec.get(i);
                    vec.add(segHash.get(name));
                }
            } else {
                for(int i=(nameVec.size()-1); i>=0; i--) {
                    String name = (String)nameVec.get(i);
                    vec.add(segHash.get(name));
                }
            }
        } catch( Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return vec;
    }

    /**
     *
     * @param segVec
     * @param inAscendingOrder
     * @return
     */
    public static Vector orderSegmentsByProcessedDate(Vector segVec, boolean inAscendingOrder) {

        Vector vec = new Vector();
        try {
            Vector timeVec = new Vector();
            Hashtable segHash = new Hashtable();
            for(int i=0; i<segVec.size(); i++) {
                ListSegmentBean bean = (ListSegmentBean)segVec.get(i);

                long lastProcDate = 0l;
                lastProcDate = ((getRecurLastProcessedDate(bean)==null)? System.currentTimeMillis(): getRecurLastProcessedDate(bean).getTime());
                NameValue nv = new NameValue(""+lastProcDate, ""+bean.getSegmentNumber());

                timeVec.add(nv);
                segHash.put(""+bean.getSegmentNumber(), bean);
            }
            Collections.sort(timeVec);

            if(inAscendingOrder) {
                for(int i=0; i<timeVec.size(); i++) {
                    NameValue nv = (NameValue)timeVec.get(i);
                    vec.add(segHash.get(nv.getValue()));
                }
            } else {
                for(int i=(timeVec.size()-1); i>=0; i--) {
                    NameValue nv = (NameValue)timeVec.get(i);
                    vec.add(segHash.get(nv.getValue()));
                }
            }
        } catch( Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return vec;
    }

    /**
     *
     * @param segVec
     * @param inAscendingOrder
     * @return
     */
    public static Vector orderSegmentsBySubscriberCount(Vector segVec, boolean inAscendingOrder) {

        Vector vec = new Vector();
        try {
            Vector countVec = new Vector();
            Hashtable segHash = new Hashtable();

            for(int i=0; i<segVec.size(); i++) {
                ListSegmentBean bean = (ListSegmentBean)segVec.get(i);
                NameValue nv = new NameValue(""+bean.getCurrentCount(), ""+bean.getSegmentNumber());
                countVec.add(nv);
                segHash.put(""+bean.getSegmentNumber(), bean);
            }
            Collections.sort(countVec);

            if(inAscendingOrder) {
                for(int i=0; i<countVec.size(); i++) {
                    NameValue nv = (NameValue)countVec.get(i);
                    vec.add(segHash.get(nv.getValue()));
                }
            } else {
                for(int i=(countVec.size()-1); i>=0; i--) {
                    NameValue nv = (NameValue)countVec.get(i);
                    vec.add(segHash.get(nv.getValue()));
                }
            }
        } catch( Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return vec;
    }

    /**
     *
     * @return
     */
    public static String getChangeFactor(ListSegmentBean bean) {

        String changeFactor = "";
        try {
            if(bean.isEditable())
                return "-";

            int latestCount = bean.getCurrentCount();
            int secondLatestCount = (bean.getListSubsCountVec().size()>0)? Default.defaultInt(((NameValue)bean.getListSubsCountVec().get(0)).getValue()): 0;
            if(secondLatestCount==0)
                return "-";

            return getChangeFactor(latestCount, secondLatestCount);

        } catch( Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return changeFactor;
    }
    /**
     *
     * @param latestCount
     * @param secondLatestCount
     * @return
     */
    public static String getChangeFactor(int latestCount, int secondLatestCount) {

        String changeFactor = "";
        try {
            int diff = (latestCount-secondLatestCount);
            changeFactor = ((diff>=0)? "+"+diff: ""+diff);
            diff = (diff<0)? diff*(-1): diff;
            float value = diff;
            changeFactor += "("+(Math.round((value/secondLatestCount)*100)+"%")+")";

        } catch( Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return changeFactor;
    }
    /**
     *
     * @param bean
     * @return
     */
    public static java.util.Date getRecurLastProcessedDate(ListSegmentBean bean) {
            return bean.getLastProcessedDate();
    }
    /**
     *
     * @param userId
     * @param segmentNumber
     * @return
     */
    public static boolean playSegment(String userId, int segmentNumber) {

        if(userId.equals("") || segmentNumber<=0)
            return false;

        try {
            ListSegmentBean bean = getListSegmentByNum(segmentNumber);
            if(bean==null || !bean.getUserID().equalsIgnoreCase(userId))
                return false;

            return complexQueryServer.markSegmentActive(segmentNumber);

        } catch (Exception ex) {
            error(ex);
        }
        return false;
    }
    /**
     * @param userId
     * @param segmentNumber
     * @return
     */
    public static boolean pauseSegment(String userId, int segmentNumber) {

        if(userId.equals("") || segmentNumber<=0)
            return false;

        try {
            ListSegmentBean bean = getListSegmentByNum(segmentNumber);
            if(bean==null || !bean.getUserID().equalsIgnoreCase(userId))
                return false;

            return complexQueryServer.markSegmentInactive(segmentNumber);

        } catch (Exception ex) {
            error(ex);
        }
        return false;
    }
    /**
     * @param segmentNumber
     * @param fromDate
     * @param toDate
     * @return
     */
    public static int countSegmentSubsByDateRange(int segmentNumber, long fromDate, long toDate) {

        if(segmentNumber<=0)
            return 0;
        try {
            return complexQueryServer.countSegmentSubsByDateRange(segmentNumber, fromDate, toDate);

        } catch (Exception ex) {
            error(ex);
        }
        return 0;
    }
    /**
     *
     * @param samUserBean
     * @param segmentNumber
     * @param fromDate
     * @param toDate
     * @return
     */
    public static int countSegmentSubsByDateRangeForSAM(DashboardSAMBean samUserBean, int segmentNumber, long fromDate, long toDate) {

        if(segmentNumber<=0 || samUserBean==null)
            return 0;

        try {
            return complexQueryServer.countSegmentSubsByDateRangeForSAM(samUserBean, segmentNumber, fromDate, toDate);

        } catch (Exception ex) {
            error(ex);
        }
        return 0;
    }

    /**
     * @param segmentNumber
     * @param fromDate
     * @param toDate
     * @return
     */
    public static Vector viewSegmentSubsByDateRange(String dataUserId, int segmentNumber, int offset, int bucket, long fromDate, long toDate, boolean isSAM) {

        if(segmentNumber<=0)
            return new Vector();
        try {
            return complexQueryServer.viewSegmentSubsByDateRange(dataUserId, segmentNumber, offset, bucket, fromDate, toDate, isSAM);

        } catch (Exception ex) {
            error(ex);
        }
        return new Vector();
    }
    /**
     * @param samUserBean
     * @param segmentNumber
     * @param offset
     * @param bucket
     * @param fromDate
     * @param toDate
     * @return
     */
    public static Vector viewSegmentSubsByDateRangeForSAM(DashboardSAMBean samUserBean, int segmentNumber, int offset, int bucket, long fromDate, long toDate, boolean isSAM) {

        if(segmentNumber<=0 || samUserBean==null)
            return new Vector();
        try {
            return complexQueryServer.viewSegmentSubsByDateRangeForSAM(samUserBean, segmentNumber, offset, bucket, fromDate, toDate, isSAM);

        } catch (Exception ex) {
            error(ex);
        }
        return new Vector();
    }
    /**
     *
     * @param segmentNumber
     * @param startDate
     * @param endDate
     * @return
     */
    public static Vector getListSegmentCountByDateRange(int segmentNumber, long startDate, long endDate) {

        if(segmentNumber<=0 || startDate<=0 || endDate<=0)
            return new Vector();
        try {
            return alphaServer.getListSegmentCountByDateRange(segmentNumber, startDate, endDate);

        } catch (Exception ex) {
            error(ex);
        }
        return new Vector();
    }
    /**
     *
     * @param bean
     * @return
     */
    public static InstantSegmentActionBean createInstantSegmentAction(InstantSegmentActionBean bean) {

        try {
            return alphaServer.createInstantSegmentAction(bean);

        } catch (Exception ex) {
            error(ex);
        }
        return null;
    }
    /**
     *
     * @param bean
     * @return
     */
    public static boolean updateInstantSegmentAction(InstantSegmentActionBean bean) {

        if(bean==null || bean.getInstantActionId()<=0)
            return false;

        try {
            return alphaServer.updateInstantSegmentAction(bean);

        } catch (Exception ex) {
            error(ex);
        }
        return false;
    }
    /**
     *
     * @param instantActionId
     * @return
     * @throws RemoteException
     */
    public static boolean deleteInstantSegmentAction(int instantActionId) {

        try {
            return alphaServer.deleteInstantSegmentAction(instantActionId);

        } catch (Exception ex) {
            error(ex);
        }
        return false;
    }
    /**
     *
     * @param segmentNumber
     * @param status
     * @return
     * @throws RemoteException
     */
    public static Vector getInstantSegmentActionsBySegNumWithStatus(int segmentNumber, String status) {

        if(segmentNumber<=0)
            return new Vector();

        try {
            return alphaServer.getInstantSegmentActionsBySegNum(segmentNumber, status);

        } catch (Exception ex) {
            error(ex);
        }
        return new Vector();
    }
    /**
     *
     * @param segmentNumber
     * @return
     */
    public static Vector getInstantSegmentActionsBySegNum(int segmentNumber) {

        if(segmentNumber<=0)
            return new Vector();

        try {
            return alphaServer.getInstantSegmentActionsBySegNum(segmentNumber, "");

        } catch (Exception ex) {
            error(ex);
        }
        return new Vector();
    }
    /**
     *
     * @param instantActionId
     * @return
     */
    public static InstantSegmentActionBean getInstantSegmentActionsById(int instantActionId) {

        try {
            return alphaServer.getInstantSegmentActionsById(instantActionId);

        } catch (Exception ex) {
            error(ex);
        }
        return null;
    }
    /**
     *
     * @param instantActionId
     * @return
     */
    public static boolean markInstantSegmentActionActive(int instantActionId) {

        try {
            return alphaServer.markInstantSegmentActionActive(instantActionId);

        } catch (Exception ex) {
            error(ex);
        }
        return false;
    }
}