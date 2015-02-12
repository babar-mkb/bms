package com.PMSystems;

import com.PMSystems.sejbs.*;
import com.PMSystems.util.*;
import com.PMSystems.logger.*;
import com.PMSystems.dbbeans.*;
import com.PMSystems.mail.*;


import java.io.*;
import java.util.*;
import java.sql.Timestamp;
/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */


public class TrialAccountManager {

	private static AlphaQueryServer alphaQueryServer;
	private static DataQueryServer dataServer;
	private static ReportsQueryServer reportServer;

	public static final String CREATE_EVENT = "cr";
	public static final String CONFIRMATION_EVENT = "co";
	public static final String USERID_AVAIL_EVENT = "av";

	public static final String TRIAL_SUBSCRIPTION_USERID = "jayadams";
	public static final int TRIAL_SUBSCRIPTION_LIST_NUMBER = 96;


	//Trial Account's limitations
	private static final int EMAIL_SENT_LIMIT = 500;
	public static final int SUBSCRIBER_LIMIT = 500;
	public static final int CAMPAIGN_LIMIT = 3;
	public static final int SINGLE_MSG_LIMIT = 5;
	public static final int WORKFLOW_LIMIT = 1;
	public static final int LIST_LIMIT = 5;
	public static final int SIGNUP_FORM_LIMIT = 5;
	public static final int TEMPLATE_LIMIT = 5;
	public static final int REQUEST_FROM_SAME_IP = 3;
	public static final int REQUESTED_USERID_LENGTH = 5;
	/**    
    private static String blackListed = "yahoo,gmail,hotmail,live.com,live.co,mail.com,aol.com,inbox.com,zoho,usa.net,gmx,outlook.com"
        +",lycos,hushmail,incontact,eloqua,exacttarget,marketo,verticalresponse,acton,predictiveresponse,hubspot,mailchimp,infusionsoft"
        +",icontact,constantcontact,wiredmarketing,benchmarkemail,Pinpointe,getresponse,mailigen,graphicmail,campaigner,pure360"
        +",littlegreenplane,dotmailer,extravision,ekmresponse,mailinator,manybrain";
    private static final Vector blackListedDomains = Default.fromCSV(blackListed);
	 **/

	private TrialAccountManager() {
	}


	static{
		alphaQueryServer = EJBHomesFactory.getAlphaQueryServerRemote();
		dataServer = EJBHomesFactory.getDataQueryServerRemote();
		reportServer = EJBHomesFactory.getReportsQueryServerRemote();		
	}


	/**
	 *
	 * @param bean
	 * @return
	 */
	public static int createTrialAccount(TrialAccountDataBean bean){
		if(bean == null || bean.getUserID().equals("") || bean.getEmail().equals("")){
			System.out.println("not valid bean input");
			return 0;
		}

		try{
			return alphaQueryServer.createTrialAccount(bean).getTrialId();
		} catch (Exception ex){
			WebServerLogger.getLogger().log(ex);
			ex.printStackTrace();
		}
		return 0;
	}

	/**
	 *
	 * @param trialId
	 * @return
	 */
	public static TrialAccountDataBean getTrialAccount(int trialId){
		if(trialId<1)
			return null;
		try{
			return alphaQueryServer.getTrialAccount(trialId);
		} catch (Exception ex){
			WebServerLogger.getLogger().log(ex);
			ex.printStackTrace();
		}
		return null;
	}


	/**
	 *
	 * @param userId
	 * @return
	 */
	public static boolean isActiveTrialAccountExist(String userId){
		if(Default.toDefault(userId).equals(""))
			return false;
		try{
			return alphaQueryServer.isActiveTrialAccountExist(userId);
		} catch (Exception ex){
			WebServerLogger.getLogger().log(ex);
			ex.printStackTrace();
		}
		return false;
	}



	/**
	 *
	 * @param email
	 * @return
	 */
	public static boolean isTrialAccountEmailExist(String email){
		if(Default.toDefault(email).equals(""))
			return false;
		try{
			return alphaQueryServer.isTrialAccountEmailExist(email);
		} catch (Exception ex){
			WebServerLogger.getLogger().log(ex);
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 *
	 * @param formId
	 * @return
	 */
	public static String getTrialAccountCreationURL(long formId){
		try{
			PMSResources resources = PMSResources.getInstance();
			return getTrialAccountServletPath()+"?type=cr";
		} catch(Exception ex){
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
		}
		return "";
	}


	/**
	 *
	 * @param trialId
	 * @param email
	 * @return
	 */
	public static String getTrialAccountActivationURL(long trialId,String email){
		try{
			if(trialId == 0)
				return "";

			PMSResources resources = PMSResources.getInstance();
			return getTrialAccountServletPath()+"?tId="+PMSEncoding.encode(""+trialId)+"&e="+email+"&type=co";
		} catch(Exception ex){
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
		}
		return "";
	}

	public static String getTrialAccountServletPath(){
		try{
			PMSResources resources = PMSResources.getInstance();
			return "https://"+resources.getWebServerDomainName()+"/pms/trial";
		} catch(Exception ex){
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
		}
		return "";
	}


	/**
	 *
	 * @param bean
	 * @return
	 */
	public static boolean activateTrialAccount(TrialAccountDataBean bean) {

		if(bean==null || bean.getUserID().equals("") || bean.getEmail().equals(""))
			return false;

		try{
			System.out.println("going to activate Trial Account at MakesBridge.");
			long customerNumber = createTrialCustomer(bean);
			if(customerNumber<1)
				return false;

			String userKey = ApplicationManager.generateUniqueUserKey(bean.getUserID());
			if(userKey==null || userKey.equals("")) {
				System.out.println("[/TrialAccountManager/activateTrialAccount]: Unable to generate UserKey for userId: "+bean.getUserID());
				return false;
			}

			return createTrialUser(bean,customerNumber,userKey);
		} catch(Exception ex){
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
		}
		return false;
	}
	/**
         private static void saveRoles() {
        try {
            RoleDataBean[] allRoles = dataServer.getAllRoles();

            if(groups!=null && groups.length>0 && allRoles!=null && allRoles.length>0)
                for(int i=0; i<groups.length; i++) {
                    for(int j=0; j<allRoles.length; j++) {
                        if(groups[i].equalsIgnoreCase(""+allRoles[j].getRoleNumber())) {
                            roles.add(i, allRoles[j]);
                            break;
                        }
                    }
                }

        } catch(Exception e) {
            e.printStackTrace();
        }
         }
	 **/

	/**
	 *
	 * @param bean
	 * @return
	 */
	public static boolean sendActivationEmail(TrialAccountDataBean bean){
		try{
			if(bean == null)
				return false;
			StringBuffer body = new StringBuffer();
			String fromEmail = "support@makesbridge.com";
			MailResources resources = new MailResources();
			//loading email body for User
			String activationURL = getTrialAccountActivationURL(bean.getTrialId(),bean.getEmail());
			getAccountActivationEmailBody(body,bean,activationURL);
			//sending notification email to user
			AlertMail.sendMail("MakesBridge Trial Account",body.toString(), fromEmail,new String[]{bean.getEmail()}, true);
			//sendig notification email at System notification address too
			//loading email body for User
			body = new StringBuffer();
			getAccountSystemNotificationEmailBody(body,bean,activationURL);
			//sending notification email too
			return AlertMail.sendMail("MakesBridge Trial Account Request ["+bean.getSource()+"]",body.toString(), fromEmail,resources.getNotificationAddress(), true);
		} catch(Exception ex){
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
		}
		return false;
	}

	/**
	 *
	 * @param body
	 * @param bean
	 */
	private static void getAccountActivationEmailBody(StringBuffer body, TrialAccountDataBean bean,String activationURL){

		try {
			String webServerURL = PMSResources.getInstance().getWebServerURL();
			String accType = "MakesBridge Trial Account Activation Email";

			//if request is data.com offer
			if(bean.getSource().equalsIgnoreCase("data.com offer")){
				System.out.println("[TrialAccountManager] getting request from data.com == ");
				getDataComOfferEmailBody(body,bean,activationURL);
				return;
			}

			// else if request not coming from DataComOffer
			String accMsg = "Congratulations "+bean.getFirstName()+" your Account has been created successfully at MakesBridge! Please click the url below to activate and log into your MakesBridge account."
			+"<br><a href="+activationURL+">"+activationURL+"</a><br> If you have any questions, please send email to support@makesbridge.com.";

			body.append("\n<html>\n<head>\n<link HREF=\"/pms/pms2.css\" rel=\"STYLESHEET\" TYPE=\"text/css\"/>\n<link HREF=\"/pms/jwb.css\" rel=\"STYLESHEET\" TYPE=\"text/css\"/>\n\n</head>\n<body leftmargin=\"0\" topmargin=\"0\" marginwidth=\"0\" marginheight=\"0\" >\n<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"90%\">\n<tr><td id=\"logo\"><img src=\"");
			body.append(webServerURL+"graphics/logo.gif\" alt=\"BridgeMail Systems\"><br>&nbsp;</td></tr>\n</table>\n\n<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"90%\" align=\"center\" id=\"mainWrapper\">\n<tr><td id=\"mainContentCell\" align=\"left\" style=\"font-family:arial;font-size:16px;font-weight: bold;\">\n<img src=\"");
			body.append(webServerURL+"img/newui/single-subscriber_40x40.png\">&nbsp;&nbsp;\n "+accType+"</td></tr>\n<tr><td width=100%><hr style=\"color: #666699; height: 5px;\"/>&nbsp;</td></tr>\n</table>\n\n\n\n<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"90%\" align=\"center\" id=\"mainWrapper\" style=\"font-family:Arial, Helvetica, sans-serif; font-size:11px;\">\n<tr><td colspan=2 style=\"color:BLUE;\" align=left>");
			body.append("<b>MakesBridge Requested User ID:  "+bean.getUserID()+"</b><br><br>");

			body.append("\n\n");
			body.append("<tr><td colspan=2>");
			body.append(accMsg);
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
	 * @param body
	 * @param bean
	 */
	public static void getAccountSystemNotificationEmailBody(StringBuffer body, TrialAccountDataBean bean, String activationURL){

		try {
			String webServerURL = PMSResources.getInstance().getWebServerURL();
			String accType = "MakesBridge Trial Account Request Email ["+bean.getSource()+"]";

			// else if request not coming from DataComOffer
			String accMsg = "User Trial Account request has been created successfully from Email ["+bean.getEmail()+"] at MakesBridge. Activation URL "+activationURL+" \n\n";

			body.append("\n<html>\n<head>\n<link HREF=\"/pms/pms2.css\" rel=\"STYLESHEET\" TYPE=\"text/css\"/>\n<link HREF=\"/pms/jwb.css\" rel=\"STYLESHEET\" TYPE=\"text/css\"/>\n\n</head>\n<body leftmargin=\"0\" topmargin=\"0\" marginwidth=\"0\" marginheight=\"0\" >\n<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"90%\">\n<tr><td id=\"logo\"><img src=\"");
			body.append(webServerURL+"graphics/logo.gif\" alt=\"BridgeMail Systems\"><br>&nbsp;</td></tr>\n</table>\n\n<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"90%\" align=\"center\" id=\"mainWrapper\">\n<tr><td id=\"mainContentCell\" align=\"left\" style=\"font-family:arial;font-size:16px;font-weight: bold;\">\n<img src=\"");
			body.append(webServerURL+"img/newui/single-subscriber_40x40.png\">&nbsp;&nbsp;\n "+accType+"</td></tr>\n<tr><td width=100%><hr style=\"color: #666699; height: 5px;\"/>&nbsp;</td></tr>\n</table>\n\n\n\n<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"90%\" align=\"center\" id=\"mainWrapper\" style=\"font-family:Arial, Helvetica, sans-serif; font-size:11px;\">\n<tr><td colspan=2 style=\"color:BLUE;\" align=left>");
			body.append("<b>MakesBridge Requested User ID:  "+bean.getUserID()+"</b><br><br>");

			body.append("\n\n");
			body.append("<tr><td colspan=2>");
			body.append(accMsg);
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

	private static Vector getTrialAccountUserRoles() {
		Vector roles =  new Vector();
		try {
			RoleDataBean[] allRoles = dataServer.getAllRoles();
			if(allRoles!=null && allRoles.length>0){
				for (int i = 0; i < allRoles.length; i++) {
					roles.add(allRoles[i]);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
		}
		return roles;
	}


	/**
	 * Creating app subscriptions for Trial account (15 days)
	 * @param bean
	 */
	private static void createTrialAccountAppSubscriptions(TrialAccountDataBean bean){
		if(bean == null)
			return;
		try{
			int expiryInDays = 30;
			Vector subsVec = new Vector();
			// Creating subscription for BridgeMail System
			UserAppSubsBean sub1Bean = new UserAppSubsBean();
			sub1Bean.setUserId(bean.getUserID());
			sub1Bean.setAppId(ApplicationManager.APPL_ID_BRIDGEMAILSYSTEM);
			sub1Bean.setType("T");
			sub1Bean.setStatus("A");
			//setting 15 days expiry date
			long expiryTime = Default.removeHourPart(System.currentTimeMillis()+(expiryInDays*24*60*60*1000l));
			sub1Bean.setExpirationTime(new java.util.Date(expiryTime));
			subsVec.add(sub1Bean);

			// Creating subscription for SAM
			UserAppSubsBean sub2Bean = new UserAppSubsBean();
			sub2Bean.setUserId(bean.getUserID());
			sub2Bean.setAppId(ApplicationManager.APPL_ID_SAM);
			sub2Bean.setType("T");
			sub2Bean.setStatus("A");
			sub2Bean.setExpirationTime(new java.util.Date(expiryTime));
			subsVec.add(sub2Bean);
			//creating both app subscriptions
			ApplicationManager.createUserAppSubscriptions(subsVec);

		} catch(Exception ex){
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
		}
	}

	/**
	 *
	 * @param trialId
	 * @return
	 */
	public static boolean markTrialAccountInActive(int trialId){
		if(trialId<1)
			return false;
		try{
			return alphaQueryServer.markTrialAccountInActive(trialId);
		} catch(Exception ex){
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
		}
		return false;
	}

	/**
	 *
	 * @param ip
	 * @return
	 */
	public static int getTrialAccountIPCount(String ip){
		if(Default.toDefault(ip).equals(""))
			return 0;
		try{
			return alphaQueryServer.getTrialAccountIPCount(ip);
		} catch(Exception ex){
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
		}
		return 0;
	}

	/**
	 *
	 * @param email
	 * @return
	 */
	public static boolean isValidEmailDomain(String email){
		if(Default.toDefault(email).equals(""))
			return false;

		try{
			Vector blackListedDomains = reportServer.getBadDomains(); 
			//traversing blacklisted domains
			for(int i=0; i< blackListedDomains.size();i++){
				if(email.toLowerCase().endsWith("@"+(String)blackListedDomains.get(i)))
					return false;
			}
		} catch(Exception ex){
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
		}
		return true;
	}

	/**
	 * 
	 * @param domain
	 * @return
	 */
	public static boolean addBadDomain(String domain){
		if(Default.toDefault(domain).equals(""))
			return false;
		try{			
			return reportServer.addBadDomain(domain);
		} catch(Exception ex){
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
		}
		return false;
	}

	/**
	 *
	 * @param domain
	 * @return
	 */
	public static boolean deleteBadDomain(String domain){
		if(Default.toDefault(domain).equals(""))
			return false;

		try{
			return reportServer.deleteBadDomain(domain);
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
	private static long createTrialCustomer(TrialAccountDataBean bean){

		if(bean == null)
			return 0;
		try{
			CustomerDataBean customerDataBean = new CustomerDataBean();

			customerDataBean.setEmail(bean.getEmail());
			customerDataBean.setContactPersonName(bean.getFirstName());
			customerDataBean.setCustomerName(bean.getCompany());
			customerDataBean.setContactPersonTitle(bean.getTitle());
			customerDataBean.setLogo(bean.getLogo());
			customerDataBean.setUrl(bean.getUrl());
			customerDataBean.setCity(bean.getCity());
			customerDataBean.setCountryCode(bean.getCountryCode());
			customerDataBean.setMobile(bean.getMobile());
			customerDataBean.setPhone(bean.getPhone());
			customerDataBean.setStateCode(bean.getStateCode());
			customerDataBean.setZip(bean.getZip());
			customerDataBean.setAddressLine1(bean.getAddressLine1());
			customerDataBean.setaddressLine2(bean.getAddressLine2());

			//inserting into Customer table
			customerDataBean = dataServer.createCustomer(customerDataBean);
			if(customerDataBean == null) {
				System.out.println("[/TrialAccountManager/createTrialCustomer]: Unable to create Customer: "+bean.getCompany());
				return 0;
			}
			return customerDataBean.getCustomerNumber();
		} catch(Exception ex){
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
		}
		return 0;
	}


	/**
	 *
	 * @param bean
	 * @param customerNumber
	 * @param userKey
	 * @return
	 */
	private static boolean createTrialUser(TrialAccountDataBean bean, long customerNumber, String userKey){

		if(bean == null || customerNumber<1)
			return false;
		try{
			//inserting into User table
			UserDataBean userBean = new UserDataBean();
			userBean.setUserID(bean.getUserID());
			userBean.setUserKey(userKey);

			userBean.setCustomerNumber(new Long(customerNumber));
			userBean.setFirstName(bean.getFirstName());
			userBean.setLastName(bean.getLastName());
			userBean.setPhone(bean.getPhone());
			userBean.setAddressLine1(bean.getAddressLine1());
			userBean.setAddressLine2(bean.getAddressLine2());
			userBean.setEmail(bean.getEmail());
			userBean.setUrl(bean.getUrl());
			userBean.setUserIDCreated(bean.getUserID());
			userBean.setCreationDate(new Timestamp(System.currentTimeMillis()));
			userBean.setAlertEmail(bean.getEmail());
			userBean.setSenderName(bean.getCompany());
			userBean.setWebAddress(bean.getUrl());

			userBean.setUserRole("A");
			//== Email Category [assigning BAD by default]
			userBean.setEmailCategory(PMSDefinitions.EMAIL_CATEGORY_TRIAL);
			userBean.setLocked(PMSDefinitions.LOCK_FALSE);

			userBean.setFromEmail("bms@bridgemailsystem.com");
			userBean.setReplyToAddress("bms@bridgemailsystem.com");
			userBean.setPopHost("192.168.7.4");
			userBean.setPopPort((new Long("110")));
			userBean.setEmailPassword("bms");
			userBean.setEmailLogin("bms");

			userBean.setWorkflowAccess("N");
			userBean.setSFDataSynchAccess("Y");
			userBean.setSickAccess("N");
			userBean.setCustomCampaignFooter("");
			userBean.setIsSupressShared("N");

			//== web site tracking
			userBean.setWebTrack("Y");
			userBean.setWebAddress(userKey+".bridgemailsystem.com");

			//set subscriber's limit & email sent limit
			userBean.setPackageType(PMSDefinitions.PACKAGE_TYPE_TRIAL);
			userBean.setSubscriberLimit(SUBSCRIBER_LIMIT);
			userBean.setEmailSentLimit(EMAIL_SENT_LIMIT);

			System.out.println("[/TrialAccountManager/createTrialUser]: Creating User: "+userBean.getUserID()+" ==");
			//setting Trial account user roles
			userBean.setRoles(TrialAccountManager.getTrialAccountUserRoles());

			//=== Create User. ===
			if(dataServer.createUser(userBean)!=null){
				//=== Create Customer to Data Mapping. ===
				String mapId = dataServer.createUserDataMapping((int)userBean.getCustomerNumber().longValue(), bean.getUserID());

				//=== Create Trial Account User's Authentication details. ===
				alphaQueryServer.createTrialAccountUserAuth(bean.getUserID(), bean.getPassword());

				//=== Create Trial Account App Subscription
				createTrialAccountAppSubscriptions(bean);

				//Create Suppress List
				ListDataBean listDataBean = loadSuppressListData(bean);
				dataServer.createList(listDataBean);
				return true;
			}
		} catch(Exception ex){
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
			System.out.println("[/TrialAccountManager/createTrialUser]: Unable to create User: "+bean.getUserID());
		}
		return false;
	}


	/**
	 *
	 * @param bean
	 * @return
	 */
	private static ListDataBean loadSuppressListData(TrialAccountDataBean bean){

		try{
			if(bean == null)
				return null;
			ListDataBean listDataBean = new ListDataBean();
			listDataBean.setUserID(bean.getUserID());
			listDataBean.setName(PMSDefinitions.SUPRESS_LIST_NAME+bean.getUserID());
			listDataBean.setStatus(PMSDefinitions.LIST_STATUS_ACTIVE);
			listDataBean.setIsAutoReply(null);
			listDataBean.setAutoReplyMessage(null);
			listDataBean.setUserIDCreated(bean.getUserID());
			listDataBean.setCreationDate(new Timestamp(System.currentTimeMillis()));
			listDataBean.setUserIDUpdated(null);
			listDataBean.setUpdationDate(null);
			listDataBean.setLocked(PMSDefinitions.LOCK_FALSE);
			return listDataBean;
		} catch(Exception ex){
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
			System.out.println("[/TrialAccountManager/loadSuppressListData]: Unable to suppressListData: "+bean.getUserID());
		}
		return null;
	}


	/**
	 *
	 * @param bean
	 * @return
	 */
	public static SubscriberInfo loadSubscriberInfoData(TrialAccountDataBean bean){
		if(bean == null)
			return null;

		SubscriberInfo sInfo = new SubscriberInfo();

		sInfo.setEmail(bean.getEmail());
		sInfo.setFirstName(bean.getFirstName());
		sInfo.setLastName(bean.getLastName());
		sInfo.setCity(bean.getCity());
		sInfo.setAddressLine1(bean.getAddressLine1());
		sInfo.setAddressLine2(bean.getAddressLine2());
		sInfo.setCompany(bean.getCompany());
		sInfo.setCountryCode(bean.getCountryCode());
		sInfo.setTelephone(bean.getPhone());
		sInfo.setStateCode(bean.getStateCode());
		sInfo.setTitle(bean.getTitle());
		sInfo.setZip(bean.getZip());
		sInfo.setSource(bean.getSource());
		//setting custom fields
		Vector customFldVec = new Vector();
		Vector beanDataVec = bean.getDataVec();
		for(int i=0;i< beanDataVec.size();i++){
			NameValue nv = (NameValue) beanDataVec.get(i);
			customFldVec.add(new SubscriberDetail(nv.getName(),nv.getValue()));
		}
		sInfo.setCustomFields(customFldVec);

		//hardcoding to subscribe Customer in jayadams's list [30 Day]
		sInfo.setUserID(TRIAL_SUBSCRIPTION_USERID);
		sInfo.setListNumber(TRIAL_SUBSCRIPTION_LIST_NUMBER);

		return sInfo;
	}


	/**
	 *
	 * @param body
	 */
	private static void getDataComOfferEmailBody(StringBuffer body,TrialAccountDataBean bean, String activationURL){

		body.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n");
		body.append("<html>\r\n");
		body.append("   <head>\r\n");
		body.append("      <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n");
		body.append("      <title>MakesBridge Account Activation</title>\r\n");
		body.append("   </head>\r\n");
		body.append("   <body bgcolor=\"#f4f4f4\">\r\n");
		body.append("<table style=\"border-collapse:collapse; border:0;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding:20px 20px 22px 20px;\" bgcolor=\"#f4f4f4\"><!-- START#Header -->\r\n");
		body.append("<table style=\"border-collapse:collapse; text-align:left; font-family:Arial, Helvetica, sans-serif; font-weight:normal; font-size:12px; line-height:15pt; color:#999999; margin:0 auto;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"622\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"font-size:2px; line-height:0px;\" width=\"622\"><img src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/border_top.gif\" style=\"display:block;\" align=\"left\" border=\"0\" height=\"5\" hspace=\"0\" vspace=\"0\" width=\"622\"></td>\r\n");
		body.append("</tr>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding:0 1px;\" bgcolor=\"#e4e4e4\">\r\n");
		body.append("<table style=\"border-collapse:collapse; text-align:left; font-family:Arial, Helvetica, sans-serif; font-weight:normal; font-size:12px; line-height:15pt; color:#999999; margin:0 auto;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"620\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding:0; font-family:Arial, Helvetica, sans-serif; font-size:12px; line-height:15pt; color:#999999;\" bgcolor=\"#FFFFFF\">\r\n");
		body.append("<table style=\"border-collapse:collapse; text-align:left; font-family:Arial, Helvetica, sans-serif; font-weight:normal; font-size:12px; line-height:15pt; color:#999999;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"620\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding-top:17px; padding-bottom:17px; padding-left:20px; font-family:Arial, Helvetica, sans-serif; font-size:12px; line-height:15pt; color:#999999; text-align:left;\" width=\"360\"><a href=\"http://www.makesbridge.com/\" target=\"_blank\"><img alt=\"Linkedin\" src=\"http://www.immediatemarketingautomation.com/wp-content/themes/makesbridge/images1/logo.png\" border=\"0\" height=\"57\" hspace=\"0\" vspace=\"0\" width=\"283\"></a></td>\r\n");
		body.append("<td style=\"padding-top:18px; padding-right:20px; padding-bottom:10px; padding-left:20px; font-family:Arial, Helvetica, sans-serif; font-size:12px; line-height:15pt; color:#999999; text-align:right;\" width=\"200\"><a href=\"https://twitter.com/makesbridge\" target=\"_blank\"><img alt=\"Twitter\" src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/twitter.jpg\" border=\"0\" height=\"32\" hspace=\"0\" vspace=\"0\" width=\"32\"></a>&nbsp;&nbsp;                                  <a href=\"https://www.facebook.com/login.php?next=http%3A%2F%2Fwww.facebook.com%2Fsharer%2Fsharer.php%3Fu%3Dhttp%253A%252F%252Fwww.facebook.com%252Fpages%252FMakesBridge%252F120716081348355&amp;display=popup\" target=\"_blank\"><img alt=\"Facebook\" src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/facebook.jpg\" border=\"0\" height=\"32\" hspace=\"0\" vspace=\"0\" width=\"32\"></a>&nbsp;&nbsp;                                  <a href=\"https://www.youtube.com/user/makesbridge\"><img alt=\"Pinterest\" src=\"http://mail.bridgemailsystem.com/pms/graphics/jayadams/Youtube.png\" border=\"0\" height=\"32\" hspace=\"0\" vspace=\"0\" width=\"32\"></a>&nbsp;&nbsp;                                  <a href=\"http://www.linkedin.com/company/makesbridge-technology\" target=\"_blank\"><img alt=\"Linkedin\" src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/linkedin.jpg\" border=\"0\" height=\"32\" hspace=\"0\" vspace=\"0\" width=\"32\"></a></td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"font-size:2px; line-height:0px;\" width=\"622\"><img src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/border_bottom.gif\" style=\"display:block;\" align=\"left\" border=\"0\" height=\"5\" hspace=\"0\" vspace=\"0\" width=\"622\"></td>\r\n");
		body.append("</tr>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding:1px 22px 2px 0; font-family:Arial, Helvetica, sans-serif; font-size:12px; line-height:15pt; color:#999999; text-align:center;\">\r\n");
		body.append("<h2 style=\"padding:0; font-family:Arial, Helvetica, sans-serif; font-size:18px; line-height:18pt; color:#99cc66; font-weight:bold; margin-top:15px !important; margin-bottom:10px !important;\"><span style=\"font-size: 20px;\">MakesBridge Account Activation</span><br></h2>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("<!-- END#header --> <!-- START#Message -->\r\n");
		body.append("<table style=\"border-collapse:collapse; text-align:left; font-family:Arial, Helvetica, sans-serif; font-weight:normal; font-size:12px; line-height:15pt; color:#999999; margin:0 auto;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"622\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"font-size:2px; line-height:0px;\" width=\"622\"><img src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/border_top.gif\" style=\"display:block;\" align=\"left\" border=\"0\" height=\"5\" hspace=\"0\" vspace=\"0\" width=\"622\"></td>\r\n");
		body.append("</tr>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding:0 1px;\" bgcolor=\"#e4e4e4\">\r\n");
		body.append("<table style=\"border-collapse:collapse; text-align:left; font-family:Arial, Helvetica, sans-serif; font-weight:normal; font-size:12px; line-height:15pt; color:#999999; margin:0 auto;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"620\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding:0; font-family:Arial, Helvetica, sans-serif; font-size:12px; line-height:15pt; color:#999999;\" bgcolor=\"#FFFFFF\">\r\n");
		body.append("<table style=\"border-collapse:collapse; text-align:left; font-family:Arial, Helvetica, sans-serif; font-weight:normal; font-size:12px; line-height:15pt; color:#999999;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"620\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding-top:17px; padding-bottom:17px; padding-left:20px; font-family:Arial, Helvetica, sans-serif; font-size:12px; line-height:15pt; color:#999999; text-align:left;\" width=\"620\"><span style=\"font-size: 18px;\">MakesBridge Requested User ID: "+bean.getUserID()+"</span><br><br><span style=\"font-size: 14px;\">Congratulations <strong>"+bean.getFirstName()+"</strong> your account has been created successfully at MakesBridge! <br><br>Please click the url below to activate and log into your MakesBridge account. <br><a href="+activationURL+">"+activationURL+"</a></span><br><br><span style=\"font-size: 18px;\">Get credited $125.00/month for using Data.com!</span><br><span style=\"font-size: 14px;\"><br>All you need to do is email your <a target=\"_blank\" href=\"http://salesforceoffer.makesbridge.com/order-form/\">Data.com Order Form</a> to support@makesbridge.com. <br><br>MakesBridge will credit your account $125.00 every month during the term of your Data.com account. To be eligible for this credit your MKS Enterprise subscription and Data.com account must be in good standing. </span><br><br><span style=\"font-size: 18px;\">Got Questions?</span><br><span style=\"font-size: 14px;\"><br>Email sales@makesbridge.com, <a target=\"_blank\" href=\"https://server.iad.liveperson.net/hc/69791877/?cmd=file&amp;file=visitorWantsToChat&amp;site=69791877&amp;byhref=1&amp;imageUrl=https://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/English\">chat with a representative</a> or call 408-740-8224.</span><br></td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"font-size:2px; line-height:0px; padding-bottom: 25px ! important;\" width=\"622\"><img src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/border_bottom.gif\" style=\"display:block;\" align=\"left\" border=\"0\" height=\"5\" hspace=\"0\" vspace=\"0\" width=\"622\"></td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("<!-- END#Message --> <!-- START#Section -->\r\n");
		body.append("<table style=\"border-collapse:collapse; text-align:left; font-family:Arial, Helvetica, sans-serif; font-weight:normal; font-size:12px; line-height:15pt; color:#999999; margin:0 auto;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"622\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"font-size:2px; line-height:0px;\" width=\"622\"><img src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/border_top.gif\" style=\"display:block;\" align=\"left\" border=\"0\" height=\"5\" hspace=\"0\" vspace=\"0\" width=\"622\"></td>\r\n");
		body.append("</tr>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding:0 1px;\" bgcolor=\"#e4e4e4\">\r\n");
		body.append("<table style=\"border-collapse:collapse; text-align:left; font-family:Arial, Helvetica, sans-serif; font-weight:normal; font-size:12px; line-height:15pt; color:#999999;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"620\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding-top:17px; padding-right:20px; padding-bottom:17px; padding-left:20px; font-family:Arial, Helvetica, sans-serif; font-size:12px; line-height:15pt; color:#999999;\" bgcolor=\"#FFFFFF\">\r\n");
		body.append("<h2 style=\"padding: 0px 0px 13px 0px; font-family:Arial, Helvetica, sans-serif; font-size:20px; line-height:15pt; color:#666699; font-weight:bold; margin-top:0; margin-bottom:10px !important; border-bottom: 1px solid #e4e4e4;\">How to videos section</h2>\r\n");
		body.append("<table style=\"border-collapse:collapse; border-width:0;\" cellpadding=\"0\" cellspacing=\"0\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding: 7px 10px 0px 0px;\" valign=\"top\" width=\"142\"><a href=\"http://www.makesbridge.com/index.php?option=com_content&amp;view=article&amp;id=286\" target=\"_blank\"><img alt=\"videos section\" src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/MakesBridge_vd1.png\" style=\"display:block; padding-bottom: 17px;\" align=\"right\" border=\"0\" height=\"77\" hspace=\"0\" vspace=\"0\" width=\"132\"></a>\r\n");
		body.append("<p style=\"text-align: center; font-size: 12px; font-weight: bold; color: #666699;\">Get started video</p>\r\n");
		body.append("</td>\r\n");
		body.append("<td style=\"padding: 7px 10px 0px 7px;\" valign=\"top\" width=\"142\"><a href=\"http://www.makesbridge.com/video-demos-a-tutorials/upload-a-list\" target=\"_blank\"><img alt=\"videos section\" src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/MakesBridge_vd2.png\" style=\"display:block; padding-bottom: 17px;\" align=\"right\" border=\"0\" height=\"77\" hspace=\"0\" vspace=\"0\" width=\"132\"></a>\r\n");
		body.append("<p style=\"text-align: center; font-size: 12px; font-weight: bold; color: #666699;\">Upload a list <br></p>\r\n");
		body.append("</td>\r\n");
		body.append("<td style=\"padding: 7px 10px 0px 7px;\" valign=\"top\" width=\"142\"><a href=\"http://www.makesbridge.com/video-demos-a-tutorials/importing-from-salesforce\" target=\"_blank\"><img alt=\"videos section\" src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/MakesBridge_vd3.png\" style=\"display:block; padding-bottom: 17px;\" align=\"right\" border=\"0\" height=\"77\" hspace=\"0\" vspace=\"0\" width=\"132\"></a>\r\n");
		body.append("<p style=\"text-align: center; font-size: 12px; font-weight: bold; color: #666699;\">Import from Salesforce</p>\r\n");
		body.append("</td>\r\n");
		body.append("<td style=\"padding: 7px 0px 0px 7px;\" valign=\"top\" width=\"142\"><a href=\"http://www.makesbridge.com/index.php?option=com_content&amp;view=article&amp;id=103&amp;Itemid=125\" target=\"_blank\"><img alt=\"videos section\" src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/MakesBridge_vd4.png\" style=\"display:block; padding-bottom: 17px;\" align=\"right\" border=\"0\" height=\"77\" hspace=\"0\" vspace=\"0\" width=\"132\"></a>\r\n");
		body.append("<p style=\"text-align: center; font-size: 12px; font-weight: bold; color: #666699;\">Create campaign</p>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding-top:17px; padding-right:20px; padding-bottom:17px; padding-left:20px; font-family:Arial, Helvetica, sans-serif; font-size:12px; line-height:15pt; color:#999999;\" bgcolor=\"#FFFFFF\">\r\n");
		body.append("<h2 style=\"padding: 0px 0px 13px 0px; font-family:Arial, Helvetica, sans-serif; font-size:20px; line-height:15pt; color:#666699; font-weight:bold; margin-top:0; margin-bottom:10px !important; border-bottom: 1px solid #e4e4e4;\">Get help! Support information</h2>\r\n");
		body.append("<table style=\"border-collapse:collapse; border-width:0;\" cellpadding=\"0\" cellspacing=\"0\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding: 7px 10px 0px 0px;\" align=\"center\" valign=\"top\" width=\"177\"><a href=\"https://server.iad.liveperson.net/hc/69791877/?cmd=file&amp;file=visitorWantsToChat&amp;site=69791877&amp;byhref=1&amp;imageUrl=https://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/English\" target=\"_blank\"><img alt=\"videos section\" src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/MakesBridge_info_chat.png\" style=\"display:block; padding-bottom: 3px;\" align=\"top\" border=\"0\" hspace=\"0\" vspace=\"0\"></a>\r\n");
		body.append("<p style=\"text-align: center; font-size: 17px; font-weight: bold; color: #666699;\">Chat</p>\r\n");
		body.append("</td>\r\n");
		body.append("<td style=\"padding: 7px 10px 0px 7px;\" align=\"center\" valign=\"top\" width=\"177\"><a target=\"_blank\" href=\"http://server.iad.liveperson.net/hc/s-69791877/cmd/kbresource/kb-7276422033049075588/front_page!PAGETYPE?category=-1&amp;product=-1\"><img alt=\"videos section\" src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/MakesBridge_knowledge_base.png\" style=\"display:block; padding-bottom: 3px;\" align=\"top\" border=\"0\" hspace=\"0\" vspace=\"0\"></a>\r\n");
		body.append("<p style=\"text-align: center; font-size: 17px; font-weight: bold; color: #666699;\">Knowledge Base</p>\r\n");
		body.append("</td>\r\n");
		body.append("<td style=\"padding: 7px 10px 0px 7px;\" align=\"center\" valign=\"top\" width=\"177\"><a href=\"mailto:support@makesbridge.com\" target=\"_blank\"><img alt=\"videos section\" src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/MakesBridge_%20Support_email.png\" style=\"display:block; padding-bottom: 3px;\" align=\"top\" border=\"0\" hspace=\"0\" vspace=\"0\"></a>\r\n");
		body.append("<p style=\"text-align: center; font-size: 17px; font-weight: bold; color: #666699;\">Support Email</p>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"font-size:2px; line-height:0px; padding-bottom: 25px ! important;\" width=\"622\"><img src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/border_bottom.gif\" style=\"display:block;\" align=\"left\" border=\"0\" height=\"5\" hspace=\"0\" vspace=\"0\" width=\"622\"></td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("<!-- END#section --> <!-- START#Modules -->\r\n");
		body.append("<table style=\"border-collapse:collapse; text-align:left; font-family:Arial, Helvetica, sans-serif; font-weight:normal; font-size:12px; line-height:15pt; color:#999999; margin:0 auto;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"622\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"font-size:2px; line-height:0px;\" width=\"622\"><img src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/border_top.gif\" style=\"display:block;\" align=\"left\" border=\"0\" height=\"5\" hspace=\"0\" vspace=\"0\" width=\"622\"></td>\r\n");
		body.append("</tr>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding:0 1px;\" bgcolor=\"#e4e4e4\">\r\n");
		body.append("<table style=\"border-collapse:collapse; text-align:left; font-family:Arial, Helvetica, sans-serif; font-weight:normal; font-size:12px; line-height:15pt; color:#999999;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"620\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding-top:17px; padding-right:20px; padding-bottom:17px; padding-left:20px; font-family:Arial, Helvetica, sans-serif; font-size:12px; line-height:15pt; color:#999999;\" bgcolor=\"#FFFFFF\">\r\n");
		body.append("<h2 style=\"padding: 0px 0px 13px 0px; font-family:Arial, Helvetica, sans-serif; font-size:20px; line-height:15pt; color:#666699; font-weight:bold; margin-top:0; margin-bottom:10px !important; border-bottom: 1px solid #e4e4e4;\">Start marketing with pre-designed marketing templates!<br></h2>\r\n");
		body.append("<table style=\"border-collapse:collapse; border-width:0;\" cellpadding=\"0\" cellspacing=\"0\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding: 7px 4px 0px 0px;\" valign=\"top\" width=\"142\"><a href=\"http://www.makesbridge.com/simplify/push-playtm-modules\" target=\"_blank\"><img alt=\"videos section\" src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/MakesBridge_ignite.png\" style=\"display:block; padding-bottom: 17px;\" align=\"right\" border=\"0\" height=\"139\" hspace=\"0\" vspace=\"0\" width=\"139\"></a></td>\r\n");
		body.append("<td style=\"padding: 7px 4px 0px 3px;\" valign=\"top\" width=\"142\"><a href=\"http://www.makesbridge.com/simplify/push-playtm-modules\" target=\"_blank\"><img alt=\"videos section\" src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/MakesBridge_response_booster.png\" style=\"display:block; padding-bottom: 17px;\" align=\"right\" border=\"0\" height=\"139\" hspace=\"0\" vspace=\"0\" width=\"139\"></a></td>\r\n");
		body.append("<td style=\"padding: 7px 4px 0px 3px;\" valign=\"top\" width=\"142\"><a href=\"http://www.makesbridge.com/simplify/push-playtm-modules\" target=\"_blank\"><img alt=\"videos section\" src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/MakesBridge_lemonade.png\" style=\"display:block; padding-bottom: 17px;\" align=\"right\" border=\"0\" height=\"139\" hspace=\"0\" vspace=\"0\" width=\"139\"></a></td>\r\n");
		body.append("<td style=\"padding: 7px 0px 0px 3px;\" valign=\"top\" width=\"142\"><a href=\"http://www.makesbridge.com/simplify/push-playtm-modules\" target=\"_blank\"><img alt=\"videos section\" src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/MakesBridge_pipe_cleaner.png\" style=\"display:block; padding-bottom: 17px;\" align=\"right\" border=\"0\" height=\"139\" hspace=\"0\" vspace=\"0\" width=\"139\"></a></td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"font-size:2px; line-height:0px; padding-bottom: 7px ! important;\" width=\"622\"><img src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/border_bottom.gif\" style=\"display:block;\" align=\"left\" border=\"0\" height=\"5\" hspace=\"0\" vspace=\"0\" width=\"622\"></td>\r\n");
		body.append("</tr>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding:1px 22px 2px 0; font-family:Arial, Helvetica, sans-serif; font-size:12px; line-height:15pt; color:#999999; text-align:center;\">\r\n");
		body.append("<h2 style=\"padding:0; font-family:Arial, Helvetica, sans-serif; font-size:18px; line-height:18pt; color:#666699; font-weight:bold; margin-top:15px !important; margin-bottom:10px !important;\">Choose one of these complimentary marketing automation templates<br></h2>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("<!-- END#Modules --> <!-- START#Packages -->\r\n");
		body.append("<table style=\"border-collapse:collapse; text-align:left; font-family:Arial, Helvetica, sans-serif; font-weight:normal; font-size:12px; line-height:15pt; color:#999999; margin:0 auto;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"622\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td valign=\"top\" width=\"300\">\r\n");
		body.append("<table style=\"border-collapse:collapse; text-align:left; font-family:Arial, Helvetica, sans-serif; font-weight:normal; font-size:12px; line-height:15pt; color:#999999; margin:0 auto;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"300\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"font-size:2px; line-height:0px;\" width=\"300\"><img src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/border_top2.gif\" style=\"display:block;\" align=\"left\" border=\"0\" height=\"5\" hspace=\"0\" vspace=\"0\" width=\"300\"></td>\r\n");
		body.append("</tr>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding:0 1px;\" bgcolor=\"#e4e4e4\">\r\n");
		body.append("<table style=\"border-collapse:collapse; text-align:left; font-family:Arial, Helvetica, sans-serif; font-weight:normal; font-size:12px; line-height:15pt; color:#999999; margin:0 auto;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"298\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding-top:17px; padding-right:20px; padding-bottom:17px; padding-left:20px; font-family:Arial, Helvetica, sans-serif; font-size:12px; line-height:15pt; color:#999999;\" bgcolor=\"#FFFFFF\">\r\n");
		body.append("<h2 style=\"padding:0; font-family:Arial, Helvetica, sans-serif; font-size:16px; line-height:15pt; color:#666699; font-weight:bold; margin-top:0; margin-bottom:10px !important;\">Ignite<br></h2>\r\n");
		body.append("<table style=\"border-collapse:collapse; border-width:0;\" align=\"left\" cellpadding=\"0\" cellspacing=\"0\" width=\"84\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding-top:4px; padding-right:20px;\"><img alt=\"image\" src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/MakesBridge_ignite.png\" style=\"display: block; border: 0; margin: 0px;\" border=\"0\" height=\"83\" hspace=\"0\" vspace=\"0\" width=\"83\"></td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("<strong>Fire up your follow up with automated email and sales alerts</strong><br>You spend a lot of money promoting your business. Ignite ensures maximum return on those investments with a series of 10 triggered emails. Each containing proven copy.\r\n");
		body.append("<table style=\"border-collapse:collapse; border-width:0;\" cellpadding=\"0\" cellspacing=\"0\" width=\"258\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding-top:15px; padding-bottom:3px;\" valign=\"top\" width=\"104\"><a target=\"_blank\" href=\"http://makesbridge.com/simplify/push-playtm-modules\"><img alt=\"Read More\" src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/mks_more.png\" style=\"display:block;\" align=\"right\" border=\"0\" height=\"35\" hspace=\"0\" vspace=\"0\" width=\"86\"></a></td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding:0 0 22px 0; font-size:2px; line-height:0px;\" width=\"300\"><img src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/border_bottom2.gif\" style=\"display:block;\" align=\"left\" border=\"0\" height=\"5\" hspace=\"0\" vspace=\"0\" width=\"300\"></td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</td>\r\n");
		body.append("<td style=\"padding:0 0 0 22px;\" valign=\"top\" width=\"300\">\r\n");
		body.append("<table style=\"border-collapse:collapse; text-align:left; font-family:Arial, Helvetica, sans-serif; font-weight:normal; font-size:12px; line-height:15pt; color:#999999; margin:0 auto;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"300\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"font-size:2px; line-height:0px;\" width=\"300\"><img src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/border_top2.gif\" style=\"display:block;\" align=\"left\" border=\"0\" height=\"5\" hspace=\"0\" vspace=\"0\" width=\"300\"></td>\r\n");
		body.append("</tr>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding:0 1px;\" bgcolor=\"#e4e4e4\">\r\n");
		body.append("<table style=\"border-collapse:collapse; text-align:left; font-family:Arial, Helvetica, sans-serif; font-weight:normal; font-size:12px; line-height:15pt; color:#999999; margin:0 auto;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"298\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding-top:17px; padding-right:20px; padding-bottom:17px; padding-left:20px; font-family:Arial, Helvetica, sans-serif; font-size:12px; line-height:15pt; color:#999999;\" bgcolor=\"#FFFFFF\">\r\n");
		body.append("<h2 style=\"padding:0; font-family:Arial, Helvetica, sans-serif; font-size:16px; line-height:15pt; color:#666699; font-weight:bold; margin-top:0; margin-bottom:10px !important;\">Lemonade<br></h2>\r\n");
		body.append("<table style=\"border-collapse:collapse; border-width:0;\" align=\"left\" cellpadding=\"0\" cellspacing=\"0\" width=\"84\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding-top:4px; padding-right:20px;\"><img alt=\"image\" src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/MakesBridge_response_booster.png\" style=\"display: block; border: 0; margin: 0px;\" border=\"0\" height=\"83\" hspace=\"0\" vspace=\"0\" width=\"83\"></td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("<strong>Squeeze a second chance from lemons of lost opportunity.</strong><br>Prospects come and go. That's business. Push play on this marketing campaign module to ensure ex-prospects call when it's time for them to shop again.\r\n");
		body.append("<table style=\"border-collapse:collapse; border-width:0;\" cellpadding=\"0\" cellspacing=\"0\" width=\"258\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding-top:15px; padding-bottom:3px;\" valign=\"top\" width=\"104\"><a target=\"_blank\" href=\"http://makesbridge.com/simplify/push-playtm-modules\"><img alt=\"Read More\" src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/mks_more.png\" style=\"display:block;\" align=\"right\" border=\"0\" height=\"35\" hspace=\"0\" vspace=\"0\" width=\"86\"></a></td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding:0 0 22px 0; font-size:2px; line-height:0px;\" width=\"300\"><img src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/border_bottom2.gif\" style=\"display:block;\" align=\"left\" border=\"0\" height=\"5\" hspace=\"0\" vspace=\"0\" width=\"300\"></td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("<!-- END#Packages --> <!-- START#Packages -->\r\n");
		body.append("<table style=\"border-collapse:collapse; text-align:left; font-family:Arial, Helvetica, sans-serif; font-weight:normal; font-size:12px; line-height:15pt; color:#999999; margin:0 auto;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"622\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td valign=\"top\" width=\"300\">\r\n");
		body.append("<table style=\"border-collapse:collapse; text-align:left; font-family:Arial, Helvetica, sans-serif; font-weight:normal; font-size:12px; line-height:15pt; color:#999999; margin:0 auto;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"300\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"font-size:2px; line-height:0px;\" width=\"300\"><img src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/border_top2.gif\" style=\"display:block;\" align=\"left\" border=\"0\" height=\"5\" hspace=\"0\" vspace=\"0\" width=\"300\"></td>\r\n");
		body.append("</tr>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding:0 1px;\" bgcolor=\"#e4e4e4\">\r\n");
		body.append("<table style=\"border-collapse:collapse; text-align:left; font-family:Arial, Helvetica, sans-serif; font-weight:normal; font-size:12px; line-height:15pt; color:#999999; margin:0 auto;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"298\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding-top:17px; padding-right:20px; padding-bottom:17px; padding-left:20px; font-family:Arial, Helvetica, sans-serif; font-size:12px; line-height:15pt; color:#999999;\" bgcolor=\"#FFFFFF\">\r\n");
		body.append("<h2 style=\"padding:0; font-family:Arial, Helvetica, sans-serif; font-size:16px; line-height:15pt; color:#666699; font-weight:bold; margin-top:0; margin-bottom:10px !important;\">Response Booster<br></h2>\r\n");
		body.append("<table style=\"border-collapse:collapse; border-width:0;\" align=\"left\" cellpadding=\"0\" cellspacing=\"0\" width=\"84\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding-top:4px; padding-right:20px;\"><img alt=\"image\" src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/MakesBridge_lemonade.png\" style=\"display: block; border: 0; margin: 0px;\" border=\"0\" height=\"83\" hspace=\"0\" vspace=\"0\" width=\"83\"></td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("<strong>Get unresponsive prospects to call you back.</strong><br>Push play to start a series of meeting requests and stop wasting valuable sales time. Response Booster breaks the ice with humor and captures meetings.\r\n");
		body.append("<table style=\"border-collapse:collapse; border-width:0;\" cellpadding=\"0\" cellspacing=\"0\" width=\"258\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding-top:15px; padding-bottom:3px;\" valign=\"top\" width=\"104\"><a target=\"_blank\" href=\"http://makesbridge.com/simplify/push-playtm-modules\"><img alt=\"Read More\" src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/mks_more.png\" style=\"display:block;\" align=\"right\" border=\"0\" height=\"35\" hspace=\"0\" vspace=\"0\" width=\"86\"></a></td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding:0 0 22px 0; font-size:2px; line-height:0px;\" width=\"300\"><img src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/border_bottom2.gif\" style=\"display:block;\" align=\"left\" border=\"0\" height=\"5\" hspace=\"0\" vspace=\"0\" width=\"300\"></td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</td>\r\n");
		body.append("<td style=\"padding:0 0 0 22px;\" valign=\"top\" width=\"300\">\r\n");
		body.append("<table style=\"border-collapse:collapse; text-align:left; font-family:Arial, Helvetica, sans-serif; font-weight:normal; font-size:12px; line-height:15pt; color:#999999; margin:0 auto;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"300\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"font-size:2px; line-height:0px;\" width=\"300\"><img src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/border_top2.gif\" style=\"display:block;\" align=\"left\" border=\"0\" height=\"5\" hspace=\"0\" vspace=\"0\" width=\"300\"></td>\r\n");
		body.append("</tr>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding:0 1px;\" bgcolor=\"#e4e4e4\">\r\n");
		body.append("<table style=\"border-collapse:collapse; text-align:left; font-family:Arial, Helvetica, sans-serif; font-weight:normal; font-size:12px; line-height:15pt; color:#999999; margin:0 auto;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"298\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding-top:17px; padding-right:20px; padding-bottom:17px; padding-left:20px; font-family:Arial, Helvetica, sans-serif; font-size:12px; line-height:15pt; color:#999999;\" bgcolor=\"#FFFFFF\">\r\n");
		body.append("<h2 style=\"padding:0; font-family:Arial, Helvetica, sans-serif; font-size:16px; line-height:15pt; color:#666699; font-weight:bold; margin-top:0; margin-bottom:10px !important;\">Pipe Cleaner<br></h2>\r\n");
		body.append("<table style=\"border-collapse:collapse; border-width:0;\" align=\"left\" cellpadding=\"0\" cellspacing=\"0\" width=\"84\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding-top:4px; padding-right:20px;\"><img alt=\"image\" src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/MakesBridge_pipe_cleaner.png\" style=\"display: block; border: 0; margin: 0px;\" border=\"0\" height=\"83\" hspace=\"0\" vspace=\"0\" width=\"83\"></td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("<strong>Pique and probe prospects to resolve indecision.</strong><br>When discussions stall, use Pipe Cleaner to get things moving again. Pipe Clenaer is a four phase proces that gets prospects off the fence.\r\n");
		body.append("<table style=\"border-collapse:collapse; border-width:0;\" cellpadding=\"0\" cellspacing=\"0\" width=\"258\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding-top:15px; padding-bottom:3px;\" valign=\"top\" width=\"104\"><a target=\"_blank\" href=\"http://makesbridge.com/simplify/push-playtm-modules\"><img alt=\"Read More\" src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/mks_more.png\" style=\"display:block;\" align=\"right\" border=\"0\" height=\"35\" hspace=\"0\" vspace=\"0\" width=\"86\"></a></td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding:0 0 22px 0; font-size:2px; line-height:0px;\" width=\"300\"><img src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/border_bottom2.gif\" style=\"display:block;\" align=\"left\" border=\"0\" height=\"5\" hspace=\"0\" vspace=\"0\" width=\"300\"></td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("<!-- END#Packages --> <!-- START#Footer -->\r\n");
		body.append("<table style=\"border-collapse:collapse; text-align:left; font-family:Arial, Helvetica, sans-serif; font-weight:normal; font-size:12px; line-height:15pt; color:#999999; margin:0 auto;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"622\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"font-size:2px; line-height:0px;\" width=\"622\"><img src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/border_top.gif\" style=\"display:block;\" align=\"left\" border=\"0\" height=\"5\" hspace=\"0\" vspace=\"0\" width=\"622\"></td>\r\n");
		body.append("</tr>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding:0 1px;\" bgcolor=\"#e4e4e4\">\r\n");
		body.append("<table style=\"border-collapse:collapse; text-align:left; font-family:Arial, Helvetica, sans-serif; font-weight:normal; font-size:12px; line-height:15pt; color:#999999; margin:0 auto;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"620\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding:0; font-family:Arial, Helvetica, sans-serif; font-size:12px; line-height:15pt; color:#999999;\" bgcolor=\"#FFFFFF\">\r\n");
		body.append("<table style=\"border-collapse:collapse; text-align:left; font-family:Arial, Helvetica, sans-serif; font-weight:normal; font-size:12px; line-height:15pt; color:#999999;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"620\">\r\n");
		body.append("<tbody>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"padding-top:17px; padding-bottom:17px; padding-left:20px; font-weight: bold; font-family:Arial, Helvetica, sans-serif; font-size:12px; line-height:15pt; color:#999999; text-align:center;\" width=\"620\">"+PMSDefinitions.COPYRIGHT_STATEMENT+"<br></td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</td>\r\n");
		body.append("</tr>\r\n");
		body.append("<tr>\r\n");
		body.append("<td style=\"font-size:2px; line-height:0px; padding-bottom: 25px ! important;\" width=\"622\"><img src=\"http://d227d4nygip7u.cloudfront.net/pms/graphics/makesbridge/border_bottom.gif\" style=\"display:block;\" align=\"left\" border=\"0\" height=\"5\" hspace=\"0\" vspace=\"0\" width=\"622\"></td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("<!-- END#Footer --></td>\r\n");
		body.append("</tr>\r\n");
		body.append("</tbody>\r\n");
		body.append("</table>\r\n");
		body.append("</body>\r\n");
		body.append("</html>");

	}
}


