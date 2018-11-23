package com.hp.uca.expert.vp.pd.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;

import com.hp.uca.common.trace.LogHelper;
import com.hp.uca.expert.alarm.Alarm;
import com.hp.uca.expert.event.Event;
import com.hp.uca.expert.group.Group;
import com.hp.uca.expert.instancemapper.MapperUtils;
import com.hp.uca.expert.vp.common.core.CommonTopology;
import com.hp.uca.expert.vp.util.string.parameters.StringParameterUtil;



public class CalculateUtil {
	private static final Logger LOG = LoggerFactory.getLogger(CalculateUtil.class);
	
	public static String replaceAllMappingTag(Group group,Event event, CommonTopology problemOrPropagation, String template) {
		if (LOG.isTraceEnabled()) {
			LogHelper.enter(LOG, "CalculateUtil.replaceMappingTag(), {}", "alarm : " + event.getIdentifier()+" for template "+template);
			
		}
		
		String ret = null;
		if(template!=null){
			ret = StringParameterUtil.calculateStringValue(template, event, group, problemOrPropagation, null);
		}
		
		if (LOG.isTraceEnabled()) {
			LogHelper.exit(LOG, "CalculateUtil.replaceMappingTag(), {}",
					"alarm : " + event.getIdentifier() +" for template "+template+ " result:" + ret);
		}
		return ret;
	}
	
	/**
	 * 
	 * @param group
	 * @param tagName
	 * @return
	 */
	public static int countAlarmsSize(Group group,String tagName){
		int size = 0;
		
		Iterator<Alarm> alarmIte = group.getAlarmList().iterator();
		Event alarm = null;
		
		while (alarmIte.hasNext()) {
			alarm = alarmIte.next();
			if(CalculateUtil.tagExist(alarm, tagName, group.getProblemContext().getName())){
				size++;
			}
		}
		return size;
	}
	
	
	/**
	 * 
	 * @param group
	 * @param tagName
	 * @return
	 */
	public static int countAlarmsSizeWithoutTrigger(Group group,String tagName){
		int size = 0;
		
		Iterator<Alarm> alarmIte = group.getAlarmList().iterator();
		Event alarm = null;
		Alarm trigger = (Alarm) group.getTriggerEvent();
		
		while (alarmIte.hasNext()) {
			alarm = alarmIte.next();
			if(!alarm.getIdentifier().equalsIgnoreCase(trigger.getIdentifier())
					&& CalculateUtil.tagExist(alarm, tagName, group.getProblemContext().getName())){
				size++;
			}
		}
		return size;
	}
	
	
	public static boolean tagExist(Event event, String tag, String problem) {	
		if (LOG.isTraceEnabled()) {
			LogHelper.enter(LOG, "CalculateUtil.tagExist(), {}",
					"alarm : " + event.getIdentifier() + ", problem : " + problem + ", filterParam : " + tag);

		}
		
		boolean rt = false;
		
		if(event != null && problem != null && tag != null) {


			if(event.getPassingFilters() != null) {
				Set<String> tagSet = event.getPassingFiltersTags().get(problem);
				

				if(tagSet.contains(tag)) {
					rt = true;
				}
			}
		}
		
		return rt;
	}
	
	/**
	 * common
	 * 
	 * @param event
	 * @param filterParam
	 * @param problem
	 * @return
	 */
	public static String getPassingFilterParamValue(Event event, String filterParam, String problem) {
		if (LOG.isTraceEnabled()) {
			LogHelper.enter(LOG, "CalculateUtil.getPassingFiltersParamValue(), {}",
					"alarm : " + event.getIdentifier() + ", problem : " + problem + ", filterParam : " + filterParam);
		}

		String result = null;
		if (event != null && problem != null && filterParam != null) {
			if (event.getPassingFiltersParams() != null && event.getPassingFiltersParams().get(problem) != null
					&& event.getPassingFiltersParams().get(problem).get(filterParam) != null) {
				result = event.getPassingFiltersParams().get(problem).get(filterParam);
				
			} else {
				if (LOG.isDebugEnabled()) {
					//Configuration tag [ADTextAttrsFromCustomize] not found in filter file for Problem [Problem_LinkDown]. Using blank value.
					LOG.debug(String
							.format("CalculateUtil.getPassingFilterParamValue: Configuration tag [%s] not found in filter file for Problem [%s]. Using blank value for this tag",
									filterParam,
									problem
									));					
				}
			}
		}

		if (LOG.isTraceEnabled()) {
			LogHelper.exit(LOG, "CalculateUtil.getPassingFiltersParamValue()", "result : " + result);
		}

		return result;
	}
	
	
	public static String getMapperValueWithFilterParamTag(Event event, String tag, String problemName ){
		if (LOG.isTraceEnabled()) {
			LogHelper.enter(LOG, "calculateMapperValueWithFilterParamTag(), {}",
					"alarm : " + event.getIdentifier() + ", problem : " + problemName + ", paramKey : " + tag);
		}
		
		String mapperName = null;
		String value = "";
		if(event!=null 
				&& !StringUtils.isEmpty(tag)){
			mapperName = getPassingFilterParamValue(event, tag, problemName);
			
			if(!StringUtils.isEmpty(mapperName)){
				value = MapperUtils.doMapping(event, mapperName);
			} else {
				LOG.warn(String.format("calculateMapperValueWithFilterParamTag, can't find mapper tag, "
						+ "alarm is [%s], probleName is [%s], tag is [%s]", event.getIdentifier(), 
						problemName, tag));
			}
		}
		
		if(StringUtils.isEmpty(value)){
			value = "";
			LOG.warn(String.format("calculateMapperValueWithFilterParamTag, can't get mapper value, "
					+ "alarm is [%s], probleName is [%s], tag is [%s]", event.getIdentifier(), 
					problemName, tag));
		}
		
		if (LOG.isTraceEnabled()) {
			LogHelper.exit(LOG, "calculateMapperValueWithFilterParamTag, {}", "result : " + value);
		}
		return value;
	}
	


	//OPERATION_CONTEXT ihvtfs01_ns:.Nor_Tx_ZTE alarm_object 1887310 
	public static String getOCName(Alarm a) {
		String oc = "";
		
		if(a != null && a.getIdentifier()!=null) {
			String identifier = a.getIdentifier();
			
			int idx  = identifier.indexOf("OPERATION_CONTEXT");
			int idx2 = identifier.indexOf("alarm_object");
			if(idx>=0 && idx2>0) {
				String ocStr = identifier.substring(idx+18, idx2);
				
				int idx3 = ocStr.indexOf(":");
				if(idx3>0) {
					oc = ocStr.substring(idx3).trim();
				} else {
					oc = ocStr.trim();
				}
			}
			
		}
		
		return oc;
	}
	
	public static String getBtsId(Alarm a) {
		String bts_id = "";
		
		if(a != null && a.getAdditionalText()!=null) {
			String Addi_text = a.getAdditionalText();
			
			int idx2 = Addi_text.indexOf("@");
			if(idx2>0) {
				bts_id = Addi_text.substring(1, idx2);
				}
			}
		
		return bts_id;
	}
	
	public static int getNECount(Group group) {
		
		String neName = "";
		List<String> neNameList = new ArrayList<String>();
		
		for(Alarm alarm : group.getAlarmList()) {
			
			neName = CalculateUtil.getMapperValueWithFilterParamTag(alarm, 
					"Customized.NE_Name", group.getProblemContext().getName());
			if(!neNameList.contains(neName.toLowerCase())) {
				neNameList.add(neName.toLowerCase());
			}
		
		}
		
		int count = neNameList.size();
		
		group.getVar().put("count", count);
		
		return count;
	}
	
	
	public static List<String> getSortedStringList(String... strings) {
		
		List<String> sortedList = new ArrayList<String> ();
		
		for(String string : strings) {
			if(!StringUtils.isEmpty(string)) {
				sortedList.add(string);
			}
		}

		sortedList.sort(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				// TODO Auto-generated method stub
				return o1.compareTo(o2);
			}
			
		});
		
		
		return sortedList;
	}
	
	
}
