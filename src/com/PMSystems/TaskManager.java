package com.PMSystems;

/**
 * <p>Title: </p>
 * <p>Description: A Singleton Class.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author Suhaib.
 * @version 1.0
 */
import java.util.*;
import com.PMSystems.util.*;
import org.apache.log4j.*;
import com.PMSystems.mail.smtp.*;
import com.PMSystems.SFIntegration.*;

public class TaskManager {

    private Vector tasksPool = new Vector();
    private Vector timerPool = new Vector();
    private Vector waitPool = new Vector();

    private Hashtable campMailStatHash = new Hashtable();
    private SMTPServer[] smtpServers;

    private Hashtable hash = new Hashtable();
    private Hashtable tasksTimerHash = new Hashtable();
    private Hashtable timerSMTPHash = new Hashtable();
    private static TaskManager theInstance;

    public static Logger logger = null;
    static {
	logger = Logger.getLogger(TaskManager.class);
    }

    private TaskManager(int INIT_COUNT) {

	//Filling-up the pool.
	logger.info("[TaskManager()] Initializing TaskManager with threadCount="+INIT_COUNT);
	for(int i=0; i<INIT_COUNT; i++) {
	    Timer timer = new Timer();
	    timerPool.add(timer);
	    hash.put(timer, "0");
	}
	logger.info("[TaskManager()] TaskManager Initialized !!! ");
    }

    /**
     *
     * @return TaskManager
     */
    public static synchronized TaskManager getInstance(int threadCount) {

	if(theInstance==null) {
	    theInstance = new TaskManager(threadCount);
	}
	return theInstance;
    }
    /**
     * Create New Instance (in contrast to getInstance() singleton method) of TaskManager every time its called. 
     * 
     * @param threadCount
     * @return
     */
    public static TaskManager createNewInstance(int threadCount) {
	return new TaskManager(threadCount);
    }

    public int getTaskCount() {
	return tasksPool.size();
    }

    public Vector getTasksPool() {
	return tasksPool;
    }

    public int getWaitPoolSize() {
	int count = tasksTimerHash.size();
	count += waitPool.size();
	return count;
    }

    public int getInProcessPoolSize() {
	return tasksTimerHash.size();
    }

    /**
     * 
     * @return
     */
    public Vector getWaitPool() {
	
	Vector taskVec = new Vector();
	//==== Collect running tasks ====
	synchronized (tasksTimerHash) {
	    taskVec.addAll(Default.toKeysVector(tasksTimerHash));
	}
	//==== Collect queued tasks ====
	taskVec.addAll(waitPool);
	
	return taskVec;//return all running & queued tasks.
    }

    public boolean isManagerFull() {
	return (tasksPool.size()>=timerPool.size());
    }
    /**
     * Equally spreading out smtp-servers over the given Timer pool.
     * @param smtpServers
     */
    public void setSMTPServers(SMTPServer[] smtp) {
	smtpServers = smtp;
	for(int i=0; i<timerPool.size();) {
	    for(int j=0; j<smtpServers.length && i<timerPool.size(); j++,i++) {
		timerSMTPHash.put((Timer)timerPool.get(i), smtpServers[j]);
		logger.debug(i+"]Timer Assigned ("+smtpServers[j]+")");
	    }
	}
    }

    /**
     * a TimerTask call this method to remove itself from the Timer,
     * and task is marked canceled.
     *
     * @param aTask
     * @return
     */
    public boolean removeTask(TimerTask aTask) {
	if(aTask==null)
	    return false;

	boolean allOk = false;
	synchronized(tasksPool) {
	    if(tasksPool.remove(aTask)) {
		aTask.cancel();
		allOk = true;

	    } else {//For tasks added via scheduleNow() or scheduleTask().
		aTask.cancel();
		allOk = true;
	    }
	}

	if(allOk) {//If task was existing in pool.
	    Timer timer = null;
	    synchronized (tasksTimerHash) {
		timer = (Timer) tasksTimerHash.remove(aTask);
		if(timer!=null) {
		    subtractToBurden(timer);
		}
	    }

	    //==== Assign Task from Waiting Pool, if any eixsts =====
	    synchronized(waitPool) {
		if(timer!=null && waitPool.size()>0 && Default.defaultInt((String)hash.get(timer))==0) {

		    TimerTask nTask = waitPool.size()>0? (TimerTask)waitPool.remove(0): null;//Get first task in waiting pool i.e. FIFO
		    if(nTask!=null)
			assignTask(timer, nTask);
		}
	    }
	}
	return allOk;
    }


    public int getThreadCount() {
	return timerPool.size();
    }

    /**
     *
     * @param task
     * @param delay
     */
    public void scheduleForOnce(TimerTask task, long delay) {

	Timer timer = getLeastBurdenedTimer();
	if(timer==null)
	    return;
	timer.schedule(task, delay);
	synchronized(tasksPool) {
	    tasksPool.add(task);
	}
	synchronized(tasksTimerHash) {
	    tasksTimerHash.put(task, timer);
	}
	SMTPServer smtp = (SMTPServer)timerSMTPHash.get(timer);
	((PostMail)task).setSMTPServer(smtp);

	addToBurden(timer);
    }
    /**
     *
     * @param task
     */
    public void scheduleNow(TimerTask task) {

	Timer timer = getFreeTimer();
	if(timer==null) {
	    synchronized(waitPool) {
		waitPool.add(task);
	    }
	} else {
	    assignTask(timer, task);
	}
    }
    /**
     *
     */
    private void assignTask(Timer timer, TimerTask task) {

	if(timerSMTPHash.size()>0 && task instanceof PostMail) {
	    SMTPServer smtp = (SMTPServer)timerSMTPHash.get(timer);
	    ((PostMail)task).setSMTPServer(smtp);
	}
	/**
	 * IMP: Don't need to add to waitPool as Task can be picked up by another Timer.
	 */

	synchronized(tasksTimerHash) {
	    tasksTimerHash.put(task, timer);
	}

	addToBurden(timer);
	timer.schedule(task, 01);
    }



    /**
     *
     * @param task
     * @param delay
     */
    public void scheduleTask(TimerTask task) {
	scheduleNow(task);

	/*	Timer timer = getLeastBurdenedTimer();
	if(timer==null)
	    return;
	timer.schedule(task, 0l);
	synchronized(tasksPool) {
	    tasksPool.add(task);
	}
	synchronized(tasksTimerHash) {
	    tasksTimerHash.put(task, timer);
	}
	addToBurden(timer);
	 */    
    }


    /**
     * Return's the least burdend Timer.
     * always returns the last Timer obj. in the pool, with min. burden.
     * @return Timer
     */
    private Timer getLeastBurdenedTimer() {
	int leastBurden = 0;
	int leastBurdenedIndex = 0;

	if(timerPool.size()<=0)
	    return null;

	synchronized(hash) {
	    for (int i = 0; i < timerPool.size(); i++) {
		Timer timer = (java.util.Timer) timerPool.get(i);
		int burden = Default.defaultInt((String)hash.get(timer));
		leastBurden = (i==0)? burden: leastBurden;

		if (burden == 0) {//Found min. possible burden.
		    return timer;

		} else if (burden <= leastBurden) {
		    leastBurden = burden;
		    leastBurdenedIndex = i;
		}
	    }
	}
	return (java.util.Timer)timerPool.get(leastBurdenedIndex);
    }
    /**
     *
     * @return
     */
    private Timer getFreeTimer() {

	if(timerPool.size()<=0)
	    return null;

	synchronized(hash) {
	    for (int i = 0; i < timerPool.size(); i++) {
		Timer timer = (java.util.Timer) timerPool.get(i);
		int burden = Default.defaultInt((String)hash.get(timer));
		if(burden==0)
		    return timer;
	    }
	}
	return null;
    }

    /**
     * Update the Timer's burden.
     * @param timer Timer
     */
    private void addToBurden(Timer timer) {

	int currentBurden = 0;
	synchronized(hash) {
	    currentBurden = Default.defaultInt((String)hash.get(timer));
	    hash.put(timer, ""+(currentBurden+1));
	}
    }

    private void subtractToBurden(Timer timer) {

	synchronized(hash) {
	    int currentBurden = Default.defaultInt((String)hash.get(timer));
	    hash.put(timer, ""+(currentBurden-1));
	}
    }

    public void print() {
	logger.debug("\n\n============== TaskManager Details ==============");
	synchronized(hash) {
	    for (int i = 0; i < timerPool.size(); i++) {
		logger.debug(i + "]: Burden: " +Default.defaultInt((String)hash.get((java.util.Timer)timerPool.get(i)))+", SMTP: "+ (SMTPServer)timerSMTPHash.get((java.util.Timer)timerPool.get(i)) );
	    }
	}
    }


    public void printSynchTransactions() {
	System.out.println("\n\n============== TaskManager SynchTransaction Details ==============");
	//Itrate tasksTimerHash
	Enumeration sEnum = tasksTimerHash.keys();
	while(sEnum.hasMoreElements()){
	    SynchTransactionDispenser synchTransaction = (SynchTransactionDispenser)sEnum.nextElement();
	    if(!synchTransaction.isRunning)
		System.out.println("(TASK MANG) ===["+tasksTimerHash.get(synchTransaction)+"]==Scheduled Transaction is in Waiting state == "+synchTransaction.schId);
	}

	for (int i = 0; i < timerPool.size(); i++) {
	    System.out.println(i + "]: Burden: " +Default.defaultInt((String)hash.get((java.util.Timer)timerPool.get(i))));
	}


    }



    public void printTasksDetails() {
	logger.debug("\n\n============== Tasks Details ==============");
	for (int i = 0; i < timerPool.size(); i++) {
	    Timer timer = (Timer)timerPool.get(i);
	    SMTPServer smtp = (SMTPServer)timerSMTPHash.get(timer);
	    PostMail postMail = null;
	    Enumeration keys = tasksTimerHash.keys();
	    while(keys.hasMoreElements()) {
		Object obj = keys.nextElement();
		if(((Timer)tasksTimerHash.get(obj)).equals(timer)) {
		    postMail = (PostMail)obj;
		    break;
		}
	    }

	    String toAddresses = "";
	    String subsNumber = "";//postMail.getCurrentMail().getSubscriberNumber()
	    String campNumber = "";//postMail.getCurrentMail().getCampaignNumber()
	    long delay = 0;
	    String mailServer = "";
	    long stuckTime=0;
	    long sendDelay=0;
	    int cursorPos=0;

	    if(postMail!=null && postMail.getCurrentMail()!=null && postMail.getCurrentMail().getTo()!=null) {
		toAddresses = Default.toCSV(postMail.getCurrentMail().getTo());
		subsNumber = ""+postMail.getCurrentMail().getSubscriberNumber();
		campNumber = ""+postMail.getCurrentMail().getCampaignNumber();
		delay = postMail.getSessionDelay();
		stuckTime = postMail.getStuckTime();
		sendDelay= postMail.getTransportDelay();
		cursorPos = postMail.getCursorPostion();
		mailServer = (smtp!=null)? ""+smtp: "n/a";
	    }

	    logger.debug(i + "]:SMTP:" +mailServer+", ("+campNumber+") TargetEmail:"+toAddresses+",\t sessionDelay:"+delay+"ms, stuckTime:"+stuckTime
		    +"ms, sendDelay:"+sendDelay+"ms, cursor:#"+cursorPos);
	}
	logger.debug("\n\n==============end==============");
    }

    public void setCampaignMailStat(String campaignNumber, String smtp) {

	if(smtpServers==null)
	    return;

	try {

	    synchronized(campMailStatHash) {
		if (!campMailStatHash.containsKey(campaignNumber)) {
		    Vector vec = new Vector();
		    campMailStatHash.put(campaignNumber, vec);
		    for (int i = 0; i < smtpServers.length; i++)
			vec.add("0");
		}

		Vector vec = (Vector) campMailStatHash.get(campaignNumber);
		int index = -1;
		for (int i = 0; i < smtpServers.length; i++) {
		    if (smtpServers[i].toString().equals(smtp)) {
			index = i;
			break;
		    }
		}
		if(index<0)
		    return;
		vec.setElementAt(""+(Default.defaultInt((String)vec.get(index))+1), index);
	    }

	} catch (Exception ex) {
	}
    }

    public void printCampaignMailStat() {

	Enumeration keys = campMailStatHash.keys();
	if(keys==null || !keys.hasMoreElements())
	    System.out.println("==== No Campaign stats entered yet. ===");

	while(keys!=null && keys.hasMoreElements()) {
	    String campaignNumber = (String)keys.nextElement();
	    Vector vec = (Vector)campMailStatHash.get(campaignNumber);
	    System.out.print("\n=== CampaignNumber#"+campaignNumber);
	    for (int i = 0; i < smtpServers.length; i++) {
		System.out.print(", "+smtpServers[i].toString()+"#"+( (i<vec.size())? ""+(String)vec.get(i): "0"));
	    }
	    System.out.print("===\n");
	}
    }
}
