package com.PMSystems;

import com.PMSystems.logger.*;
import com.PMSystems.dbbeans.*;
import com.PMSystems.container.*;
import com.PMSystems.sejbs.*;
import com.PMSystems.util.*;
import com.PMSystems.*;

import java.util.*;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import javax.servlet.http.HttpSession;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ListManager {

    private static ComplexQueryServer complexQueryServer;
    private static DataQueryServer dataServer;
    private static AlphaQueryServer alphaServer;

    private static String []basicFields = {"EMAIL_ADDR","EMAIL_TYPE","GENDER","FIRST_NAME"
	,"MIDDLE_NAME","LAST_NAME","BIRTH_DATE","ADDRESS_LINE1","ADDRESS_LINE2","CITY"
	,"STATE_CODE","ZIP_CODE","COUNTRY_CODE","MARITAL_STATUS","OCCUPATION","JOB_STATUS"
	,"HOUSEHOLD_INCOME","EDUCATION_LEVEL","TELEPHONE","COMPANY","INDUSTRY","SOURCE","SALES_REP"
	,"SALES_STATUS","AREA_CODE"};
    static{
	complexQueryServer = EJBHomesFactory.getComplexQueryServerRemote();
	dataServer = EJBHomesFactory.getDataQueryServerRemote();
	alphaServer = EJBHomesFactory.getAlphaQueryServerRemote();
    }
    private ListManager(){
    }


    public static Vector getAllLists(UserInfo userInfo){
	Vector listVec = new Vector();
	try{
	    listVec = dataServer.getAllLists(userInfo);
	}catch(Exception ex){
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return listVec;
    }
    /**
     * 
     * @param userInfo
     * @param searchText
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector getAllLists(UserInfo userInfo, String searchText, int offset, int bucket) {
	
	try {
	    return dataServer.getAllLists(userInfo, searchText, offset, bucket);
	    
	}catch(Exception ex){
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }
    /**
     * 
     * @param listNum
     * @param tagsVec
     * @return
     */
    public static boolean createUpdateListTags(long listNum, Vector tagsVec) {
	
	try {
	    return dataServer.createUpdateListTags(listNum, tagsVec);
	    
	}catch(Exception ex){
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return false;
    }
    /**
     * 
     * @param userInfo
     * @param searchText
     * @return
     */
    public static int countAllLists(UserInfo userInfo, String searchText) {
	
	try {
	    return dataServer.countAllLists(userInfo, searchText);
	    
	}catch(Exception ex){
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return 0;
    }
    /**
     * 
     * @param layout
     * @return
     */
    public static String getLayoutCustomFields(String layout){
	Vector layoutVec = new Vector();
	try{
	    layoutVec =  Default.fromCSV(layout);
	    for(int i=0;i<SubscriberManager.NUMBER_OF_REGULAR_FIELDS;i++){
		layoutVec.remove(getFieldName(i));
	    }
	}catch(Exception ex){
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return Default.toCSV(layoutVec);
    }


    public static String getLayoutFields(String layout) {
	Vector fieldVec = new Vector();
	String fieldLayout = "";
	try{
	    if (layout == null || layout.equals(""))
		return fieldLayout;
	    fieldVec = Default.fromCSV(layout);
	    if (fieldVec == null || fieldVec.size() < 1)
		return fieldLayout;
	    // Swaping name on field numbers
	    //System.out.println("fieldVec size ="+fieldVec.size());
	    for (int i = 0; i < SubscriberManager.NUMBER_OF_REGULAR_FIELDS; i++) {
		if (fieldVec.indexOf("" + i) > -1){
		    fieldVec.set(fieldVec.indexOf("" + i),getFieldName(i));
		}
	    }
	}catch(Exception ex){
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return Default.toCSV(fieldVec);
    }


    public static String getFieldName(int fieldNumber){

	String field="";
	switch (fieldNumber) {
	case 0: {
	    field = "EMAIL_ADDR";
	    break;
	}
	case 1: {
	    field = "EMAIL_TYPE";
	    break;
	}
	case 2: {
	    field = "GENDER";
	    break;
	}
	case 3: {
	    field = "FIRST_NAME";
	    break;
	}
	case 4: {
	    field = "MIDDLE_NAME";
	    break;
	}
	case 5: {
	    field = "LAST_NAME";
	    break;
	}
	case 6: {
	    field = "BIRTH_DATE";
	    break;
	}
	case 7: {
	    field = "ADDRESS_LINE1";
	    break;
	}
	case 8: {
	    field = "ADDRESS_LINE2";
	    break;
	}
	case 9: {
	    field = "CITY";
	    break;
	}
	case 10: {
	    field = "STATE_CODE";
	    break;
	}
	case 11: {
	    field = "ZIP_CODE";
	    break;
	}
	case 12: {
	    field = "COUNTRY_CODE";
	    break;
	}
	case 13: {
	    field = "MARITAL_STATUS";
	    break;
	}
	case 14: {
	    field = "OCCUPATION";
	    break;
	}
	case 15: {
	    field = "JOB_STATUS";
	    break;
	}
	case 16: {
	    field = "HOUSEHOLD_INCOME";
	    break;
	}
	case 17: {
	    field = "EDUCATION_LEVEL";
	    break;
	}
	case 18: {
	    field = "TELEPHONE";
	    break;
	}
	case 19: {
	    field = "COMPANY";
	    break;
	}
	case 20: {
	    field = "INDUSTRY";
	    break;
	}
	case 21: {
	    field = "SOURCE";
	    break;
	}
	case 22: {
	    field = "SALES_REP";
	    break;
	}
	case 23: {
	    field = "SALES_STATUS";
	    break;
	}
	case 24: {
	    field = "AREA_CODE";
	    break;
	}
	default:{
	    field = "";
	}

	}//end switch
	return field;
    }

    public static boolean updateListLayout(String listNumber, String layout)
    {
	boolean update= false;
	try{
	    update = dataServer.setLastUploadLayout(listNumber,layout);
	}catch(Exception ex){
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return update;
    }

    public static boolean hasLayoutField(String field, String layout) {
	try {
	    if(layout == null || layout.equals("") || field ==null || field.equals(""))
		return false;
	    StringTokenizer strTok = new StringTokenizer(layout, ",");
	    for (; strTok.hasMoreTokens(); ) {
		String str = strTok.nextToken();
		if (str.equals(field)) {
		    return true;
		}
	    }
	}
	catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return false;
    }


    public static boolean verifyLayoutStr(String fileLayoutStr) {
	if (fileLayoutStr == null || fileLayoutStr.equals("")) {
	    return false;
	}
	if (!fileLayoutStr.startsWith("EMAIL_ADDR")) {
	    return false;
	}
	return true;
    } //()

    public static boolean isBasicField(String field){
	try{
	    for(int i=0;i<basicFields.length;i++){
		if(basicFields[i].equalsIgnoreCase(field))
		    return true;
	    }
	}catch(Exception ex){
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return false;
    }

    public static long createList(String listName, String userId, HttpSession session){
	long listNumber = 0l;
	try{
	    //checking if Trial List's limit
	    UserInfo userInfo = UserManager.getUserInfo(userId);
	    if(userInfo == null || (userInfo.getPackageType().equals(PMSDefinitions.PACKAGE_TYPE_TRIAL) && isTrialLimitExceed(userId)))
		return 0l;

	    long currDate = System.currentTimeMillis();
	    Timestamp currTime = new Timestamp(currDate);
	    String strCurrDate = currTime.toString();

	    //Create List
	    ListDataBean listDataBean = new ListDataBean();
	    listDataBean.setUserID(userId);
	    listDataBean.setName(listName);
	    listDataBean.setStatus(PMSDefinitions.LIST_STATUS_ACTIVE);
	    listDataBean.setUserIDCreated(userId);
	    listDataBean.setCreationDate(new Timestamp(System.currentTimeMillis()));
	    listDataBean.setLocked(PMSDefinitions.LOCK_FALSE);

	    listDataBean = dataServer.createList(listDataBean);
	    if(listDataBean == null)
		return listNumber;

	    //Adding in crurrent List Vectors
	    Vector listsVector = (Vector) session.getAttribute("AllLists");
	    Vector listVec = new Vector();

	    listVec.add(""+listDataBean.getListNumber());
	    listVec.add(""+listName);
	    listVec.add("0");
	    listVec.add("N");
	    listVec.add(""+listDataBean.getAccessType());
	    listVec.add(""+PMSDefinitions.LIST_STATUS_ACTIVE);
	    listVec.add(""+strCurrDate.substring(0,strCurrDate.indexOf(" ")));
	    if(listsVector!=null)
		listsVector.add(0,listVec);
	    listsVector = (Vector) session.getAttribute("PMSMainPageBean.listsSubscriberCount");
	    if(listsVector!=null)
		listsVector.add(0,listVec);

	    // Adding in ListVec
	    Vector ListVec = (Vector) session.getAttribute("ListVec");
	    if(ListVec!=null)
		ListVec.add(listDataBean);
	    listNumber = listDataBean.getListNumber();
	}catch(Exception ex){
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return listNumber;
    }

    public static int varifyListName(String userId, String listName){
	try{
	    if(listName==null || listName.trim().equals(""))
		return 1;
	    if(listName.trim().toLowerCase().startsWith(PMSDefinitions.SUPRESS_LIST_NAME.toLowerCase()))
		return 2;
	    ListDataBean listDataBean = dataServer.getList(userId, listName);
	    if(listDataBean!=null && listDataBean.getListNumber()>0)
		return 3;
	    if(listName.indexOf("\"")>=0)
		return 4;

	    return 0;
	}catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 5;
    }

    public static String getListName(long listNumber) {
	try {
	    ListDataBean list = dataServer.getList(listNumber);
	    if(list == null)
		return null;
	    return list.getName();
	} catch (Exception e) {
	    WebServerLogger.getLogger().log(e);
	    e.printStackTrace();
	}
	return null;
    }

    public static void main(String[] args) {
    }

    /**
     *
     * @param listVec
     * @return
     */
    public static Vector orderListsByName(Vector listVec, boolean inAscendingOrder) {

	Vector vec = new Vector();
	try {
	    Vector nameVec = new Vector();
	    Hashtable listHash = new Hashtable();
	    for(int i=0; i<listVec.size(); i++) {
		ListDataBean lb = (ListDataBean)listVec.get(i);
		nameVec.add(lb.getName());
		listHash.put(lb.getName(), lb);
	    }
	    Collections.sort(nameVec);

	    if(inAscendingOrder) {
		for(int i=0; i<nameVec.size(); i++) {
		    String name = (String)nameVec.get(i);
		    vec.add(listHash.get(name));
		}
	    } else {
		for(int i=(nameVec.size()-1); i>=0; i--) {
		    String name = (String)nameVec.get(i);
		    vec.add(listHash.get(name));
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
     * @param listVec
     * @param inAscendingOrder
     * @return
     */
    public static Vector orderListsByCreationDate(Vector listVec, boolean inAscendingOrder) {

	Vector vec = new Vector();
	try {
	    Vector timeVec = new Vector();
	    Hashtable listHash = new Hashtable();
	    for(int i=0; i<listVec.size(); i++) {
		ListDataBean lb = (ListDataBean)listVec.get(i);
		NameValue nv = new NameValue(""+((lb.getCreationDate()==null)? System.currentTimeMillis(): lb.getCreationDate().getTime()), ""+lb.getListNumber());
		timeVec.add(nv);
		listHash.put(""+lb.getListNumber(), lb);
	    }
	    Collections.sort(timeVec);

	    if(inAscendingOrder) {
		for(int i=0; i<timeVec.size(); i++) {
		    NameValue nv = (NameValue)timeVec.get(i);
		    vec.add(listHash.get(nv.getValue()));
		}
	    } else {
		for(int i=(timeVec.size()-1); i>=0; i--) {
		    NameValue nv = (NameValue)timeVec.get(i);
		    vec.add(listHash.get(nv.getValue()));
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
     * @param listVec
     * @param inAscendingOrder
     * @return
     */
    public static Vector orderListsBySubscriberCount(Vector listVec, boolean inAscendingOrder) {

	Vector vec = new Vector();
	try {
	    Vector countVec = new Vector();
	    Hashtable listHash = new Hashtable();
	    for(int i=0; i<listVec.size(); i++) {
		ListDataBean lb = (ListDataBean)listVec.get(i);
		NameValue nv = new NameValue(""+lb.getSubscriberCount(), ""+lb.getListNumber());
		countVec.add(nv);
		listHash.put(""+lb.getListNumber(), lb);
	    }
	    Collections.sort(countVec);

	    if(inAscendingOrder) {
		for(int i=0; i<countVec.size(); i++) {
		    NameValue nv = (NameValue)countVec.get(i);
		    vec.add(listHash.get(nv.getValue()));
		}
	    } else {
		for(int i=(countVec.size()-1); i>=0; i--) {
		    NameValue nv = (NameValue)countVec.get(i);
		    vec.add(listHash.get(nv.getValue()));
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
     * @param campaignNumber
     * @return
     */
    public static Vector getCampaignLists(long campaignNumber) {

	try {
	    return dataServer.getCampaignLists(campaignNumber);

	} catch (Exception ex) {
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return new Vector();
    }

    /**
     *
     * @param listNumber
     * @return
     */
    public static ListDataBean getList(long listNumber){
	try{
	    return dataServer.getList(listNumber);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new ListDataBean();
    }
    /**
     * @param listNumVec
     * @return
     */
    public static Vector getLists(Vector listNumVec){
	try{
	    return dataServer.getList(listNumVec);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }

    /**
     *
     * @param userID
     * @param listName
     * @return
     */
    public static ListDataBean getList(String userID, String listName){
	try{
	    return dataServer.getList(userID,listName);
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new ListDataBean();
    }
    /**
     * 
     * @param userID
     * @param listName
     * @return
     */
    public static boolean isListExists(String userID, String listName, long listNum) {

	try{
	    return dataServer.isListExists(userID, listName, listNum);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }
    /**
     * 
     * @param userID
     * @param listNum
     * @param newName
     * @return
     */
    public static boolean renameList(String userID, long listNum, String newName) {

	try{
	    return dataServer.renameList(userID, listNum, newName);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }

    /**
     *
     * @param listName
     * @param userId
     * @return
     */
    public static long createList(String listName, String userId){
	long listNumber = 0l;
	try{
	    //checking if Trial List's limit
	    UserInfo userInfo = UserManager.getUserInfo(userId);
	    if(userInfo == null || (userInfo.getPackageType().equals(PMSDefinitions.PACKAGE_TYPE_TRIAL) && isTrialLimitExceed(userId)))
		return 0l;

	    long currDate = System.currentTimeMillis();
	    Timestamp currTime = new Timestamp(currDate);
	    String strCurrDate = currTime.toString();

	    //Create List
	    ListDataBean listDataBean = new ListDataBean();
	    listDataBean.setUserID(userId);
	    listDataBean.setName(listName);
	    listDataBean.setStatus(PMSDefinitions.LIST_STATUS_ACTIVE);
	    listDataBean.setUserIDCreated(userId);
	    listDataBean.setCreationDate(new Timestamp(System.currentTimeMillis()));
	    listDataBean.setLocked(PMSDefinitions.LOCK_FALSE);

	    listDataBean = dataServer.createList(listDataBean);
	    if(listDataBean == null)
		return listNumber;

	    return listDataBean.getListNumber();
	} catch(Exception ex){
	    WebServerLogger.getLogger().log(ex);
	    ex.printStackTrace();
	}
	return listNumber;
    }
    /**
     * 
     * @param listNumber
     * @param status
     * @return
     */
    public static Vector countListRespondersPercentage(long listNumber, String status) {

	try {
	    return dataServer.countListRespondersPercentage(listNumber, status);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     *
     * @param listNumber
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector getSubsByListNum(long listNumber, int offset, int bucket,String status) {

	try {
	    return dataServer.getSubsByListNum(listNumber, offset, bucket,status);
	    
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     * 
     * @param listNumber
     * @param offset
     * @param bucket
     * @param status
     * @param searchTxt
     * @return
     */
    public static Vector getSubsByListNum(long listNumber, int offset, int bucket,String status, String searchTxt) {

	try{
	    return dataServer.getSubsByListNum(listNumber, offset, bucket,status, searchTxt);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     *
     * @param listNumber
     * @param status
     * @return
     */
    public static int countSubsByListNum(long listNumber, String status){

	try{
	    return dataServer.countSubsByListNum(listNumber,status);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;
    }
    /**
     * 
     * @param listNumber
     * @param status
     * @param searchTxt
     * @return
     */
    public static int countSubsByListNum(long listNumber, String status, String searchTxt){

	try{
	    return dataServer.countSubsByListNum(listNumber,status, searchTxt);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;
    }

    /**
     * 
     * @param listNumber
     * @param isExplicit
     * @param activityLog
     * @return
     */
    public static boolean deleteList(long listNumber, boolean isExplicit, ActivityLog_CC activityLog) {
	return deleteList(""+listNumber, isExplicit, activityLog);
    }
    /**
     *
     * @param listNumber
     * @param isExplicit
     * @param activityLog
     * @return
     */
    public static boolean deleteList(String listNumber, boolean isExplicit, ActivityLog_CC activityLog) {

	if(Default.defaultLong(listNumber)<=0)
	    return false;

	try {
	    DeleteListTask delTask = new DeleteListTask(listNumber, isExplicit, activityLog);
	    TaskManager.getInstance(5).scheduleTask(delTask);
	    return true;

	} catch(Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }

    /**
     *
     * @param userId
     * @param listNumber
     * @return
     */
    public static int deleteListSubscription(String userId, long listNumber){

	if(Default.toDefault(userId).equals("") || listNumber <=0 )
	    return 0;

	try{
	    return complexQueryServer.deleteListSubscription(userId,listNumber);

	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return 0;
    }

    /**
     *
     * <p>Title: </p>
     * <p>Description: </p>
     * <p>Copyright: Copyright (c) 2012</p>
     * <p>Company: </p>
     * @author Ahmad Suhaib.
     * @version 1.0
     */
    private static class DeleteListTask extends TimerTask {

	String listNumber="";
	boolean isExplicity;
	ActivityLog_CC activityLog;

	public DeleteListTask(String num, boolean bool, ActivityLog_CC log) {
	    listNumber = num;
	    isExplicity = bool;
	    activityLog = log;
	}

	public void run() {

	    try {
		EJBHomesFactory.getComplexQueryServerRemote().deleteList(listNumber, isExplicity, activityLog);
		System.out.println("[ListManager.innerClass]:   LIST_DELETE_SUCCESSFULLY   listNum:"+listNumber);

	    } catch(Exception ex) {
		ex.printStackTrace();
		WebServerLogger.getLogger().log(ex);
	    } finally {
		TaskManager.getInstance(0).removeTask(this);
	    }
	}
    }

    /**
     *
     * @param userId
     * @return
     */
    public static int getListCount(String userId){
	if(Default.toDefault(userId).equals(""))
	    return 0;
	try{
	    return alphaServer.getListCount(userId);

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
    private static boolean isTrialLimitExceed(String userId){
	try{
	    return getListCount(userId) >= TrialAccountManager.LIST_LIMIT;
	} catch(Exception ex){
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return false;
    }

}


