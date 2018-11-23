/**
 * 
 */
package com.hp.uca.expert.vp.pd.core;

import org.slf4j.LoggerFactory;

import com.hp.uca.common.trace.LogHelper;
import com.hp.uca.expert.alarm.AlarmDeletion;
import com.hp.uca.expert.alarm.AlarmUpdater;
import com.hp.uca.expert.group.Group;

/**
 * The Class MyProblemDefault extends ProblemDefault and overrides <li>
 * {@link #whatToDoWhenProblemAlarmIsAttachedToGroup(Group)} <li>
 * {@link #whatToDoWhenProblemAlarmIsNoMoreEligible(Group)}
 */
public class MyProblemDefault extends ProblemDefault {

	/**
	 * 
	 */
	public MyProblemDefault() {
		super();
		setLog(LoggerFactory.getLogger(MyProblemDefault.class));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hp.uca.expert.vp.pd.core.ProblemDefault#
	 * whatToDoWhenProblemAlarmIsAttachedToGroup(com.hp.uca.expert.group.Group)
	 */
	@Override
	public void whatToDoWhenProblemAlarmIsAttachedToGroup(Group group)
			throws Exception {
		if (getLog().isTraceEnabled()) {
			LogHelper.enter(getLog(),
					"whatToDoWhenProblemAlarmIsAttachedToGroup()",
					group.getName());
		}

		getLog().info(
				"Forwarding ProblemAlarm to topoPropagation: "
						+ group.getProblemAlarm().getIdentifier());

		getScenario().applyOrchestration(group.getProblemAlarm());

		if (getLog().isTraceEnabled()) {
			LogHelper.exit(getLog(),
					"whatToDoWhenProblemAlarmIsAttachedToGroup()");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hp.uca.expert.vp.pd.core.ProblemDefault#
	 * whatToDoWhenProblemAlarmIsNoMoreEligible(com.hp.uca.expert.group.Group)
	 */
	@Override
	public void whatToDoWhenProblemAlarmIsNoMoreEligible(Group group)
			throws Exception {
		if (getLog().isTraceEnabled()) {
			LogHelper.enter(getLog(),
					"whatToDoWhenProblemAlarmIsNoMoreEligible()",
					group.getName());
		}

		getLog().info(
				"Forwarding ProblemAlarm to topoPropagation: "
						+ group.getProblemAlarm().getIdentifier());
		AlarmDeletion alarmDeletion = new AlarmDeletion();
		AlarmUpdater.replaceAllFields(group.getProblemAlarm(), alarmDeletion);

		getScenario().applyOrchestration(alarmDeletion);

		super.whatToDoWhenProblemAlarmIsNoMoreEligible(group);

		if (getLog().isTraceEnabled()) {
			LogHelper.exit(getLog(),
					"whatToDoWhenProblemAlarmIsNoMoreEligible()");
		}
	}

}
