package com.PMSystems;

import com.PMSystems.sejbs.*;
import com.PMSystems.util.*;
import java.util.*;
import com.PMSystems.logger.*;
import com.PMSystems.dbbeans.*;
import java.io.*;
/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */


public class MessageManager {

    private static ReportsQueryServer reportQueryServer;
    private static AlphaQueryServer alphaServer;
    private MessageManager() {
    }
    static{
        reportQueryServer = EJBHomesFactory.getReportsQueryServerRemote();
        alphaServer = EJBHomesFactory.getAlphaQueryServerRemote();
    }



    /**
     *
     * @param msgBean
     * @return
     */
    public static boolean createSingleMessage(SingleMessageBean msgBean){
        if(msgBean == null || msgBean.getBody().equals(""))
            return false;
        try{
            //checking if Trial Campaign's limit
            UserInfo userInfo = UserManager.getUserInfo(msgBean.getUserId());
            if(userInfo == null || (userInfo.getPackageType().equals(PMSDefinitions.PACKAGE_TYPE_TRIAL) && isTrialLimitExceed(userInfo.getUserID())))
                return false;

            PKManager msgPK = new PKManager("SingleMessage",1);
            msgBean.setMsgId(msgPK.getPrimaryKey().longValue());
            System.out.println("New MSG ID ++ "+msgBean.getMsgId());

            return reportQueryServer.createSingleMessage(msgBean);
        } catch (Exception ex){
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }


    /**
     *
     * @param msgId
     * @param url
     * @return
     */
    private static long addSingleMessageURL(long msgId, String url){

        try {
            PKManager urlPK = new PKManager("SingleMessageURL",1);
            long urlId = urlPK.getPrimaryKey().longValue();

            if(reportQueryServer.createSingleMessageURL(urlId,msgId,url))
                return urlId;

        } catch(Exception ex){
            WebServerLogger.getLogger().log(ex);
            System.out.println("Exception in CampaignManager.createSingleMessageURL()"+ex.getMessage());
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     *
     * @param msgId
     * @param url
     * @return
     */
    public static String getValidSingleMessageURL(long urlId, long subNo){

        if(urlId == 0l || subNo ==0l)
            return "";
        try {
            return reportQueryServer.getValidSingleMessageURL(urlId,subNo);
        } catch(Exception ex){
            WebServerLogger.getLogger().log(ex);
            System.out.println("Exception in CampaignManager.getValidSingleMessageURL()"+ex.getMessage());
            ex.printStackTrace();
        }
        return "";
    }

    /**
     *
     * @param sample
     * @return
     */
    public static String procesSingleMessageLinks(long msgId, long subNo, String text){

        String sample = text;
        char h, r, e, f;
        char SPACE=' ';
        char EQUALITY='=';
        char DOUBLE_QUOTE = '"';
        char SINGLE_QUOTE = '\'';
        String link="";
        StringBuffer bag = new StringBuffer();
        int len = sample.length();
        int sampleLength=sample.length();

        try{
            int i=0;
            for(;i+3<len; i++) {
                h = sample.charAt(i);
                r = sample.charAt(i+1);
                e = sample.charAt(i+2);
                f = sample.charAt(i+3);

                if( (h=='h'||h=='H') && (r=='r'||r=='R') && (e=='e'||e=='E') && (f=='f'||f=='F')) {
                    bag.append(""+h+r+e+f);
                    int j = i+4;
                    if(!(j< sampleLength)) break;
                    for(; j<sampleLength && sample.charAt(j)==SPACE; j++)
                        bag.append(""+SPACE);
                    if(!(j< sampleLength)) break;
                    if(sample.charAt(j)==EQUALITY){
                        bag.append(""+EQUALITY);
                        j++;
                        for(; j<sampleLength && sample.charAt(j)==SPACE; j++)
                            bag.append(""+SPACE);
                        if(!(j<sampleLength)) break;
                        char c = sample.charAt(j++);
                        boolean quoted = false;
                        if( c==DOUBLE_QUOTE || c==SINGLE_QUOTE){
                            bag.append(""+c);
                            quoted=true;
                        }else{
                            link = "";
                            link=link+c;
                        }
                        if(quoted){
                            link = "";
                            for(;sample.charAt(j)!=DOUBLE_QUOTE && sample.charAt(j)!=SINGLE_QUOTE;j++){
                                link=link+sample.charAt(j);
                            }
                        }//quoted
                        else{
                            link = "";
                            if(!(j< sampleLength)) break;
                            for(; j<sampleLength && sample.charAt(j)!=SPACE && sample.charAt(j)!='>';j++){
                                link=link+sample.charAt(j);
                            }
                        }//not quoted
                    /* --------------  handle link -------------- **/
                        System.out.println("[CampaignManager.processSingleMessageLinks()]: LINK: "+link);

                        if(link.endsWith(".css")) {
                            bag.append(link);
                        } else if(link.startsWith("#") || link.startsWith("mailto:") ){
                                /*if link is a Bookmark or mailto*/
                                bag.append(link);
                        } else{
                            long urlId = addSingleMessageURL(msgId,link);
                            if(urlId == 0l)
                                bag.append(link);
                            else                             /*need to put encoded url*/
                                bag.append(""+getSingleMessageTrackingClickURL(urlId,subNo));
                        }
                        //}
                        bag.append(""+sample.charAt(j)); //inserting closing quote incase of quoted
                        i=j;
                    } else{//if equallity
                        bag.append(""+sample.charAt(j));
                        i=j;
                    }
                } else{// if href
                    bag.append( ""+h);
                    //System.out.println(" >: "+ h+r+e+f);
                }
            }//for

            /*if few charactors are left in the end*/
            for(; i<sample.length();i++)
                bag.append(""+sample.charAt(i));

        } catch(Exception ex){
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return bag.toString();
    }


    private static String processSingleMessageTextLinks(long msgId, long subNo, String text) {

        String sample = text;

        char h, t1, t2, p, col, s1, s2,s3;
        char SPACE=' ';
        char CARRIAGE_RETURN='\r';
        char END_LINE = '\n';

        String link="";
        StringBuffer bag = new StringBuffer();
        int len = sample.length();
        int sampleLength = sample.length();
        try{
            int i=0;
            for(;i+8<len; i++){
                h = sample.charAt(i);
                t1 = sample.charAt(i+1);
                t2 = sample.charAt(i+2);
                p = sample.charAt(i+3);
                col = sample.charAt(i+4);
                s1 = sample.charAt(i+5);
                s2 = sample.charAt(i+6);
                s3= sample.charAt(i+7);

                if( ((h=='h'||h=='H') && (t1=='t'||t1=='T') && (t2=='t'||t2=='T') && (p=='p'||p=='P')&& (col==':')) ||
                    ((h=='h'||h=='H') && (t1=='t'||t1=='T') && (t2=='t'||t2=='T') && (p=='p'||p=='P')&& (col=='s' || col=='S') && (s1==':'))){

                    int j = i;

                    if((h=='h'||h=='H') && (t1=='t'||t1=='T') && (t2=='t'||t2=='T') && (p=='p'||p=='P')&& (col==':')){
                        j = j+7;
                        link = link + h + t1 + t2 + p + col + s1 + s2;
                    } else{
                        j = j+8;
                        link = link + h + t1 + t2 + p + col + s1 + s2 + s3;
                    }


                    for(; j<sampleLength && sample.charAt(j)!=SPACE && sample.charAt(j)!=CARRIAGE_RETURN  && sample.charAt(j)!=END_LINE ; j++)
                        link = link+sample.charAt(j);

                    /* --------handle link------- */
                    System.out.println("[CampaignManager]: processSingleMessageTextLinks(): LINK BEFORE INVALID HANDLING: "+link);

                    System.out.println("[CampaignManager]: processSingleMessageTextLinks(): LINK : "+link);

                    PMSResources resources = PMSResources.getInstance();
                    if( link.startsWith("#") || link.startsWith("mailto:") || link.endsWith(".css") ||
                        link.startsWith("http://"+resources.getEventsDomain())) {
                        /*do not replace if link is a Bookmark or mailto*/
                        bag.append(link);
                    } else{
                        /* --------handle Invalid last character of link------- */
                        String newLink = Default.removeInvalidLinkEndChars(link);
                        j = j - (link.length()-newLink.length());
                        link = newLink;

                        /*replacing*/
                        long urlId = addSingleMessageURL(msgId,link);
                        if(urlId == 0l)
                            bag.append(link);
                        else                             /*need to put encoded url*/
                            bag.append(""+getSingleMessageTrackingClickURL(urlId,subNo));
                    }//else
                    /* --------------- */
                    link="";
                    if(j<sampleLength)
                        bag.append(""+sample.charAt(j));
                    i=j;
                }else{// if href
                    bag.append( ""+h);
                }
            }//for
            /*if some characters are left in the end*/
            for(; i<sample.length();i++)
                bag.append(""+sample.charAt(i));
        } catch(Exception ex) {
            WebServerLogger.getLogger().log(ex);
            System.out.println("[CampaignManager] processSingleMessageTextLinks(): "+ex.getMessage());
            ex.printStackTrace();
        }
        return bag.toString();

    }//processPlainTextLinks


    /**
     *
     * @param msgId
     * @param subNo
     * @return
     */
    public static boolean addSingleMessageOpenEvent(long msgId,long subNo){
        if(msgId == 0l || subNo == 0l)
            return false;
        try{
            return reportQueryServer.addSingleMessageEvent(msgId,subNo,"o");
        } catch (Exception ex){
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param urlId
     * @return
     */
    public static boolean addSingleMessageClickEvent(long urlId){
        if(urlId==0l)
            return false;
        try{
            return reportQueryServer.addSingleMessageClickEvent(urlId);
        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }

    /**
     *
     * @param urlId
     * @return
     */
    public static boolean updateSingleMessageStatus(long msgId,String status){
        if(msgId==0l)
            return false;
        try{
            return reportQueryServer.updateSingleMessageStatus(msgId,status);
        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }


    /**
     *
     * @param urlId
     * @return
     */
    private static String getSingleMessageTrackingClickURL(long urlId, long subNo){
        try{
            PMSResources resources = PMSResources.getInstance();
            return getMessageServletPath()+"?urlId="+PMSEncoding.encode(""+urlId)+"&subId="+PMSEncoding.encode(""+subNo)+"&type=c";
        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return "";
    }

    /**
     *
     * @param urlId
     * @return
     */
    public static String getSingleMessageTrackingOpenURL(long msgId, long subNo){
        try{
            PMSResources resources = PMSResources.getInstance();

            return "<img src=\""+getMessageServletPath()+"?msgId="+
                PMSEncoding.encode(""+msgId) + "&subId=" + PMSEncoding.encode(""+subNo)+"&type=o \" border=\"0\" width=\"1\" height=\"1\">";
        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return "";
    }




    /**
     *
     * @param urlId
     * @return
     */
    public static String getSingleMessageFooter(long msgId, long subNo){

        try{



            String supressLink = getMessageServletPath()+"?msgId="+PMSEncoding.encode(""+msgId)+"&subId="+PMSEncoding.encode(""+subNo)+"&type=s";

            StringBuffer buff = new StringBuffer("");
            buff.append("\r\n<center>");
            buff.append("\r\n<table><tr><td>&nbsp;</td></tr></table>");
            buff.append("\r\n<table border=\"0\" cellpadding=\"2\" cellspacing=\"2\" width=\"100%\" style=\"color:black;font-weight:normal;font-family:verdana;font-size:11px;background-color:#EEE;\">\r\n");
            buff.append("\r\n<tr><td width=70%>");
            buff.append("\r\n<table border=\"0\" cellpadding=\"2\" cellspacing=\"2\" width=\"100%\" style=\"font-family:verdana;font-size:11px;\">");
            buff.append("\r\n\r\n<tr><td align=center><span style=\"font-size:11px;\"><b>Subscription Center:</b></span>");

            //Show Suppress Link
            buff.append("\r\n&nbsp;&nbsp;<a href=\""+supressLink+"\">Suppress Me</a>");

            return buff.toString();
        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return "";
    }


    /**
     *
     * @param urlId
     * @return
     */
    public static boolean addSingleMessageSupressEvent(long msgId,long subNo){
        if(msgId==0l || subNo==0l)
            return false;
        try{
            return reportQueryServer.addSingleMessageEvent(msgId,subNo,"s");
        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }

    /**
     *
     * @param urlId
     * @return
     */
    public static long getURLMessageId(long urlId){
        if(urlId <= 0)
            return 0;
        try{
            return reportQueryServer.getURLMessageId(urlId);
        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return 0;
    }


    /**
     *
     * @param subNo
     * @param startDate
     * @param endDate
     * @param limit
     * @param batch
     * @return
     */
    public static Vector getSubscriberSingleMessageHistory(long subNo,Date startDate,Date endDate,int limit, int batch){
        Vector msgVec = new Vector();
        if (subNo < 1)
            return msgVec;
        try{
            return reportQueryServer.getSubscriberMessageEmails(subNo,startDate,endDate,limit,batch);
        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return msgVec;
    }


    /**
    *
    * @param subNo
    * @param startDate
    * @param endDate
    * @return
    */
   public static int getSubscriberSingleMessageCount(long subNo,Date startDate,Date endDate){
       Vector msgVec = new Vector();
       if (subNo < 1)
           return 0;
       try{
           return reportQueryServer.getSubscriberMessageEmailCount(subNo,startDate,endDate);
       } catch(Exception ex){
           ex.printStackTrace();
           WebServerLogger.getLogger().log(ex);
       }
       return 0;
   }
    
    /**
     *
     * @param msgId
     * @param subNo
     * @return
     */
    public static SingleMessageBean getSingleMessage(long msgId,long subNo){
        if(msgId <= 0 || subNo <= 0)
            return null;
        try{
            return reportQueryServer.getSingleMessage(msgId,subNo);
        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return null;
    }

    /**
     *
     * @param userId
     * @param startDate
     * @param endDate
     * @param limit
     * @param batch
     * @return
     */
    public static Vector getUserSingleMessageHistory(String userId,Date startDate,Date endDate,int limit, int batch){
      if(userId==null || userId.equals(""))
          return null;
      try{
          return reportQueryServer.getUserMessageEmails(userId,startDate,endDate,limit,batch);
      } catch(Exception ex){
          ex.printStackTrace();
          WebServerLogger.getLogger().log(ex);
      }
      return null;
    }

    /**
     *
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getUserSingleMessageCount(String userId, Date startDate, Date endDate){
        if(userId==null || userId.equals(""))
            return 0;
        try{
            return reportQueryServer.getUserMessageEmailCount(userId,startDate,endDate);
        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return 0;
    }


    private static String getMessageServletPath(){
        try{
            PMSResources resources = PMSResources.getInstance();
            return "http://"+resources.getEventsDomain()+"/pms/smsg";
        } catch(Exception ex){
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
     public static int getSingleMessageCount(String userId){
         if(Default.toDefault(userId).equals(""))
             return 0;
         try{
             return alphaServer.getSingleMessageCount(userId);
         } catch(Exception ex){
             ex.printStackTrace();
             WebServerLogger.getLogger().log(ex);
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
            return getSingleMessageCount(userId) >= TrialAccountManager.SINGLE_MSG_LIMIT;
        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }

    /**
     * 
     * @param userId
     * @param searchText
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getSingleMessageCountBySearch(String userId, String searchText, long startDate, long endDate){
    	if(Default.toDefault(userId).equals("") || searchText.equals(""))
    		return 0;
    	try{
    		return reportQueryServer.getSingleMessageCountBySearch(userId,searchText,startDate,endDate);
    	} catch(Exception ex){
    		ex.printStackTrace();
    		WebServerLogger.getLogger().log(ex);
    	}
    	return 0;
    }

    
    /**
     * 
     * @param userId
     * @param searchText
     * @param startDate
     * @param endDate
     * @param limit
     * @param batch
     * @return
     */
    public static Vector searchSingleMessage(String userId, String searchText, long startDate, long endDate,int offset, int bucket){
    	if(Default.toDefault(userId).equals("") || searchText.equals(""))
    		return new Vector();
    	try{
    		return reportQueryServer.searchSingleMessage(userId,searchText,startDate,endDate,offset,bucket);
    	} catch(Exception ex){
    		ex.printStackTrace();
    		WebServerLogger.getLogger().log(ex);
    	}
    	return new Vector();
    }
    
    
    /**
     * 
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getSingleMessageOpenCount(String userId, long startDate, long endDate){
    	if(Default.toDefault(userId).equals(""))
    		return 0;
    	try{
    		return reportQueryServer.getUserMessageEmailCountByEvent(userId,"O",startDate,endDate);
    	} catch(Exception ex){
    		ex.printStackTrace();
    		WebServerLogger.getLogger().log(ex);
    	}
    	return 0;
    }

    /**
     * 
     * @param userId
     * @param startDate
     * @param endDate
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector getOpenedSingleMessage(String userId, long startDate, long endDate,int offset,int bucket){
    	if(Default.toDefault(userId).equals(""))
    		return new Vector();
    	try{
    		return reportQueryServer.getUserMessageEmailsByEvent(userId, "O", startDate, endDate, offset, bucket);
    	} catch(Exception ex){
    		ex.printStackTrace();
    		WebServerLogger.getLogger().log(ex);
    	}
    	return new Vector();
    }
    
    /**
     * 
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getSingleMessageSupressCount(String userId, long startDate, long endDate){
    	if(Default.toDefault(userId).equals(""))
    		return 0;
    	try{
    		return reportQueryServer.getUserMessageEmailCountByEvent(userId,"S",startDate,endDate);
    	} catch(Exception ex){
    		ex.printStackTrace();
    		WebServerLogger.getLogger().log(ex);
    	}
    	return 0;
    }
    
}


