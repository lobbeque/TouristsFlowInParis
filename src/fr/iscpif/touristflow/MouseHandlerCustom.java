/*

Copyright Quentin Lobbé (2012)

Authors : 
Quentin Lobbé <quentin.lobbe@gmail.com>
Julie Fen-Chong <julie.fenchong@gmail.com>
Julian Bilcke <julian.bilcke@iscpif.fr>

This file is a part of TouristsFlowInParis Project

Build with Processing ( Ben Fry, Casey Reas ) ( GNU GPL )
Build with Unfolding ( Till Nagel, Felix Lange ) ( BSD )


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

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import processing.core.PApplet;
import processing.core.PConstants;
import de.fhpotsdam.unfolding.Map;
import de.fhpotsdam.unfolding.events.MapEventBroadcaster;
import de.fhpotsdam.unfolding.events.PanMapEvent;
import de.fhpotsdam.unfolding.events.ZoomMapEvent;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.interactions.MouseHandler;

/**
 *
 * @ Till Nagel & Julian Bilcke & Quentin Lobbé
 */

public class MouseHandlerCustom extends MouseHandler {
    
        public static float width = 0;
        public static float height = 0;

        public static Logger log = Logger.getLogger(MouseHandler.class);


        public MouseHandlerCustom(PApplet p, Map... maps) {
                this(p, Arrays.asList(maps));
        }

        public MouseHandlerCustom(PApplet p, List<Map> maps) {
                super(p, maps);

                p.registerMouseEvent(this);

                p.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
                        public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                                mouseWheel(evt.getWheelRotation());
                        }
                });
        }

        public void mouseClicked() {
                for (Map map : maps) {
                        if (map.isHit(mouseX, mouseY)) {
                                if (mouseEvent.getClickCount() == 2) {

                                        // FIXME Pan + Zoom does not work without tweening

                                        // Pan + Zoom (order is important)

                                        PanMapEvent panMapEvent = new PanMapEvent(this, map.getId());
                                        Location location = map.mapDisplay
                                                        .getLocationFromScreenPosition(mouseX, mouseY);
                                        panMapEvent.setToLocation(location);
                                        eventDispatcher.fireMapEvent(panMapEvent);

                                        ZoomMapEvent zoomMapEvent = new ZoomMapEvent(this, map.getId(),
                                                        ZoomMapEvent.ZOOM_BY, 1);
                                        zoomMapEvent.setTransformationCenterLocation(location);
                                        eventDispatcher.fireMapEvent(zoomMapEvent);
                                }
                        }
                }
        }

        public void mouseWheel(float delta) {
                for (Map map : maps) {
                        if (map.isHit(mouseX, mouseY)) {
                                //log.debug("mouse: fire zoomBy for " + map.getId());

                                ZoomMapEvent zoomMapEvent = new ZoomMapEvent(this, map.getId(),
                                                ZoomMapEvent.ZOOM_BY);

                                // Use location as zoom center, so listening maps can zoom correctly
                                Location location = map.mapDisplay.getLocationFromScreenPosition(mouseX, mouseY);
                                zoomMapEvent.setTransformationCenterLocation(location);

                                // Zoom in or out
                                if (delta < 0) {
                                        zoomMapEvent.setZoomLevelDelta(1);
                                } else if (delta > 0) {
                                        zoomMapEvent.setZoomLevelDelta(-1);
                                }

                                eventDispatcher.fireMapEvent(zoomMapEvent);
                        }
                }
        }

        public void mouseDragged() {
            
            if (App.db.isHeat() && mouseX > width/56 && mouseX < (width/56 + 320) && mouseY > height - 150 && mouseY < height - 150 + 100){
                return;
            }
            
            if ((App.db.isBoxCox() || App.db.isBoxCoxNode()) && mouseX > width/56 + 175 && mouseX < width/56 + 175 + 175 && mouseY > height - 320 && mouseY < height - 320 + 100){
                return;
            } 
            
            if ( App.db.isUrchin() && mouseX > width - 250 && mouseX < width - 250 + 220 && mouseY > height/18 && mouseY < height/18 + 50 ) {
                return;
            }
            
            if ( App.db.isArrow() && mouseX > width/70 && mouseX < width/70 + 120 && mouseY > height - 500 && mouseY < height - 500 + 300 ){
                return;
            }

                for (Map map : maps) {
                        if (map.isHit(mouseX, mouseY)) {
                                //log.debug("mouse: fire panTo for " + map.getId());

                                // Pan between two locations, so other listening maps can pan correctly

                                Location oldLocation = map.mapDisplay.getLocationFromScreenPosition(pmouseX,
                                                pmouseY);
                                Location newLocation = map.mapDisplay.getLocationFromScreenPosition(mouseX, mouseY);

                                PanMapEvent panMapEvent = new PanMapEvent(this, map.getId(), PanMapEvent.PAN_BY);
                                panMapEvent.setFromLocation(oldLocation);
                                panMapEvent.setToLocation(newLocation);
                                eventDispatcher.fireMapEvent(panMapEvent);
                        }
                }
        }

        public void mouseMoved() {
        }
        
        public static void setWidth(float a){
            width = a;
        }
        
        public static void setHeight(float a){
            height = a;
        }

        // --------------------------------------------------------------
        // Shamelessly copied code from Processing PApplet. No other way to hook into
        // register Processing mouse event and still have the same functionality with pmouseX, etc.
        // --------------------------------------------------------------

        private int mouseX;
        private int mouseY;
        private int pmouseX, pmouseY;
        private int dmouseX, dmouseY;
        private int emouseX, emouseY;
        private boolean firstMouse;
        private int mouseButton;
        private boolean mousePressed;
        private MouseEvent mouseEvent;

        public void mouseEvent(MouseEvent event) {
                int id = event.getID();
                mouseEvent = event;

                if ((id == MouseEvent.MOUSE_DRAGGED) || (id == MouseEvent.MOUSE_MOVED)) {
                        pmouseX = emouseX;
                        pmouseY = emouseY;
                        mouseX = event.getX();
                        mouseY = event.getY();
                }

                int modifiers = event.getModifiers();
                if ((modifiers & InputEvent.BUTTON1_MASK) != 0) {
                        mouseButton = PConstants.LEFT;
                } else if ((modifiers & InputEvent.BUTTON2_MASK) != 0) {
                        mouseButton = PConstants.CENTER;
                } else if ((modifiers & InputEvent.BUTTON3_MASK) != 0) {
                        mouseButton = PConstants.RIGHT;
                }

                if (firstMouse) {
                        pmouseX = mouseX;
                        pmouseY = mouseY;
                        dmouseX = mouseX;
                        dmouseY = mouseY;
                        firstMouse = false;
                }

                switch (id) {
                case MouseEvent.MOUSE_PRESSED:
                        mousePressed = true;
                        // mousePressed();
                        break;
                case MouseEvent.MOUSE_RELEASED:
                        mousePressed = false;
                        // mouseReleased();
                        break;
                case MouseEvent.MOUSE_CLICKED:
                        mouseClicked();
                        break;
                case MouseEvent.MOUSE_DRAGGED:
                        mouseDragged();
                        break;
                case MouseEvent.MOUSE_MOVED:
                        mouseMoved();
                        break;
                }

                if ((id == MouseEvent.MOUSE_DRAGGED) || (id == MouseEvent.MOUSE_MOVED)) {
                        emouseX = mouseX;
                        emouseY = mouseY;
                }
        }
}
