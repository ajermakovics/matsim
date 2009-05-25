/* *********************************************************************** *
 * project: org.matsim.*
 * AllTests.java
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

package org.matsim.core.events;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for " + AllTests.class.getPackage().getName());

		suite.addTest(org.matsim.core.events.parallelEventsHandler.AllTests.suite());
		suite.addTestSuite(ActEndEventTest.class);
		suite.addTestSuite(ActStartEventTest.class);
		suite.addTestSuite(AgentArrivalEventTest.class);
		suite.addTestSuite(AgentDepartureEventTest.class);
		suite.addTestSuite(AgentMoneyEventTest.class);
		suite.addTestSuite(AgentStuckEventTest.class);
		suite.addTestSuite(AgentWait2LinkEventTest.class);
		suite.addTestSuite(BasicEventsHandlerTest.class);
		suite.addTestSuite(EventsHandlerHierarchyTest.class);
		suite.addTestSuite(EventsReadersTest.class);
		suite.addTestSuite(LinkEnterEventTest.class);
		suite.addTestSuite(LinkLeaveEventTest.class);
		suite.addTestSuite(PersonEntersVehicleEventTest.class);
		suite.addTestSuite(PersonLeavesVehicleEventTest.class);

		return suite;
	}

}
