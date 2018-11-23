package com.hp.uca.expert.vp.pd.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.uca.common.trace.LogHelper;
import com.hp.uca.expert.alarm.Alarm;
import com.hp.uca.expert.group.Group;
import com.hp.uca.expert.vp.common.interfaces.CommonActions;
import com.hp.uca.expert.vp.pd.Const;
import com.hp.uca.expert.vp.pd.services.PD_Service_Action;
import com.hp.uca.expert.vp.pd.services.PD_Service_ProblemAlarm;
import com.hp.uca.expert.vp.pd.util.CalculateUtil;
import com.hp.uca.expert.vp.util.string.parameters.StringParameterUtil;
import com.hp.uca.expert.x733alarm.NetworkState;
import com.hp.uca.expert.x733alarm.OperatorState;
import com.hp.uca.expert.x733alarm.ProblemState;
import com.hp.uca.mediation.action.client.Action;
import com.hp.uca.mediation.action.exception.UcaActionExecutionException;
import com.hp.uca.mediation.action.exception.UcaActionInitializationException;
import com.hp.uca.mediation.action.jaxws.ActionRequest;


import java.util.ArrayList;
import java.util.List;


import org.apache.commons.lang.StringUtils;

public class PTCL_Base_Problem extends ProblemDefault {
	
	private static final Logger LOG = LoggerFactory
			.getLogger(PTCL_Base_Problem.class);

	/**
	 * Initialize User defined attributions
	 */
	public PTCL_Base_Problem() {
		super();
		// TODO Auto-generated constructor stub
		setLog(LoggerFactory.getLogger(this.getClass()));
	}

  
	/**
	 * add outage control
	 */
	@Override
	public boolean isAllCriteriaForProblemAlarmCreation(Group group)
			throws Exception {
		// TODO Auto-generated method stub
		if (LOG.isTraceEnabled()) {
			LogHelper.enter(LOG, "isAllCriteriaForProblemAlarmCreation()",
					group.getName());
		}
		boolean rt =super.isAllCriteriaForProblemAlarmCreation(group);
		
        if(rt) {
            rt = false;
            
            Alarm Trigger = (Alarm) group.getTriggerEvent();
            
            if(Trigger!=null 
                         && Trigger.getOperatorState() != OperatorState.TERMINATED 
                         && Trigger.getNetworkState() != NetworkState.CLEARED) {
                  rt = true;
            } else {
                  LOG.debug(String
                                .format("isAllCriteriaForProblemAlarmCreation returns [%s] for Group [%s(%s)], trigger is null or terminated/cleared",
                                             String.valueOf(rt),
                                             group.getProblemContext().getName(),
                                             group.getProblemEntity()                      
                                            ));
            }
     }
        if(rt)
        {
              Alarm Triggerfin = (Alarm) group.getTriggerEvent();

              String paGenValue =  Triggerfin.getVar().getString("paGenerate");
              String pbValue    =  Triggerfin.getCustomFieldValue("pb");
              if ((!StringUtils.isEmpty(paGenValue))&&(paGenValue.equalsIgnoreCase("true"))
            		  || (!StringUtils.isEmpty(pbValue)&&pbValue.contains("Sub")))
              {
                   rt=false;
                   LOG.debug(String
      						.format("isAllCriteriaForProblemAlarmCreation returns [%s] for Group [%s(%s)], trigger is already correlated by other group",
      								String.valueOf(rt),
      								group.getProblemContext().getName(),
      								group.getProblemEntity()				
      								));
              }

        }
        
/*        if(rt)
		{
			Alarm Triggerfin = (Alarm) group.getTriggerEvent();
		String paGenValue =	Triggerfin.getVar().getString("paGenerate");
			if ((!StringUtils.isEmpty(paGenValue))&&(paGenValue.equalsIgnoreCase("true")))
			{
				rt=false;
			}
		}
		*/
		if (rt)
		{
			for(Alarm alarmsubcond : group.getAlarmList()) {
			alarmsubcond.getVar().put("paGenerate", "true");
			}
		}
		LOG.debug(String
				.format("isAllCriteriaForProblemAlarmCreation returns [%s] for Group [%s(%s)], trigger alarm [%s]",
						String.valueOf(rt),
						group.getProblemContext().getName(),
						group.getProblemEntity(),
						group.getTriggerEvent().getIdentifier()					
						));

		if (LOG.isTraceEnabled()) {
			LogHelper.exit(LOG, "isAllCriteriaForProblemAlarmCreation()",
					String.valueOf(rt));
		}

		return rt;
	}
	
	
	
	/* According to Rizwan's requirement, if the Problem Alarm is handled, then 
	 * this PA will not be cleared automatically*/	
	
	@Override
	public boolean calculateIfProblemAlarmhasToBeCleared(Group group) throws Exception {
		// TODO Auto-generated method stub
		boolean rt = super.calculateIfProblemAlarmhasToBeCleared(group);
		
	/*	if(rt 
			&& group.getProblemAlarm().getProblemState()!=ProblemState.HANDLED) {
			rt = true;
		} else {
			rt = false;
		} */
		
		return rt;
	}

	@Override
	public String calculateProblemAlarmAdditionalText(Group group)
			throws Exception {
		if (LOG.isTraceEnabled()) {
			LogHelper.enter(LOG, "calculateProblemAlarmAdditionalText()" + " Group : "
					+ group.getName());
		}
		
		String result =super.calculateProblemAlarmAdditionalText(group);
		
		Alarm trigger = (Alarm)group.getTriggerEvent();
		result = CalculateUtil.replaceAllMappingTag(group,trigger, this, result);
		
		LOG.debug(String
				.format("calculateProblemAlarmAdditionalText returns [%s] for Group [%s(%s)], trigger alarm [%s],  ProblemAlarmAdditionalText [%s]",
						String.valueOf(result),
						group.getProblemContext().getName(),
						group.getProblemEntity(),
						trigger.getIdentifier()	,
						result
						));
		




		if (LOG.isTraceEnabled()) {
			LogHelper.exit(LOG, "calculateProblemAlarmAdditionalText()",
					"result : " + result);
		}
		
		if(result.equalsIgnoreCase("Problem Default additional text...")) {
			result = trigger.getAdditionalText();
		}
		
		return result;
	}
	
	
	@Override
	public String calculateProblemAlarmManagedEntity(Group group) throws Exception {
		// TODO Auto-generated method stub
		Alarm trigger = (Alarm) group.getTriggerEvent();
		return trigger.getOriginatingManagedEntity();
	}

	@Override
	public boolean isMatchingSubAlarmCriteria(Alarm a, Group group) throws Exception {
		// TODO Auto-generated method stub
		boolean rt = super.isMatchingSubAlarmCriteria(a, group);
		
		if(rt && CalculateUtil.tagExist(a, "Customized.OCMustEqualToTrigger", group.getProblemContext().getName())) {




			//Check the OC is the same with trigger's oc
			String subAlarmOC = CalculateUtil.getOCName(a);
			String triggerOC = "";
			
			if(group.getTriggerEvent()!=null) {
				Alarm trigger = (Alarm) group.getTriggerEvent();
				triggerOC = CalculateUtil.getOCName(trigger);
			}
			
			if(subAlarmOC.equalsIgnoreCase(triggerOC)) {
				rt = true;
			} else {
				rt = false;
			}
		} 
		
		return rt;
	}
	



























	
	public boolean commandAlreadyAdded(Action action, String key) {
		boolean ret = false;
		



		if(action != null && key != null) {
			for(ActionRequest.Command.Entry e : action.getCommand().getEntry()) {
				if(e.getKey().equalsIgnoreCase(key)) {
					ret = true;
					break;
				}
			}
		}

		
		return ret;
	}

	
	@Override
	public void calculateProblemAlarmOtherAttribute(Group group, Action action) throws Exception {
		// TODO Auto-generated method stub
		super.calculateProblemAlarmOtherAttribute(group, action);
		//default FC function
				Alarm trigger = (Alarm) group.getTriggerEvent();
				String fc = trigger.getCustomFieldValue(Const.Attr_FC_Read);
				if(StringUtils.isEmpty(fc)) {
					String defaultFC = CalculateUtil.getPassingFilterParamValue(group.getTriggerEvent(), 
							"Customized.defaultFC", group.getProblemContext().getName());
					
					if(!StringUtils.isEmpty(defaultFC)) {
						//calculate mapper
						String fcValue = CalculateUtil.getMapperValueWithFilterParamTag(group.getTriggerEvent(), 
								"Customized.defaultFC", group.getProblemContext().getName());
						
						//if mapper calculation can get result, then use this result, otherwise use defaultFC direclty
						if(!StringUtils.isEmpty(fcValue)) {
							action.addCommand(Const.Attr_FC_Write, fcValue);
						} else {		
							action.addCommand(Const.Attr_FC_Write, defaultFC);
						}
					}
				}
		
		//default alarmClassification function
			String aC = CalculateUtil.getPassingFilterParamValue(group.getTriggerEvent(), 
					"Customized.defaultAlarmClassification", group.getProblemContext().getName());
			
			if(!StringUtils.isEmpty(aC)) {
				//calculate mapper 
				String aCValue = CalculateUtil.getMapperValueWithFilterParamTag(group.getTriggerEvent(), 
						"Customized.defaultAlarmClassification", group.getProblemContext().getName());
				
				//if mapper calculation can get result, then use this result, otherwise use aC direclty
				if(!StringUtils.isEmpty(aCValue)) {
					action.addCommand(Const.Attr_Alarm_Classification_Write, aCValue);
				} else {		
					action.addCommand(Const.Attr_Alarm_Classification_Write, aC);
				}
			}
			
			if(action != null) {
				for(ActionRequest.Command.Entry e : action.getCommand().getEntry()) {
					if(e.getKey().equalsIgnoreCase("Notification_Identifier")) {
						e.setValue("0");
						break;
					}

				}
			}
		
		//need to add customized attributes
		// calculateProblemAlarmCustomizedAttribute(group, action);
	}

	@Override
	public void whatToDoWhenProblemAlarmIsAttachedToGroup(Group group) throws Exception {
		// TODO Auto-generated method stub
		super.whatToDoWhenProblemAlarmIsAttachedToGroup(group);
		
		String externalScript = CalculateUtil.getPassingFilterParamValue(group.getTriggerEvent(), 
				"Customized.ExternalScript", group.getProblemContext().getName());
		String scriptParameter = CalculateUtil.getPassingFilterParamValue(group.getTriggerEvent(), 
				"Customized.ExternalScriptParameter", group.getProblemContext().getName());
		
		if(!StringUtils.isEmpty(externalScript) 
				&& !StringUtils.isEmpty(scriptParameter) 
				&& group.getProblemAlarm()!=null) {
				
				String userText = group.getProblemAlarm().getCustomFieldValue(Const.Attr_User_Text_Read);
						
				String alarmsList = StringParameterUtil.calculateStringValue(scriptParameter, 
						group.getProblemAlarm(), group, this, null);
			
				if(alarmsList!=null 
						&& !StringUtils.isEmpty(userText) 
						&& !userText.contains(Const.ALREADY_SEND_SCRIPT_TEXT)) {
					
					executeScript(group, externalScript, alarmsList);	
					
					//Add indication mark, so that after VP restart this script will not be called again
					
					
					if(!StringUtils.isEmpty(userText)) {
						userText += Const.ALREADY_SEND_SCRIPT_TEXT;
						
						List<CommonActions.KV> toRefresh = new ArrayList();
						
						toRefresh.add(new CommonActions.KV(Const.Attr_User_Text_Write, userText)); 
						
	 					LOG.debug(String
								.format("whatToDoWhenProblemAlarmIsAttachedToGroup for Group [%s(%s)], PA alarm [%s] will update PA's userText,  userText is [%s]",
										group.getProblemContext().getName(),
										group.getProblemEntity(),
										group.getProblemAlarm().getIdentifier()	,
										userText
										));
						PD_Service_Action.updateAlarm(getScenario(), group.getProblemAlarm(), 
								this, toRefresh);
					}
					
				}
		}
	}

	
	
	public Boolean executeScript(Group group, String externalScript, String alarmsList){
		Boolean result = false;
		
		if(StringUtils.isEmpty(externalScript) 
				|| StringUtils.isEmpty(alarmsList)) {
			log.debug("executeScript, externalScript or alarmList is NULL");
			return result;
		}
		
		
		log.info("External Script is: " + externalScript);
		log.info("External Scripts parameter is: " + alarmsList);	
		
		LOG.debug(String
				.format("executeScript for Group [%s(%s)], PA alarm [%s] will send notification script,  alarmList is [%s]",
						group.getProblemContext().getName(),
						group.getProblemEntity(),
						group.getProblemAlarm().getIdentifier()	,
						alarmsList
						));

		try {
			String scriptRef = this.getMainPolicy().getActions()
                    .getDefaultActionScriptReference();
			
			Action action = new Action(scriptRef);
            action.addCommand("Command", externalScript);
            action.addCommand("Argument", alarmsList);
            
            if (log.isInfoEnabled()) {
                log.info("-+-+-+-+ build action with: " + externalScript
                             + " parameter: " + alarmsList);
            }
            
            getScenario().addAction(action);
            if (log.isInfoEnabled()) {
                log.info("executing action in async way");
            }
            
            action.executeAsync(alarmsList);
            result = true;
		} catch (UcaActionInitializationException e) {
            LogHelper.logErrorDebug(log, "Exception received", e);
		} catch (UcaActionExecutionException e) {
            LogHelper.logErrorDebug(log, "Exception received", e);
		} catch (SecurityException e) {
            LogHelper.logErrorDebug(log, "Exception received", e);
		}

		
		return result;
	}


}
