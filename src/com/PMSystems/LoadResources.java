package com.PMSystems;

import java.util.*;
import java.io.*;

/**
 * This class is used to load the property/resource/conf file into Properties object
 *
 * @version         1.0            06 Aug 2002
 * @author          Usman
 *
 * 06 Aug 2002      Usman          Initial Development of Class
 */

public class LoadResources {

    private static final String CLASS_NAME = "LoadResources";

    /**
     * @param resource_path_name
     * @return Properties
     * @throws ResourceFileException
     */
    public static Properties getResources(String resource_path_name) throws ResourceFileException {
        Properties properties = new Properties();
        try {
            FileInputStream fileIn = new FileInputStream(resource_path_name);
            properties.load(fileIn);
        }catch(FileNotFoundException e) {
            throw new ResourceFileException(ResourceFileException.getErrorCode(), CLASS_NAME, "getResources", "reqource/properties file not found (" + resource_path_name + ") ", e);
        }catch(IOException e) {
            throw new ResourceFileException(ResourceFileException.getErrorCode(), CLASS_NAME, "getResources", "unable to read reqource/properties file (" + resource_path_name + ") ", e);
        }
        return properties;
    }
}