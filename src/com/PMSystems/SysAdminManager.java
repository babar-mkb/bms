package com.PMSystems;

import com.PMSystems.*;
import com.PMSystems.sejbs.*;
import com.PMSystems.logger.*;
import com.PMSystems.util.*;
import com.PMSystems.beans.*;
import com.PMSystems.dbbeans.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;


/**
 * This class is developed for SysAdmin ONLY. And ONLY SysAdmin can call methods of this class.
 * @author Ahmad Suhaib.
 * @version 1.0
 */

public class SysAdminManager {

    private static DataQueryServer dataServer;
    private static ComplexQueryServer complexQueryServer;
    private static ComplexQueryServer2 complexQueryServer2;

    public static final String SESSION_USER_OBJs = "admin.userObjs";
    public static final String SESSION_USER_IDs = "admin.userIds";
    public static final String SESSION_CUSTOMER_OBJs = "admin.customerObjs";
    public static final String SYSADMIN_USER_ID = "admin";

    static{
        dataServer = EJBHomesFactory.getDataQueryServerRemote();
        complexQueryServer = EJBHomesFactory.getComplexQueryServerRemote();
        complexQueryServer2 = EJBHomesFactory.getComplexQueryServer2Remote();
    }

    private SysAdminManager() {
    }


    /**
      *
      * @param session
      * @param byDefault
      * @return
      */
     public static boolean refreshUsers(HttpSession session) {

         try {
             Vector userObjVec = dataServer.getAllUserObjects();
             session.setAttribute(SESSION_USER_OBJs, userObjVec);
             return true;

         } catch (Exception ex) {
             WebServerLogger.getLogger().log(ex);
             ex.printStackTrace();
         }
         return false;
     }

     /**
      * @param userObjVec
      * @return
      */
     public static Vector getAllCustomerObjects(Vector userObjVec) {

         try {
             Vector customerNumVec = getCustomerNumbers(userObjVec);
             //System.out.println("[SysAdminManager]: CustomerNumVec.size(): "+customerNumVec.size());
             Vector customerObjVec = dataServer.getCustomer(customerNumVec);
             return customerObjVec;

         } catch (Exception ex) {
             WebServerLogger.getLogger().log(ex);
             ex.printStackTrace();
         }
         return new Vector();
     }

     /**
      * @param customerObjVec
      * @param num
      * @return
      */
     public static CustomerDataBean getCustomerObject(Vector customerObjVec, long num) {

         for(int i=0; i<customerObjVec.size(); i++) {
             CustomerDataBean bean = (CustomerDataBean)customerObjVec.get(i);
             if(bean.getCustomerNumber()==num)
                 return bean;
         }
         return new CustomerDataBean();
     }

     /**
      * @param customerNumber
      * @param userObjVec
      * @return
      */
     public static Vector getCompanyUserObjects(long customerNumber, Vector userObjVec) {

         Vector vec = new Vector();
         for(int i=0; i<userObjVec.size(); i++) {
             UserDataBean bean = (UserDataBean)userObjVec.get(i);
             if(bean.getCustomerNumber().longValue()==customerNumber)
                 vec.add(bean);
         }
         return vec;
     }
     /**
      * @param userId
      * @return
      */
     public static UserDataBean getUserObj(String userId) {

         try {
             return dataServer.getUser(userId);

         } catch (Exception ex) {
             WebServerLogger.getLogger().log(ex);
             ex.printStackTrace();
         }
         return null;
     }

     /**
      * @param num
      * @return
      */
     public static CustomerDataBean getCustomerObj(int num) {

         try {
             return dataServer.getCustomer(num);

         } catch (Exception ex) {
             WebServerLogger.getLogger().log(ex);
             ex.printStackTrace();
         }
         return null;
     }

     /**
      * @param userObjVec
      * @return
      */
     public static Vector getCustomerNumbers(Vector userObjVec) {

         Vector customerNumVec = new Vector();
         for(int i=0; i<userObjVec.size(); i++) {
             UserDataBean userBean = (UserDataBean)userObjVec.get(i);
             customerNumVec.add(""+userBean.getCustomerNumber().longValue());
         }
         return customerNumVec;
     }
     /**
      * @return
      */
     public static SysAdminDetail getSysAdminDetail() {

         try {
             return dataServer.getSysAdminDetails();

         } catch (Exception ex) {
             WebServerLogger.getLogger().log(ex);
             ex.printStackTrace();
         }
         return (new SysAdminDetail());
     }
     /**
      * @param sysDetail
      * @return
      */
     public static boolean createSysAdminDetail(SysAdminDetail sysDetail) {

         try {
             return dataServer.createSysAdminDetails(sysDetail);

         } catch (Exception ex) {
             WebServerLogger.getLogger().log(ex);
             ex.printStackTrace();
         }
         return false;
     }

     /**
      * will be called from Login Page: /pms/login.jsp
      *
      * @return
      */
     public static String getLoginPageMessage() {

         try {
             SysAdminDetail detail = dataServer.getSysAdminDetails();
             if(detail.isShowLoginMsg())
                 return detail.getLoginPageMessage();

         } catch (Exception ex) {
             WebServerLogger.getLogger().log(ex);
             ex.printStackTrace();
         }
         return "";
     }

     /**
      * will be called from Login Page: /pms/newuiheader.jsp
      *
      * @return
      */
     public static String getMaintenanceMsgForHeader() {

         try {
             SysAdminDetail detail = dataServer.getSysAdminDetails();
             if(detail.isShowMaintenanceMsgForHeader())
                 return detail.getMaintenanceMsgForHeader();

         } catch (Exception ex) {
             WebServerLogger.getLogger().log(ex);
             ex.printStackTrace();
         }
         return "";
     }

     /**
      *
      * @param userID
      * @param startDate
      * @param endDate
      * @return
      */
     public static Vector getEvergreenCampaignStatsForAdmin(String userID, java.util.Date startDate, java.util.Date endDate) {

         try {
             return complexQueryServer2.getEvergreenCampaignStatsForAdmin(userID, startDate, endDate);

         } catch (Exception ex) {
             WebServerLogger.getLogger().log(ex);
             ex.printStackTrace();
         }
         return new Vector();
     }
     /**
      *
      * @param userID
      * @param startDate
      * @param endDate
      * @return
      */
     public static Vector getNurtureTrackStatsForAdmin(String userID, Date startDate, Date endDate) {

         try {
             //return complexQueryServer2.getNurtureTrackStatsForAdmin(userID, startDate, endDate);

         } catch (Exception ex) {
             WebServerLogger.getLogger().log(ex);
             ex.printStackTrace();
         }
         return new Vector();
     }

     public static Vector getWorkflowStatsForAdmin(String userID, Date startDate, Date endDate) {

         try {
             //return complexQueryServer2.getWorkflowStatsForAdmin(userID, startDate, endDate);

         } catch (Exception ex) {
             WebServerLogger.getLogger().log(ex);
             ex.printStackTrace();
         }
         return new Vector();
     }

     /**
      * @param userID
      * @param startDate
      * @param endDate
      * @return
      */
     public static Vector getNormalCampaignStatsForAdmin(String userID, java.util.Date startDate, java.util.Date endDate) {

         try {
             return complexQueryServer2.getNormalCampaignStatsForAdmin(userID, startDate, endDate);

         } catch (Exception ex) {
             WebServerLogger.getLogger().log(ex);
             ex.printStackTrace();
         }
         return new Vector();
     }

}

