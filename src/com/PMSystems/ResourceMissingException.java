package com.PMSystems;

/**
 * This class is used to present the missing of any resource content from resource file
 *
 * @version         1.0            06 Aug 2002
 * @author          Usman
 *
 * 06 Aug 2002      Usman          Initial Development of Class
 */

public class ResourceMissingException extends ResourceException {

    /**
     * @param errorCode as int
     * @param className as String
     * @param methodName as String
     * @param message as String
     * @param source as Exception
     */
    public ResourceMissingException(int errorCode, String className, String methodName, String message, Exception source) {
        super(errorCode, className, methodName, message, source);
    }

    /**
     * returns an error code assigned to this type of exception
     *
     * @return error code in form of int
     */
    public static int getErrorCode() {
        return 1004;
    }
}