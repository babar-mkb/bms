package com.PMSystems;

import java.io.*;
import java.util.*;

import com.PMSystems.dbbeans.*;
import com.PMSystems.util.*;
import com.PMSystems.sejbs.*;
import com.PMSystems.logger.*;



/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: </p>
 * @author Ahmad Suhaib
 * @version 1.0
 */

public class LoginHackManager {

    private static Hashtable userHash = new Hashtable();
    private static Hashtable userBlockHash = new Hashtable();

    //Max successive failed attempts allowed.
    private static final int MAX_SUCCCESSIVE_FAILED_ATTEMPTS = 5;
    //Timespan during which failed attempts adds up.
    private static final long SUCCCESSIVE_FAILED_ATTEMPTS_TIMESPAN = 1000l*60*5;//5mins
    //Duration to block a user login, after max. successive failed attempts reached.
    private static final long LOGIN_BLOCK_DURATION = 1000l*60*5;//5mins

    private static void debug(String str) {
        System.out.println(str);
        AppServerLogger.getLogger().log(new LogEntry("", "", str));
    }
    private static void error(String str) { AppServerLogger.getLogger().log(new LogEntry("", "", str)); }
    private static void error(String str, Throwable th) { AppServerLogger.getLogger().log(th); }

    /**
     * Remove any failed attempts log, for a given userId, after successful login.
     *
     * USE this method with isUserBlocked();
     * @param userId
     */
    public static void logSuccessfulLogin(String userId) {

        synchronized(userHash) {
            if(userHash.containsKey(""+userId)) {
                userHash.remove(userId);
            }
        }
    }

    /**
     * Log failed attempt, increment successive failed attempt counter.
     * Also if failed attempts adds up to MAX allowed, block the userId
     * for further login attempts for selected time period.
     *
     * @param userId
     */
    public static void logFailedAttempt(String userId) {

        synchronized(userHash) {

            if(userHash.containsKey(userId)) {
                NameValuePair nv = (NameValuePair)userHash.get(userId);
                Date firstFailedAttemptDate = (Date)nv.getValue();

                //check time, since first failed attempt was made, has crossed SUCCCESSIVE_FAILED_ATTEMPTS_TIMESPAN
                if((System.currentTimeMillis()-firstFailedAttemptDate.getTime()) >= SUCCCESSIVE_FAILED_ATTEMPTS_TIMESPAN) {
                    userHash.remove(userId);//remove stale log entry
                    userHash.put(userId, new NameValuePair("1", new Date()));//create new entry

                } else {
                    int counter = Default.defaultInt(nv.getName())+1;//update counter
                    //check if MAX failed attempts has been reached.
                    if(counter>=MAX_SUCCCESSIVE_FAILED_ATTEMPTS) {
                        //block user with timestamp for further login attempts
                        Date unblockDate = new Date(System.currentTimeMillis()+LOGIN_BLOCK_DURATION);
                        userBlockHash.put(userId, unblockDate);

                        /**
                         * remove failed logs, assuming login attempts will first check if user is block or not.
                         */
                        userHash.remove(userId);
                        debug("[LoginHackManager]: UserId: "+userId+" has BLOCKED blocked [untill: "+unblockDate+"] after "
                             +MAX_SUCCCESSIVE_FAILED_ATTEMPTS+" failed attempts.");

                    } else {
                        debug("[LoginHackManager]: UserId:"+userId+", FailedAttempts:"+counter);
                        nv.setName("" + counter); //update counter
                    }
                }

            } else {
                //create new entry
                debug("[LoginHackManager]: UserId:"+userId+", FailedAttempts:1");
                userHash.put(userId, new NameValuePair("1", new Date()));
            }
        }
    }

    /**
     * Returns true, if userId is blocked due to excessive failed attempts
     * @param userId
     * @return
     */
    public static boolean isUserBlocked(String userId) {

        synchronized(userBlockHash) {
            if(userBlockHash.containsKey(userId)) {
                if(((Date)userBlockHash.get(userId)).getTime()<System.currentTimeMillis()) {
                    userBlockHash.remove(userId);
                    return false;
                } else {
                    debug("[LoginHackManager]: UserId: "+userId+" BLOCKED untill: "+((Date)userBlockHash.get(userId)));
                    return true;
                }
            }
        }
        return false;
    }

}