package com.PMSystems;


import javax.servlet.*;
import javax.servlet.http.*;

import java.util.*;
import java.net.*;
import java.io.*;

import com.PMSystems.sejbs.*;
import com.PMSystems.util.*;
import com.PMSystems.logger.*;
import com.PMSystems.beans.*;
import com.PMSystems.dbbeans.*;
import com.PMSystems.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: </p>
 * @author Suhaib
 * @version 1.0
 *
 Bypass UniqueRequestToken With Stored XSS
 =========================================
 There have been discussions suggesting that the unique request token can be compromised using JavaScript.
 This attack implies that the application also contains a stored cross-site scripting vulnerability,
 which is frequently a more severe issue than cross-site request forgery. The first MySpace worm worked in this
 manner where it used a Cross Site Scripting vulnerability to forge requests to update a user's profile, where
 the user profile update mechanism was protected with a CSRF defense mechanism similar to what is provided by this filter.
 If your application contains a stored cross-site scripting vulnerability, then the unique request token can be parsed
 from the HTML response to successfully forge form submissions.

 Retrieved from "http://www.owasp.org/index.php/How_CSRFGuard_Works"

 */


public class WebSecurityManager {

    public static final String CSRF_FILTER_FILE = "csrf_filter.properties";
    private static Vector applyFilterVec = new Vector();
    private static Vector noFilterVec = new Vector();

    static {
        loadSecurityFilterFile();
        System.out.println("[WebSecurityManager-init]: ApplyVec.size:"+applyFilterVec.size()+", NoFilterVec.size:"+noFilterVec.size());
    }

    private WebSecurityManager() {
    }

    /**
     * @return
     */
    public static String getCSRFToken() {

        int keyLength = PMSDefinitions.CSRF_TOKEN_LENGTH;
        try {
            String token = TokenGenerator.generateCSRFToken(keyLength+10);
            if(token.length()<keyLength) {
                StringBuffer buff = new StringBuffer();
                char[] ch = token.toCharArray();
                buff.append(ch);

                for(int i=0; i<keyLength && buff.length()<keyLength; i++)
                    buff.append(TokenGenerator.generateRandomChar("SHA1PRNG"));
            } else
                return token.substring(0, keyLength);

        } catch (Exception ex) {
           ex.printStackTrace();
           WebServerLogger.getLogger().log(ex);
       }
       return "";
    }

    /**
     *
     * @return
     */
    private static void loadSecurityFilterFile() {

        try {
            System.out.println("\n[WebSecurityManager]: @@@@ Loading CSRF Filter File: "+PMSDefinitions.RESOURCE_DIR+CSRF_FILTER_FILE);

            Vector applyVec = new Vector();
            Vector notapplyVec = new Vector();
            File file = new File(PMSDefinitions.RESOURCE_DIR+CSRF_FILTER_FILE);
            if (!file.isFile()) {
                System.out.println("[WebSecurityManager]: ***** CSRF Filter File NOT Found ****** \n");
                return;
            }

            BufferedReader in = new BufferedReader(new FileReader(file));
            String str = null;
            boolean apply = false;
            while((str = in.readLine()) != null) {

                if(str.trim().equalsIgnoreCase("") || str.trim().startsWith("#"))
                    continue;
                else if(str.trim().equalsIgnoreCase("[apply]")) {
                    apply = true;
                    continue;
                } else if(str.trim().equalsIgnoreCase("[notapply]")) {
                    apply = false;
                    continue;
                } else if(str==null)
                    break;

                if(apply)
                    applyVec.add(str.trim());
                else
                    notapplyVec.add(str.trim());
            }
            in.close();

            for(int i=0; i<applyVec.size(); i++)
                System.out.println("[apply]: "+(String)applyVec.get(i));

            System.out.println("");
            for(int i=0; i<notapplyVec.size(); i++)
                System.out.println("[not-apply]: "+(String)notapplyVec.get(i));

            System.out.println("\n==============================");

            applyFilterVec.addAll(applyVec);
            noFilterVec.addAll(notapplyVec);

        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
    }

    /**
     *
     * @param uri
     * @return
     */
    public static boolean isFilterApplies(String uri) {

        try {
            boolean match = false;
            for(int i=0; i<applyFilterVec.size(); i++) {
                String rule = (String)applyFilterVec.get(i);
                if(rule.endsWith(".jsp") && rule.equalsIgnoreCase(uri)) {
                    match = true; break;

                } else if(rule.endsWith("*")) {
                    String subrule = (rule.indexOf("/")!=-1)? rule.substring(0, rule.lastIndexOf("/")+1): "";//removing '*'
                    String suburi = (uri.indexOf("/")!=-1)? uri.substring(0, uri.lastIndexOf("/")+1): "";//remove 'xyz.jsp' from uri
                    if(suburi.startsWith(subrule)) {
                        match=true; break;
                    }
                }
            }

            if(match==false)
                return false;

            for(int i=0; match==true && i<noFilterVec.size(); i++) {
                String rule = (String)noFilterVec.get(i);
                if(rule.endsWith(".jsp") && rule.equalsIgnoreCase(uri)) {
                    return false;//found in notApply

                } else if(rule.endsWith("*")) {
                    String subrule = (rule.indexOf("/")!=-1)? rule.substring(0, rule.lastIndexOf("/")+1): "";//removing '*'
                    String suburi = (uri.indexOf("/")!=-1)? uri.substring(0, uri.lastIndexOf("/")+1): "";//remove 'xyz.jsp' from uri
                    if(subrule.equalsIgnoreCase(suburi)) {
                        return false;//found in notApply
                    }
                }
            }

            return true;//rule applied: add security filter.

        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }

    public static void init() {
        ;//do nothing. just load class.
    }

    //Returns string like <input type="hidden" name="bms_req_tk" value="adsf&amp;asd&#32;"
    public static String getCSRFToken_FORM(HttpSession session) {
        return getCSRFTokenForWeb(session, 1);
    }

    public static String getCSRFToken_HREF(HttpSession session) {
        return getCSRFTokenForWeb(session, 2);
    }

    public static String getCSRFToken_FORWARD(HttpSession session, String path) {
        return getCSRFTokenForWeb(session, 3);
    }

    private static String getCSRFTokenForWeb(HttpSession session, int type) {

        try {
            String token = Default.toDefault((String)session.getAttribute(PMSDefinitions.CSRF_TOKEN_NAME));
            if(token.equals(""))
                return "";

            if(type==1)//for forms
                return "<input type=\"hidden\" name=\""+PMSDefinitions.CSRF_TOKEN_NAME+"\" value=\""+Default.escapeHtml(token)+"\">";
            else if(type==2)
                return PMSDefinitions.CSRF_TOKEN_NAME+"="+token;

        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return "";
    }

    /**
     *
     * @param session
     * @return
     */
    public static String getCookieScript(HttpSession session) {

        try {
            if(session.getAttribute(PMSDefinitions.CSRF_COOKIE)==null)
                return "";

            String action = Default.toDefault((String)session.getAttribute(PMSDefinitions.CSRF_COOKIE));
            String oldValue = Default.toDefault((String)session.getAttribute(PMSDefinitions.CSRF_COOKIE_OLD_VALUE));
            String nowValue = Default.toDefault((String)session.getAttribute(PMSDefinitions.CSRF_COOKIE_NOW_VALUE));
            System.out.println("[cookie.script]: action:"+action+", tk.now: "+nowValue+", tk.old: "+oldValue);

            String script = "<SCRIPT SRC=\"/pms/js/bmstk.js\" TYPE=\"text/javascript\"></SCRIPT>"
                +"\n<SCRIPT TYPE=\"text/javascript\">setBMS_TK(\""+nowValue+"\", \""+oldValue+"\");</SCRIPT>";

            session.removeAttribute(PMSDefinitions.CSRF_COOKIE);
            session.removeAttribute(PMSDefinitions.CSRF_COOKIE_OLD_VALUE);
            session.removeAttribute(PMSDefinitions.CSRF_COOKIE_NOW_VALUE);
            return script;

        } catch (Exception ex) {
            ex.printStackTrace();
            //WebServerLogger.getLogger().log(ex);
        }
        return "";
    }
}