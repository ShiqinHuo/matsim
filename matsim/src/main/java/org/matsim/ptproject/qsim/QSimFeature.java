/* *********************************************************************** *
 * project: org.matsim																																							 *
 *                               																			                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2009 by the members listed in the COPYING,        *
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


package org.matsim.ptproject.qsim;

import java.util.Collection;

import org.matsim.api.core.v01.network.Link;
import org.matsim.core.mobsim.framework.PersonAgent;


public interface QSimFeature {

	void afterPrepareSim();

	void beforeCleanupSim();

	void beforeHandleAgentArrival(PersonAgent agent);

	void afterAfterSimStep(double time);

	void beforeHandleUnknownLegMode(double now, PersonAgent agent, Link link);

	Collection<PersonAgent> createAgents();

	void afterActivityBegins(PersonAgent agent, int planElementIndex);

	void afterActivityEnds(PersonAgent agent, double time);
	
	void agentCreated(PersonAgent agent);

}
