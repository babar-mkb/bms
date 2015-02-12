package com.PMSystems;

import java.util.*;
import java.io.*;

import com.PMSystems.*;
import com.PMSystems.dbbeans.*;
import com.PMSystems.util.*;
import com.PMSystems.sejbs.*;

/**
 * This class is used to keep the resources of mail system
 *
 * @version         1.0            08 Aug 2002
 * @author          Usman
 *
 * 08 Aug 2002      Usman          Initial Development of Class
 */

public class PMSResources {

    private static PMSResources resources;

    // Mail Server
    private String processName;

    // SMTP properties
    private String jksPassword;
    private String providerURL;
    private String initialContext;
    private String webServerURL;
    private String graphicsPath;
    private String chartsPath;// Added by Shahbaz
    private String tempFilePath;
    private String exportPath;
    private String appServerLogFile;
    private String webServerLogFile;
    private String groupFile;
    private String userFile;
    private String certFile;
    private String dirRoot;
    private String apacheHome;
    private String webAppFile;
    private String reportProcessingTimeDuration;
    private String listDownloadDelay;
    private String listDownloadPath;
    private String webVersionPath;
    private Vector suppressedEmails;
    private String deleteBatchSize;//Adeel
    private String deleteUserID;//Adeel
    private String deleteProcessDelay;//Adeel
    private String graphicAccessURL;
    private String formUploadDir; //Adeel
    private String abusePop3Host = "";
    private int abusePop3Port;
    private String abusePop3User = "";
    private String abusePop3Pass = "";
    private long abusePop3CheckDelay;
    private boolean isAbusePop3WithSSL;

    private String previewDomain = "";
    private String eventsDomain = "";

    private String cdnStaticContents = "";
    private String cdnImagesContents = "";

    private int salesforceHistoryDays = 15;

    /**
     * Constructor, initializes it's members from values from property file
     *
     * @throws ResourceException
     */
    private PMSResources() throws ResourceException {
        Properties resources = PMSResourceLoader.getResources();
        processName = resources.getProperty(PMSDefinitions.PROCESS_NAME_ID);
        providerURL = resources.getProperty(PMSDefinitions.PROVIDER_URL_ID);
        initialContext = resources.getProperty(PMSDefinitions.INITIAL_CONTEXT_FACTORY_ID);
        webServerURL = resources.getProperty(PMSDefinitions.WEB_SERVER_URL_ID);
        graphicsPath = resources.getProperty(PMSDefinitions.GRAPHICS_PATH);
        chartsPath = resources.getProperty(PMSDefinitions.CHARTS_PATH);//Added by Shahbaz
        tempFilePath = resources.getProperty(PMSDefinitions.TEMP_FILE_PATH);
        exportPath = resources.getProperty(PMSDefinitions.EXPORT_PATH);
        appServerLogFile = resources.getProperty(PMSDefinitions.APP_SERVER_LOG_FILE_ID);
        webServerLogFile = resources.getProperty(PMSDefinitions.WEB_SERVER_LOG_FILE_ID);
        userFile = resources.getProperty(PMSDefinitions.USER_FILE_ID);
        groupFile = resources.getProperty(PMSDefinitions.GROUP_FILE_ID);
        certFile = resources.getProperty(PMSDefinitions.CERT_FILE_ID);
        dirRoot = resources.getProperty(PMSDefinitions.DIR_ROOT_ID);
        apacheHome = resources.getProperty(PMSDefinitions.APACHE_HOME_ID);
        webAppFile = resources.getProperty(PMSDefinitions.WEB_APPLICATION_FILE_ID);
        reportProcessingTimeDuration = resources.getProperty(PMSDefinitions.REPORT_PROCESSING_TIME_DURATION);
        listDownloadDelay = resources.getProperty(PMSDefinitions.LIST_DOWNLOAD_DELAY);
        listDownloadPath = resources.getProperty(PMSDefinitions.LIST_DOWNLOAD_PATH);
        webVersionPath = resources.getProperty(PMSDefinitions.WEB_VERSION_PATH);
        deleteBatchSize = resources.getProperty(PMSDefinitions.DELETE_BATCH_SIZE);//Adeel
        deleteProcessDelay = resources.getProperty(PMSDefinitions.DELETE_PROCESS_DELAY);//Adeel
        deleteUserID = resources.getProperty(PMSDefinitions.DELETE_USER_ID);//Adeel
        graphicAccessURL = resources.getProperty(PMSDefinitions.GRAPHIC_ACCESS_URL_ID);
        formUploadDir = resources.getProperty(PMSDefinitions.FORM_UPLOAD_DIR); // Adeel
        jksPassword = resources.getProperty(PMSDefinitions.JKS_PASSWORD); // Suhaib

        abusePop3Host = resources.getProperty(PMSDefinitions.ABUSE_POP3_HOST_ID);
        abusePop3Port = Default.defaultInt(resources.getProperty(PMSDefinitions.ABUSE_POP3_PORT_ID));
        abusePop3User = resources.getProperty(PMSDefinitions.ABUSE_POP3_USER_ID);
        abusePop3Pass = resources.getProperty(PMSDefinitions.ABUSE_POP3_PASS_ID);
        abusePop3CheckDelay = Default.defaultInt(resources.getProperty(PMSDefinitions.ABUSE_POP3_DELAY_ID))*60l*1000l;//mins to milli
        isAbusePop3WithSSL = Default.toDefault(resources.getProperty(PMSDefinitions.ABUSE_POP3_SSL_ID)).equalsIgnoreCase("Y");

        previewDomain = resources.getProperty(PMSDefinitions.PREVIEW_DOMAIN);
        eventsDomain = resources.getProperty(PMSDefinitions.EVENTS_DOMAIN);

        cdnImagesContents = resources.getProperty(PMSDefinitions.CDN_FOR_IMAGES);
        cdnStaticContents = resources.getProperty(PMSDefinitions.CDN_FOR_STATIC_CONTENTS);

        salesforceHistoryDays = Default.defaultInt(resources.getProperty(PMSDefinitions.SF_HISTORY_DAYS));

        suppressedEmails=new Vector();//Adeel
        setSuppressedEmails();//Adeel
    }


    public String getAbusePop3Host() { return abusePop3Host; }
    public int getAbusePop3Port() { return abusePop3Port; }
    public String getAbusePop3User() { return abusePop3User; }
    public String getAbusePop3Pass() { return abusePop3Pass; }
    public long getAbusePop3CheckDelay() { return abusePop3CheckDelay; }
    public boolean isAbusePop3WithSSL() { return isAbusePop3WithSSL; }


    public static PMSResources getInstance() throws ResourceException {
        if(resources == null) {
            resources = new PMSResources();
        }
        return resources;
    }

    /**
     * @return String
     */
    public String getProcessName() {
        return processName;
    }

    public String getJKSPassword() {
        return jksPassword;
    }

    /**
     * @return String
     */
    public String getProviderURL() {
        return providerURL;
    }

    /**
     * @return String
     */
    public String getWebServerURL() {
        return webServerURL;
    }

    public String getWebServerDomainName() {
        if(webServerURL!=null && webServerURL.startsWith("http")) {
            return webServerURL.substring(webServerURL.indexOf("//")+2, webServerURL.indexOf("/pms"));
        }
        System.out.println("[PMSResources]: Webserver Domain Name NOT FOUND, webServerURL: "+webServerURL);
        return "";
    }

    public String getGraphicAccessURL() {
        return graphicAccessURL;
    }
    /**
     * @return String
     */
    public String getGraphicsPath() {
        return graphicsPath;
    }

    /**
     * @return String
     */
    public String getChartsPath() {
        return chartsPath;
    }

    /**
     * @return String
     */
    public String getTempFilePath() {
        return tempFilePath;
    }

    /**
     * @return String
     */
    public String getExportPath() {
        return exportPath;
    }

    /**
     * @return String
     */
    public String getInitialContext() {
        return initialContext;
    }

    /**
     * @return String
     */
    public String getGroupFile() {
        return groupFile;
    }

    /**
     * @return String
     */
    public String getUserFile() {
        return userFile;
    }

    /**
     * @return String
     */
    public String getCertFile() {
        return certFile;
    }

    /**
     * @return String
     */
    public String getDirRoot() {
        return dirRoot;
    }

    /**
     * @return String
     */
    public String getApacheHome() {
        return apacheHome;
    }

    /**
     * Inserts the suppressed emails from file specified i.e. SuppressedEmails.dat into the Vector object
     * i.e. this.suppressedEmails
     * @return
     */
    private void setSuppressedEmails() {
//        EJBHomesFactory ejHome = new EJBHomesFactory();
//        ComplexQueryServer cqs = ejHome.getComplexQueryServerRemote();
//        System.out.println("+++++++++ cqs: " + cqs + " ++++++++++++");
//        try {
//            suppressedEmails = cqs.getUniversalSupressedEmails();
//        }
//        catch(Exception ex) {
//            ex.printStackTrace();
//        }
/*        FileReader fr=null;
        BufferedReader br=null;
        try {
            fr = new FileReader(new java.io.File(//Adeel
                PMSDefinitions.RESOURCE_DIR+PMSDefinitions.SUPPRESSED_FILE));
            br = new BufferedReader(fr);
            String line = null;
            while ((line=br.readLine())!=null) {
                line=line.toLowerCase();
                if(!line.equals(""))
                    suppressedEmails.add(line);
            }
            System.out.println("suppressed Email Vec Size ="+suppressedEmails.size());
        }
        catch(java.io.FileNotFoundException fnfe) {//Adeel
            fnfe.printStackTrace();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            try {
                br.close();
                fr.close();
            }
            catch(IOException ioe) {
                ioe.printStackTrace();
            }
        }*/
    }

    /**
     * Iterates over each email address contained inside vector against the given email address for
     * validation. Email addresses inside a vector are categorized amongst three types.
     * @param email
     * @return
     */
    public boolean isValidEmail(String email) {

        email = email.trim().toLowerCase();
        if(suppressedEmails.size() == 0) {
            getUniversalSupressedEmails();
        }

        String str = "";
        for(int i=0; i<suppressedEmails.size(); i++) {
            str=(String)suppressedEmails.get(i);
            if(str.charAt(0)=='@' && (email.indexOf(str) != -1)) {
                return false;
            } else if(str.indexOf("<")!=-1 && email.indexOf(str)!=-1) {
                return false;

            } else if(str.equals(email)) {//both values are in lower-case
                return false;
            }
        }
        return true;
    }

    /**
     * @return String
     */
    public String getAppServerLogFile() {
        return appServerLogFile;
    }

    /**
     * @return String
     */
    public String getWebServerLogFile() {
        return webServerLogFile;
    }

    public String getWebApplicationFile() {
        return webAppFile;
    }

    public String getReportProcessingTimeDuration() {
        return reportProcessingTimeDuration;
    }

    public String getListDownloadDelay() {
        return listDownloadDelay;
    }

    public String getListDownloadPath() {
        return listDownloadPath;
    }

    public String getWebVersionPath() {
        return webVersionPath;
    }

    //Adeel
    public String getDeleteBatchSize() {
        return deleteBatchSize;
    }

    public String getDeleteUserID() {
        return deleteUserID;
    }

    public String getDeleteProcessDelay() {
        return deleteProcessDelay;
    }

    public Vector getUniversalSupressedEmails() {
        try {
            if (suppressedEmails.size() == 0) {
                ComplexQueryServer cqs = EJBHomesFactory.getComplexQueryServerRemote();
                Vector vec = cqs.getUniversalSupressedEmails();
                for(int i=0; vec!=null && i<vec.size(); i++)
                    //Load in UniversalSupress Vector as Lower Case Emails.
                    suppressedEmails.add(((String)vec.get(i)).toLowerCase());
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return suppressedEmails;
    }

    public Hashtable getChallengeHash() {

        Hashtable vec = new Hashtable();
        try {
            Properties prp = LoadResources.getResources(PMSDefinitions.CHALLENGE_FILE);
            Enumeration enum_1 = prp.propertyNames();
            while(enum_1.hasMoreElements()) {
                String key = Default.toDefault((String)enum_1.nextElement());
                String value = Default.toDefault(prp.getProperty(key));
                if(key.length()>0 && value.length()>0)
                    vec.put(key, value);
            }
        } catch (Exception ex) { ; }
        return vec;
    }

    public String getFormUploadDir() { // Adeel
        return formUploadDir;
    }

    public boolean addInUniversalSupressList(String email) {
        email = email.trim();
        if (suppressedEmails.contains(email.toLowerCase()))
            return false;
        suppressedEmails.add(0, email.toLowerCase());
        return true;
    }

    public String getPreviewDomain() {
        return previewDomain;
    }

    public String getEventsDomain() {
        return eventsDomain;
    }

    public String getCDNForStaticContents() {
        return cdnStaticContents;
    }

    public String getCDNForImages() {
        return cdnImagesContents;
    }

    public int getSalesforceHistoryDays() {
        return salesforceHistoryDays;
    }

    /**
     *
     * @return String
     */
/*    public String getRunReportAgentDelay() {
        return this.runReportAgentDelay;
    }
*/

}//class
