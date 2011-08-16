/* *********************************************************************** *
 * project: org.matsim.*
 * OTFAgentsListHandler.java
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

package org.matsim.vis.otfvis.handler;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.matsim.core.basic.v01.IdImpl;
import org.matsim.core.utils.geometry.CoordImpl;
import org.matsim.core.utils.misc.ByteBufferUtils;
import org.matsim.vis.otfvis.caching.SceneGraph;
import org.matsim.vis.otfvis.data.OTFDataReceiver;
import org.matsim.vis.otfvis.data.OTFDataSimpleAgentReceiver;
import org.matsim.vis.otfvis.data.OTFDataWriter;
import org.matsim.vis.otfvis.data.OTFServerQuadTree;
import org.matsim.vis.otfvis.interfaces.OTFDataReader;
import org.matsim.vis.snapshots.writers.AgentSnapshotInfo;
import org.matsim.vis.snapshots.writers.AgentSnapshotInfo.AgentState;
import org.matsim.vis.snapshots.writers.AgentSnapshotInfoFactory;

/**
 * OTFAgentsListHandler is responsible for the IO of the
 * agent's data in case of a mvi file converted from an events-file.
 *
 * @author david
 *
 */
public class OTFAgentsListHandler extends OTFDataReader {

	private Class<? extends OTFDataSimpleAgentReceiver> agentReceiverClass = null;

	private List<OTFDataSimpleAgentReceiver> agents = new LinkedList<OTFDataSimpleAgentReceiver>();

	static public class Writer extends OTFDataWriter<Collection<? extends AgentSnapshotInfo>> {

		private static final long serialVersionUID = -6368752578878835954L;

		@Override
		public void writeConstData(ByteBuffer out) throws IOException {
			
		}

		@Override
		public void writeDynData(ByteBuffer out) throws IOException {
			out.putInt(src.size());
			for (AgentSnapshotInfo pos : this.src) {
				writeAgent(pos, out);
			}
		}

		private void writeAgent(AgentSnapshotInfo agInfo, ByteBuffer out) {
			String id = agInfo.getId().toString();
			ByteBufferUtils.putString(out, id);
			Point2D.Double point = OTFServerQuadTree.transform(new CoordImpl(agInfo.getEasting(), agInfo.getNorthing()));
			out.putFloat((float) point.getX());
			out.putFloat((float) point.getY());
			out.putInt(agInfo.getAgentState().ordinal() ) ;
			out.putInt(agInfo.getUserDefined());
			out.putFloat((float)agInfo.getColorValueBetweenZeroAndOne());
		}

	}

	private static AgentState[] al = AgentState.values();

	private void readAgent(ByteBuffer in, SceneGraph graph) {
		String id = ByteBufferUtils.getString(in);
		float x = in.getFloat();
		float y = in.getFloat();
		int int1 = in.getInt() ;
		int int2 = in.getInt() ;
		float float1 = in.getFloat() ;
		AgentSnapshotInfo agInfo = AgentSnapshotInfoFactory.staticCreateAgentSnapshotInfo(new IdImpl(id), x, y, 0., 0.) ;
		agInfo.setAgentState( al[int1] ) ;
		agInfo.setUserDefined( int2 ) ;
		agInfo.setColorValueBetweenZeroAndOne( float1 ) ;
		try {
			OTFDataSimpleAgentReceiver drawer = (OTFDataSimpleAgentReceiver) graph.newInstanceOf(this.agentReceiverClass);
			drawer.setAgent( agInfo ) ;
			this.agents.add(drawer);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void readDynData(ByteBuffer in, SceneGraph graph) throws IOException {
		this.agents.clear();
		int count = in.getInt();
		for(int i= 0; i< count; i++) readAgent(in, graph);
	}

	@Override
	public void readConstData(ByteBuffer in) throws IOException {
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void connect(OTFDataReceiver receiver) {
		if (receiver instanceof OTFDataSimpleAgentReceiver) {
			this.agentReceiverClass = (Class<? extends OTFDataSimpleAgentReceiver>) receiver.getClass();
		}
	}

	@Override
	public void invalidate(SceneGraph graph) {
		for(OTFDataSimpleAgentReceiver agent : this.agents) {
			agent.invalidate(graph);
		}
	}

}
