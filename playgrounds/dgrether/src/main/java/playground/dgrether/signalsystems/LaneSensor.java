/* *********************************************************************** *
 * project: org.matsim.*
 * DgLaneSensor
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2011 by the members listed in the COPYING,        *
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
package playground.dgrether.signalsystems;

import org.matsim.api.core.v01.network.Link;
import org.matsim.core.api.experimental.events.LaneEnterEvent;
import org.matsim.core.api.experimental.events.LaneLeaveEvent;
import org.matsim.lanes.data.Lane;


/**
 * @author dgrether
 *
 */
public final class LaneSensor {

	private Link link;
	private Lane lane;
	private int agentsOnLane = 0;

	public LaneSensor(Link link, Lane lane) {
		this.link = link;
		this.lane = lane;
	}

	public void handleEvent(LaneLeaveEvent event) {
		this.agentsOnLane--;
	}

	public void handleEvent(LaneEnterEvent event) {
		this.agentsOnLane++;
	}

	public int getNumberOfCarsOnLane() {
		return this.agentsOnLane;
	}
	
	

}