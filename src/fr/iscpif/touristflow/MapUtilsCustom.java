/*

Copyright Quentin Lobbé (2012)
Author : Quentin Lobbé <quentin.lobbe@gmail.com>
Contributor : Julian Bilcke

This file is a part of TouristsFlowInParis Project

Build with Unfloding ( Till Nagel, Felix Lange ) ( BSD )


This software is a computer program whose purpose is to manipulate, 
explore and visualize large graphs of flow through time and space .
This software is governed by the CeCILL license under French law and 
abiding by the rules of distribution of free software. You can use, 
modify and/ or redistribute the software under the terms of the CeCILL 
license as circulated by CEA, CNRS and INRIA at the following URL 
"http://www.cecill.info".


As a counterpart to the access to the source code and rights to copy, 
modify and redistribute granted by the license, users are provided only with a 
limited warranty and the software's author, the holder of the economic rights, 
and the successive licensors have only limited liability.


In this respect, the user's attention is drawn to the risks associated with loading, 
using, modifying and/or developing or reproducing the software by the user in light 
of its specific status of free software, that may mean that it is complicated to manipulate, 
and that also therefore means that it is reserved for developers and experienced professionals 
having in-depth computer knowledge. Users are therefore encouraged to load and test 
the software's suitability as regards their requirements in conditions enabling the security 
of their systems and/or data to be ensured and, more generally, to use and operate it in 
the same conditions as regards security.


The fact that you are presently reading this means that you have had knowledge of the CeCILL 
license and that you accept its terms.
 
 */


package fr.iscpif.touristflow;
import java.util.List;

import processing.core.PApplet;
import de.fhpotsdam.unfolding.Map;
import de.fhpotsdam.unfolding.events.EventDispatcher;
import de.fhpotsdam.unfolding.events.PanMapEvent;
import de.fhpotsdam.unfolding.events.ZoomMapEvent;
import de.fhpotsdam.unfolding.interactions.KeyboardHandler;
import de.fhpotsdam.unfolding.interactions.MouseHandler;
import de.fhpotsdam.unfolding.utils.MapUtils;

/**
 *
 * @ Till Nagel & Julian Bilcke & Quentin Lobbé
 */
public class MapUtilsCustom extends MapUtils {

        /**
         * Initializes default events, i.e. all given maps handle mouse and keyboard interactions. No
         * cross-listening between maps.
         * 
         * @param p
         *            The PApplet needed for mouse and key user interactions.
         * @param maps
         *            One or many maps.
         * @return The EventDispatcher to use for additional event handling.
         */

        public static EventDispatcher createDefaultEventDispatcherCustom(PApplet p, Map... maps) {
                EventDispatcher eventDispatcher = new EventDispatcher();

                MouseHandler mouseHandler = new MouseHandlerCustom(p, maps);
                KeyboardHandler keyboardHandler = new KeyboardHandler(p, maps);

                eventDispatcher.addBroadcaster(mouseHandler);
                eventDispatcher.addBroadcaster(keyboardHandler);

                for (Map map : maps) {
                        eventDispatcher.register(map, PanMapEvent.TYPE_PAN, map.getId());
                        eventDispatcher.register(map, ZoomMapEvent.TYPE_ZOOM, map.getId());
                }

                return eventDispatcher;
        }
}
