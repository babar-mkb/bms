package com.PMSystems;

import java.util.*;
import java.io.*;

import com.PMSystems.*;
import com.PMSystems.sejbs.*;
import com.PMSystems.beans.*;
import com.PMSystems.dbbeans.*;
import com.PMSystems.util.*;
import com.PMSystems.template.*;
import com.PMSystems.logger.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: </p>
 * @author Ahmad Suhaib
 * @version 1.0
 */

public class WorkflowManager {

    private static DataQueryServer dataServer;
    private static ComplexQueryServer complexQueryServer;
    private static ComplexQueryServer2 complexQueryServer2;
    private static BMSQueryServer bmsServer;
    public static Vector pmsRuleVec = new Vector();

    static {
        bmsServer = EJBHomesFactory.getBMSQueryServerRemote();
        dataServer = EJBHomesFactory.getDataQueryServerRemote();
        complexQueryServer = EJBHomesFactory.getComplexQueryServerRemote();
        complexQueryServer2 = EJBHomesFactory.getComplexQueryServer2Remote();

        pmsRuleVec.add(new NameValue("=", "equal to"));
        pmsRuleVec.add(new NameValue("!=", "not equal to"));
        pmsRuleVec.add(new NameValue("ct", "contains"));
        pmsRuleVec.add(new NameValue("!ct", "does not contain"));
        pmsRuleVec.add(new NameValue("empty", "is empty"));
        pmsRuleVec.add(new NameValue("notempty", "is not empty"));
        pmsRuleVec.add(new NameValue("dr", "within date range"));
        pmsRuleVec.add(new NameValue("nr", "within numeric range"));
        pmsRuleVec.add(new NameValue("prior", "day(s) prior"));
        pmsRuleVec.add(new NameValue("after", "day(s) after"));
        pmsRuleVec.add(new NameValue("dayof", "day of"));
        pmsRuleVec.add(new NameValue("birthday", "birthday"));
        pmsRuleVec.add(new NameValue("pbday", "day(s) prior birthday"));
    }

    public WorkflowManager() {
    }

    /**
     *
     * @param workflowId
     * @param newName
     * @return
     */
    public static boolean updateWorkflowData(int workflowId, String newName, String desc, boolean isManualAddition) {

        try {
            return bmsServer.updateWorkflowData(workflowId, newName, desc, isManualAddition);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     *
     * @param workBean
     * @return
     */
    public static boolean updateWorkflowBeanOnly(WorkflowBean workBean) {

        try {
            return bmsServer.updateWorkflowBeanOnly(workBean);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * @param userId
     * @param name
     * @return
     */
    public static boolean isWorkflowExists(String userId, String name, int workflowId) {

        try {
            return bmsServer.isWorkflowExists(userId, name, workflowId);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     * @param workflowBean
     * @return
     */
    public static WorkflowBean createWorkflow(WorkflowBean workflowBean) {

        try {
            //checking if Trial Workflow's limit
            UserInfo userInfo = UserManager.getUserInfo(workflowBean.getUserID());
            if(userInfo == null || (userInfo.getPackageType().equals(PMSDefinitions.PACKAGE_TYPE_TRIAL) && isTrialLimitExceed(userInfo.getUserID())))
                return null;

            return bmsServer.createWorkflow(workflowBean);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return null;
    }
    /**
     *
     * @param workflowId
     * @param segmentIdVec
     * @return
     */
    public static boolean saveWorkflowSegments(int workflowId, Vector segmentIdVec) {

        try {
            return dataServer.saveWorkflowSegments(workflowId, segmentIdVec);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     * @param workflowId
     * @param stepVec
     * @return
     */
    public static Vector createWorkflowSteps(int workflowId, Vector stepVec) {

        try {
            return bmsServer.createWorkflowSteps(workflowId, stepVec);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }
    /**
     * @param workflowId
     * @param stepBean
     * @return
     */
    public static boolean createWorkflowStep(int workflowId, WorkflowStepBean stepBean) {

        try {
            return bmsServer.createWorkflowStep(workflowId, stepBean);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param workflowId
     * @param index
     * @return
     */
    public static boolean insertWorkflowStepAt(int workflowId, WorkflowStepBean stepBean) {

        if(stepBean.getTriggerOrder()<=0)
            return false;

        try {
            if(bmsServer.makeRoomForWorkflowStepAt(workflowId, stepBean.getTriggerOrder())) {
                Vector vec = createWorkflowSteps(workflowId, Default.toVector(stepBean));
                if(vec.size()>0) {
                    WorkflowStepBean newStepBean = (WorkflowStepBean)vec.get(0);
                    stepBean.setStepID(newStepBean.getStepID());
                    return true;
                }
                return false;
            }

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * @param workflowId
     * @param stepBean
     * @return
     */
    public static boolean updateWorkflowStep(int workflowId, WorkflowStepBean stepBean) {

        try {
            WorkflowStepBean oldStepBean = getWorkflowStepById(workflowId, stepBean.getStepID());
            if(oldStepBean==null)
                return false;

            boolean isUpdated = bmsServer.updateWorkflowStep(workflowId, stepBean);
            if(!isUpdated)
                return false;

            //=== FIND Options which no longer exists.
            Vector oldOptionsVec = oldStepBean.getOptionsVec();

            for(int i=0; i<oldOptionsVec.size(); i++) {
                WorkflowOptionBean optionBean = (WorkflowOptionBean)oldOptionsVec.get(i);
                if(stepBean.getStepOption(optionBean.getOptionNumber())!=null) {
                    oldOptionsVec.remove(i);
                    --i;
                }
            }
            //-- Return if no options to delete --
            if(oldOptionsVec.isEmpty())
                return isUpdated;

            Vector optionNumVec = new Vector();
            for(int i=0; i<oldOptionsVec.size(); i++)
                optionNumVec.add(""+((WorkflowOptionBean)oldOptionsVec.get(i)).getOptionNumber());

            if(oldOptionsVec.size()>0)
                System.out.println("[WorkflowManager]: UPDATE_STEP workflowId:"+workflowId+", stepId:"+stepBean.getStepID()+", DELETE Options No More In Use: ("+Default.toCSV(optionNumVec)+")");

            //=== DELETE Options which no longer exists in the updated StepBean.
            bmsServer.deleteWorkflowOptions(stepBean.getStepID(), oldOptionsVec, true);

            //=== Re-Order OptionNumbers after an option has been deleted.
            updateWorkflowOptionOrderAfterDelete(workflowId, stepBean.getStepID());

            //=== Update Workflow Segements Collection.
            saveWorkflowSegments(workflowId, fetchSegmentsUsed(WorkflowManager.getWorkflowById(workflowId)));

            return isUpdated;

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     * Correct options order in step after some of the options in the step has been deleted.
     *
     * example of orders after deletion of options can be: {0,2,3,5}, {3}, {0,5}, {0}
     *
     * @param workflowId
     * @param stepId
     * @return
     */
    private static boolean updateWorkflowOptionOrderAfterDelete(int workflowId, int stepId) {

        System.out.println("[WorkflowManager]  CORRECT_OPTIONs_ORDER_AFTER_DEL  workflowId:"+workflowId+", stepId:"+stepId);

        try {
            WorkflowBean workflowBean = getWorkflowById(workflowId);
            WorkflowStepBean stepBean = workflowBean!=null? workflowBean.getWorkflowStepByID(stepId): null;
            if(workflowBean==null || stepBean==null || stepBean.getOptionsVec().isEmpty())
                return false;

            //=== Check if only one option exists & its order is correct, then returns from here ====
            if(stepBean.getOptionsVec().size()==1 && (stepBean.isDefaultOptionExists() || stepBean.getStepOption(1)!=null))
                return true; //already in order.

            //=== Collect all option's Numbers & order them in ascending order ===
            Vector numVec = new Vector();
            for(int i=0; i<stepBean.getOptionsVec().size(); i++) {
                WorkflowOptionBean optionBean = (WorkflowOptionBean)stepBean.getOptionsVec().get(i);
                numVec.add(""+optionBean.getOptionNumber());
            }

            Collections.sort(numVec);

            //==== Now change the option's order to 'correct' optionNumber ====
            int optionNumber=1;
            Vector newMapVec = new Vector();

            for(int i=0; i<numVec.size(); i++) {

                int oldNum = Default.defaultInt((String)numVec.get(i));
                if(oldNum<=0)
                    continue;

                WorkflowOptionBean optionBean = stepBean.getStepOption(oldNum);
                optionBean.setOptionNumber(optionNumber);
                if(optionNumber!=oldNum)
                    newMapVec.add(new NameValue(""+oldNum, ""+optionNumber));

                ++optionNumber;
            }

            //==== Collect newly ordered option Nums ====
            Vector newNumVec = new Vector();
            for(int i=0; i<stepBean.getOptionsVec().size(); i++) {
                WorkflowOptionBean optionBean = (WorkflowOptionBean)stepBean.getOptionsVec().get(i);
                newNumVec.add(""+optionBean.getOptionNumber());
            }
            Collections.sort(newNumVec);

            System.out.println("[WorkflowManager] CORRECT_OPTIONs_ORDER_AFTER_DEL  workflowId:"+workflowId+", stepId:"+stepId
                               +", old.order:"+Default.toCSV(numVec)+", new.order:"+Default.toCSV(newNumVec));

            //==== Save the changed order =====
            bmsServer.updateOptionNumberInTransmission(workflowId, stepBean.getStepID(), newMapVec);

            //=== Reload the StepBean ===
            stepBean = getWorkflowStepById(workflowId, stepId);
            Vector campNumVec = stepBean.getAllCampaignNumbers();
            if(campNumVec.isEmpty()) {
                System.out.println("[WorkflowManager]: CORRECT_OPTIONs_ORDER_AFTER_DEL  NO_CAMP_FOUND workflowId:"+workflowId+", stepId:"+stepBean.getStepID());
                return true;
            }

            //=== assign JUNK campaign names to avoid 'Duplicate CampaignName' errors sub-sequently. ===
            for(int i=0; i<campNumVec.size(); i++) {
                long campNum = Default.defaultLong((String)campNumVec.get(i));
                String newName = workflowBean.getName().trim()+"_"+PMSEncoding.encode(""+campNum);

                CampaignManager.updateCampaignName(campNum, newName);
                CampaignManager.updateCompletedCampaignName(campNum, newName);
                System.out.println("[WorkflowManager]: CORRECT_OPTIONs_ORDER_AFTER_DEL  workflowId:"+workflowId+", stepId:"+stepBean.getStepID()
                                   +", campNum:"+campNum+", junkName:"+newName);
            }

            //=== assign CORRECT campaign names ===
            for(int j=0; j<stepBean.getOptionsVec().size(); j++) {
                WorkflowOptionBean optionBean = (WorkflowOptionBean)stepBean.getOptionsVec().get(j);
                if(optionBean.isDoNothing() || !optionBean.isActionEmailExist())
                    continue;

                WorkflowActionBean actionBean = optionBean.getOptionAction(PMSDefinitions.WORKFLOW_ACTION_EMAIL);
                if(actionBean==null || actionBean.getCampaignNumber()<=0)
                    continue;

                String newName = buildWorkflowCampaignName(workflowBean.getName(), stepBean.getTriggerOrder(), optionBean.getOptionNumber());
                long campNum = actionBean.getCampaignNumber();

                //=== update campaign names ===
                CampaignManager.updateCampaignName(campNum, newName);
                CampaignManager.updateCompletedCampaignName(campNum, newName);

                System.out.println("[WorkflowManager]: CORRECT_OPTIONs_ORDER_AFTER_DEL  workflowId:"+workflowId+", stepId:"+stepBean.getStepID()
                                   +", optionNum:"+optionBean.getOptionNumber()+", campNum:"+campNum+", newName:"+newName);
            }

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * @param userId
     * @return
     */
    public static Vector getWorkflowsByUserId(String userId) {

        try {
            return bmsServer.getWorkflowsByUserId(userId);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }
    /**
     *
     * @param workflowId
     * @param stepId
     * @return
     */
    public static WorkflowStepBean getWorkflowStepById(int workflowId, int stepId) {

        try {
            return bmsServer.getWorkflowStepById(workflowId, stepId);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param workflowId
     * @return
     */
    public static WorkflowBean getWorkflowById(int workflowId) {

        try {
            return bmsServer.getWorkflowById(workflowId);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return null;
    }
    /**
     *
     * @param workflowIDVec
     * @return
     */
    public static Vector getWorkflowByIDVec(Vector workflowIDVec) {

        try {
            return bmsServer.getWorkflowByIDVec(workflowIDVec);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }
    /**
     *
     * @param workflowIDVec
     * @param subNum
     * @return
     */
    public static Hashtable getWorkflowSummaryBySubNum(Vector workflowIDVec, long subNum) {

        try {
            return bmsServer.getWorkflowSummaryBySubNum(workflowIDVec, subNum);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Hashtable();
    }

    /**
     * @param workflowId
     * @return
     */
    public static boolean deleteWorkflow(int workflowId) {

        try {
            if(bmsServer.deleteWorkflow(workflowId)) {
                saveWorkflowSegments(workflowId, new Vector());
                return true;
            }

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     *
     * @param stepId
     * @return
     */
    public static boolean deleteWorkflowStep(int workflowId, int stepId) {

        try {
            WorkflowBean workflowBean = bmsServer.getWorkflowById(workflowId);
            if(workflowBean==null || workflowBean.isRunning())
                return false;

            WorkflowStepBean stepBean = workflowBean.getWorkflowStepByID(stepId);
            if(stepBean==null)
                return false;

            if(bmsServer.deleteWorkflowStep(workflowId, stepId)) {

                //If Step deleted successfully, update triggerOrder for remaining steps.
                bmsServer.updateWorkflowOrderAfterStepDelete(workflowId, stepBean.getTriggerOrder());
                updateWorkflowStepNames(workflowId);

                //Update Workflow Segements Collection.
                saveWorkflowSegments(workflowId, fetchSegmentsUsed(WorkflowManager.getWorkflowById(workflowId)));

                System.out.println("[WorkflowManager] WORKFLOW_ORDER_SHIFTED  userId:"+workflowBean.getUserID()+", workflowId:"+workflowId+", del.StepId:"
                                   +stepId+", del.TriggerOrder:"+stepBean.getTriggerOrder());
                return true;
            }

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     *
     * @param workflowBean
     * @return
     */
    public static Vector fetchSegmentsUsed(WorkflowBean workflowBean) {

        Vector segmentIdVec = new Vector();
        if(workflowBean.getStepsVec().isEmpty())
            return segmentIdVec;

        for(int triggerOrder=1; triggerOrder<=workflowBean.getStepsVec().size(); triggerOrder++) {

            WorkflowStepBean stepBean = workflowBean.getWorkflowStepByOrder(triggerOrder);
            if(stepBean.isWait() || stepBean.getOptionsVec().isEmpty())
                continue;

            for(int optionNumber=0; optionNumber<=stepBean.getOptionsVec().size(); optionNumber++) {
                WorkflowOptionBean optionBean = stepBean.getStepOption(optionNumber);
                if(optionBean==null)
                    continue;

                if(optionBean.getSegBehaviorFilterNumber()>0) {
                    WorkflowSegmentFilterBean segFilterBean = FilterManager.getWorkflowSegmentFilterBean(optionBean.getSegBehaviorFilterNumber());
                    if(segFilterBean!=null && segFilterBean.isFilterEnabled(true)) {
                        //System.out.println("[WorkflowHandler.jsp]: MEM_SEG: "+Default.toCSV(segFilterBean.getSegmentVec(true)));
                        segmentIdVec.addAll(segFilterBean.getSegmentVec(true));

                    }
                    if(segFilterBean!=null && segFilterBean.isFilterEnabled(false)) {
                        //System.out.println("[WorkflowHandler.jsp]: NON_MEM_SEG: "+Default.toCSV(segFilterBean.getSegmentVec(false)));
                        segmentIdVec.addAll(segFilterBean.getSegmentVec(false));
                    }
                }
            }
        }
        return segmentIdVec;
    }

    /**
     *
     * @param userID
     * @param workflowId
     * @return
     */
    public static boolean playWorkflow(String userID, int workflowId) {

        if(workflowId<=0)
            return false;

        try {
            WorkflowBean workflowBean = getWorkflowById(workflowId);
            if(workflowBean==null || !workflowBean.getUserID().equals(userID) || !workflowBean.isEditable())
                return false;

            return bmsServer.markWorkflowActive(workflowId);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param userID
     * @param workflowId
     * @return
     */
    public static boolean pauseWorkflow(String userID, int workflowId) {

        if(workflowId<=0)
            return false;

        try {
            WorkflowBean workflowBean = getWorkflowById(workflowId);
            if(workflowBean==null || !workflowBean.getUserID().equals(userID) || !workflowBean.isRunning())
                return false;

            return bmsServer.markWorkflowInactive(workflowId);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     *
     * @param workflowId
     * @param stepId
     * @param optionNumber
     * @return
     */
    public static String buildWorkflowCampaignName(String workflowName, int triggerOrder, int optionNumber) {

        //return workflowName.trim()+" - Message for Workflow Step "+triggerOrder+((optionNumber==0)? " Default Option":" Option "+optionNumber);
	return workflowName.trim()+"_"+triggerOrder+"."+optionNumber+"."+WebSecurityManager.getCSRFToken();
    }
    /**
     * @param campBean
     * @param userInfo
     * @return
     */
    public static String createWebVersion(CampaignDataBean campBean, UserInfo userInfo) {

        String url = "";
        if(campBean==null || campBean.getCampaignNumber()<=0)
            return url;

        try {
            // Creating Web Version of Campaign
            String fileName = PMSEncoding.encode(""+campBean.getCampaignNumber())+".html";
            if(!campBean.getWebVersionFileName().equals("")) {
                fileName = campBean.getWebVersionFileName();
                fileName = PMSUtils.removeChar(fileName,' ');
            }
            String view = campBean.getAdvanceEditorOrignalHtmlText();

            String webVersioDIR = PMSResources.getInstance().getWebVersionPath();
            String dirFileName = webVersioDIR + fileName;

            File file = new File(dirFileName);
            file.delete();
            file.createNewFile();
            DataOutputStream dataOut = new DataOutputStream(new FileOutputStream(file));
            dataOut.writeBytes(view);

            if(campBean.getWebVersionFileName().equals("")) {
                campBean.setWebVersionFileName(fileName);
                //Update CampaignDetail as WebVersionFileName is stored as custom campaign field.
                CampaignManager.createUpdateCampainDetails(campBean.getCampaignNumber(), campBean.getCampaignDetail());
            }

            System.out.println("[WorkflowManager]: WEB_VERSION_SAVED campNum:"+campBean.getCampaignNumber()+", userId:"+userInfo.getUserID()+", fileName:"+dirFileName);
            return fileName;

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return "";
    }
    /**
     *
     * @param workflowId
     * @param stepId
     * @param optionNumber
     * @param alertAction
     * @return
     */
    public static boolean updateWorkflowAlertAction(int workflowId, int stepId, int optionNumber, WorkflowActionBean alertAction) {

        if(workflowId<=0 || stepId<=0 || optionNumber<0)//optionNumber==0 is Default Option, and it can have a alert action against it.
            return false;

        try {
            WorkflowStepBean stepBean = WorkflowManager.getWorkflowStepById(workflowId, stepId);
            if(stepBean==null)
                return false;

            WorkflowOptionBean optionBean = stepBean.getStepOption(optionNumber);
            boolean isOptionExists = (optionBean!=null);
            optionBean = (optionBean==null)? new WorkflowOptionBean(optionNumber) : optionBean;

            if(!optionBean.isActionAlertToSalesRepExist())
                alertAction.setType(PMSDefinitions.WORKFLOW_ACTION_ALERT_SALESREP);

            //======= If option exists, add/update SalesRep Alert Action. =====
            if(isOptionExists) {
                return bmsServer.createUpdateWorkflowAction(stepBean.getStepID(), optionNumber, alertAction, PMSDefinitions.WORKFLOW_ACTION_ALERT_SALESREP);

            } else {
                optionBean.setActionVec(Default.toVector(alertAction));

                //====== Create option with default values & add email action. =======
                if(bmsServer.createNewWorkflowOption(stepBean.getStepID(), Default.toVector(optionBean))) {
                    System.out.println("[WorkflowManager]: updateWorkflowAlertAction() OPTION.created ALERT.ACTION.created  stepId:"+stepBean.getStepID()
                                       +", optionNumber:"+optionNumber+", Alert.Emails:"+alertAction.getAdditionAlertEmails()+", isAlertSalesRepToo:"+alertAction.isAlertToSalesRep());
                    return true;
                }
            }

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     *
     * @param stepId
     * @param optionNumber
     * @param alertAction
     * @return
     */
    public static boolean deleteWorkflowAction(int stepId, int optionNumber, WorkflowActionBean actionBean) {

        if(stepId<=0 || optionNumber<0 || actionBean==null)
            return false;

        try {
            //===== Delete Campaign, If Email Action ======
            if(actionBean.isSendEmail() && actionBean.getCampaignNumber()>0) {
                CampaignManager.deleteCampaign(actionBean.getCampaignNumber());
            }
            return bmsServer.deleteWorkflowAction(stepId, optionNumber, actionBean.getType());

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     *
     * @param stepId
     * @param optionNumber
     * @param actionBean
     * @param actionType
     * @return
     */
    public static boolean createUpdateWorkflowAction(int stepId, int optionNumber, WorkflowActionBean actionBean, String actionType) {

        if(stepId<=0 || actionBean==null)
            return false;

        try {
            return bmsServer.createUpdateWorkflowAction(stepId, optionNumber, actionBean, actionType);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     *
     * @param stepId
     * @param newOptionsVec
     * @return
     */
    public static boolean createNewWorkflowOption(int stepId, Vector newOptionsVec) {

        if(stepId<=0 || newOptionsVec.isEmpty())
            return false;

        try {
            return bmsServer.createNewWorkflowOption(stepId, newOptionsVec);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     *
     * @param stepId
     * @param optionBean
     * @return
     */
    public static WorkflowOptionBean createUpdateWorkflowOption(int stepId, WorkflowOptionBean optionBean) {

        if(stepId<=0 || optionBean==null)
            return null;

        try {
            return bmsServer.createUpdateWorkflowOption(stepId, optionBean);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return null;
    }
    /**
     *
     * @param stepId
     * @param optionBean
     * @return
     */
    public static boolean createUpdateWorkflowOptionOnlyData(int stepId, WorkflowOptionBean optionBean) {

        if(stepId<=0 || optionBean==null)
            return false;

        try {
            return bmsServer.createUpdateWorkflowOptionOnlyData(stepId, optionBean);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     *
     * @param stepId
     * @param optionNumber
     * @param ruleVec
     * @return
     */
    public static boolean createUpdateWorkflowOptionRules(int stepId, int optionNumber, Vector ruleVec) {

        if(stepId<=0 || optionNumber<=0)//ruleVec.isEmpty() check is removed, so that older rules gets deleted.
            return false;

        try {
            return bmsServer.createUpdateWorkflowOptionRules(stepId, optionNumber, ruleVec);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param stepBean
     * @param optionNumber
     * @param campNum
     * @return
     */
    public static boolean updateWorkflowEmailAction(WorkflowStepBean stepBean, int optionNumber, long campNum) {

        if(stepBean==null || optionNumber<0 || campNum<=0)//optionNumber==0 is Default Option, and it can have a campaign action against it.
            return false;

        try {
            WorkflowOptionBean optionBean = stepBean.getStepOption(optionNumber);
            boolean isOptionExists = (optionBean!=null);
            optionBean = (optionBean==null)? new WorkflowOptionBean(optionNumber): optionBean;

            WorkflowActionBean emailAction = optionBean.isActionEmailExist()? optionBean.getOptionAction(PMSDefinitions.WORKFLOW_ACTION_EMAIL)
                : new WorkflowActionBean();
            if(!optionBean.isActionEmailExist()) {
                emailAction.setType(PMSDefinitions.WORKFLOW_ACTION_EMAIL);
            }
            emailAction.setCampaignNumber(campNum);

            //======= If option exists, add/update email action. =====
            if(isOptionExists) {
                return bmsServer.createUpdateWorkflowAction(stepBean.getStepID(), optionNumber, emailAction, PMSDefinitions.WORKFLOW_ACTION_EMAIL);

            } else {
                optionBean.setActionVec(Default.toVector(emailAction));

                //====== Create option with default values & add email action. =======
                if(bmsServer.createNewWorkflowOption(stepBean.getStepID(), Default.toVector(optionBean))) {
                    System.out.println("[WorkflowManager]: updateWorkflowEmailAction() OPTION.created EMAIL.ACTION.created  "
                                       +" stepId:"+stepBean.getStepID()+", optionNumber:"+optionNumber+", campNum:"+campNum);
                    return true;
                }
            }

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * @param stepId
     * @param optionNumber
     * @param behaviorFilterNumber
     * @return
     */
    public static boolean updateWorkflowOptionBehavior(int workflowId, int stepId, int optionNumber, int behaviorFilterNumber) {

        try {
            WorkflowStepBean sBean = getWorkflowStepById(workflowId, stepId);
            if(sBean==null) {
                System.out.println("[WorkflowManager]: updateWorkflowOptionBehavior() Step Not Found. workflowId:"+workflowId+", stepId:"+stepId);
                return false;
            }

            WorkflowOptionBean oBean = sBean.getStepOption(optionNumber);
            if(oBean==null) {//Option not exists.
                System.out.println("[WorkflowManager]: updateWorkflowOptionBehavior() OPTION.NOT.FOUND workflowId:"+workflowId+", stepId:"+stepId+", optionNumber:"+optionNumber);
                oBean = new WorkflowOptionBean(optionNumber);
                oBean.setBehaviorFilterNumber(behaviorFilterNumber);

                //Create option with default values.
                if(bmsServer.createNewWorkflowOption(stepId, Default.toVector(oBean))) {
                    System.out.println("[WorkflowManager]: updateWorkflowOptionBehavior() OPTION.CREATED workflowId:"+workflowId+", stepId:"+stepId+", optionNumber:"+optionNumber);
                    return true;
                }

            } else {//Option exists.
                if(bmsServer.updateWorkflowOptionBehavior(stepId, optionNumber, behaviorFilterNumber)) {
                    System.out.println("[WorkflowManager]: updateWorkflowOptionBehavior() OPTION.UPDATED workflowId:"+workflowId+", stepId:"+stepId+", optionNumber:"+optionNumber);
                    return true;
                }
            }

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     *
     * @param workflowBean
     * @return
     */
    public static boolean updateWorkflowStepNames(int workflowId) {

        WorkflowBean workflowBean = getWorkflowById(workflowId);
        if(workflowBean==null)
            return false;

        try {
            Vector campNumVec = workflowBean.getAllCampaignNumbers();
            if(campNumVec.isEmpty())
                return true;

            //=== assign junk campaign names to avoid 'Duplicate CampaignName' errors sub-sequently. ===
            for(int i=0; i<campNumVec.size(); i++) {
                long campNum = Default.defaultLong((String)campNumVec.get(i));
                String newName = workflowBean.getName().trim()+"_"+PMSEncoding.encode(""+campNum);

                CampaignManager.updateCampaignName(campNum, newName);
                CampaignManager.updateCompletedCampaignName(campNum, newName);
            }

            /**
             * ======================== Update Campaign Names =================================
             */
            for(int i=0; i<workflowBean.getStepsVec().size(); i++) {

                WorkflowStepBean stepBean = (WorkflowStepBean)workflowBean.getStepsVec().get(i);
                if(stepBean.isWait())
                    continue;

                for(int j=0; j<stepBean.getOptionsVec().size(); j++) {
                    WorkflowOptionBean optionBean = (WorkflowOptionBean)stepBean.getOptionsVec().get(j);
                    if(optionBean.isDoNothing() || !optionBean.isActionEmailExist())
                        continue;

                    WorkflowActionBean actionBean = optionBean.getOptionAction(PMSDefinitions.WORKFLOW_ACTION_EMAIL);
                    if(actionBean==null || actionBean.getCampaignNumber()<=0)
                        continue;

                    String newName = buildWorkflowCampaignName(workflowBean.getName(), stepBean.getTriggerOrder(), optionBean.getOptionNumber());
                    long campNum = actionBean.getCampaignNumber();

                    //=== update campaign names ===
                    CampaignManager.updateCampaignName(campNum, newName);
                    CampaignManager.updateCompletedCampaignName(campNum, newName);
                }
            }
            return true;

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Search & process subscribers for Workflows that are using Behavior Filter matching the activityType provided.
     *
     * @param subActivityHistoryVec - Vector of SubscriberActivityHistoryBean
     * @param activityType - 'WV', 'CK' etc.

    public static void processForBehavioralWorkflows(Vector subAHVec, String activityType) {

        if(subAHVec.isEmpty() || activityType.equals(""))
            return;

        String behaviorFilterType = "";
        //==== Convert Subscriber activityType into BehaviourFilter type. ====
        if(activityType.equalsIgnoreCase(SubscriberManager.ACTIVITY_ARTICLE_CLICK))
            behaviorFilterType = PMSDefinitions.BEHAVIOUR_FILTER_EMAIL;

        else if(activityType.equalsIgnoreCase(SubscriberManager.ACTIVITY_CAMPAIGN_OPEN))
            behaviorFilterType = PMSDefinitions.BEHAVIOUR_FILTER_EMAIL;

        else if(activityType.equalsIgnoreCase(SubscriberManager.ACTIVITY_SCORE_CHANGE))
            behaviorFilterType = PMSDefinitions.BEHAVIOUR_FILTER_SCORE;

        else if(activityType.equalsIgnoreCase(SubscriberManager.ACTIVITY_SIGN_UP))
            behaviorFilterType = PMSDefinitions.BEHAVIOUR_FILTER_FORM_SUBMISSION;

        else if(activityType.equalsIgnoreCase(SubscriberManager.ACTIVITY_WEB_VISIT))
            behaviorFilterType = PMSDefinitions.BEHAVIOUR_FILTER_WEB_VISIT;

        else {
            return;//Others SubscriberActivity types either not implemented as BehaviorFilters OR unknown activityType found.
        }

        Hashtable userSubNumHash = new Hashtable();
        try {
            //===== Group all activities by UserId. =====
            for(int i=0; i<subAHVec.size(); i++) {
                SubscriberActivityHistoryBean bean = (SubscriberActivityHistoryBean)subAHVec.get(i);

                if(userSubNumHash.containsKey(bean.getUserId())) {
                    ((Vector)userSubNumHash.get(bean.getUserId())).add(""+bean.getSubscriberNumber());
                } else {
                    Vector vec = new Vector();
                    vec.add(""+bean.getSubscriberNumber());
                    userSubNumHash.put(bean.getUserId(), vec);
                }
            }

            //===== Process subscribers every user. =====
            Enumeration userEnum = userSubNumHash.keys();
            while(userEnum.hasMoreElements()) {

                String userId = (String)userEnum.nextElement();
                if(userId==null || userId.trim().equals(""))
                    continue;

                Vector subNumVec = (Vector)userSubNumHash.get(userId);

                //--- send group to process
                if(!behaviorFilterType.equals("") && subNumVec.size()>0)
                    bmsServer.processForBehavioralWorkflows(userId, subNumVec, behaviorFilterType);
            }

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
    }*/
    /**
     *
     * @param workflowIdVec
     * @param isSAM
     * @param startDate
     * @param endDate
     * @return
     */
    public static Hashtable getWorkflowSummariesByIdsAndDateRange(Vector workflowIdVec, boolean isSAM, long startDate, long endDate) {

        try {
            Hashtable hash = bmsServer.getWorkflowSummariesByIds(workflowIdVec, isSAM, startDate, endDate);
            return (hash==null)? new Hashtable(): hash;

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Hashtable();
    }
    /**
     *
     * @param userId
     * @param isSAM
     * @param startDate
     * @param endDate
     * @return
     */
    public static Hashtable getWorkflowSummariesByUserIdAndDateRange(String userId, boolean isSAM, long startDate, long endDate) {

        try {
            Hashtable hash = bmsServer.getWorkflowSummariesByUserId(userId, isSAM, startDate, endDate);
            return (hash==null)? new Hashtable(): hash;

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Hashtable();
    }
    /**
     *
     * @param workflowIdVec
     * @return
     */
    public static Hashtable getWorkflowSummariesByIds(Vector workflowIdVec, boolean isSAM) {

        try {
            Hashtable hash = bmsServer.getWorkflowSummariesByIds(workflowIdVec, isSAM, 0, 0);
            return (hash==null)? new Hashtable(): hash;

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Hashtable();
    }
    /**
     *
     * @param userId
     * @return
     */
    public static Hashtable getWorkflowSummariesByUserId(String userId, boolean isSAM) {

        try {
            Hashtable hash = bmsServer.getWorkflowSummariesByUserId(userId, isSAM, 0, 0);
            return (hash==null)? new Hashtable(): hash;

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Hashtable();
    }
    /**
     *
     * @param workflowId
     * @param stepId
     * @param optionNumber
     * @param actionType
     * @param viewType
     * @return
     */
    public static int countWorkflowSubsByViewType(int workflowId, int stepId, int optionNumber, String actionType, String viewType, boolean isSAM) {

        try {
            return bmsServer.countWorkflowSubsByViewType(workflowId, stepId, optionNumber, actionType, viewType, isSAM, 0, 0);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     *
     * @param workflowId
     * @param stepId
     * @param optionNumber
     * @param actionType
     * @param viewType
     * @param isSAM
     * @param startDate
     * @param endDate
     * @return
     */
    public static int countWorkflowSubsByViewTypeAndDateRange(int workflowId, int stepId, int optionNumber, String actionType, String viewType, boolean isSAM, long startDate, long endDate) {

        try {
            return bmsServer.countWorkflowSubsByViewType(workflowId, stepId, optionNumber, actionType, viewType, isSAM, startDate, endDate);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     *
     * @param samUserBean
     * @param workflowId
     * @param stepId
     * @param optionNumber
     * @param actionType
     * @param viewType
     * @return
     */
    public static int countWorkflowSubsByViewTypeForSAM(DashboardSAMBean samUserBean, int workflowId, int stepId, int optionNumber
        , String actionType, String viewType) {

        try {
            return bmsServer.countWorkflowSubsByViewTypeForSAM(samUserBean, workflowId, stepId, optionNumber, actionType, viewType,0,0);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return 0;
    }
    public static int countWorkflowSubsByViewTypeForSAMByDateRange(DashboardSAMBean samUserBean, int workflowId, int stepId, int optionNumber
        , String actionType, String viewType, long startDate, long endDate) {

        try {
            return bmsServer.countWorkflowSubsByViewTypeForSAM(samUserBean, workflowId, stepId, optionNumber, actionType, viewType,startDate,endDate);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     *
     * @param workflowId
     * @param stepId
     * @param optionNumber
     * @param actionType
     * @param viewType
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector viewWorkflowSubsByViewType(int workflowId, int stepId, int optionNumber, String actionType, String viewType
        , int offset, int bucket, boolean isSAM) {

        try {
            return bmsServer.viewWorkflowSubsByViewType(workflowId, stepId, optionNumber, actionType, viewType, offset, bucket, isSAM,0,0);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }
    //==============================================================================
    public static Vector viewWorkflowSubsByViewTypeAndDateRange(int workflowId, int stepId, int optionNumber, String actionType, String viewType
        , int offset, int bucket, boolean isSAM, long startDate, long endDate) {

        try {
            return bmsServer.viewWorkflowSubsByViewType(workflowId, stepId, optionNumber, actionType, viewType, offset, bucket, isSAM,startDate,endDate);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }

    /**
     *
     * @param samUserBean
     * @param workflowId
     * @param stepId
     * @param optionNumber
     * @param actionType
     * @param viewType
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector viewWorkflowSubsByViewTypeForSAM(DashboardSAMBean samUserBean, int workflowId, int stepId, int optionNumber, String actionType, String viewType
        , int offset, int bucket) {

        try {
            return bmsServer.viewWorkflowSubsByViewTypeForSAM(samUserBean, workflowId, stepId, optionNumber, actionType, viewType, offset, bucket,0,0);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }
    public static Vector viewWorkflowSubsByViewTypeForSAMByDateRange(DashboardSAMBean samUserBean, int workflowId, int stepId, int optionNumber, String actionType, String viewType
        , int offset, int bucket, long startDate, long endDate) {

        try {
            return bmsServer.viewWorkflowSubsByViewTypeForSAM(samUserBean, workflowId, stepId, optionNumber, actionType, viewType, offset, bucket, startDate, endDate);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }

    /**
     *
     * @param userId
     * @return
     */
    public static Vector getWorkflowCampaignsNV(String userId) {

        try {
            return bmsServer.getWorkflowCampaignsNV(userId);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return new Vector();
    }
    /**
     * Return SubNum vector of subscribers, which have atleast one Workflow yet to be dismissed by SalesRep.
     *
     * @param salesRepNumVec
     * @param subNumVec
     * @return
     */
    public static Vector getSubsWithSalesRepAlertOn(Vector salesRepNumVec, Vector subNumVec) {

        try {
            return bmsServer.getSubsWithSalesRepAlertOn(salesRepNumVec, subNumVec);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return new Vector();
    }

    /**
     * Returns WorkflowId, StepId, OptionNumber in a Vector for each 'visible' Workflow Alert against the subscriber.
     *
     * @param salesRepNumVec
     * @param subNum
     * @return
     */
    public static Vector getWFAlertsForSalesBySubNum(long subNum) {

        try {
            return bmsServer.getWFAlertsForSalesBySubNum(subNum);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return new Vector();
    }
    /**
     *
     * @param salesRepNumVec
     * @param subNum
     * @param workflowId
     * @param stepId
     * @return
     */
    public static boolean markWorkflowAlertAsSeen(long subNum, int workflowId, int stepId) {

        try {
            return bmsServer.markWorkflowAlertAsSeen(subNum, workflowId, stepId);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return false;
    }
    /**
     *
     * @param samUserBean
     * @return
     */
    public static int countSubsWithWorkflowAlertOn(DashboardSAMBean samUserBean) {

        try {
            return bmsServer.countSubsWithWorkflowAlertOn(samUserBean);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return 0;
    }
    /**
     *
     * @param samUserBean
     * @return
     */
    public static int countSubsPartOfAnyWorkflow(DashboardSAMBean samUserBean) {

        try {
            return bmsServer.countSubsPartOfAnyWorkflow(samUserBean);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return 0;
    }
    /**
     *
     * @param samUserBean
     * @return
     */
    public static int countSubsForCourseCorrect(DashboardSAMBean samUserBean) {

        try {
            return bmsServer.countSubsForCourseCorrect(samUserBean);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return 0;
    }

    /**
     * @param samUserBean
     * @param offset
     * @param bucket
     * @return
     */
    public static Hashtable getSubsWorkflowAlertTimes(Vector subNumVec, boolean isAlertOn) {

        try {
            return bmsServer.getSubsWorkflowAlertTimes(subNumVec, isAlertOn);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return new Hashtable();
    }
    /**
     *
     * @param str
     * @return
     */
    public static String getWorkflowRuleName(String str) {
        int index = pmsRuleVec.indexOf(new NameValue(str, ""));
        if(index!=-1) {
            return ((NameValue)pmsRuleVec.get(index)).getValue();
        }
        return "n/a";
    }

    /**
     *
     * @param keyword
     * @return
     */
    public static String getWorkflowFieldLabel(String keyword) {

        Vector pmsFieldVec = new Vector(TriggerManager.pmsFieldVec);

        int index = pmsFieldVec.indexOf(new NameValue(keyword, ""));
        if(index!=-1) {
            return "[basic] "+((NameValue)pmsFieldVec.get(index)).getValue();
        }

        //assuming keyword is a CustomField.
        String customPadder = "{{9*9_";
        if(keyword==null)
            return "";

        if(keyword.startsWith(customPadder))
            keyword = keyword.substring(customPadder.length(), keyword.length());

        if(keyword.startsWith("{{"))
            keyword = keyword.substring(2, keyword.length());

        if(keyword.endsWith("}}"))
            keyword = keyword.substring(0, keyword.indexOf("}}"));

        return "[custom] "+keyword;
    }

    /**
     *
     * @param listNum
     * @return
     */
    public static String getWorkflowRuleListName(long listNum) {

        if(listNum<=0)
            return "[not found]";

        try {
            String name = Default.toDefault(ListManager.getListName(listNum));
            return name.equals("")? "[not found]": Default.secureInput(name);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return "[not found]";
    }
    /**
     *
     * @param campNum
     * @param subNum
     * @return
     */
    public static Vector getOpenClickWebVisitCount(long campNum, long subNum) {

        if(campNum<=0 || subNum<=0)
            return new Vector();

        try {
            return bmsServer.getOpenClickWebVisitCount(campNum, subNum);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return new Vector();
    }
    /**
     *
     * @param workflowId
     * @param subNumVec
     * @param userIdUpdated
     * @param isPaused
     */
    public static void manuallyRemoveFromWorkflow(int workflowId, Vector subNumVec, String userIdUpdated, boolean isPaused) {

        try {
            bmsServer.manuallyRemoveFromWorkflow(workflowId, subNumVec, userIdUpdated, isPaused);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
    }
    /**
     *
     * @param workflowId
     * @param subNumVec
     * @return
     */
    public static boolean undoManualRemovalFromWorkflow(int workflowId, Vector subNumVec) {

        try {
            return bmsServer.undoManualRemovalFromWorkflow(workflowId, subNumVec);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return false;
    }
    /**
     *
     * @param workflowId
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector getManuallyRemovedFromWorkflow(int workflowId, String type, int offset, int bucket) {

        try {
            return bmsServer.getManuallyRemovedFromWorkflow(workflowId, type, offset, bucket);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return new Vector();
    }
    /**
     *
     * @param subNumVec
     * @return
     */
    public static Hashtable isWorkflowSubs(Vector subNumVec) {

        try {
            return bmsServer.isWorkflowSubs(subNumVec);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return new Hashtable();
    }
    /**
     *
     * @param subNum
     * @return
     */
    public static Vector getWorkflowBeansBySubNum(long subNum) {

        try {
            Vector idVec = bmsServer.getWorkflowIDsBySubNum(subNum);
            Vector beanVec = bmsServer.getWorkflowByIDVec(idVec);

            Vector orderedVec = new Vector();
            for(int i=0; i<idVec.size(); i++)
                for(int j=0; j<beanVec.size(); j++)
                    if(Default.defaultLong((String)idVec.get(i))==((WorkflowBean)beanVec.get(j)).getWorkflowID())
                        orderedVec.add(beanVec.get(j));

            return orderedVec;

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return new Vector();
    }

    /**
     * at index[0]: pause count, index[1]: delete count.
     *
     * @param workflowId
     * @return
     */
    public static Vector countManuallyRemovedFromWorkflow(int workflowId) {

        try {
            return bmsServer.countManuallyRemovedFromWorkflow(workflowId);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return new Vector();
    }
    /**
     *
     * @param workflowId
     * @param subNumVec
     * @return
     */
    public static Hashtable isManuallyRemovedFromWorkflow(int workflowId, Vector subNumVec) {

        try {
            return bmsServer.isManuallyRemovedFromWorkflow(workflowId, subNumVec);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return new Hashtable();
    }
    /**
     *
     * @param subNum
     * @param workflowId
     * @param stepId
     * @param optionNumber
     * @param actionType
     * @return
     */
    public static boolean skipSubsForWorkflowStep(long subNum, int workflowId, int stepId) {

        try {
            return bmsServer.skipSubsForWorkflowStep(subNum, workflowId, stepId);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return false;
    }

    /**
     *
     * @param subNum
     * @param workflowId
     * @param stepId
     * @return
     */
    public static boolean unskipSubsForWorkflowStep(long subNum, int workflowId, int stepId) {

        try {
            return bmsServer.unskipSubsForWorkflowStep(subNum, workflowId, stepId);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return false;
    }

    /**
     *
     * @param subNum
     * @param workflowId
     * @return
     */
    public static Hashtable getSkippedWorkflowStepsForSubs(long subNum, int workflowId) {

        try {
            return bmsServer.getSkippedWorkflowStepsForSubs(subNum, workflowId);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return new Hashtable();
    }
    /**
     *
     * @param workflowId
     * @param stepId
     * @param subNumVec
     * @return
     */
    public static Hashtable getSkippedSubsForWorkflowStep(int workflowId, int stepId, String actionType, Vector subNumVec) {

        try {
            return bmsServer.getSkippedSubsForWorkflowStep(workflowId, stepId, actionType, subNumVec);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return new Hashtable();
    }
    /**
     *
     * @param samUserBean
     * @return
     */
    public static Hashtable getWorkflowSummariesForSAMUser(DashboardSAMBean samUserBean) {

        try {
            Hashtable hash = bmsServer.getWorkflowSummariesForSAMUser(samUserBean);
            return (hash==null)? new Hashtable(): hash;

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Hashtable();
    }
    /**
     *
     * @param samUserBean
     * @param workflowIdVec
     * @return
     */
    public static Hashtable getWorkflowSummariesForSAMUser(DashboardSAMBean samUserBean, Vector workflowIdVec, long startDate, long endDate) {

        try {
            Hashtable hash = bmsServer.getWorkflowSummariesForSAMUser(samUserBean, workflowIdVec, startDate, endDate);
            return (hash==null)? new Hashtable(): hash;

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Hashtable();
    }

    /**
     * Parses the text passed as a parameter, break the text where the keyword is found & assign this part to a keyword and
     * return the NameValue Vector.
     *
     * @param page
     * @return
     */
    public static Vector getAlertCommentsKeywordedParts(String page, Vector keywords) {

        Vector indexes = new Vector();

        //=== Collecting indexes for the keywords ===
        for(int k=0; keywords!=null && indexes!=null && k<keywords.size(); k++) {
            String keyword = (String)keywords.get(k);
            int fromIndex = 0;
            while(page.indexOf(keyword, fromIndex)!=-1) {
                int tmpIndex = page.indexOf(keyword, fromIndex);
                fromIndex = tmpIndex+1;
                indexes.add(new NameValue(""+tmpIndex, keyword));
            }
        }
        //=== Sorts the indexes in ascending order. ===
        if(indexes!=null && indexes.size()>0) {
            Collections.sort(indexes);
        }

        String html = page;
        Vector pagePartsVec = new Vector();
        int lastIndex = 0;

        //==== Using collected indexes to break the page in small parts assigned to a keyword. ====
        for(int i=0; indexes!=null && i<indexes.size(); i++) {
            NameValue nv = (NameValue)indexes.get(i);
            int index = Default.defaultInt(nv.getName());
            String keyword = nv.getValue();

            String subStr = html.substring(lastIndex, index);
            lastIndex = index+keyword.length();
            pagePartsVec.add(new NameValue(subStr, keyword));
        }

        /**
         * Adding the remainig part at the end of page.
         * Assigning it to a junk keyword, so that it get attached to completed page.
         */
        if(lastIndex<html.length())
            pagePartsVec.add(new NameValue(html.substring(lastIndex, html.length()), ""+(Integer.MIN_VALUE)));

        return pagePartsVec;
    }
    /**
     *
     * @param subHash
     * @param commentPartsVec
     * @return
     */
    public static String joinKeywordedCommentsUsingSubsData(Hashtable subHash, Vector commentPartsVec) {

        StringBuffer page = new StringBuffer("");
        for(int i=0; commentPartsVec!=null && i<commentPartsVec.size(); i++) {
            NameValue nv = (NameValue)commentPartsVec.get(i);
            String subStr = nv.getName();
            String keyword = Default.toDefault(nv.getValue());
            page.append(subStr);
            page.append(Default.toDefault((String)subHash.get(keyword)));
        }
        return page.toString();
    }

    /**
     *
     * @param subInfo
     * @return
     */
    public static Hashtable getSubsHashForSalesRepAlertComments(SubscriberInfo subInfo) {

        Hashtable subHash = new Hashtable();
        subHash.put(TemplateUtils.SUB_NUMBER_KEY, PMSEncoding.encode(String.valueOf(subInfo.getSubscriberNumber())));
        subHash.put(TemplateUtils.SUB_SALESFORCE_ID_KEY, subInfo.getConLeadId());

        subHash.put(TemplateUtils.SUB_EMAIL_KEY, Default.toDefault(subInfo.getEmail()));
        subHash.put(TemplateUtils.SUB_FIRSTNAME_KEY, Default.toDefault(subInfo.getFirstName()));
        subHash.put(TemplateUtils.SUB_LASTNAME_KEY, Default.toDefault(subInfo.getLastName()));
        subHash.put(TemplateUtils.SUB_ADDR_KEY, Default.toDefault(subInfo.getAddressLine1()));
        subHash.put(TemplateUtils.SUB_ADDRESS2_KEY, Default.toDefault(subInfo.getAddressLine2()));
        subHash.put(TemplateUtils.SUB_CITY_KEY, Default.toDefault(subInfo.getCity()));
        subHash.put(TemplateUtils.SUB_STATE_KEY, Default.toDefault(subInfo.getStateCode()));
        subHash.put(TemplateUtils.SUB_ZIP_KEY, Default.toDefault(subInfo.getZip()));
        subHash.put(TemplateUtils.SUB_COUNTRY_KEY, Default.toDefault(subInfo.getCountryCode()));
        subHash.put(TemplateUtils.SUB_PHONE_KEY, Default.toDefault(subInfo.getTelephone()));
        subHash.put(TemplateUtils.SUB_GENDER_KEY, Default.toDefault(subInfo.getGender()));

        if(subInfo.getBirthDate()!=null)
            subHash.put(TemplateUtils.SUB_BIRTHDATE_KEY, Default.formatDate(subInfo.getBirthDate().getTime(), "yyyy-MM-dd"));

        subHash.put(TemplateUtils.SUB_SOURCE_KEY, Default.toDefault(subInfo.getSource()));
        subHash.put(TemplateUtils.SUB_SALESREP_KEY, Default.toDefault(subInfo.getSalesRep()));
        subHash.put(TemplateUtils.SUB_SALESSTATUS_KEY, Default.toDefault(subInfo.getSalesStatus()));

        subHash.put(TemplateUtils.SUB_MARITAL_STATUS_KEY, Default.toDefault(subInfo.getMaritalStatus()));
        subHash.put(TemplateUtils.SUB_OCCUPATION_KEY, Default.toDefault(subInfo.getOccupation()));
        subHash.put(TemplateUtils.SUB_HOUSEHOLD_INCOME_KEY, ""+subInfo.getHouseholdIncome());
        subHash.put(TemplateUtils.SUB_INDUSTRY_KEY, Default.toDefault(subInfo.getIndustry()));
        subHash.put(TemplateUtils.SUB_JOB_STATUS_KEY, Default.toDefault(subInfo.getJobStatus()));
        subHash.put(TemplateUtils.SUB_EDUCATION_LEVEL_KEY, Default.toDefault(subInfo.getEducationLevel()));
        subHash.put(TemplateUtils.SUB_COMPANY_KEY, Default.toDefault(subInfo.getCompany()));


        //==== adding custom fields ======
        Vector subDetails = subInfo.getCustomFields();
        for (int i=0; subDetails!=null && i<subDetails.size(); i++) {
            SubscriberDetail detail = (SubscriberDetail)subDetails.elementAt(i);
            subHash.put("{{"+Default.toDefault(detail.getName()).trim()+"}}", Default.toDefault(detail.getValue()));
        }
        return subHash;
    }
    /**
     *
     * @return
     */
    public static Vector getKeywordsForSalesRepComments(String userId) {

        Vector vec = new Vector();

        vec.add(TemplateUtils.SUB_SALESFORCE_ID_KEY);
        vec.add(TemplateUtils.SUB_NUMBER_KEY);
        
        vec.add(TemplateUtils.SUB_EMAIL_KEY);
        vec.add(TemplateUtils.SUB_FIRSTNAME_KEY);
        vec.add(TemplateUtils.SUB_LASTNAME_KEY);
        vec.add(TemplateUtils.SUB_ADDR_KEY);
        vec.add(TemplateUtils.SUB_ADDRESS2_KEY);
        vec.add(TemplateUtils.SUB_CITY_KEY);
        vec.add(TemplateUtils.SUB_STATE_KEY);
        vec.add(TemplateUtils.SUB_ZIP_KEY);
        vec.add(TemplateUtils.SUB_COUNTRY_KEY);
        vec.add(TemplateUtils.SUB_PHONE_KEY);
        vec.add(TemplateUtils.SUB_BIRTHDATE_KEY);
        vec.add(TemplateUtils.SUB_GENDER_KEY);

        vec.add(TemplateUtils.SUB_MARITAL_STATUS_KEY);
        vec.add(TemplateUtils.SUB_OCCUPATION_KEY);
        vec.add(TemplateUtils.SUB_HOUSEHOLD_INCOME_KEY);
        vec.add(TemplateUtils.SUB_INDUSTRY_KEY);
        vec.add(TemplateUtils.SUB_JOB_STATUS_KEY);
        vec.add(TemplateUtils.SUB_EDUCATION_LEVEL_KEY);
        vec.add(TemplateUtils.SUB_COMPANY_KEY);

        vec.add(TemplateUtils.SUB_SOURCE_KEY);
        vec.add(TemplateUtils.SUB_SALESREP_KEY);
        vec.add(TemplateUtils.SUB_SALESSTATUS_KEY);

        /**
         * Load all user's custom fields and parse the campaign against them.
         * So that no keywords for a custom field, gets sent to a subscriber not having that custom field.
         */
        Vector userCustomFields = SubscriberManager.getUserCustomFields(userId);

        for (int i = 0; userCustomFields!=null && i<userCustomFields.size(); i++) {
            String aField = Default.toDefault((String)userCustomFields.get(i)).trim();
            if(!aField.equals("") && !vec.contains("{{"+aField+"}}"))
                vec.add("{{"+aField+"}}");
        }

        return vec;
    }

    /**
     *
     * @param subInfo
     * @param comments
     * @param userId
     * @return
     */
    public static String getCommentsWithSubscriberData(SubscriberInfo subInfo, String comments, String userId) {

        if(subInfo==null || userId.equals(""))
            return comments.trim();

        else if(comments.trim().equals(""))
            return "";

        try {
            Hashtable subHash = WorkflowManager.getSubsHashForSalesRepAlertComments(subInfo);

            Vector keywordsVec = WorkflowManager.getKeywordsForSalesRepComments(userId);
            Vector commentPartsVec = WorkflowManager.getAlertCommentsKeywordedParts(comments.trim(), keywordsVec);
            String newSalesRepComments = WorkflowManager.joinKeywordedCommentsUsingSubsData(subHash, commentPartsVec);

            return newSalesRepComments.trim();

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return comments.trim();
    }

    /**
     *
     * @param wfId
     * @param stepId
     * @return
     */
    public static Vector getWorkflowCheckStatus(int wfId, int stepId){

        try {
            return bmsServer.getWorkflowCheckStatus(wfId, stepId);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return new Vector();
    }

    /**
     *
     * @param stepId
     * @return
     */

    public static Hashtable getWorkflowCheckOptionSubCount(int stepId)
    {
        Hashtable optionSubcountHashtable = new Hashtable();
        try{
             optionSubcountHashtable = bmsServer.getWorkflowCheckOptionSubCount(stepId);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return optionSubcountHashtable;
    }

    /**
     *
     * @param stepId
     * @param optionNumber
     * @param offset
     * @param bucket
     * @return
     */
    public static Vector getWorkflowCheckOptionSubNum(int stepId, int optionNumber, int offset, int bucket){

        Vector subNumVector = new Vector();
        try {
            subNumVector = bmsServer.getWorkflowCheckOptionSubNum(stepId,optionNumber, offset, bucket);

        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return subNumVector;
    }
    /**
     *
     * @param workflowId
     * @param stepId
     * @return
     */
    public static boolean createWorkflowCheck(int workflowId,int stepId) {

        try{
            return bmsServer.createWorkflowCheck(workflowId,stepId);

        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }
    /**
     *
     * @param stepId
     * @return
     */
    public static WorkflowCircuitBreakerBean getWFCircuitBreakerById(int stepId) {

        WorkflowCircuitBreakerBean wfcbBean = new WorkflowCircuitBreakerBean();
        try {
            wfcbBean = bmsServer.getWFCircuitBreakerById(stepId);

        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return wfcbBean;
    }
    /**
     *
     * @param wfcbBean
     */
    public static void createWFCircuitBreaker(WorkflowCircuitBreakerBean wfcbBean) {

        try {
            bmsServer.createWFCircuitBreaker(wfcbBean);

        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
    }
    /**
     *
     * @param stepId
     */
    public static void deleteWFCircuitBreaker(int stepId) {

        try {
            bmsServer.deleteWFCircuitBreaker(stepId);

        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
    }
    /**
     * @return
     */
    public static Vector getPendingTriggerMailUserIds() {

        try {
            return bmsServer.getPendingTriggerMailUserIds();

        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }
    /**
     *
     * @param userId
     * @param batchSize
     * @return
     */
    public static Vector getPendingTriggerMailByUserId(String userId, int batchSize) {

        try {
            return bmsServer.getPendingTriggerMailByUserId(userId, batchSize);

        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }


    /**
     *
     * @param transmissionVec
     */
    public static void updatePendingTriggerMailStatus(String userId, Vector transmissionVec) {

        try {
            bmsServer.updatePendingTriggerMailStatus(userId, transmissionVec);

        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
    }
    /**
     *
     * @param stepIdSubNumVec
     * @return
     */
    public static Hashtable loadWorkflowTriggerMailNames(String userId, Vector stepIdSubNumNVVec) {

        try {
            return bmsServer.loadWorkflowTriggerMailNames(userId, stepIdSubNumNVVec);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Hashtable();
    }
    /**
     * @param bean
     * @return
     */
    public static MyC2YTriggerMailBean createMyC2YTriggerMail(MyC2YTriggerMailBean bean) {

        try {
            return bmsServer.createMyC2YTriggerMail(bean);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return null;
    }
    /**
     *
     * @param bean
     * @return
     */
    public static boolean updateMyC2YTriggerMailData(MyC2YTriggerMailBean bean) {

        try {
            return bmsServer.updateMyC2YTriggerMailData(bean);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }
    /**
     * @param beanVec
     * @return
     */
    public static boolean updateMyC2YTriggerMailAsSent(Vector beanVec) {

        try {
            return bmsServer.updateMyC2YTriggerMailAsSent(beanVec);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }
    /**
     * @param mailId
     * @return
     */
    public static MyC2YTriggerMailBean getMyC2YTriggerMailBeanByMailId(int mailId) {

        try {
            return bmsServer.getMyC2YTriggerMailBeanByMailId(mailId);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return null;
    }
    /**
     *
     * @param mailId
     * @return
     */
    public static Vector getMyC2YTriggerMailBeanByMailId(Vector mailIdVec) {

        try {
            return bmsServer.getMyC2YTriggerMailBeanByMailId(mailIdVec);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }
    /**
     * @param userId
     * @return
     */
    public static Vector getMyC2YTriggerMailBeanByUserId(String userId) {

        try {
            return bmsServer.getMyC2YTriggerMailBeanByUserId(userId);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }
    /**
     * @param userId
     * @param status
     * @param startDate
     * @param endDate
     * @param offset
     * @param batch
     * @return
     */
    public static Vector getMyC2YTriggerMailBeanByUserId(String userId, String status, long startDate, long endDate, int offset, int batch) {

        try {
            return bmsServer.getMyC2YTriggerMailBeanByUserId(userId, status, startDate, endDate, offset, batch);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }
    /**
     * @param subNum
     * @return
     */
    public static Vector getMyC2YTriggerMailBeanBySubNum(long subNum) {

        try {
            return bmsServer.getMyC2YTriggerMailBeanBySubNum(subNum);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }
    /**
     * @param subNum
     * @param startDate
     * @param endDate
     * @param offset
     * @param batch
     * @return
     */
    public static Vector getMyC2YTriggerMailBeanBySubNum(long subNum, long startDate, long endDate, int offset, int batch) {

        try {
            return bmsServer.getMyC2YTriggerMailBeanBySubNum(subNum, startDate, endDate, offset, batch);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }
    /**
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
    public static int countMyC2YTriggerMailByUserId(String userId, long startDate, long endDate) {

        try {
            return bmsServer.countMyC2YTriggerMailByUserId(userId, startDate, endDate);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return 0;
    }
    /**
     * @return
     */
    public static Vector getPendingUserIdsForMyC2YTriggerMail() {

        try {
            return bmsServer.getPendingUserIdsForMyC2YTriggerMail();

        } catch(Exception ex) {
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
     * @param offset
     * @param bucket
     * @param orderBy
     * @return
     */
    public static Vector workflowSnapShotByUserId(String userId, long startDate, long endDate, int offset, int bucket, String orderBy) {

        try {
            return bmsServer.workflowSnapShotByUserId(userId, startDate, endDate, offset, bucket, orderBy);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }
    /**
     *
     * @param samUserBean
     * @param startDate
     * @param endDate
     * @param offset
     * @param bucket
     * @param orderBy
     * @return
     */
    public static Vector workflowSnapShotBySAMUser(DashboardSAMBean samUserBean, long startDate, long endDate, int offset, int bucket, String orderBy) {

        try {
            return bmsServer.workflowSnapShotBySAMUser(samUserBean, startDate, endDate, offset, bucket, orderBy);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return new Vector();
    }
    /**
     *
     * @param samUserBean
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
    public static int countForWorkflowSnapShot(DashboardSAMBean samUserBean, String userId, long startDate, long endDate) {

        try {
            return bmsServer.countForWorkflowSnapShot(samUserBean, userId, startDate, endDate);

        } catch(Exception ex) {
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return 0;
    }
    /**
     *
     * @param workflowId
     * @param triggerOrder
     * @param label
     * @return
     */
    public static boolean updateWorkflowStepLabel(int workflowId, int triggerOrder, String label) {

        try {
            return bmsServer.updateWorkflowStepLabel(workflowId, triggerOrder, label);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     *
     * @param stepId
     * @param optionNumber
     * @param label
     * @return
     */
    public static boolean updateWorkflowOptionLabel(int stepId, int optionNumber, String label) {

        try {
            return bmsServer.updateWorkflowOptionLabel(stepId, optionNumber, label);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     *
     * @param userId
     * @param subNum
     * @return
     */
    public static Vector getWorkflowsForManualAddition(String userId, long subNum) {

        try {
            return bmsServer.getWorkflowsForManualAddition(userId, subNum);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }
    /**
     *
     * @param subNum
     * @return
     */
    public static Vector getManuallyAddedWorkflowIds(long subNum) {

        try {
            return bmsServer.getManuallyAddedWorkflowIds(subNum);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }
    /**
     *
     * @param subNum
     * @return
     */
    public static Hashtable getManuallyAddedWorkflowDetails(long subNum) {

        try {
            return bmsServer.getManuallyAddedWorkflowDetails(subNum);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Hashtable();
    }

    /**
     * @param workflowId
     * @param triggerOrder
     * @param subNum
     * @param overrideAllSteps
     * @param userIdUpdated
     * @return
     */
    public static boolean addManuallyToWorkflow(int workflowId, int triggerOrder, long subNum, boolean overrideAllSteps, String userIdUpdated) {

        try {
            return addManuallyToWorkflow(workflowId, triggerOrder, Default.toVector(""+subNum), overrideAllSteps, userIdUpdated);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param workflowId
     * @param triggerOrder
     * @param subNumVec
     * @param overrideAllSteps
     * @param userIdUpdated
     * @return
     */
    public static boolean addManuallyToWorkflow(int workflowId, int triggerOrder, Vector subNumVec, boolean overrideAllSteps, String userIdUpdated) {

        try {
            return bmsServer.addManuallyToWorkflow(workflowId, triggerOrder, subNumVec, overrideAllSteps, userIdUpdated);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * @param stepOptionVec
     * @return
     */
    public static boolean updateWorkflowOverrideMatrix(Vector stepOptionNVVec) {

        try {
            return bmsServer.updateWorkflowOverrideMatrix(stepOptionNVVec);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     *
     * @param subNumVec
     */
    public static void processForWorkflows(String userId, Vector subNumVec) {

        try {
            bmsServer.processForWorkflows(userId, subNumVec);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
    }
    /**
     *
     * @param workflowId
     * @param tagsVec
     * @return
     */
    public static boolean createUpdateWorkflowTags(int workflowId, Vector tagsVec) {

        if(workflowId<=0)//tagsVec.size()==0 IS ALLOWED TO EMPTY TAGS.
            return false;
        try {
            return bmsServer.createUpdateWorkflowTags(workflowId, tagsVec);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     *
     * @param workflowId
     * @param subNumVec
     * @return
     */
    public static Vector getExistingSubsInWorkflow(int workflowId, Vector subNumVec){

        try {
            return bmsServer.getExistingSubsInWorkflow(workflowId, subNumVec);
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }
    /**
     *
     * @param workflowId
     * @param stepId
     * @param optionNumber
     * @return
     */
    public static boolean deleteWorkflowOption(int workflowId, int stepId, int optionNumber) {

        try {
            WorkflowBean workflowBean = bmsServer.getWorkflowById(workflowId);
            if(workflowBean==null || workflowBean.isRunning())
                return false;

            WorkflowStepBean stepBean = workflowBean.getWorkflowStepByID(stepId);
            if(stepBean==null || workflowBean.getWorkflowStepByID(stepId).getStepOption(optionNumber)==null)
                return false;

            if(bmsServer.deleteWorkflowOptions(stepId, Default.toVector(stepBean.getStepOption(optionNumber)), true)) {

                //Re-Order OptionNumbers after an option has been deleted.
                updateWorkflowOptionOrderAfterDelete(workflowId, stepId);

                //Update Workflow Segements Collection.
                saveWorkflowSegments(workflowId, fetchSegmentsUsed(WorkflowManager.getWorkflowById(workflowId)));

                System.out.println("[WorkflowManager] WORKFLOW_OPTION_DELETED  userId:"+workflowBean.getUserID()+", workflowId:"+workflowId+", stepId:"
                                   +stepId+", optionNumber:"+optionNumber);
                return true;
            }

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }
    /**
     *
     * @param targetUserId
     * @param workflowId
     * @param newName
     * @param isSysAdminWorkflow
     * @return
     */
    public static WorkflowBean copyPasteWorkflow(String targetUserId, int workflowId, String newName, boolean isSysAdminWorkflow) {

        WorkflowBean workBean = getWorkflowById(workflowId);
        UserDataBean targetUserBean = UserManager.getUser(targetUserId);

        if(workBean==null || workBean.getWorkflowID()<=0 || newName.trim().equals("") || targetUserBean==null)
            return null;

        else if(!isSysAdminWorkflow && !workBean.getUserID().equalsIgnoreCase(targetUserId))
            return null;

        else if(isSysAdminWorkflow && !workBean.getUserID().equalsIgnoreCase(SysAdminManager.SYSADMIN_USER_ID))
            return null;

        try {
            //============= Copy Paste Basic Workflow Data ==============
            WorkflowBean copyBean = new WorkflowBean();
            copyBean.setName(newName);
            copyBean.setUserID(targetUserId);
            copyBean.setDescription(workBean.getDescription());
            copyBean.setManualAddition(workBean.isManualAddition());

            copyBean.setTagsVec(new Vector(workBean.getTagsVec()));
            copyBean.setDataVec(Default.cloneNameValueVector(workBean.getDataVec()));

            copyBean = WorkflowManager.createWorkflow(copyBean);
            if(copyBean==null || copyBean.getWorkflowID()<=0)
                return null;

            System.out.println("[WorkflowManager - copyPasteWorkflow()]: targetUserId:"+targetUserId+", copied.workflowId:"+workflowId
                               +", copied.name:"+workBean.getName()+", pasted.workflowId:"+copyBean.getWorkflowID()
                               +", pasted.name:"+copyBean.getName()+", isSysAdminWorkflow:"+isSysAdminWorkflow);

            Vector copyStepsVec = new Vector();
            //============================== Copy Paste Steps ===================================
            for(int triggerOrder=1; triggerOrder<=workBean.getStepsVec().size(); triggerOrder++) {

                WorkflowStepBean stepBean = workBean.getWorkflowStepByOrder(triggerOrder);
                if(stepBean==null)
                    break;

                WorkflowStepBean copyStepBean = new WorkflowStepBean();
                copyStepBean.setTriggerOrder(stepBean.getTriggerOrder());
                copyStepBean.setIsWait(stepBean.isWait());
                copyStepBean.setLabel(stepBean.getLabel());

                //========== Copy Paste Options ============
                for(int k=0; k<stepBean.getOptionsVec().size(); k++) {
                    WorkflowOptionBean optionBean = (WorkflowOptionBean)stepBean.getOptionsVec().get(k);
                    if(optionBean==null)
                        continue;

                    String optionID = stepBean.getTriggerOrder()+"."+optionBean.getOptionNumber();

                    WorkflowOptionBean copyOptBean = new WorkflowOptionBean(optionBean.getOptionNumber());
                    copyOptBean.setAfterwards(optionBean.getAfterwards());
                    copyOptBean.setApplyRuleCount(optionBean.getApplyRuleCount());
                    copyOptBean.setIsDoNothing(optionBean.isDoNothing());
                    copyOptBean.setIsScheduledBased(optionBean.isScheduledBased());
                    copyOptBean.setScheduleTime(optionBean.getScheduleTime());
                    copyOptBean.setLabel(optionBean.getLabel());
                    copyOptBean.setIsRuleOverrideAllowed(optionBean.isRuleOverrideAllowed());

                    if(isSysAdminWorkflow) {
                        copyOptBean.setRuleVec(copyRulesFromSysAdminWF(targetUserId, optionBean.getRuleVec()));
                    } else {
                        copyOptBean.setRuleVec(optionBean.getRuleVec());
                    }

                    if(optionBean.getBehaviorFilterNumber()>0) {//--- create copy of Behavior Filter ----
                        copyOptBean.setBehaviorFilterNumber(FilterManager.cloneBehaviorFilter(targetUserId, optionBean.getBehaviorFilterNumber()
                            , getWorkflowBehaviorFilterName(copyBean.getWorkflowID(), copyStepBean.getTriggerOrder(), copyOptBean.getOptionNumber(), false), isSysAdminWorkflow));
                    }
                    if(optionBean.getSegBehaviorFilterNumber()>0) {//--- create copy of Segment Behavior Filter ----
                        copyOptBean.setSegBehaviorFilterNumber(FilterManager.cloneBehaviorFilter(targetUserId, optionBean.getSegBehaviorFilterNumber()
                            , getWorkflowBehaviorFilterName(copyBean.getWorkflowID(), copyStepBean.getTriggerOrder(), copyOptBean.getOptionNumber(), true), isSysAdminWorkflow));
                    }

                    //========== Copy Paste Option Actions ============
                    for(int z=0; z<optionBean.getActionVec().size(); z++) {
                        WorkflowActionBean actionBean = (WorkflowActionBean)optionBean.getActionVec().get(z);

                        WorkflowActionBean copyActionBean = new WorkflowActionBean();
                        copyActionBean.setType(actionBean.getType());


                        if(actionBean.isSendAlertToSalesRep()) { //---- Copy Alert Action ----
                            System.out.println("[WorkflowManager - copyPasteWorkflow() "+optionID+"]: COPY_ACTION  SalesAlert");
                            copyActionBean.setDataVec(Default.cloneNameValueVector(actionBean.getDataVec()));

                        } else if(actionBean.isChangeScore()) {//---- Copy ScoreChange Action ----
                            System.out.println("[WorkflowManager - copyPasteWorkflow() "+optionID+"]: COPY_ACTION   ScoreChange");
                            copyActionBean.setDataVec(Default.cloneNameValueVector(actionBean.getDataVec()));

                        } else if(actionBean.isDoNothing()) {//---- Copy DoNothing Action ----
                            copyActionBean.setDataVec(Default.cloneNameValueVector(actionBean.getDataVec()));

                        } else if(actionBean.isSendEmail()) {//---- Copy Email Action ----
                            System.out.println("[WorkflowManager - copyPasteWorkflow() "+optionID+"]: COPY_ACTION_EMAIL   ld.campNum:"+actionBean.getCampaignNumber());
                            copyActionBean.setDataVec(Default.cloneNameValueVector(actionBean.getDataVec()));
                            copyActionBean.setCampaignNumber(0);

                            CampaignDataBean campBean = CampaignManager.getCampaignData(actionBean.getCampaignNumber());
                            long newCampNum = 0;
                            if(campBean!=null) {
                                String copyCampaignName = WorkflowManager.buildWorkflowCampaignName(copyBean.getName(), stepBean.getTriggerOrder()
                                    , optionBean.getOptionNumber());

                                if(isSysAdminWorkflow) {
                                    newCampNum = CampaignManager.copyCampaignForSysAdmin(targetUserId, copyCampaignName, actionBean.getCampaignNumber());
                                } else {
                                    newCampNum = CampaignManager.createCopyCampaign(workBean.getUserID(), actionBean.getCampaignNumber(), copyCampaignName);
                                }

                                if(newCampNum>0)
                                    copyActionBean.setCampaignNumber(newCampNum);//set copied campNum to Action Bean.
                            }
                            System.out.println("[WorkflowManager - copyPasteWorkflow()]: new.workflowId:"+copyBean.getWorkflowID()
                                +", old.campNum:"+actionBean.getCampaignNumber()+", new.campNum:"+newCampNum);

                        } else if(actionBean.isTriggerMail()) { //---- Copy TriggerMail Action ----
                            System.out.println("[WorkflowManager - copyPasteWorkflow() "+optionID+"]: COPY_ACTION    TriggerMail");
                            if(isSysAdminWorkflow) {
                                copyActionBean.setDataVec(new Vector());
                            } else {
                                copyActionBean.setDataVec(Default.cloneNameValueVector(actionBean.getDataVec()));
                            }

                        } else if(actionBean.isWait()) { //---- Copy Wait Action ----
                            System.out.println("[WorkflowManager - copyPasteWorkflow() "+optionID+"]: COPY_ACTION     Waiting");
                            copyActionBean.setDataVec(Default.cloneNameValueVector(actionBean.getDataVec()));
                        }
                        copyOptBean.getActionVec().add(copyActionBean);//Add new action to opton.
                        System.out.println("[WorkflowManager - copyPasteWorkflow() "+optionID+"]: copied actions to new OptionBean ");
                    }
                    copyStepBean.getOptionsVec().add(copyOptBean);//Add new option to step.
                }
                System.out.println("[WorkflowManager - copyPasteWorkflow() "+triggerOrder+"]:  copied new StepBean to stepsVec");
                copyStepsVec.add(copyStepBean);//Add new step to steps vector.
            }
            copyBean.setStepsVec(copyStepsVec);//Add stepsVec to Newly Created Workflow.
            System.out.println("[WorkflowManager - copyPasteWorkflow() copy.workId_"+copyBean.getWorkflowID()+"]:  copied new stepsVec to new WorkflowBean");

            //===== Create Copied Steps ======
            for(int i=0; i<copyStepsVec.size(); i++) {
               WorkflowStepBean stepBean = (WorkflowStepBean)copyStepsVec.get(i);
               if(WorkflowManager.createWorkflowStep(copyBean.getWorkflowID(), stepBean))
                   System.out.println("[WorkflowManager - copyPasteWorkflow()]: CREATED_STEP_"+stepBean.getTriggerOrder()+" pasted.workflowId:"+copyBean.getWorkflowID()
                                      +", step.isWait:"+stepBean.isWait()+", step.label:"+stepBean.getLabel());
            }

            //==== Update Workflow Segements Collection. =====
            saveWorkflowSegments(copyBean.getWorkflowID(), fetchSegmentsUsed(WorkflowManager.getWorkflowById(copyBean.getWorkflowID())));

            return WorkflowManager.getWorkflowById(copyBean.getWorkflowID());

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return null;
    }
    /**
     *
     * @param ruleVec
     * @return
     */
    private static Vector copyRulesFromSysAdminWF(String targetUserId, Vector ruleVec) {

        Vector newVec = new Vector();
        if(targetUserId.equals("") || ruleVec.isEmpty())
            return newVec;

        try {
            for(int i=0; i<ruleVec.size(); i++) {
                WorkflowRuleBean ruleBean = (WorkflowRuleBean)ruleVec.get(i);
                WorkflowRuleBean copyBean = new WorkflowRuleBean();

                copyBean.setDateFormat(ruleBean.getDateFormat());
                copyBean.setDayGap(ruleBean.getDayGap());
                copyBean.setFieldName(ruleBean.getFieldName());
                copyBean.setMatchValue(ruleBean.getMatchValue());
                copyBean.setRule(ruleBean.getRule());

                if(ruleBean.getListNumber()>0)
                    copyBean.setListNumber(copySysAdminList(targetUserId, ruleBean.getListNumber()));

                newVec.add(copyBean);
            }

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return newVec;
    }
    /**
     *
     * @param targetUserId
     * @param listNum
     * @return
     */
    private static long copySysAdminList(String targetUserId, long listNum) {

        ListDataBean listBean = ListManager.getList(listNum);
        if(listBean==null)
            return 0;

        try {
            ListDataBean copyBean = ListManager.getList(targetUserId, listBean.getName());
            if(copyBean!=null && copyBean.getListNumber()>0)
                return copyBean.getListNumber();

            return ListManager.createList(listBean.getName(), targetUserId);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return 0;
    }
    /**
     *
     * @param workflowId
     * @param stepId
     * @param optionNumber
     * @return
     */
    public static String getWorkflowBehaviorFilterName(int workflowId, int stepId, int optionNumber, boolean isSegment) {
       return "BMS.W-"+workflowId+".S-"+stepId+"."+(isSegment? "SEG.":"")
           +Default.toDefault(KeyGenerator.getActivationCode(""+System.currentTimeMillis())).trim();
    }

    /**
     *
     * @param userId
     * @return
     */
    private static boolean isTrialLimitExceed(String userId){
        try{
            return getWorkflowsByUserId(userId).size() >= TrialAccountManager.WORKFLOW_LIMIT;
        } catch(Exception ex){
            ex.printStackTrace();
            WebServerLogger.getLogger().log(ex);
        }
        return false;
    }


    /**
     *
     * @param campNum
     * @param startDate
     * @param endDate
     * @return
     */
    public static Vector getLinkIDBreakUp(long campNum, long startDate, long endDate) {

        try {
            return bmsServer.getLinkIDBreakUp(null, campNum, startDate, endDate);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }
    /**
     *
     * @param samUserBean
     * @param campNum
     * @param startDate
     * @param endDate
     * @return
     */
    public static Vector getLinkIDBreakUpWithSAM(DashboardSAMBean samUserBean, long campNum, long startDate, long endDate) {

        if(samUserBean == null || samUserBean.getUserId()==null || samUserBean.getUserId().equals("") || samUserBean.getMasterUserId().equals("")
           || samUserBean.getSalesRepNameVec().isEmpty())
            return new Vector();

        try {
            return bmsServer.getLinkIDBreakUp(samUserBean, campNum, startDate, endDate);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }

    /**
     *
     * @param campNum
     * @param articleNumber
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getClickCountByCampNum(long campNum, int articleNumber, long startDate, long endDate) {

        try {
            return bmsServer.getClickCountByCampNum(null, campNum, articleNumber, startDate, endDate);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return 0;
    }
    /**
     *
     * @param samUserBean
     * @param campNum
     * @param articleNumber
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getClickCountByCampNumWithSAM(DashboardSAMBean samUserBean, long campNum, int articleNumber, long startDate, long endDate) {

        try {
            return bmsServer.getClickCountByCampNum(samUserBean, campNum, articleNumber, startDate, endDate);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     *
     * @param campNum
     * @param articleNumber
     * @param offset
     * @param bucket
     * @param startDate
     * @param endDate
     * @return
     */
    public static Vector getClickSubscriberDetailByCampNum(long campNum, int articleNumber, int offset, int bucket, long startDate, long endDate) {

        try {
            return bmsServer.getClickSubscriberDetailByCampNum(null, campNum, articleNumber, offset, bucket, startDate, endDate);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }
    /**
     *
     * @param samUserBean
     * @param campNum
     * @param articleNumber
     * @param offset
     * @param bucket
     * @param startDate
     * @param endDate
     * @return
     */
    public static Vector getClickSubscriberDetailByCampNumWithSAM(DashboardSAMBean samUserBean, long campNum, int articleNumber, int offset, int bucket, long startDate, long endDate) {

        try {
            return bmsServer.getClickSubscriberDetailByCampNum(samUserBean, campNum, articleNumber, offset, bucket, startDate, endDate);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new Vector();
    }


}