/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
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

package org.matsim.basic.signalsystems;

import java.util.ArrayList;
import java.util.List;

import org.matsim.basic.v01.IdImpl;
import org.matsim.interfaces.basic.v01.Id;
import org.matsim.signalsystems.control.SignalSystemControler;

/**
 * @author dgrether
 */
public class BasicSignalGroupDefinitionImpl implements BasicSignalGroupDefinition {
  
	private final Id id;
	private Id lightSignalSystemDefinitionId;
	private List<Id> laneIds;
	private List<Id> toLinkIds;
	private final Id linkRefId;
	
	private org.matsim.signalsystems.control.SignalSystemControler signalSystemControler = null;

	public BasicSignalGroupDefinitionImpl(Id linkRefId, Id id) {
		this.linkRefId = linkRefId;
		this.id = id;
	}

	/**
	 * @see org.matsim.basic.signalsystems.BasicSignalGroupDefinition#setLightSignalSystemDefinitionId(org.matsim.basic.v01.IdImpl)
	 */
	public void setLightSignalSystemDefinitionId(IdImpl id) {
		this.lightSignalSystemDefinitionId = id;
	}

	/**
	 * @see org.matsim.basic.signalsystems.BasicSignalGroupDefinition#addLaneId(org.matsim.interfaces.basic.v01.Id)
	 */
	public void addLaneId(Id laneId) {
		if (this.laneIds == null)
			this.laneIds = new ArrayList<Id>();
		this.laneIds.add(laneId);
	}
	
	/**
	 * @see org.matsim.basic.signalsystems.BasicSignalGroupDefinition#getLinkRefId()
	 */
	public Id getLinkRefId() {
		return linkRefId;
	}

	/**
	 * @see org.matsim.basic.signalsystems.BasicSignalGroupDefinition#addToLinkId(org.matsim.interfaces.basic.v01.Id)
	 */
	public void addToLinkId(Id linkId) {
		if (this.toLinkIds == null)
			this.toLinkIds = new ArrayList<Id>();
		this.toLinkIds.add(linkId);
	}

	/**
	 * @see org.matsim.basic.signalsystems.BasicSignalGroupDefinition#getId()
	 */
	public Id getId() {
		return id;
	}

	/**
	 * @see org.matsim.basic.signalsystems.BasicSignalGroupDefinition#getLightSignalSystemDefinitionId()
	 */
	public Id getLightSignalSystemDefinitionId() {
		return lightSignalSystemDefinitionId;
	}

	/**
	 * @see org.matsim.basic.signalsystems.BasicSignalGroupDefinition#getLaneIds()
	 */
	public List<Id> getLaneIds() {
		return laneIds;
	}

	/**
	 * @see org.matsim.basic.signalsystems.BasicSignalGroupDefinition#getToLinkIds()
	 */
	public List<Id> getToLinkIds() {
		return toLinkIds;
	}

	/**
	 * @see org.matsim.basic.signalsystems.BasicSignalGroupDefinition#setResponsibleLSAControler(org.matsim.signalsystems.control.SignalSystemControler)
	 */
	public void setResponsibleLSAControler(SignalSystemControler signalSystemControler) {
		this.signalSystemControler = signalSystemControler;		
	}
	
	/**
	 * @see org.matsim.basic.signalsystems.BasicSignalGroupDefinition#isGreen()
	 */
	public boolean isGreen(){
		return this.signalSystemControler.givenSignalGroupIsGreen(this);
	}
	
}
