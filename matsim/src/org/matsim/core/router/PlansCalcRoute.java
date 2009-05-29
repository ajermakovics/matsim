/* *********************************************************************** *
 * project: org.matsim.*
 * PlansCalcRoute.java
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

package org.matsim.core.router;

import org.matsim.api.basic.v01.TransportMode;
import org.matsim.core.api.network.Link;
import org.matsim.core.api.network.Network;
import org.matsim.core.api.network.Node;
import org.matsim.core.api.population.Activity;
import org.matsim.core.api.population.Leg;
import org.matsim.core.api.population.NetworkRoute;
import org.matsim.core.api.population.Person;
import org.matsim.core.api.population.Plan;
import org.matsim.core.api.population.PlanElement;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup;
import org.matsim.core.network.NetworkFactory;
import org.matsim.core.router.costcalculators.FreespeedTravelTimeCost;
import org.matsim.core.router.util.DijkstraFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;
import org.matsim.core.router.util.TravelCost;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.misc.Time;
import org.matsim.population.algorithms.AbstractPersonAlgorithm;
import org.matsim.population.algorithms.PlanAlgorithm;

public class PlansCalcRoute extends AbstractPersonAlgorithm implements PlanAlgorithm {

	//////////////////////////////////////////////////////////////////////
	// member variables
	//////////////////////////////////////////////////////////////////////

	/**
	 * The routing algorithm to be used for finding routes on the network with actual travel times.
	 */
	private LeastCostPathCalculator routeAlgo;
	/**
	 * The routing algorithm to be used for finding routes in the empty network, with freeflow travel times.
	 */
	private LeastCostPathCalculator routeAlgoFreeflow;
	
	private LeastCostPathCalculatorFactory factory;

	private PlansCalcRouteConfigGroup configGroup = new PlansCalcRouteConfigGroup();
	
	private final NetworkFactory routeFactory;
	
	//////////////////////////////////////////////////////////////////////
	// constructors
	//////////////////////////////////////////////////////////////////////
	/**
	 * Creates a rerouting strategy module using the rerouting of the factory
	 * @deprecated use the constructor with the config group as argument
	 */
	@Deprecated
	public PlansCalcRoute(final Network network, final TravelCost costCalculator,
			final TravelTime timeCalculator, LeastCostPathCalculatorFactory factory){
		super();
		this.factory = factory;
		this.routeAlgo = this.factory.createPathCalculator(network, costCalculator, timeCalculator);
		FreespeedTravelTimeCost ptTimeCostCalc = new FreespeedTravelTimeCost(-1.0, 0.0, 0.0);
		this.routeAlgoFreeflow = this.factory.createPathCalculator(network, ptTimeCostCalc, ptTimeCostCalc);
		this.routeFactory = network.getFactory();
	}
	
	/**
	 * Creates a rerouting strategy module using dijkstra rerouting
	 * @deprecated use the constructor with the config group as argument
	 */
	@Deprecated
	public PlansCalcRoute(final Network network, final TravelCost costCalculator, final TravelTime timeCalculator) {
		this(network, costCalculator, timeCalculator, new DijkstraFactory());
	}
	
	/**
	 * Uses the speed factors from the config group and the rerouting of the factory 
	 */
	public PlansCalcRoute(final PlansCalcRouteConfigGroup group, final Network network, 
			final TravelCost costCalculator,
			final TravelTime timeCalculator, LeastCostPathCalculatorFactory factory){
		this(network, costCalculator, timeCalculator, factory);
		this.configGroup = group;
	}
	
	/**
	 * Creates a rerouting strategy module using dijkstra rerouting
	 */
	public PlansCalcRoute(final PlansCalcRouteConfigGroup group, final Network network, final TravelCost costCalculator, final TravelTime timeCalculator) {
		this(group, network, costCalculator, timeCalculator, new DijkstraFactory());
	}

	/**
	 * @param router The routing algorithm to be used for finding routes on the network with actual travel times.
	 * @param routerFreeflow The routing algorithm to be used for finding routes in the empty network, with freeflow travel times.
	 * @deprecated use one of the other constructors of this class
	 */
	@Deprecated
	public PlansCalcRoute(final Network network, final LeastCostPathCalculator router, final LeastCostPathCalculator routerFreeflow) {
		super();
		this.routeAlgo = router;
		this.routeAlgoFreeflow = routerFreeflow;
		this.routeFactory = network.getFactory();
	}
	
	public final LeastCostPathCalculator getLeastCostPathCalculator(){
		return this.routeAlgo;
	}
	
	public final LeastCostPathCalculator getPtFreeflowLeastCostPathCalculator(){
		return this.routeAlgoFreeflow;
	}

	//////////////////////////////////////////////////////////////////////
	// run methods
	//////////////////////////////////////////////////////////////////////

	@Override
	public void run(final Person person) {
		int nofPlans = person.getPlans().size();

		for (int planId = 0; planId < nofPlans; planId++) {
			Plan plan = person.getPlans().get(planId);
			handlePlan(plan);
		}
	}

	public void run(final Plan plan) {
		handlePlan(plan);
	}

	//////////////////////////////////////////////////////////////////////
	// helper methods
	//////////////////////////////////////////////////////////////////////

	protected void handlePlan(final Plan plan) {
		double now = 0;

		// loop over all <act>s
		Activity fromAct = null;
		Activity toAct = null;
		Leg leg = null;
		for (PlanElement pe : plan.getPlanElements()) {
			if (pe instanceof Leg) {
				leg = (Leg) pe;
			} else if (pe instanceof Activity) {
				if (fromAct == null) {
					fromAct = (Activity) pe;
				} else {
					toAct = (Activity) pe;
					
					double endTime = fromAct.getEndTime();
					double startTime = fromAct.getStartTime();
					double dur = fromAct.getDuration();
					if (endTime != Time.UNDEFINED_TIME) {
						// use fromAct.endTime as time for routing
						now = endTime;
					} else if ((startTime != Time.UNDEFINED_TIME) && (dur != Time.UNDEFINED_TIME)) {
						// use fromAct.startTime + fromAct.duration as time for routing
						now = startTime + dur;
					} else if (dur != Time.UNDEFINED_TIME) {
						// use last used time + fromAct.duration as time for routing
						now += dur;
					} else {
						throw new RuntimeException("activity of plan of person " + plan.getPerson().getId().toString() + " has neither end-time nor duration." + fromAct.toString());
					}
					
					now += handleLeg(leg, fromAct, toAct, now);
					
					fromAct = toAct;
				}
			}
		}
	}

	/**
	 * @param leg the leg to calculate the route for.
	 * @param fromAct the Act the leg starts
	 * @param toAct the Act the leg ends
	 * @param depTime the time (seconds from midnight) the leg starts
	 * @return the estimated travel time for this leg
	 */
	public double handleLeg(final Leg leg, final Activity fromAct, final Activity toAct, final double depTime) {
		TransportMode legmode = leg.getMode();

		if (legmode == TransportMode.car) {
			return handleCarLeg(leg, fromAct, toAct, depTime);
		} else if (legmode == TransportMode.ride) {
			return handleRideLeg(leg, fromAct, toAct, depTime);
		} else if (legmode == TransportMode.pt) {
			return handlePtLeg(leg, fromAct, toAct, depTime);
		} else if (legmode == TransportMode.walk) {
			return handleWalkLeg(leg, fromAct, toAct, depTime);
		} else if (legmode == TransportMode.bike) {
			return handleBikeLeg(leg, fromAct, toAct, depTime);
		} else if (legmode == TransportMode.undefined) {
			/* balmermi: No clue how to handle legs with 'undef' mode
			 *                Therefore, handle it similar like bike mode with 50 km/h
			 *                and no route assigned  */
			return handleUndefLeg(leg, fromAct, toAct, depTime);
		} else {
			throw new RuntimeException("cannot handle legmode '" + legmode + "'.");
		}
	}

	protected double handleCarLeg(final Leg leg, final Activity fromAct, final Activity toAct, final double depTime) {
		double travTime = 0;
		Link fromLink = fromAct.getLink();
		Link toLink = toAct.getLink();
		if (fromLink == null) throw new RuntimeException("fromLink missing.");
		if (toLink == null) throw new RuntimeException("toLink missing.");

		Node startNode = fromLink.getToNode();	// start at the end of the "current" link
		Node endNode = toLink.getFromNode(); // the target is the start of the link

//		CarRoute route = null;
		Path path = null;
		if (toLink != fromLink) {
			// do not drive/walk around, if we stay on the same link
			path = this.routeAlgo.calcLeastCostPath(startNode, endNode, depTime);
			if (path == null) throw new RuntimeException("No route found from node " + startNode.getId() + " to node " + endNode.getId() + ".");
			NetworkRoute route = (NetworkRoute) ((Network) fromLink.getLayer()).getFactory().createRoute(TransportMode.car, fromLink, toLink);
			route.setNodes(fromLink, path.nodes, toLink);
			route.setTravelTime((int) path.travelTime);
			route.setTravelCost(path.travelCost);
			leg.setRoute(route);
			travTime = (int) path.travelTime;
		} else {
			// create an empty route == staying on place if toLink == endLink
			NetworkRoute route = (NetworkRoute) ((Network) fromLink.getLayer()).getFactory().createRoute(TransportMode.car, fromLink, toLink);
			route.setTravelTime(0);
			leg.setRoute(route);
			travTime = 0;
		}

		leg.setDepartureTime(depTime);
		leg.setTravelTime(travTime);
		leg.setArrivalTime(depTime + travTime);
		return travTime;
	}

	private double handleRideLeg(final Leg leg, final Activity fromAct, final Activity toAct, final double depTime) {
		// handle a ride exactly the same was as a car
		// the simulation has to take care that this leg is not really simulated as a stand-alone driver
		return handleCarLeg(leg, fromAct, toAct, depTime);
	}

	private double handlePtLeg(final Leg leg, final Activity fromAct, final Activity toAct, final double depTime) {
		// currently: calc route in empty street network, use twice the traveltime
		// TODO [MR] later: use special pt-router

		int travTime = 0;
		Link fromLink = fromAct.getLink();
		Link toLink = toAct.getLink();
		if (fromLink == null) throw new RuntimeException("fromLink missing.");
		if (toLink == null) throw new RuntimeException("toLink missing.");

		Path path = null;
//		CarRoute route = null;
		if (toLink != fromLink) {
			Node startNode = fromLink.getToNode();	// start at the end of the "current" link
			Node endNode = toLink.getFromNode(); // the target is the start of the link
			// do not drive/walk around, if we stay on the same link
			path = this.routeAlgoFreeflow.calcLeastCostPath(startNode, endNode, depTime);
			if (path == null) throw new RuntimeException("No route found from node " + startNode.getId() + " to node " + endNode.getId() + ".");
			// we're still missing the time on the final link, which the agent has to drive on in the java mobsim
			// so let's calculate the final part.
			double travelTimeLastLink = toLink.getFreespeedTravelTime(depTime + path.travelTime);
			travTime = (int) (((int) path.travelTime + travelTimeLastLink) * this.configGroup.getPtSpeedFactor());
			NetworkRoute route = (NetworkRoute) this.routeFactory.createRoute(TransportMode.car, fromLink, toLink); // TODO [MR] change to PtRoute once available
			route.setNodes(fromLink, path.nodes, toLink);
			route.setTravelTime(travTime);
			route.setTravelCost(path.travelCost); 
			leg.setRoute(route);
		} else {
			// create an empty route == staying on place if toLink == endLink
			NetworkRoute route = (NetworkRoute) this.routeFactory.createRoute(TransportMode.car, fromLink, toLink);
			route.setTravelTime(0);
			leg.setRoute(route);
			travTime = 0;
		}

		leg.setDepartureTime(depTime);
		leg.setTravelTime(travTime);
		leg.setArrivalTime(depTime + travTime);
		return travTime;
	}

	private double handleWalkLeg(final Leg leg, final Activity fromAct, final Activity toAct, final double depTime) {
		// make simple assumption about distance and walking speed
		double dist = CoordUtils.calcDistance(fromAct.getCoord(), toAct.getCoord());
		// create an empty route, but with realistic traveltime
		NetworkRoute route = (NetworkRoute) this.routeFactory.createRoute(TransportMode.car, fromAct.getLink(), toAct.getLink());
		int travTime = (int)(dist / this.configGroup.getWalkSpeedFactor());
		route.setTravelTime(travTime);
		leg.setRoute(route);
		leg.setDepartureTime(depTime);
		leg.setTravelTime(travTime);
		leg.setArrivalTime(depTime + travTime);
		return travTime;
	}

	private double handleBikeLeg(final Leg leg, final Activity fromAct, final Activity toAct, final double depTime) {
		// make simple assumption about distance and cycling speed
		double dist = CoordUtils.calcDistance(fromAct.getCoord(), toAct.getCoord());
		// create an empty route, but with realistic traveltime
		NetworkRoute route = (NetworkRoute) this.routeFactory.createRoute(TransportMode.car, fromAct.getLink(), toAct.getLink());
		int travTime = (int)(dist / this.configGroup.getBikeSpeedFactor());
		route.setTravelTime(travTime);
		leg.setRoute(route);
		leg.setDepartureTime(depTime);
		leg.setTravelTime(travTime);
		leg.setArrivalTime(depTime + travTime);
		return travTime;
	}

	private double handleUndefLeg(final Leg leg, final Activity fromAct, final Activity toAct, final double depTime) {
		// make simple assumption about distance and a dummy speed (50 km/h)
		double dist = CoordUtils.calcDistance(fromAct.getCoord(), toAct.getCoord());
		// create an empty route, but with realistic traveltime
		NetworkRoute route = (NetworkRoute) this.routeFactory.createRoute(TransportMode.car, fromAct.getLink(), toAct.getLink());
		int travTime = (int)(dist / this.configGroup.getUndefinedModeSpeedFactor());
		route.setTravelTime(travTime);
		leg.setRoute(route);
		leg.setDepartureTime(depTime);
		leg.setTravelTime(travTime);
		leg.setArrivalTime(depTime + travTime);
		return travTime;
	}

}
