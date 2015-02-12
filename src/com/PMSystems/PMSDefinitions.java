package com.PMSystems;

import java.util.*;
import com.PMSystems.PMSResources;

/**
 * This class is used to define constants related to pms
 *
 * @version         1.0            06 Aug 2002
 * @author          Usman
 *
 * 06 Aug 2002      Usman          Initial Development of Class
 */

public final class PMSDefinitions {

    // represents the service name
    public static final String SERVICE_NAME = "PMS";

    // default logo
    public static final String DEFAULT_LOGO = "/pms/graphics/logo.gif";

    public static final String RESOURCE_DIR = "/usr/services/";
    // name amd path of the resourc file of mail system
    public static final String RESOURCE_FILE = "/usr/services/pms.properties";

    // path of templates.properties
    public static final String TEMPLATES_PROPS = "/usr/services/templates.properties";

    // path where forms will be uploaded
    public static final String FORM_UPLOAD_DIR = "FORM_UPLOAD_DIR";

    // path containing directories of users' for HTML templates
    public static final String USER_TEMPLATE_DIR = "/usr/services/templates/";

    //Challenge Text Properties
    public static final String CHALLENGE_FILE = "/usr/services/challenge.properties";
    public static final String CHALLENGE_CODE_DIR = "/usr/services/code/";

    // The name for specifying the location of the JBoss Service provider.
    public static final String PROVIDER_URL_ID = "PROVIDER_URL";

    // root of the directory structure
    public static final String DIR_ROOT_ID = "DIR_ROOT";

    // home directory of apache server
    public static final String APACHE_HOME_ID = "APACHE_HOME";

    // pms certificate file
    public static final String CERT_FILE_ID = "CERT_FILE";

    // file containing groups info
    public static final String GROUP_FILE_ID = "GROUP_FILE";

    // file containing users info
    public static final String USER_FILE_ID = "USER_FILE";

    // admin password
    public static final String ADMIN_PASSWORD = "ADMIN_PASSWORD";
    public static final String JKS_PASSWORD = "JKS_PASSWORD";

    //Minimum password char limit.
    public static final int MIN_PASSWORD_LENGTH = 8;

    // The application server log file path and name
    public static final String APP_SERVER_LOG_FILE_ID = "APP_SERVER_LOG_FILE";

    // The web server log file path and name
    public static final String WEB_SERVER_LOG_FILE_ID = "WEB_SERVER_LOG_FILE";

    // The name for specifying the location of the JBoss Service provider.
    public static final String WEB_SERVER_URL_ID = "WEB_SERVER_URL";

    public static final String GRAPHIC_ACCESS_URL_ID = "GRAPHIC_ACCESS_URL";
    // The name for specifying the location of the JBoss Service provider.
    public static final String PROCESS_NAME_ID = "PROCESS_NAME";

    public static final String WEB_APPLICATION_FILE_ID = "WEB_APPLICATION_FILE";

    // The name for specifying the location of the uploaded graphics path.
    public static final String GRAPHICS_PATH = "GRAPHICS_PATH";

    // The name for specifying the location for generating charts and then picking them up.
    public static final String CHARTS_PATH = "CHARTS_PATH";//Added by Shahbaz

    // The name for specifying the location of the uploaded File Layout Path.
    public static final String TEMP_FILE_PATH = "TEMP_FILE_PATH";

    // The name for specifying the location of the uploaded File Layout Path.
    public static final String WEB_VERSION_PATH = "WEB_VERSION_PATH";
    //public static final String WEB_VERSION_DIR = "WebVersion/";
    public static final String WEB_VERSION_DIR = "webversion/";
    // The name for specifying the location of the exported File Path.
    public static final String EXPORT_PATH = "EXPORT_PATH";

    // The name for specifying the location for suppressed list file's path.Adeel
    public static final String SUPPRESSED_FILE = "SuppressedEmails.dat";//Adeel

    // The name for specifying the initial context facory to use.
    public static final String INITIAL_CONTEXT_FACTORY_ID = "INITIAL_CONTEXT_FACTORY";

    //public static final String RUN_REPORT_AGENT_DELAY = "RUN_REPORT_AGENT_DELAY";
    public static final String REPORT_PROCESSING_TIME_DURATION = "reportProcessingTimeDuration";
    public static final String LIST_DOWNLOAD_DELAY = "LIST_DOWNLOAD_DELAY";
    public static final String LIST_DOWNLOAD_PATH = "LIST_DOWNLOAD_PATH";

    /*------------------- Number Type Definitions -------------------*/
    // These definitions will mainly be used to specify the type of the field
    // when getting the value of a number type form field from the request
    public static final int NUMTYPE_INTEGER = 0;
    public static final int NUMTYPE_LONG = 1;
    public static final int NUMTYPE_FLOAT = 2;
    public static final int NUMTYPE_DOUBLE = 3;

    // EJB JNDI Names
    public static final String JNDI_NAME_ACTIVITY_LOG = "ActivityLogRemote";
    public static final String JNDI_NAME_ARTILCE = "ArticleRemote";
    public static final String JNDI_NAME_AUTO_NUMBERS = "AutoNumbersRemote";
    public static final String JNDI_NAME_BOUNCED_CAMPAIGN = "BouncedCampaignRemote";
    public static final String JNDI_NAME_CAMPAIGN = "CampaignRemote";
    public static final String JNDI_NAME_COMPLETED_CAMPAIGN = "CompletedCampaignRemote";
    public static final String JNDI_NAME_CAMPAIGN_LIST = "CampaignListRemote";
    public static final String JNDI_NAME_CAMPAIGN_TRANSMISSION = "CampaignTransmissionRemote";
    public static final String JNDI_NAME_CLICKED_OFFER = "ClickedOfferRemote";
    public static final String JNDI_NAME_COMPLEX_QUERY_SERVER = "ComplexQueryServer";
    public static final String JNDI_NAME_COMPLEX_QUERY_SERVER2 = "ComplexQueryServer2";
    public static final String JNDI_NAME_REPORT_QUERY_SERVER = "ReportsQueryServer";
    public static final String JNDI_NAME_DATA_QUERY_SERVER = "DataQueryServerBean";
    public static final String JNDI_NAME_BMS_QUERY_SERVER = "BMSQueryServer";
    public static final String JNDI_NAME_ALPHA_QUERY_SERVER = "AlphaQueryServer";

    public static final String JNDI_NAME_COUNTRY = "CountryRemote";
    public static final String JNDI_NAME_STATE = "StateRemote";
    public static final String JNDI_NAME_CUSTOMER = "CustomerRemote";
    public static final String JNDI_NAME_GRAPHICS = "GraphicsRemote";
    public static final String JNDI_NAME_LIST = "ListRemote";
    public static final String JNDI_NAME_OPENED_CAMPAIGN = "OpenedCampaignRemote";
    public static final String JNDI_NAME_REPORTS_QUERY_SERVER = "ReportsQueryServer";
    public static final String JNDI_NAME_SUBSCRIBER = "SubscriberRemote";
    public static final String JNDI_NAME_SUBSCRIBER_DETAIL = "SubscriberDetailRemote";
    public static final String JNDI_NAME_TEMPLATE = "TemplateRemote";
    public static final String JNDI_NAME_USER = "UserRemote";
    public static final String JNDI_NAME_MONTHLY_SUBSCRIBER_STATISTICS = "MonthlySubscriberStatisticsRemote";
    public static final String JNDI_NAME_SCHEDUELD_DOWNLOAD = "ScheduledDownloadRemote";
    public static final String JNDI_NAME_SCHEDUELD_DOWNLOAD_DETAIL = "ScheduledDownloadDetailRemote";
    public static final String JNDI_NAME_SUBSCRIBER_TRACKING = "SubscriberTrackingRemote";
    // Campaign Status
    public static final String CAMPAIGN_STATUS_DRAFT = "D";
    public static final String CAMPAIGN_STATUS_COMPLETED = "C";
    public static final String CAMPAIGN_STATUS_SCHEDULED = "S";
    public static final String CAMPAIGN_STATUS_PENDING = "P";
    public static final String CAMPAIGN_STATUS_DELETED = "L";
    public static final String CAMPAIGN_STATUS_ALL = "all";

    // Campaign's target type
    public static final String CAMPAIGN_RECIPIENTS_TYPE_LIST = "List";
    public static final String CAMPAIGN_RECIPIENTS_TYPE_SALESFORCE = "Salesforce";
    public static final String CAMPAIGN_RECIPIENTS_TYPE_NETSUITE = "Netsuite";
    public static final String CAMPAIGN_RECIPIENTS_TYPE_HIGHRISE = "Highrise";
    public static final String CAMPAIGN_RECIPIENTS_TYPE_GOOGLE = "Google";    
    public static final String CAMPAIGN_RECIPIENTS_TYPE_TARGET = "Target";
    public static final String CAMPAIGN_RECIPIENTS_TYPE_TAGS = "Tags";	
        
    
    //means its a TriggerTrack campaign, will only be visible in TriggerTrack pages.
    public static final String CAMPAIGN_STATUS_TRIGGER_TRACK = "T";

    public static final String EVERGREEN_STATUS_DRAFT = "D";
    public static final String EVERGREEN_STATUS_ACTIVE = "E";
    public static final String EVERGREEN_STATUS_INACTIVE = "I";
    public static final String EVERGREEN_STATUS_PENDING = "Q";

    public static final String CAMPAIGN_IS_ACTIVE = "Y";
    public static final String CAMPAIGN_IS_NOT_ACTIVE = "N";

    public static final String USER_IS_ACTIVE = "Y";
    public static final String USER_IS_NOT_ACTIVE = "N";
    // User Role
    public static final String USER_SYSTEM_ADMIN = "S";
    public static final String USER_EXTERNAL_ACCOUNT = "E";
    public static final String USER_ADMINISTRATOR_ACCOUNT = "A";
    public static final String USER_OPERATOR_ACCOUNT = "O";

    // Scheduled FTP Upload
    public static final String FTPDOWNLOAD_STATUS_SCHEDULED = "S";
    public static final String FTPDOWNLOAD_STATUS_UNSCHEDULED = "U";
    public static final String FTPDOWNLOAD_STATUS_PENDING   = "P";
    public static final String FTPDOWNLOAD_STATUS_QUEUED    = "Q";
    public static final String FTPDOWNLOAD_STATUS_COMPLETED = "C";
    public static final String FTPDOWNLOAD_STATUS_ERRONEOUS = "E";
    public static final String FTPDOWNLOAD_STATUS_ALL = "A";

    public static final String FTPDOWNLOAD_USE_LIST_YES = "Y";
    public static final String FTPDOWNLOAD_USE_LIST_NO = "N";

    // Auto Follow Up Campaign Status
    public static final String AFU_CAMPAIGN_STATUS_ACTIVE = "A";
    public static final String AFU_CAMPAIGN_STATUS_INACTIVE = "I";

    // Article Status
    public static final String ARTICLE_STATUS_ACTIVE = "A";
    public static final String ARTICLE_STATUS_INACTIVE = "I";

    //User Info Session Id
    public static final String USER_INFO_SESSION_ID = "userInfo";
    public static final String SYSADMIN_INFO_SESSION_ID = "adminInfo";

    //Lists Status
    public static final String LIST_STATUS_ACTIVE = "A";
    public static final String LIST_STATUS_INACTIVE = "I";

    // Empty String
    public static final String EMPTY_STRING = "";

    // Empty String
    public static final String NONE = "BridgeMail-NONE";

    // Name of Graphics Dir on Web Server
    public static final String GRAPHICS_DIR = "graphics/";

    // Name of Graphics Dir on Web Server
    public static final String EXPORT_DIR = "export/";

    // Subscriber status
    public static final String SUBSCRIBER_STATUS_SUBSCRIBE = "S";
    public static final String SUBSCRIBER_STATUS_UNSUBSCRIBE = "U";
    public static final String SUBSCRIBER_STATUS_REMOVE = "R";

    //Supress List
    public static final String SUPRESS_LIST_NAME = "Supress_List_";
    public static final String LIKE_SUPRESS_LIST_NAME = "Supress\\_List\\_%";
    public static final String SUBSCRIBER_SUPRESSED = "S";
    public static final String SUBSCRIBER_UNSUPRESSED = "U";

    // Bounce Supress List
    public static final String BOUNCE_SUPRESS_LIST_NAME = "Bounce_Supress_List_";

    // Marital Status
    public static final String MARITAL_STATUS_SINGLE = "S";
    public static final String MARITAL_STATUS_MARRIED = "M";
    public static final String MARITAL_STATUS_WIDOW = "W";

    // Gender
    public static final String GENDER_MAIL = "M";
    public static final String GENDER_FEMAIL = "F";

    // Email Types
    public static final String EMAIL_TYPE_HTML = "H";
    public static final String EMAIL_TYPE_TEXT = "T";

    //no of Friends in Tell A Friend mail choice.
    public static final int NO_OF_TELL_A_FRIENDS = 5;

    // Recor lock
    public static final String LOCK_TRUE = "Y";
    public static final String LOCK_FALSE = "N";

    // Access type
    public static final int ACCESS_TYPE_NON_CENTRALIZED = 0;
    public static final int ACCESS_TYPE_CENTRALIZED_READ_ONLY = 1;
    public static final int ACCESS_TYPE_CENTRALIZED_READ_WRITE = 2;

    // Campaign Transmission
     public static final String CAMPAIGN_TRANSMISSION_STATUS_PENDING = "P";
     public static final String CAMPAIGN_TRANSMISSION_STATUS_SENT = "S";
     public static final String CAMPAIGN_TRANSMISSION_STATUS_FAILED = "F";
     public static final String CAMPAIGN_TRANSMISSION_STATUS_QUEUED = "Q";

    //Household Income ranges
    public static Hashtable HOUSEHOLD_INCOME_RANGES = new Hashtable();

    static {
        HOUSEHOLD_INCOME_RANGES.put("1", "$15,000 or less");
        HOUSEHOLD_INCOME_RANGES.put("2", "$15,001 - $19,999");
        HOUSEHOLD_INCOME_RANGES.put("3", "$20,000 - $24,999");
        HOUSEHOLD_INCOME_RANGES.put("4", "$25,000 - $29,999");
        HOUSEHOLD_INCOME_RANGES.put("5", "$30,000 - $34,999");
        HOUSEHOLD_INCOME_RANGES.put("6", "$35,000 - $39,999");
        HOUSEHOLD_INCOME_RANGES.put("7", "$40,000 - $44,999");
        HOUSEHOLD_INCOME_RANGES.put("8", "$45,000 - $49,999");
        HOUSEHOLD_INCOME_RANGES.put("9", "$50,000 - $59,999");
        HOUSEHOLD_INCOME_RANGES.put("10", "$60,000 - $74,999");
        HOUSEHOLD_INCOME_RANGES.put("11", "$75,000 - $99,999");
        HOUSEHOLD_INCOME_RANGES.put("12", "$100,000 - $124,999");
        HOUSEHOLD_INCOME_RANGES.put("13", "$125,000 - $149,999");
        HOUSEHOLD_INCOME_RANGES.put("14", "$150,000 - $199,999");
        HOUSEHOLD_INCOME_RANGES.put("15", "$200,000 - $249,999");
        HOUSEHOLD_INCOME_RANGES.put("16", "$250,000 or Over");
        HOUSEHOLD_INCOME_RANGES.put("17", "None of the above");
   }

   public static String COPYRIGHT_STATEMENT = "Copyright &copy; 2002 - "+Calendar.getInstance().get(Calendar.YEAR)+" Makesbridge Technologies, Inc. All rights reserved.";

   public static String CAMPAIGN_LOCKED_STATEMENT = "NOTE: This campaign has been locked by some one else. If you want to change/remove any of it's contents then try again later.";

   public static String AT_CAMPAIGN_LOCKED_STATEMENT = "NOTE: This Auto-Trigger campaign is in active status and may not be edited. If you wish to edit Return to the Auto-Trigger setup page to Deactivate this campaign.";

   public static final String GROUP_PUBLISHERS = "publishers";
   public static final String GROUP_SCHEDULERS = "schedulers";
   public static final String GROUP_LIST_MANAGERS = "listmanagers";
   public static final String GROUP_REPORT_VIEWERS = "reportviewers";
   public static final String GROUP_STRONG_AUTH_REPORT_VIEWERS = "strongauthreportviewers";
   public static final String GROUP_FORM_CREATORS = "formcreators";
   public static final String GROUP_USER_MANAGEMENT = "usermanagement";
   public static final String GROUP_ANALYTIC_RESULTS = "analyticresutls";

   public static String DIR_ROOT;

   // apache
   static {
       try {
           DIR_ROOT = PMSResources.getInstance().getAppServerLogFile();
       } catch (ResourceException ex) {
       }
   }
   public static final String DIR_PUBLISHER = "publisher";
   public static final String DIR_SCHEDULE = "schedule";
   public static final String DIR_LIST = "list";
   public static final String DIR_REPORT = "report";
   public static final String DIR_REPORT_STRONGAUTH = DIR_REPORT + "/" + "strongauth";
   public static final String DIR_FORM = "form";
   public static final String DIR_USRMGMT = "usrmgmt";

   // tomcat ...
   public static final String URL_PATTERN_INDEX = "/index.jsp";
   public static final String URL_PATTERN_PUBLISHER = "/publisher/*";
   public static final String URL_PATTERN_SCHEDULE = "/schedule/*";
   public static final String URL_PATTERN_LIST = "/list/*";
   public static final String URL_PATTERN_REPORT = "/report/*";
   public static final String URL_PATTERN_REPORT_STRONGAUTH = "/report/strongauth/*";
   public static final String URL_PATTERN_FORM = "/form/*";
   public static final String URL_PATTERN_USRMGMT = "/usrmgmt/*";


   /* - activity log  types - */
   public static final String ACTIVITY_TYPE_CAMPAIGN_CREATE = "CMP_CR";
   public static final String ACTIVITY_TYPE_CAMPAIGN_DELETE = "CMP_DEL";

   public static final String ACTIVITY_TYPE_LIST_CREATE = "LIST_CR";
   public static final String ACTIVITY_TYPE_LIST_DELETE = "LIST_DEL";

   public static final String ACTIVITY_TYPE_USER_CREATE = "USR_CR";
   public static final String ACTIVITY_TYPE_USER_DELETE = "USR_DEL";

   //PKManager Bucket Size
    public static final int DEFAULT_BUCKET_SIZE = 100;
    public static final int BUCKET_REFILL_DELAY = 200;//millie seconds.

    //OneTime Insertion Size in CampaignTrasmission table for Dispensing;
    public static final int DISPENSE_BUCKET_SIZE = 100;

    // Delete process properties
    public static final String DELETE_BATCH_SIZE = "DELETE_BATCH_SIZE";
    public static final String DELETE_USER_ID = "DELETE_USER_ID";
    public static final String DELETE_PROCESS_DELAY = "DELETE_PROCESS_DELAY";

    // Sales Status types
    public static final String SALES_STATUS_CUSTOMER="CUSTOMER";
    public static final String SALES_STATUS_LEAD="LEAD";
    public static final String SALES_STATUS_PROSPECT="PROSPECT";
    public static final String SALES_STATUS_CONTACT="CONTACT";
    public static final String SALES_STATUS_PARTNER="PARTNER";
    public static final String SALES_STATUS_ACCOUNT="ACCOUNT";
    // Sales Status types added for Bullhorn(Clarity)
    public static final String SALES_STATUS_ACTIVE ="ACTIVE";
    public static final String SALES_STATUS_ARCHIVE ="ARCHIVE";
    public static final String SALES_STATUS_CALL_ONLY_DO_NOT_EMAIL = "CALL ONLY-DO NOT EMAIL";
    public static final String SALES_STATUS_DO_NOT_USE = "DO NOT USE";
    public static final String SALES_STATUS_EMAIL_ONLY = "EMAIL ONLY";
    public static final String SALES_STATUS_FORMER_CONSULTANT = "FORMER CONSULTANT";
    public static final String SALES_STATUS_FORMER_CONTACT = "FORMER CONTACT";
    public static final String SALES_STATUS_IMPORTED = "IMPORTED";
    public static final String SALES_STATUS_INTERVIEWING = "INTERVIEWING";
    public static final String SALES_STATUS_KICKBACK = "KICKBACK";
    public static final String SALES_STATUS_NEW_LEAD = "NEW LEAD";
    public static final String SALES_STATUS_OFFER_PENDING = "OFFER PENDING";
    public static final String SALES_STATUS_PASSIVE = "PASSIVE";
    public static final String SALES_STATUS_PLACED = "PLACED";
    public static final String SALES_STATUS_PRIVATE = "PRIVATE";
    public static final String SALES_STATUS_QUALIFIED = "QUALIFIED";
    public static final String SALES_STATUS_SCREENED = "SCREENED";
    public static final String SALES_STATUS_UNAWAILABLE = "UNAVAILABLE";
    public static final String SALES_STATUS_UNSCREENED = "UNSCREENED";
    public static final String SALES_STATUS_DORMANT_CONTACT = "DORMANT CONTACT";
    public static final String SALES_STATUS_SUSPECT = "SUSPECT";
    public static final String SALES_STATUS_PROSPECT_DECISION_MAKER = "PROSPECT-DECISION MAKER";
    public static final String SALES_STATUS_PROSPECT_SUPPORT_PERSONNEL = "PROSPECT-SUPPORT PERSONNEL";
    public static final String SALES_STATUS_DO_NOT_CONTACT = "DO NOT CONTACT";
    public static final String SALES_STATUS_DORMANT_PROSPECT = "DORMANT PROSPECT";
    public static final String SALES_STATUS_DORMANT_PROSPECT_STC = "DORMANT PROSPECT-STC";
    public static final String SALES_STATUS_DORMANT_PROSPECT_IABC = "DORMANT PROSPECT-IABC";
    public static final String SALES_STATUS_DORMANT_PROSPECT_AMWA = "DORMANT PROSPECT-AMWA";

    // Schedule Status types
    public static final String SCHEDULE_STATUS_SCHEDULED="S";
    public static final String SCHEDULE_STATUS_COMPLETE="C";
    public static final String SCHEDULE_STATUS_DEACTIVATE="D";
    public static final String SCHEDULE_STATUS_ERROR="E";
    public static final String SCHEDULE_STATUS_PENDING="P";

    // Synchronization/Integration Partners
    public static final String SYNCH_PARTNER_SALESFORCE="Salesforce";
    public static final String SYNCH_PARTNER_BULLHORN="Bullhorn";
    public static final String SYNCH_PARTNER_NETSUITE="Netsuite";
    public static final String SYNCH_PARTNER_HIGHRISE="Highrise";
    public static final String SYNCH_PARTNER_LINKEDIN="LinkedIn";    
    public static final String SYNCH_PARTNER_GOOGLE="Google";    

    // Schedule Recur Frequency
    public static final String SCHEDULE_FREQUENCY_ONCE_WEEK="O";
    public static final String SCHEDULE_FREQUENCY_ONCE_TWOWEEK="T";
    public static final String SCHEDULE_FREQUENCY_ONCE_MONTH="M";

    // Schedule Type Import/Export
    public static final String SCHEDULE_TYPE_IMPORT="I";
    public static final String SCHEDULE_TYPE_EXPORT="E";
    public static final String SCHEDULE_TYPE_PEERING="P";

    public static final int ACTION_CHANGE_PASS = 1;
    public static final int ACTION_NEW_PASS = 2;
    public static final int PMS_ACTIVATIONCODE_LENGTH = 8;
    public static final int PMS_ENCODED_LENGTH = 10;


    //AbuseMail pop3 settings
    public static final String ABUSE_POP3_HOST_ID = "abuse_pop3_host";
    public static final String ABUSE_POP3_PORT_ID = "abuse_pop3_port";
    public static final String ABUSE_POP3_USER_ID = "abuse_pop3_user";
    public static final String ABUSE_POP3_PASS_ID = "abuse_pop3_password";
    public static final String ABUSE_POP3_DELAY_ID = "abuse_pop3_check_delay";
    public static final String ABUSE_POP3_SSL_ID = "abuse_pop3_SSL";

    //Adding for WEB VERSION
    public static final String APPLICATION_WEB_URL = "abuse_pop3_SSL";

    //Adding for NetSuite
    public static final String NetSuite_Transaction_Session="NetSuite_Transaction_Session";

    public static final String LIST_SEGMENT_STATUS_DRAFT = "D";
    public static final String LIST_SEGMENT_STATUS_SCHEDULED = "S";
    public static final String LIST_SEGMENT_STATUS_PENDING = "P";
    public static final String LIST_SEGMENT_STATUS_QUEUED = "Q";
    public static final String LIST_SEGMENT_STATUS_COMPLETED = "C";

    public static final int APPL_ID_BRIDGEMAILSYSTEM = 1;
    public static final int APPL_ID_BRIDGESTATZ = 2;

    //security token for CSRF attacks
    public static final String CSRF_TOKEN_NAME = "BMS_REQ_TK";
    public static final String CSRF_COOKIE = "csrf.cookie";

    public static final String CSRF_COOKIE_NOW_NAME = "tk.now";
    public static final String CSRF_COOKIE_OLD_NAME = "tk.old";

    public static final String CSRF_COOKIE_NOW_VALUE = "csrf.cookie.now.value";
    public static final String CSRF_COOKIE_OLD_VALUE = "csrf.cookie.old.value";

    public static final int CSRF_TOKEN_LENGTH = 30;

    //Set duration such that a user's session doesnt outlast the Two updates to CSRF cookies.
    public static final int CSRF_COOKIE_EXPIRY_HRS=2;

    //==== Preview Domain =====
    public static final String PREVIEW_DOMAIN = "PREVIEW_DOMAIN";
    public static final String EVENTS_DOMAIN = "EVENTS_DOMAIN";

    //===== CDN Domain ======
    public static final String CDN_FOR_STATIC_CONTENTS = "CDN_FOR_STATIC_CONTENTS";
    public static final String CDN_FOR_IMAGES = "CDN_FOR_IMAGES";

    public static final String DASHBOARD_VEC = "DASHBOARD_VEC";
    public static final String DASHBOARD_FILTER_BEAN = "DASHBOARD_FILTER_BEAN";
    public static final String SUBSCRIBER_COUNT = "SUBSCRIBER_COUNT";
    public static final String DASHBOARD_USER = "DASHBOARD_USER";

    // for Signup Forms
    public static final String SF_LEAD = "L";
    public static final String SF_CONTACT = "C";
    public static final String SF_NO = "N";
    public static final String SF_HISTORY_DAYS = "SF_HISTORY_DAYS";

    // Subscriber's Activity
    public static final String SUB_EVENT_CLICK = "C";
    public static final String SUB_EVENT_OPEN = "O";
    public static final String SUB_EVENT_SIGNUP = "S";
    public static final String SUB_EVENT_WEBVIST = "W";

    public static final String BEHAVIOUR_FILTER_SCORE = "S";
    public static final String BEHAVIOUR_FILTER_WEB_VISIT = "W";
    public static final String BEHAVIOUR_FILTER_EMAIL = "E";
    public static final String BEHAVIOUR_FILTER_FORM_SUBMISSION = "F";
    public static final String BEHAVIOUR_FILTER_LIST = "L";
    public static final String BEHAVIOUR_FILTER_SEGMENT = "G";
    public static final String BEHAVIOUR_FILTER_PROFILE = "P";
    public static final String BEHAVIOUR_FILTER_TAG = "T";

    // SAM logo
    public static final String SAM_LOGO = "/pms/graphics/SAM_logo.jpg";

    // Message Status types
    public static final String MESSAGE_STATUS_FAIL="F";
    public static final String MESSAGE_STATUS_SENT="S";
    public static final String MESSAGE_STATUS_DRAFT="D";

    //Workflow Action Types
    public static final String WORKFLOW_ACTION_EMAIL ="E";
    public static final String WORKFLOW_ACTION_SCORE_CHANGE ="SC";
    public static final String WORKFLOW_ACTION_ALERT_SALESREP ="A";
    public static final String WORKFLOW_ACTION_WAIT ="W";
    public static final String WORKFLOW_ACTION_DO_NOTHING ="N";
    public static final String WORKFLOW_ACTION_TRIGGER_MAIL ="TM";

    //Behavior Filter For Types
    public static final String BEHAVIOR_FILTER_FOR_NURTURE_TRACKS ="N";
    public static final String BEHAVIOR_FILTER_FOR_WORKFLOWS ="W";
    public static final String BEHAVIOR_FILTER_FOR_SEGMENTS = "S";
    public static final String BEHAVIOR_FILTER_FOR_NORMAL_CAMPAIGN = "C";

    // User Email Category types
    public static final String EMAIL_CATEGORY_GOOD="A";
    public static final String EMAIL_CATEGORY_MIDDLE="B";
    public static final String EMAIL_CATEGORY_BAD="C";
    public static final String EMAIL_CATEGORY_NO="N";
    public static final String EMAIL_CATEGORY_TRIAL="Trial";

    // Campaign's types
    public static final String CAMPAIGN_TYPE_NORMAL="N";
    public static final String CAMPAIGN_TYPE_AUTO_TRIGGER="A";
    public static final String CAMPAIGN_TYPE_NURTURE_TRACK="T";
    public static final String CAMPAIGN_TYPE_WORKFLOW="W";
    public static final String CAMPAIGN_TYPE_AUTOBOT="B";

    // Salesforce Campaign Memeber Status
    public static final String SF_CAMPAIGN_MEMBER_STATUS_BOUNCED="B";
    public static final String SF_CAMPAIGN_MEMBER_STATUS_OPENED="O";
    public static final String SF_CAMPAIGN_MEMBER_STATUS_CLICKED="C";
    public static final String SF_CAMPAIGN_MEMBER_STATUS_UNSUBSCRIBED="U";

    // MakesBridge package types
    public static final String PACKAGE_TYPE_TRIAL = "TRIAL";
    public static final String PACKAGE_TYPE_MINI = "MINI";
    public static final String PACKAGE_TYPE_SMB = "SMB";
    public static final String PACKAGE_TYPE_PRO = "PRO";
    public static final String PACKAGE_TYPE_ENTERPRISE = "ENTPRISE";
    public static final String PACKAGE_TYPE_ENGAGE = "ENGAGE";

    // Instant Segment Actions
    public static final String INSTANT_ACTION_EMAIL = "E";
    public static final String INSTANT_ACTION_SCORE_CHANGE = "SC";
    public static final String INSTANT_ACTION_EXPORT_SUBS = "EX";
    public static final String INSTANT_ACTION_UPDATE_PROFILE = "UP";
    public static final String INSTANT_ACTION_ADD_TO_LIST = "L";
    public static final String INSTANT_ACTION_ALERT = "A";
    public static final String INSTANT_ACTION_ADD_TO_SALESFORCE = "SF";
    public static final String INSTANT_ACTION_ADD_TO_WORKFLOW = "WF";
    public static final String INSTANT_ACTION_SUPPRESS = "SP";
    
    public static final String USER_TAGS_FOR_TEMPLATE = "UT";
    public static final String USER_TAGS_FOR_CAMPAIGN = "C";
    public static final String USER_TAGS_FOR_BEHAVIOR_FILTER = "BF";
    public static final String USER_TAGS_FOR_LIST = "L";
    public static final String USER_TAGS_FOR_SUBSCRIBER = "S";
    public static final String USER_TAGS_FOR_IMAGES = "IM";
    public static final String USER_TAGS_FOR_AUTOBOTS = "AB";
    public static final String USER_TAGS_FOR_LANDING_PAGES = "LP";
    public static final String USER_TAGS_FOR_SIGN_UP_FORM = "SF";
    
    public static final String NOTIFY_TYPE_INFO = "I";
    public static final String NOTIFY_TYPE_WARNING = "W";
    public static final String NOTIFY_TYPE_ERROR = "E";
    
    public static final String NOTIFY_EVENT_TARGET_POPULATION_COUNT_READY = "TG_PCT";
    public static final String NOTIFY_EVENT_CAMPAIGN_COMPLETED = "CMP_C";
    public static final String NOTIFY_EVENT_CSV_UPLOAD_COMPLETED = "CSV";
    public static final String NOTIFY_EVENT_CAMPAIGN_ZERO_RECIPIENTS = "CMP_ZERO";
    public static final String NOTIFY_EVENT_CAMPAIGN_STARTED_DISPENSING = "CMP_RR";
    public static final String NOTIFY_EVENT_CAMPAIGN_ANALYSIS = "CMP_ANZ";
    
    public static final String AUTOBOT_TYPE_NORMAL = "N";
    public static final String AUTOBOT_TYPE_BIRTHDAY = "B";
    
    public static final String AUTOBOT_ACTION_EMAIL = "E";
    public static final String AUTOBOT_ACTION_SCORE_CHANGE = "SC";
    public static final String AUTOBOT_ACTION_ALERT = "A";
    public static final String AUTOBOT_ACTION_TAG = "TG";
    public static final String AUTOBOT_ACTION_PROFILE_UPDATE = "PU";

    public static final String EDITOR_TYPE_WYSIWYG = "W";
    public static final String EDITOR_TYPE_HAND_CODED = "H";
    public static final String EDITOR_TYPE_MEE = "MEE";
}//CLASS
