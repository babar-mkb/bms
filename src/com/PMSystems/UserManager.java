package com.PMSystems;

import com.PMSystems.sejbs.*;
import com.PMSystems.util.*;

import java.rmi.RemoteException;
import java.util.*;

import com.PMSystems.logger.*;
import com.PMSystems.beans.*;
import com.PMSystems.dbbeans.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class UserManager {

    private static AlphaQueryServer alphaServer;
    private static BMSQueryServer bmsServer;	
    private static ReportsQueryServer reportQueryServer;
    private static DataQueryServer dataServer;
    private static ComplexQueryServer complexQueryServer;

    private static String[] defSources = {"WebSite","Email","TradeShow","Search Engine","Friend","Other"};

    static{
	alphaServer = EJBHomesFactory.getAlphaQueryServerRemote();
	reportQueryServer = EJBHomesFactory.getReportsQueryServerRemote();
	dataServer = EJBHomesFactory.getDataQueryServerRemote();
	complexQueryServer = EJBHomesFactory.getComplexQueryServerRemote();
	bmsServer = EJBHomesFactory.getBMSQueryServerRemote();		
    }

    /**
     *
     * @param userID
     * @return All salesRep Names
     */
    public static Vector getSalesReps(String userID){
	Vector salesRepVec = new Vector();
	try{
	    SalesRep[] userSalesReps = reportQueryServer.getUserSalesReps(userID);
	    for(int i=0;userSalesReps!=null && i<userSalesReps.length;i++){
		salesRepVec.add(userSalesReps[i].getSalesRep());
	    }
	}catch(Exception ex){
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return salesRepVec;
    }

    /**
     *
     * @param userID
     * @return
     */
    public static Vector getSources(String userID){
	Vector sourceVec = new Vector();
	try{
	    if(userID == null || userID.equals(""))
		return sourceVec;
	    sourceVec = dataServer.getUserSources(userID);
	    for(int i=0;i<defSources.length;i++){
		if(!sourceVec.contains(defSources[i]))
		    sourceVec.add(defSources[i]);
	    }

	}catch(Exception ex){
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return sourceVec;
    }

    /**
     *
     * @param userId
     * @param newPass
     * @return
     */
    public static boolean changePassword(String userId, String newPass) {

	try {
	    return dataServer.changePassword(userId, newPass);

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return false;
    }


    /**
     *
     * @param userId
     * @param oldPass
     * @param newPass
     * @return
     */
    public static boolean changePassword(String userId, String oldPass, String newPass) {

	try {
	    return dataServer.changePassword(userId, oldPass, newPass);

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return false;
    }


    /**
     *
     * @param userId
     * @param email
     * @return
     */
    public static boolean isValidAcc(String userId, String email) {

	try {
	    return dataServer.isValidAcc(userId, email);
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return false;
    }

    public static boolean isValidAcc(String userId, String email, int customerNumber, String key) {

	try {
	    return dataServer.isValidAcc(userId, email, customerNumber, key);
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return false;
    }
    /**
     *
     * @param userId
     * @param email
     * @return
     */
    public static boolean setPassChangeRequest(String userId, String key) {

	try {
	    return dataServer.setPassChangeRequest(userId, key);
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
    public static UserDataBean getUser(String userId) {

	try {
	    return dataServer.getUser(userId);
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return null;
    }

    /**
     *
     * @param userId
     * @param pass
     * @param key
     * @return
     */
    public static boolean setNewPassword(String userId, String pass, String key) {

	try {
	    return dataServer.setNewPassword(userId, pass, key);
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return false;
    }

    /**
     * Can only be called by SysAdmin user.
     * @return
     */
    public static String[] getAllUserIDs() {

	try {
	    return dataServer.getAllUserIDs();
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new String[0];
    }

    /**
     * Can only be called by SysAdmin user.
     *
     * @param userId
     * @return
     */
    public static UserInfo universalLogin(String userId) {

	UserInfo userInfo = null;
	try {
	    UserDataBean userDataBean = dataServer.getUser(userId);
	    if(userDataBean == null)
		return null;

	    userDataBean.setIsActive(PMSDefinitions.USER_IS_ACTIVE);
	    userInfo = new UserInfo();
	    userInfo.setUserID(userDataBean.getUserID());
	    Long cn = userDataBean.getCustomerNumber();
	    userInfo.setCustomerNumber(cn == null ? 0 : cn.longValue());
	    userInfo.setCustomerLogo(PMSDefinitions.DEFAULT_LOGO);

	    if(userInfo.getCustomerNumber() > 0) {
		CustomerDataBean customerDataBean = dataServer.getCustomer(cn.longValue());
		String logo = customerDataBean.getLogo();
		if(logo!=null && !logo.trim().equals("")) {
		    userInfo.setCustomerLogo(logo);
		}
	    }

	    userInfo.setFirstName(userDataBean.getFirstName());
	    userInfo.setLastName(userDataBean.getLastName());
	    userInfo.setPhone(userDataBean.getPhone());
	    userInfo.setAddress1(userDataBean.getAddressLine1());
	    userInfo.setAddress2(userDataBean.getAddressLine2());
	    userInfo.setEmail(userDataBean.getEmail());

	    userInfo.setSenderName(userDataBean.getSenderName());
	    userInfo.setFromEmail(userDataBean.getFromEmail());
	    userInfo.setUserRole(userDataBean.getUserRole());
	    userInfo.setURL(userDataBean.getUrl());
	    userInfo.setUserLayout(userDataBean.getUserLayout());
	    userInfo.setFormLayout(userDataBean.getFormLayout());
	    userInfo.setSubscriberProfileLayout(userDataBean.getSubscriberProfileLayout());
	    userInfo.setSendAutoReplyEmail(userDataBean.getSendAutoReplyEmail());
	    userInfo.setIsSupressShared(userDataBean.getIsSupressShared());
	    userInfo.setAlertEmail((userDataBean.getAlertEmail()==null || userDataBean.getAlertEmail().equals("")) ?
		    userDataBean.getEmail() : userDataBean.getAlertEmail());
	    userInfo.setReplyToAddress(userDataBean.getReplyToAddress());
	    userInfo.setRoles(userDataBean.getRoles());

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}//end try-catch
	return userInfo;
    }


    /**
     * Get subUser ids of Admin user.
     * @return
     */
    public static Vector getCompanyUsers(long customerNumber) {

	try {
	    return complexQueryServer.getCompanyUsers(new Long(customerNumber));
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
    public static UserInfo getUserInfo(String userId) {

	try {
	    UserDataBean userDataBean =  dataServer.getUser(userId);
	    if(userDataBean != null)
		return ApplicationManager.fetchUserInfoObject(userDataBean);
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return null;
    }



    /**
     *
     * @param userInfo
     * @return
     */
    private static UserDataBean fetchUserDataBean(UserInfo userInfo) {

	if(userInfo ==null || userInfo.getUserID().equals(""))
	    return null;

	UserDataBean bean = new UserDataBean();
	try {

	    bean.setUserID(userInfo.getUserID());
	    bean.setUserKey(userInfo.getUserKey());

	    Long cn = userInfo.getCustomerNumber();
	    bean.setCustomerNumber(cn == null ? 0 : cn.longValue());

	    bean.setFirstName(userInfo.getFirstName());
	    bean.setLastName(userInfo.getLastName());
	    bean.setPhone(userInfo.getPhone());
	    bean.setAddressLine1(userInfo.getAddress1());
	    bean.setAddressLine2(userInfo.getAddress2());
	    bean.setEmail(userInfo.getEmail());
	    bean.setWebAddress(userInfo.getWebAddress());
	    bean.setWebTrack(userInfo.getWebTrack());
	    bean.setEmailCategory(userInfo.getEmailCategory());

	    bean.setSenderName(userInfo.getSenderName());
	    bean.setFromEmail(userInfo.getFromEmail());
	    bean.setUserRole(userInfo.getUserRole());
	    bean.setUrl(userInfo.getURL());
	    bean.setUserLayout(userInfo.getUserLayout());
	    bean.setFormLayout(userInfo.getFormLayout());
	    bean.setSubscriberProfileLayout(userInfo.getSubscriberProfileLayout());
	    bean.setSendAutoReplyEmail(userInfo.getSendAutoReplyEmail());
	    bean.setIsSupressShared(userInfo.getIsSupressShared());
	    bean.setAlertEmail(userInfo.getAlertEmail());
	    bean.setReplyToAddress(userInfo.getReplyToAddress());
	    bean.setRoles(userInfo.getRoles());

	    bean.setUserAppSubs(userInfo.getUserAppSubs());
	    bean.setDetailVec(userInfo.getDetailVec());

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}//end try-catch

	return bean;
    }

    /**
     *
     * @param userKey
     * @return
     */
    public static UserInfo getUserByKey(String userKey) {

	try {
	    UserDataBean userDataBean =  dataServer.getUserByKey(userKey);
	    if(userDataBean != null)
		return ApplicationManager.fetchUserInfoObject(userDataBean);
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return null;
    }


    /**
     *
     * @param userID
     * @return All salesRep Names
     */
    public static Vector getAllSalesRepData(String userID){
	Vector salesRepVec = new Vector();
	try{
	    SalesRep[] userSalesReps = reportQueryServer.getUserSalesReps(userID);
	    for(int i=0;userSalesReps!=null && i<userSalesReps.length;i++){
		salesRepVec.add(userSalesReps[i]);
	    }
	}catch(Exception ex){
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return salesRepVec;
    }

    /**
     *
     * @param userId
     * @param salesRepNumVec
     * @return
     */
    public static boolean addUpdateUserSAMSalesReps(String samUserId,Vector salesRepNumVec){
	try{
	    return dataServer.addUpdateUserSAMSalesReps(samUserId,salesRepNumVec);
	}catch(Exception ex){
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return false;
    }

    /**
     *
     * @param samUserId
     * @return
     */
    public static Vector getSAMSalesRepNumVec(String samUserId){
	Vector salesRepNumVec = new Vector();
	try{
	    return dataServer.getSAMSalesRepNumVec(samUserId);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return salesRepNumVec;
    }

    /**
     *
     * @param samUserId
     * @return
     */
    public static Vector getSAMSalesReps(String samUserId){
	Vector salesRepNumVec = new Vector();
	try{
	    return reportQueryServer.getSAMUserSalesReps(samUserId);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return salesRepNumVec;
    }

    /**
     *
     * @param samUserId
     * @return
     */
    public static boolean deleteSAMSalesReps(String samUserId){
	try{
	    return dataServer.deleteSAMSalesReps(samUserId);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }

    /**
     *
     * @param samUserId
     * @return
     */
    public static String getMasterUserId(String userId){
	try{
	    if(userId == null || userId.equals(""))
		return null;
	    System.out.println("going to get master userId");
	    return reportQueryServer.getMasterUserId(userId);
	} catch(Exception ex){
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
    public static Vector getSAMTemplates(String userId){
	Vector templateVec = new Vector();
	try{
	    if(userId == null || userId.equals(""))
		return templateVec;

	    // Getting SysAdmin templates
	    templateVec = dataServer.getSystemTemplates();
	    // Getting user's own templates
	    Vector vec = dataServer.getMyTemplates(userId);
	    if(vec !=null)
		templateVec.addAll(vec);
	    //Getting Master Account's templates
	    String masterUserId = getMasterUserId(userId);
	    if(!userId.equals(masterUserId)){
		Vector masterVec = dataServer.getMyTemplates(masterUserId);
		if(masterVec!=null)
		    templateVec.addAll(masterVec);
	    }

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return templateVec;
    }


    /**
     *
     * @param customerNum
     * @param emailCategory
     * @return
     */
    public static boolean updateSubUsersEmailCategory(int customerNum, String emailCategory){
	if(customerNum == 0 || emailCategory == null || emailCategory.equals(""))
	    return false;
	try{
	    return dataServer.updateSubUsersEmailCategory(customerNum,emailCategory);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }
    /**
     *
     * @param customerNum
     * @param workflowAccess
     * @return
     */
    public static boolean updateSubUsersDetailField(int customerNum, String fieldName, String fieldValue) {

	if(customerNum == 0)
	    return false;
	try{
	    return dataServer.updateSubUsersDetailField(customerNum, fieldName, fieldValue);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }

    /**
     *
     * @param userId
     * @return
     */
    public static boolean markUserExpired(String userId){
	if(Default.toDefault(userId).equals(""))
	    return false;

	try{
	    return dataServer.markUserExpired(userId);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }
    /**
     * @param campNum
     * @return
     */
    public static String getUserIdByCampNum(long campNum) {

	if(campNum<=0)
	    return "";
	try{
	    return complexQueryServer.getUserIdByCampNum(campNum);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return "";
    }
    /**
     *
     * @param listNum
     * @return
     */
    public static String getUserIdByListNum(long listNum) {

	if(listNum<=0)
	    return "";
	try{
	    return complexQueryServer.getUserIdByListNum(listNum);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return "";
    }
    /**
     *
     * @param userKey
     * @param customerNumber
     * @param bmsTK
     * @return
     */
    public static String getUserSnippet(String userKey, int customerNumber, String bmsTK) {

	StringBuffer buff = new StringBuffer("");
	try {
	    buff.append("<!------- BMS WebStats SNIPPET ------->");
	    buff.append("\n<SCRIPT TYPE=\"text/javascript\">");
	    buff.append("\n var BMS_DID='"+userKey+"';");
	    buff.append("\n var proto =document.location.protocol||'http:';");
	    buff.append("\n proto = proto+'//"+PMSResources.getInstance().getCDNForStaticContents()+"/pms/js/bmstats.js?'+(new Date()).getTime();");
	    buff.append("\n var purl = unescape(\"%3Cscript src='\"+proto+\"' type='text/javascript' %3E%3C/script%3E\");");
	    buff.append("\n document.writeln(purl);");
	    buff.append("\n</SCRIPT>");

	    if(!bmsTK.equals(""))
		buff.append("\n<SCRIPT TYPE=\"text/javascript\">sniffUpTK('"+bmsTK+"');</SCRIPT>");

	    buff.append("\n<!------ BMS WebStats SNIPPET ------>");

	    BridgeStatzBean statzBean = BridgeStatsManager.getBridgeStatsAccForMasterUser(customerNumber);

	    if(statzBean!=null && !statzBean.getBridgeStatsID().equals("")) {
		buff = new StringBuffer();
		buff.append("<!----- BridgeStatz SNIPPET ----->");
		buff.append("\n<SCRIPT TYPE=\"text/javascript\">");
		buff.append("\n var BMS_DID='"+userKey+"'; var DID="+statzBean.getBridgeStatsID()+";");
		buff.append("\n var proto =document.location.protocol||'http:';");
		buff.append("\n proto = proto+'//"+PMSResources.getInstance().getCDNForStaticContents()+"/pms/js/bmstatsCombo.js?'+(new Date()).getTime();");
		buff.append("\n var purl = unescape(\"%3Cscript src='\"+proto+\"' type='text/javascript' %3E%3C/script%3E\");");
		buff.append("\n document.writeln(purl);");
		buff.append("\n</SCRIPT>");
		buff.append("\n<SCRIPT TYPE=\"text/javascript\">var MyID=afetchBMSID(); "+(bmsTK.equals("")? "sniffUp();": "sniffUpTK('"+bmsTK+"');")
			+" SaaS();</SCRIPT>");
		buff.append("\n<!----- BridgeStatz SNIPPET ----->");
	    }
	    return buff.toString().trim();

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return "";
    }
    /**
     *
     * @param userId
     * @return
     */
    public static UserUISettingsBean getUserUISettings(String userId) {

	try{
	    return alphaServer.getUserUISettings(userId);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return null;
    }
    /**
     *
     * @param bean
     * @return
     */
    public static UserUISettingsBean createUpdateUserUISettings(UserUISettingsBean bean) {

	if(bean==null || bean.getUserID().equals(""))
	    return null;

	try{
	    return alphaServer.createUpdateUserUISettings(bean);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return null;
    }
    /**
     *
     * @param userId
     * @param pageId
     * @param nv
     * @return
     */
    public static boolean updatePageUISettings(String userId, String pageId, Vector nvVec) {

	if(userId.equals("") || nvVec==null || nvVec.isEmpty())
	    return false;

	try{
	    return alphaServer.updatePageUISettings(userId, pageId, nvVec);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }

    /**
     *
     * @param bean
     * @return
     */
    public static boolean updateUser(UserDataBean bean) {

	if(bean==null || bean.getUserID().equals(""))
	    return false;

	try{
	    return dataServer.updateUser(bean.getUserID(),bean);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }



    /**
     *
     * @param userInfo
     * @return
     */
    public static boolean updateUser(UserInfo userInfo) {

	if(userInfo==null || userInfo.getUserID().equals(""))
	    return false;

	try{
	    UserDataBean bean = fetchUserDataBean(userInfo);
	    if(bean == null)
		return false;

	    if(updateUser(bean)){
		//== need to update Customer data
		if(bean.getUserRole().equalsIgnoreCase(PMSDefinitions.USER_ADMINISTRATOR_ACCOUNT)){
		    CustomerDataBean custBean = dataServer.getCustomer(bean.getCustomerNumber());
		    if(custBean != null){
			custBean.setLogo(userInfo.getCustomerLogo());

			return reportQueryServer.updateCustomerData(custBean);
		    } else {
			return false;
		    }
		}    		   
		return true;
	    }    	   
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }

    /**
     * 
     * @param customerNumber
     * @return
     */
    public static Vector getCustomerOperatorAccounts(long customerNumber) {

	if(customerNumber <=  0)
	    return new Vector();

	try{
	    return	complexQueryServer.getCustomerOperatorAccounts(customerNumber);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }

    /**
     * 
     * @param opBean
     * @return
     */
    public static UserDataBean addOperatorAccount(UserDataBean opBean) {

	if(opBean == null || opBean.getUserID().equals(""))
	    return null;

	try{
	    return dataServer.createUser(opBean);           
	} catch(Exception ex){
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
    public static boolean deleteOperatorAccount(String userId) {

	if(Default.toDefault(userId).equals(""))
	    return false;

	try{
	    return	complexQueryServer.deleteOperatorAccount(userId);           
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }


    /**
     *
     * @param nvVec
     * @return
     */
    public static String addOneTimeTokenData(Vector nvVec) {
	if(nvVec == null || nvVec.size()==0)
	    return "";
	try{
	    return alphaServer.addOneTimeTokenData(nvVec);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return "";

    }

    /**
     *
     * @param token
     * @return
     */
    public static Vector getOneTimeTokenData(String token){
	if(Default.toDefault(token).equals(""))
	    return new Vector();
	try{
	    return alphaServer.getOneTimeTokenData(token);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();

    }

    /**
     * 	
     * @param userId
     * @param nv
     * @return
     */
    public static boolean createUpdateUserDetailField(String userId, Vector nvVec) {

	if(userId.equals("")|| nvVec ==null || nvVec.size()==0)
	    return false;

	try{
	    return dataServer.createUpdateUserDetailField(userId, nvVec);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }
    /**
     * 
     * @param userId
     * @param tagType
     * @return
     */
    public static Vector getAllUserTags(String userId, String tagType) {

	try {
	    return dataServer.getAllUserTags(userId, tagType);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     * 
     * @param userId
     * @param type
     * @param tagVec
     * @return
     */
    public static boolean addUpdateUserTagsWithColors(String userId, String type, Vector tagVec) {

	try {
	    return alphaServer.addUpdateUserTagsWithColors(userId, type, tagVec); 

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }
    /**
     * 
     * @param userId
     * @param type
     * @param tagVec
     * @return
     */
    public static boolean deleteUserTags(String userId, String type, Vector tagVec) {

	try {
	    return alphaServer.deleteUserTags(userId, type, tagVec); 

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }
    /**
     * 
     * @param userId
     * @param viewType
     * @param type
     * @return
     */
    public static int countUserNotifications(String userId, String notifyType, String eventType, String isViewed) {

	try {
	    return alphaServer.countUserNotifications(userId, notifyType, eventType, isViewed);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;
    }
    /**
     * 
     * @param userId
     * @param isViewed
     * @return
    public static int countUserNotifications(String userId, boolean viewed) {

        try {
            return alphaServer.countUserNotifications(userId, viewed);

        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return 0;
    }*/

    /**
     * 
     * @param notifyIdVec
     * @param viewed
     * @return
     */
    public static boolean updateUserNotifications(Vector notifyIdVec, boolean viewed) {

	try {
	    return alphaServer.updateUserNotifications(notifyIdVec, viewed);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }

    /**
     * 
     * @param userId
     * @param viewType
     * @param type
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector getUserNotifications(String userId, String notifyType, String eventType, String isViewed, int offset, int bucket) {

	try {
	    return alphaServer.getUserNotifications(userId, notifyType, eventType, isViewed, offset, bucket);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }

    /**
     * 
     * @return
     */
    public static Vector getUserAllRoles() {
	Vector roleVec = new Vector();
	try {
	    RoleDataBean[] allRoles = dataServer.getAllRoles();
	    for(int i=0; i<allRoles.length; i++) {
		roleVec.add(allRoles[i]);
	    }

	} catch(Exception e) {
	    e.printStackTrace();
	}
	return roleVec;
    }

    /**
     * 
     * @return
     */
    public static boolean isValidUserID(String userId){
	if(Default.toDefault(userId).equals("") || userId.indexOf("_") != -1 || userId.length()<5)
	    return false;
	return true;
    }

    /**
     * 
     * @param userId
     * @param customerNumber
     * @param monthYear, e.g. '2007-09'
     * @return
     */
    public static int getEmailVolume(String userId, int customerNumber, String yearMonth) {

	if(customerNumber<=0 && userId.equals(""))
	    return 0;

	try {
	    return reportQueryServer.getEmailVolume(userId, customerNumber, yearMonth);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;
    }
    /**
     * 
     * @param userId
     * @param customerNumber
     * @return
     */
    public static int getContactsCount(String userId, int customerNumber, String yearMonth) {

	if(customerNumber<=0 && userId.equals(""))
	    return 0;

	try {
	    return reportQueryServer.getContactsCount(userId, customerNumber, yearMonth);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;
    }
    /**
     * 
     * @param userId
     * @param customerNumber
     * @param yearMonth
     * @return
     */
    public static int getUniqueAutobotPlayCount(String userId, int customerNumber, String yearMonth) {

	if(customerNumber<=0 && userId.equals(""))
	    return 0;

	try {
	    return reportQueryServer.getUniqueAutobotPlayCount(userId, customerNumber, yearMonth);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;
    }
    /**
     * 
     * @param userId
     * @param customerNumber
     * @param yearMonth
     * @return
     */
    public static int getUniqueNTPlayCount(String userId, int customerNumber, String yearMonth) {

	if(customerNumber<=0 && userId.equals(""))
	    return 0;

	try {
	    return reportQueryServer.getUniqueNTPlayCount(userId, customerNumber, yearMonth);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;
    }

    /**
     * ======= SYS ADMIN ========
     * @return
     */
    public static Vector getAllCustomers(int offset, int bucket) {
	return getAllCustomers(offset, bucket, "");
    }
    /**
     * 
     * @param offset
     * @param bucket
     * @param accountType
     * @return
     */
    public static Vector getAllCustomers(int offset, int bucket, String accountType) {

	try {
	    return bmsServer.getAllCustomers(offset, bucket, accountType);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     * 
     * @param customerNumber
     * @return
     */
    public static Vector getAllUsers(int customerNumber) {

	try {
	    return bmsServer.getAllUsers(customerNumber);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }

}