package com.PMSystems;

import java.util.*;

import javax.servlet.http.*;
import java.io.*;
import java.rmi.RemoteException;
import java.sql.*;

import com.PMSystems.*;
import com.PMSystems.logger.*;
import com.PMSystems.sejbs.*;
import com.PMSystems.util.*;
import com.PMSystems.mail.*;
import com.PMSystems.mail.smtp.*;
import com.PMSystems.dbbeans.*;
import com.PMSystems.SFIntegration.*;
import com.PMSystems.webstats.*;

import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class SignupFormManager {

    private static AlphaQueryServer alphaQueryServer = null;
    private static DataQueryServer dataQueryServer = null;
    private static ComplexQueryServer complexQueryServer = null;
    private static ComplexQueryServer2 complexQueryServer2 = null;

    private static String formSavingPath = "";
    private static PMSResources pmsResources = null;
    private static HashMap basicFields = new HashMap();

    static {
        basicFields.put("First Name", "firstName");
        basicFields.put("Middle Name", "middleName");
        basicFields.put("Last Name", "lastName");
        basicFields.put("Address", "address");
        basicFields.put("City", "city");
        basicFields.put("State", "state");
        basicFields.put("Zip", "zip");
        basicFields.put("Country", "country");
        basicFields.put("Marital Status", "maritalStatus");
        basicFields.put("Occupation", "occupation");
        basicFields.put("Job Status", "jobStatus");
        basicFields.put("Household Income", "householdIncome");
        basicFields.put("Education Level", "educationLevel");
        basicFields.put("Area Code", "areaCode");
        basicFields.put("Gender", "gender");
        basicFields.put("Telephone", "telephone");
        basicFields.put("Company", "company");
        basicFields.put("Industry", "industry");
        basicFields.put("Date of Birth", "dateOfBirth");
        basicFields.put("Job Title", "jobTitle");
        basicFields.put("Email type", "emailType");

        alphaQueryServer = EJBHomesFactory.getAlphaQueryServerRemote();
        dataQueryServer = EJBHomesFactory.getDataQueryServerRemote();
        complexQueryServer = EJBHomesFactory.getComplexQueryServerRemote();
        complexQueryServer2 = EJBHomesFactory.getComplexQueryServer2Remote();
        try {
            pmsResources = PMSResources.getInstance();
        }
        catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        formSavingPath = pmsResources.getFormUploadDir();
    }

    private SignupFormManager() {
    }
    /**
     *
     * @param sfdb
     * @return
     */
    public static SignupFormDataBean saveFormInDb(SignupFormDataBean sfdb) {

        if(sfdb==null)
            return null;
        try {
            //checking if Trial Signup Form's limit
            UserInfo userInfo = UserManager.getUserInfo(sfdb.getUserId());
            if(userInfo == null || (userInfo.getPackageType().equals(PMSDefinitions.PACKAGE_TYPE_TRIAL) && isTrialLimitExceed(userInfo.getUserID())))
                return null;

            sfdb.setCreationDate(new Timestamp(System.currentTimeMillis()));
            sfdb.setUpdationDate(new Timestamp(System.currentTimeMillis()));
            return dataQueryServer.createForm(sfdb);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return null;
    }
    /**
     *
     * @param sfdb
     * @return
     */
    public static boolean updateForm(SignupFormDataBean sfdb) {

        if(sfdb==null || Default.defaultLong(sfdb.getFormId())<=0)
            return false;

        try {
            return dataQueryServer.updateForm(sfdb)!=null;

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }

    public static SignupFormDataBean updateForm(SignupFormDataBean sfdb, boolean reGenerateHtml) {
        try {

            if(sfdb ==null || Default.defaultLong(sfdb.getFormId()) < 1)
                return null;

            String htmlCode = sfdb.getFormHtmlCode();
            if(htmlCode != null && htmlCode.indexOf("<body onload='javascript:getServerData();'>") == -1) {
                htmlCode = "<body onload='javascript:getServerData();'>" + htmlCode + "</body>";
            }
            if(htmlCode != null && !htmlCode.startsWith("<html>")) {
               htmlCode = "<html>" + htmlCode + "</html>";
            }
            sfdb.setFormHtmlCode(htmlCode);


            // generating new html code
            sfdb.setUpdationDate(new Timestamp(System.currentTimeMillis()));
            if(reGenerateHtml){
                System.out.println("going to Re- generate form html code +++ " + sfdb.getFormId());
                generateHtmlCode(sfdb);
            }

            System.out.println("Writing html in form");
            // updating into file
            writeToFile(sfdb);

            // updating html into database
            return dataQueryServer.updateForm(sfdb);
        }
        catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return null;
    }
    /**
     *
     * @param sfdb
     * @return
     */
    public static SignupFormDataBean saveFormAsFile(SignupFormDataBean sfdb) {

        File file = null;
        try {
            //checking if Trial Signup Form's limit
            UserInfo userInfo = UserManager.getUserInfo(sfdb.getUserId());
            if(userInfo == null || (userInfo.getPackageType().equals(PMSDefinitions.PACKAGE_TYPE_TRIAL) && isTrialLimitExceed(userInfo.getUserID())))
                return null;

            sfdb = saveFormInDb(sfdb);
            generateHtmlCode(sfdb);
            file = new File(formSavingPath +
                            PMSEncoding.encode(sfdb.getFormId()) + ".html");

            file.createNewFile();

            FileOutputStream writer = new FileOutputStream(file);
            writer.write(sfdb.getFormHtmlCode().getBytes());
            writer.close();

            sfdb.setFormPath(pmsResources.getWebServerURL().replaceFirst("http://","https://") + "signups/" +
                             file.getName());
            sfdb.setFileName(file.getName());
            updateForm(sfdb, false);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return sfdb;
    }

    private static void writeToFile(SignupFormDataBean sfdb) {
        File file = null;
        try {
            // reGenerate html code before updating it in file
            file = new File(formSavingPath + sfdb.getFileName());
            if(file.exists()) {
                FileOutputStream writer = new FileOutputStream(file);
                writer.write(sfdb.getFormHtmlCode().getBytes());
                writer.close();
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
    }

    private static void generateHtmlCode(SignupFormDataBean bean) {
        String formID = PMSEncoding.encode(bean.getFormId());
        StringBuffer sb = new StringBuffer();
        try {
            PMSResources pmsResources = PMSResources.getInstance();
            Vector fields = Default.fromCSV(bean.getFormFields()); // make a vector of basic fields
            fields.toArray();
            Vector lists = Default.fromCSV(bean.getLists()); // make a vector of lists
            Hashtable listTable = complexQueryServer2.getListNames(lists); // To get list names that were selected by User
            Vector customFieldsVec = Default.fromCSV(bean.getCustomFields()); // make vector custom fields
            sb.append("<html>");
            sb.append("<head><SCRIPT src='" + pmsResources.getWebServerURL().replaceFirst("http://","https://")
                      + "js/works.js' type='text/javascript'></SCRIPT>"
                      + "</head><body onload='javascript:getServerData();'>\n");
            sb.append("<table align='center' border='0' width='100%'><tr><td style='font-color:green;' align='"
                      + "center'><h2>" + bean.getName() + "</h2></td></tr></table>\n");
            sb.append("<form name='signupForm' id='" + formID + "' action= '" + pmsResources.getWebServerURL().replaceFirst("http://","https://") + "form/"
                      + "FormSubmissionHandler.jsp' method='POST' onSubmit=\"javascript: return validateForm('" + formID + "');\">\n");

            sb.append("<input type='hidden' name='formId' value='" +
                      formID + "'>\n");
            sb.append("<table width='100%' align='center' border='0'>");
            sb.append("<tr><td align='right'><FONT color=#ff0000>*<"
                + "/FONT><b>Email</b></td>"
                + "<td><input type='text' style='width:165px;' name='email'>"
                + "</td></tr>\n");

      for (int i = 0; i < fields.size(); i++) {
          String fieldName = (String) fields.get(i);
          String value = "";
          if ( (value = (String) basicFields.get( (String) fieldName)) != null) {
              if(fieldName.equals("Gender")) {
                  sb.append("<tr><td width='40%' align='right'>" + fieldName +
                        "</td><td width='60%' align='left'><select style='width:165px;' name='" +
                        value + "'><option value='Male'>Male</option><option value='Female'>"
                        + "Female</option></select></td></tr>\n");
              }
              else if(fieldName.equals("Email type")) {
                  sb.append("<tr><td width='40%' align='right'>" + fieldName +
                        "</td><td width='60%' align='left'><select style='width:165px;' name='" +
                        value + "'><option value='HTML'>HTML</option><option value='Text'>"
                        + "Text</option></select></td></tr>\n");
              }
              else if(fieldName.equals("Country")) {
                  File file = new File("/usr/services/countries.dat");
                  StringBuffer options = new StringBuffer();
                  if(file.exists()) {
                      String line = "";
                      FileReader fr = new FileReader(file);
                      BufferedReader br = new BufferedReader(fr);
                      while((line=br.readLine())!=null) {
                          options.append(line + "\n");
                      }
                  }
                  sb.append("<tr><td width='40%' align='right'>" +
                            fieldName +
                            "</td><td width='60%' align='left'><select style='width:165px;' name='" +
                            value +
                            "'>" + options + "</select></td></tr>\n");

              } else if(fieldName.equalsIgnoreCase("Date of Birth")) {
                  sb.append("<tr><td width='40%' align='right'>" + fieldName +
                            "</td><td width='60%' align='left'><input style='width:165px;' type='text' name='" +
                            value + "'>&nbsp;<span style='color:#808080;'>(e.g. 1980-07-28)</span></td></tr>\n");
              } else {
                  sb.append("<tr><td width='40%' align='right'>" + fieldName +
                        "</td><td width='60%' align='left'><input style='width:165px;' type='text' name='" +
                        value + "'></td></tr>\n");
              }
          }
      }

      for (int i = 0; i < customFieldsVec.size(); i++) {
          String fieldName = (String) customFieldsVec.get(i);
          sb.append("<tr><td align='right'>" + customFieldsVec.get(i) +
                    "</td><td align='left'>"
                    +
              "<input type='text' style='width:165px;' name='frmFld_"
                    + customFieldsVec.get(i) + "'>\n");
      }

      sb.append("<tr><td align='center' colspan='2'><br><font color=#ff0000>*</font>I would like to receive following: </td></tr>\n");

         Enumeration listNumbers = listTable.keys();
         while(listNumbers.hasMoreElements()) {
             String key = (String)listNumbers.nextElement();
             sb.append("<tr><td align='right'><input type='checkbox' name='lists' value='"
                       + PMSEncoding.encode(key) + "'></td><td align='left'>"
                       + listTable.get(key) + "</td></tr>");
         }

         sb.append(
             "<br><tr><td colspan='2' valign='bottom' align='center'><img style='display:none' id='image' src='' border='1'></td></tr> <tr><td valign='bottom' align='right'><input type='hidden' name='chValue' id='chValue'>\n"
             +
             "<br><font color=#ff0000>*</font>Enter text here</td><td valign='bottom'> <input type='text' "
             + "name='uText'>\n "
             + "</td></tr>");
      sb.append("<tr><td colspan='2' valign='middle' align='center'><font color='GRAY' size='2'>Typing the characters from a picture helps ensure "
                + "that a person, not an automated program, is using this form.</font></td></tr>");
            sb.append("<tr><td colspan='2' align='center'><br><input value='Sign Me Up' type='submit'></td></tr>\n");
            sb.append("</table>\n");
            sb.append("\n</form>\n</body>\n</html>");
            bean.setFormHtmlCode(sb.toString());
        }
        catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
    }

    public static SignupFormDataBean getForm(long formId) {
        try {
            return dataQueryServer.getSignupForm(formId);
        }
        catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return null;
    }

    /**
     * This method will return the customer current lists.
     *
     * @param userId as String
     * @return currentLists
     */
    public static Vector getUserLists(String userId, long customerNumber, String userRole) {
        Vector currentLists = new Vector();
        try {
            currentLists = (Vector)complexQueryServer.getListSubscribersCount(userId, new Long(customerNumber), userRole);
        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }//end try-catch

        return currentLists;
    }//end getUserLists()


    /**
     * Subscripion method called for signup
     * @param fromEmail
     * @param userId
     * @param listCheckbox
     * @param request
     * @return
     */
    public static boolean signup(SignupFormDataBean bean, HttpServletRequest request) {

        boolean isSignedUp = false, isUserIDSame = false;
        if(bean == null)
            return false;

        String uid = bean.getUserId();
        Vector clientCustomDataVec = new Vector();
        Hashtable clientCustomData = new Hashtable();
        String fromEmail = bean.getConfirmationEmail();
        if (fromEmail.indexOf('@') == -1) {
            fromEmail = fromEmail.replaceAll(" ", "_");
        }

        try {
            Vector listVec = new Vector();
            String[] lists = Default.secureInput(request.getParameterValues("lists"));

            for(int i = 0; i < lists.length; i++) {
                listVec.add(PMSEncoding.decode(lists[i]));
            }
            long sn = 0;

            String reqEmail = request.getParameter("email");
            if(reqEmail==null || reqEmail.trim().equals("") || !ValidationChecks.validateEmail(reqEmail)) {
                System.out.println("[SignupFormManager]: SIGNUP_FAILED invalid email: "+reqEmail);
                return false;
            }

            SubscriberInfo sub = new SubscriberInfo();
            sub.setAddressLine1(request.getParameter("addressLine1"));
            sub.setAddressLine2(request.getParameter("addressLine2"));

            /*
             * To cover bug (in HashMap basicFields), where 'Address' was mapped to 'address' field,
             */
            if(sub.getAddressLine1().equals(""))
                sub.setAddressLine1(request.getParameter("address"));

            String birthDate = Default.toDefault(request.getParameter("dateOfBirth"));
            if(Default.isValidDate(birthDate, "yyyy-MM-dd"))
                sub.setBirthDate(new java.sql.Timestamp(Default.parseDate(birthDate, "yyyy-MM-dd")));

            sub.setCity(request.getParameter("city"));
            sub.setCompany(request.getParameter("company"));
            sub.setCountryCode(request.getParameter("country"));
            sub.setEducationLevel(request.getParameter("educationLevel"));
            sub.setEmailType(request.getParameter("emailType"));
            sub.setFirstName(request.getParameter("firstName"));
            sub.setGender(request.getParameter("gender"));
            sub.setHouseholdIncome(request.getParameter("householdIncome"));
            sub.setIndustry(request.getParameter("industry"));
            sub.setJobStatus(request.getParameter("jobStatus"));
            sub.setLastName(request.getParameter("lastName"));
            sub.setMaritalStatus(request.getParameter("maritalStatus"));
            sub.setMiddleName(request.getParameter("middleName"));
            sub.setOccupation(request.getParameter("occupation"));

            sub.setStateCode(request.getParameter("stateCode"));
            /*
             * To cover bug (in HashMap basicFields), where 'Address' was mapped to 'address' field,
             * if address 1&2 are empty check 'address' parameter in request.
             */
            if(sub.getStateCode().equals(""))
                sub.setStateCode(request.getParameter("state"));

            sub.setStatus(PMSDefinitions.SUBSCRIBER_STATUS_SUBSCRIBE);
            sub.setSupress(PMSDefinitions.SUBSCRIBER_UNSUPRESSED);
            sub.setSubscribeDate(new Timestamp(System.currentTimeMillis()));
            sub.setTelephone(request.getParameter("telephone"));
            sub.setZip(request.getParameter("zip"));
            sub.setSource(request.getParameter("source"));
            sub.setSalesRep(request.getParameter("salesRep"));
            sub.setSalesStatus(request.getParameter("salesStatus"));
            sub.setAreaCode(request.getParameter("areaCode"));
            sub.setEmail(Default.toDefault(Default.secureEmail(request.getParameter("email"))));

            if (getClientFields(clientCustomDataVec, clientCustomData, request)) {
                sub.setCustomFields(clientCustomDataVec);
            }

            Vector subscriberInfoVec = new Vector();
            subscriberInfoVec.add(sub);

            if (uid == null) {
                return false;
            }
            UserInfo userInfo = loadUserInfo(uid);
            Vector insertedLists = new Vector();

            //Adding layout
            String layout = getSubscriberFieldLayout(request);

            for (int i = 0; i < listVec.size(); i++) {
                ListDataBean list = dataQueryServer.getList(Default.defaultLong( (String) listVec.get(i)));
                if (list == null) {
                    continue;
                }

                uid = list.getUserID();
                if (!userInfo.getUserRole().equalsIgnoreCase(PMSDefinitions.USER_ADMINISTRATOR_ACCOUNT) && !uid.equals((String)uid)) {
                    System.out.println("[SignupFormManager]: SubAccount CASE --- List Not matched =" +uid);
                    continue;
                }

                if (userInfo.getUserRole().equalsIgnoreCase(PMSDefinitions.USER_ADMINISTRATOR_ACCOUNT)) {
                    Vector compUsers = complexQueryServer.getCompanyUsers(new Long(userInfo.getCustomerNumber()));
                    if (compUsers == null || !compUsers.contains(uid)) {
                        System.out.println("[SignupFormManager]: Admin CASE --- userID Not matched =" + uid);
                        continue;
                    }
                }


                Vector resultantVec = SubscriberManager.addSubscribersWithLayout(subscriberInfoVec,(String)listVec.get(i), layout, userInfo);

                if (resultantVec.get(0).equals("0") &&   resultantVec.get(1).equals("0")) {
                    continue;

                } else {
                    isSignedUp = true;
                    SubscriberManager.logSignUpFormActivity(userInfo.getUserID(), sub.getEmail(), (String)listVec.get(i), Default.defaultInt(bean.getFormId()));
                }
                // to send alert for update Subscriber case too
                insertedLists.add(list.getName());
            }

            if(!isSignedUp)
                return false;

            if(bean.getSendConfirmationEmail().equals("Y")) {
                AlertMail.sendMail("Thank You For Subscribing", bean.getConfirmationEmailText(), fromEmail, new String[]{sub.getEmail()}, true);
            }

            // going to check SF form option
            boolean sfUpdated = false;
            boolean sfAdded = false;
            String sfMsg = "";

            SubscriberInfo subInfo = SubscriberManager.getSubscriber(sub.getEmail(), uid);
            sub.setSubscriberNumber(subInfo.getSubscriberNumber());
            sub.setConLeadId(subInfo.getConLeadId());
            sub.setSalesStatus(subInfo.getSalesStatus());

            System.out.println("Current SalesStatus +++ "+sub.getSalesStatus());
            if(!bean.getSource().equals(""))
                sub.setSource(bean.getSource());

            //Signup at SalesForce
            if(!bean.getSFEntity().equals(PMSDefinitions.SF_NO)){
                SalesForceManager.signUpAtSF(sub,bean,insertedLists, layout);
                
            //== if not salesforce then send alert here    
            } else if (bean.getSendAlertEmail().equals("Y")){
                sendAlertEmailNew(insertedLists, bean, request,sfMsg);
            }

            // Handling Signup Form Activity
            SubscriberManager.markActive(Default.toVector(""+subInfo.getSubscriberNumber()),PMSDefinitions.SUB_EVENT_SIGNUP);

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(new LogEntry("SignupFormBean","signup", "User ID = " + uid+ ", FromEmail:" + fromEmail));
            WebServerLogger.getLogger().log(ex);
            return false;

        }  finally {
            String paramNameValue = "PARAM NAME -- PARAM VALUE\n\n";
            Enumeration parameterNames = (Enumeration) request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String paramName = (String) parameterNames.nextElement();
                if (paramName.equals("listCheckbox")) {
                    continue; // because listCheckbox will be handled by next loop as listCheckbox
                } // is an array
                paramNameValue += paramName + " -- " +Default.secureInput(request.getParameter(paramName)) + "\n";
            }
            if (request.getParameterValues("listCheckbox") != null) {
                String[] listNumberArray = Default.secureInput(request.getParameterValues("listCheckbox"));
                for (int i = 0; i < listNumberArray.length; i++) {
                    paramNameValue += "listCheckbox[" + (i + 1) + "] -- "+ listNumberArray[i] + "\n";
                }
            }
            WebServerLogger.getLogger().log(new LogEntry("SignupFormBean","signup()","--- Parameters Name Value detail below ---\n\n"+paramNameValue));
        }

    } //end signup()

    private static boolean getClientFields(Vector clientCustomDataVec, Hashtable clientCustomData, HttpServletRequest request) {

        String FIELD_INITIAL = "frmFld";
        Enumeration fEnum = request.getParameterNames();
        for(;fEnum.hasMoreElements();) {
            String param = (String)fEnum.nextElement();
            if(param.startsWith(FIELD_INITIAL)){
                String values[] = request.getParameterValues(param);
                String joinedValue="";

                for(int i=0; values!=null && i<values.length;i++) {
                    joinedValue = joinedValue + values[i];
                    if( (i+1)!=values.length )
                        joinedValue = joinedValue +"  |  ";
                }//for

                param = param.substring(FIELD_INITIAL.length()+1);
                clientCustomDataVec.add(new SubscriberDetail(param, joinedValue));
                clientCustomData.put(param, joinedValue);
            }//if
        }//for
        return true;
    }//getClientFields()

    public static UserInfo loadUserInfo(String userName) {

        boolean systemError;
        UserInfo userInfo = new UserInfo();
        try {
            UserDataBean user = dataQueryServer.getUser(userName);

            if(user == null) {
                systemError = true;
                WebServerLogger.getLogger().log(new LogEntry("SignupFormBean.java", "loadUserInfo()", "Requested user not found"));
            } else {
                userInfo = new UserInfo();
                userInfo.setUserID(user.getUserID());
                userInfo.setFirstName(user.getFirstName());
                userInfo.setLastName(user.getLastName());
                userInfo.setPhone(user.getPhone());
                userInfo.setAddress1(user.getAddressLine1());
                userInfo.setAddress2(user.getAddressLine2());
                userInfo.setEmail(user.getEmail());
                userInfo.setFromEmail(user.getFromEmail());
                userInfo.setUserRole(user.getUserRole());
                userInfo.setURL(user.getUrl());
                userInfo.setUserLayout(user.getUserLayout());
                userInfo.setFormLayout(user.getFormLayout());
                userInfo.setSubscriberProfileLayout(user.getSubscriberProfileLayout());
                userInfo.setSendAutoReplyEmail(user.getSendAutoReplyEmail());
                userInfo.setCustomerNumber(user.getCustomerNumber().longValue());

            }//end if
            return userInfo;
        } catch (Exception ex) {
            systemError = true;
            WebServerLogger.getLogger().log(new LogEntry("SignupFormBean", "loadUserInfo()", "UserName not Found: "+userName));
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);

        }//end try-catch
        return null;
    }

    private static void sendConfirmationEmail(String sendTo, String message, long listNumber, String fromEmail) {
        if(sendTo==null || !sendTo.equalsIgnoreCase("Y")) {
            return;
        } else {
            try{
                message = (message==null || message.equals(""))? "Subscription Successful !!": message;
                String subject ="Thank You For Subscribing";
                sendMail(sendTo, message, subject, fromEmail);

            } catch(Exception e) {
                WebServerLogger.getLogger().log(new LogEntry("SignupFormBean","sendConfirmationEmail",
                        " sendTo:"+sendTo+" , listNumber:"+listNumber));
                WebServerLogger.getLogger().log(e);
            }
        }//else
    }//

    /**
     *
     * @param listNameVec
     * @param alertEmail
     * @param userID
     * @param formName
     * @param request
     */
    private static void sendAlertEmail(Vector listNameVec, String alertEmail, String userID, String formName, HttpServletRequest request) {

        if( alertEmail==null || alertEmail.equals("") ){
            return;
        } else {
            if(!Default.isValidEmail(alertEmail)) {
                WebServerLogger.getLogger().log(new LogEntry("SignupFormBean","sendAlertEmail[Invalid Email]",
                        " userID:"+userID+" , sendTo:"+alertEmail));
                    return;
            }
            String subDetail="<html>\r\n<body>\r\n\r\n<table width=650 align=\"center\">\r\n<tr>\r\n\t<td width=\"100%\" align=\"center\">\r\n\t\t<img src=\"http://www.bridgemailsystem.com/pms/graphics/logo.gif\">\r\n\t</td>\r\n</tr>\r\n</table>\r\n\r\n<table cellpadding=\"0\" cellspacing=\"0\" width=\"650\" align=\"center\" border=\"1\" style=\"font-size:11;font-family: Verdana, Arial, Helvetica;\">\r\n<tr>\r\n\t<td width=\"100%\" align=\"center\" colspan=\"2\">\r\n\r\n<tr>\r\n\t<td width=\"100%\" align=\"center\" colspan=\"2\">\r\n<b>\t\t";
            subDetail += "<h3>New Subscriber Signed Up</h3>";
            subDetail += "</b>\r\n\t</td>\r\n</tr>\r\n\r\n<tr>\r\n\t<td align=\"center\" width=\"65%\">\r\n\t\t";
            subDetail += "<b>Subscriber detail</b>";
            subDetail += "\r\n\t</td>\r\n\t<td align=\"center\" width=\"35%\">\r\n\t\t";
            subDetail += "<b>Added in following list(s)</b>";
            subDetail += "\r\n\t</td>\r\n</tr>\r\n\r\n<tr>\r\n\t<td align=\"left\" width=\"65%\">\r\n\t\t";
            try {
                Vector paramNamesVec = new Vector();
                Enumeration parameterNames = (Enumeration)request.getParameterNames();
                while(parameterNames.hasMoreElements()) {
                    String paramName = (String)parameterNames.nextElement();
                    paramNamesVec.add(paramName);
                }
                    if(paramNamesVec.contains("firstName")) {
                        String paramValue = Default.secureInput(request.getParameter("firstName"));
                        subDetail += "<b>First Name: ";
                        subDetail += "</b>";
                        subDetail += paramValue;
                        subDetail += "<br>\r\n\t\t";

                        paramNamesVec.remove("firstName");
                    }  if(paramNamesVec.contains("middleName")) {
                        String paramValue = Default.secureInput(request.getParameter("middleName"));
                        subDetail += "<b>Middle Name: ";
                        subDetail += "</b>";
                        subDetail += paramValue;
                        subDetail += "<br>\r\n                ";

                        paramNamesVec.remove("middleName");
                    }  if(paramNamesVec.contains("lastName")) {
                        String paramValue = Default.secureInput(request.getParameter("lastName"));
                        subDetail += "<b>Last Name: ";
                        subDetail += "</b>";
                        subDetail += paramValue;
                        subDetail += "<br>\r\n\t\t";

                        paramNamesVec.remove("lastName");
                    }  if(paramNamesVec.contains("addressLine1")) {
                        String paramValue = Default.secureInput(request.getParameter("addressLine1"));
                        subDetail += "<b>Address Line 1: ";
                        subDetail += "</b>";
                        subDetail += paramValue;
                        subDetail += "<br>\r\n\t\t";

                        paramNamesVec.remove("addressLine1");
                    }  if(paramNamesVec.contains("addressLine2")) {
                        String paramValue = Default.secureInput(request.getParameter("addressLine2"));
                        subDetail += "<b>Address Line 2: ";
                        subDetail += "</b>";
                        subDetail += paramValue;
                        subDetail += "<br>\r\n\t\t";

                        paramNamesVec.remove("addressLine2");
                    }  if(paramNamesVec.contains("city")) {
                        String paramValue = Default.secureInput(request.getParameter("city"));
                        subDetail += "<b>City: ";
                        subDetail += "</b>";
                        subDetail += paramValue;
                        subDetail += "<br>\r\n\t\t";

                        paramNamesVec.remove("city");
                    }  if(paramNamesVec.contains("stateCode") || paramNamesVec.contains("otherState")) {
                        String otherState = Default.secureInput(request.getParameter("otherState"));
                        if(otherState==null || otherState.trim().equals("")) {
                            String paramValue = Default.secureInput(request.getParameter("stateCode"));
                            subDetail += "<b>State Code: ";
                            subDetail += "</b>";
                            subDetail += paramValue;
                            subDetail += "<br>\r\n\t\t";

                        } else {
                            subDetail += "<b>State: ";
                            subDetail += "</b>";
                            subDetail += otherState;
                            subDetail += "<br>\r\n\t\t";

                        }
                        paramNamesVec.remove("stateCode");
                        paramNamesVec.remove("otherState");
                    }  if(paramNamesVec.contains("zip")) {
                        String paramValue = Default.secureInput(request.getParameter("zip"));
                        subDetail += "<b>Zip: ";
                        subDetail += "</b>";
                        subDetail += paramValue;
                        subDetail += "<br>\r\n\t\t";

                        paramNamesVec.remove("zip");
                    }  if(paramNamesVec.contains("country")) {
                        String paramValue = Default.secureInput(request.getParameter("country"));
                        subDetail += "<b>Country: ";
                        subDetail += "</b>";
                        subDetail += complexQueryServer.getCountryName(paramValue);
                        subDetail += "<br>\r\n\t\t";

                        paramNamesVec.remove("country");
                    }  if(paramNamesVec.contains("areaCode")) {
                        String paramValue = Default.secureInput(request.getParameter("areaCode"));
                        subDetail += "<b>Area Code: ";
                        subDetail += "</b>";
                        subDetail += paramValue;
                        subDetail += "<br>\r\n\t\t";

                        paramNamesVec.remove("areaCode");
                    } if(paramNamesVec.contains("maritalStatus")) {
                        String paramValue = Default.secureInput(request.getParameter("maritalStatus"));
                        subDetail += "<b>Marital Status: ";
                        subDetail += "</b>";
                        subDetail += paramValue;
                        subDetail += "<br>\r\n\t\t";

                        paramNamesVec.remove("maritalStatus");
                    }  if(paramNamesVec.contains("gender")) {
                        String paramValue = Default.secureInput(request.getParameter("gender"));
                        subDetail += "<b>Gender: ";
                        subDetail += "</b>";
                        subDetail += paramValue;
                        subDetail += "<br>\r\n\t\t";

                        paramNamesVec.remove("gender");
                    }  if(paramNamesVec.contains("telephone")) {
                        String paramValue = Default.secureInput(request.getParameter("telephone"));
                        subDetail += "<b>Telephone: ";
                        subDetail += "</b>";
                        subDetail += paramValue;
                        subDetail += "<br>\r\n\t\t";

                        paramNamesVec.remove("telephone");
                    }  if(paramNamesVec.contains("occupation")) {
                        String paramValue = Default.secureInput(request.getParameter("occupation"));
                        subDetail += "<b>Occupation: ";
                        subDetail += "</b>";
                        subDetail += paramValue;
                        subDetail += "<br>\r\n\t\t";

                        paramNamesVec.remove("occupation");
                    }  if(paramNamesVec.contains("jobStatus")) {
                        String paramValue = Default.secureInput(request.getParameter("jobStatus"));
                        subDetail += "<b>Job Status: ";
                        subDetail += "</b>";
                        subDetail += paramValue;
                        subDetail += "<br>\r\n\t\t";

                        paramNamesVec.remove("jobStatus");
                    }  if(paramNamesVec.contains("householdIncome")) {
                        String paramValue = Default.secureInput(request.getParameter("householdIncome"));
                        subDetail += "<b>Household Income: ";
                        subDetail += "</b>";
                        subDetail += paramValue;
                        subDetail += "<br>\r\n\t\t";

                        paramNamesVec.remove("householdIncome");
                    }  if(paramNamesVec.contains("educationLevel")) {
                        String paramValue = Default.secureInput(request.getParameter("educationLevel"));
                        subDetail += "<b>Education: ";
                        subDetail += "</b>";
                        subDetail += paramValue;
                        subDetail += "<br>\r\n\t\t";

                        paramNamesVec.remove("educationLevel");
                    }  if(paramNamesVec.contains("company")) {
                        String paramValue = Default.secureInput(request.getParameter("company"));
                        subDetail += "<b>Company: ";
                        subDetail += "</b>";
                        subDetail += paramValue;
                        subDetail += "<br>\r\n\t\t";

                        paramNamesVec.remove("company");
                    }  if(paramNamesVec.contains("industry")) {
                        String paramValue = Default.secureInput(request.getParameter("industry"));
                        subDetail += "<b>Industry: ";
                        subDetail += "</b>";
                        subDetail += paramValue;
                        subDetail += "<br>\r\n\t\t";

                        paramNamesVec.remove("industry");
                    }  if(paramNamesVec.contains("jobTitle")) {
                        String paramValue = Default.secureInput(request.getParameter("jobTitle"));
                        subDetail += "<b>Job Title: ";
                        subDetail += "</b>";
                        subDetail += paramValue;
                        subDetail += "<br>\r\n\t\t";

                        paramNamesVec.remove("jobTitle");
                    }  if(paramNamesVec.contains("companySize")) {
                        String paramValue = Default.secureInput(request.getParameter("companySize"));
                        subDetail += "<b>Company size: ";
                        subDetail += "</b>";
                        subDetail += paramValue;
                        subDetail += "<br>\r\n\t\t";

                        paramNamesVec.remove("companySize");
                    }  if(paramNamesVec.contains("email")) {
                        String paramValue = Default.secureEmail(request.getParameter("email"));
                        subDetail += "<b>Email: ";
                        subDetail += "</b>";
                        subDetail += paramValue;
                        subDetail += "<br>\r\n\t\t";

                        paramNamesVec.remove("email");
                    }  if(paramNamesVec.contains("dateOfBirthDay")) {
                        String paramValue = Default.secureInput(request.getParameter("dateOfBirthDay"));
                        subDetail += "<b>Date of Birth: ";
                        subDetail += "</b>";
                        subDetail += paramValue;
                        subDetail += "&nbsp;&#045;\r\n\t\t";

                        paramNamesVec.remove("dateOfBirthDay");
                    }  if(paramNamesVec.contains("dateOfBirthMonth")) {
                        String paramValue = Default.secureInput(request.getParameter("dateOfBirthMonth"));
                        subDetail += "";
                        subDetail += paramValue;
                        subDetail += "&nbsp;&#045;\r\n\t\t";

                        paramNamesVec.remove("dateOfBirthMonth");
                    }  if(paramNamesVec.contains("dateOfBirthYear")) {
                        String paramValue = Default.secureInput(request.getParameter("dateOfBirthYear"));
                        subDetail += paramValue;
                        subDetail += "<br>\r\n\t\t";

                        paramNamesVec.remove("dateOfBirthYear");
                    }

                    subDetail += "</b>\r\n\t</td>\r\n\t<td align=\"left\" valign=\"top\" width=\"35%\">\r\n\t&#183;&nbsp;";

                    //Adeel
                    if (listNameVec != null && listNameVec.size() != 0) {
                        for (int i = 0; i < listNameVec.size(); i++) {
                            subDetail += listNameVec.get(i);
                            if(i == (listNameVec.size() - 1)) {
                                break;
                            }
                            subDetail += "<br>\r\n\t\t&#183;&nbsp;";
                        }
                    }

                    subDetail += "<br>\r\n\t</td>\r\n</tr>\r\n\r\n";

                    boolean flag = false;
                    for (int j = 0; j < paramNamesVec.size(); j++) { // to get customized fields from request
                        String paramName = (String) paramNamesVec.elementAt(j);
                        String paramValue = Default.secureInput(request.getParameter(paramName));
                        if (paramName.startsWith("frmFld_")) {
                            if (!flag) {
                                subDetail += "<tr>\r\n\t<td align=\"center\" colspan=\"2\">\r\n\t\t<b>Subscriber Customized detail</b>";
                                subDetail += "\r\n\t</td>\r\n</tr>\r\n\r\n<tr>\r\n\t<td align=\"left\" colspan=\"2\">\r\n\t\t";
                                flag = true;
                            }
                            int _indexOf = paramName.indexOf("_");
                            subDetail += "<b>"+paramName.substring(_indexOf + 1,
                                paramName.length()) + ": </b>&nbsp;" + paramValue
                                + "<br>\r\n\t\t";
                        }
                    }
                    if(flag) {
                        subDetail += "\r\n\t</td>\r\n</tr>";
                    }
                    subDetail += "\r\n</table>\r\n\r\n\r\n</body>\r\n</html>";

                    MailResources resources = new MailResources();
                    AlertMail.sendMail("Sign Up Alert - "+formName,subDetail, resources.getSystemAddress(),
                                       new String[]{alertEmail}, true);

            } catch(Exception e) {
                WebServerLogger.getLogger().log(new LogEntry("SignupFormBean","sendAlertEmail",
                        " sendTo : " + alertEmail));
                WebServerLogger.getLogger().log(e);
            }
        }//else
    }//

    private static void sendAlertEmailNew(Vector listNameVec, SignupFormDataBean bean, HttpServletRequest request, String sfMsg){

        try {
            String webServerURL = PMSResources.getInstance().getWebServerURL();
            System.out.println("going to send new signup alert");
            StringBuffer body = new StringBuffer();

            // Basic Fields
            Vector paramNamesVec = new Vector();
            Enumeration parameterNames = (Enumeration)request.getParameterNames();
            while(parameterNames.hasMoreElements()) {
                String paramName = (String)parameterNames.nextElement();
                paramNamesVec.add(paramName);
            }

            String email = "";
            if(paramNamesVec.contains("email"))
                email = Default.secureEmail(request.getParameter("email"));
            else{
                System.out.println("Email NotFound in SigupForm [SignupFormManager]");
                return;
            }

            body.append("\n<html>\n<head>\n<link HREF=\"/pms/pms2.css\" rel=\"STYLESHEET\" TYPE=\"text/css\"/>\n<link HREF=\"/pms/jwb.css\" rel=\"STYLESHEET\" TYPE=\"text/css\"/>\n\n</head>\n<body leftmargin=\"0\" topmargin=\"0\" marginwidth=\"0\" marginheight=\"0\" >\n<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"90%\">\n<tr><td id=\"logo\"><img src=\"");
            body.append(webServerURL+"graphics/logo.gif\" alt=\"BridgeMail Systems\"><br>&nbsp;</td></tr>\n</table>\n\n<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"90%\" align=\"center\" id=\"mainWrapper\">\n<tr><td id=\"mainContentCell\" align=\"left\" style=\"font-family:arial;font-size:16px;font-weight: bold;\">\n");
            body.append("New Subscriber Signed Up : "+email+"</td></tr>\n<tr><td width=100%><hr style=\"color: #666699; height: 5px;\"/>&nbsp;</td></tr>\n</table>\n\n\n\n<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"90%\" align=\"center\" id=\"mainWrapper\" style=\"font-family:Arial, Helvetica, sans-serif; font-size:11px;\">\n<tr><td colspan=2 style=\"color:BLUE;\" align=center>");
            body.append("<b>Form Name:  "+bean.getName()+"</b><br>");


            body.append("</td></tr>\n<tr><td colspan=2 style=\"background-color:#CCCCFE; padding:5px; font-size:14px;font-weight: bold;\"> Subscriber Detail </td></tr>\n<tr><td colspan=2>&nbsp;</td></tr>\n<tr><td width=25%><b>Added to List(s):</td>");

            //Signup Form(s) list(s)
            if (listNameVec != null && listNameVec.size() != 0) {
                for (int i = 0; i < listNameVec.size(); i++) {
                    body.append("<td>"+(String) listNameVec.get(i)+"</td></tr>\n<tr><td>&nbsp;</td>");
                }
            }


            body.append("<td>&nbsp;</td></tr>\n");

            body.append("<tr><td width=25%><b>Email :</b></td><td>"+email+"</td></tr>\n");

            if(paramNamesVec.contains("firstName")){
                body.append("<tr><td width=25%><b>First Name :</b></td><td>"+Default.secureInput(request.getParameter("firstName"))+"</td></tr>\n");
            }
            if(paramNamesVec.contains("middleName")){
                body.append("<tr><td width=25%><b>Middle Name :</b></td><td>"+Default.secureInput(request.getParameter("middleName"))+"</td></tr>\n");
            }
            if(paramNamesVec.contains("lastName")){
                body.append("<tr><td width=25%><b>Last Name :</b></td><td>"+Default.secureInput(request.getParameter("lastName"))+"</td></tr>\n");
            }
            if(paramNamesVec.contains("company")){
                body.append("<tr><td width=25%><b>Company :</b></td><td>"+Default.secureInput(request.getParameter("company"))+"</td></tr>\n");
            }
            if(paramNamesVec.contains("industry")){
                body.append("<tr><td width=25%><b>Industry :</b></td><td>"+Default.secureInput(request.getParameter("industry"))+"</td></tr>\n");
            }

            if(paramNamesVec.contains("jobStatus")){
                body.append("<tr><td width=25%><b>Job Status :</b></td><td>"+Default.secureInput(request.getParameter("jobStatus"))+"</td></tr>\n");
            }
            if(paramNamesVec.contains("occupation")){
                body.append("<tr><td width=25%><b>Occupation :</b></td><td>"+Default.secureInput(request.getParameter("occupation"))+"</td></tr>\n");
            }

            if(paramNamesVec.contains("gender")){
                body.append("<tr><td width=25%><b>Gender :</b></td><td>"+Default.secureInput(request.getParameter("gender"))+"</td></tr>\n");
            }
            if(paramNamesVec.contains("maritalStatus")){
                body.append("<tr><td width=25%><b>Maital Status :</b></td><td>"+Default.secureInput(request.getParameter("maritalStatus"))+"</td></tr>\n");
            }
            if(paramNamesVec.contains("dateOfBirth")){
                body.append("<tr><td width=25%><b>Date Of Birth :</b></td><td>"+Default.secureInput(request.getParameter("dateOfBirth"))+"</td></tr>\n");
            }
            if(paramNamesVec.contains("educationLevel")){
                body.append("<tr><td width=25%><b>Education Level :</b></td><td>"+Default.secureInput(request.getParameter("educationLevel"))+"</td></tr>\n");
            }
            if(paramNamesVec.contains("householdIncome")){
                body.append("<tr><td width=25%><b>House Hold Income :</b></td><td>"+Default.secureInput(request.getParameter("householdIncome"))+"</td></tr>\n");
            }

            if(paramNamesVec.contains("city")){
                body.append("<tr><td width=25%><b>City :</b></td><td>"+Default.secureInput(request.getParameter("city"))+"</td></tr>\n");
            }
            if(paramNamesVec.contains("state")){
                body.append("<tr><td width=25%><b>State :</b></td><td>"+Default.secureInput(request.getParameter("state"))+"</td></tr>\n");
            }
            if(paramNamesVec.contains("zip")){
                body.append("<tr><td width=25%><b>Zip :</b></td><td>"+Default.secureInput(request.getParameter("zip"))+"</td></tr>\n");
            }
            if(paramNamesVec.contains("areaCode")){
                body.append("<tr><td width=25%><b>Area Code :</b></td><td>"+Default.secureInput(request.getParameter("areaCode"))+"</td></tr>\n");
            }
            if(paramNamesVec.contains("country")){
                body.append("<tr><td width=25%><b>Country :</b></td><td>"+Default.secureInput(request.getParameter("country"))+"</td></tr>\n");
            }
            if(paramNamesVec.contains("telephone")){
                body.append("<tr><td width=25%><b>Telephone :</b></td><td>"+Default.secureInput(request.getParameter("telephone"))+"</td></tr>\n");
            }

            if(paramNamesVec.contains("address")){
                body.append("<tr><td width=25%><b>Address :</b></td><td>"+Default.secureInput(request.getParameter("address"))+"</td></tr>\n");
            }

            boolean flag = false;
            for (int j = 0; j < paramNamesVec.size(); j++) { // to get customized fields from request
                String paramName = (String) paramNamesVec.elementAt(j);
                String paramValue = Default.secureInput(request.getParameter(paramName));
                if (paramName.startsWith("frmFld_")) {
                    if (!flag) {
                        body.append("<tr><td colspan=2>&nbsp;</td></tr>\n<tr><td colspan=2 style=\"background-color:#CCCCFE; padding:5px; font-size:14px;font-weight: bold;\"> Custom Field(s)</td></tr>\n");
                        flag = true;
                    }
                    int _indexOf = paramName.indexOf("_");
                    body.append("<tr><td> <b>"+paramName.substring(_indexOf + 1,paramName.length()) + " :</b></td><td>" + paramValue+ "</td></tr>\n");
                }
            }


/*            if(bean.getSFEntity().equals(PMSDefinitions.SF_LEAD) || bean.getSFEntity().equals(PMSDefinitions.SF_CONTACT)){
                body.append("\n<tr><td colspan=2>&nbsp;</td></tr>\n\n<tr><td colspan=2 style=\"background-color:#CCCCFE; padding:5px; font-size:14px;font-weight: bold;\"><img src=\""+webServerURL+"img/newui/sf_favicon.png\">&nbsp;Salesforce Detail</td></tr>\n");
                body.append("<tr><td colspan=2>&nbsp;</td></tr>");
                body.append("<tr><td colspan=2>");
                body.append(sfMsg);
                body.append("</td></tr>");
            }*/
            body.append("\n</table>\n\n");
            body.append("\n");

            body.append("<center>\n");
            body.append("<table>\n");
            body.append("  <tr>\n");
            body.append("    <td align=\"center\" style=\"font-size:11px;font-family:arial;\"><br>\n");
            body.append("      ");
            body.append(PMSDefinitions.COPYRIGHT_STATEMENT);
            body.append("<br>&nbsp;\n");
            body.append("    </td>\n");
            body.append("  </tr>\n");
            body.append("</table>\n");
            body.append("</center>\n");

            body.append("\n</body>\n</html>");

            MailResources resources = new MailResources();
            AlertMail.sendMail("Sign Up Alert - "+bean.getName(),body.toString(),resources.getSystemAddress(),
                               new String[]{bean.getAlertEmail()}, true);
        } catch (Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
    }








    private static void sendMail(String sendTo, String messageText, String subject, String fromEmail ) {
        try {
            /* Loading mail resources */
            MailResources resources = getMailResources();
            InternetAddress toAddress = new InternetAddress(sendTo);
            String[] smtpHosts = resources.getSMTPHosts();
            Properties info = new Properties();
            info.put("mail.smtp.host", smtpHosts[0]);
            info.put("mail.smtp.port", ""+resources.getSMTPPort());
            /*Get session*/
            Session session = Session.getInstance(info, null);
            /*Define message*/
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setSubject(subject);
            message.addRecipient(Message.RecipientType.TO,
                                 toAddress);
            message.setSentDate(new java.util.Date());
            message.setText(messageText);

            /* Send message */
            Transport.send(message);
        }catch(Exception e) {
            e.printStackTrace();
            WebServerLogger.getLogger().log(new LogEntry("SignupFormBean", "sendMail()", e.getMessage()));
            WebServerLogger.getLogger().log(e);
        }
    }///sendMail(String body)

    private static MailResources getMailResources() {
        try {
            MailResources mailResources = new MailResources();
            return mailResources;
        }
        catch (Exception e) {
            e.printStackTrace();
            WebServerLogger.getLogger().log(e);
            return null;
        }
    } //getMailResources()
    /**
     *
     * @param userId
     * @return
     */
    public static HashMap getFormsByUserid(String userId) {
        HashMap map = new HashMap(50);
        LinkedList list = null;
        SignupFormDataBean sb = null;
        try {
            list = dataQueryServer.getSignupFormsByUserId(userId);
            while((list.size() != 0) && ((sb = (SignupFormDataBean)list.removeFirst()) != null)) {
                map.put(sb.getName(), sb);
            }
            return map;
        }
        catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
            return null;
        }
    }

    public static boolean deleteForm(String name, HttpSession sess) {
        boolean isDeleted = false;
        try {
            HashMap map = (HashMap) sess.getAttribute("userForms");
            SignupFormDataBean bean = (SignupFormDataBean) map.get(name);
            isDeleted = dataQueryServer.deleteSignupForm(bean.getFormId());
            if(isDeleted) {
                map.remove(name);
                sess.setAttribute("userForms", map);
                return true;
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
            return false;
        }
        return false;
    }
    /**
     *
     * @param formId
     * @return
     */
    public static boolean deleteForm(int formId) {

        try {
            return dataQueryServer.deleteSignupForm(""+formId);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }

    /**
     * @param request
     * @return
     */
    public static String getSubscriberFieldLayout(HttpServletRequest request) {

        String layout = "";
        try {
            Vector fieldVec = SubscriberManager.getBasicFieldLayoutNames();
            for(int i=0;i<fieldVec.size();i++) {
                NameValue nv = (NameValue) fieldVec.get(i);
                layout += request.getParameter(nv.getName())==null? "": i==0?nv.getValue():","+nv.getValue();
            }
            // SignupForm Custom Field(s) Layout
            String FIELD_INITIAL = "frmFld";
            Enumeration fEnum = request.getParameterNames();
            for(;fEnum.hasMoreElements();) {
                String param = (String)fEnum.nextElement();
                if(param.startsWith(FIELD_INITIAL)){
                    layout += ","+param;
                }//if
            }//for


        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        System.out.println("[SignupFormManager] LAYOUT: "+layout);
        return layout;
    }
    /**
     *
     * @param bean
     * @return
     */
    public static LandingPageBean createLandingPage(LandingPageBean bean) {

        if(bean==null)
            return null;
        try {
            return alphaQueryServer.createLandingPage(bean);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return null;
    }
    /**
     * 
     * @param userInfo
     * @param pageBean
     * @param name
     * @return
     */
    public static LandingPageBean copyLandingPage(UserInfo userInfo, LandingPageBean pageBean, String name) {

	if(userInfo==null || pageBean==null || name.equals(""))
	    return null;

	try {
	    if(!userInfo.getUserID().equalsIgnoreCase(pageBean.getUserID()) && !pageBean.getUserID().equalsIgnoreCase(SysAdminManager.SYSADMIN_USER_ID)) {
		System.out.println("[SignupFormManager - COPY_PAGE]:  UNAUTHORIZED_ACCESS  pageId:"+pageBean.getPageId()+", session.UserId:"+userInfo.getUserID()+", page.userId:"+pageBean.getUserID());
		return null;
	    }

	    LandingPageBean newPage = getLandingPageById(pageBean.getPageId());
	    newPage.setUserID(userInfo.getUserID());
	    newPage.setName(name);
	    
	    newPage.setCategory(pageBean.getCategory());
	    newPage.setTitle(pageBean.getTitle());
	    newPage.setDescription(pageBean.getDescription());
	    newPage.setKeywords(pageBean.getKeywords());
	    	    
	    if(userInfo.getUserID().equalsIgnoreCase(pageBean.getUserID())) {
		newPage.setFormId(pageBean.getFormId());
		newPage.setImageId(pageBean.getImageId());
	    }
	    
	    newPage.setTagVec(pageBean.getTagVec());//copy tags
	    newPage.setHtmlCode(pageBean.getHtmlCode());//copy html
	    newPage.setDataVec(pageBean.getDataVec());//copy Hashed Data
	    
	    //=== Send create request for cloned Page & return results ===
	    return createLandingPage(newPage);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return null;
    }
    /**
     * 
     * @param userInfo
     * @param formBean
     * @param name
     * @return
     */
    public static SignupFormDataBean copySignUpForm(UserInfo userInfo, SignupFormDataBean formBean, String name) {

	if(userInfo==null || formBean==null || name.equals(""))
	    return null;

	try {
	    if(!userInfo.getUserID().equalsIgnoreCase(formBean.getUserId())) {
		System.out.println("[SignupFormManager - COPY_FORM]:  UNAUTHORIZED_ACCESS  formId:"+formBean.getFormId()+", session.UserId:"+userInfo.getUserID()+", form.userId:"+formBean.getUserId());
		return null;
	    }

	    SignupFormDataBean newBean = getForm(Default.defaultInt(formBean.getFormId()));
	    newBean.setUserId(userInfo.getUserID());
	    newBean.setName(name);
	    
	    newBean.setFormPath("");
	    newBean.setFileName("");
	    newBean.setFormId("");
	        
	    //=== Send create request for cloned Page & return results ===
	    return saveFormInDb(newBean);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return null;
    }


    /**
     * @param bean
     * @return
     */
    public static boolean updateLandingPage(LandingPageBean bean) {

        if(bean==null || bean.getPageId()<=0)
            return false;
        try {
            return alphaQueryServer.updateLandingPage(bean);
        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }
    /**
     * 
     * @param userId
     * @param newPageName
     * @param pageId
     * @return
     */
    public static boolean changeLandingPageName(String userId, String newPageName, int pageId) {
	
        if(userId.equalsIgnoreCase("") || newPageName.equals(""))
            return false;
        try {
            return alphaQueryServer.changeLandingPageName(userId, newPageName, pageId);
        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }
    /**
     * 
     * @param userId
     * @param newName
     * @param formId
     * @return
     */
    public static boolean changeSignUpFormName(String userId, String newName, int formId) {
	
        if(userId.equalsIgnoreCase("") || newName.equals(""))
            return false;
        try {
            return alphaQueryServer.changeSignUpFormName(userId, newName, formId);
        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }

    /**
     * 
     * @param userId
     * @param pageId
     * @param tagVec
     * @return
     */
    public static boolean createUpdateLandingPageTags(String userId, int pageId, Vector tagVec) {
	
        if(userId.equals("") || pageId<=0)
            return false;
        
        try {
            return alphaQueryServer.createUpdateLandingPageTags(userId, pageId, tagVec);
        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }
    /**
     * 
     * @param userId
     * @param formId
     * @param tagVec
     * @return
     */
    public static boolean createUpdateSignUpFormTags(String userId, int formId, Vector tagVec) {
	
        if(userId.equalsIgnoreCase("") || formId<=0)
            return false;
        
        try {
            return alphaQueryServer.createUpdateSignUpFormTags(userId, formId, tagVec);
            
        } catch(Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 
     * @param userId
     * @param pageId
     * @param imageId
     * @return
     */
    public static boolean updateLandingPageImage(String userId, int pageId, long imageId) {
	
        if(userId.equalsIgnoreCase("") || pageId<=0)
            return false;
        
        try {
            return alphaQueryServer.updateLandingPageImage(userId, pageId, imageId);
        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }
    /**
     * @param pageId
     * @param formId
     * @return
     */
    public static boolean createUpdateLandingPageForm(int pageId, int formId) {

        if(pageId<=0)
            return false;
        try {
            return alphaQueryServer.createUpdateLandingPageForm(pageId, formId);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }
    /**
     * @param pageId
     * @param status
     * @return
     */
    public static boolean changeLandingPageStatus(int pageId, String status){

        if(pageId<=0 || status.equals(""))
            return false;
        try {
            return alphaQueryServer.changeLandingPageStatus(pageId, status);
        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }
    /**
     *
     * @param bean
     * @return
     */
    public static String getLandingPageTrackingCode(LandingPageBean bean) {

        if(bean==null || bean.getPageId()<=0)
            return "";
        try {
            PMSResources resources = PMSResources.getInstance();
            String WEB_URL = "https://"+resources.getEventsDomain()+"/pms/events/viewpage.jsp?pageId="+bean.getURLCode();

            StringBuffer buff = new StringBuffer("");
            buff.append("<img src=\""+WEB_URL+"\" width=1 height=1 border=0>");
            return buff.toString();

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return "";
    }
    /**
     *
     * @param userId
     * @return
     */
    public static Vector getUniqueCategories(String userId) {

        try {
            return alphaQueryServer.getUniqueCategories(userId);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }
    /**
     * @param bean
     * @return
     */
    public static String getLandingPageURL(LandingPageBean bean, boolean isSecure) {

        if(bean==null || bean.getPageId()<=0)
            return "";

        try {
            PMSResources resources = PMSResources.getInstance();
            String WEB_URL = (isSecure? "https:":"http:")+"//"+resources.getEventsDomain()+"/pms/vlanding/"+bean.getURLCode()+"/";
            return WEB_URL;

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return "";
    }
    /**
     *
     * @param bean
     * @param isSecure
     * @return
     */
    public static String getSignUpFormURL(SignupFormDataBean bean, boolean isSecure) {

        if(bean==null || bean.getFormId().equals(""))
            return "";

        try {
            String fileName = bean.getFileName();
            String formId = (!fileName.equals("") && fileName.indexOf(".html")!=-1)? fileName.substring(0, fileName.indexOf(".html")): "";

            PMSResources resources = PMSResources.getInstance();
            String WEB_URL = (isSecure? "https:":"http:")+"//"+resources.getEventsDomain()+"/pms/vform/"+formId+"/";
            return WEB_URL;

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return "";
    }

    /**
     * @param pageId
     * @return
     */
    public static boolean deleteLandingPage(int pageId) {

        if(pageId<=0)
            return false;
        try {
            return alphaQueryServer.deleteLandingPage(pageId);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }
    /**
     * @param pageId
     * @return
     */
    public static LandingPageBean getLandingPageById(int pageId) {

        if(pageId<=0)
            return null;
        try {
            return alphaQueryServer.getLandingPageById(pageId);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return null;
    }
    /**
     * @param userId
     * @param name
     * @param pageId
     * @return
     */
    public static boolean isLandingPageExists(String userId, String name, int pageId) {

        try {
            return alphaQueryServer.isLandingPageExists(userId, name, pageId);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     * 
     * @param userId
     * @return
     */
    public static Vector getLandingPageHeaderStats(String userId) {

	if(userId.equals(""))
	    return new Vector();

	try {
	    return alphaQueryServer.getLandingPageHeaderStats(userId);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    WebServerLogger.getLogger().log(ex);
	}
	return new Vector();
    }
    /**
     *
     * @param userId
     * @param name
     * @param formId
     * @return
     */
    public static boolean isSignUpFormExists(String userId, String name, int formId) {

        try {
            return alphaQueryServer.isSignUpFormExists(userId, name, formId);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     * 
     * @param userId
     * @param searchText
     * @param searchType
     * @param status
     * @return
     */
    public static int countLandingPages(String userId, String searchText, String searchType, String status) {
	
        if(userId.equals(""))
            return 0;
        
        try {
            return alphaQueryServer.countLandingPages(userId, searchText, searchType, status);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return 0;
    }
    /**
     * 
     * @param userId
     * @param searchText
     * @param searchType
     * @return
     */
    public static int countSignUpForms(String userId, String searchText, String searchType) {
	
        if(userId.equals(""))
            return 0;
        
        try {
            return alphaQueryServer.countSignUpForms(userId, searchText, searchType);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return 0;
    }
    /**
     * @param userId
     * @return
     */
    public static Vector searchLandingPages(String userId, String searchText, String searchType, String status, int offset, int bucket) {

        if(userId.equals("") || offset<0 || bucket<=0)
            return new Vector();
        try {
            return alphaQueryServer.searchLandingPages(userId, searchText, searchType, status, offset, bucket);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }
    /**
     * @param formId
     * @param pageId
     * @param subscriberNumber
     * @return
     */
    public static boolean addSignupFormSubmission(int formId, int pageId, long subscriberNumber) {

        try {
            return alphaQueryServer.addSignupFormSubmission(formId, pageId, subscriberNumber);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     * @param pageId
     * @param subscriberNumber
     * @return
     */
    public static boolean addLandingPageView(int pageId, long subscriberNumber) {

        try {
            return alphaQueryServer.addLandingPageView(pageId, subscriberNumber);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     * 
     * @param userId
     * @param searchText
     * @param searchType
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector searchSignUpForms(String userId, String searchText, String searchType, int offset, int bucket) {

        if(userId.equals(""))
            return new Vector();

        try {
            return alphaQueryServer.searchSignUpForms(userId, searchText, searchType, offset, bucket);
            
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }

    /**
     * @param userId
     * @return
     */
    public static Vector getSignUpFormListByUserId(String userId) {
        return getSignUpFormListByUserId(userId, "");
    }
    /**
     *
     * @param userId
     * @param type
     * @return
     */
    public static Vector getSignUpFormListByUserId(String userId, String type) {

        if(userId.equals(""))
            return new Vector();
        try {
            return alphaQueryServer.getSignUpFormListByUserId(userId, type);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }

    /**
     *
     * @param sdb
     * @param email
     * @param bmsTK
     * @return
     */
    public static long getTrackingTicketForFormSubmission(SignupFormDataBean sdb, String email, long bmsTK) {

        try {
            SubscriberInfo subInfo = SubscriberManager.getSubscriber(Default.secureEmail(email), sdb.getUserId());
            if(subInfo==null || subInfo.getSubscriberNumber()<=0)
                return 0;

            if(bmsTK>0) {
                String ticketDetails[] = WebStatsManager.getTrackingTicketDetails(bmsTK);
                if(ticketDetails!=null && ticketDetails.length>4 && Default.defaultLong(ticketDetails[1])>0) {
                    if(Default.defaultLong(ticketDetails[1])==subInfo.getSubscriberNumber()) { //if subNum for 'bms.tk' matches submission subscriber.
                        return bmsTK;
                    }
                }
            }
            return WebStatsManager.getWebTrackingTicketForSubNum(subInfo.getSubscriberNumber());

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return 0;
    }
    /**
     *
     * @param userInfo
     * @param formBean
     * @param encFormId
     * @param pageId
     * @return
     */
    public static String prepareFormHTMLCode(UserInfo userInfo, SignupFormDataBean formBean, String encFormId, int pageId) {
        return prepareFormHTMLCode(userInfo, formBean, encFormId, pageId, false);
    }
    /**
     *
     * @param userInfo
     * @param formId
     * @param formCode
     * @return
     */
    public static String prepareFormHTMLCode(UserInfo userInfo, SignupFormDataBean formBean, String encFormId, int pageId, boolean isSSL) {

        StringBuffer buff = new StringBuffer(formBean.getFormHtmlCode());
        StringBuffer formBody = new StringBuffer("");
        try {

            formBody.append("\n\n<form name='signupForm' id='frm"+encFormId+"'");
            formBody.append(" action= 'https://"+PMSResources.getInstance().getWebServerDomainName()+"/pms/form/FormSubmissionHandler.jsp' method='POST'");
            formBody.append(" onSubmit=\"javascript: return validateBMSForm();\">");

            formBody.append("\n<input type='hidden' name='formId' value='"+encFormId+"'>");
            if(pageId>0) {
                formBody.append("\n<input type='hidden' name='pageId' value='"+PMSEncoding.encode(""+pageId)+"'>");
            }
            formBody.append("\n\n"+buff);
            formBody.append("\n\n</form>");

            formBody.append("\n\n<SCRIPT src='"+(isSSL? "https:":"http:")+"//"+PMSResources.getInstance().getEventsDomain()+"/pms/js/jquery.js' type='text/javascript'></SCRIPT>");
            formBody.append("\n\n<SCRIPT type='text/javascript'>var JS_DID='"+userInfo.getUserKey()+"'; var BMS_FORM_ID='frm"+encFormId+"';</SCRIPT>");
            formBody.append("\n\n<SCRIPT src='"+(isSSL? "https:":"http:")+"//"+PMSResources.getInstance().getEventsDomain()+"/pms/js/rFormUtils.js?"+userInfo.getUserKey()+"' type='text/javascript'></SCRIPT>");

            if(formBean.getHasChallengeText().equalsIgnoreCase("Y"))
                formBody.append("\n\n<SCRIPT type='text/javascript'>loadCAPTCHA();</SCRIPT>");

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return formBody.toString();
    }
    /**
     *
     * @param bean
     * @return
     */
    public static LandingPageTemplateBean createLandingPageTemplate(LandingPageTemplateBean bean) {

        if(bean==null || bean.getUserID().equals("") || bean.getName().equals(""))
            return null;
        try {
            return alphaQueryServer.createLandingPageTemplate(bean);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return null;
    }
    /**
     * @param templateId
     * @return
     */
    public static LandingPageTemplateBean getLandingPageTemplateById(int templateId) {

        if(templateId<=0)
            return null;
        try {
            return alphaQueryServer.getLandingPageTemplateById(templateId);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return null;
    }
    /**
     * @param userId
     * @return
     */
    public static Vector getLandingPageTemplateByUserId(String userId) {

        if(userId.equals(""))
            return new Vector();
        try {
            return alphaQueryServer.getLandingPageTemplateByUserId(userId);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }
    /**
     * @return
     */
    public static Vector getLandingPageTemplatesForSysAdmin() {

        try {
            return alphaQueryServer.getLandingPageTemplateByUserId("admin");

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }

    /**
     *
     * @param bean
     * @return
     * @throws RemoteException
     */
    public static boolean updateLandingPageTemplate(LandingPageTemplateBean bean) {

        if(bean==null || bean.getTemplateId()<=0)
            return false;
        try {
            return alphaQueryServer.updateLandingPageTemplate(bean);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }
    /**
     * @param templateId
     * @return
     */
    public static boolean deleteLandingPageTemplate(int templateId) {

        if(templateId<=0)
            return false;
        try {
            return alphaQueryServer.deleteLandingPageTemplate(templateId);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }
    /**
     *
     * @param userId
     * @param name
     * @param templateId
     * @return
     */
    public static boolean isLandingTemplateExists(String userId, String name, int templateId) {

        try {
            return alphaQueryServer.isLandingTemplateExists(userId, name, templateId);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     *
     * @param formId
     * @param pageId
     * @return
     */
    public static String getURLForViewForm(int formId, int pageId) {
        return getURLForViewForm(formId, pageId, false);
    }
    /**
     *  https://test.bridgemailsystem.com/pms/vform/js/BzAEqwsLr20Ns21Qm30BgyStRf/zdTyioEk17zcdZrt/
     * @param formId
     * @param isPreviewOnly
     * @return
     */
    public static String getURLForViewForm(int formId, int pageId, boolean isSSL) {

        if(formId<=0)
            return "";

        try {
            PMSResources resources = PMSResources.getInstance();
            String url = (isSSL? "https:":"http:")+"//"+resources.getEventsDomain()+"/pms/vform/"+PMSEncoding.encode(""+formId)+"/";
            url += (pageId>0? PMSEncoding.encode(""+pageId)+"/": "");
            return url;

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return "";
    }
    /**
     *
     * @param formId
     * @param pageId
     * @return
     */
    public static String getEmbedCodeForForm(int formId, int pageId, String additionalParas, boolean isSSL) {

        if(formId<=0)
            return "";

        try {
            PMSResources resources = PMSResources.getInstance();
            StringBuffer buff = new StringBuffer();
            buff.append("<SCRIPT TYPE='text/javascript' src='");
            String url = (isSSL? "https:":"http:")+"//"+resources.getEventsDomain()+"/pms/vform/js/"+PMSEncoding.encode(""+formId)+"/";
            url += (pageId>0? PMSEncoding.encode(""+pageId)+"/": "");

            if(!additionalParas.trim().equals("")) {
                url += ("?"+additionalParas);
            }
            buff.append(url);
            buff.append("'></SCRIPT>");

            return buff.toString();

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return "";
    }
    /**
     *
     * @param formId
     * @param pageId
     * @return
     */
    public static String getEmbedCodeForForm(int formId, int pageId) {
        return getEmbedCodeForForm(formId, pageId, "", false);
    }
    public static String getEmbedCodeForForm(int formId, int pageId, String additionalParas) {
        return getEmbedCodeForForm(formId, pageId, additionalParas, false);
    }

    /**
     *
     * @param userId
     * @return
     */
    public static int getSignupFormCount(String userId){
        try {
            if(Default.toDefault(userId).equals(""))
                return 0;
            return alphaQueryServer.getSignupFormCount(userId);

        } catch(Exception ex){
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     *
     * @param userId
     * @return
     */
    private static boolean isTrialLimitExceed(String userId){
        try{
            return getSignupFormCount(userId) >= TrialAccountManager.SIGNUP_FORM_LIMIT;
        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }

}