package com.PMSystems;

import com.PMSystems.*;
import com.PMSystems.util.*;
import com.PMSystems.beans.*;
import com.PMSystems.logger.*;

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class SessionManager  {

    private static HashMap activeUserHash = new HashMap();
    private static Vector expireUserVec = new Vector();

    public SessionManager() {
    }

    public static SessionListener getSessionListener(String userId, String sessionId, String desc
        , HttpServletRequest request) {
        return new SessionListener(userId, sessionId, desc, request);
    }

    /**
     *
     * @param userInfo
     */
    public static synchronized void addActiveUser(UserSessionBean bean) {

        if(bean==null || bean.getSessionId().equals(""))
            return;

        synchronized (activeUserHash) {
            bean.setLoggedInTime(System.currentTimeMillis());
            activeUserHash.put(bean.getSessionId(), bean);

            String output = "[SessionManager]: BOUNDED UserId:"+bean.getUserId()+", Login-Type:"+bean.getDescription()+", IP:"+bean.getUserIP()
                +", CurrentTime:"+(new Date());
            WebServerLogger.getLogger().log(new LogEntry("", "", output));
            System.out.println(output);
        }
    }

    /**
     *
     * @param userId
     */
    public static synchronized void removeActiveUser(UserSessionBean bean) {

        synchronized (activeUserHash) {

            activeUserHash.remove(bean.getSessionId());
            long aliveTime = System.currentTimeMillis() - bean.getLoggedInTime();
            bean.setAliveTime(aliveTime);
            expireUserVec.add(bean);

            String output = "[SessionManager]: REMOVED UserId:"+bean.getUserId()+", Login-Type:"+bean.getDescription()+", IP:"+bean.getUserIP()
                +", AliveTime:"+((int)(aliveTime/1000))+"sec, CurrentTime:"+(new Date());

            WebServerLogger.getLogger().log(new LogEntry("", "", output));
            System.out.println(output);
        }
    }

    /**
     *
     * @return
     */
    public static Vector getLoggedOutUsers() {
        Vector vec = new Vector();
        vec.addAll(expireUserVec);
        return vec;
    }

    public static Vector getLoggedInUsers() {

        Vector vec = new Vector();
        try {
            Iterator it = activeUserHash.values().iterator();
            while(it.hasNext())
                vec.add(it.next());

        } catch (Exception ex) { ; }
        return vec;
    }

}

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
class SessionListener implements HttpSessionBindingListener {

    private UserSessionBean bean;
    public SessionListener(String userId, String sessionId, String desc, HttpServletRequest request) {
        bean = new UserSessionBean(userId, sessionId, desc, request.getRemoteAddr());
        bean.setIsLoggedIn(true);
    }

    public void valueBound(HttpSessionBindingEvent event) {
        bean.setSession(event.getSession());
        SessionManager.addActiveUser(bean);
    }

    public void valueUnbound(HttpSessionBindingEvent event) {

        try {
            if(event.getSession()!=null && event.getSession().getLastAccessedTime()>0)
                bean.setLastRequestTime(event.getSession().getLastAccessedTime());
        } catch (Exception ex) {

        }
        SessionManager.removeActiveUser(bean);
        bean.setIsLoggedIn(false);
        bean.setSession(null);
    }
}
