/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * @author guest
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
