package com.hp.uca.expert.vp.pd.core;

import org.slf4j.LoggerFactory;

/**
 * The Class MyGeneralBehavior.
 * 
 * This behavior is empty and ready to define methods to customize the
 * GeneralBehaviorDefault
 */
public class MyGeneralBehavior extends GeneralBehaviorDefault {

	/**
	 * Instantiates a new my general behavior.
	 */
	public MyGeneralBehavior() {
		super();
		setLog(LoggerFactory.getLogger(MyGeneralBehavior.class));
	}

}
