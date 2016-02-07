/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2014 by the members listed in the COPYING,        *
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
package playground.agarwalamit.analysis.trip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.PersonArrivalEvent;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.events.PersonStuckEvent;
import org.matsim.api.core.v01.events.TransitDriverStartsEvent;
import org.matsim.api.core.v01.events.handler.PersonArrivalEventHandler;
import org.matsim.api.core.v01.events.handler.PersonDepartureEventHandler;
import org.matsim.api.core.v01.events.handler.PersonStuckEventHandler;
import org.matsim.api.core.v01.events.handler.TransitDriverStartsEventHandler;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.gbl.Gbl;

/**
 * Handles departure and arrival events and store total travel time of a person and 
 * travel time of each trip of a person segregated by leg mode
 * @author amit
 */

public class ModalTripTravelTimeHandler implements PersonDepartureEventHandler, PersonArrivalEventHandler, PersonStuckEventHandler, TransitDriverStartsEventHandler {

	private static final Logger LOGGER = Logger.getLogger(ModalTripTravelTimeHandler.class);
	private static final int MAX_STUCK_AND_ABORT_WARNINGS = 5;
	private final SortedMap<String, Map<Id<Person>, List<Double>>> mode2PersonId2TravelTimes;
	private final Map<Id<Person>, Double> personId2DepartureTime;
	private int warnCount = 0;
	private final SortedMap<String, Double> mode2NumberOfLegs ;
	private final List<Id<Person>> transitDriverPersons = new ArrayList<>();
	
	public ModalTripTravelTimeHandler() {
		this.mode2PersonId2TravelTimes = new TreeMap<String, Map<Id<Person>,List<Double>>>();
		this.personId2DepartureTime = new HashMap<Id<Person>, Double>();
		this.mode2NumberOfLegs = new TreeMap<String, Double>();
		LOGGER.warn("Excluding the departure and arrivals of transit drivers.");
	}

	@Override
	public void reset(int iteration) {
		this.mode2PersonId2TravelTimes.clear();
		this.personId2DepartureTime.clear();
		this.mode2NumberOfLegs.clear();
		this.transitDriverPersons.clear();
	}

	@Override
	public void handleEvent(PersonArrivalEvent event) {
		Id<Person> personId = event.getPersonId();
		
		if(transitDriverPersons.remove(personId)) return; // exclude arrivals of transit drivers
		
		String legMode = event.getLegMode();
		double arrivalTime = event.getTime();
		double travelTime = arrivalTime - this.personId2DepartureTime.remove(personId);

		if(this.mode2PersonId2TravelTimes.containsKey(legMode)){
			Map<Id<Person>, List<Double>> personId2TravelTimes = this.mode2PersonId2TravelTimes.get(legMode);
			if(personId2TravelTimes.containsKey(personId)){
				List<Double> travelTimes = personId2TravelTimes.get(personId);
				travelTimes.add(travelTime);
				personId2TravelTimes.put(personId, travelTimes);
			} else {
				List<Double> travelTimes = new ArrayList<Double>();
				travelTimes.add(travelTime);
				personId2TravelTimes.put(personId, travelTimes);
			}
		} else { 
			Map<Id<Person>, List<Double>> personId2TravelTimes = new HashMap<Id<Person>, List<Double>>();
			List<Double> travelTimes = new ArrayList<Double>();
			travelTimes.add(travelTime);
			personId2TravelTimes.put(personId, travelTimes);
			this.mode2PersonId2TravelTimes.put(legMode, personId2TravelTimes);
		}
	}

	@Override
	public void handleEvent(PersonDepartureEvent event) {
		Id<Person> personId = event.getPersonId();
		
		if(transitDriverPersons.contains(personId)) return; // exclude departures of transit drivers and remove them from arrivals
		
		double deartureTime = event.getTime();
		this.personId2DepartureTime.put(personId, deartureTime);
	}

	/**
	 * @return  trip time for each trip of each person segregated w.r.t. travel modes.
	 */
	public SortedMap<String, Map<Id<Person>, List<Double>>> getLegMode2PesonId2TripTimes (){
		return this.mode2PersonId2TravelTimes;
	}
	
	@Override
	public void handleEvent(PersonStuckEvent event) {
		this.warnCount++;
		if(this.warnCount <= MAX_STUCK_AND_ABORT_WARNINGS){
		LOGGER.warn("'StuckAndAbort' event is thrown for person "+event.getPersonId()+" on link "+event.getLinkId()+" at time "+event.getTime()+
				". \n Correctness of travel time for such persons can not be guaranteed.");
		if(this.warnCount== MAX_STUCK_AND_ABORT_WARNINGS) LOGGER.warn(Gbl.FUTURE_SUPPRESSED);
		}
	}
	@Override
	public void handleEvent(TransitDriverStartsEvent event) {
		transitDriverPersons.add(event.getDriverId());
	}
}