/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
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

/**
 * 
 */
package playground.ikaddoura.noise2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.utils.misc.Time;
import org.matsim.vehicles.Vehicle;

/**
 * 
 * @author ikaddoura
 *
 */

public class NoiseTimeTracker implements LinkEnterEventHandler {

	private static final Logger log = Logger.getLogger(NoiseTimeTracker.class);
	
	private final NoiseContext noiseContext;
	private final String outputDirectory;
	private final EventsManager events;
	
	private final List<String> consideredActivityTypes = new ArrayList<String>();
	
	private boolean collectNoiseEvents = true;
	private List<NoiseEventCaused> noiseEventsCaused = new ArrayList<NoiseEventCaused>();
	private List<NoiseEventAffected> noiseEventsAffected = new ArrayList<NoiseEventAffected>();
	private double totalCausedNoiseCost = 0.;
	private double totalAffectedNoiseCost = 0.;
	
	// time interval specific information
	private final Map<Id<Link>,List<Id<Vehicle>>> linkId2enteringVehicleIds = new HashMap<Id<Link>, List<Id<Vehicle>>>();
	private final Map<Id<Link>, Integer> linkId2Cars = new HashMap<Id<Link>, Integer>();
	private final Map<Id<Link>, Integer> linkId2Hgv = new HashMap<Id<Link>, Integer>();
	private Map<Id<Link>, Double> linkId2damageCost = new HashMap<Id<Link>, Double>();
	private Map<Id<Link>, Double> linkId2damageCostPerCar = new HashMap<Id<Link>, Double>();
	private Map<Id<Link>, Double> linkId2damageCostPerHgv = new HashMap<Id<Link>, Double>();
		
	public NoiseTimeTracker(NoiseContext noiseContext, EventsManager events, String outputDirectory) {
		this.noiseContext = noiseContext;
		this.outputDirectory = outputDirectory;
		this.events = events;
		
		String[] consideredActTypesArray = noiseContext.getNoiseParams().getConsideredActivities();
		for (int i = 0; i < consideredActTypesArray.length; i++) {
			this.consideredActivityTypes.add(consideredActTypesArray[i]);
		}
		
		if (this.consideredActivityTypes.size() == 0) {
			log.warn("Not considering any activity type for the noise damage computation.");
		}			
	}

	@Override
	public void reset(int iteration) {
				
		this.totalCausedNoiseCost = 0.;
		this.totalAffectedNoiseCost = 0.;
		this.noiseEventsCaused.clear();
		this.noiseEventsAffected.clear();
		
	}
	
	private void resetCurrentTimeIntervalInfo() {
		
		this.linkId2Cars.clear();
		this.linkId2Hgv.clear();
		this.linkId2enteringVehicleIds.clear();
		this.linkId2damageCost.clear();
		this.linkId2damageCostPerCar.clear();
		this.linkId2damageCostPerHgv.clear();
		
		for (ReceiverPoint rp : this.noiseContext.getReceiverPoints().values()) {
			rp.getLinkId2IsolatedImmission().clear();
			rp.setFinalImmission(0.);
			rp.getPersonId2actInfos().clear();
			rp.setDamageCosts(0.);
			rp.setDamageCostsPerAffectedAgentUnit(0.);
		}
	}
	
	private void checkTime(double time) {
		
		if (this.noiseContext.getCurrentTimeBinEndTime() <= 0.) {
			updateCurrentTimeInterval(time);
			log.info("The first time interval is set to " + this.noiseContext.getCurrentTimeBinEndTime());
		}
		
		if (time > this.noiseContext.getCurrentTimeBinEndTime()) {
			// All events of the current time bin are processed.

			log.info("##############################################");
			log.info("# Computing noise for time interval " + Time.writeTime(this.noiseContext.getCurrentTimeBinEndTime(), Time.TIMEFORMAT_HHMMSS) + " #");
			log.info("##############################################");

			updateActivityInformation();
			computeCurrentTimeInterval();
			updateCurrentTimeInterval(time);
			resetCurrentTimeIntervalInfo();
		}
	}
	
	private void updateActivityInformation() {
		for (ReceiverPoint rp : this.noiseContext.getReceiverPoints().values()) {
			
			for (Id<Person> personId : rp.getPersonId2actInfos().keySet()) {
				List<Integer> removeActivityInfo = new ArrayList<Integer>();

				int actInfoIndex = 0;
				for (PersonActivityInfo actInfo : rp.getPersonId2actInfos().get(personId)) {
					
					if (actInfo.getEndTime() < (this.noiseContext.getCurrentTimeBinEndTime() - this.noiseContext.getNoiseParams().getTimeBinSizeNoiseComputation())) {
						removeActivityInfo.add(actInfoIndex);
					}
					actInfoIndex++;
				}
				
				if (!removeActivityInfo.isEmpty()) {
					for (Integer i : removeActivityInfo) {
						rp.getPersonId2actInfos().get(personId).remove((int) i);
					}
				}
			}				
		}
	}

	private void computeCurrentTimeInterval() {
		
		log.info("Calculating noise emissions...");
		Map<Id<Link>, Double> emissions = calculateNoiseEmission();
		NoiseWriter.writeNoiseEmissionStatsPerHour(emissions, this.linkId2Cars, this.linkId2Hgv, this.noiseContext, outputDirectory, this.noiseContext.getCurrentTimeBinEndTime());
		log.info("Calculating noise emissions... Done.");
		
		log.info("Calculating noise immissions...");
		calculateNoiseImmission(emissions);
		NoiseWriter.writeNoiseImmissionStatsPerHour(noiseContext, outputDirectory, noiseContext.getCurrentTimeBinEndTime());
		log.info("Calculating noise immissions... Done.");
				
		NoiseWriter.writePersonActivityInfoPerHour(noiseContext, outputDirectory, noiseContext.getCurrentTimeBinEndTime());
		
		log.info("Calculating noise damage costs and throwing noise events...");
		calculateNoiseDamageCosts();
		NoiseWriter.writeDamageInfoPerHour(noiseContext, outputDirectory, noiseContext.getCurrentTimeBinEndTime());
		log.info("Calculating noise damage costs and throwing noise events... Done.");
	
	}
	
	private void updateCurrentTimeInterval(double time) {
		for (int i = 0; i < 30 ; i++) {
			double timeIntervalEnd = (i+1) * noiseContext.getNoiseParams().getTimeBinSizeNoiseComputation();
			if ((timeIntervalEnd - time) <= noiseContext.getNoiseParams().getTimeBinSizeNoiseComputation()) {
				this.noiseContext.setCurrentTimeBinEndTime(timeIntervalEnd);
			}
		}
	}
	
	@Override
	public void handleEvent(LinkEnterEvent event) {
		
		checkTime(event.getTime());
		
		if (!(noiseContext.getScenario().getPopulation().getPersons().containsKey(event.getVehicleId()))) {
			// probably public transit
			
		} else {
		
			if (event.getVehicleId().toString().startsWith(this.noiseContext.getNoiseParams().getHgvIdPrefix())) {
				// HGV
				
				if (linkId2Hgv.containsKey(event.getLinkId())) {
					int hgv = this.linkId2Hgv.get(event.getLinkId());
					hgv++;
					this.linkId2Hgv.put(event.getLinkId(), hgv); // TODO: remove this line?!
					
				} else {
					linkId2Hgv.put(event.getLinkId(), 1);
				}
				
			} else {
				// car
				
				if (linkId2Cars.containsKey(event.getLinkId())) {
					int cars = this.linkId2Cars.get(event.getLinkId());
					cars++;
					linkId2Cars.put(event.getLinkId(), cars); // TODO: remove this line?!
					
				} else {
					linkId2Cars.put(event.getLinkId(), 1);
				}
			}
			
			// for all vehicle types
			if (linkId2enteringVehicleIds.containsKey(event.getLinkId())) {
				List<Id<Vehicle>> listTmp = linkId2enteringVehicleIds.get(event.getLinkId());
				listTmp.add(event.getVehicleId());
				linkId2enteringVehicleIds.put(event.getLinkId(), listTmp);
				
			} else {
				List<Id<Vehicle>> listTmp = new ArrayList<Id<Vehicle>>();
				listTmp.add(event.getVehicleId());
				linkId2enteringVehicleIds.put(event.getLinkId(), listTmp);
			}
		}
		
	}

	

	private void calculateNoiseDamageCosts() {
		
		log.info("Calculating noise exposure costs for each receiver point...");
		calculateDamagePerReceiverPoint();
		log.info("Calculating noise exposure costs for each receiver point... Done.");

		log.info("Allocating the total exposure cost (per receiver point) to the relevant links...");
		calculateCostSharesPerLinkPerTimeInterval();
		log.info("Allocating the total exposure cost (per receiver point) to the relevant links... Done.");
		
		log.info("Allocating the exposure cost per link to the vehicle categories and vehicles...");
		calculateCostsPerVehiclePerLinkPerTimeInterval();
		log.info("Allocating the exposure cost per link to the vehicle categories and vehicles... Done.");
		
		log.info("Throwing noise events (caused)...");
		throwNoiseEventsCaused();
		log.info("Throwing noise events (caused)... Done.");

		log.info("Throwing noise events (affected)...");
		throwNoiseEventsAffected();
		log.info("Throwing noise events (affected)... Done.");
		
	}
	
	private void calculateDamagePerReceiverPoint() {
		
		for (ReceiverPoint rp : this.noiseContext.getReceiverPoints().values()) {
				
			double noiseImmission = rp.getFinalImmission();
				
			double affectedAgentUnits = 0.;
			if (!(rp.getPersonId2actInfos().isEmpty())) {
				
				for (Id<Person> personId : rp.getPersonId2actInfos().keySet()) {
					
					for (PersonActivityInfo actInfo : rp.getPersonId2actInfos().get(personId)) {
						double unitsThisPersonActivityInfo = actInfo.getDurationWithinInterval(this.noiseContext.getCurrentTimeBinEndTime(), this.noiseContext.getNoiseParams().getTimeBinSizeNoiseComputation()) / this.noiseContext.getNoiseParams().getTimeBinSizeNoiseComputation();
						affectedAgentUnits = affectedAgentUnits + unitsThisPersonActivityInfo;
					}
				}
			}	
			
			double damageCost = NoiseEquations.calculateDamageCosts(noiseImmission, affectedAgentUnits, this.noiseContext.getCurrentTimeBinEndTime(), this.noiseContext.getNoiseParams().getAnnualCostRate(), this.noiseContext.getNoiseParams().getTimeBinSizeNoiseComputation());
			double damageCostPerAffectedAgentUnit = NoiseEquations.calculateDamageCosts(noiseImmission, 1., this.noiseContext.getCurrentTimeBinEndTime(), this.noiseContext.getNoiseParams().getAnnualCostRate(), this.noiseContext.getNoiseParams().getTimeBinSizeNoiseComputation());
				
			rp.setDamageCosts(damageCost);
			rp.setDamageCostsPerAffectedAgentUnit(damageCostPerAffectedAgentUnit);
		}
	}

	private void calculateCostSharesPerLinkPerTimeInterval() {
		
		Map<Id<ReceiverPoint>, Map<Id<Link>, Double>> rpId2linkId2costShare = new HashMap<Id<ReceiverPoint>, Map<Id<Link>,Double>>();

		for (ReceiverPoint rp : this.noiseContext.getReceiverPoints().values()) {
										
			Map<Id<Link>,Double> linkId2costShare = new HashMap<Id<Link>, Double>();
			
			double resultingNoiseImmission = rp.getFinalImmission();
				
			if (rp.getDamageCosts() != 0.) {
				for (Id<Link> linkId : rp.getLinkId2IsolatedImmission().keySet()) {
						
						double noiseImmission = rp.getLinkId2IsolatedImmission().get(linkId);
						double costs = 0.;
						
						if (!(noiseImmission == 0.)) {
							double costShare = NoiseEquations.calculateShareOfResultingNoiseImmission(noiseImmission, resultingNoiseImmission);
							costs = costShare * rp.getDamageCosts();	
						}
						linkId2costShare.put(linkId, costs);
					}
				}
			
			rpId2linkId2costShare.put(rp.getId(), linkId2costShare);
		}
		
		// summing up the link-based costs
		for (ReceiverPoint rp : this.noiseContext.getReceiverPoints().values()) {

			for (Id<Link> linkId : this.noiseContext.getReceiverPoints().get(rp.getId()).getLinkId2distanceCorrection().keySet()) {
		
				if(rp.getDamageCosts() != 0.) {
					
					if (linkId2damageCost.containsKey(linkId)) {
						double sum = linkId2damageCost.get(linkId) + rpId2linkId2costShare.get(rp.getId()).get(linkId);
						linkId2damageCost.put(linkId, sum);
					} else {
						linkId2damageCost.put(linkId, rpId2linkId2costShare.get(rp.getId()).get(linkId));
					}
				}
				
			}
		}
	}

	private void calculateCostsPerVehiclePerLinkPerTimeInterval() {
		
		for (Id<Link> linkId : this.noiseContext.getScenario().getNetwork().getLinks().keySet()) {
			
			double damageCostPerCar = 0.;
			double damageCostPerHgv = 0.;
			
			double damageCostSum = 0.;
				
			if (linkId2damageCost.containsKey(linkId)) {					
				damageCostSum = linkId2damageCost.get(linkId);
			}
				
			int nCar = 0;
			if (this.linkId2Cars.containsKey(linkId)) {
				nCar = this.linkId2Cars.get(linkId);
			}
			
			int nHdv = 0;
			if (this.linkId2Hgv.containsKey(linkId)) {
				nHdv = this.linkId2Hgv.get(linkId);
			}
			
			double vCar = (this.noiseContext.getScenario().getNetwork().getLinks().get(linkId).getFreespeed()) * 3.6;
			double vHdv = vCar;
				
			// If different speeds for different vehicle types have to be considered, adapt the calculation here.
			// For example, a maximum speed for hdv-vehicles could be set here (for instance for German highways) 
				
			double lCar = NoiseEquations.calculateLCar(vCar);
			double lHdv = NoiseEquations.calculateLHdv(vHdv);
				
			double shareCar = 0.;
			double shareHdv = 0.;
				
			if ((nCar > 0) || (nHdv > 0)) {
				shareCar = NoiseEquations.calculateShare(nCar, lCar, nHdv, lHdv);
				shareHdv = NoiseEquations.calculateShare(nHdv, lHdv, nCar, lCar);
			}
			
			double damageCostSumCar = shareCar * damageCostSum;
			double damageCostSumHdv = shareHdv * damageCostSum;
				
			if (!(nCar == 0)) {
				damageCostPerCar = damageCostSumCar/nCar;
			}
				
			if (!(nHdv == 0)) {
				damageCostPerHgv = damageCostSumHdv/nHdv;
			}
			
			linkId2damageCostPerCar.put(linkId, damageCostPerCar);
			linkId2damageCostPerHgv.put(linkId, damageCostPerHgv);
			
		}
	}

	private void throwNoiseEventsCaused() {
		
		for (Id<Link> linkId : this.noiseContext.getScenario().getNetwork().getLinks().keySet()) {
			double amountCar = (linkId2damageCostPerCar.get(linkId)) / (this.noiseContext.getNoiseParams().getScaleFactor());
			double amountHdv = (linkId2damageCostPerHgv.get(linkId)) / (this.noiseContext.getNoiseParams().getScaleFactor());
								
			if (this.linkId2enteringVehicleIds.containsKey(linkId)){
				for(Id<Vehicle> id : this.linkId2enteringVehicleIds.get(linkId)) {
					
					double amount = 0.;
					boolean isHdv = false;
					
					if(!(id.toString().startsWith(this.noiseContext.getNoiseParams().getHgvIdPrefix()))) {
						amount = amountCar;
					} else {
						amount = amountHdv;
						isHdv = true;
					}
					
					if (amount != 0.) {
						NoiseVehicleType carOrHdv = NoiseVehicleType.car;
						if (isHdv == true) {
							carOrHdv = NoiseVehicleType.hgv;
						}
						
						// The person Id is assumed to be equal to the vehicle Id.
						NoiseEventCaused noiseEvent = new NoiseEventCaused(this.noiseContext.getCurrentTimeBinEndTime(), Id.create(id, Person.class), id, amount, linkId, carOrHdv);
						events.processEvent(noiseEvent);
						
						if (this.collectNoiseEvents) {
							this.noiseEventsCaused.add(noiseEvent);
						}
						
						totalCausedNoiseCost = totalCausedNoiseCost + amount;
					}
				}
			}
		}
	}
	
	private void throwNoiseEventsAffected() {
		
		for (ReceiverPoint rp : this.noiseContext.getReceiverPoints().values()) {
			
			if (!(rp.getPersonId2actInfos().isEmpty())) {
				
				for (Id<Person> personId : rp.getPersonId2actInfos().keySet()) {
					
					for (PersonActivityInfo actInfo : rp.getPersonId2actInfos().get(personId)) {
						
						double factor = actInfo.getDurationWithinInterval(this.noiseContext.getCurrentTimeBinEndTime(), this.noiseContext.getNoiseParams().getTimeBinSizeNoiseComputation()) / this.noiseContext.getNoiseParams().getTimeBinSizeNoiseComputation();
						double amount = factor * rp.getDamageCostsPerAffectedAgentUnit();
						
						if (amount != 0.) {
							NoiseEventAffected noiseEventAffected = new NoiseEventAffected(this.noiseContext.getCurrentTimeBinEndTime(), personId, amount, rp.getId(), actInfo.getActivityType());
							events.processEvent(noiseEventAffected);
							
							if (this.collectNoiseEvents) {
								this.noiseEventsAffected.add(noiseEventAffected);
							}
							
							totalAffectedNoiseCost = totalAffectedNoiseCost + amount;
						}				
					}
				}
			}	
		}
	}

	private void calculateNoiseImmission(Map<Id<Link>, Double> emissions) {
		
		for (ReceiverPoint rp : this.noiseContext.getReceiverPoints().values()) {
					
			Map<Id<Link>, Double> linkId2isolatedImmission = new HashMap<Id<Link>, Double>();
			
			for(Id<Link> linkId : rp.getLinkId2distanceCorrection().keySet()) {
				if (this.noiseContext.getNoiseParams().getTunnelLinkIDs().contains(linkId)) {
					linkId2isolatedImmission.put(linkId, 0.);
			 			
			 	} else {
					double noiseImmission = 0.;
					if (!(emissions.get(linkId) == 0.)) {
						noiseImmission = emissions.get(linkId)
								+ this.noiseContext.getReceiverPoints().get(rp.getId()).getLinkId2distanceCorrection().get(linkId)
								+ this.noiseContext.getReceiverPoints().get(rp.getId()).getLinkId2angleCorrection().get(linkId)
								;
						
						if (noiseImmission < 0.) {
							noiseImmission = 0.;
						}
					}
					linkId2isolatedImmission.put(linkId, noiseImmission);
			 	}
			}
			
			double finalNoiseImmission = 0.;
			if (!linkId2isolatedImmission.isEmpty()) {
				finalNoiseImmission = NoiseEquations.calculateResultingNoiseImmission(linkId2isolatedImmission.values());
			}
			
			rp.setFinalImmission(finalNoiseImmission);
			rp.setLinkId2IsolatedImmission(linkId2isolatedImmission);
		}
	}
	
	private Map<Id<Link>, Double> calculateNoiseEmission() {
		
		Map<Id<Link>, Double> linkID2emission = new HashMap<Id<Link>, Double>();
		
		for (Id<Link> linkId : this.noiseContext.getScenario().getNetwork().getLinks().keySet()){
			
			double vCar = (this.noiseContext.getScenario().getNetwork().getLinks().get(linkId).getFreespeed()) * 3.6;
			double vHdv = vCar;
			
			double noiseEmission = 0.;

			int n_car = 0;
			if (this.linkId2Cars.containsKey(linkId)) {
				n_car = this.linkId2Cars.get(linkId);
			}
			
			int n_hgv = 0;
			if (this.linkId2Hgv.containsKey(linkId)) {
				n_hgv = this.linkId2Hgv.get(linkId);
			}
			int n = n_car + n_hgv;
			double p = 0.;
				
			if(!(n == 0)) {
				p = n_hgv / ((double) n);
			}
				
			if(!(n == 0)) {
					
				// correction for a sample, multiplicate the scale factor
				n = (int) (n * (this.noiseContext.getNoiseParams().getScaleFactor()));
					
				// correction for intervals unequal to 3600 seconds (= one hour)
				n = (int) (n * (3600. / this.noiseContext.getNoiseParams().getTimeBinSizeNoiseComputation()));
					
				double mittelungspegel = NoiseEquations.calculateMittelungspegelLm(n, p);
				double Dv = NoiseEquations.calculateGeschwindigkeitskorrekturDv(vCar, vHdv, p);
				noiseEmission = mittelungspegel + Dv;					
			}	
			
			linkID2emission.put(linkId, noiseEmission);			
		}
		return linkID2emission;
	}
	
	public void computeFinalTimeInterval() {
		
		log.info("######################################################");
		log.info("# Computing noise for (final) time interval " + Time.writeTime(this.noiseContext.getCurrentTimeBinEndTime(), Time.TIMEFORMAT_HHMMSS) + " #");
		log.info("######################################################");
		
		updateActivityInformation();
		computeCurrentTimeInterval();
		resetCurrentTimeIntervalInfo();
	}

	public List<NoiseEventCaused> getNoiseEventsCaused() {
		return noiseEventsCaused;
	}

	public List<NoiseEventAffected> getNoiseEventsAffected() {
		return noiseEventsAffected;
	}

	public double getTotalCausedNoiseCost() {
		return totalCausedNoiseCost;
	}

	public double getTotalAffectedNoiseCost() {
		return totalAffectedNoiseCost;
	}
	
}
