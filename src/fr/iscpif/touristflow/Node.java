/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.iscpif.touristflow;

import processing.core.*;
import de.fhpotsdam.unfolding.geo.Location;

/**
 *
 * @author Quentin lobbé
 */
public class Node {
    
    

    public static boolean hide = false;

    public static void afficheNode() {
        PApplet p = Application.session.getPApplet();
        float radius = 0;
        for (int i = (int) Application.session.getTableauGephiCount(Application.session.getIndex(), 0) - 1; i >= 0; i--) {
            p.noStroke();
            // Transformation des coordonnées de chaque noeud !!! Attention il faut toujours inverser la latitude et la longitude
            Location l = new Location(Application.session.getMatNode(0, i), Application.session.getMatNode(1, i));
            float xy[] = Application.session.getMap().getScreenPositionFromLocation(l);


            // Valuation de la taille du noeuds en fonction de son poids
            float degree = Application.session.getMatNode(2, i);


            if (!Application.session.isBoxCoxNode()) {
                radius = PApplet.map(degree, Application.session.getNodeMin(), Application.session.getNodeMax(), 1, 10);
            } else {
                radius = PApplet.map(Bibliotheque.CoxBox(degree, 'n'), Bibliotheque.CoxBox(Application.session.getNodeMin(), 'n'), Bibliotheque.CoxBox(Application.session.getNodeMax(), 'n'), 1, 10);
            }
            p.ellipseMode(PConstants.RADIUS);
            float zoom = Application.session.getMap().getZoom();
            if ((Application.session.isNode()) && (!Application.session.isChaud())) {
                //p.fill(182, 92, 96, 200);
                p.fill(38,155,225,200);
                if (!hide) {
                    p.ellipse(xy[0], xy[1], radius * PApplet.exp(zoom / 20000), radius * PApplet.exp(zoom / 20000));
                }
                p.fill(178, 206, 255, 200);

                p.ellipse(xy[0], xy[1], radius * PApplet.exp(zoom / 20000) / 5, radius * PApplet.exp(zoom / 20000) / 5);

            }

            //mode Heatmap
            if (Application.session.isChaud()) {
                HeatMap.drawHeatMap((int) xy[0], (int) xy[1], (int) radius, (int) degree);
            }

            float d = PApplet.dist(xy[0], xy[1], p.mouseX, p.mouseY);

            //mode sélection
            if ((d < radius + 2) && Application.session.isClicked() && (!Application.session.isSelect()) && (!Application.session.isOursin())) {
                Affichage.selection(xy[0], xy[1], radius, i);
            }

        }
    }

    public static boolean getHide() {
        return hide;
    }

    public static void setHide(boolean a) {
        hide = a;
    }
}
