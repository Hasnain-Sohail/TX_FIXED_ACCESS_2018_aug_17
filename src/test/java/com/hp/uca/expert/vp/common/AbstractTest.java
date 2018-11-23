package com.hp.uca.expert.vp.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.uca.common.misc.Constants;
import com.hp.uca.common.trace.LogHelper;
import com.hp.uca.expert.alarm.Alarm;
import com.hp.uca.expert.group.Group;
import com.hp.uca.expert.testmaterial.AbstractJunitIntegrationTest;
import com.hp.uca.expert.x733alarm.NetworkState;
import com.hp.uca.expert.x733alarm.ProblemState;


/**
 * @author pimi
 * 
 */
public class AbstractTest extends AbstractJunitIntegrationTest {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractTest.class);

	protected static final String SCENARIO_BEAN_NAME = "com.hp.uca.expert.vp.pd.ProblemDetection";	

	private int defaultSleepAfterSendAlarms = 10 * 1000;

	protected String uc;

	//protected static TmpDir tmpDir;

	protected String fileType;
	
	private boolean useTopo = true;

	/*protected static final String DB_FOLDER = "target" + File.separator + "db" + File.separator
			+ "skeleton.h2.db";
	*/
	public AbstractTest(String uc) {
		
		this.uc = uc;
		this.fileType = ".xml";
	}

	public static void beforeClass() {
		// clear DB file.
		/*File file = new File(DB_FOLDER);
		if (file.exists()) {
			if (!file.delete()) {
				throw new SkeletonException("cannot delete DB file:" + DB_FOLDER);
			}

		}
*/	}

	public void setUp() throws Exception {

		LOG.info(Constants.TEST_START.val() + this.getClass().getName());
		// load topo data.
/*		if (this.useTopo) {

			tmpDir = new TmpDir("topos");
			Loader loader = new Loader(TopoAccess.getGraphDB(), tmpDir.tmpCsvPath());
			Report report = loader.loadAll();
			LOG.info(report.toString());
		}
*/
		// init here
		initTest(SCENARIO_BEAN_NAME, BMK_PATH);

		getScenario().setTestOnly(true);

		// start DB flow.
/*		Map<String, AlarmFlow> fMap = getScenario().getValuePack().getDbFlowsMap();
		for (AlarmFlow af : fMap.values()) {
			af.start();
		}
*/	}

	public void tearDown() throws Exception {
		/*if (this.useTopo) {

			LOG.info(Constants.TEST_END.val() + AbstractTest.class.getName()
					+ Constants.GROUP_ALT1_SEPARATOR.val());
			tmpDir.cleanup();
		}
*/	}

/*	public void sendAlarms(String[] idxs, boolean sleep) {
		for (int i = 0; i <= idxs.length; i++) {
			boolean slp = (i == idxs.length - 1 ? sleep : false);
			this.sendAlarms(idxs[i], slp);
		}
	}*/

	public void sendAlarms(String AlarmPerfix, String fileIdx, boolean sleep) {

		String file = AlarmPerfix + fileIdx + this.fileType;
		this.sendAlarmFile(file, sleep);

	}
	
	public synchronized void  sendAlarms(String AlarmPerfix, String fileIdx){
		
		String file = AlarmPerfix + fileIdx + this.fileType;
		this.sendAlarmFile(file, false);
	}

	public void sendAlarmFile(String file) {
		this.sendAlarmFile(file, 0);
	}

	public void sendAlarmFile(String file, boolean sleep) {
		int slp = sleep ? this.defaultSleepAfterSendAlarms : 0;
		this.sendAlarmFile(file, slp);
	}

	public void sendAlarmFile(String file, int sleep) {
		this.doSendFile(file);
		if (sleep <= 0) {
			return;
		}
		this.sleep(sleep);
	}

	protected void doSendFile(String file) {
		this.getProducer().sendAlarms(file);
	}

	protected void dumpSession() {
		getScenario().getSession().dump();
	}

	protected void sleep(int sleep) {
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			//throw new Exception(e);
		}
	}

	public List<Group> getGroupList(String name) {

		Collection<Group> gL = this.getGroupsFromWorkingMemory();
		List<Group> rt = new ArrayList<Group>();
		for (Group g : gL) {
			String pe = g.getName();
			if (pe.equals(name)) {
				rt.add(g);
			}
		}
		return rt;

	}
	public Group getGroupWithNotClearedPA(String groupName) {

		List<Group> rtL = new ArrayList<Group>();

		for (Group gw : this.getGroupList(groupName)) {
			if (this.isPANotCleared(gw)) {
				rtL.add(gw);
			}
		}

		if (rtL.isEmpty()) {
			
			return null;
		} else if (rtL.size() == 1) {
			return rtL.get(0);
		}else{
			return rtL.get(0);
		}
	}
	
	public boolean isPANotCleared(Group group) {
		Alarm pa = group.getProblemAlarm();
		if (pa == null) {
			return false;
		}
		
		boolean rt =true;
		rt=	!pa.getProblemState().equals(ProblemState.CLOSED);
		
		rt=NetworkState.NOT_CLEARED.equals(pa.getNetworkState());
		if (LOG.isDebugEnabled()) {
			LogHelper.method(LOG, "isPANotCleared()", String.valueOf(rt));
		}
		return rt;
	}
}
