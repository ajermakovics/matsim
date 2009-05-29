/* *********************************************************************** *
 * project: org.matsim.*
 * KnowledgePlansCalcRoute.java
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

package playground.christoph.router;

import org.apache.log4j.Logger;
import org.matsim.core.api.network.Network;
import org.matsim.core.api.population.Person;
import org.matsim.core.mobsim.queuesim.QueueNetwork;
import org.matsim.core.network.NetworkLayer;
import org.matsim.core.router.PlansCalcRoute;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelCost;
import org.matsim.core.router.util.TravelTime;

import playground.christoph.router.util.PersonLeastCostPathCalculator;

public class KnowledgePlansCalcRoute extends PlansCalcRoute implements Cloneable{
	
	protected Person person;
	protected QueueNetwork queueNetwork;
	protected double time;
	
	private final static Logger log = Logger.getLogger(KnowledgePlansCalcRoute.class);
	
	public KnowledgePlansCalcRoute(final NetworkLayer network, final TravelCost costCalculator, final TravelTime timeCalculator) 
	{
		super(network, costCalculator, timeCalculator);
	}
	
	public KnowledgePlansCalcRoute(final Network network, final LeastCostPathCalculator router, final LeastCostPathCalculator routerFreeflow) 
	{
		super(network, router, routerFreeflow);
	}
	
	/*
	 * We have to hand over the person to the Cost- and TimeCalculators of the Router.
	 */
	public void setPerson(Person person)
	{
		this.person = person;
		
		if(this.getLeastCostPathCalculator() instanceof PersonLeastCostPathCalculator)
		{
			((PersonLeastCostPathCalculator)this.getLeastCostPathCalculator()).setPerson(person);
		}
		
		if(this.getPtFreeflowLeastCostPathCalculator() instanceof PersonLeastCostPathCalculator)
		{
			((PersonLeastCostPathCalculator)this.getPtFreeflowLeastCostPathCalculator()).setPerson(person);
		}
	}
	
	public Person getPerson()
	{
		return this.person;
	}
	
	/*
	 * We have to hand over the queueNetwork to the Cost- and TimeCalculators of the Router.
	 */
	public void setQueueNetwork(QueueNetwork queueNetwork)
	{
		this.queueNetwork = queueNetwork;
		
		if(this.getLeastCostPathCalculator() instanceof PersonLeastCostPathCalculator)
		{
			((PersonLeastCostPathCalculator)this.getLeastCostPathCalculator()).setQueueNetwork(queueNetwork);
		}
		
		if(this.getPtFreeflowLeastCostPathCalculator() instanceof PersonLeastCostPathCalculator)
		{
			((PersonLeastCostPathCalculator)this.getPtFreeflowLeastCostPathCalculator()).setQueueNetwork(queueNetwork);
		}
	}
	
	public QueueNetwork QueueNetwork()
	{
		return this.queueNetwork;
	}

	/*
	 * We have to hand over the time to the Cost- and TimeCalculators of the Router.
	 */
	public void setTime(Double time)
	{
		this.time = time;
		
		if(this.getLeastCostPathCalculator() instanceof PersonLeastCostPathCalculator)
		{
			((PersonLeastCostPathCalculator)this.getLeastCostPathCalculator()).setTime(time);
		}
		
		if(this.getPtFreeflowLeastCostPathCalculator() instanceof PersonLeastCostPathCalculator)
		{
			((PersonLeastCostPathCalculator)this.getPtFreeflowLeastCostPathCalculator()).setTime(time);
		}
	}
	
	public double getTime()
	{
		return this.time;
	}
	
	@Override
	public KnowledgePlansCalcRoute clone()
	{
		LeastCostPathCalculator routeAlgoClone;
		LeastCostPathCalculator routeAlgoFreeflowClone;
		
		if(this.getLeastCostPathCalculator() instanceof PersonLeastCostPathCalculator)
		{
			routeAlgoClone = ((PersonLeastCostPathCalculator)this.getLeastCostPathCalculator()).clone();
		}
		else
		{
			log.error("Could not clone the Route Algorithm - use reference to the existing Algorithm and hope the best...");
			routeAlgoClone = this.getLeastCostPathCalculator();
		}
		
		if(this.getPtFreeflowLeastCostPathCalculator() instanceof PersonLeastCostPathCalculator)
		{
			routeAlgoFreeflowClone = ((PersonLeastCostPathCalculator)this.getPtFreeflowLeastCostPathCalculator()).clone();
		}
		else
		{
			log.error("Could not clone the Freeflow Route Algorithm - use reference to the existing Algorithm and hope the best...");
			routeAlgoFreeflowClone = this.getPtFreeflowLeastCostPathCalculator();
		}
		
		/* 
		 * routeAlgo and routeAlgoFreeflow may be the references to the same object.
		 * In this case -> use only one clone.
		 */
		KnowledgePlansCalcRoute clone;
		if(this.getLeastCostPathCalculator() == this.getPtFreeflowLeastCostPathCalculator())
		{
			clone = new KnowledgePlansCalcRoute(this.queueNetwork.getNetworkLayer(), routeAlgoClone, routeAlgoClone);
		}
		else
		{
			clone = new KnowledgePlansCalcRoute(this.queueNetwork.getNetworkLayer(), routeAlgoClone, routeAlgoFreeflowClone);
		}
		
		clone.setQueueNetwork(this.queueNetwork);
		//clone.queueNetwork = this.queueNetwork;
/*		
		log.info(routeAlgo.getClass());
		log.info("org   " + this);
		log.info("clone " + clone);
		
		log.info("org wrapper   " + this.routeAlgo);
		log.info("clone wrapper " + clone.routeAlgo);
*/		
		return clone;
	}
}
