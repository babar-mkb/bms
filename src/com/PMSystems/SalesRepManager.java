package com.PMSystems;

import java.util.*;
import java.io.*;

import com.PMSystems.*;
import com.PMSystems.sejbs.*;
import com.PMSystems.dbbeans.*;
import com.PMSystems.util.*;
import com.PMSystems.logger.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: </p>
 * @author Babar Ali Virk
 * @version 1.0
 */

public class SalesRepManager {

    private static DataQueryServer dataServer;
    private static ReportsQueryServer reportQueryServer = null;

    public static String SALESREP_KEYWORD_PREFIX = "BMS_SALESREP.";
    public static String SALESREP_EMAIL_KEY="{{BMS_SALESREP.EMAIL}}";
    public static String SALESREP_NAME_KEY="{{BMS_SALESREP.NAME}}";
    public static String SALESREP_NUM_KEY="{{BMS_SALESREP.ID}}";
    public static String SALESREP_CREATION_DATE_KEY="{{BMS_SALESREP.CREATION_DATE}}";

    public static Vector basicFieldsNameVec = new Vector();

    static{
        dataServer = EJBHomesFactory.getDataQueryServerRemote();
        reportQueryServer = EJBHomesFactory.getReportsQueryServerRemote();
    }

    public SalesRepManager() {
    }

    public static Vector getBasicFieldsNameVec(){
            if(basicFieldsNameVec.size()>0)
                return basicFieldsNameVec;

            basicFieldsNameVec.add("salesrep");
            basicFieldsNameVec.add("name");
            basicFieldsNameVec.add("email");
            basicFieldsNameVec.add("sendalert");
            basicFieldsNameVec.add("creationdate");
            basicFieldsNameVec.add("updationdate");
            basicFieldsNameVec.add("id");
            basicFieldsNameVec.add("sfid");
            basicFieldsNameVec.add("userid");

            return basicFieldsNameVec;
    }

    /**
     * This method will return sales reps against a user.
     *
     */
    public static Vector getUserSalesReps(String userId,int offset, int bucket) {

        try {
            if(Default.toDefault(userId).equals("") || bucket== 0)
                return new Vector();

            return reportQueryServer.getUserSalesReps(userId,offset,bucket);
        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        } //end try-catch
        return new Vector();
    }

    /**
     * This method will return sales reps count against a user.
     *
     */
    public static int getUserSalesRepsCount(String userId) {

        try {
            if(Default.toDefault(userId).equals(""))
                return 0;

            return reportQueryServer.getUserSalesRepsCount(userId);
        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        } //end try-catch
        return 0;
    }

    /**
     * This method will return sales rep number against a userId & salesrep name.
     *
     */
    public static int getSalesRepNumber(String userId,String name) {

        try {
            if(Default.toDefault(userId).equals("") || Default.toDefault(name).equals(""))
                return 0;

            return reportQueryServer.getSalesRepNumber(userId,name);
        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        } //end try-catch
        return 0;
    }

    /**
     * This method will return sales reps by salesRepNumber
     *
     */
    public static Vector getSalesReps(Vector sRepNumVec) {

        try {
            if(sRepNumVec == null || sRepNumVec.size()==0)
                return new Vector();

            return reportQueryServer.getSalesReps(sRepNumVec);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        } //end try-catch
        return new Vector();
    }


    /**
     * This method will return sales rep by salesRepNumber
     *
     */
    public static SalesRepBean getSalesRep(int salesRepNumber) {

        try {
            if(salesRepNumber<=0)
                return null;

            Vector vec = reportQueryServer.getSalesReps(Default.toVector(""+salesRepNumber));
            return (SalesRepBean)vec.get(0);
        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        } //end try-catch
        return null;
    }

    /**
     *
     * @param userId
     * @param name
     * @return
     */
    public static SalesRepBean getSalesRep(String userId, String name) {

        try {
            if(Default.toDefault(userId).equals("") || Default.toDefault(name).equals(""))
                return null;

            return reportQueryServer.getSalesRep(userId,name);

        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        } //end try-catch
        return null;
    }


    /**
     *
     * @param sRepBean
     * @return
     */
    public static int addSalesRep(SalesRepBean sRepBean) {

        if(sRepBean == null)
            return 0;
        return addSalesReps(Default.toVector(sRepBean));
    }

    /**
     *
     * @param sRepVec
     * @return
     */
    public static int addSalesReps(Vector sRepVec) {

        if(sRepVec == null || sRepVec.size()==0)
            return 0;
        try {
            return reportQueryServer.addSalesRep(sRepVec);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        } //end try-catch
        return 0;
    }

    /**
     *
     * @param sRepBean
     * @return
     */
    public static int updateSalesRep(SalesRepBean sRepBean) {

        if(sRepBean == null)
            return 0;
        return updateSalesReps(Default.toVector(sRepBean));
    }

    /**
     *
     * @param sRepVec
     * @return
     */
    public static int updateSalesReps(Vector sRepVec) {

        if(sRepVec == null || sRepVec.size()==0)
            return 0;
        try {
            return reportQueryServer.updateSalesRep(sRepVec);
        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        } //end try-catch
        return 0;
    }


    /**
     *
     * @param sRepVec
     * @return
     */
    public static NameValue addUpdateSalesReps(Vector sRepVec,Vector updateFieldsVec) {

        if(sRepVec == null || sRepVec.size()==0)
            return null;
        try {
            return reportQueryServer.addUpdateSalesRep(sRepVec,updateFieldsVec);
        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        } //end try-catch
        return null;
    }

    /**
     *
     * @param salesRepNumber
     * @return
     */
    public static boolean removeSalesRep(int salesRepNumber) {
        boolean removed = false;
        try {
            removed = reportQueryServer.removeSalesRep(salesRepNumber);
        } catch (Exception ex) {
            ex.printStackTrace();
        } //end try-catch
        return removed;
    }

    /**
     *
     * @param userId
     * @param sRepNumDel
     * @param sRepNumNew
     * @return
     */
    public static boolean removeSalesRep(String userId, int sRepNumDel, int sRepNumNew) {

        try {
            if(Default.toDefault(userId).equals("") || sRepNumDel == 0)
                return false;

            return reportQueryServer.removeSalesRep(userId,sRepNumDel,sRepNumNew);
        } catch (Exception ex) {
            ex.printStackTrace();
        } //end try-catch
        return false;
    }


    /**
     *
     * @param salesRepName
     * @return
     */
    public static Hashtable getMailingSalesRepHash(SalesRepBean srBean) {

        Hashtable hash = new Hashtable();
        if(srBean==null || srBean.getSalesRepNumber()==0)
            return hash;

        hash.put(SALESREP_NUM_KEY, PMSEncoding.encode(String.valueOf(srBean.getSalesRepNumber())));
        hash.put(SALESREP_EMAIL_KEY, Default.toDefault(srBean.getEmail()));

        if(srBean.getCreationDate()!=null)
            hash.put(SALESREP_CREATION_DATE_KEY, Default.formatDate(srBean.getCreationDate().getTime(), "yyyy-MM-dd"));


        Vector fldDetails = srBean.getCustomFields();
        for(int i=0; fldDetails!=null && i<fldDetails.size(); i++) {
            NameValue nv = (NameValue)fldDetails.elementAt(i);
            hash.put("{{"+SALESREP_KEYWORD_PREFIX+Default.toDefault(nv.getName()).trim()+"}}", Default.toDefault(nv.getValue()));
        }
        return hash;
    }

    
    /**
     * 
     * @param userId
     * @param searchText
     * @param offset
     * @param bucket
     * @return
     */
   public static Vector searchSalesRep(String userId, String searchText, int offset, int bucket) {

       try {
           if(Default.toDefault(userId).equals("") || Default.toDefault(searchText).equals("") || bucket <= 0)
               return new Vector();

           return reportQueryServer.searchSalesRep(userId, searchText, offset, bucket);
       } catch (Exception ex) {
           ex.printStackTrace();
       } //end try-catch
       return new Vector();
   }    
}