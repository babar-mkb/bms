package com.PMSystems;

import java.util.*;
import javax.naming.*;
import java.io.*;
import java.sql.Timestamp;

/**
 * This class contains utility methos that are used by different packages
 *
 * @version         1.0            06 Aug 2002
 * @author          Usman
 *
 * 06 Aug 2002      Usman          Initial Development of Class
 * 22 Aug 2002      Zeeshan        Added getCampaignStatus(), getAFUCampaignStatus()
 *                                 and getListsStatus() methods.
 * 02 Sep 2002      Imran          added getMaritalStatus() method.
 */

public final class PMSUtils {

    // Month Names
    public static String[] monthNames = {"January", "February", "March", "April",
                                         "May", "June", "July", "August",
                                         "September", "October", "November", "December"};
    // Month Names
    public static String[][] timeHours = {
        {"12 AM","0"},
        {"1 AM","1"},
        {"2 AM","2"},
        {"3 AM","3"},
        {"4 AM","4"},
        {"5 AM","5"},
        {"6 AM","6"},
        {"7 AM","7"},
        {"8 AM","8"},
        {"9 AM","9"},
        {"10 AM","10"},
        {"11 AM","11"},
        {"12 PM","12"},
        {"1 PM","13"},
        {"2 PM","14"},
        {"3 PM","15"},
        {"4 PM","16"},
        {"5 PM","17"},
        {"6 PM","18"},
        {"7 PM","19"},
        {"8 PM","20"},
        {"9 PM","21"},
        {"10 PM","22"},
        {"11 PM","23"}   };

   public static String[]    subscriberBasicFields = {
                "Email Address", "Email Type", "Gender",
                "First Name", "Middle Name", "Last Name", "Birth Day", "Address Line 1",
                "Address Line 2", "City", "State", "Zip", "Country", "Marital Status",
                "Occupation", "Job Status", "Household Income", "Education Level",
                "Telephone", "Company", "Industry","Source","Sales Rep","Sales Status","Area Code"};

   public static String[] timeMinutes = {"00", "05", "10", "15", "20", "25", "30",
       "35", "40","45","50","55"};

    // Context
    private static Context context;

    //Described Campaign Statuses
    private static String CAMPAIGN_STATUS_DRAFT = "Draft";
    private static String CAMPAIGN_STATUS_COMPLETED = "Completed";
    private static String CAMPAIGN_STATUS_SCHEDULED = "Scheduled";
    private static String CAMPAIGN_STATUS_PENDING = "Pending";
    private static String CAMPAIGN_STATUS_DELETED = "Deleted";

    private static String AFU_CAMPAIGN_STATUS_ACTIVE = "Active";
    private static String AFU_CAMPAIGN_STATUS_INACTIVE = "Inactive";

    private static String ARTICLE_STATUS_ACTIVE = "Active";
    private static String ARTICLE_STATUS_INACTIVE = "Inactive";

    private static String LIST_STATUS_ACTIVE = "Active";
    private static String LIST_STATUS_INACTIVE = "Inactive";

    private static String MARITAL_STATUS_SINGLE = "Single";
    private static String MARITAL_STATUS_MARRIED = "Married";
    private static String MARITAL_STATUS_WIDOW = "Widow";

    private static String GENDER_MAIL = "Male";
    private static String GENDER_FEMAIL = "Female";

    private static String EMAIL_TYPE_HTML = "Html Email";
    private static String EMAIL_TYPE_TEXT = "Text Email";

    private static String STATUS_UNKNOWN = "Unknown";

    /**
     * Return InitialContext of the JBoss Ejb container according to properties
     * defined in pms.properties file.
     *
     * @return Context
     * @throws Exception
     */
    public static Context getContext() throws Exception {
        if(context==null) {
            PMSResources pmsResources = PMSResources.getInstance();
            Hashtable contextValues = new Hashtable();
            contextValues.put(Context.PROVIDER_URL, pmsResources.getProviderURL());
            contextValues.put(Context.INITIAL_CONTEXT_FACTORY, pmsResources.getInitialContext());
            context = new InitialContext(contextValues);
        }
        return context;
    } // end getContext()

    /**
     * Compares two byte arrays byte by byte.
     *
     * @param arr1 as byte array
     * @param arr2 as byte array
     * @return boolean result
     */
    public static boolean compareByteArrays(byte[] arr1, byte[] arr2) {
        boolean equals = true;
        if(arr1.length == arr2.length) {
            for(int i=0; i<arr1.length; i++) {
                if(arr1[i]!=arr2[i]) {
                    equals = false;
                    break;
                }
            }
        }else {
            equals = false;
        }
        return equals;
    }

    /**
     * This method returns status of orginal campaign
     *
     * @param dbStatus as String
     * @return a String
     */
    public static String getCampaignStatus(String dbStatus) {
        if(dbStatus == null || dbStatus.trim().equals("") || dbStatus.trim().length() > 1) {
            return STATUS_UNKNOWN;
        }//end if

        if(dbStatus.equals(PMSDefinitions.CAMPAIGN_STATUS_DRAFT))
            return CAMPAIGN_STATUS_DRAFT;

        if(dbStatus.equals(PMSDefinitions.CAMPAIGN_STATUS_COMPLETED))
            return CAMPAIGN_STATUS_COMPLETED;

        if(dbStatus.equals(PMSDefinitions.CAMPAIGN_STATUS_DELETED))
            return CAMPAIGN_STATUS_DELETED;

        if(dbStatus.equals(PMSDefinitions.CAMPAIGN_STATUS_PENDING))
            return CAMPAIGN_STATUS_PENDING;

        if(dbStatus.equals(PMSDefinitions.CAMPAIGN_STATUS_SCHEDULED))
            return CAMPAIGN_STATUS_SCHEDULED;

        return STATUS_UNKNOWN;
    }//end getCampaignStatus()

    /**
     * This method returns status of follow up campaigns
     *
     * @param aStatus as String
     * @return status as String
     */
    public static String getAFUCampaignStatus(String aStatus) {
        if(aStatus == null || aStatus.trim().equals("") || aStatus.trim().length() > 1) {
            return STATUS_UNKNOWN;
        }//end if

        if(aStatus.equals(PMSDefinitions.AFU_CAMPAIGN_STATUS_ACTIVE))
            return AFU_CAMPAIGN_STATUS_ACTIVE;

        if(aStatus.equals(PMSDefinitions.AFU_CAMPAIGN_STATUS_INACTIVE))
            return AFU_CAMPAIGN_STATUS_INACTIVE;

        return STATUS_UNKNOWN;
    }//end getAFUCampaignStatus()

    /**
     * This method returns status of articles
     *
     * @param aStatus as String
     * @return status as String
     */
    public static String getArticleStatus(String aStatus) {
        if(aStatus == null || aStatus.trim().equals("") || aStatus.trim().length() > 1) {
            return STATUS_UNKNOWN;
        }//end if

        if(aStatus.equals(PMSDefinitions.ARTICLE_STATUS_ACTIVE))
            return ARTICLE_STATUS_ACTIVE;

        if(aStatus.equals(PMSDefinitions.ARTICLE_STATUS_INACTIVE))
            return ARTICLE_STATUS_INACTIVE;

        return STATUS_UNKNOWN;
    }//end getArticleStatus()



    /**
     * This method returns status current Lists
     *
     * @param aStatus as String
     * @return status as String
     */
    public static String getListsStatus(String aStatus) {
        if(aStatus == null || aStatus.trim().equals("") || aStatus.trim().length() > 1) {
            return STATUS_UNKNOWN;
        }//end if

        if(aStatus.equals(PMSDefinitions.LIST_STATUS_ACTIVE))
            return LIST_STATUS_ACTIVE;

        if(aStatus.equals(PMSDefinitions.LIST_STATUS_INACTIVE))
            return LIST_STATUS_INACTIVE;

        return STATUS_UNKNOWN;
    }//end getListsStatus()


    /**
     * returns the marital Status.
     *
     * @param mStatus as String
     * @return marital Status String.
     */
    public static String getMaritalStatus(String mStatus) {
        if(mStatus == null || mStatus.trim().equals("") || mStatus.trim().length() > 1) {
            return STATUS_UNKNOWN;
        }//end if

        if(mStatus.equals(PMSDefinitions.MARITAL_STATUS_MARRIED)) // "M"
            return MARITAL_STATUS_MARRIED;

        if(mStatus.equals(PMSDefinitions.MARITAL_STATUS_SINGLE)) // "S"
            return MARITAL_STATUS_SINGLE;

        if(mStatus.equals(PMSDefinitions.MARITAL_STATUS_WIDOW)) // "W"
            return MARITAL_STATUS_WIDOW;

        return STATUS_UNKNOWN;
    }

    public static String getGenderName(String genderCode) {
        if(genderCode == null || genderCode.trim().equals("") || genderCode.trim().length() > 1) {
            return STATUS_UNKNOWN;
        }//end if

        if(genderCode.equals(PMSDefinitions.GENDER_FEMAIL)) // "M"
            return GENDER_FEMAIL;

        if(genderCode.equals(PMSDefinitions.GENDER_MAIL)) // "S"
            return GENDER_MAIL;

        return STATUS_UNKNOWN;
    }

    public static String getGenderCode(String genderName) {
        if(genderName == null || genderName.trim().equals("")) {
            return "U";
        }//end if

        if(genderName.equals(GENDER_FEMAIL)) // "M"
            return PMSDefinitions.GENDER_FEMAIL;

        if(genderName.equals(GENDER_MAIL)) // "S"
            return PMSDefinitions.GENDER_MAIL;

        return "U";
    }

    public static String getEmailTypeDesc(String emailTypeCode) {
        if(emailTypeCode == null || emailTypeCode.trim().equals("") || emailTypeCode.trim().length() > 1) {
            return STATUS_UNKNOWN;
        }//end if

        if(emailTypeCode.equals(PMSDefinitions.EMAIL_TYPE_HTML)) // "M"
            return EMAIL_TYPE_HTML;

        if(emailTypeCode.equals(PMSDefinitions.EMAIL_TYPE_TEXT)) // "S"
            return EMAIL_TYPE_TEXT;

        return STATUS_UNKNOWN;
    }

    /**
     * Validates if a String value from database is null or "null" and returns empty string
     *
     * @param value as String
     * @return a String
     */
    public static String validateString(String value) {
        if(value == null || value.trim().equalsIgnoreCase("null")) {
            return PMSDefinitions.EMPTY_STRING;
        }else {
            return value.trim();
        }
    }

    /**
     * Validates an email address
     *
     * @param email as String
     * @return boolean
     */
    public static boolean validateEmail(String email) {
        String at = "@";
        String dot = ".";
        int lat = email.indexOf(at);
        int len = email.length();
        int ldot = email.indexOf(dot);

        //If '@' is at start or end or not present.
        if(email.indexOf(at) == -1 || email.indexOf(at) == 0 || email.indexOf(at) == (len-1)) {
            return false;
        }
        //If '.' is at start or end or not present.
        else if (email.indexOf(dot) == -1 || email.indexOf(dot) == 0 || email.indexOf(dot) == (len-1)) {
            return false;
        }
        //If '@' is only followed by a char
        else if(email.indexOf(at, (lat + 1)) != -1) {
            return false;
        }
        //If '@' is immediately followed by '.'
        else if (email.substring(lat-1, lat) == dot ) {
            return false;
        }
        //If '.' is immediately followed by @
        else if (email.indexOf(dot, (lat + 2)) == -1) {
            return false;
        }
        //if email is empty
        else if (email.indexOf(" ") != -1) {
            return false;
        }
        return true;
    }

    public static boolean deleteDir(String dirPath){
        File  dirPathObj = new File(dirPath);
        if(dirPathObj.isDirectory()){
            File[] fileNames = dirPathObj.listFiles();
            for (int i = 0; i < fileNames.length; i++) {
                fileNames[i].delete();
            }
            dirPathObj.delete();
            return true;
        }
        return false;
    }

    public static String removeChar(String str, char c){
        String strReply = "";
        char[] strCheck = str.toCharArray();
          for(int i=0;i<str.length();i++){
              if(strCheck[i]!=c){
                  // adding change character code..../ & \
                  if(strCheck[i] == '/' || strCheck[i] == '\\'){
                      strReply +='_';
                  }else{
                      strReply += strCheck[i];
                  }
              }
          }

         return strReply;
     }

     /**
      * populate years starting from 2000 to current year
      *
      * @return int array of years
      * Duplicate method in Snapshot Report....
      */
     public static ArrayList getYears(){
         ArrayList years = new ArrayList();
         int startYear = 2000;
         Calendar cal = Calendar.getInstance();
         while(startYear <= cal.get(cal.YEAR)){
             years.add(String.valueOf(startYear));
             startYear++;
         }
         return years;
     }

     /**
      * This method is used to check whether campaign is realTime or not?
      * Returns true if the passed date is less than 2 days old.
      *
      * @param scheduledDate
      * @return
      */
     public static boolean doRealtimeReport(Timestamp scheduledDate){
         int realtimeDayLimit = -2;
         Calendar today = Calendar.getInstance();
         today.set(Calendar.HOUR,0);
         today.set(Calendar.MINUTE,0);
         today.set(Calendar.SECOND,0);
         today.set(Calendar.MILLISECOND,0);
         today.add(Calendar.DATE, realtimeDayLimit);

         int thisDay = today.get(Calendar.DAY_OF_MONTH);
         int thisMon = today.get(Calendar.MONTH);
         int thisYear = today.get(Calendar.YEAR);
         String td = thisDay+"-"+thisMon+"-"+thisYear;

         Calendar scheduleDay = Calendar.getInstance();
         scheduleDay.setTime(scheduledDate);
         scheduleDay.set(Calendar.HOUR,0);
         scheduleDay.set(Calendar.MINUTE,0);
         scheduleDay.set(Calendar.SECOND,0);
         scheduleDay.set(Calendar.MILLISECOND,0);

         int thatDay = scheduleDay.get(Calendar.DAY_OF_MONTH);
         int thatMon = scheduleDay.get(Calendar.MONTH);
         int thatYear = scheduleDay.get(Calendar.YEAR);
         String sd = thatDay+"-"+thatMon+"-"+thatYear;

         if(today.equals( scheduleDay)){
             System.out.println("|=====| today:"+td+" scheduleday:"+sd+"   process realtime YES");
             return true;
         }else
         if(today.before(scheduleDay)){
             System.out.println("|>>>>>| today:"+td+" scheduleday:"+sd+"   process realtime YES");
             return true;
         }

         return false;
     }

     public static Vector getCountry(){
         Vector countryVec = new Vector();
         countryVec.add("United States");
         countryVec.add("Afghanistan");
         countryVec.add("Aland Islands");
         countryVec.add("Albania");
         countryVec.add("Algeria");
         countryVec.add("American Samoa");
         countryVec.add("Andorra");
         countryVec.add("Angola");
         countryVec.add("Anguilla");
         countryVec.add("Antarctica");
         countryVec.add("Antigua and Barbuda");
         countryVec.add("Argentina");
         countryVec.add("Armenia");
         countryVec.add("Aruba");
         countryVec.add("Australia");
         countryVec.add("Austria");
         countryVec.add("Azerbaijan");
         countryVec.add("Bahamas");
         countryVec.add("Bahrain");
         countryVec.add("Bangladesh");
         countryVec.add("Barbados");
         countryVec.add("Belarus");
         countryVec.add("Belgium");
         countryVec.add("Belize");
         countryVec.add("Benin");
         countryVec.add("Bermuda");
         countryVec.add("Bhutan");
         countryVec.add("Bolivia");
         countryVec.add("Bosnia and Herzegovina");
         countryVec.add("Botswana");
         countryVec.add("Bouvet Island");
         countryVec.add("Brazil");
         countryVec.add("British Indian Ocean Territory");
         countryVec.add("Brunei");
         countryVec.add("Bulgaria");
         countryVec.add("Burkina Faso");
         countryVec.add("Burundi");
         countryVec.add("Cambodia");
         countryVec.add("Cameroon");
         countryVec.add("Canada");
         countryVec.add("Cape Verde");
         countryVec.add("Caribbean");
         countryVec.add("Cayman Islands");
         countryVec.add("Central African Republic");
         countryVec.add("Chad");
         countryVec.add("Channel Islands");
         countryVec.add("Chile");
         countryVec.add("China");
         countryVec.add("Christmas Island");
         countryVec.add("Cocos (Keeling) Islands");
         countryVec.add("Colombia");
         countryVec.add("Comoros");
         countryVec.add("Congo");
         countryVec.add("Cook Islands");
         countryVec.add("Costa Rica");
         countryVec.add("Cote d'Ivoire");
         countryVec.add("Croatia");
         countryVec.add("Cuba");
         countryVec.add("Cyprus");
         countryVec.add("Czech Republic");
         countryVec.add("Denmark");
         countryVec.add("Djibouti");
         countryVec.add("Dominica");
         countryVec.add("Dominican Republic");
         countryVec.add("East Timor");
         countryVec.add("Ecuador");
         countryVec.add("Egypt");
         countryVec.add("El Salvador");
         countryVec.add("Equatorial Guinea");
         countryVec.add("Eritrea");
         countryVec.add("Estonia");
         countryVec.add("Ethiopia");
         countryVec.add("Falkland Islands");
         countryVec.add("Faroe Islands");
         countryVec.add("Fiji");
         countryVec.add("Finland");
         countryVec.add("France");
         countryVec.add("French Guiana");
         countryVec.add("French Polynesia");
         countryVec.add("French Southern Territories");
         countryVec.add("Gabon");
         countryVec.add("Gambia");
         countryVec.add("Georgia");
         countryVec.add("Germany");
         countryVec.add("Ghana");
         countryVec.add("Gibraltar");
         countryVec.add("Greece");
         countryVec.add("Greenland");
         countryVec.add("Grenada");
         countryVec.add("Guadeloupe");
         countryVec.add("Guam");
         countryVec.add("Guatemala");
         countryVec.add("Guernsey");
         countryVec.add("Guinea");
         countryVec.add("Guinea-Bissau");
         countryVec.add("Guyana");
         countryVec.add("Haiti");
         countryVec.add("Heard Island and McDonald Islands");
         countryVec.add("Holy See (Vatican City)");
         countryVec.add("Honduras");
         countryVec.add("Hong Kong");
         countryVec.add("Hungary");
         countryVec.add("Iceland");
         countryVec.add("India");
         countryVec.add("Indonesia");
         countryVec.add("Iran");
         countryVec.add("Iraq");
         countryVec.add("Isle of Man");
         countryVec.add("Israel");
         countryVec.add("Italy");
         countryVec.add("Jamaica");
         countryVec.add("Japan");
         countryVec.add("Jersey");
         countryVec.add("Jordan");
         countryVec.add("Kazakhstan");
         countryVec.add("Kenya");
         countryVec.add("Kiribati");
         countryVec.add("Korea, North");
         countryVec.add("Korea, South");
         countryVec.add("Kuwait");
         countryVec.add("Kyrgyzstan");
         countryVec.add("Laos");
         countryVec.add("Latvia");
         countryVec.add("Lebanon");
         countryVec.add("Lesotho");
         countryVec.add("Liberia");
         countryVec.add("Libya");
         countryVec.add("Liechtenstein");
         countryVec.add("Lithuania");
         countryVec.add("Luxembourg");
         countryVec.add("Macau");
         countryVec.add("Macedonia, F.Y.R.");
         countryVec.add("Madagascar");
         countryVec.add("Malawi");
         countryVec.add("Malaysia");
         countryVec.add("Maldives");
         countryVec.add("Mali");
         countryVec.add("Malta");
         countryVec.add("Marshall Islands");
         countryVec.add("Martinique");
         countryVec.add("Mauritania");
         countryVec.add("Mauritius");
         countryVec.add("Mayotte");
         countryVec.add("Mexico");
         countryVec.add("Micronesia");
         countryVec.add("Moldova");
         countryVec.add("Monaco");
         countryVec.add("Mongolia");
         countryVec.add("Montserrat");
         countryVec.add("Morocco");
         countryVec.add("Mozambique");
         countryVec.add("Myanmar");
         countryVec.add("Namibia");
         countryVec.add("Nauru");
         countryVec.add("Nepal");
         countryVec.add("Netherlands");
         countryVec.add("Netherlands Antilles");
         countryVec.add("New Caledonia");
         countryVec.add("New Zealand");
         countryVec.add("Nicaragua");
         countryVec.add("Niger");
         countryVec.add("Nigeria");
         countryVec.add("Niue");
         countryVec.add("Norfolk Island");
         countryVec.add("Northern Ireland");
         countryVec.add("Northern Mariana Islands");
         countryVec.add("Norway");
         countryVec.add("Oman");
         countryVec.add("Pakistan");
         countryVec.add("Palau");
         countryVec.add("Panama");
         countryVec.add("Papua New Guinea");
         countryVec.add("Paraguay");
         countryVec.add("Peru");
         countryVec.add("Philippines");
         countryVec.add("Pitcairn");
         countryVec.add("Poland");
         countryVec.add("Portugal");
         countryVec.add("Puerto Rico");
         countryVec.add("Qatar");
         countryVec.add("Republic of Ireland");
         countryVec.add("Reunion");
         countryVec.add("Romania");
         countryVec.add("Russia");
         countryVec.add("Rwanda");
         countryVec.add("Saint Helena");
         countryVec.add("Saint Kitts and Nevis");
         countryVec.add("Saint Lucia");
         countryVec.add("Saint Pierre and Miquelon");
         countryVec.add("Saint Vincent and the Grenadines");
         countryVec.add("Samoa");
         countryVec.add("San Marino");
         countryVec.add("Sao Tome and Principe");
         countryVec.add("Saudi Arabia");
         countryVec.add("Senegal");
         countryVec.add("Serbia and Montenegro");
         countryVec.add("Seychelles");
         countryVec.add("Sierra Leone");
         countryVec.add("Singapore");
         countryVec.add("Slovakia");
         countryVec.add("Slovenia");
         countryVec.add("Solomon Islands");
         countryVec.add("Somalia");
         countryVec.add("South Africa");
         countryVec.add("South Georgia and the South Sandwich Islands");
         countryVec.add("Spain");
         countryVec.add("Sri Lanka");
         countryVec.add("Sudan");
         countryVec.add("Suriname");
         countryVec.add("Svalbard and Jan Mayen Islands");
         countryVec.add("Swaziland");
         countryVec.add("Sweden");
         countryVec.add("Switzerland");
         countryVec.add("Syria");
         countryVec.add("Taiwan");
         countryVec.add("Tajikistan");
         countryVec.add("Tanzania");
         countryVec.add("Thailand");
         countryVec.add("Togo");
         countryVec.add("Tokelau");
         countryVec.add("Tonga");
         countryVec.add("Trinidad and Tobago");
         countryVec.add("Tunisia");
         countryVec.add("Turkey");
         countryVec.add("Turkmenistan");
         countryVec.add("Turks and Caicos Island");
         countryVec.add("Tuvalu");
         countryVec.add("Uganda");
         countryVec.add("Ukraine");
         countryVec.add("United Arab Emirates");
         countryVec.add("United Kingdom");
         countryVec.add("United States Minor Outlying Islands");
         countryVec.add("Uruguay");
         countryVec.add("Uzbekistan");
         countryVec.add("Vanuatu");
         countryVec.add("Vatican City State");
         countryVec.add("Venezuela");
         countryVec.add("Vietnam");
         countryVec.add("Virgin Islands, British");
         countryVec.add("Virgin Islands, U.S.");
         countryVec.add("Wallis and Futuna");
         countryVec.add("Western Sahara");
         countryVec.add("Yemen");
         countryVec.add("Yugoslavia");
         countryVec.add("Zaire");
         countryVec.add("Zambia");
         countryVec.add("Zimbabwe");

         return countryVec;
     }

     /**
      *
      * @return Current Time in PST
      */

     public static String getCurrentPSTTime(){
         TimeZone a = TimeZone.getTimeZone("PST");
         Calendar cal =Calendar.getInstance(a);
                     int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                     int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
         int currHour = cal.get(Calendar.HOUR_OF_DAY);
         int min = cal.get(Calendar.MINUTE);
         String minStr = "";
         if(min<10){
                 minStr = "0"+min;
         }
         else
             minStr = ""+min;
         String maredian = "AM";
         if(currHour==12){
             maredian = "PM";
         }else if(currHour>12){
             maredian = "PM";
             currHour = (currHour%12);
             System.out.println("mod >> "+currHour);
         }else if(currHour==0){
             currHour = 12;
         }
         return currHour+":"+minStr+" "+maredian;
     }

     /**
      *
      * @return Days name
      */

     public static Vector getDays(){
         Vector daysVec = new Vector();
         daysVec.add("Sunday");
         daysVec.add("Monday");
         daysVec.add("Tuesday");
         daysVec.add("Wednesday");
         daysVec.add("Thursday");
         daysVec.add("Friday");
         daysVec.add("Saturday");
         return daysVec;
     }


    public static void main(String[] args) {
        System.out.println("Gender Code: "+PMSUtils.getGenderCode(GENDER_FEMAIL));
        System.out.println("Valid: "+PMSUtils.validateEmail("naaman.boss@4hsinc.com"));
    }

}
