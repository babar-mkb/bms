package com.PMSystems;

import java.util.*;
import java.io.*;

/**
 * This class is used to load the PMS resources
 *
 * @version         1.0            06 Aug 2002
 * @author          Usman
 *
 * 06 Aug 2002      Usman          Initial Development of Class
 */

public class PMSResourceLoader {

    private static final String CLASS_NAME = "PMSResourceLoader";

    public static Properties getResources() throws ResourceException {
        Properties properties = new Properties();
        properties = LoadResources.getResources(PMSDefinitions.RESOURCE_FILE);
        if(
                properties.getProperty(PMSDefinitions.PROCESS_NAME_ID) == null ||
                properties.getProperty(PMSDefinitions.PROVIDER_URL_ID) == null ||
                properties.getProperty(PMSDefinitions.INITIAL_CONTEXT_FACTORY_ID) == null ||
                properties.getProperty(PMSDefinitions.WEB_SERVER_URL_ID) == null ||
                properties.getProperty(PMSDefinitions.GRAPHICS_PATH) == null ||
                properties.getProperty(PMSDefinitions.CHARTS_PATH) == null ||//Shahbaz
                properties.getProperty(PMSDefinitions.TEMP_FILE_PATH) == null ||
                properties.getProperty(PMSDefinitions.EXPORT_PATH) == null ||
                properties.getProperty(PMSDefinitions.APP_SERVER_LOG_FILE_ID) == null ||
                properties.getProperty(PMSDefinitions.WEB_SERVER_LOG_FILE_ID) == null ||
                properties.getProperty(PMSDefinitions.APACHE_HOME_ID) == null ||
                properties.getProperty(PMSDefinitions.DIR_ROOT_ID) == null ||
                properties.getProperty(PMSDefinitions.GROUP_FILE_ID) == null ||
                properties.getProperty(PMSDefinitions.CERT_FILE_ID) == null ||
                properties.getProperty(PMSDefinitions.USER_FILE_ID) == null ||
                properties.getProperty(PMSDefinitions.ADMIN_PASSWORD) == null ||
                properties.getProperty(PMSDefinitions.JKS_PASSWORD) == null ||
                properties.getProperty(PMSDefinitions.WEB_APPLICATION_FILE_ID) == null ||
                properties.getProperty(PMSDefinitions.REPORT_PROCESSING_TIME_DURATION) == null ||
                properties.getProperty(PMSDefinitions.LIST_DOWNLOAD_DELAY) == null ||
                properties.getProperty(PMSDefinitions.LIST_DOWNLOAD_PATH) == null ||
                properties.getProperty(PMSDefinitions.DELETE_BATCH_SIZE) == null ||
                properties.getProperty(PMSDefinitions.DELETE_PROCESS_DELAY) == null ||
                properties.getProperty(PMSDefinitions.GRAPHIC_ACCESS_URL_ID) == null ||
                properties.getProperty(PMSDefinitions.DELETE_USER_ID) == null  ||
                properties.getProperty(PMSDefinitions.FORM_UPLOAD_DIR) == null ||
                properties.getProperty(PMSDefinitions.SF_HISTORY_DAYS) == null ||

                properties.getProperty(PMSDefinitions.PREVIEW_DOMAIN) == null ||
                properties.getProperty(PMSDefinitions.EVENTS_DOMAIN) == null ||
                properties.getProperty(PMSDefinitions.CDN_FOR_IMAGES) == null ||
                properties.getProperty(PMSDefinitions.CDN_FOR_STATIC_CONTENTS) == null
                )
            {
                System.out.println(PMSDefinitions.PROCESS_NAME_ID+":"+properties.getProperty(PMSDefinitions.PROCESS_NAME_ID));
                System.out.println(PMSDefinitions.PROVIDER_URL_ID+":"+properties.getProperty(PMSDefinitions.PROVIDER_URL_ID));
                System.out.println(PMSDefinitions.INITIAL_CONTEXT_FACTORY_ID+":"+properties.getProperty(PMSDefinitions.INITIAL_CONTEXT_FACTORY_ID));
                System.out.println(PMSDefinitions.INITIAL_CONTEXT_FACTORY_ID+":"+properties.getProperty(PMSDefinitions.INITIAL_CONTEXT_FACTORY_ID));
                System.out.println(PMSDefinitions.WEB_SERVER_URL_ID+":"+properties.getProperty(PMSDefinitions.WEB_SERVER_URL_ID));
                System.out.println(PMSDefinitions.GRAPHICS_PATH+":"+properties.getProperty(PMSDefinitions.GRAPHICS_PATH));
                System.out.println(PMSDefinitions.CHARTS_PATH+":"+properties.getProperty(PMSDefinitions.CHARTS_PATH));//Shahbaz
                System.out.println(PMSDefinitions.TEMP_FILE_PATH+":"+properties.getProperty(PMSDefinitions.TEMP_FILE_PATH));
                System.out.println(PMSDefinitions.EXPORT_PATH+":"+properties.getProperty(PMSDefinitions.EXPORT_PATH));
                System.out.println(PMSDefinitions.APP_SERVER_LOG_FILE_ID+ ":"+properties.getProperty(PMSDefinitions.APP_SERVER_LOG_FILE_ID));
                System.out.println(PMSDefinitions.WEB_SERVER_LOG_FILE_ID+": "+properties.getProperty(PMSDefinitions.WEB_SERVER_LOG_FILE_ID));
                System.out.println(PMSDefinitions.APACHE_HOME_ID+": "+properties.getProperty(PMSDefinitions.APACHE_HOME_ID));
                System.out.println(PMSDefinitions.DIR_ROOT_ID+": "+properties.getProperty(PMSDefinitions.DIR_ROOT_ID));

                System.out.println(PMSDefinitions.EXPORT_PATH+": "+properties.getProperty(PMSDefinitions.EXPORT_PATH));
                System.out.println(PMSDefinitions.GROUP_FILE_ID+ ":"+properties.getProperty(PMSDefinitions.GROUP_FILE_ID));
                System.out.println(PMSDefinitions.CERT_FILE_ID+": "+properties.getProperty(PMSDefinitions.CERT_FILE_ID));
                System.out.println(PMSDefinitions.USER_FILE_ID+": "+properties.getProperty(PMSDefinitions.USER_FILE_ID));
                System.out.println(PMSDefinitions.ADMIN_PASSWORD+": "+properties.getProperty(PMSDefinitions.ADMIN_PASSWORD));
                System.out.println(PMSDefinitions.JKS_PASSWORD+": "+properties.getProperty(PMSDefinitions.JKS_PASSWORD));

                System.out.println(PMSDefinitions.WEB_APPLICATION_FILE_ID+": "+properties.getProperty(PMSDefinitions.WEB_APPLICATION_FILE_ID));
                System.out.println(PMSDefinitions.REPORT_PROCESSING_TIME_DURATION+": "+properties.getProperty(PMSDefinitions.REPORT_PROCESSING_TIME_DURATION));

                System.out.println(PMSDefinitions.LIST_DOWNLOAD_DELAY+": "+properties.getProperty(PMSDefinitions.LIST_DOWNLOAD_DELAY));
                System.out.println(PMSDefinitions.LIST_DOWNLOAD_PATH+": "+properties.getProperty(PMSDefinitions.LIST_DOWNLOAD_PATH));
                System.out.println(PMSDefinitions.DELETE_BATCH_SIZE+": "+properties.getProperty(PMSDefinitions.DELETE_BATCH_SIZE));
                System.out.println(PMSDefinitions.DELETE_PROCESS_DELAY+": "+properties.getProperty(PMSDefinitions.DELETE_PROCESS_DELAY));
                System.out.println(PMSDefinitions.DELETE_USER_ID+": "+properties.getProperty(PMSDefinitions.DELETE_USER_ID));
                System.out.println(PMSDefinitions.GRAPHIC_ACCESS_URL_ID+": "+properties.getProperty(PMSDefinitions.GRAPHIC_ACCESS_URL_ID));
                System.out.println(PMSDefinitions.FORM_UPLOAD_DIR+": "+properties.getProperty(PMSDefinitions.FORM_UPLOAD_DIR));

                System.out.println(PMSDefinitions.PREVIEW_DOMAIN+": "+properties.getProperty(PMSDefinitions.PREVIEW_DOMAIN));
                System.out.println(PMSDefinitions.EVENTS_DOMAIN+": "+properties.getProperty(PMSDefinitions.EVENTS_DOMAIN));

                System.out.println(PMSDefinitions.CDN_FOR_IMAGES+": "+properties.getProperty(PMSDefinitions.CDN_FOR_IMAGES));
                System.out.println(PMSDefinitions.CDN_FOR_STATIC_CONTENTS+": "+properties.getProperty(PMSDefinitions.CDN_FOR_STATIC_CONTENTS));

                System.out.println(PMSDefinitions.SF_HISTORY_DAYS+": "+properties.getProperty(PMSDefinitions.SF_HISTORY_DAYS));

                throw new ResourceMissingException(ResourceMissingException.getErrorCode(), CLASS_NAME, "getResources", "Resource content(s) missing from file (" + "" + ")", null);
            }
            return properties;
    }
}