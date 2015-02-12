package com.PMSystems;

import java.util.*;
import java.net.*;
import java.io.*;

import com.PMSystems.sejbs.*;
import com.PMSystems.util.*;
import com.PMSystems.logger.*;
import com.PMSystems.beans.*;
import com.PMSystems.dbbeans.*;


/**
 * Returns Master SubId for an existing SubId in case of following events:
 *
 * BouncedCampaign, Click Article, Open Campaign, Abuse Email, UnSubscriber, Supress, TellAFriend, UpdateSubscriberProfile, Opt-InLists
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: </p>
 * @author Suhaib
 * @version 1.0
 */

public class SubIDManager {

    private static long MaxID = 0;
    private static DataQueryServer dataServer;

    static{
        try {
            dataServer = EJBHomesFactory.getDataQueryServerRemote();
            MaxID = dataServer.getMaxMasterSubID();

        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
    }

    private SubIDManager() {
    }

    /**
     *
     * @param subId
     * @return
     */
    public static long getMasterSubId(long subId) {

        if(subId<=0)
            return 0;

        //Return subId as it is, if its greater than any subId in Master3 table.
        if(subId > MaxID)
            return subId;

        try {
            return dataServer.getMasterSubID(subId);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            //ex.printStackTrace();
        }
        return 0;
    }

    /**
     *
     * @param subId
     * @return
     */
    public static long getMasterSubId(String subId) {
        return getMasterSubId(Default.defaultLong(subId));
    }

}