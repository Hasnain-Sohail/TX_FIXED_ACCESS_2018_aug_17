package com.hp.uca.expert.vp.pd.problem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.uca.expert.alarm.Alarm;
import com.hp.uca.expert.instancemapper.MapperUtils;
import com.hp.uca.expert.vp.pd.core.PTCL_Base_Problem;
import com.hp.uca.expert.vp.pd.util.CalculateUtil;

public class Problem_HW_TXM_Access_Fiber_Cut extends PTCL_Base_Problem {

	private static final Logger LOG = LoggerFactory.getLogger(Problem_HW_TXM_Access_Fiber_Cut.class);

	@Override
	public List<String> computeProblemEntity(Alarm a) throws Exception {
		// TODO Auto-generated method stub
		List<String>  peList = new ArrayList<String>();
		
		//Requirement:
		//(?sectionfrom_c.equals(?sectionfrom) && ?sectionto_c.equals(?sectionto))
		//||
		//(?sectionfrom_c.equals(?sectionto) && ?sectionto_c.equals(?sectionfrom)) ;
		//So in UCA:
		//We get the from and to value, then sort them to avoid the sequence difference
		
		String AEnd = MapperUtils.doMapping(a, "retrieveHWAEndFrom");
		String ZEnd = MapperUtils.doMapping(a, "retrieveHWZEndTo");
		
		if(!StringUtils.isEmpty(AEnd) 
				&& !StringUtils.isEmpty(ZEnd)) {
			
			String pe = "";
			List<String> sortedList = CalculateUtil.getSortedStringList(AEnd, ZEnd);
			
			for(int i=0; i<sortedList.size(); i++) {
				pe += sortedList.get(i) + "+";
			}
			
			if(!StringUtils.isEmpty(pe)) {
				//Add OC information
				String oc = MapperUtils.doMapping(a, "retrieveOC");
				
				if(!StringUtils.isEmpty(oc)) {
					pe += oc.toLowerCase();
				}
				
				LOG.debug(String
						.format("computeProblemEntity pe is [%s] for Problem Problem_HW_TXM_Access_Fiber_Cut alarm is [%s]",
								pe,
								a.getIdentifier()
								));
				
				peList.add(pe);
			}
		}
		
		
		return peList;
 	}
	
	
}
