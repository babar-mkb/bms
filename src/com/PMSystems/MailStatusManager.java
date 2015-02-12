package com.PMSystems;

import java.util.*;
import java.io.*;

import com.PMSystems.*;
import com.PMSystems.sejbs.*;
import com.PMSystems.mail.*;
import com.PMSystems.util.*;

import org.apache.log4j.*;
/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Suhaib.
 * @version 1.0
 */
public class MailStatusManager extends TimerTask {

    private static Logger logger = null;
    private static MailStatusManager theInstance;

    private static Vector canceledMailsVec = new Vector();
    private static Vector failedMailsVec = new Vector();
    private static Vector successMailsVec = new Vector();
    private static Vector queuedMailsVec = new Vector();

    //private static TaskManager taskExecutor = TaskManager.createNewInstance(1);

    private static String category = "";
    private static MailQueue queue;

    private static String SUCCESS_NUM_FILE = PMSDefinitions.RESOURCE_DIR+"mailSuccess.dat";
    private static String FAILED_NUM_FILE = PMSDefinitions.RESOURCE_DIR+"mailFailed.dat";
    private static String CANCELED_NUM_FILE = PMSDefinitions.RESOURCE_DIR+"mailCanceled.dat";
    private static String QUEUED_NUM_FILE = PMSDefinitions.RESOURCE_DIR+"mailQueued.dat";

    static { //import org.apache.log4j.*;
	logger = Logger.getLogger(MailStatusManager.class);
    }

    private static ComplexQueryServer complexQueryServer = null;
    private static ComplexQueryServer2 complexQueryServer2 = null;
    private static BMSQueryServer bmsQueryServer = null;
    static {
	complexQueryServer = EJBHomesFactory.getComplexQueryServerRemote();
	complexQueryServer2 = EJBHomesFactory.getComplexQueryServer2Remote();
	bmsQueryServer = EJBHomesFactory.getBMSQueryServerRemote();
    }

    /**
     *
     */
    private MailStatusManager(String cat, MailQueue que) throws Exception {

	category = cat;
	queue = que;

	SUCCESS_NUM_FILE = PMSDefinitions.RESOURCE_DIR+"mailSuccess_"+category+".dat";
	FAILED_NUM_FILE = PMSDefinitions.RESOURCE_DIR+"mailFailed_"+category+".dat";
	CANCELED_NUM_FILE = PMSDefinitions.RESOURCE_DIR+"mailCanceled_"+category+".dat";
	QUEUED_NUM_FILE = PMSDefinitions.RESOURCE_DIR+"mailQueued_"+category+".dat";

	logger.info("[Mail System] Initializing MailStatusManager ... category:"+cat);
	logger.info("[MSM] File Names: "+SUCCESS_NUM_FILE+", "+FAILED_NUM_FILE+", "+CANCELED_NUM_FILE+", "+QUEUED_NUM_FILE);

	Vector doneVec = new Vector();

	loadExistingNumbers(SUCCESS_NUM_FILE, successMailsVec, false);
	if(successMailsVec.size()>0) {
	    doneVec.addAll(successMailsVec);
	    logger.debug("[MSM] Processing left-over Sent CampTrans count: "+successMailsVec.size());
	    if(updateCampaignTransmission(successMailsVec, PMSDefinitions.CAMPAIGN_TRANSMISSION_STATUS_SENT)) {
		successMailsVec.clear();
		cleanFile(SUCCESS_NUM_FILE);
	    }
	}

	loadExistingNumbers(FAILED_NUM_FILE, failedMailsVec, false);
	if(failedMailsVec.size()>0) {
	    doneVec.addAll(failedMailsVec);
	    logger.debug("[MSM] Processing left-over Failed CampTrans count: "+failedMailsVec.size());
	    if(updateCampaignTransmission(failedMailsVec, PMSDefinitions.CAMPAIGN_TRANSMISSION_STATUS_FAILED)) {
		failedMailsVec.clear();
		cleanFile(FAILED_NUM_FILE);
	    }
	}

	loadExistingNumbers(CANCELED_NUM_FILE, canceledMailsVec, false);
	if(canceledMailsVec.size()>0) {
	    doneVec.addAll(canceledMailsVec);
	    logger.debug("[MSM] Processing left-over Canceled CampTrans count: "+canceledMailsVec.size());
	    if(updateCampaignTransmission(canceledMailsVec, "C")) {
		canceledMailsVec.clear();
		cleanFile(CANCELED_NUM_FILE);
	    }
	}

	loadExistingNumbers(QUEUED_NUM_FILE, queuedMailsVec, true);
	logger.debug("[MSM] ****** Loaded Queue Emails count: "+queuedMailsVec.size()+" *******");

	if(queuedMailsVec.size()>0)
	    removeFromQueue(doneVec);

	if(queuedMailsVec.size()>0) {
	    logger.debug("[MSM] ****** Mark Queued Emails as Pending, count: "+queuedMailsVec.size()+" *******");
	    if(updateCampaignTransmission(convertInToNameValueVec(queuedMailsVec), "P")) {
		queuedMailsVec.clear();
		cleanFile(QUEUED_NUM_FILE);
	    }
	}

	/**
	 * Starts calling run() method repitatively in 20sec intervals, starting in 5sec.
	 */
	new Timer().schedule(this, 5*1000l);
	logger.info("[Mail System] MailStatusManager Initialized with mail-status updation interval=20sec !!!");
    }
    /**
     *
     * @param queuedMailsVec
     * @return
     */
    private static Vector convertInToNameValueVec(Vector queuedMailsVec) {

	Vector nvVec = new Vector();
	for(int i=0; i<queuedMailsVec.size(); i++) {
	    String str = (String)queuedMailsVec.get(i);

	    if(str.indexOf(",")!=-1) {
		String campNum = Default.toDefault(str.substring(0, str.indexOf(","))).trim();
		String subNum = Default.toDefault(str.substring(str.indexOf(",")+1, str.length())).trim();
		if(Default.defaultLong(campNum)>0 && Default.defaultLong(subNum)>0)
		    nvVec.add(new NameValue(campNum, subNum));
	    }
	}
	return nvVec;
    }

    /**
     *
     * @return
     */
    public static synchronized MailStatusManager getInstance(String category) throws Exception  {

	if(theInstance==null) {
	    theInstance = new MailStatusManager(category, new MailQueue(1));
	}
	return theInstance;
    }

    public static synchronized MailStatusManager getInstance(String category, MailQueue queue) throws Exception  {

	if(theInstance==null) {
	    theInstance = new MailStatusManager(category, queue);
	}
	return theInstance;
    }

    public static synchronized MailStatusManager getInstance() throws Exception  {
	return getInstance("");
    }

    /**
     *
     * @param campTransNumber
     */
    public static void addToSuccess(long campNum, long subNum) {

	synchronized(successMailsVec) {
	    successMailsVec.add(new NameValue(""+campNum, ""+subNum));
	    saveData(SUCCESS_NUM_FILE, campNum+","+subNum);
	}
    }

    /**
     *
     * @param campTransNumber
     */
    public static void addToFailure(long campNum, long subNum) {

	synchronized(failedMailsVec) {
	    failedMailsVec.add(new NameValue(""+campNum, ""+subNum));
	    saveData(FAILED_NUM_FILE, campNum+","+subNum);
	}
    }

    /**
     *
     * @param campTransNumber
     */
    public static void addToCancel(long campNum, long subNum) {

	synchronized(canceledMailsVec) {
	    canceledMailsVec.add(new NameValue(""+campNum, ""+subNum));
	    saveData(CANCELED_NUM_FILE, campNum+","+subNum);
	}
    }
    /**
     *
     * @param campTransNumber
     */
    public static void addToQueue(long campNum, long subNum) {

	if(campNum<=0 || subNum<=0)
	    return;

	addToQueue(Default.toVector(campNum+","+subNum));
    }
    /**
     *
     * @param numVec
     */
    public static void addToQueue(Vector numVec) {

	if(numVec.isEmpty())
	    return;

	synchronized(queuedMailsVec) {

	    StringBuffer buff = new StringBuffer();
	    for(int i=0; i<numVec.size(); i++) {
		queuedMailsVec.add((String)numVec.get(i));

		buff.append((String)numVec.get(i));
		buff.append("\n");
	    }
	    saveData(QUEUED_NUM_FILE, buff.toString().trim());
	}
    }

    public static void removeFromQueue(long campNum, long subNum) {
	removeFromQueue(Default.toVector(new NameValue(""+campNum, ""+subNum)));
    }
    /**
     *
     * @param campNum
     * @param subNum
     */
    public static void removeFromQueue(Vector numVec) {

	synchronized(queuedMailsVec) {

	    boolean isRemoved = false;
	    for(int k=0; k<numVec.size(); k++) {

		NameValue nv = (NameValue)numVec.get(k);
		if(queuedMailsVec.remove(nv.getName()+","+nv.getValue())) {
		    isRemoved = true;
		}
	    }

	    if(isRemoved) {
		cleanFile(QUEUED_NUM_FILE);
		StringBuffer buff = new StringBuffer();

		for(int i=0; i<queuedMailsVec.size(); i++) {
		    buff.append((String)queuedMailsVec.get(i));
		    buff.append("\n");
		}

		if(queuedMailsVec.size()>0)
		    saveData(QUEUED_NUM_FILE, buff.toString().trim());
	    }
	}
    }

    /**
     *
     * @param campaignTransmissionNumber
     * @param status
     */
    private boolean updateCampaignTransmission(Vector copySubNVVec, String status) {

	if(copySubNVVec.isEmpty())
	    return true;

	try {
	    Hashtable hash = new Hashtable();
	    for(int i=0; i<copySubNVVec.size(); i++) {
		NameValue nv = (NameValue)copySubNVVec.get(i);
		if(!hash.containsKey(nv.getName())) {
		    hash.put(nv.getName(), Default.toVector(nv.getValue()));
		} else {
		    ((Vector)hash.get(nv.getName())).add(nv.getValue());
		}
	    }
	    return complexQueryServer.updateCampaignTransmissionAndCampaignSentCount(hash, status);

	} catch(Exception e) {
	    logger.error("", e);
	}
	return false;
    }


    /**
     *
     */
    public void run() {

	try {
	    while(true) {

		try {
		    synchronized(queue) {
			
			if(successMailsVec.size()>0) {
			    logger.debug("[MSM - 20s] Processing Sent CampTrans count: "+successMailsVec.size());
			    processData(SUCCESS_NUM_FILE, successMailsVec,PMSDefinitions.CAMPAIGN_TRANSMISSION_STATUS_SENT);
			}

			if(failedMailsVec.size()>0) {
			    logger.debug("[MSM - 20s] Processing Failed CampTrans count: "+failedMailsVec.size());
			    processData(FAILED_NUM_FILE, failedMailsVec, PMSDefinitions.CAMPAIGN_TRANSMISSION_STATUS_FAILED);
			}

			if(canceledMailsVec.size()>0) {
			    logger.debug("[MSM - 20s] Processing Canceled CampTrans count: "+canceledMailsVec.size());
			    processData(CANCELED_NUM_FILE, canceledMailsVec,"C");
			}
		    }
		    Thread.sleep(Default.MILLIES_FOR_ONE_SECOND*20);

		} catch(Throwable e) {
		    logger.error("", e);
		}

	    }

	} catch(Exception e) {
	    logger.error("", e);

	} finally {
	    logger.debug("[MSM] Mailing Status Manager *** CRASHED ***    EXIST_PROCESS");
	    System.out.println("\n\n\n**********\n\n [MSM] Mailing Status Manager *** CRASHED ***    EXIST_PROCESS \n\n*********\n");
	    
	    System.exit(1);//*****
	}
    }
    /**
     *
     * @param fileName
     * @param dataVec
     * @param status
     */
    private void processData(String fileName, Vector dataVec, String status) {

	Vector copyVec = new Vector();

	if(dataVec.size()>0) {
	    synchronized(dataVec) {
		copyVec.addAll(dataVec);
		cleanFile(fileName);
		dataVec.clear();
	    }


	    //Re-organize data if not processed successfully.
	    if(!updateCampaignTransmission(copyVec, status)) {
		StringBuffer buff = new StringBuffer();
		synchronized(dataVec) {
		    for (int i=0; i<copyVec.size(); i++) {
			NameValue nv = (NameValue)copyVec.get(i);
			buff.append(nv.getName()+","+nv.getValue());
			buff.append("\n");
			dataVec.add(nv);
		    }
		    saveData(fileName, buff.toString().trim());
		}

	    } else {
		//==== Empty Queue, If Processed Successfully ====
		removeFromQueue(copyVec);
	    }
	}
    }

    /**
     * Loads campaignTranmissionNumbers that were left when this mailing process stopped.
     *
     * @param fileName
     * @return
     */
    private void loadExistingNumbers(String fileName, Vector campSubNVVec, boolean isQueue) {

	try {
	    File file = new File(fileName);
	    if (file.isFile()) {

		BufferedReader in = new BufferedReader(new FileReader(file));
		String str = null;
		while((str = in.readLine()) != null) {

		    if(str.indexOf(",")!=-1) {

			String campNum = Default.toDefault(str.substring(0, str.indexOf(","))).trim();
			String subNum = Default.toDefault(str.substring(str.indexOf(",")+1, str.length())).trim();
			if(Default.defaultLong(campNum)>0 && Default.defaultLong(subNum)>0) {
			    if(isQueue) {
				campSubNVVec.add(str);
			    } else {
				campSubNVVec.add(new NameValue(campNum, subNum));
			    }
			}
		    }
		}
		in.close();

	    } else
		file.createNewFile();
	} catch (Exception ex) {
	    logger.error("", ex);
	}
    }

    /**
     * Writes in-memory data to file, for preserving it in case process gets killed.
     *
     * @param fileName
     * @param data
     */
    private static void saveData(String fileName, String data) {
	try {
	    data += "\n";
	    FileOutputStream writer = new FileOutputStream(fileName, true);
	    writer.write(data.getBytes());
	    writer.close();
	} catch (Exception ex) {
	    logger.error("", ex);
	}
    }

    /**
     * Clean backup data from file on successful processing of in-memory data.
     * @param fileName
     */
    private static void cleanFile(String fileName) {
	try {
	    File file = new File(fileName);
	    if(file.exists()) {
		if(file.delete()) {
		    file.createNewFile();
		}
	    } else
		file.createNewFile();
	} catch (Exception ex) {
	    logger.error("", ex);
	}
    }

    /**
     * @param campTransNumVec
     * @return

    private void updateAutoCampaignsSentCount(Vector campTransNumVec) {

        try {
            //Update Trigger Track Sent Count
            complexQueryServer2.updateTriggerTracksSentCount(campTransNumVec);

            //Update Workflow Sent Count
            bmsQueryServer.updateWorkflowsSentCount(campTransNumVec);

            updateEvergreenSentCount(Statement stat, long campaignNumber, int sentCount) {

        } catch(Exception e) {
            logger.error("", e);
        }
    }*/

    /**
     * @param campaignTransmissionNumber
     * @param status
    private boolean markSubscriberActiveByCampaignTransmissionNumber(Vector cTNoVec) {
        try {
            complexQueryServer.markSubscriberActiveByCampaignTransmissionNumber(cTNoVec);
        } catch(Exception e) {
            logger.error("", e);
            return false;
        }
        return true;
    }*/

}