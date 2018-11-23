package com.hp.uca.expert.vp.pd.problem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.uca.common.trace.LogHelper;
import com.hp.uca.expert.alarm.Alarm;
import com.hp.uca.expert.group.Group;
import com.hp.uca.expert.vp.pd.Const;
import com.hp.uca.expert.vp.pd.core.PTCL_Base_Problem;
import com.hp.uca.expert.vp.pd.util.CalculateUtil;
import com.hp.uca.expert.vp.util.string.parameters.StringParameterUtil;
import com.hp.uca.expert.x733alarm.NetworkState;
import com.hp.uca.expert.x733alarm.OperatorState;
import com.hp.uca.mediation.action.client.Action;

public class Problem_Broadband_Voice_Outage_U2K_U31_Entity extends PTCL_Base_Problem {

	private static final Logger LOG = LoggerFactory.getLogger(Problem_Broadband_Voice_Outage_U2K_U31_Entity.class);
	
	
	
	public String calcualteAlarmName(Group group) {
		String rt = "";
		
		Alarm trigger = (Alarm) group.getTriggerEvent();
		
		boolean communcationFailAlarmExist = false;
		String technology = CalculateUtil.getMapperValueWithFilterParamTag(trigger, 
				"Customized.Technology", group.getProblemContext().getName());
		
	/*	if(StringUtils.isEmpty(technology))
		{
			technology = "MSAG";
		}*/
		if(StringUtils.isEmpty(technology)) {
			if (trigger.getAdditionalText().contains("alarmNetype=ZXDSL"))
			{technology = "DSLAM";}
			else
			technology = "MSAG";
		}
		
		
			for(Alarm alarm : group.getAlarmList()) {
				if(CalculateUtil.tagExist(alarm, "Customized.Communication_failed", 
						group.getProblemContext().getName())) {
					communcationFailAlarmExist = true;
					break;
				}
			}
		
			if(communcationFailAlarmExist) {
				rt = technology + " DOWN";
			} else {
				rt = technology + " NB SERVICE DOWN";
			}
			
		
		if(!StringUtils.isEmpty(rt)) {
			group.getVar().put("nativeCause", rt);
		}
		
		return rt;
	}

	@Override
	public String calculateProblemAlarmAdditionalText(Group group) throws Exception {
		// TODO Auto-generated method stub
		if (LOG.isTraceEnabled()) {
			LogHelper.enter(LOG, "calculateProblemAlarmAdditionalText()" + " Group : "
					+ group.getName());
		}
		
		String result = "";
		String template = CalculateUtil.getPassingFilterParamValue(group.getTriggerEvent(), 
				"ProblemAlarm.Creation.AdditionalText", group.getProblemContext().getName());
		
		String nativeCause = calcualteAlarmName(group);
		
		Map<String, Object> attachParameters = new HashMap<String, Object>();
		if(!StringUtils.isEmpty(nativeCause)) {		
			attachParameters.put("nativeCause", nativeCause);
		}
		
		if(result != null) {
			result = StringParameterUtil.calculateStringValue(template, 
				group.getTriggerEvent(), group, this, attachParameters);
		}
		
		LOG.debug(String
				.format("calculateProblemAlarmAdditionalText returns [%s] for Group [%s(%s)], trigger alarm [%s],  ProblemAlarmAdditionalText [%s]",
						String.valueOf(result),
						group.getProblemContext().getName(),
						group.getProblemEntity(),
						group.getTriggerEvent().getIdentifier()	,
						result
						));
		
		if (LOG.isTraceEnabled()) {
			LogHelper.exit(LOG, "calculateProblemAlarmAdditionalText()",
					"result : " + result);
		}
		return result;
	}

	@Override
	public void calculateProblemAlarmOtherAttribute(Group group, Action action) throws Exception {
		// TODO Auto-generated method stub
		super.calculateProblemAlarmOtherAttribute(group, action);
		
		String nativeCause = group.getVar().getString("nativeCause");
		
		if(!StringUtils.isEmpty(nativeCause)) {
			action.addCommand(Const.Attr_NATIVE_CAUSE_Write, nativeCause);
		}
	}	
	
}
