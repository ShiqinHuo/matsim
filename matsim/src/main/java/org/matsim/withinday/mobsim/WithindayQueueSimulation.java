/* *********************************************************************** *
 * project: org.matsim.*
 * WithindayQueueSimulation.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.withinday.mobsim;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.ptproject.qsim.QSim;
import org.matsim.withinday.WithindayAgentFactory;
import org.matsim.withinday.WithindayControler;
import org.matsim.withinday.trafficmanagement.TrafficManagement;

/**
 * This extension of the QueueSimulation is used for withinday replanning.
 *
 * @author dgrether
 *
 */
public class WithindayQueueSimulation extends QSim {

	private final WithindayControler controler;

	private TrafficManagement trafficManagement;

	public WithindayQueueSimulation(final Scenario scenario, final EventsManager events, final WithindayControler controler) {
		super(scenario, events);
		this.controler = controler;
		super.setAgentFactory(new WithindayAgentFactory(this, controler.getConfig().withinday(), this.controler.getAgentLogicFactory()));
	}

	@Override
	protected void prepareSim() {
	  super.prepareSim();
		this.trafficManagement.simulationPrepared(this.getSimTimer().getSimStartTime());
	}

	@Override
	protected void beforeSimStep(final double time) {
		super.beforeSimStep(time);
  	//check capacity change whishes for pending items
		this.trafficManagement.updateBeforeSimStep(time);
	}


	public void setTrafficManagement(final TrafficManagement trafficManagement) {
		this.trafficManagement = trafficManagement;
	}






}
