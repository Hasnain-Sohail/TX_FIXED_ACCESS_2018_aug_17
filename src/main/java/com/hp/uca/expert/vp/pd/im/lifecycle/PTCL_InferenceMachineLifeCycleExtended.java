package com.hp.uca.expert.vp.pd.im.lifecycle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.uca.expert.alarm.Alarm;
import com.hp.uca.expert.event.Event;
import com.hp.uca.expert.scenario.Scenario;
import com.hp.uca.expert.vp.pd.core.PTCL_Base_Problem;

public class PTCL_InferenceMachineLifeCycleExtended extends InferenceMachineLifeCycleExtended {

	private static final Logger LOG = LoggerFactory
			.getLogger(PTCL_InferenceMachineLifeCycleExtended.class);
	
	public PTCL_InferenceMachineLifeCycleExtended(Scenario scenario) {
		super(scenario);
		// TODO Auto-generated constructor stub
	}
	
	public XMLGregorianCalendar stringToXMLGregorianCalendar(String s) 
		     throws ParseException, 
		            DatatypeConfigurationException
		 {
		 XMLGregorianCalendar result = null;
		 Date date;
		 SimpleDateFormat simpleDateFormat;
		 GregorianCalendar gregorianCalendar;
		 
		 simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		 simpleDateFormat.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
		                date = simpleDateFormat.parse(s);        
		                gregorianCalendar = 
		                    (GregorianCalendar)GregorianCalendar.getInstance();
		                gregorianCalendar.setTime(date);
		                result = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
		                return result;
		 }

	@Override
	public Event onAlarmCreationProcess(Alarm alarm) {
		// TODO Auto-generated method stub
		Alarm ptclAlarm = (Alarm) super.onAlarmCreationProcess(alarm);
		
		String time = ptclAlarm.getCustomFieldValue("creationTime");
		
		try {
			if(!StringUtils.isEmpty(time)) {
				XMLGregorianCalendar xmlGregorianCalendar = stringToXMLGregorianCalendar(time);
			
				ptclAlarm.setTimeInMilliseconds(xmlGregorianCalendar.toGregorianCalendar().getTimeInMillis());
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return ptclAlarm;
		
		
	}
	
	
}
