package com.PMSystems;


import com.PMSystems.sejbs.*;
import com.PMSystems.beans.*;
import com.PMSystems.util.*;
import com.PMSystems.SFIntegration.*;
import com.PMSystems.template.*;
import com.PMSystems.dbbeans.*;
import com.PMSystems.logger.*;

import java.io.*;
import java.rmi.RemoteException;
import java.util.*;
import java.sql.Timestamp;

import javax.mail.*;
import javax.mail.internet.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class SubscriberManager {

    private static final String ADD_SUBS_MONITOR = "ADD_SUBS_MONITOR";

    public static final int NUMBER_OF_REGULAR_FIELDS = 25;
    public static final int NUMBER_OF_CUSTOM_FIELDS = 50;

    public static final String ACTIVITY_WEB_VISIT = "WV";
    public static final String ACTIVITY_TELL_A_FRIEND = "TF";
    public static final String ACTIVITY_CAMPAIGN_OPEN = "OP";
    public static final String ACTIVITY_ARTICLE_CLICK = "CK";
    public static final String ACTIVITY_SIGN_UP = "SU";
    public static final String ACTIVITY_UN_SUBSCRIBE = "UN";
    public static final String ACTIVITY_SUPPRESS = "SP";
    public static final String ACTIVITY_SCORE_CHANGE = "SC";
    public static final String ACTIVITY_CAMPAIGN_SENT = "CS";
    public static final String ACTIVITY_CAMPAIGN_BOUNCED = "CB";    
    public static final String ACTIVITY_CAMPAIGN_CONVERTED = "CT";

    public static final String ACTIVITY_WF_ALERT = "A";
    public static final String ACTIVITY_WF_C2Y_TRIGGER_MAIL = "WM";
    public static final String ACTIVITY_MY_C2Y_TRIGGER_MAIL = "MM";

    public static final String ACTIVITY_MESSAGE_SENT = "MT";
    public static final String ACTIVITY_MESSAGE_OPEN = "MO";
    public static final String ACTIVITY_MESSAGE_URL_CLICK = "MC";
    public static final String ACTIVITY_MESSAGE_SUPPRESS = "MS";

    public static final String OPEN_URL_SUB_INFO = "OPEN_URL_SUB_INFO";

    private static ComplexQueryServer complexQueryServer;
    private static ComplexQueryServer2 complexQueryServer2;
    private static DataQueryServer dataServer;
    private static ReportsQueryServer reportQueryServer;
    private static BMSQueryServer bmsServer;
    private static AlphaQueryServer alphaQueryServer;

    private static Vector salesStatusVec = new Vector();
    private static Vector basicFieldLayoutVec = new Vector();
    private static Vector basicFieldNamesVec = new Vector();
    private static Vector layoutVec = new Vector();

    static{
	bmsServer = EJBHomesFactory.getBMSQueryServerRemote();
	complexQueryServer = EJBHomesFactory.getComplexQueryServerRemote();
	complexQueryServer2 = EJBHomesFactory.getComplexQueryServer2Remote();
	dataServer = EJBHomesFactory.getDataQueryServerRemote();
	reportQueryServer = EJBHomesFactory.getReportsQueryServerRemote();
	alphaQueryServer = EJBHomesFactory.getAlphaQueryServerRemote();
    }
    private SubscriberManager(){
    }

    public static Vector getBasicFieldLayoutNames() {
	if(basicFieldLayoutVec.size()>0)
	    return basicFieldLayoutVec;

	basicFieldLayoutVec.add(new NameValue("email","EMAIL_ADDR"));
	basicFieldLayoutVec.add(new NameValue("firstName","FIRST_NAME"));
	basicFieldLayoutVec.add(new NameValue("middleName","MIDDLE_NAME"));
	basicFieldLayoutVec.add(new NameValue("lastName","LAST_NAME"));
	basicFieldLayoutVec.add(new NameValue("company","COMPANY"));
	basicFieldLayoutVec.add(new NameValue("occupation","OCCUPATION"));
	basicFieldLayoutVec.add(new NameValue("areaCode","AREA_CODE"));
	basicFieldLayoutVec.add(new NameValue("telephone","TELEPHONE"));
	basicFieldLayoutVec.add(new NameValue("industry","INDUSTRY"));
	basicFieldLayoutVec.add(new NameValue("addressLine1","ADDRESS_LINE1"));
	basicFieldLayoutVec.add(new NameValue("addressLine2","ADDRESS_LINE2"));
	basicFieldLayoutVec.add(new NameValue("city","CITY"));
	basicFieldLayoutVec.add(new NameValue("stateCode","STATE_CODE"));
	basicFieldLayoutVec.add(new NameValue("zip","ZIP_CODE"));
	basicFieldLayoutVec.add(new NameValue("countryCode","COUNTRY_CODE"));
	basicFieldLayoutVec.add(new NameValue("salesRep","SALES_REP"));
	basicFieldLayoutVec.add(new NameValue("salesStatus","SALES_STATUS"));
	basicFieldLayoutVec.add(new NameValue("source","SOURCE"));
	basicFieldLayoutVec.add(new NameValue("birthDate","BIRTH_DATE"));
	basicFieldLayoutVec.add(new NameValue("educationLevel","EDUCATION_LEVEL"));
	basicFieldLayoutVec.add(new NameValue("jobStatus","JOB_STATUS"));

	//==== For SignUp Forms ====
	basicFieldLayoutVec.add(new NameValue("address","ADDRESS_LINE1"));
	basicFieldLayoutVec.add(new NameValue("state","STATE_CODE"));
	basicFieldLayoutVec.add(new NameValue("country","COUNTRY_CODE"));
	basicFieldLayoutVec.add(new NameValue("dateOfBirth","BIRTH_DATE"));
	basicFieldLayoutVec.add(new NameValue("jobTitle","JOB_STATUS"));

	return basicFieldLayoutVec;
    }

    public static Vector getBasicFieldNames() {
	if(basicFieldNamesVec.size()>0)
	    return basicFieldNamesVec;

	basicFieldNamesVec.add("email");
	basicFieldNamesVec.add("firstName");
	basicFieldNamesVec.add("middleName");
	basicFieldNamesVec.add("lastName");
	basicFieldNamesVec.add("company");
	basicFieldNamesVec.add("occupation");
	basicFieldNamesVec.add("areaCode");
	basicFieldNamesVec.add("telephone");
	basicFieldNamesVec.add("industry");
	basicFieldNamesVec.add("addressLine1");
	basicFieldNamesVec.add("addressLine2");
	basicFieldNamesVec.add("city");
	basicFieldNamesVec.add("stateCode");
	basicFieldNamesVec.add("zip");
	basicFieldNamesVec.add("countryCode");
	basicFieldNamesVec.add("salesRep");
	basicFieldNamesVec.add("salesStatus");
	basicFieldNamesVec.add("source");
	basicFieldNamesVec.add("birthDate");
	basicFieldNamesVec.add("educationLevel");
	basicFieldNamesVec.add("jobStatus");

	return basicFieldNamesVec;
    }

    public static Vector getLayoutVec() {
	if(layoutVec.size()>0)
	    return layoutVec;

	layoutVec.add("EMAIL_ADDR");
	layoutVec.add("FIRST_NAME");
	layoutVec.add("MIDDLE_NAME");
	layoutVec.add("LAST_NAME");
	layoutVec.add("COMPANY");
	layoutVec.add("OCCUPATION");
	layoutVec.add("AREA_CODE");
	layoutVec.add("TELEPHONE");
	layoutVec.add("INDUSTRY");
	layoutVec.add("ADDRESS_LINE1");
	layoutVec.add("ADDRESS_LINE2");
	layoutVec.add("CITY");
	layoutVec.add("STATE_CODE");
	layoutVec.add("ZIP_CODE");
	layoutVec.add("COUNTRY_CODE");
	layoutVec.add("SALES_REP");
	layoutVec.add("SALES_STATUS");
	layoutVec.add("SOURCE");
	layoutVec.add("BIRTH_DATE");
	layoutVec.add("EDUCATION_LEVEL");
	layoutVec.add("JOB_STATUS");

	return layoutVec;
    }


    public static Vector getSalesStatusVec() {
	if(salesStatusVec.size()>0)
	    return salesStatusVec;

	salesStatusVec.add(PMSDefinitions.SALES_STATUS_ACCOUNT);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_ACTIVE);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_ARCHIVE);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_PARTNER);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_CALL_ONLY_DO_NOT_EMAIL);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_CONTACT);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_CUSTOMER);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_DO_NOT_USE);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_EMAIL_ONLY);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_FORMER_CONSULTANT);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_FORMER_CONTACT);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_IMPORTED);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_INTERVIEWING);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_KICKBACK);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_LEAD);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_NEW_LEAD);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_OFFER_PENDING);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_PASSIVE);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_PLACED);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_PRIVATE);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_PROSPECT);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_QUALIFIED);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_SCREENED);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_UNAWAILABLE);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_UNSCREENED);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_DORMANT_CONTACT);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_SUSPECT);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_PROSPECT_DECISION_MAKER);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_PROSPECT_SUPPORT_PERSONNEL);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_DO_NOT_CONTACT);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_DORMANT_PROSPECT);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_DORMANT_PROSPECT_AMWA);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_DORMANT_PROSPECT_IABC);
	salesStatusVec.add(PMSDefinitions.SALES_STATUS_DORMANT_PROSPECT_STC);

	return salesStatusVec;
    }

    /**
     *
     * @param subInfo
     * @param listNumber
     * @param userInfo
     * @return
     */
    public static Vector addSubscriber(SubscriberInfo subInfo, String listNumber, UserInfo userInfo){
	Vector info = new Vector();
	try{
	    Vector subInfoVec = new Vector();
	    subInfoVec.add(subInfo);

	    //checking if Trial Subscriber's limit
	    if(userInfo.getPackageType().equals(PMSDefinitions.PACKAGE_TYPE_TRIAL) && isSubscriberLimitValid(userInfo,subInfoVec.size()))
		return info;

	    info = complexQueryServer.addSubscribers(subInfoVec,listNumber,userInfo, false);

	}catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return info;
    }

    /**
     *
     * @param subInfoVec
     * @param listNumber
     * @param userInfo
     * @return
     */
    public static Vector addSubscribers(Vector subInfoVec, String listNumber, UserInfo userInfo){
	Vector info = new Vector();
	try{
	    //checking if Trial Subscriber's limit
	    if(userInfo.getPackageType().equals(PMSDefinitions.PACKAGE_TYPE_TRIAL) && isSubscriberLimitValid(userInfo,subInfoVec.size()))
		return info;

	    info = complexQueryServer.addSubscribers(subInfoVec,listNumber,userInfo,false);
	}catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return info;
    }


    /**
     *
     * @param subInfoVec
     * @param listNumber
     * @param layout
     * @param userInfo
     * @return
     */
    public static Vector addSubscribersWithLayout(Vector subInfoVec, String listNumber, String layout, UserInfo userInfo){
	Vector info = new Vector();
	try{
	    //checking if Trial Subscriber's limit
	    if(userInfo.getPackageType().equals(PMSDefinitions.PACKAGE_TYPE_TRIAL) && isSubscriberLimitValid(userInfo,subInfoVec.size()))
		return info;

	    info = complexQueryServer.addSubscribers(subInfoVec,listNumber, layout, userInfo);
	}catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return info;
    }

    /**
     * 
     * @param subInfoVec
     * @param layout
     * @param userInfo
     * @return
     */
    public static Vector addSubscribersWithoutList(Vector subInfoVec, String layout, UserInfo userInfo){
	Vector info = new Vector();
	try{
	    //checking if Trial Subscriber's limit
	    if(userInfo.getPackageType().equals(PMSDefinitions.PACKAGE_TYPE_TRIAL) && isSubscriberLimitValid(userInfo,subInfoVec.size()))
		return info;

	    info = complexQueryServer.addSubscribers(subInfoVec,layout, userInfo);
	}catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return info;
    }

    /**
     *
     * @param email
     * @param listNumber
     * @return
     */
    public static boolean isSubscriberExist(String email, String listNumber){
	boolean exist = false;
	try{
	    exist = dataServer.isSubscriberExist(email,Default.defaultLong(listNumber));
	}catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return exist;
    }

    /**
     *
     * @param subVec
     * @param layout
     * @param sessionId
     * @param userInfo
     * @param exportType
     * @return
     */
    public static String exportSubscribers(Vector subVec, String layout, String sessionId, UserInfo userInfo, String exportType){
	String url = "";
	SubscriberInfo subInfo = null;
	FileOutputStream fos = null;
	PrintStream ps = null;
	PMSResources pmsResources = null;
	String exportFilePath = "";
	String exportFileName = "";
	String seperator = "";

	try{

	    pmsResources = PMSResources.getInstance();
	    exportFilePath = pmsResources.getExportPath() +
	    PMSDefinitions.EXPORT_DIR;
	    exportType = exportType.equals("")?"csv":exportType;
	    File expPath = new File(exportFilePath);
	    if (!expPath.exists()) {
		if (!expPath.mkdirs()) {

		    return null;
		} //end if
	    } //end if
	    exportFileName = System.currentTimeMillis() +"_" + userInfo.getUserID() +"_Subscribers." + exportType;
	    seperator = exportType.equals("csv") ? "," : "|";

	    fos = new FileOutputStream(exportFilePath + exportFileName);
	    ps = new PrintStream(fos);
	    // Processing File Heading
	    Vector layoutVec = Default.fromCSV(layout);
	    for(int i=0;layoutVec !=null && i<layoutVec.size();i++){
		if(i ==0 )
		    ps.print(layoutVec.get(i));
		else
		    ps.print(seperator + layoutVec.get(i));
	    }
	    ps.println();
	    // processing for fields data
	    processExportLayout(subVec,layout,ps,seperator);

	    url = exportFilePath + exportFileName;
	}catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}

	return url;
    }

    private static void processExportLayoutHeading(String layout, Vector subVec, PrintStream ps, String seprator){

    }
    private static void processExportLayout(Vector subVec, String layout, PrintStream ps, String seprator){

	Vector layoutVec = Default.fromCSV(layout);
	if(subVec ==null || subVec.size()<1)
	    return;
	System.out.println("subVec in processExportLayout size = "+subVec.size());
	for(int j=0;j<subVec.size();j++){
	    SubscriberInfo subData = (SubscriberInfo) subVec.get(j);
	    Vector subCustomData = subData.getCustomFields();
	    // Processing basic fields
	    for(int i=0;i<layoutVec.size();i++){
		String field =  (String) layoutVec.get(i);
		if(field==null||field.equals(""))
		    continue;
		else if(field.equals("EMAIL_ADDR"))
		    ps.print(""+Default.toDefault(subData.getEmail()));
		else if(field.equals("FIRST_NAME"))
		    ps.print(seprator+Default.toDefault(subData.getFirstName()));
		else if(field.equals("MIDDLE_NAME"))
		    ps.print(seprator+Default.toDefault(subData.getMiddleName()));
		else if(field.equals("LAST_NAME"))
		    ps.print(seprator+Default.toDefault(subData.getLastName()));
		else if(field.equals("EMAIL_TYPE"))
		    ps.print(seprator+Default.toDefault(subData.getEmailType()));
		else if(field.equals("GENDER"))
		    ps.print(seprator+Default.toDefault(subData.getGender()));
		else if(field.equals("BIRTH_DATE"))
		    ps.print(seprator+Default.toDefault(""+subData.getBirthDate()));
		else if(field.equals("ADDRESS_LINE1"))
		    ps.print(seprator+Default.toDefault(subData.getAddressLine1()));
		else if(field.equals("ADDRESS_LINE2"))
		    ps.print(seprator+Default.toDefault(subData.getAddressLine2()));
		else if(field.equals("CITY"))
		    ps.print(seprator+Default.toDefault(subData.getCity()));
		else if(field.equals("STATE_CODE"))
		    ps.print(seprator+Default.toDefault(subData.getStateCode()));
		else if(field.equals("ZIP_CODE"))
		    ps.print(seprator+Default.toDefault(subData.getZip()));
		else if(field.equals("COUNTRY_CODE"))
		    ps.print(seprator+Default.toDefault(subData.getCountryCode()));
		else if(field.equals("MARITAL_STATUS"))
		    ps.print(seprator+Default.toDefault(subData.getMaritalStatus()));
		else if(field.equals("OCCUPATION"))
		    ps.print(seprator+Default.toDefault(subData.getOccupation()));
		else if(field.equals("JOB_STATUS"))
		    ps.print(seprator+Default.toDefault(subData.getJobStatus()));
		else if(field.equals("HOUSEHOLD_INCOME"))
		    ps.print(seprator+Default.toDefault(subData.getHouseholdIncome()));
		else if(field.equals("EDUCATION_LEVEL"))
		    ps.print(seprator+Default.toDefault(subData.getEducationLevel()));
		else if(field.equals("TELEPHONE"))
		    ps.print(seprator+Default.toDefault(subData.getTelephone()));
		else if(field.equals("COMPANY"))
		    ps.print(seprator+Default.toDefault(subData.getCompany()));
		else if(field.equals("INDUSTRY"))
		    ps.print(seprator+Default.toDefault(subData.getIndustry()));
		else if(field.equals("SOURCE"))
		    ps.print(seprator+Default.toDefault(subData.getSource()));
		else if(field.equals("SALES_REP"))
		    ps.print(seprator+Default.toDefault(subData.getSalesRep()));
		else if(field.equals("SALES_STATUS"))
		    ps.print(seprator+Default.toDefault(subData.getSalesStatus()));
		else if(field.equals("AREA_CODE"))
		    ps.print(seprator+Default.toDefault(subData.getAreaCode()));
		//processing customize fields
		else if(subCustomData!=null && subCustomData.size()>0)
		    ps.print(seprator+Default.toDefault(""+subData.getCustomFieldValue(field)));
		else
		    ps.print(seprator);
	    }
	    ps.println();
	}
    }

    /**
     *
     * @param userID
     * @param email
     * @return
     */
    public static boolean supressSubscriber(String userID, String email){
	boolean added = true;
	try{
	    complexQueryServer.addToSupressList(userID, email);
	}catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	    added = false;
	}
	return added;
    }

    /**
     *
     * @param subNo
     * @return
     */
    public static boolean supressSubscriber(String userId, long subNo) {

	if(subNo<=0)
	    return false;

	try {
	    complexQueryServer.addToSupressList(userId, subNo);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	    return false;
	}
	return true;
    }

    /**
     *
     * @param email
     * @return
     */
    public static boolean markUniversalSuppress(String email){
	boolean find = false;
	try {
	    if(email == null || email.equals(""))
		return false;
	    email = email.trim();
	    PMSResources pmsResources = PMSResources.getInstance();
	    if (pmsResources.addInUniversalSupressList(email)) {
		// Append in theworker SupressedEmail file
		return complexQueryServer.markUniversalSuppress(email);
	    }
	}catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return find;
    }

    /**
     *
     * @return
     */
    public static Vector getAllUniversalSuppressSubscribers(){
	try{
	    PMSResources pmsResources = PMSResources.getInstance();
	    return pmsResources.getUniversalSupressedEmails();
	}catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }

    /**
     * @param userId
     * @return
     */
    public static Vector getUserCustomFields(String userId) {

	try {
	    return dataServer.getUserCustomFields(userId);
	} catch(Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }


    /*    public static boolean blockSubscriberDomain(String domain){
        try{
            PMSResources pmsResources = PMSResources.getInstance();
            if (pmsResources.addInUniversalSupressList(domain))
                return complexQueryServer.blockSubscriberDomain(domain);
        }catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }*/

    public static Vector loadNetSuiteSubscribers(long listNumber, int startLimit, int bucketSize){
	try{
	    if (listNumber==0 )
		return new Vector();

	    return dataServer.loadNetSuiteSubscribers(listNumber, startLimit, bucketSize);
	}catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }

    /**
     * for USM.jsp, Unsubscribe.jsp, SubscriberInfoUpdate.jsp
     *
     * @param userId
     * @return
     */
    public static String getCompanyLogo(String userId) {

	if(Default.toDefault(userId).trim().equals(""))
	    return PMSDefinitions.DEFAULT_LOGO;

	try {
	    String logo = Default.toDefault(dataServer.getCompanyLogo(userId));
	    if(!logo.equals(""))
		return logo;

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return PMSDefinitions.DEFAULT_LOGO;
    }

    /**
     *
     * @param subNum
     * @param eventType
     */
    public static void markActive(long subNum, String eventType){
	try{
	    if(subNum <= 0)
		return;
	    Vector subVec = new Vector();
	    subVec.add(""+subNum);
	    complexQueryServer.markSubscriberActive(subVec,eventType);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
    }

    /**
     *
     * @param subVec
     * @param eventType
     */
    public static void markActive(Vector subVec, String eventType){
	try{
	    if(subVec ==null || subVec.size()==0)
		return;
	    complexQueryServer.markSubscriberActive(subVec,eventType);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
    }

    /**
     *
     * @param subNum
     */
    public static void markInActive(long subNum){
	try{
	    if(subNum <= 0)
		return;
	    Vector subVec = new Vector();
	    subVec.add(""+subNum);
	    complexQueryServer.markSubscriberInActive(subVec);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
    }

    /**
     *
     * @param subVec
     */
    public static void markInActive(Vector subVec){
	try{
	    if(subVec == null || subVec.size()==0)
		return;
	    complexQueryServer.markSubscriberInActive(subVec);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
    }


    /**
     *
     * @param userId
     * @param limit
     * @param batch
     * @param sortBy
     * @return
     */
    public static Vector getDashboardSubscribers(DashboardSAMBean samUserBean, int limit, int batch, String sortBy, String order){
	try{
	    if (samUserBean==null)
		return new Vector();
	    return reportQueryServer.getDashboardSubscribers(samUserBean,limit,batch,sortBy,order);
	} catch(Exception ex){
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
    public static int getSubscriberCount(DashboardSAMBean samUserBean){
	try{
	    if (samUserBean==null)
		return 0;
	    return reportQueryServer.getSubscriberCount(samUserBean);
	} catch (Exception ex){
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
    public static int getSubscriberCount(String userId){
	try{
	    if (Default.toDefault(userId).equals(""))
		return 0;
	    return alphaQueryServer.getSubscriberCount(userId);
	} catch (Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;
    }
    /**
     * 
     * @param userId
     * @param subNumVec
     * @return
     */
    public static Hashtable loadSubscriptionHash(String userId, Vector subNumVec) {

	if(userId.equals("") || subNumVec.isEmpty())
	    return new Hashtable();
	try {
	    return alphaQueryServer.loadSubscriptionHash(userId, subNumVec);

	} catch (Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Hashtable();
    }

    /**
     *
     * @param userId
     * @param value
     * @return
     */
    public static Vector searchSubscribers(DashboardSAMBean samUserBean, String value,int limit, int batch, String sortBy, String order){

	try {
	    return reportQueryServer.searchSubscribers(samUserBean,value,limit,batch,sortBy,order);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     *
     * @param userId
     * @param value
     * @return
     */
    public static int getSearchCount(DashboardSAMBean samUserBean, String value){

	try{
	    return reportQueryServer.getSearchSubscriberCount(samUserBean,value);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;
    }

    /**
     *
     * @param str
     * @return
     */
    public static String getFieldLayoutMapping(String str){
	Vector vec = getBasicFieldLayoutNames();
	for(int i=0;i<vec.size();i++){
	    NameValue nv = (NameValue) vec.get(i);
	    if(nv.getName().equalsIgnoreCase(str))
		return nv.getValue();
	}
	return "";
    }

    /**
     *
     * @param email
     * @param userId
     * @return
     */
    public static SubscriberInfo getSubscriber(String email, String userId){
	try{
	    Vector vec = getSubscriber(Default.toVector(email),userId);

	    if(vec == null || vec.size() ==0)
		return null;
	    return (SubscriberInfo) vec.get(0);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return null;
    }

    /**
     *
     * @param emailVec
     * @param userId
     * @return
     */
    public static Vector getSubscriber(Vector emailVec, String userId){
	try{
	    return dataServer.getSubscriber(emailVec,userId,false);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return null;

    }

    /**
     *
     * @param emailVec
     * @param userId
     * @return
     */
    public static Vector getSubscriberWithCRM(Vector emailVec, String userId){
	try{
	    return dataServer.getSubscriber(emailVec,userId,true);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return null;

    }

    /**
     *
     * @param subNumVec
     * @param activity
     *
     */
    public static void updateLastActivity(Vector subNumVec, String activity){
	try{
	    complexQueryServer.updateSubscriberLastActivity(subNumVec,activity);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
    }

    /**
     *
     * @param subNumVec
     * @param activity
     *
     */
    public static String getActivityName(String activityType){
	String activity = "";
	if(activityType == null || activityType.equals(""))
	    return activity;
	if(activityType.equals(PMSDefinitions.SUB_EVENT_CLICK))
	    activity = "Email Click";
	if(activityType.equals(PMSDefinitions.SUB_EVENT_OPEN))
	    activity = "Email Open";
	if(activityType.equals(PMSDefinitions.SUB_EVENT_SIGNUP))
	    activity = "Form Signup";
	if(activityType.equals(PMSDefinitions.SUB_EVENT_WEBVIST))
	    activity = "Web Visit";

	return activity;
    }

    /**
     *  (Independent from List)
     * @param subInfo
     * @param userId
     * @return
     */
    public static boolean updateSubscriberObject(SubscriberInfo subInfo, String userId){
	try{
	    return complexQueryServer.updateSubscriberObject(subInfo, userId);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }

    /**
     *
     * @param userId
     * @param filterBean
     * @return
     */
    public static int getDashboardFilterSubscriberCount(DashboardSAMBean samUserBean, DashboardFilterBean filterBean){
	try{
	    if (filterBean == null || samUserBean==null)
		return 0;
	    return reportQueryServer.getDashboardFilterSubscriberCount(samUserBean,filterBean);
	} catch(Exception ex){
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
    public static Vector getSAMUserSalesReps(String userId){
	try{
	    if (userId == null || userId.equals(""))
		return new Vector();
	    return reportQueryServer.getSAMUserSalesReps(userId);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }

    /**
     *
     * @param userId
     * @param filterBean
     * @return
     */
    public static Vector getDashboardFilterSubscribers(DashboardSAMBean samUserBean, DashboardFilterBean filterBean,int limit, int batch){
	try{
	    if (filterBean == null || samUserBean==null)
		return new Vector();
	    return reportQueryServer.getDashboardFilterSubscribers(samUserBean,filterBean,limit,batch);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }


    /**
     *
     * @param userId
     * @param filterBean
     * @return
     */
    public static Vector getOpenedCampNumVec(long subNum,Vector campNumVec){
	try{
	    if (campNumVec == null || campNumVec.size()==0 || subNum == 0)
		return new Vector();
	    return reportQueryServer.getOpenedCampNumVec(subNum,campNumVec);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }

    /**
     * @param bean
     * @return
     */
    public static boolean logSubscriberActivity(SubscriberActivityHistoryBean bean) {
	if(bean==null || bean.getSubscriberNumber()<=0)
	    return false;
	return logSubscriberActivity(Default.toVector(bean));
    }

    /**
     * @param vec
     * @return
     */
    public static boolean logSubscriberActivity(Vector vec) {

	if(vec==null || vec.isEmpty())
	    return false;

	try {
	    return complexQueryServer2.createSubsActivityHistory(vec);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }

    /**
     * @param vec
     * @return
     */
    public static boolean logSubscriberScoreChangeActivity(Vector vec) {
	return logSubscriberActivity(vec);
    }

    /**
     * @param visitVec
     * @return
     */
    public static boolean logWebVisitActivity(Vector visitVec) {

	Vector vec = new Vector();
	if(visitVec==null || visitVec.isEmpty())
	    return false;

	for(int i=0; i<visitVec.size(); i++) {
	    WebVisitBean bean = (WebVisitBean)visitVec.get(i);

	    SubscriberActivityHistoryBean activityBean = new SubscriberActivityHistoryBean();
	    activityBean.setSubscriberNumber(bean.getSubscriberNumber());
	    activityBean.setUserId(bean.getUserId());
	    activityBean.setActivityType(SubscriberManager.ACTIVITY_WEB_VISIT);

	    activityBean.setIdOne(bean.getCampaignNumber());
	    activityBean.setIdTwo(bean.getArticleNumber());
	    activityBean.setURL(bean.getPageFullURL());

	    vec.add(activityBean);
	}
	return logSubscriberActivity(vec);
    }

    /**
     * @param userID
     * @param subNum
     * @param campNum
     * @return
     */
    public static boolean logTellAFriendActivity(String userID, long subNum, long campNum) {

	userID = Default.toDefault(userID).trim();
	if(userID.equals("") || subNum<=0 || campNum<=0)
	    return false;

	SubscriberActivityHistoryBean activityBean = new SubscriberActivityHistoryBean();
	activityBean.setSubscriberNumber(subNum);
	activityBean.setUserId(userID);
	activityBean.setActivityType(SubscriberManager.ACTIVITY_TELL_A_FRIEND);
	activityBean.setIdOne(campNum);

	return logSubscriberActivity(activityBean);
    }

    /**
     * @param openVec
     * @return
     */
    public static boolean logCampaignOpenActivity(Vector openVec) {

	Vector vec = new Vector();
	if(openVec==null || openVec.isEmpty())
	    return false;

	try {
	    Vector subNumVec = new Vector();
	    for(int i=0; i<openVec.size(); i++) {
		String[] str = (String[])openVec.get(i);
		if(Default.defaultLong(str[1])>0)
		    subNumVec.add(str[1]);
	    }
	    Hashtable userIdHash = complexQueryServer2.getSubscriberUserIds(subNumVec);

	    for(int i=0; i<openVec.size(); i++) {
		String[] str = (String[])openVec.get(i);

		long campNum = Default.defaultLong(str[0]);
		long subNum = Default.defaultLong(str[1]);
		String userId = Default.toDefault((String)userIdHash.get(str[1])).trim();
		if(subNum<=0 || campNum<=0 || userId.equals(""))
		    continue;

		SubscriberActivityHistoryBean activityBean = new SubscriberActivityHistoryBean();
		activityBean.setSubscriberNumber(subNum);
		activityBean.setUserId(userId);
		activityBean.setActivityType(SubscriberManager.ACTIVITY_CAMPAIGN_OPEN);
		activityBean.setIdOne(campNum);

		vec.add(activityBean);
	    }
	    return logSubscriberActivity(vec);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }

    /**
     * @param clickVec
     * @return
     */
    public static boolean logArticleClickedActivity(Vector clickVec) {

	Vector vec = new Vector();
	if(clickVec==null || clickVec.isEmpty())
	    return false;

	try {
	    Vector artNumVec = new Vector();
	    Vector subNumVec = new Vector();

	    for(int i=0; i<clickVec.size(); i++) {
		String[] str = (String[])clickVec.get(i);

		if(Default.defaultLong(str[0])>0)
		    artNumVec.add(str[0]);
		if(Default.defaultLong(str[1])>0)
		    subNumVec.add(str[1]);
	    }

	    Hashtable artHash = new Hashtable();
	    Hashtable userIdHash = complexQueryServer2.getSubscriberUserIds(subNumVec);
	    Vector articlesVec = dataServer.getArticle(artNumVec);

	    for(int i=0; i<articlesVec.size(); i++) {
		ArticleDataBean ab = (ArticleDataBean)articlesVec.get(i);
		artHash.put(""+ab.getArticleNumber(), ab);
	    }

	    for(int i=0; i<clickVec.size(); i++) {
		String[] str = (String[])clickVec.get(i);

		long articleNum = Default.defaultLong(str[0]);
		long subNum = Default.defaultLong(str[1]);
		String userId = Default.toDefault((String)userIdHash.get(str[1])).trim();
		ArticleDataBean articleBean = (ArticleDataBean)artHash.get(str[0]);

		if(subNum<=0 || articleNum<=0 || userId.equals("") || articleBean==null)
		    continue;

		SubscriberActivityHistoryBean activityBean = new SubscriberActivityHistoryBean();
		activityBean.setSubscriberNumber(subNum);
		activityBean.setUserId(userId);
		activityBean.setActivityType(SubscriberManager.ACTIVITY_ARTICLE_CLICK);
		activityBean.setIdOne(articleBean.getCampaignNumber());
		activityBean.setIdTwo(articleNum);
		activityBean.setURL(articleBean.getArticleURL());

		vec.add(activityBean);
	    }
	    return logSubscriberActivity(vec);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }

    /**
     *
     * @param userId
     * @param email
     * @param listNum
     * @param formId
     * @return
     */
    public static boolean logSignUpFormActivity(String userId, String email, String listNum, int formId) {

	//FomId can be zero for Old SignUp forms.
	if(formId<0 || listNum.trim().equals("") || email.equals("") || userId.equals(""))
	    return false;

	SubscriberInfo info = SubscriberManager.getSubscriber(email, userId);
	if(info==null)
	    return false;

	SubscriberActivityHistoryBean activityBean = new SubscriberActivityHistoryBean();
	activityBean.setSubscriberNumber(info.getSubscriberNumber());
	activityBean.setUserId(userId);
	activityBean.setActivityType(SubscriberManager.ACTIVITY_SIGN_UP);
	activityBean.setIdOne(formId);
	activityBean.setIdTwo(Default.defaultLong(listNum));

	return logSubscriberActivity(activityBean);
    }

    /**
     * @param subNum
     * @param campNum
     * @return
     */
    public static boolean logUnsubscribeActivity(long subNum, long campNum) {
	return logSubscriptionChangeActivity(subNum, campNum, SubscriberManager.ACTIVITY_UN_SUBSCRIBE);
    }

    /**
     * @param subNum
     * @param campNum
     * @return
     */
    public static boolean logSuppressActivity(long subNum, long campNum) {
	return logSubscriptionChangeActivity(subNum, campNum, SubscriberManager.ACTIVITY_SUPPRESS);
    }

    /**
     * @param subNum
     * @param campNum
     * @return
     */
    private static boolean logSubscriptionChangeActivity(long subNum, long campNum, String activityType) {

	if(subNum<=0 || campNum<=0)
	    return false;
	try {
	    Hashtable userIdHash = complexQueryServer2.getSubscriberUserIds(Default.toVector(""+subNum));
	    if(userIdHash==null || userIdHash.size()==0 || userIdHash.get(""+subNum)==null)
		return false;

	    String userId = (String)userIdHash.get(""+subNum);

	    SubscriberActivityHistoryBean activityBean = new SubscriberActivityHistoryBean();
	    activityBean.setSubscriberNumber(subNum);
	    activityBean.setUserId(userId);
	    activityBean.setActivityType(activityType);
	    activityBean.setIdOne(campNum);

	    return logSubscriberActivity(activityBean);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }

    /**
     *
     * @param subNum
     * @return
     */
    public static SalesRep getSalesRepData(long subNum){
	try{
	    return reportQueryServer.getSubscriberSalesRepData(subNum);
	}catch(Exception ex){
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return null;
    }

    /**
     *
     * @param subNum
     * @return
     */
    public static SubscriberInfo getSubscriberInfo(String userId, long subNum){

	if(subNum == 0l || userId.equals(""))
	    return null;
	try{
	    return complexQueryServer2.getSubscriberInfo(userId, subNum);
	}catch(Exception ex){
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return null;
    }
    /**
     *
     * @param subNumVec
     * @return
     */
    public static Vector getSubscriberInfo(String userId, Vector subNumVec) {

	if(subNumVec.isEmpty() || userId.equals(""))
	    return new Vector();
	try{
	    return complexQueryServer2.getSubscriberInfo(userId, subNumVec);
	}catch(Exception ex){
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }
    /**
     *
     * @param transmissionVec
     */
    public static void logWorkflowTriggerMailActivityHistory(Vector transmissionVec) {

	if(transmissionVec.isEmpty())
	    return;

	try {
	    Vector dataVec = new Vector();

	    for(int i=0; i<transmissionVec.size(); i++) {
		TriggerMailTransmissionBean bean = (TriggerMailTransmissionBean)transmissionVec.get(i);
		if(bean.getSalesforceId().equals(""))//Failed TriggerMail Action.
		    continue;

		SubscriberActivityHistoryBean activityBean = new SubscriberActivityHistoryBean();
		activityBean.setUserId(bean.getUserID());
		activityBean.setSubscriberNumber(bean.getSubscriberNumber());
		activityBean.setActivityType(ACTIVITY_WF_C2Y_TRIGGER_MAIL);
		activityBean.setIdOne(bean.getWorkflowId());
		activityBean.setIdTwo(bean.getStepID());

		dataVec.add(activityBean);
	    }
	    //==== log ====
	    logSubscriberActivity(dataVec);

	} catch(Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
    }
    /**
     *
     * @param transmissionVec
     */
    public static void logMyC2YTriggerMailActivityHistory(Vector transmissionVec) {

	if(transmissionVec.isEmpty())
	    return;

	try {
	    Vector dataVec = new Vector();

	    for(int i=0; i<transmissionVec.size(); i++) {
		MyC2YTriggerMailBean bean = (MyC2YTriggerMailBean)transmissionVec.get(i);
		if(bean.getStatus().equals("F"))//Failed TriggerMail Action.
		    continue;

		SubscriberActivityHistoryBean activityBean = new SubscriberActivityHistoryBean();
		activityBean.setUserId(bean.getUserId());
		activityBean.setSubscriberNumber(bean.getSubscriberNumber());
		activityBean.setActivityType(ACTIVITY_MY_C2Y_TRIGGER_MAIL);
		activityBean.setIdOne(bean.getMailId());

		dataVec.add(activityBean);
	    }
	    //==== log ====
	    logSubscriberActivity(dataVec);

	} catch(Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
    }

    /**
     *
     * @param msgId
     * @param subNo
     * @param userId
     * @return
     */
    public static boolean logMessageOpenActivity(long msgId, long subNo) {

	if(msgId <= 0 || subNo <= 0)
	    return false;

	try {
	    SingleMessageBean msgBean = MessageManager.getSingleMessage(msgId, subNo);
	    String userId = (msgBean==null)? null: msgBean.getUserId();
	    if(userId == null)
		return false;

	    SubscriberActivityHistoryBean activityBean = new SubscriberActivityHistoryBean();
	    activityBean.setSubscriberNumber(subNo);
	    activityBean.setUserId(userId);
	    activityBean.setActivityType(ACTIVITY_MESSAGE_OPEN);
	    activityBean.setIdOne(msgId);

	    return logSubscriberActivity(activityBean);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }

    /**
     *
     * @param urlId
     * @param subNo
     * @param msgId
     * @param userId
     * @param url
     * @return
     */
    public static boolean logMessageURLClickedActivity(long urlId, long subNo, String url) {

	if(urlId <= 0 || subNo <= 0 || url == null)
	    return false;

	try {
	    long msgId = MessageManager.getURLMessageId(urlId);
	    if(msgId <= 0)
		return false;

	    SingleMessageBean msgBean = MessageManager.getSingleMessage(msgId, subNo);
	    String userId = (msgBean==null)? null: msgBean.getUserId();
	    if(userId == null)
		return false;


	    SubscriberActivityHistoryBean activityBean = new SubscriberActivityHistoryBean();
	    activityBean.setSubscriberNumber(subNo);
	    activityBean.setUserId(userId);
	    activityBean.setActivityType(ACTIVITY_MESSAGE_URL_CLICK);
	    activityBean.setIdOne(msgId);
	    activityBean.setIdTwo(urlId);
	    activityBean.setURL(url);

	    return logSubscriberActivity(activityBean);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }

    /**
     *
     * @param subNo
     * @param msgId
     * @param userId
     * @return
     */
    public static boolean logMessageSuppressActivity(long msgId, long subNo) {
	if(subNo <= 0 || msgId <= 0)
	    return false;
	try{
	    SingleMessageBean msgBean = MessageManager.getSingleMessage(msgId, subNo);
	    String userId = (msgBean==null)? null: msgBean.getUserId();
	    if(userId == null)
		return false;

	    SubscriberActivityHistoryBean activityBean = new SubscriberActivityHistoryBean();
	    activityBean.setSubscriberNumber(subNo);
	    activityBean.setUserId(userId);
	    activityBean.setActivityType(ACTIVITY_MESSAGE_SUPPRESS);
	    activityBean.setIdOne(msgId);

	    return logSubscriberActivity(activityBean);
	}  catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }
    /**
     *
     * @param subNo
     * @return
     */
    public static SubscriberInfo loadDashboardSubscriber(String userId, long subNo){
	if (subNo <= 0)
	    return null;
	try{
	    return reportQueryServer.loadDashboardSubscriber(userId, subNo);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return null;
    }

    /**
     *
     * @param subNo
     * @return
     */
    public static Vector getSubscriberListsInfo(String userId, long subNo){
	if(subNo <= 0)
	    return new Vector();
	try{
	    return reportQueryServer.getSubscriberListsInfo(userId, subNo);
	} catch(Exception ex){
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
    public static int getSAMHotListSubscriberCount(DashboardSAMBean samUserBean){

	try{
	    if (samUserBean==null)
		return 0;
	    return reportQueryServer.getSAMHotListSubscriberCount(samUserBean);
	} catch (Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;
    }
    /**
     * @param campNum
     * @param subNum
     * @return
     */
    public static Vector getSubscriberClickHistory(long campNum, long subNum) {

	try{
	    return bmsServer.getCampaignSubscriberClickHistory(campNum, subNum);
	} catch(Exception ex){
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }

    /**
     *
     * @param campNumVec
     * @param subNum
     * @param startDate
     * @param endDate
     * @param offset
     * @return
     */
    public static Vector getSubscriberSentCampaignNumByOrder(Vector campNumVec, long subNum, Date startDate, Date endDate,int offset){
	try{
	    if(campNumVec==null||campNumVec.size()==0||subNum==0)
		return new Vector();

	    return dataServer.getSubscriberSentCampaignNumByOrder(campNumVec,subNum,startDate,endDate,offset);
	} catch(Exception ex){
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }

    /**
     *
     * @param campNumVec
     * @param subNum
     * @return
     */
    public static Vector getSubscriberSentCampaignData(Vector campNumVec,long subNum){
	try{
	    if(campNumVec==null||campNumVec.size()==0||subNum==0)
		return new Vector();

	    return dataServer.getSubscriberSentCampaignData(campNumVec,subNum);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     * @param listNumber
     * @param subNumVec
     * @return
     */
    public static Hashtable getSubStatus(int listNumber, Vector subNumVec) {

	if(subNumVec.isEmpty())
	    return new Hashtable();

	try{
	    return complexQueryServer2.getSubStatus(listNumber,subNumVec);

	} catch(Exception ex){
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Hashtable();
    }
    /**
     * ==== For OLD Dashboard Activity Page ====
     * 
     * @param subNum
     * @param startDate
     * @param endDate
     * @param limit
     * @param offset
     * @return
     */
    public static Vector getSubscriberActivityHistory(long subNum, Date startDate, Date endDate, int limit, int offset){
	return getSubscriberActivityHistory(subNum, new Vector(), new Vector(), (startDate!=null? startDate.getTime(): 0), (endDate!=null? endDate.getTime(): 0),limit,offset);
    }
    /**
     *
     * @param subNum
     * @param startDate
     * @param endDate
     * @param limit
     * @param offset
     * @return
     */
    public static Vector getSubscriberActivityHistory(long subNum, Vector activityTypeVec, Vector allowedCampNumVec, long startDate, long endDate, int limit, int offset) {

	if(subNum<=0)
	    return new Vector();

	try {
	    return dataServer.getSubscriberActivityHistory(subNum, activityTypeVec, allowedCampNumVec, startDate, endDate,limit,offset);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     * 
     * @param userId
     * @param subNum
     * @return
     */
    public static Vector getSubscriberBounces(String userId, long subNum, Vector allowedCampNumVec) {

	if(subNum<=0)
	    return new Vector();

	try {
	    return alphaQueryServer.getSubscriberBounces(userId, subNum, allowedCampNumVec);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     * 
     * @param userId
     * @param subNum
     * @return
     */
    public static Vector getSubscriberConversions(String userId, long subNum, Vector allowedCampNumVec) {

	if(subNum<=0)
	    return new Vector();

	try {
	    return alphaQueryServer.getSubscriberConversions(userId, subNum, allowedCampNumVec);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     * 
     * @param userId
     * @param sentCampNumVec
     * @return
     */
    public static Hashtable getIsOpenIsClickHash(long subNum, Vector sentCampNumVec) {

	if(subNum<=0 || sentCampNumVec.isEmpty())
	    return new Hashtable();

	try {
	    return alphaQueryServer.getIsOpenIsClickHash(subNum, sentCampNumVec);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Hashtable();
    }
    /**
     *
     * @param userId
     * @param subNumVec
     * @return
     */
    public static Vector getBouncedSuppressSubscribers(String userId, Vector subNumVec){
	if(subNumVec == null || subNumVec.size() == 0)
	    return new Vector();
	try{
	    return dataServer.getBouncedSuppressSubscribers(userId,subNumVec);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     *
     * @param subNum
     * @return
     */
    public static Vector getConvertedCampNums(long subNum) {

	try {
	    return bmsServer.getConvertedCampNums(subNum);
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }
    /**
     * ======= FOR SYSADMIN ONLY ======
     *
     * @param email
     * @return
     */
    public static Vector findSubscribers(String email) {

	try {
	    return complexQueryServer.findSubscribers(email);
	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }
    /**
     *
     * @param listNumber
     * @param offset
     * @param batch
     * @return
     */
    public static Vector getSubscriberInfoByList(long listNumber, int offset, int batch){
	if(listNumber <1 || batch <1)
	    return new Vector();

	try{
	    return reportQueryServer.getSubscriberInfoByList(listNumber,false,offset,batch);
	} catch(Exception ex){
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }

    /**
     *
     * @param listNumber
     * @param isCRM
     * @param offset
     * @param batch
     * @return
     */
    public static Vector getSubscriberInfoByListWithCRM(long listNumber, int offset, int batch){
	if(listNumber < 1 || batch < 1)
	    return new Vector();

	try{
	    return reportQueryServer.getSubscriberInfoByList(listNumber,true,offset,batch);
	} catch(Exception ex){
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }

    /**
     * @param userId
     * @param emailVec
     * @return
     */
    public static Hashtable getSubscriberNumberByEmail(String userId, Vector emailVec) {

	if(Default.toDefault(userId).equals("")|| emailVec == null || emailVec.size()==0)
	    return new Hashtable();

	try{
	    return dataServer.getSubscriberNumberByEmail(userId, emailVec,false);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Hashtable();
    }

    /**
     * @param userId
     * @param emailVec
     * @return
     */
    public static Hashtable getEmailAbleSubscriberNumberByEmail(String userId, Vector emailVec) {

	if(Default.toDefault(userId).equals("")|| emailVec == null || emailVec.size()==0)
	    return new Hashtable();

	try{
	    return dataServer.getSubscriberNumberByEmail(userId, emailVec,true);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Hashtable();
    }
    /**
     * 
     * @param subscriber
     * @param campNum
     * @return
     */
    public static Hashtable getMailingSubscriberHash(SubscriberInfo subscriber, long campNum) {
	return getMailingSubscriberHash(subscriber, campNum, false);
    }
    /**
     * @param subscriber
     * @return
     */
    public static Hashtable getMailingSubscriberHash(SubscriberInfo subscriber, long campNum, boolean isPreview) {

	Hashtable hash = new Hashtable();
	if(subscriber==null)
	    return hash;

	long subNum = isPreview? 0: subscriber.getSubscriberNumber();

	hash.put(TemplateUtils.SUB_NUMBER_KEY, PMSEncoding.encode(""+subNum));

	hash.put(TemplateUtils.SUB_EMAIL_KEY, Default.toDefault(subscriber.getEmail()));
	hash.put(TemplateUtils.SUB_FIRSTNAME_KEY, Default.toDefault(subscriber.getFirstName()));
	hash.put(TemplateUtils.SUB_LASTNAME_KEY, Default.toDefault(subscriber.getLastName()));
	hash.put(TemplateUtils.SUB_ADDR_KEY, Default.toDefault(subscriber.getAddressLine1()));
	hash.put(TemplateUtils.SUB_ADDRESS2_KEY, Default.toDefault(subscriber.getAddressLine2()));
	hash.put(TemplateUtils.SUB_CITY_KEY, Default.toDefault(subscriber.getCity()));
	hash.put(TemplateUtils.SUB_STATE_KEY, Default.toDefault(subscriber.getStateCode()));
	hash.put(TemplateUtils.SUB_ZIP_KEY, Default.toDefault(subscriber.getZip()));
	hash.put(TemplateUtils.SUB_COUNTRY_KEY, Default.toDefault(subscriber.getCountryCode()));
	hash.put(TemplateUtils.SUB_PHONE_KEY, Default.toDefault(subscriber.getTelephone()));

	if(subscriber.getBirthDate()!=null)
	    hash.put(TemplateUtils.SUB_BIRTHDATE_KEY, Default.formatDate(subscriber.getBirthDate().getTime(), "yyyy-MM-dd"));

	hash.put(TemplateUtils.SUB_GENDER_KEY, Default.toDefault(subscriber.getGender()));

	hash.put(TemplateUtils.SUB_SOURCE_KEY, Default.toDefault(subscriber.getSource()));
	hash.put(TemplateUtils.SUB_SALESREP_KEY, Default.toDefault(subscriber.getSalesRep()));
	hash.put(TemplateUtils.SUB_SALESSTATUS_KEY, Default.toDefault(subscriber.getSalesStatus()));

	hash.put(TemplateUtils.SUB_MARITAL_STATUS_KEY, Default.toDefault(subscriber.getMaritalStatus()));
	hash.put(TemplateUtils.SUB_OCCUPATION_KEY, Default.toDefault(subscriber.getOccupation()));
	hash.put(TemplateUtils.SUB_HOUSEHOLD_INCOME_KEY, ""+subscriber.getHouseholdIncome());
	hash.put(TemplateUtils.SUB_INDUSTRY_KEY, Default.toDefault(subscriber.getIndustry()));
	hash.put(TemplateUtils.SUB_JOB_STATUS_KEY, Default.toDefault(subscriber.getJobStatus()));
	hash.put(TemplateUtils.SUB_EDUCATION_LEVEL_KEY, Default.toDefault(subscriber.getEducationLevel()));
	hash.put(TemplateUtils.SUB_COMPANY_KEY, Default.toDefault(subscriber.getCompany()));


	hash.put(TemplateUtils.SUB_UNSUBSCRIBE_URL, TemplateUtils.getSubscriberUnsubscribeSimpleLink(campNum, subNum));
	hash.put(TemplateUtils.SUB_EDIT_PROFILE_URL, TemplateUtils.getSubscriberUpdateLink(campNum, subNum));
	hash.put(TemplateUtils.SUB_TELL_AFRIEND_URL, TemplateUtils.getTellFriendLink(campNum, subNum));
	hash.put(TemplateUtils.SUB_SUBS_CENTER_URL, TemplateUtils.getSubscriberUnsubscribeUniversalLink(campNum, subNum));
	hash.put(TemplateUtils.SUB_WEB_VERSION_URL, TemplateUtils.getWebVersionLink(campNum, subNum));

	hash.put(TemplateUtils.SUB_FACEBOOK_SHARE_URL, TemplateUtils.getShareLinkForFacebook(campNum, subNum));
	hash.put(TemplateUtils.SUB_TWITTER_SHARE_URL, TemplateUtils.getShareLinkForTwitter(campNum, subNum));
	hash.put(TemplateUtils.SUB_LINKEDIN_SHARE_URL, TemplateUtils.getShareLinkForLinkedIn(campNum, subNum));
	hash.put(TemplateUtils.SUB_GOOGLEPLUS_SHARE_URL, TemplateUtils.getShareLinkForGooglePlus(campNum, subNum));
	hash.put(TemplateUtils.SUB_PINTEREST_SHARE_URL, TemplateUtils.getShareLinkForPinterest(campNum, subNum));

	Vector subDetails = subscriber.getCustomFields();
	for(int i=0; subDetails!=null && i<subDetails.size(); i++) {
	    SubscriberDetail detail = (SubscriberDetail)subDetails.elementAt(i);
	    hash.put("{{"+Default.toDefault(detail.getName()).trim()+"}}", Default.toDefault(detail.getValue()));
	}
	return hash;
    }


    //******** For Subscriber Activity *********/////////////////////////////////

    public static void getActivityHistoryVec(SubscriberInfo subInfo, Vector historyVec, Vector sortVec, Vector urlVec,Vector activityVec
	    ,long startDate, long endDate){

	try{

	    activityVec = getSubscriberActivityHistory(subInfo.getSubscriberNumber(), new Vector(), new Vector(),0,0,0,0);

	    Vector campHistoryVec = new Vector();

	    //loading Sent campaigns history
	    SubscriberSummaryBean subSummaryBean = subInfo.getSubscriberSummaryBean();
	    Vector sentCampListNumVec = Default.fromCSV(subSummaryBean.getSentCampListNumber());

	    if(sentCampListNumVec.size()>0){
		Vector campNumVec = new Vector();
		for(int i=0;i<sentCampListNumVec.size();i++) {
		    String campListNo = (String) sentCampListNumVec.get(i);
		    if(campListNo==null || campListNo.equals(""))
			continue;
		    campNumVec.add(campListNo.substring(0,campListNo.indexOf("#")));
		}
		//System.out.println(" Sent campaigns +++ "+campNumVec.size());
		Vector campNvVec = SubscriberManager.getSubscriberSentCampaignNumByOrder(campNumVec,subInfo.getSubscriberNumber(),null,null,0);
		Vector campNoVec = new Vector();

		if(campNvVec !=null && campNvVec.size()>0){
		    for(int i=0;i<campNvVec.size();i++){
			NameValue nv = (NameValue) campNvVec.get(i);
			campNoVec.add(nv.getValue());
		    }
		    //loading sent campaign's data
		    campHistoryVec = SubscriberManager.getSubscriberSentCampaignData(campNoVec,subInfo.getSubscriberNumber());
		}
	    }

	    //Single Message History
	    Vector msgHistoryVec = MessageManager.getSubscriberSingleMessageHistory(subInfo.getSubscriberNumber(),null,null,0,0);


	    // Arranging Subscriber's activity by date DESC
	    int historyIndex = 0;

	    if(activityVec!=null && activityVec.size()>0){

		//Inserting into new historyvec
		for(int i =0;i<activityVec.size();i++){
		    SubscriberActivityHistoryBean bean  = (SubscriberActivityHistoryBean) activityVec.get(i);
		    historyVec.add(bean);
		    sortVec.add(new NameValue(""+bean.getLogTime().getTime(),""+historyIndex++));

		    //For refilling campaign's name in ActivityHistoryBean
		    if(campHistoryVec != null && (bean.getActivityType().equals(SubscriberManager.ACTIVITY_CAMPAIGN_OPEN) ||
			    bean.getActivityType().equals(SubscriberManager.ACTIVITY_TELL_A_FRIEND) ||
			    bean.getActivityType().equals(SubscriberManager.ACTIVITY_UN_SUBSCRIBE) ||
			    bean.getActivityType().equals(SubscriberManager.ACTIVITY_SUPPRESS) ||
			    bean.getActivityType().equals(SubscriberManager.ACTIVITY_WEB_VISIT) ||
			    bean.getActivityType().equals(SubscriberManager.ACTIVITY_ARTICLE_CLICK) ||
			    bean.getActivityType().equals(SubscriberManager.ACTIVITY_SCORE_CHANGE) )){

			for(int j =0;j<campHistoryVec.size();j++){
			    CampaignDataBean cBean  = (CampaignDataBean) campHistoryVec.get(j);
			    if(bean.getIdOne() == cBean.getCampaignNumber()){
				bean.setName1(cBean.getSubject());
				break;
			    }
			}

			if(bean.getActivityType().equals(SubscriberManager.ACTIVITY_WEB_VISIT) && !urlVec.contains(bean.getURL()))
			    urlVec.add(bean.getURL());

			//For refilling Message's name in ActivityHistoryBean
		    } else if(msgHistoryVec != null && (bean.getActivityType().equals(SubscriberManager.ACTIVITY_MESSAGE_OPEN) ||
			    bean.getActivityType().equals(SubscriberManager.ACTIVITY_MESSAGE_SUPPRESS) ||
			    bean.getActivityType().equals(SubscriberManager.ACTIVITY_MESSAGE_URL_CLICK))){

			for(int j =0;j<msgHistoryVec.size();j++){
			    SingleMessageBean mBean  = (SingleMessageBean) msgHistoryVec.get(j);
			    if(bean.getIdOne() == mBean.getMsgId()){
				bean.setName1(mBean.getSubject());
				break;
			    }
			}
		    }
		}
	    }

	    if(campHistoryVec!=null && campHistoryVec.size()>0){
		for(int i =0;i<campHistoryVec.size();i++){
		    CampaignDataBean bean  = (CampaignDataBean) campHistoryVec.get(i);
		    historyVec.add(bean);
		    sortVec.add(new NameValue(""+bean.getScheduledDate().getTime(),""+historyIndex++));
		}
	    }

	    if(msgHistoryVec!=null && msgHistoryVec.size()>0){
		for(int i =0;i<msgHistoryVec.size();i++){
		    SingleMessageBean bean  = (SingleMessageBean) msgHistoryVec.get(i);
		    historyVec.add(bean);
		    sortVec.add(new NameValue(""+bean.getSentDate().getTime(),""+historyIndex++));
		}
	    }

	    System.out.println("urlVec size === "+urlVec.size());

	} catch(Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
    }


    public static Vector getTriggerMailActivities(Vector activityVec) {

	Vector mailVec = new Vector();
	if(activityVec.isEmpty())
	    return mailVec;

	for(int i=0; i<activityVec.size(); i++) {
	    SubscriberActivityHistoryBean bean  = (SubscriberActivityHistoryBean) activityVec.get(i);
	    if(bean.getActivityType().equalsIgnoreCase(SubscriberManager.ACTIVITY_WF_C2Y_TRIGGER_MAIL)) {
		mailVec.add(new NameValue(""+bean.getIdTwo(), ""+bean.getSubscriberNumber()));
	    }
	}
	return mailVec;
    }

    public static Vector getMyC2YTriggerMailActivities(Vector activityVec) {

	Vector mailVec = new Vector();
	if(activityVec.isEmpty())
	    return mailVec;

	for(int i=0; i<activityVec.size(); i++) {
	    SubscriberActivityHistoryBean bean  = (SubscriberActivityHistoryBean) activityVec.get(i);
	    if(bean.getActivityType().equalsIgnoreCase(SubscriberManager.ACTIVITY_MY_C2Y_TRIGGER_MAIL)) {
		mailVec.add(""+bean.getIdOne());
	    }
	}
	return mailVec;
    }

    public static String getMyC2YTriggerMailName(long mailId, Vector c2yMailBeanVec) {

	for(int i=0; i<c2yMailBeanVec.size(); i++) {
	    MyC2YTriggerMailBean bean = (MyC2YTriggerMailBean)c2yMailBeanVec.get(i);
	    if(bean.getMailId()==mailId) {
		return bean.getName().equals("")? "My Trigger Mail": bean.getName();
	    }
	}
	return "";
    }
    //***  End of Subscriber Activity ***********************/////////////////////

    public static void processUploadFileForSubscribers(UserInfo userInfo, String fileName, String filePath, String layout
	    , long listNumber, String optionalEmail) {

	processCSVUploadAsCampaignRecipients(userInfo, fileName, filePath, layout, listNumber, optionalEmail, 0);
    }
    /**
     *
     * @param userInfo
     * @param filenName
     * @param filePath
     * @param layout
     * @param listNumber
     */
    public static void processCSVUploadAsCampaignRecipients(UserInfo userInfo, String fileName, String filePath, String layout, long listNumber, String optionalEmail, long campNum) {

	if(userInfo==null || fileName.equals("") || filePath.equals("") || layout.equals("") || listNumber<=0) {
	    System.out.println("[SubscriberManager - processCSV]: ERR userInfo:"+userInfo+", fileName:"+fileName+", filePath:"+filePath+", layout:"+layout+", listNumber:"+listNumber);
	    return;
	}

	System.out.println("[SubscriberManager - processCSV]: OK userId:"+userInfo.getUserID()+", fileName:"+fileName+", filePath:"+filePath+", layout:"+layout+", listNumber:"+listNumber);

	CampaignDataBean campBean = campNum>0? CampaignManager.getCampaignData(campNum): null;
	try {

	    if(campBean!=null && campBean.getCampaignType().equalsIgnoreCase(PMSDefinitions.CAMPAIGN_TYPE_NORMAL) && campBean.getStatus().equalsIgnoreCase("D" )) {
		campBean.setCSVUploadRunning("Y");
		CampaignManager.createUpdateCampainDetails(campBean.getCampaignNumber(), campBean.getCampaignDetail());

		System.out.println("[SubcriberManager - csvUpload]: MARKED_CSV_UPLOAD_RUNNING  userId:"+userInfo.getUserID()+", campNum:"+campNum);
	    }

	    Vector layoutVec = Default.fromCSV(layout);
	    BufferedReader bf = new BufferedReader(new FileReader(new File(filePath+fileName)));
	    String csvLine = bf.readLine();

	    int lineNumber = 0;
	    long startTime = System.currentTimeMillis();
	    Vector errorVec = new Vector();

	    //==== Finding seperator ====
	    char[] c = csvLine.toCharArray();
	    String seperator = "";
	    for (int i = 0; i < c.length; i++) {
		if(c[i]==',' || c[i]=='|' || c[i]=='\t') {
		    seperator = String.valueOf(c[i]);
		    break;
		}
	    }

	    //====== Return IF no seperator found && layout is more than one column ========
	    if(seperator.trim().equals("") && layoutVec.size()>1) {

		System.out.println("[SubscriberManager - processCSV]:  -- NO_SEPERATOR_FOUND --  userId:"+userInfo.getUserID()+", fileName:"+fileName);
		sendConfirmationEmailForCSVUpload(userInfo, listNumber, fileName, new String[]{"0","0","0","0"}, errorVec, optionalEmail, startTime);

		alphaQueryServer.notifyCSVUploadFinished(userInfo.getUserID(), listNumber, fileName, 0, 0, 0, optionalEmail);
		return;

	    } else {
		System.out.println("[SubscriberManager - processCSV]: userId:"+userInfo.getUserID()+", fileName:"+fileName+", SEPERATOR:'"+seperator+"'");
	    }

	    Vector infoVec = new Vector();
	    int BATCH_COUNT = 500;

	    int processCount = 0;
	    int addCount = 0;
	    int updateCount = 0;

	    //==== Process Each Line In File ====
	    do {
		lineNumber++;
		//System.out.println("\n\n[Line:"+lineNumber+"]: "+csvLine);
		boolean isError = false;

		//--- Add empty space between two consective separators in one line ---
		if(!seperator.trim().equals("")) {
		    csvLine = csvLine.replaceAll(seperator+seperator, seperator + " " + seperator);
		    if (csvLine.endsWith(seperator))
			csvLine += " ";
		    if (csvLine.startsWith(seperator))
			csvLine = " " + csvLine;
		}

		//--- Create String Tokenizer for the CSV Line ---
		StringTokenizer lineTokenizer = new StringTokenizer(csvLine, seperator);

		//--- Fill up missing values at the end, in CSV line. ---
		if(lineTokenizer.countTokens()<layoutVec.size()) {
		    for(int k=0; k<(layoutVec.size()-lineTokenizer.countTokens()); k++) {
			csvLine += (seperator+" ");
		    }
		    lineTokenizer = new StringTokenizer(csvLine, seperator);
		}

		//--- Create Hash for fieldName & fieldValue. ---
		Hashtable mHash = new Hashtable();
		for(int i=0; i<layoutVec.size(); i++) {
		    String fieldName = (String)layoutVec.get(i);
		    if(lineTokenizer.hasMoreTokens())
			mHash.put(fieldName.trim(), lineTokenizer.nextToken().replaceAll("\"",""));
		    else {
			errorVec.add("[Line:"+lineNumber+"]: Given values and selected fields count mismatched.");
			isError = true;
		    }
		}

		//--- Check if values in CSV Line are more than the mapped fields ---
		if(!isError && lineTokenizer.hasMoreTokens()) {
		    errorVec.add("[Line:"+lineNumber+"]: Given values and selected fields count mismatched.");
		    isError = true;
		}

		//--- If no error found, load subscriber object & process in batch mode ---
		if(!isError) {
		    SubscriberInfo subInfo = loadSubscriberInfoFromHash(userInfo, mHash, layoutVec, listNumber, errorVec, lineNumber);
		    if(subInfo!=null) {
			//System.out.println("[Line:"+lineNumber+"]: email:"+subInfo.getEmail());
			infoVec.add(subInfo);

			if(infoVec.size()>=BATCH_COUNT) {
			    synchronized(ADD_SUBS_MONITOR) {

				long delay = System.currentTimeMillis();
				Vector rowStatistics = complexQueryServer.addSubscribers(infoVec, ""+listNumber, layout, userInfo);
				delay = (System.currentTimeMillis()-delay);

				updateCount += Default.defaultInt((String)rowStatistics.get(0));
				addCount += Default.defaultInt((String)rowStatistics.get(1));

				System.out.println("\n[SubscriberManager - uploadCSV]: userId:"+userInfo.getUserID()+", listNumber:"+listNumber+", subs.batch:("+processCount+" + "+infoVec.size()
					+"), added:"+addCount+", updated:"+updateCount+", delay:"+delay+"ms.");

				processCount += infoVec.size();
				infoVec.clear();

				try {
				    Thread.sleep(Default.MILLIES_FOR_ONE_SECOND*5);//5 seconds delay.
				} catch (Exception ex) { ; }
			    }
			}

		    }/* else
                        System.out.println("[Line:"+lineNumber+"]: SUBSCRIBER NOT LOADED.");*/
		} else {
		    System.out.println("[SubscriberManager - processCSV]: -- ERR_FOUND -- userId:"+userInfo.getUserID()+", fileName:"+fileName+", errVec[0]:"+(errorVec.size()>0? (String)errorVec.get(0): ""));
		}

		csvLine = bf.readLine();

	    } while(csvLine!=null);

	    //===== Check If Some Subscribers Are Left To Be Processed. ======
	    if(infoVec.size()>0) {
		System.out.println("[SubscriberManager - processCSV]: [Line:"+lineNumber+"]: records left to process:"+infoVec.size());

		synchronized(ADD_SUBS_MONITOR) {

		    long delay = System.currentTimeMillis();
		    Vector rowStatistics = complexQueryServer.addSubscribers(infoVec, ""+listNumber, layout, userInfo);
		    delay = (System.currentTimeMillis()-delay);

		    updateCount += Default.defaultInt((String)rowStatistics.get(0));
		    addCount += Default.defaultInt((String)rowStatistics.get(1));

		    System.out.println("\n[SubscriberManager - processCSV]: LAST_BATCH userId:"+userInfo.getUserID()+", listNumber:"+listNumber+", subs.batch:("+processCount+" + "+infoVec.size()
			    +"), added:"+addCount+", updated:"+updateCount+", delay:"+delay+"ms.");
		    processCount += infoVec.size();

		}
		try {
		    Thread.sleep(Default.MILLIES_FOR_ONE_SECOND*5);//5 seconds delay.
		} catch (Exception ex) { ; }
	    }

	    sendConfirmationEmailForCSVUpload(userInfo, listNumber, fileName, new String[]{""+lineNumber, ""+processCount, ""+addCount
		    , ""+updateCount}, errorVec, optionalEmail, startTime);

	    alphaQueryServer.notifyCSVUploadFinished(userInfo.getUserID(), listNumber, fileName, processCount, addCount, updateCount, optionalEmail);

	} catch(Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();

	} finally {
	    FileManager.deleteFile(fileName, filePath);

	    if(campBean!=null && campBean.getCampaignType().equalsIgnoreCase(PMSDefinitions.CAMPAIGN_TYPE_NORMAL) && campBean.getStatus().equalsIgnoreCase("D" )) {
		campBean.setCSVUploadRunning("N");
		CampaignManager.createUpdateCampainDetails(campBean.getCampaignNumber(), campBean.getCampaignDetail());
		System.out.println("[SubcriberManager - processCSV()]: MARKED_CSV_UPLOAD_FINISHED  userId:"+userInfo.getUserID()+", campNum:"+campNum);
	    }

	}
    }
    /**
     *
     * @param userInfo
     * @param listNumber
     * @param fileName
     * @param stats
     * @param errorVec
     * @param optionalEmail
     * @param startTime
     */
    private static void sendConfirmationEmailForCSVUpload(UserInfo userInfo, long listNumber, String fileName, String stats[]
                                                                                                                            , Vector errorVec, String optionalEmail, long startTime) {

	//Confirmation email
	try {
	    StringBuffer errorDetail = new StringBuffer("");
	    for(int i=0; i<errorVec.size(); i++) {
		errorDetail.append(" <br>"+(String)errorVec.get(i));
	    }
	    String addListName = Default.toDefault(ListManager.getListName(listNumber));
	    String delayText = Default.elapsedDateFormat(System.currentTimeMillis()-startTime, false);

	    StringBuffer body = new StringBuffer("");
	    mkNewEmailBody(body, userInfo, fileName, addListName, optionalEmail, Default.defaultInt(stats[0]), Default.defaultInt(stats[2])
		    , Default.defaultInt(stats[0])-Default.defaultInt(stats[1]), Default.defaultInt(stats[3]), errorDetail.toString(), true, "", "");

	    AlertMail.sendMail("BridgeMail .CSV Upload Confirmation",body.toString(),AlertMail.getSystemEmailAddress(),new String[]{optionalEmail},true);

	    body = new StringBuffer("");
	    mkNewEmailBody(body, userInfo, fileName, addListName, optionalEmail, Default.defaultInt(stats[0]), Default.defaultInt(stats[2])
		    , Default.defaultInt(stats[0])-Default.defaultInt(stats[1]), Default.defaultInt(stats[3]), errorDetail.toString(), true, delayText, ""+listNumber);

	    String subject = "Subs. Upload - "+userInfo.getUserID()+" - Add Count:"+Default.defaultInt(stats[2]);
	    AlertMail.sendMail(subject,body.toString(),AlertMail.getSystemEmailAddress(),AlertMail.getNotificationAddresses(),true);

	} catch(Exception se) {
	    se.printStackTrace();
	    WebServerLogger.getLogger().log(se);
	}
    }
    /**
     *
     * @param userInfo
     * @param mHash
     * @param layoutVec
     * @param listNumber
     * @return
     */
    private static SubscriberInfo loadSubscriberInfoFromHash(UserInfo userInfo, Hashtable mHash, Vector layoutVec, long listNumber
	    , Vector errorVec, int lineNumber) {

	if(userInfo==null || mHash.isEmpty() || layoutVec.isEmpty() || listNumber<=0)
	    return null;

	try {
	    SubscriberInfo subInfo = new SubscriberInfo();
	    for(int i=0; i<layoutVec.size(); i++) {
		String fieldName = ((String)layoutVec.get(i)).trim();
		if(mHash.get(fieldName)==null)
		    continue;

		String fieldValue = Default.toDefault((String)mHash.get(fieldName));
		if(!saveSubInfoValue(subInfo, fieldName, fieldValue, errorVec, lineNumber)) {
		    return null;//error occured in processing values.
		}
	    }
	    return subInfo;

	} catch(Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return null;
    }
    /**
     *
     * @param subInfo
     * @param fieldName
     * @param fieldValue
     * @param errorVec
     * @param lineNumber
     * @return
     */
    private static boolean saveSubInfoValue(SubscriberInfo subInfo, String fieldName, String fieldValue, Vector errorVec, int lineNumber) {

	try {
	    fieldName = fieldName.trim();
	    fieldValue = Default.toDefault(fieldValue).trim();

	    //==== Email Address ====
	    if(fieldName.equals(ListManager.getFieldName(0))) {

		if(!ValidationChecks.validateEmail(fieldValue)) {
		    if(fieldValue.trim().equals(""))
			errorVec.add("[Line:"+lineNumber+"]: Email Address not found");
		    else
			errorVec.add("[Line:"+lineNumber+"]: Email Address '"+fieldValue+"' is not valid");

		    return false;
		}

		try {
		    javax.mail.internet.InternetAddress check = new javax.mail.internet.InternetAddress(fieldValue);
		} catch (javax.mail.internet.AddressException ax) {
		    errorVec.add("[Line:"+lineNumber+"]: Email Address '"+fieldValue+"' is not valid");
		    return false;
		}
		subInfo.setEmail(Default.extractEmailWithoutName(fieldValue));

		//==== Email Type ====
	    } else if(fieldName.equals(ListManager.getFieldName(1))) {

		if(fieldValue.toUpperCase().equals(PMSDefinitions.EMAIL_TYPE_HTML)) {
		    fieldValue = PMSDefinitions.EMAIL_TYPE_HTML;
		} else if(fieldValue.toUpperCase().equals(PMSDefinitions.EMAIL_TYPE_TEXT)) {
		    fieldValue = PMSDefinitions.EMAIL_TYPE_TEXT;
		} else if(fieldValue.equals("")) {
		    fieldValue = PMSDefinitions.EMAIL_TYPE_HTML;
		} else {
		    errorVec.add("[Line:"+lineNumber+"]: Email Type '"+fieldValue+"' is not valid. Valid email types are 'H' or 'T'.");
		    return false;
		}
		subInfo.setEmailType(fieldValue);

		//==== Gender ====
	    } else if(fieldName.equals(ListManager.getFieldName(2))) {

		if(fieldValue.toUpperCase().equals(PMSDefinitions.GENDER_MAIL)) {
		    fieldValue = PMSDefinitions.GENDER_MAIL;
		} else if(fieldValue.toUpperCase().equals(PMSDefinitions.GENDER_FEMAIL)) {
		    fieldValue = PMSDefinitions.GENDER_FEMAIL;
		} else if(fieldValue.equals("")) {
		    ;//do nothing
		} else {
		    errorVec.add("[Line:"+lineNumber+"]: Gender '"+fieldValue+"' is not valid. Valid genders are 'M', 'F'");
		    return false;
		}
		subInfo.setGender(fieldValue);

		//==== First Name ====
	    } else if(fieldName.equals(ListManager.getFieldName(3))){
		subInfo.setFirstName(fieldValue);

		//==== Middle Name ====
	    } else if(fieldName.equals(ListManager.getFieldName(4))){
		subInfo.setMiddleName(fieldValue);

		//==== Last Name ====
	    } else if(fieldName.equals(ListManager.getFieldName(5))){
		subInfo.setLastName(fieldValue);

		//==== Birth Date (YYYY-MM-DD) ====
	    } else if(fieldName.equals(ListManager.getFieldName(6))){

		if(!fieldValue.equals("") && Default.isValidDate(fieldValue, "yyyy-MM-dd")) {
		    subInfo.setBirthDate(new java.sql.Timestamp(Default.parseDate(fieldValue, "yyyy-MM-dd")));

		} else if(!fieldValue.equals("") && !Default.isValidDate(fieldValue, "yyyy-MM-dd")) {
		    errorVec.add("[Line:"+lineNumber+"]: Invalid date value '"+fieldValue+"'. Valid date format is YYYY-MM-DD");
		    return false;
		}

	    } else if(fieldName.equals(ListManager.getFieldName(7))){
		subInfo.setAddressLine1(fieldValue);

	    } else if(fieldName.equals(ListManager.getFieldName(8))){
		subInfo.setAddressLine2(fieldValue);

	    } else if(fieldName.equals(ListManager.getFieldName(9))){
		subInfo.setCity(fieldValue);

	    } else if(fieldName.equals(ListManager.getFieldName(10))){
		subInfo.setStateCode(fieldValue);

	    } else if(fieldName.equals(ListManager.getFieldName(11))){
		subInfo.setZip(fieldValue);

	    } else if(fieldName.equals(ListManager.getFieldName(12))){
		subInfo.setCountryCode(fieldValue);

	    } else if(fieldName.equals(ListManager.getFieldName(13))){

		if(fieldValue.toUpperCase().equals(PMSDefinitions.MARITAL_STATUS_MARRIED)) {
		    fieldValue = PMSDefinitions.MARITAL_STATUS_MARRIED;
		} else if(fieldValue.toUpperCase().equals(PMSDefinitions.MARITAL_STATUS_SINGLE)) {
		    fieldValue = PMSDefinitions.MARITAL_STATUS_SINGLE;
		} else if(fieldValue.toUpperCase().equals(PMSDefinitions.MARITAL_STATUS_WIDOW)) {
		    fieldValue = PMSDefinitions.MARITAL_STATUS_WIDOW;
		} else if(fieldValue.equals("")) {
		    fieldValue = "";
		} else {
		    errorVec.add("[Line:"+lineNumber+"]: Marital Status '"+fieldValue+"' is not valid. Valid statuses are 'S', 'M', 'W'.");
		    return false;
		}
		subInfo.setMaritalStatus(fieldValue);

	    } else if(fieldName.equals(ListManager.getFieldName(14))){
		subInfo.setOccupation(fieldValue);

	    } else if(fieldName.equals(ListManager.getFieldName(15))){
		subInfo.setJobStatus(fieldValue);

	    } else if(fieldName.equals(ListManager.getFieldName(16))){
		subInfo.setHouseholdIncome(fieldValue);

	    } else if(fieldName.equals(ListManager.getFieldName(17))){
		subInfo.setEducationLevel(fieldValue);

	    } else if(fieldName.equals(ListManager.getFieldName(18))){
		subInfo.setTelephone(fieldValue);

	    } else if(fieldName.equals(ListManager.getFieldName(19))){
		subInfo.setCompany(fieldValue);

	    } else if(fieldName.equals(ListManager.getFieldName(20))){
		subInfo.setIndustry(fieldValue);

	    } else if(fieldName.equals(ListManager.getFieldName(21))){
		subInfo.setSource(fieldValue);

	    } else if(fieldName.equals(ListManager.getFieldName(22))){
		subInfo.setSalesRep(fieldValue);

	    } else if(fieldName.equals(ListManager.getFieldName(23))){

		if(SubscriberManager.getSalesStatusVec().contains(fieldValue.toUpperCase())) {
		    fieldValue = fieldValue.toUpperCase();
		}else {
		    fieldValue = PMSDefinitions.SALES_STATUS_CONTACT;
		}
		subInfo.setSalesStatus(fieldValue);

	    } else if(fieldName.equals(ListManager.getFieldName(24))){
		subInfo.setAreaCode(fieldValue);

	    } else {//=== Found Custom Field ===
		subInfo.getCustomFields().add(new SubscriberDetail(fieldName.trim(), fieldValue));
	    }

	} catch(Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return true;
    }
    /**
     *
     * @param body
     * @param user
     * @param fileName
     * @param listName
     * @param userEmail
     * @param totalRecords
     * @param addCount
     * @param badCount
     * @param updateCount
     * @param errorDetail
     * @param isCSV
     * @param delay
     * @param listNum
     */
    private static void mkNewEmailBody(StringBuffer body, UserInfo user, String fileName, String listName, String userEmail, int totalRecords, int addCount, int badCount
	    , int updateCount, String errorDetail, boolean isCSV, String delay, String listNum) {

	try {
	    String webServerURL = PMSResources.getInstance().getWebServerURL();
	    body.append("\n<html>\n<head>\n<link HREF=\"/pms/pms2.css\" rel=\"STYLESHEET\" TYPE=\"text/css\"/>\n<link HREF=\"/pms/jwb.css\" rel=\"STYLESHEET\" TYPE=\"text/css\"/>\n\n</head>\n<body leftmargin=\"0\" topmargin=\"0\" marginwidth=\"0\" marginheight=\"0\" >\n<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"90%\">\n<tr><td id=\"logo\"><img src=\"");
	    body.append(webServerURL+"graphics/logo.gif\" alt=\"BridgeMail Systems\"><br>&nbsp;</td></tr>\n</table>\n\n<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"90%\" align=\"center\" id=\"mainWrapper\">\n<tr><td id=\"mainContentCell\" align=\"left\" style=\"font-family:arial;font-size:16px;font-weight: bold;\">\n<img src=\"");
	    body.append(webServerURL+"img/newui/uploadSub_main.png\">&nbsp;&nbsp;\nAdd Subscribers</td></tr>\n<tr><td width=100%><hr style=\"color: #666699; height: 5px;\"/>&nbsp;</td></tr>\n</table>\n\n\n\n<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"90%\" align=\"center\" id=\"mainWrapper\" style=\"font-family:Arial, Helvetica, sans-serif; font-size:11px;\">\n<tr><td colspan=2 style=\"color:BLUE;\" align=center>");
	    if(delay!=null && !delay.equals(""))
		body.append("<b>UserID:  "+user.getUserID()+"</b><br>");

	    if(isCSV) {
		body.append("Your BridgeMail email File '"+fileName+"' was uploaded successfully");
	    } else {
		body.append("&nbsp;");
	    }
	    if(delay!=null && !delay.equals("")) {
		body.append("<br>Email sent to '"+userEmail+"'");
	    }

	    body.append("</td></tr>\n<tr><td colspan=2 style=\"background-color:#CCCCFE; padding:5px; font-size:14px;font-weight: bold;\">Upload Summary</td></tr>\n<tr><td colspan=2>&nbsp;</td></tr>\n<tr><td width=18%><b>Selected List:</td><td>");
	    body.append(listName);
	    if(listNum!=null && !listNum.equals(""))
		body.append("&nbsp;&nbsp;(#"+listNum+")");

	    body.append("</td></tr>\n<tr><td colspan=2>&nbsp;</td></tr>\n<tr><td style=\"background-color:#EEE;\" width=18%><b>Added Subscribers:</td><td style=\"background-color:#EEE;\">");
	    body.append(addCount);
	    body.append("</td></tr>\n<tr><td width=18%><b>Updated Subscribers:</td><td>"+updateCount);
	    body.append("</td></tr>\n<tr><td style=\"background-color:#EEE;\" width=18%><b>Bad Records:</td><td style=\"background-color:#EEE;\" >"+badCount);
	    body.append("</td></tr>\n<tr><td colspan=2>&nbsp;</td></tr>\n<tr><td style=\"background-color:#EEE;\" width=18%><b>Total Records Processed:</td><td style=\"background-color:#EEE;\" >");
	    body.append(totalRecords);
	    if(delay!=null && !delay.equals(""))
		body.append("&nbsp;&nbsp;(Processed in "+delay+")</td></tr>");

	    body.append("<tr><td colspan=2 style='font-size:14px;font-weight: bold;'><br><img border=0 src='"+webServerURL+"img/newui/idea2_20x20.png'>&nbsp;File Layout Mismatched ?</td></tr>");
	    body.append("<tr><td colspan=2> <hr size=2 style='color:#CCCCFE;'></td></tr>");
	    body.append("<tr><td colspan=2><table width=100%><tr><td style='font-size:12px;font-weight: bold;'>");
	    body.append("<br>Watch a video about how to remove commas from .CSV files.");
	    body.append("<br>The most common cause of mismatched file layout.</td>");
	    body.append("<td style='padding:5px; font-size:14px;font-weight: bold;'><a style='text-decoration:none;' href='http://www.makesbridge.com/index.php?option=com_content&view=article&id=112&Itemid=133'>");
	    body.append("<img border=0 src='"+webServerURL+"img/newui/video_main.png'>&nbsp;&nbsp;Watch Video</a><td><tr>");
	    body.append("</table></td></tr>");

	    body.append("</td></tr>\n<tr><td colspan=2>&nbsp;</td></tr>\n\n<tr><td colspan=2 style=\"background-color:#CCCCFE; padding:5px; font-size:14px;font-weight: bold;\">Bad Records Detail</td></tr>\n");
	    body.append("<tr><td colspan=2>");
	    body.append(errorDetail);
	    body.append("</td></tr>\n</table>\n\n");
	    body.append("\n");

	    body.append("<center>\n");
	    body.append("<table>\n");
	    body.append("  <tr>\n");
	    body.append("    <td align=\"center\" style=\"font-size:11px;font-family:arial;\"><br>\n");
	    body.append("      ");
	    body.append(PMSDefinitions.COPYRIGHT_STATEMENT);
	    body.append("<br>&nbsp;\n");
	    body.append("    </td>\n");
	    body.append("  </tr>\n");
	    body.append("</table>\n");
	    body.append("</center>\n");

	    body.append("\n</body>\n</html>");

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
    }

    /**
     *
     * @param userInfo
     * @param subInfoVec
     * @return
     */
    private static boolean isSubscriberLimitValid(UserInfo userInfo, int subSize){
	try{
	    return (SubscriberManager.getSubscriberCount(userInfo.getUserID()) + subSize > userInfo.getSubscriberLimit());
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }


    /**
     * 
     * @param userId
     * @param campaignNumber
     * @param emailVec
     * @return
     */
    public static int addSubscribersToCampaignTransmission_TMP(String userId, long campaignNumber, Vector emailVec) {

	if(Default.toDefault(userId).equals("") || campaignNumber<1 || emailVec.size()==0)
	    return 0;
	try {
	    return complexQueryServer.addSubscribersToCampaignTransmission_TMP(userId, campaignNumber, emailVec);
	} catch(Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;
    }

    /**
     * 
     * @param subscriberNumber
     * @param tagsVec
     * @return
     */
    public static int createSubscriberTags(String userId, long subscriberNumber, Vector tagsVec){
	if(subscriberNumber<1 || tagsVec == null || tagsVec.isEmpty())
	    return 0;
	try{
	    return alphaQueryServer.createSubscriberTags(userId, subscriberNumber, tagsVec);
	}catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;    	
    }

    /**
     * 
     * @param subscriberNumber
     * @param tagsVec
     * @return
     */
    public static int deleteSubscriberTags(String userId, long subscriberNumber, Vector tagsVec){
	if(subscriberNumber<1 || tagsVec == null || tagsVec.isEmpty())
	    return 0;
	try{
	    return alphaQueryServer.deleteSubscriberTags(userId, subscriberNumber, tagsVec);
	}catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;    	
    }    


    /**
     * 
     * @param userId
     * @param tagsVec
     * @param offset
     * @param limit
     * @return
     */
    public static Vector getSubscribersByTag(String userId, String tag, int offset, int bucket){

	if(Default.toDefault(userId).equals("") || Default.toDefault(tag).length()<2 || offset<0)
	    return new Vector();
	try{
	    return alphaQueryServer.getSubscribersByTag(userId, tag, offset, bucket);
	}catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();    	
    }        

    /**
     * 
     * @param userId
     * @param tagsVec
     * @param offset
     * @param limit
     * @return
     */
    public static Vector searchSubscribersByTag(String userId, String tag, int offset, int bucket){
	if(Default.toDefault(userId).equals("") || Default.toDefault(tag).equals("") || offset<0)
	    return new Vector();
	try{
	    return alphaQueryServer.searchSubscribersByTag(userId, tag, offset, bucket);
	}catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();    	
    }    

    /**
     * 
     * @param userId
     * @param tag
     * @return
     */
    public static int getSubscribersCountByTag(String userId, String tag, boolean isSearch){
	if(Default.toDefault(userId).equals("") || Default.toDefault(tag).length()<2)
	    return 0;
	try{
	    return alphaQueryServer.getSubscribersCountByTag(userId, tag,isSearch);
	} catch(Exception ex){
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
    public static Vector getUserSubscriberTags(String userId){
	if(Default.toDefault(userId).equals(""))
	    return new Vector();
	try{
	    return alphaQueryServer.getUserSubscriberTags(userId);
	}catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();    	
    }
    /**
     * 
     * @param userId
     * @param tag
     * @return
     */
    public static Vector countTagRespondersPercentage(String userId, String tag) {

	try {
	    return dataServer.countTagRespondersPercentage(userId, tag);
	}catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     * 
     * @param userId
     * @param tag
     * @param searchText
     * @return
     */
    public static int countSubsByTags(String userId, String tag, String searchText) {

	if(userId.equals("") || tag.equals(""))
	    return 0;

	try {
	    return alphaQueryServer.countSubsByTags(userId, tag, searchText);
	}catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;
    }
    /**
     * 
     * @param userId
     * @param tag
     * @param searchText
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector getSubsByTags(String userId, String tag, String searchText, int offset, int bucket) {

	if(userId.equals("") || tag.equals(""))
	    return new Vector();

	try {
	    return alphaQueryServer.getSubsByTags(userId, tag, searchText, offset, bucket);
	}catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }

    /**
     * 
     * @param userId
     * @param subNum
     * @param listNum
     * @return
     */
    public static boolean changeListSubscriptionStatus(String userId,long subNum, long listNum, String status){
	if(Default.toDefault(userId).equals("") || subNum < 1 || listNum < 1)
	    return false;
	try{
	    complexQueryServer2.changeSubscriberStatus(userId, Default.toVector(new NameValue(""+subNum,""+listNum)), status);
	    return true;
	}catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;    	
    }


    /**
     * 
     * @param subNum
     * @param listNum
     * @return
     */
    public static boolean unSubscribe(long subNum,long listNum){
	if(subNum < 1 || listNum < 1)
	    return false;
	try{
	    return dataServer.unSubscribe(subNum, listNum);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }

    /**
     * 
     * @param userId
     * @param subNum
     * @return
     */
    public static SubscriberActivityHistoryBean getLastSeenActivity(String userId, long subNum){
	if(Default.toDefault(userId).equals("") || subNum < 1)
	    return null;
	try{
	    return alphaQueryServer.getLastSeenSubscriberActivity(userId, subNum);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return null;    	
    }

    /**
     * 
     * @param userId
     * @param subNum
     * @param activityTypeVec
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getSubscriberActivityCountByType(String userId, long subNum, Vector activityTypeVec, long startDate, long endDate){
	if(Default.toDefault(userId).equals("") || subNum < 1 || activityTypeVec.isEmpty())
	    return 0;
	try{
	    return alphaQueryServer.getSubscriberActivityCountByType(userId, subNum, activityTypeVec, startDate, endDate);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;    	
    }

    /**
     * 
     * @param userId
     * @param subNum
     * @return
     */
    public static Vector getSubscriberInvolvedInStats(String userId, long subNum){
	if(Default.toDefault(userId).equals("") || subNum < 1)
	    return new Vector();
	try{
	    return alphaQueryServer.getSubscriberInvolvedInStats(userId, subNum);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();    	
    }
    /**
     * 
     * @param userId
     * @param activityTypeVec
     * @param subNum
     * @param isFuture
     * @return
     */
    public static Vector getSubscriberWorkflowTransmissionHistory(String userId, Vector activityTypeVec, long subNum, boolean isFuture) {
	return getSubscriberWorkflowTransmissionHistoryByWorkflowId(userId, activityTypeVec, 0, subNum, isFuture);
    }
    /**
     * == WorkflowId is optional ==
     * 
     * @param userId
     * @param activityTypeVec
     * @param workflowId
     * @param subNum
     * @param isFuture
     * @return
     */
    public static Vector getSubscriberWorkflowTransmissionHistoryByWorkflowId(String userId, Vector activityTypeVec, int workflowId, long subNum, boolean isFuture) {

	if(userId.equals("") || subNum<=0)
	    return new Vector();

	try{
	    return alphaQueryServer.getSubscriberWorkflowTransmissionHistory(userId, activityTypeVec, workflowId, subNum, isFuture);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();    	
    }
    /**
     * == TrackId is optional ==
     * 
     * @param userId
     * @param activityTypeVec
     * @param trackId
     * @param subNum
     * @param isFuture
     * @return
     */
    public static Vector getSubscriberTriggerTransmissionFutureHistory(String userId, Vector activityTypeVec, int trackId, long subNum, boolean isFuture) {

	if(subNum<=0)
	    return new Vector();

	try{
	    return alphaQueryServer.getSubscriberTriggerTransmissionFutureHistory(userId, activityTypeVec, trackId, subNum, isFuture);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();    	
    }    
    /**
     * 
     * @param userId
     * @param activityTypeVec
     * @param botId
     * @param subNum
     * @param isFuture
     * @return
     */
    public static Vector getSubscriberAutobotTransmissionFutureHistory(String userId, Vector activityTypeVec, int botId, long subNum, boolean isFuture) {

	if(subNum<=0)
	    return new Vector();

	try{
	    return alphaQueryServer.getSubscriberAutobotTransmissionFutureHistory(userId, activityTypeVec, botId, subNum, isFuture);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();    	
    }    
    /**
     * ==== CampNums are optional ====
     * @param subNum
     * @return
     */
    public static Vector getSubscriberEvergreenTransmissionFutureHistory(String userId, Vector activityTypeVec, Vector campNumVec, long subNum, boolean isFuture) {
	if(subNum<0)
	    return new Vector();
	try{
	    return alphaQueryServer.getSubscriberEvergreenTransmissionFutureHistory(userId, activityTypeVec, campNumVec, subNum, isFuture);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();    	
    }    
    /**
     * 
     * @param userId
     * @param subNum
     * @return
     */
    public static Vector getSubscriberSingleMessageEmails(String userId, long subNum) {

	if(subNum<0)
	    return new Vector();
	try{
	    return alphaQueryServer.getSubscriberSingleMessageEmails(userId, subNum);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     * 
     * @param userId
     * @param subNum
     * @return
     */
    public static Vector getSubscriberMyC2YTriggerMails(String userId, long subNum) {

	if(subNum<0)
	    return new Vector();
	try{
	    return alphaQueryServer.getSubscriberMyC2YTriggerMails(userId, subNum);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     *
     * @param samUserBean
     * @return
     */
    public static Vector getSAMSubscribersStats(DashboardSAMBean bean, boolean totalCountOnly) {

	if(bean == null || bean.getUserId().equals(""))
	    return new Vector();
	try{
	    return alphaQueryServer.getSAMSubscribersStats(bean, totalCountOnly);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     * 
     * @param bean
     * @return
     */
    public static Vector getSAMSubscribersStats(DashboardSAMBean bean){
	return getSAMSubscribersStats(bean, false);
    }

    /**
     *
     * @param userId
     * @param value
     * @return
     */
    public static Vector searchSAMSubscribers(DashboardSAMBean bean, String value,String orderBy, String order, int offset, int bucket){
	if(bean == null || bean.getUserId().equals(""))
	    return new Vector();
	try {
	    return alphaQueryServer.searchSAMSubscribers(bean,value, false, orderBy,order, offset, bucket);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }

    /**
     * 
     * @param bean
     * @param value
     * @return
     */
    public static int getSAMSubscribersSearchCount(DashboardSAMBean bean, String value){
	if(bean == null || bean.getUserId().equals(""))
	    return 0;
	try{
	    return alphaQueryServer.searchSAMSubscribersCount(bean, value, false);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;
    }

    /**
     * 
     * @param bean
     * @param tag
     * @return
     */
    public static int getSAMSubscribersCountByTag(DashboardSAMBean bean, String tag){
	if(bean == null || bean.getUserId().equals("") ||  Default.toDefault(tag).length()<2)
	    return 0;
	try{
	    return alphaQueryServer.searchSAMSubscribersCount(bean,tag,true);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;
    }

    /**
     * 
     * @param bean
     * @param tag
     * @param orderBy
     * @param order
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector getSAMSubscribersByTag(DashboardSAMBean bean, String tag, String orderBy, String order, int offset, int bucket){
	if(bean == null || Default.toDefault(tag).length()<2 || bucket == 0)
	    return new Vector();
	try{
	    return alphaQueryServer.searchSAMSubscribers(bean, tag, true, orderBy, order, offset, bucket);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }


    /**
     *
     * @param userId
     * @param value
     * @return
     */
    public static Vector getSAMSubscribers(DashboardSAMBean bean, String filterBy, String orderBy, String order, int offset, int bucket){
	if(bean == null || bean.getUserId().equals(""))
	    return new Vector();
	try {
	    return alphaQueryServer.getSAMSubscribers(bean,filterBy,orderBy,order, offset, bucket);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }

    /**
     * 
     * @param bean
     * @param value
     * @return
     */
    public static int getSAMSubscribersCount(DashboardSAMBean bean, String filterBy){
	if(bean == null || bean.getUserId().equals(""))
	    return 0;
	try{
	    return alphaQueryServer.getSAMSubscribersCount(bean,filterBy);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;
    }
    /**
     * 
     * @param userId
     * @param subNum
     * @param campNum
     * @param startDate
     * @param endDate
     * @return
     */
    public static Vector getSubscriberClicks(String userId, long subNum, long campNum, long startDate, long endDate) {

	try {
	    return reportQueryServer.getSubscriberClicks(userId, subNum, campNum, startDate, endDate);

	} catch(Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }


    public static SubscriberInfo loadSubscriberInfoFromHash(Hashtable hash) {

	if(hash.isEmpty() || Default.toDefault((String)hash.get("EMAIL_ADDR")).trim().equals(""))
	    return null;

	try {
	    SubscriberInfo subInfo = new SubscriberInfo();
	    subInfo.setEmail((String)hash.get("EMAIL_ADDR"));

	    subInfo.setFirstName((String)hash.get("FIRST_NAME"));
	    subInfo.setMiddleName((String)hash.get("MIDDLE_NAME"));   			
	    subInfo.setLastName((String)hash.get("LAST_NAME"));
	    subInfo.setIndustry((String)hash.get("INDUSTRY"));
	    subInfo.setCompany((String)hash.get("COMPANY"));
	    subInfo.setOccupation((String)hash.get("OCCUPATION"));
	    subInfo.setTelephone((String)hash.get("TELEPHONE"));
	    subInfo.setCity((String)hash.get("CITY"));
	    subInfo.setCountryCode((String)hash.get("COUNTRY_CODE"));
	    subInfo.setStateCode((String)hash.get("STATE_CODE"));
	    subInfo.setZip((String)hash.get("ZIP_CODE"));
	    subInfo.setAddressLine1((String)hash.get("ADDRESS_LINE1"));
	    subInfo.setAddressLine2((String)hash.get("ADDRESS_LINE2"));
	    subInfo.setSalesRep((String)hash.get("SALES_REP"));
	    subInfo.setSalesStatus((String)hash.get("SALES_STATUS"));   				
	    subInfo.setAreaCode((String)hash.get("AREA_CODE"));
	    subInfo.setSource((String)hash.get("SOURCE"));   				
	    subInfo.setJobStatus((String)hash.get("JOB_STATUS"));
	    subInfo.setEducationLevel((String)hash.get("EDUCATION_LEVEL"));

	    if(!Default.toDefault((String)hash.get("BIRTH_DATE")).equals(""))
		subInfo.setBirthDate(new Timestamp(Default.parseDate((String)hash.get("BIRTH_DATE"), "yyyy-m-d")));

	    //== custom fields
	    Vector keysVec = Default.toKeysVector(hash);
	    for(int i=0; i<keysVec.size(); i++) {
		String key = (String)keysVec.get(i);

		//== if custom field is not already loaded
		if(subInfo.getCustomFieldValue(key) == null && !getLayoutVec().contains(key)){
		    //subInfo.getCustomFields().add(new NameValue(key,(String) hash.get(key)));
		    subInfo.getCustomFields().add(new SubscriberDetail(key,(String) hash.get(key)));					
		}
	    }
	    return subInfo;
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return null;
    }

    /**
     * 
     * @param userId
     * @param activityTypeVec
     * @param allowedCampNumVec
     * @param crmType
     * @param startDate
     * @param endDate
     * @param limit
     * @param offset
     * @return
     */
    public static Vector getActivityHistoryByDateRange(String userId, Vector activityTypeVec, String crmType, long startDate,
	    long endDate, int offset, int bucket) {

	if(Default.toDefault(userId).equals("") || startDate<=0 || endDate <= 0 || bucket <=0)
	    return new Vector();

	try {
	    return dataServer.getActivityHistoryByDateRange(userId, activityTypeVec, crmType, startDate, endDate, offset, bucket);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }

    /**
     * 
     * @param timelineVec
     * @param allowedCampNumVec
     */
    public static void removeEventsForDeletedAndNotAllowedCampaigns(Vector timelineVec, Vector allowedCampNumVec) {

	if(timelineVec.isEmpty())
	    return;

	try{
	    Vector campNumVec = new Vector();
	    for(int i=0; i<timelineVec.size(); i++) {
		SubscriberTimelineBean bean = (SubscriberTimelineBean)timelineVec.get(i);
		if(bean.getCampaignNumber()>0 && !campNumVec.contains(""+bean.getCampaignNumber()))
		    campNumVec.add(""+bean.getCampaignNumber());
	    }

	    Vector existingCampNumVec = CampaignManager.removeDeletedCampaigns(campNumVec);
	    Vector deletedCampNumVec = new Vector(campNumVec);
	    deletedCampNumVec.removeAll(existingCampNumVec);

	    //==== Remove Events For Deleted Campaigns ====
	    for(int i=0; deletedCampNumVec.size()>0 && i<timelineVec.size(); i++) {
		SubscriberTimelineBean bean = (SubscriberTimelineBean)timelineVec.get(i);
		if(bean.getCampaignNumber()>0 && deletedCampNumVec.contains(""+bean.getCampaignNumber())) {
		    timelineVec.remove(i);
		    --i;
		}
	    }

	    //===== Remove Events For Non-Required Campaigns ====
	    for(int i=0; allowedCampNumVec.size()>0 && i<timelineVec.size(); i++) {
		SubscriberTimelineBean bean = (SubscriberTimelineBean)timelineVec.get(i);
		if(bean.getCampaignNumber()>0 && !allowedCampNumVec.contains(""+bean.getCampaignNumber())) {
		    timelineVec.remove(i);
		    --i;
		}
	    }
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
    }
    /**
     * 
     * @param bean
     * @param lastXDays
     * @param activityType
     * @return
     */
    public static int searchSAM_SubscriberActivityHistoryCount(DashboardSAMBean bean, int lastXDays, String activityType) {

	if(bean==null)
	    return 0;
	
	try {
	    return alphaQueryServer.searchSAM_SubscriberActivityHistoryCount(bean, lastXDays, activityType);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;
    }
    /**
     * 
     * @param bean
     * @param lastXDays
     * @param activityType
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector searchSAM_SubscriberActivityHistory(DashboardSAMBean bean, int lastXDays, String activityType, int offset, int bucket) {

	if(bean==null || offset<0 || bucket<=0)
	    return new Vector();

	try {
	    return alphaQueryServer.searchSAM_SubscriberActivityHistory(bean, lastXDays, activityType, offset, bucket);
	    
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }

}
