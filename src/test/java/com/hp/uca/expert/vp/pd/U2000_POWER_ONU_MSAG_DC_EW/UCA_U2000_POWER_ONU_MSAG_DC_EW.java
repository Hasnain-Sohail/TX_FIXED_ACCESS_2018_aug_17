package com.hp.uca.expert.vp.pd.U2000_POWER_ONU_MSAG_DC_EW;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.loader.csv.Loader;
import org.neo4j.loader.csv.Report;
import org.neo4j.loader.csv.utils.TmpDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hp.uca.common.misc.Constants;
import com.hp.uca.expert.alarm.Alarm;
import com.hp.uca.expert.group.Group;
import com.hp.uca.expert.topology.TopoAccess;
import com.hp.uca.expert.vp.common.AbstractTest;
import junit.framework.JUnit4TestAdapter;

/**
 * The Class PropagationTest.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class UCA_U2000_POWER_ONU_MSAG_DC_EW extends AbstractTest {
	
	/**
	 * The log.
	 */
	private static Logger log = LoggerFactory.getLogger(UCA_U2000_POWER_ONU_MSAG_DC_EW.class);
	//TXM1/src/test/resources/com/hp/uca/expert/vp/pd/HW_TXM_Access_Fiber_Cut/Alarm_HW_TXM_Access_Fiber_Cut.xml
	protected static final String ALARM_FILE_PREFIX = "src/test/resources/com/hp/uca/expert/vp/pd/U2000_POWER_ONU_MSAG_DC_EW/";
	private static final String ALARM_1= "Alarm_U2000_POWER_ONU_MSAG_DC_EW";
	//private static final String ALARM_2= "Alarm_U2000_POWER_ONU_MSAG_DC_EW2";
	private static final String groupKEY1 = "<p>Problem_U2000_POWER_ONU_MSAG_DC_EW</p><e>hw_t2000_sys ihvtfs01_ns:.u2000_islamabad_02 managed_element \"7340839\"</e>";
	
	private static final String SCENARIO_BEAN_NAME = "com.hp.uca.expert.vp.pd.ProblemDetection";
	/**
	 * The Constant SCENARIO_PD_NAME.
	 */
	private static final String SCENARIO_PD_NAME = "com.hp.uca.expert.vp.pd.ProblemDetection";

	/**
	 * The Constant SCENARIO_TSP_NAME.
	 */
	private static final String SCENARIO_TSP_NAME = "com.hp.uca.expert.vp.tp.TopologyPropagation";

	/**
	 * The Constant TOPOLOGY_DATALOAD_DIR.
	 */
	private static final String TOPOLOGY_DATALOAD_DIR = "valuepack/topologyDataload";

	/**
	 * The tmp dir.
	 */
	private TmpDir tmpDir=null;
	
	public UCA_U2000_POWER_ONU_MSAG_DC_EW() {
		super("test1");
		// TODO Auto-generated constructor stub
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info(Constants.TEST_START.val()
				+ UCA_U2000_POWER_ONU_MSAG_DC_EW.class.getName());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info(Constants.TEST_END.val()
				+ UCA_U2000_POWER_ONU_MSAG_DC_EW.class.getName()
				+ Constants.GROUP_ALT1_SEPARATOR.val());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		log.info(Constants.TEST_START.val()
				+ UCA_U2000_POWER_ONU_MSAG_DC_EW.class.getName());
		
		super.setUp();

	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	@After
	public void tearDown() throws Exception {
		log.info(Constants.TEST_END.val()
				+ UCA_U2000_POWER_ONU_MSAG_DC_EW.class.getName()
				+ Constants.GROUP_ALT1_SEPARATOR.val());
	}

	/**
	 * Way to run tests via ANT Junit
	 * 
	 * @return the JUnit4TestAdapter
	 */
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(UCA_U2000_POWER_ONU_MSAG_DC_EW.class);
	}


	/**
	 * @throws Exception
	 */
	@Test
	@DirtiesContext
	public void test() throws Exception {
		try {
						
			//group name:
			this.sendAlarms(ALARM_FILE_PREFIX,ALARM_1 , true);
			//this.sendAlarms(ALARM_FILE_PREFIX,ALARM_2 , true);
			System.out.println(this);
			Thread.sleep(50 * SECOND);
			
			List<Group> gps = this.getGroupList(groupKEY1);
			Assert.assertFalse("no group found:" + groupKEY1 , gps.isEmpty());
			//Thread.sleep(20 * SECOND);
			Group gw =this.getGroupWithNotClearedPA(groupKEY1);
			//Thread.sleep(20 * SECOND);
			Assert.assertNotNull("PA is cleared or filtered out for all group:" + groupKEY1, gw);
			Alarm pa = gw.getProblemAlarm();
			Assert.assertNotNull("problem alarm should be created now", pa);
			
			Assert.assertEquals(1, gw.getNumber());// total 
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {

			getScenario().getSession().dump();
			}
	}
	
}