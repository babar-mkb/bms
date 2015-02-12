package com.PMSystems;
/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: Makesbridge Technology.</p>
 * @author Babar Ali Virk
 * @version 1.0
 */

import com.PMSystems.dbbeans.*;
import com.PMSystems.logger.*;
import com.PMSystems.sejbs.*;
import com.PMSystems.util.*;
import com.PMSystems.SFIntegration.*;
import com.PMSystems.api.*;

import java.util.*;
import java.sql.*;
import com.PMSystems.mail.*;
import com.PMSystems.mail.smtp.*;



public class SynchronizationManager {

	public static final String HR_CLIENT = "HR_CLIENT";

	private static DataQueryServer dataServer;
	private static BMSQueryServer bmsServer;
	private static ReportsQueryServer reportServer;

	static{
		dataServer = EJBHomesFactory.getDataQueryServerRemote();
		bmsServer = EJBHomesFactory.getBMSQueryServerRemote();
		reportServer = EJBHomesFactory.getReportsQueryServerRemote();
	}

	public SynchronizationManager() {
	}

	public static boolean createCredentials(SynchCredentialsDataBean synchDataBean){
		try{
			if(dataServer.createSynchCredentials(synchDataBean)>0)
				return true;
		}catch(Exception ex){
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
		}
		return false;
	}

	public static SynchCredentialsDataBean getCredentials(String userID, String synchPartner){
		try{
			return dataServer.getSynchCredentials(userID, synchPartner);
		}catch(Exception ex){
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
		}
		return null;
	}
	public static SynchCredentialsDataBean getSynchoronizionCredentials(String synchUser,String synchAccountID, String synchPartner){
		try{
			return dataServer.loadSynchronizationCredentials(synchUser, synchAccountID, synchPartner);
		}catch(Exception ex){
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
		}
		return null;
	}

	public static boolean updateCredentials(SynchCredentialsDataBean synchCredential) {
		try {
			dataServer.updateSynchCredentials(synchCredential);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
		}
		return false;
	}
	public static boolean updateConfirmationEmail(String userId, String partner, String email) {
		try {
			return dataServer.updateSynchCredentialEmail(userId, partner, email);
		} catch (Exception ex) {
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
		}
		return false;
	}

	public static long createScheduledTransaction(ScheduledTransactionDataBean schTranDataBean) {
		try {
			System.out.println("going to create transaction thrugh synch manager");
			return dataServer.createScheduledTransaction(schTranDataBean);
		} catch (Exception ex) {
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
		}
		return 0l;
	}

	public static boolean updateScheduledTransaction(ScheduledTransactionDataBean schTranDataBean) {
		try {
			System.out.println("going to update transaction thrugh synch manager");
			return dataServer.updateScheduledTransaction(schTranDataBean);
		} catch (Exception ex) {
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
		}
		return false;
	}

	public static boolean updateSynchAccountStatus(String userId, String partner, boolean status){
		try {
			return dataServer.updateSynchAccountStatus(userId, partner, status);
		} catch (Exception ex) {
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
		}

		return false;
	}

	public static Vector getUserScheduledTransactions(String userId,String synchPartner, String scheduledType) {
		try {
			System.out.println("going to load import/export scheduled transactions");
			if(Default.toDefault(userId).equals("") || Default.toDefault(scheduledType).equals(""))
				return new Vector();
			if(scheduledType.equalsIgnoreCase("import")){
				System.out.println("going to download import transaction data");
				return dataServer.getIncompleteUserImports(userId,synchPartner);
			}else{
				return dataServer.getIncompleteUserExports(userId,synchPartner);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
		}
		return new Vector();
	}

	public static ScheduledTransactionDataBean loadScheduledTransaction(String schNumber) {
		try {
			return loadScheduledTransaction(Default.defaultLong(schNumber));
		} catch (Exception ex) {
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
		}
		return new ScheduledTransactionDataBean();
	}

	public static ScheduledTransactionDataBean loadScheduledTransaction(long schNumber) {
		try {
			if(schNumber == 0)
				return null;
			return (ScheduledTransactionDataBean)dataServer.getScheduledTransaction(schNumber);
		} catch (Exception ex) {
			ex.printStackTrace();
			WebServerLogger.getLogger().log(ex);
		}
		return null;
	}

	/**
	 * 
	 * @param campaignNumber
	 * @return
	 */
	 public static ScheduledTransactionDataBean getScheduledTransactionByCampaignNumber(long campaignNumber) {
		 try {
			 if(campaignNumber <= 0)
				 return null;
			 return (ScheduledTransactionDataBean)dataServer.getScheduledTransactionByCampaignNumber(campaignNumber);
		 } catch (Exception ex) {
			 ex.printStackTrace();
			 WebServerLogger.getLogger().log(ex);
		 }
		 return null;
	 }    


	 /**
	  * 
	  * @param userId
	  * @param synchPartner
	  * @param status
	  * @return
	  */
	 public static Vector getImportSynchTransactions(String userId, String synchPartner, String status) {
		 try {
			 if(Default.toDefault(userId).equals("") || Default.toDefault(synchPartner).equals(""))
				 return new Vector();
			 return dataServer.getImportSynchTransactions(userId, synchPartner, status);
		 } catch (Exception ex) {
			 ex.printStackTrace();
			 WebServerLogger.getLogger().log(ex);
		 }
		 return new Vector();
	 }    


	 public static Vector getReadyTransactions(String status){
		 if(Default.toDefault(status).equals(""))
			 return new Vector();
		 try{
			 return dataServer.getReadyTransactions(status);
		 } catch(Exception ex){
			 ex.printStackTrace();
			 WebServerLogger.getLogger().log(ex);
		 }
		 return new Vector();
	 }

	 public static byte[] getCredentialPass(String bmsUserID,String synchPartner){
		 try{
			 return dataServer.getSyncCredPass(bmsUserID,synchPartner);
		 } catch(Exception ex){
			 ex.printStackTrace();
			 WebServerLogger.getLogger().log(ex);
		 }
		 return null;
	 }

	 //constructs date for one time scenario
	 public static Timestamp constructScheduledDate(String recurDay) {
		 Timestamp scheduledDate = null;
		 Calendar calendar = Calendar.getInstance();
		 calendar.setTimeInMillis(System.currentTimeMillis());
		 StringTokenizer strTok = new StringTokenizer(recurDay,":");
		 String minute = strTok.nextToken();
		 String hour = strTok.nextToken();
		 String date = strTok.nextToken();
		 String month = strTok.nextToken();
		 String year = strTok.nextToken();
		 calendar.set(Calendar.MINUTE,Integer.parseInt(minute));
		 calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(hour));
		 calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(date));
		 calendar.set(Calendar.MONTH,Integer.parseInt(month));
		 calendar.set(Calendar.YEAR,Integer.parseInt(year));
		 scheduledDate = new Timestamp(calendar.getTimeInMillis());
		 return scheduledDate;
	 }

	 //constructs date for recurring scenarios
	 public static Timestamp constructScheduledDate(String recurPeriod, String recurDay) {
		 Timestamp scheduledDate = null;
		 Calendar calendar = Calendar.getInstance();
		 calendar.setTimeInMillis(System.currentTimeMillis());
		 StringTokenizer strTok = new StringTokenizer(recurDay,":");
		 String minute = "";
		 String hour = "";
		 String date = "";
		 if(strTok.hasMoreTokens())
			 minute = strTok.nextToken();
		 if(strTok.hasMoreTokens())
			 hour = strTok.nextToken();
		 if(strTok.hasMoreTokens())
			 date = strTok.nextToken();
		 if(!minute.equals(""))
			 calendar.set(Calendar.MINUTE,Integer.parseInt(minute));
		 if(!hour.equals(""))
			 calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(hour));
		 if(!date.equals(""))
			 calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(date));

		 if(recurPeriod.equalsIgnoreCase("O")) {
			 int dayOfWeek = Integer.parseInt(date);
			 calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
			 if(calendar.getTimeInMillis()<System.currentTimeMillis()) {
				 calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)+7);
			 }
		 } else if(recurPeriod.equalsIgnoreCase("T")) {
			 int dayOfWeek = Integer.parseInt(date);
			 calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
			 if(calendar.getTimeInMillis()<System.currentTimeMillis()) {
				 calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)+14);
			 }
		 } else if(recurPeriod.equalsIgnoreCase("M")) {
			 int dayOfMonth = Integer.parseInt(date);
			 if(dayOfMonth>28) {//if day selected is 29 or 30 set it to 28 for Feb.
				 if(dayOfMonth<calendar.get(Calendar.DAY_OF_MONTH) && calendar.get(Calendar.MONTH)+1==Calendar.FEBRUARY) {
					 calendar.set(Calendar.DAY_OF_MONTH, 28);
				 }
			 }

			 if(calendar.getTimeInMillis()<System.currentTimeMillis()) {
				 calendar.add(Calendar.MONTH,1);
			 }
		 }
		 scheduledDate = new Timestamp(calendar.getTimeInMillis());
		 return scheduledDate;
	 }


	 //== creating new transaction with next scheduledDate
	 public static void scheduleNextRecurTransaction(ScheduledTransactionDataBean schDataBean) {

		 try {
			 schDataBean.setStatus(PMSDefinitions.SCHEDULE_STATUS_SCHEDULED);
			 schDataBean.setToGivenListAdded(0);
			 schDataBean.setToGivenListUpdated(0);
			 schDataBean.setToSupressList(0);
			 schDataBean.setTotalProcessed(0);

			 Calendar calendar = Calendar.getInstance();
			 calendar.setTimeInMillis(schDataBean.getScheduledDate().getTime());

			 if (schDataBean.getRecurPeriod().equals("O")) {
				 Timestamp scheduledDate = schDataBean.getScheduledDate();
				 // need to handle multipleDays case
				 schDataBean = getNextScheduledDateForWeekly(schDataBean);

			 } else if (schDataBean.getRecurPeriod().equals("T")) {
				 calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)+14);
				 schDataBean.setScheduledDate(Default.toTimestamp(calendar.getTime()));

			 } else if (schDataBean.getRecurPeriod().equals("M")) {
				 int dayOfMonth = schDataBean.getRecurDay();
				 if (calendar.get(Calendar.MONTH) + 1 == Calendar.FEBRUARY && dayOfMonth > 28) { //if day selected is 29 or 30 set it to 28 for Feb.
					 calendar.set(Calendar.DAY_OF_MONTH, 28);
				 }
				 calendar.add(Calendar.MONTH,1);

				 schDataBean.setScheduledDate(Default.toTimestamp(calendar.getTime()));
			 }

			 dataServer.createScheduledTransaction(schDataBean);

		 } catch (Exception e) {
			 e.printStackTrace();
			 WebServerLogger.getLogger().log(e);

		 }
	 }

	 public static Vector getSFDateLiterals(){
		 Vector literalVec = new Vector();
		 literalVec.add(new NameValue("TODAY","TODAY"));
		 literalVec.add(new NameValue("YESTERDAY","YESTERDAY"));
		 literalVec.add(new NameValue("LAST 3 DAYS","3 days"));
		 literalVec.add(new NameValue("LAST WEEK","LAST_WEEK"));
		 literalVec.add(new NameValue("LAST 2 WEEK","2 weeks"));
		 literalVec.add(new NameValue("LAST MONTH","LAST_MONTH"));
		 literalVec.add(new NameValue("LAST 2 MONTHS","2 months"));
		 literalVec.add(new NameValue("LAST 90 DAYS","LAST_90_DAYS"));
		 return literalVec;
	 }



	 public static void mkSynchErrorEmailBody(StringBuffer body, ScheduledTransactionDataBean schTransDB,  String errorDetail ) {

		 try {
			 if(schTransDB == null)
				 return;
			 String synchType = schTransDB.getScheduledType().equals("E")?"Export":schTransDB.getScheduledType().equals("P")?"Peer":"Import";
			 String owner = schTransDB.getTableName().equalsIgnoreCase(SalesForceManager.SF_OWNER_TABLENAME)?"Owner ":"";
			 String partner = schTransDB.getSynchPartner().equalsIgnoreCase("Netsuite")?"Netsuite":"Salesforce";

			 String webServerURL = PMSResources.getInstance().getWebServerURL();
			 body.append("\n<html>\n<head>\n<link HREF=\"/pms/pms2.css\" rel=\"STYLESHEET\" TYPE=\"text/css\"/>\n<link HREF=\"/pms/jwb.css\" rel=\"STYLESHEET\" TYPE=\"text/css\"/>\n\n</head>\n<body leftmargin=\"0\" topmargin=\"0\" marginwidth=\"0\" marginheight=\"0\" >\n<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"90%\">\n<tr><td id=\"logo\"><img src=\"");
			 body.append(webServerURL+"img/newui/makesbridge.png\" alt=\"BridgeMail Systems\"><br>&nbsp;</td></tr>\n</table>\n\n<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"90%\" align=\"center\" id=\"mainWrapper\">\n<tr><td id=\"mainContentCell\" align=\"left\" style=\"font-family:arial;font-size:16px;font-weight: bold;\">\n<img src=\"");
			 body.append(webServerURL+"img/newui/salesforce.png\">&nbsp;&nbsp;\n"+partner+" Synchronization [ "+owner+synchType+" ]</td></tr>\n<tr><td width=100%><hr style=\"color: #666699; height: 5px;\"/>&nbsp;</td></tr>\n</table>\n\n\n\n<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"90%\" align=\"center\" id=\"mainWrapper\" style=\"font-family:Arial, Helvetica, sans-serif; font-size:11px;\">\n<tr><td colspan=2 style=\"color:BLUE;\" align=center>");

			 body.append("\n<tr><td colspan=2>&nbsp;</td></tr>\n\n<tr><td colspan=2 style=\"background-color:#01aeee; padding:5px; font-size:14px;font-weight: bold;\">Error Detail</td></tr>\n");
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
	  * @param transaction
	  * @param toEmail
	  */
	 public static void sendSynchErrorEmail(ScheduledTransactionDataBean transaction, String toEmail) {
		 try {
			 MailResources res = new MailResources();
			 String fromEmail = res.getSystemAddress();
			 //== if sendAlert=Y && synchEmail is provided
			 if(transaction.getSendAlert() && !Default.toDefault(toEmail).equals("")){
				 StringBuffer body = new StringBuffer("");
				 String bmsError = SalesForceManager.SFErrorParsing(transaction.getError());
				 if(bmsError.equals(""))
					 bmsError = "unknown error occured during synchronization";
				 SynchronizationManager.mkSynchErrorEmailBody(body,transaction,bmsError);

				 AlertMail.sendMail("Synch Error Report",body.toString(),fromEmail,new String[]{toEmail},true);
				 System.out.println(  "==== Synch Error Email Sent to user's email: " + toEmail + " ====");
			 }

			 String subject = "Synch Transaction ["+transaction.getSynchPartner()+"]  Error Report -  [" + transaction.getUserID() + "]";

			 StringBuffer body = new StringBuffer("");
			 body.append("Transaction No: "+transaction.getScheduledNumber()+"\n<br>");
			 String type = transaction.getScheduledType().equals("I")?"Import":transaction.getScheduledType().equals("P")?"Peer":"Export";
			 body.append("Transaction Type: "+type+"\n<br>");
			 body.append("Transaction Scheduled Date: "+transaction.getScheduledDate()+"\n\n<br><br>");
			 body.append("Error Detail :\n<br>");
			 body.append(transaction.getError());

			 AlertMail.sendMail(subject,body.toString(),fromEmail,res.getNotificationAddress(),true);
			 System.out.println("==== Confirmation Email Sent to BMS Notifcation Addresses: " + res.getNotificationAddress() + " ====");

		 } catch (Exception se) {
			 se.printStackTrace();
			 WebServerLogger.getLogger().log(se);
		 }
	 }

	 private static void mkSystemConfirmationEmailBody(StringBuffer body, String delay, String err, String email,
			 ScheduledTransactionDataBean scheduledImport, CampaignDataBean cmpDB, int invalidRecords) {
		 String scheduledDate = "";
		 String timePart = "" + scheduledImport.getScheduledDate();
		 timePart = timePart.substring(0, 16);
		 scheduledDate += " " + timePart;

		 if(scheduledImport == null)
			 return;
		 String synchType = scheduledImport.getScheduledType().equals("E")?"Export":scheduledImport.getScheduledType().equals("P")?"Peer":"Import";
		 String objName = scheduledImport.getTableName().equalsIgnoreCase(SalesForceManager.SF_OWNER_TABLENAME)?"SalesRep":"Subscriber";
		 String owner = scheduledImport.getTableName().equalsIgnoreCase(SalesForceManager.SF_OWNER_TABLENAME)?"Owner ":"";

		 String cmpMsg = "User Campaign couldn't schedule successfully.";
		 if(scheduledImport.getActionType().equalsIgnoreCase(APIDefinitions.SYNCH_ACTION_TYPE_WORKFLOW)){
			 synchType = "Campaign manually add to BMS Workflow through VisualForce Page";
			 cmpMsg = "Subscribers have been successfully manually added to BMS Workflow.";

		 } else if(scheduledImport.getActionType().equalsIgnoreCase(APIDefinitions.SYNCH_ACTION_TYPE_RECIPIENT)){
			 synchType = "Campaign recipient(s) import";
			 cmpMsg = "Subscribers have been successfully imported as recipients.";

		 } else if(cmpDB != null && cmpDB.getCampaignNumber()>0){
			 synchType = "Campaign Synchronization through VisualForce Page";
			 if(cmpDB.getStatus().equalsIgnoreCase(PMSDefinitions.CAMPAIGN_STATUS_SCHEDULED)
					 || cmpDB.getStatus().equalsIgnoreCase(PMSDefinitions.CAMPAIGN_STATUS_PENDING))
				 cmpMsg = "User Campaign ["+cmpDB.getCampaignNumber()+"] has been successfully scheduled at "+cmpDB.getScheduledDate();

		 }

		 body.append("<html>\r\n<HEAD>\r\n<TITLE" + scheduledImport.getSynchPartner() + " "+owner+synchType+" </TITLE>\r\n</HEAD>\r\n\r\n<style type=\"text/css\">\r\n<!--\r\n.style14 {color: #FFFFFF}\r\n.style11 {font-family: Verdana, Arial, Helvetica, sans-serif; color: #0000FF; font-size: 11px; }\r\n.style12 {font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 11px; }\r\n.style13 {font-family: Verdana, Arial, Helvetica, sans-serif;\r\ncolor: #0000FF;\r\nfont-size: 10px; }\r\n.style15 {\r\n        font-family: Verdana, Arial, Helvetica, sans-serif;\r\n        color: #000000;\r\n        font-size: 14px;\r\n}\r\n\r\n-->\r\n</style>\r\n</style>\r\n\r\n\r\n<body bgcolor=\"#FFFFFF\" leftmargin=\"0\" topmargin=\"0\" marginwidth=\"0\" marginheight=\"0\">\r\n");
		 body.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bordercolor=\"WHITE\" width=\"100%\" id=\"AutoNumber1\">\r\n  <tr>\r\n    <td width=\"100%\" bgcolor=\"#FFFFFF\"><img src=\"http://www.bridgemailsystem.com/pms/graphics/logo.gif\" alt=\"BridgeMail System.\" border=\"0\">");
		 body.append("<br></td>\r\n  </tr>\r\n</table>\r\n\r\n<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=100%>\r\n<tr><td align=center colspan=4><div align=\"center\" class=\"style13\"><strong><br>");
		 body.append("UserId: " + scheduledImport.getUserID() +  "</strong><br>Synchronization From: (" + scheduledImport.getSynchPartner() + " ["+owner+synchType+"])");
		 body.append("</div></td>\r\n</tr>\r\n\r\n<tr><td width=100% colspan=4>\r\n<table align=center width=80% cellspacing=0 border=1>\r\n\r\n<tr>\r\n<td colspan=4><br><div align=\"left\" class=\"style13\">Import Details:</td>\r\n</tr>\r\n\r\n<tr><td colspan=4>\r\n\r\n<table width=100% align=center cellspacing=0>\r\n<tr>\r\n<td colspan=1><div align=\"right\" class=\"style12\"><strong>");
		 if(!scheduledImport.getTableName().equalsIgnoreCase(SalesForceManager.SF_OWNER_TABLENAME)){
			 body.append("Selected List:</div></td>\r\n<td colspan=3><div align=\"center\" class=\"style12\">");
			 body.append(ListManager.getListName(scheduledImport.getListNumber()) + "<br>(#" + scheduledImport.getListNumber() + ") </div></td>\r\n</tr>\r\n\r\n");
		 }
		 body.append("<tr>\r\n<td colspan=1><div align=\"right\" class=\"style12\"><strong>Scheduled Date:</div></td>\r\n<td colspan=3><div align=\"center\" class=\"style12\">");
		 body.append("" + scheduledDate);
		 body.append("</div></td>\r\n</tr>\r\n\r\n<tr>\r\n<td colspan=1><div align=\"right\" class=\"style12\"><strong>Scheduled Frequency:</div></td>\r\n<td colspan=3><div align=\"center\" class=\"style12\">");
		 if(scheduledImport.getTableName().equalsIgnoreCase(SalesForceManager.SF_OWNER_TABLENAME)){
			 body.append("" + ( (scheduledImport.getRecurPeriod() != null &&
					 scheduledImport.getRecurPeriod().equalsIgnoreCase(PMSDefinitions.SCHEDULE_FREQUENCY_ONCE_WEEK)) ? "Daily" :"Once"));
		 } else {
			 body.append("" + ( (scheduledImport.getRecurPeriod() != null &&
					 scheduledImport.getRecurPeriod().equalsIgnoreCase(PMSDefinitions.SCHEDULE_FREQUENCY_ONCE_WEEK)) ? "Once Per Week" :
						 (scheduledImport.getRecurPeriod().equalsIgnoreCase(PMSDefinitions.SCHEDULE_FREQUENCY_ONCE_TWOWEEK)) ?
								 "Once Per Two Weeks" :
									 (scheduledImport.getRecurPeriod().equalsIgnoreCase(PMSDefinitions.SCHEDULE_FREQUENCY_ONCE_MONTH)) ? "Once Per Month" :
										 (scheduledImport.getRecurPeriod().equalsIgnoreCase(PMSDefinitions.SCHEDULE_TYPE_PEERING)) ?
												 "After Every " + scheduledImport.getRecurDay() + " mins." :
			 "One Time Only"));
		 }
		 body.append("</div></td>\r\n</tr>\r\n\r\n<tr>\r\n<td colspan=1><div align=\"right\" class=\"style12\"><strong>Inserted Records:</div></td>\r\n<td colspan=3><div align=\"center\" class=\"style12\">");
		 body.append("" + scheduledImport.getToGivenListAdded());
		 body.append("</div></td>\r\n</tr>\r\n\r\n<tr>\r\n<td colspan=1><div align=\"right\" class=\"style12\"><strong>Duplicated/Updated Records:</div></td>\r\n<td colspan=1><div align=\"center\" class=\"style12\">");
		 body.append("" + scheduledImport.getToGivenListUpdated());
		 if(!scheduledImport.getTableName().equalsIgnoreCase(SalesForceManager.SF_OWNER_TABLENAME)){
			 body.append("</div></td>\r\n</tr>\r\n\r\n<tr>\r\n<td colspan=1><div align=\"right\" class=\"style12\"><strong>Records Inserted/Updated to Supress List:</div></td>\r\n<td colspan=3><div align=\"center\" class=\"style12\">");
			 body.append("" + scheduledImport.getToSupressList());
		 }
		 body.append("</div></td>\r\n</tr>\r\n<tr>\r\n<td colspan=1><div align=\"right\" class=\"style12\"><strong>Bad Records:</div></td>\r\n<td colspan=1><div align=\"center\" class=\"style12\">");
		 body.append("" + invalidRecords);
		 body.append("</div></td>\r\n</tr>\r\n<tr>\r\n<td colspan=1><div align=\"right\" class=\"style12\"><strong>&nbsp;</div></td>\r\n<td colspan=1><div align=\"center\" class=\"style12\">&nbsp;");
		 body.append("</div></td>\r\n</tr>\r\n<tr>\r\n<td colspan=1><div align=\"right\" class=\"style12\"><strong>Total Records Processed:</div></td>\r\n<td colspan=1><div align=\"center\" class=\"style12\">");
		 body.append("" + scheduledImport.getTotalProcessed() +
				 "<br>(Processed in " + delay + ")");

		 if((scheduledImport.getActionType().equals(APIDefinitions.SYNCH_ACTION_TYPE_WORKFLOW)) || (cmpDB != null && cmpDB.getCampaignNumber()>0)){
			 body.append("</div></td>\r\n</tr>\r\n<tr>\r\n<td colspan=2><div align=\"right\" class=\"style12\">"+cmpMsg);
		 }

		 body.append("</div></td>\r\n</tr>\r\n\r\n</table>\r\n\r\n</td></tr>\r\n\r\n<tr>\r\n<td colspan=4><br><div align=\"left\" class=\"style13\">Bad Records Info:</td>\r\n</tr>\r\n\r\n<tr><td colspan=4>\r\n\r\n<table width=100% align=center cellspacing=0>\r\n\r\n");
		 body.append("<tr>\r\n<td colspan=4><div align=\"left\" class=\"style12\">");
		 body.append(err);
		 body.append("</div></td></tr>");

		 body.append("\r\n\r\n</table>\r\n\r\n</td></tr></table>\r\n<b><div align=\"center\" class=\"style12\">");
		 if(scheduledImport.getSendAlert()){
			 body.append("Confirmation Email has been sent on User's Email: '" + email + "'\n\n\n");
		 } else {
			 body.append("Confirmation Email has been setted off for this transaction. \n\n\n");
		 }
		 body.append(" </div>\r\n</td></tr>\r\n</table>\r\n\r\n</body></html>");
	 }


	 public static void sendTransactionConfirmationEmails(String toEmail, ScheduledTransactionDataBean scheduledImport,int invalidRecords,
			 String delay,StringBuffer htmlSubErrors) {
		 sendTransactionConfirmationEmails(toEmail,scheduledImport,null,invalidRecords,delay,htmlSubErrors);
	 }

	 public static void sendTransactionConfirmationEmails(String toEmail, ScheduledTransactionDataBean scheduledImport, CampaignDataBean cmpDB,
			 int invalidRecords, String delay,StringBuffer htmlSubErrors) {
		 try {
			 MailResources res = new MailResources();
			 String synchType = scheduledImport.getScheduledType().equals("I")?"Import":scheduledImport.getScheduledType().equals("P")?"Peer":"Export";
			 StringBuffer body = new StringBuffer("");
			 String partner = scheduledImport.getSynchPartner().equalsIgnoreCase("Netsuite")?"Netsuite":"Salesforce";

			 if(scheduledImport.getActionType().equals(APIDefinitions.SYNCH_ACTION_TYPE_WORKFLOW)){
				 synchType = "Campaign manually add to Workflow through VisualForce Page";
			 } else if(scheduledImport.getActionType().equals(APIDefinitions.SYNCH_ACTION_TYPE_RECIPIENT)){
				 synchType = "Campaign recipient(s) import";
			 } else if(cmpDB != null && cmpDB.getCampaignNumber()>0){
				 synchType = "Campaign Synchronization through VisualForce Page";
			 }

			 if(!Default.toDefault(toEmail).equals("") && scheduledImport.getSendAlert()){

				 String sub = partner + " " + synchType + " confirmation";
				 mkUserConfirmationEmailBody(body, htmlSubErrors.toString(), scheduledImport, cmpDB, invalidRecords);
				 AlertMail.sendMail(sub,body.toString(),res.getSystemAddress(),new String[]{toEmail},true);
				 System.out.println("==== Confirmation Email Sent to user's email: " + toEmail + " ====");
			 }

			 String subject = partner+" "+synchType+" - " + scheduledImport.getUserID() + " - Process Count:" +
			 scheduledImport.getTotalProcessed();
			 body = new StringBuffer("");
			 mkSystemConfirmationEmailBody(body, delay, htmlSubErrors.toString(),toEmail, scheduledImport, cmpDB, invalidRecords);

			 AlertMail.sendMail(subject,body.toString(),res.getSystemAddress(),res.getNotificationAddress(),true);
			 System.out.println("==== Confirmation Email Sent to BMS Notifcation Addresses[0]: " +res.getNotificationAddress()[0] + " ====");

		 } catch (Exception se) {
			 se.printStackTrace();
			 WebServerLogger.getLogger().log(se);
		 }
	 }



	 private static void mkUserConfirmationEmailBody(StringBuffer body, String errorDetail, ScheduledTransactionDataBean scheduledImport,
			 CampaignDataBean cmpDB, int invalidRecords){

		 try {
			 String webServerURL = PMSResources.getInstance().getWebServerURL();
			 String synchType = scheduledImport.getScheduledType().equals("I")?"Import":scheduledImport.getScheduledType().equals("P")?"Peer":"Export";
			 String objName = scheduledImport.getTableName().equalsIgnoreCase(SalesForceManager.SF_OWNER_TABLENAME)?"SalesRep":"Subscriber";
			 String owner = scheduledImport.getTableName().equalsIgnoreCase(SalesForceManager.SF_OWNER_TABLENAME)?"Owner ":"";
			 String partner = scheduledImport.getSynchPartner().equalsIgnoreCase("Netsuite")?"Netsuite":"Salesforce";

			 String cmpMsg = "Your Campaign couldn't schedule successfully. If you have any questions, please send email to support@makesbridge.com.";
			 if(scheduledImport.getActionType().equals(APIDefinitions.SYNCH_ACTION_TYPE_WORKFLOW)){
				 synchType = "Campaign manually add to Workflow through VisualForce Page";
				 //load Workflow Info
				 Vector infoVec = scheduledImport.getHashNV();
				 int workflowId = 0;
				 int stepOrder = 0;
				 for(int i=0;infoVec!=null && i<infoVec.size();i++){
					 NameValue nv = (NameValue)infoVec.get(i);
					 if(nv != null && nv.getName().equalsIgnoreCase("workflowId")){
						 workflowId = Default.defaultInt(nv.getValue());
					 } else if(nv != null && nv.getName().equalsIgnoreCase("stepOrder")){
						 stepOrder = Default.defaultInt(nv.getValue());
					 }
				 }
				 WorkflowBean wfBean = null;
				 if(workflowId>0)
					 wfBean = WorkflowManager.getWorkflowById(workflowId);

				 if(wfBean != null){
					 WorkflowStepBean bean = wfBean.getWorkflowStepByOrder(stepOrder);
					 String step = "Step "+stepOrder;
					 if(bean != null)
						 step += bean.getLabel();
					 cmpMsg = "Subscribers have been successfully manually added to workflow "+wfBean.getName()+" in "+step;
				 } else {
					 cmpMsg = "Subscribers have been successfully manually added to BMS workflow.";
				 }

			 } else if(scheduledImport.getActionType().equals(APIDefinitions.SYNCH_ACTION_TYPE_RECIPIENT)){
				 synchType = "Campaign recipient(s) import";
				 cmpMsg = "";
			 } else if(cmpDB != null && cmpDB.getCampaignNumber()>0){
				 synchType = "Campaign Synchronization through VisualForce Page";
				 if(cmpDB.getStatus().equalsIgnoreCase(PMSDefinitions.CAMPAIGN_STATUS_SCHEDULED))
					 cmpMsg = cmpDB.getCampaignName()+" has been successfully scheduled at "+Default.formatDate(cmpDB.getScheduledDate().getTime(), "yyyy-MM-dd HH:mm");
			 }

			 body.append("\n<html>\n<head>\n<link HREF=\"/pms/pms2.css\" rel=\"STYLESHEET\" TYPE=\"text/css\"/>\n<link HREF=\"/pms/jwb.css\" rel=\"STYLESHEET\" TYPE=\"text/css\"/>\n\n</head>\n<body leftmargin=\"0\" topmargin=\"0\" marginwidth=\"0\" marginheight=\"0\" >\n<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"90%\">\n<tr><td id=\"logo\"><img src=\"");
			 body.append(webServerURL+"img/newui/makesbridge.png\" alt=\"BridgeMail Systems\"><br>&nbsp;</td></tr>\n</table>\n\n<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"90%\" align=\"center\" id=\"mainWrapper\">\n<tr><td id=\"mainContentCell\" align=\"left\" style=\"font-family:arial;font-size:16px;font-weight: bold;\">\n<img src=\"");
			 body.append(webServerURL+"img/newui/salesforce.png\">&nbsp;&nbsp;\n "+partner+" "+owner+synchType+"</td></tr>\n<tr><td width=100%><hr style=\"color: #666699; height: 5px;\"/>&nbsp;</td></tr>\n</table>\n\n\n\n<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"90%\" align=\"center\" id=\"mainWrapper\" style=\"font-family:Arial, Helvetica, sans-serif; font-size:11px;\">\n<tr><td colspan=2 style=\"color:BLUE;\" align=center>");
			 body.append("<b>UserID:  "+scheduledImport.getUserID()+"</b><br>");

			 body.append("Your '"+partner+"' "+owner+synchType+" was performed successfully");

			 body.append("</td></tr>\n<tr><td colspan=2 style=\"background-color:#01aeee; padding:5px; font-size:14px;font-weight: bold;\">"+owner+synchType+" Summary");
			 if(!scheduledImport.getTableName().equalsIgnoreCase(SalesForceManager.SF_OWNER_TABLENAME) && 
					 !scheduledImport.getActionType().equals(APIDefinitions.SYNCH_ACTION_TYPE_RECIPIENT)){
				 body.append("</td></tr>\n<tr><td colspan=2>&nbsp;</td></tr>\n<tr><td width=25%><b>Selected List:</td><td>");
				 body.append("" + ListManager.getListName(scheduledImport.getListNumber()));
			 }
			 body.append("</td></tr>\n<tr><td colspan=2>&nbsp;</td></tr>\n<tr><td style=\"background-color:#EEE;\" width=25%><b>Added "+objName+":</td><td style=\"background-color:#EEE;\">");
			 body.append(scheduledImport.getToGivenListAdded());
			 body.append("</td></tr>\n<tr><td width=25%><b>Updated "+objName+":</td><td>"+scheduledImport.getToGivenListUpdated());
			 if(!scheduledImport.getTableName().equalsIgnoreCase(SalesForceManager.SF_OWNER_TABLENAME)){
				 body.append(
						 "</td></tr>\n<tr><td width=25% style=\"background-color:#EEE;\"><b>Added/Updated to Suppress List:</td><td style=\"background-color:#EEE;\">" +
						 scheduledImport.getToSupressList());
			 }
			 body.append("</td></tr>\n<tr><td width=25%><b>Bad Records:</td><td  >"+invalidRecords);
			 body.append("</td></tr>\n<tr><td colspan=2>&nbsp;</td></tr>\n<tr><td style=\"background-color:#EEE;\" width=25%><b>Total Records Processed:</td><td style=\"background-color:#EEE;\" >");
			 body.append(scheduledImport.getTotalProcessed());

			 // if synch has been created through VF page
			 if(scheduledImport.getActionType().equalsIgnoreCase(APIDefinitions.SYNCH_ACTION_TYPE_WORKFLOW)){
				 body.append("</td></tr>\n<tr><td colspan=2>&nbsp;</td></tr>\n<tr><td style=\"background-color:#EEE;\" width=25%><b>Workflow Info:</td><td style=\"background-color:#EEE;\" >");
				 body.append(cmpMsg);
			 } else if(scheduledImport.getActionType().equalsIgnoreCase(APIDefinitions.SYNCH_ACTION_TYPE_RECIPIENT)){
				 body.append("</td></tr>\n<tr><td colspan=2>&nbsp;</td></tr>\n<tr><td style=\"background-color:#EEE;\" width=25%>&nbsp;</td><td style=\"background-color:#EEE;\" >");
				 body.append(cmpMsg);
			 } else if(cmpDB != null && cmpDB.getCampaignNumber()>0){
				 body.append("</td></tr>\n<tr><td colspan=2>&nbsp;</td></tr>\n<tr><td style=\"background-color:#EEE;\" width=25%><b>Campaign Info:</td><td style=\"background-color:#EEE;\" >");
				 body.append(cmpMsg);
			 }

			 body.append("\n<tr><td colspan=2>&nbsp;</td></tr>\n\n<tr><td colspan=2 style=\"background-color:#01aeee; padding:5px; font-size:14px;font-weight: bold;\">Bad Records Detail</td></tr>\n");
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
	  * @param bmsUserId
	  * @param syncPartner
	  * @return
	  */
	 public static String getCredPassReadOnly(String bmsUserId, String syncPartner) {
		 try {
			 byte[] pass = getCredentialPass(bmsUserId, syncPartner);
			 System.out.println("[getSyncCredPass()]: Lenght:"+pass.length+", Value:"+new String(pass));
			 //Read Only JKS manager keys
			 String decrPass = JKSManager.decryptReadOnly(bmsUserId, pass);
			 if(decrPass==null || decrPass.equals(""))
				 return null;
			 return decrPass;
		 } catch (Exception ex) {
			 ex.printStackTrace();
			 WebServerLogger.getLogger().log(ex);
		 }
		 return null;
	 }

	 /**
	  *
	  * @param multipleDays
	  * @return
	  */
	 public static String getNextRecurDayForWeekly(Vector daysVec,String hour, String min){
		 if(daysVec == null || daysVec.size()==0)
			 return "1";
		 if(daysVec.size()==1)
			 return (String)daysVec.get(0);
		 try{
			 // sorting multiple days
			 Collections.sort(daysVec);

			 Calendar calendar = Calendar.getInstance();
			 for(int i=0;i<daysVec.size();i++){
				 String day = (String) daysVec.get(i);
				 int dayValue = Default.defaultInt(day);
				 if (dayValue == calendar.get(calendar.DAY_OF_WEEK)) {
					 if (Default.defaultInt(hour) > calendar.get(Calendar.HOUR_OF_DAY))
						 return day;
					 else if (Default.defaultInt(hour) == calendar.get(calendar.HOUR_OF_DAY) && Default.defaultInt(min) > calendar.get(calendar.MINUTE))
						 return day;
				 } else if(dayValue > calendar.get(calendar.DAY_OF_WEEK)){
					 return day;
				 }
			 }
			 // first index default
			 return (String)daysVec.get(0);

		 } catch(Exception ex){
			 ex.printStackTrace();
			 WebServerLogger.getLogger().log(ex);
		 }
		 return "1";
	 }

	 /**
	  *
	  * @param multipleDays
	  * @return
	  */
	 public static ScheduledTransactionDataBean getNextScheduledDateForWeekly(ScheduledTransactionDataBean schDataBean){
		 if(schDataBean == null)
			 return null;

		 try{

			 Timestamp scheduledDate = schDataBean.getScheduledDate();

			 Calendar cal = Calendar.getInstance();
			 cal.setTime(scheduledDate);

			 Vector daysVec = schDataBean.getMultipleDaysVec();
			 //Handling old cases <= 1
			 if(daysVec == null || daysVec.size()<= 1){
				 cal.set(Calendar.DATE,cal.get(Calendar.DATE)+7);
				 schDataBean.setScheduledDate(Default.toTimestamp(cal.getTime()));
				 return schDataBean;
			 }
			 //get Next Recur Day
			 int schDay = cal.get(Calendar.DAY_OF_WEEK);
			 int schHour = cal.get(Calendar.HOUR_OF_DAY);
			 int schMin = cal.get(Calendar.MINUTE);

			 int nextDay = Default.defaultInt(getNextRecurDayForWeekly(daysVec,""+schHour,""+schMin));
			 int daysInWeek = 7;
			 int afterDay = 0;

			 if (nextDay > schDay){
				 afterDay = nextDay - schDay;
			 } else if(nextDay < schDay){
				 afterDay = daysInWeek - schDay + nextDay;
			 } else {
				 afterDay = daysInWeek;
			 }
			 //scheduling date for next recur day
			 cal.set(Calendar.DATE,cal.get(Calendar.DATE)+afterDay);
			 schDataBean.setScheduledDate(Default.toTimestamp(cal.getTime()));
			 schDataBean.setRecurDay(nextDay);

		 } catch(Exception ex){
			 ex.printStackTrace();
			 WebServerLogger.getLogger().log(ex);
		 }
		 return schDataBean;
	 }

	 /**
	  *
	  * @param schId
	  * @param status
	  * @return
	  */
	 public static boolean updateScheduledTransactionStatus(long schId, String status) {
		 if(schId == 0 || status == null || status.equals(""))
			 return false;
		 try {
			 return dataServer.updateScheduledTransactionStatus(schId,status);
		 } catch (Exception ex) {
			 ex.printStackTrace();
			 WebServerLogger.getLogger().log(ex);
		 }
		 return false;
	 }

	 /**
	  *
	  * @param credBean
	  * @return
	  */
	 public static boolean updateUserSMSCredentials(UserSMSCredentialsBean credBean){
		 if(credBean == null || credBean.getUserID().equals(""))
			 return false;
		 try{
			 return bmsServer.updateUserSMSCredentials(credBean);
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
	 public static UserSMSCredentialsBean getUserSMSCredentials(String userId){
		 if(Default.toDefault(userId).equals(""))
			 return null;
		 try{
			 return bmsServer.getUserSMSCredentials(userId);
		 } catch(Exception ex){
			 ex.printStackTrace();
			 WebServerLogger.getLogger().log(ex);
		 }
		 return null;
	 }

	 /**
	  * 
	  * @param scheduledNumber
	  * @return
	  */
	 public static boolean deleteScheduledTransaction(long scheduledNumber){
		 if(scheduledNumber <= 0)
			 return false;
		 try{
			 return dataServer.deleteScheduledTransaction(scheduledNumber);
		 } catch(Exception ex){
			 ex.printStackTrace();
			 WebServerLogger.getLogger().log(ex);
		 }
		 return false;
	 }

	 
	 /**
	  * 
	  * @param userId
	  * @param synchPartner
	  * @return
	  */
	 public static boolean deleteScheduledTransactions(String userId, String synchPartner){
		 if(Default.toDefault(userId).equals("") || Default.toDefault(synchPartner).equals(""))
			 return false;
		 
		 try{
			 return dataServer.deleteScheduledTransactions(userId, synchPartner);
		 } catch(Exception ex){
			 ex.printStackTrace();
			 WebServerLogger.getLogger().log(ex);
		 }
		 return false;
	 }
	 
	 /**
	  * 
	  * @param transaction
	  * @return
	  */
	 public static boolean sendHighriseNotificationEmails(ScheduledTransactionDataBean transaction) {
		 try{
			 String synchType = "Import";
			 String partner = "Highrise";

			 String subject = partner+" "+synchType;

			 ExternalSynchCredentials creds = APIManager.getExternalSynchCredentials(transaction.getUserID(), partner);

			 if(creds == null)
				 return false;

			 String emailBody = !transaction.getError().equals("")?getHRErrorEmailBody(transaction): getHRSuccessEmailBody(transaction);

			 MailResources res = new MailResources();
			 if(transaction.getSendAlert() && !creds.getSynchEmail().equals("")){

				 AlertMail.sendMail(subject,emailBody,res.getSystemAddress(),new String[]{creds.getSynchEmail()},true);
				 System.out.println("==== Confirmation Email Sent to user's email: " + creds.getSynchEmail() + " ====");
			 }

			 subject = partner+" "+synchType+" - " + transaction.getUserID() + " - Process Count:" +transaction.getTotalProcessed();

			 return AlertMail.sendMail(subject,emailBody,res.getSystemAddress(),res.getNotificationAddress(),true);
		 } catch(Exception ex){
			 ex.printStackTrace();
			 WebServerLogger.getLogger().log(ex);
		 }
		 return false;
	 }


	 /**
	  * 
	  * @param transaction
	  * @return
	  */
	 private static String getHRSuccessEmailBody(ScheduledTransactionDataBean transaction){

		 StringBuffer body = new StringBuffer();
		 try {
			 String webServerURL = PMSResources.getInstance().getWebServerURL();
			 String synchType = transaction.getScheduledType().equals("I")?"Import":transaction.getScheduledType().equals("P")?"Peer":"Export";
			 String partner = transaction.getSynchPartner();

			 body.append("\n<html>\n<head>\n<link HREF=\"/pms/pms2.css\" rel=\"STYLESHEET\" TYPE=\"text/css\"/>\n<link HREF=\"/pms/jwb.css\" rel=\"STYLESHEET\" TYPE=\"text/css\"/>\n\n</head>\n<body leftmargin=\"0\" topmargin=\"0\" marginwidth=\"0\" marginheight=\"0\" >\n<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"90%\">\n<tr><td id=\"logo\"><img src=\"");
			 body.append(webServerURL+"img/newui/makesbridge.png\" alt=\"BridgeMail Systems\"><br>&nbsp;</td></tr>\n</table>\n\n<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"90%\" align=\"center\" id=\"mainWrapper\">\n<tr><td id=\"mainContentCell\" align=\"left\" style=\"font-family:arial;font-size:16px;font-weight: bold;\">\n<img src=\"");
			 body.append(webServerURL+"img/newui/highrise.png\">&nbsp;&nbsp;\n "+partner+" "+synchType+"</td></tr>\n<tr><td width=100%><hr style=\"color: #666699; height: 5px;\"/>&nbsp;</td></tr>\n</table>\n\n\n\n<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"90%\" align=\"center\" id=\"mainWrapper\" style=\"font-family:Arial, Helvetica, sans-serif; font-size:11px;\">\n<tr><td colspan=2 style=\"color:BLUE;\" align=center>");
			 body.append("<b>UserID:  "+transaction.getUserID()+"</b><br>");

			 body.append("Your '"+partner+"' "+synchType+" was performed successfully");

			 body.append("</td></tr>\n<tr><td colspan=2 style=\"background-color:#01aeee; padding:5px; font-size:14px;font-weight: bold;\">"+synchType+" Summary");
			 //== appending if Target list
			 if(!transaction.getActionType().equals(APIDefinitions.SYNCH_ACTION_TYPE_RECIPIENT)){
				 body.append("</td></tr>\n<tr><td colspan=2>&nbsp;</td></tr>\n<tr><td width=25%><b>Selected List:</td><td>");
				 body.append("" + ListManager.getListName(transaction.getListNumber()));
			 }

			 String objName = transaction.getTableName();
			 body.append("</td></tr>\n<tr><td colspan=2>&nbsp;</td></tr>\n<tr><td style=\"background-color:#EEE;\" width=25%><b>Added "+objName+":</td><td style=\"background-color:#EEE;\">");
			 body.append(transaction.getToGivenListAdded());
			 body.append("</td></tr>\n<tr><td width=25%><b>Updated "+objName+":</td><td>"+transaction.getToGivenListUpdated());

			 int badRecords = transaction.getTotalProcessed() - (transaction.getToGivenListAdded() + transaction.getToGivenListUpdated());

			 body.append("</td></tr>\n<tr><td width=25%><b>Bad Records:</td><td  >"+badRecords);
			 body.append("</td></tr>\n<tr><td colspan=2>&nbsp;</td></tr>\n<tr><td style=\"background-color:#EEE;\" width=25%><b>Total Records Processed:</td><td style=\"background-color:#EEE;\" >");
			 body.append(""+transaction.getTotalProcessed());

			 body.append("\n<tr><td colspan=2>&nbsp;</td></tr>\n\n<tr><td colspan=2 style=\"background-color:#01aeee; padding:5px; font-size:14px;font-weight: bold;\">Bad Records Detail</td></tr>\n");
			 body.append("<tr><td colspan=2>");
			 body.append(transaction.getError());
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
		 return body.toString();
	 }


	 /**
	  * 
	  * @param transaction
	  * @return
	  */
	 private static String getHRErrorEmailBody(ScheduledTransactionDataBean transaction){

		 StringBuffer body = new StringBuffer();
		 try {
			 String webServerURL = PMSResources.getInstance().getWebServerURL();
			 String synchType = "Import";
			 String partner = "Highrise";

			 body.append("\n<html>\n<head>\n<link HREF=\"/pms/pms2.css\" rel=\"STYLESHEET\" TYPE=\"text/css\"/>\n<link HREF=\"/pms/jwb.css\" rel=\"STYLESHEET\" TYPE=\"text/css\"/>\n\n</head>\n<body leftmargin=\"0\" topmargin=\"0\" marginwidth=\"0\" marginheight=\"0\" >\n<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"90%\">\n<tr><td id=\"logo\"><img src=\"");
			 body.append(webServerURL+"img/newui/makesbridge.png\" alt=\"BridgeMail Systems\"><br>&nbsp;</td></tr>\n</table>\n\n<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"90%\" align=\"center\" id=\"mainWrapper\">\n<tr><td id=\"mainContentCell\" align=\"left\" style=\"font-family:arial;font-size:16px;font-weight: bold;\">\n<img src=\"");
			 body.append(webServerURL+"img/newui/highrise.png\">&nbsp;&nbsp;\n"+partner+" Synchronization [ "+synchType+" ]</td></tr>\n<tr><td width=100%><hr style=\"color: #666699; height: 5px;\"/>&nbsp;</td></tr>\n</table>\n\n\n\n<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"90%\" align=\"center\" id=\"mainWrapper\" style=\"font-family:Arial, Helvetica, sans-serif; font-size:11px;\">\n<tr><td colspan=2 style=\"color:BLUE;\" align=center>");

			 body.append("\n<tr><td colspan=2>&nbsp;</td></tr>\n\n<tr><td colspan=2 style=\"background-color:#01aeee; padding:5px; font-size:14px;font-weight: bold;\">Error Detail</td></tr>\n");
			 body.append("<tr><td colspan=2>");
			 body.append(transaction.getError());
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
		 return body.toString();
	 }


	 /**
	  * 
	  * @param schDataBean
	  * @return
	  */
	 public static ScheduledTransactionDataBean reloadWithNextScheduleDate(ScheduledTransactionDataBean schDataBean) {

		 try {

			 schDataBean.setStatus(PMSDefinitions.SCHEDULE_STATUS_SCHEDULED);
			 schDataBean.setToGivenListAdded(0);
			 schDataBean.setToGivenListUpdated(0);
			 schDataBean.setToSupressList(0);
			 schDataBean.setTotalProcessed(0);

			 schDataBean.setErrorAlertSent(false);
			 schDataBean.setError("");

			 Calendar calendar = Calendar.getInstance();
			 calendar.setTimeInMillis(schDataBean.getScheduledDate().getTime());

			 if (schDataBean.getRecurPeriod().equals("O")) {
				 Timestamp scheduledDate = schDataBean.getScheduledDate();
				 // need to handle multipleDays case
				 schDataBean = getNextScheduledDateForWeekly(schDataBean);

			 } else if (schDataBean.getRecurPeriod().equals("T")) {
				 calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)+14);
				 schDataBean.setScheduledDate(Default.toTimestamp(calendar.getTime()));

			 } else if (schDataBean.getRecurPeriod().equals("M")) {
				 int dayOfMonth = schDataBean.getRecurDay();
				 if (calendar.get(Calendar.MONTH) + 1 == Calendar.FEBRUARY && dayOfMonth > 28) { //if day selected is 29 or 30 set it to 28 for Feb.
					 calendar.set(Calendar.DAY_OF_MONTH, 28);
				 }
				 calendar.add(Calendar.MONTH,1);

				 schDataBean.setScheduledDate(Default.toTimestamp(calendar.getTime()));
			 }
		 } catch (Exception e) {
			 e.printStackTrace();
			 WebServerLogger.getLogger().log(e);

		 }

		 return schDataBean;
	 }


	 /**
	  * 
	  * @param scheduledNumber
	  * @param alert
	  * @return
	  */
	 public static boolean updateSynchErrorAlertSent(long scheduledNumber, boolean alert){
		 if(scheduledNumber <= 0 )
			 return false;
		 try {
			 return dataServer.updateSynchErrorAlertSent(scheduledNumber, alert);
		 } catch (Exception ex) {
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
	 public static boolean createExternalSynchCredentials(ExternalSynchCredentials bean){

		 try{
			 if(bean == null)
				 return false;

			 return reportServer.createExternalSynchCredentials(bean);

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
	 public static boolean updateExternalSynchCredentials(ExternalSynchCredentials bean){

		 try{
			 if(bean == null)
				 return false;

			 return reportServer.updateExternalSynchCredentials(bean);
		 } catch(Exception ex){
			 ex.printStackTrace();
			 WebServerLogger.getLogger().log(ex);
		 }
		 return false;
	 }

	 /**
	  *
	  * @param userId
	  * @param synchPartner
	  * @return
	  */
	 public static ExternalSynchCredentials getExternalSynchCredentials(String userId, String synchPartner){

		 try{
			 if(Default.toDefault(userId).equals("") || Default.toDefault(synchPartner).equals(""))
				 return null;

			 return reportServer.getExternalSynchCredentials(userId,synchPartner);
		 } catch(Exception ex){
			 ex.printStackTrace();
			 WebServerLogger.getLogger().log(ex);
		 }
		 return null;
	 }
	 
	 /**
	  * 
	  * @param userId
	  * @param synchPartner
	  * @return
	  */
	 public static boolean deleteExternalSynchCredentials(String userId, String synchPartner){

		 if(Default.toDefault(userId).equals("") || Default.toDefault(synchPartner).equals(""))
			 return false;
		 
		 try{
			 return reportServer.deleteExternalSynchCredentials(userId,synchPartner);

		 } catch(Exception ex){
			 ex.printStackTrace();
			 WebServerLogger.getLogger().log(ex);
		 }
		 return false;
	 }
	 
	 /**
	  * 
	  * @param userId
	  * @param synchId
	  * @param synchPartner
	  * @return
	  */
	 public static int getSubscriberLeadScoreBySynchID(String userId, String synchId, String synchPartner){

		 try{
			 return reportServer.getSubscriberLeadScoreBySynchID(userId,synchId,synchPartner);
		 } catch(Exception ex){
			 ex.printStackTrace();
			 WebServerLogger.getLogger().log(ex);
		 }
		 return 0;
	 }

	 
	 /**
	  *
	  * @param userId
	  * @param cmpNo
	  * @param sfId
	  * @return
	  */
	 public static boolean createCampaignCRMMapping(CampaignCRMBean bean){
		 if(bean == null || bean.getCampaignNumber() == 0)
			 return false;
		 try{
			 return dataServer.createCampaignCRMMapping(bean);
		 } catch(Exception ex){
			 ex.printStackTrace();
			 WebServerLogger.getLogger().log(ex);
		 }
		 return false;
	 }

	 /**
	  *
	  * @param userId
	  * @param cmpNo
	  * @return
	  */
	 public static int deleteCampaignCRMMapping(String userId, String synchPartner, long cmpNo){
		 if(cmpNo == 0 || Default.toDefault(userId).equals(""))
			 return 0;
		 try{
			 return dataServer.deleteCampaignCRMMapping(userId,synchPartner,cmpNo);
		 } catch(Exception ex){
			 ex.printStackTrace();
			 WebServerLogger.getLogger().log(ex);
		 }
		 return 0;
	 }

	 /**
	  *
	  * @param userId
	  * @param cmpNo
	  * @return
	  */
	 public static boolean markCampaignCRMMappingActive(CampaignCRMBean bean){
		 if(bean == null || bean.getCampaignNumber() == 0)
			 return false;
		 try{
			 return dataServer.markCampaignCRMMappingActive(bean);
		 } catch(Exception ex){
			 ex.printStackTrace();
			 WebServerLogger.getLogger().log(ex);
		 }
		 return false;

	 }

 
   /**
     *
     * @param userId
     * @param isActive
     * @return
     */
    public static Vector getCRMMappedCampaigns(String userId,String synchPartner, boolean isActive){
        if(Default.toDefault(userId).equals(""))
            return new Vector();
        try{
            return dataServer.getCRMMappedCampaigns(userId,synchPartner,isActive);
        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }

    /**
     *
     * @param userId
     * @param isActive
     * @return
     */
    public static Vector getCRMMappedCampaigns(String userId, String synchPartner){
        if(Default.toDefault(userId).equals(""))
            return new Vector();
        try{
            return dataServer.getCRMMappedCampaigns(userId,synchPartner);
        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }

    /**
     *
     * @param userId
     * @param cmpNo
     * @return
     */
    public static String getMappedCampaignSynchID(CampaignCRMBean bean){
        if(bean == null || bean.getUserId().equals("") || bean.getCampaignNumber() == 0)
            return "";
        try{        	
            return dataServer.getMappedCampaignSynchID(bean);
        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return "";
    }    
}