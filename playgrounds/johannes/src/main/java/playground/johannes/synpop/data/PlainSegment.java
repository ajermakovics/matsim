/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2015 by the members listed in the COPYING,        *
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

package playground.johannes.synpop.data;

/**
 * @author johannes
 */
public class PlainSegment extends  PlainElement implements Segment{

    private Episode episode;

    @Override
    public Episode getEpisode() {
        return episode;
    }

    void setEpisode(Episode episode) {
        this.episode = episode;
    }

    public PlainSegment clone() {
        PlainSegment clone = new PlainSegment();

        for(String key : keys()) {
            clone.setAttribute(key, getAttribute(key));
        }

        return clone;
    }
}
