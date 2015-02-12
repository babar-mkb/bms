package com.PMSystems;

/**
 * This class is used to present error occured in PSM system
 *
 * @version         1.0            06 Aug 2002
 * @author          Usman
 *
 * 06 Aug 2002      Usman          Initial Development of Class
 */

public abstract class PMSException extends Exception {

    private int errorCode;
    private String className;
    private String methodName;
    private String message;
    private Exception source;

    /**
     * @param errorCode as int
     * @param className as String
     * @param methodName as String
     * @param message as String
     * @param source as Exception
     */
    public PMSException(int errorCode, String className, String methodName, String message, Exception source) {
        super("ERROR-CODE=" + errorCode + "\tCLASS=" + className + "\tMETHOD=" + methodName + "\tMESSAGE=" + message + ((source == null) ? "" : "\tSOURCE=" + source.getMessage()));
        this.errorCode = errorCode;
        this.className = className;
        this.methodName = methodName;
        this.message = message;
        this.source = source;
    }

    /**
     * @return Exception
     */
    public Exception getSource() {
        return source;
    }

    /**
     * returns an error code assigned to this type of exception
     *
     * @return error code in form of int
     */
    public static int getErrorCode() {
        return 1001;
    }
}