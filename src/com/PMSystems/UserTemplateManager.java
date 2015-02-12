package com.PMSystems;

import java.io.*;
import java.rmi.RemoteException;
import java.util.*;

import com.PMSystems.dbbeans.*;
import com.PMSystems.logger.*;
import com.PMSystems.sejbs.*;
import com.PMSystems.util.*;

public class UserTemplateManager {

    private static DataQueryServer dataQueryServer;

    static {
        dataQueryServer = EJBHomesFactory.getDataQueryServerRemote();
    }
    /**
     * 
     * @param utbean
     * @return
     */
    public static boolean createTemplate(UserTemplateDataBean utbean) {

        //====== checking if Trial Template's limit =======
        UserInfo userInfo = UserManager.getUserInfo(utbean.getUserId());
        if(userInfo !=null && userInfo.getPackageType().equals(PMSDefinitions.PACKAGE_TYPE_TRIAL) &&
        	getUserTemplatesCount(Default.toVector(utbean.getUserId())) >= TrialAccountManager.TEMPLATE_LIMIT)
            return false;

        int pk = 0;
        try {
            if((pk = dataQueryServer.createUserTemplate(utbean)) == 0) {
                return false;
            }
            utbean.setTemplateNumber(pk);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
            return false;
        }
        return true;
    }

    public static String getHtmlFile(String userId, String templateName) {
        File file = null;
        FileReader fr=  null;
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        try {
            file = new File(PMSDefinitions.USER_TEMPLATE_DIR + userId + "/" +
                            templateName + ".html");
            if(!file.exists()) {
                file.createNewFile();
            }
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            if (file.exists()) {
                String line = null;
                while ( (line = br.readLine()) != null) {
//                    System.out.println("***** line -> " + line + " <- *****");
                    sb.append(line);
                }
                return sb.toString();
            }
            else {
                System.out.println("----- File does not exist -----");
                return "";
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
            return null;
        }
    }

    /**
     * User Templates in Hashtable
     * @param userId
     * @return
     */
    public static Hashtable getMyTemplates(String userId) {
        Vector beans = new Vector();
        Hashtable table = new Hashtable();

        try {
            beans = dataQueryServer.getMyTemplates(userId);

            if (beans.size() > 0) {

                for (int i = 0; i < beans.size(); i++) {
                    UserTemplateDataBean bean = (UserTemplateDataBean) beans.
                        get(i);
                    table.put(Integer.toString(bean.getTemplateNumber()), bean);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return table;
    }

    public static Vector getMyTemplatesVec(String userId) {
	return getMyTemplatesVec(userId, 0, 0);
    }
    /**
     * User Templates in Vector [order by template name]
     * @param userId
     * @return
     */
    public static Vector getMyTemplatesVec(String userId, int offset, int bucket) {

        try {
            return dataQueryServer.getMyTemplates(userId, offset, bucket);
        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }
    /**
     * 
     * @param userIdVec
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector getUserTemplatesVec(Vector userIdVec, int offset, int bucket) {

        try {
            return dataQueryServer.getUserTemplatesVec(userIdVec, offset, bucket);
        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }
    /**
     * 
     * @param userIdVec
     * @return
     */
    public static int getUserTemplatesCount(Vector userIdVec) {

        try {
            return dataQueryServer.getUserTemplatesCount(userIdVec);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return 0;
    }
    /**
     * System Templates in Hashtable
     * @return
     */
    public static Hashtable getSystemTemplates() {
        Vector beans = new Vector();
        Hashtable table = new Hashtable();
        try {
            beans = dataQueryServer.getSystemTemplates();
            if (beans.size() > 0) {

                for (int i = 0; i < beans.size(); i++) {
                    UserTemplateDataBean bean = (UserTemplateDataBean) beans.
                        get(i);
                    table.put(Integer.toString(bean.getTemplateNumber()), bean);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return table;
    }

    /**
     * System Templates in Vector (order by template name)
     * @return
     */
    public static Vector getSystemTemplatesVec() {

        try {
            return dataQueryServer.getSystemTemplates();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }
    /**
     * 
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector getSystemTemplatesVec(int offset, int bucket) {

        try {
            return dataQueryServer.getSystemTemplates(offset, bucket);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }

    /**
     * @param utBean
     */
    public static boolean updateUserTemplate(UserTemplateDataBean utBean) {
	
        try {
            return dataQueryServer.updateUserTemplate(utBean);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }
    /**
     *
     * @param templateNumber
     * @param isViewCount
     * @param isUsageCount
     * @return
     */
    public static boolean updateUserTemplateStats(int templateNumber, boolean isViewCount, boolean isUsageCount) {

        try {
            return dataQueryServer.updateUserTemplateStats(templateNumber, isViewCount, isUsageCount);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }

    public static boolean deleteUserTemplate(String templateNumber) {
	return deleteUserTemplate(Default.defaultInt(templateNumber));
    }
    /**
     * Deletes template
     * 
     * @param name
     * @param userId
     * @param request
     */
    public static boolean deleteUserTemplate(int templateNumber) {

        try {
            return dataQueryServer.removeUserTemplate(templateNumber);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }

    /**
     * @param templateNumber
     * @return
     */
    public static UserTemplateDataBean getUserTemplate(int templateNumber)  {

        if(templateNumber<=0)
            return null;
        try {
            return dataQueryServer.getUserTemplate(templateNumber);
        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return null;
    }
    /**
     * 
     * @param templateNumber
     * @return
     */
    public static UserTemplateDataBean getUserTemplate(String userId, String templateName)  {

        try {
            return dataQueryServer.getUserTemplate(userId, templateName);
        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return null;
    }
    /**
     * 
     * @param userId
     * @param name
     * @param templateNumber
     * @return
     */
    public static boolean isUserTemplateNameExists(String userId, String name, int templateNumber)  {

	try {
	    return dataQueryServer.isUserTemplateNameExists(userId, name, templateNumber);
	    
        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }

    /**
     * 
     * @param userIdVec
     * @param searchType
     * @param searchText
     * @param layoutId
     * @param categoryId
     * @param orderBy
     * @param isFeatured
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector searchUserTemplates(Vector userIdVec, String searchType, String searchText, int layoutId, String categoryId, String orderBy, String isMEE, String isFeatured
	    , String isMobile, String isReturnPath, int offset, int bucket) {
	
	try {
	    return dataQueryServer.searchUserTemplates(userIdVec, searchType, searchText, layoutId, categoryId, orderBy, isMEE, isFeatured, isMobile, isReturnPath, offset, bucket);
	    
        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }

    /**
     * 
     * @param userIdVec
     * @param searchType
     * @param searchText
     * @param layoutId
     * @param categoryId
     * @param isFeatured
     * @return
     */
    public static int searchUserTemplatesCountOnly(Vector userIdVec, String searchType, String searchText, int layoutId, String categoryId, String orderBy, String isMEE, String isFeatured
	    , String isMobile, String isReturnPath) {
	
	try {
	    return dataQueryServer.searchUserTemplatesCountOnly(userIdVec, searchType, searchText, layoutId, categoryId, orderBy, isMEE, isFeatured, isMobile, isReturnPath);
	    
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
    public static Vector getUserTemplateHeaderStats(String userId) {
	
	try {
	    return dataQueryServer.getUserTemplateHeaderStats(userId);
	    
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
    public static Vector getAllCategoryIds(String userId) {

	try {
	    return dataQueryServer.getAllCategoryIds(userId);
	    
        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }
    /**
     * 
     * @param userInfo
     * @param tempBean
     * @param toEmailVec
     * @return
     */
    public static boolean emailTemplate(UserInfo userInfo, UserTemplateDataBean tempBean, Vector toEmailVec) {

	if(tempBean==null)
	    return false;
	
	try {
	    String previewAlert = "<table width=100%><tr><td align=center style='background-color:#EEE;color:#666699;font-family:arial;font-size:10px;font-weight:bold;'>"
                    +"This template was last edited on "+Default.formatDate(tempBean.getUpdationDate()!=null? tempBean.getUpdationDate(): tempBean.getCreationDate(), "MMMMM dd, yyyy hh:mm aaa z")+".</td></tr></table>";
            
	    return AlertMail.sendMail("Template Preview: "+tempBean.getTemplateName(), previewAlert+tempBean.getTemplateHtml(), userInfo.getFromEmail(), (String[])toEmailVec.toArray(new String[0]), true);
	    
        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }
}