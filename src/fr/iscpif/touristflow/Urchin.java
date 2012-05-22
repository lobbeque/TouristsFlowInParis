/*

Copyright : UMR Géographie Cités - Quentin Lobbé (2012)

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

import static java.lang.System.*;
import de.fhpotsdam.unfolding.geo.Location;
import java.util.ArrayList;
import processing.core.*;

/**
 *
 * @author Quentin lobbé
 */
public class Urchin {

    Arrow arrow;
    // les coordonnées du centre de l'oursin dans le plan
    public float x;
    public float y;
    // les coordonnées du centre de l'oursin dans l'espace géographique ( ne changent pas )
    public float xNative;
    public float yNative;
    public float[] branchesSortantes;
    public float[] branchesEntrantes;
    public enum Status {
    UNSELECTED, SELECTED
    }
    public Status status = Status.UNSELECTED;
    public float somEntrant;
    public float somSortant;
    // Vecteur moyen
    public float xMoyEntrant;
    public float yMoyEntrant;
    public float xMoySortant;
    public float yMoySortant;
    public int nEntrant;
    public int nSortant;

    /*
    EstPoids, EstDist = 0, 1
    sudEstEstPoids, sudEstEstDist = 2, 3
    sudEstPoids, sudEstDist = 4, 5
    sudsudEstPoids, sudsudEstDist = 6, 7
    sudPoids, sudDist = 8, 9
    sudsudOuestPoids, sudsudOuestDist = 10, 11
    sudOuestPoids, sudOuestDist = 12, 13
    sudOuestOuestPoids, sudOuestOuestDist = 14, 15
    OuestPoids, OuestDist = 16, 17
    nordOuestOuestPoids, nordOuestOuestDist = 18, 19
    nordOuestPoids, nordOuestDist = 20, 21
    nordnordOuestPoids, nordnordOuestDist = 22, 23
    nordPoids, nordDist = 24, 25
    nordnordEstPoids, nordnordEstDist = 26, 27
    nordEstPoids, nordEstDist = 28, 29
    nordEstEstPoids, nordEstEstDist = 30, 31
     */
    // constructeur
    Urchin(float[] pointsCardinauxEntrant, float[] pointsCardinauxSortant, float x, float y, float xN, float yN) {
        branchesSortantes = new float[32];
        branchesEntrantes = new float[32];
        this.nEntrant = 0;
        this.nSortant = 0;

        for (int i = 0; i < 32; i++) {
            if (pointsCardinauxSortant[i] < 0) {
                branchesSortantes[i] = 0;
            } else {
                branchesSortantes[i] = pointsCardinauxSortant[i];
                nSortant++;
            }

            if (pointsCardinauxEntrant[i] < 0) {
                branchesEntrantes[i] = 0;
            } else {
                branchesEntrantes[i] = pointsCardinauxEntrant[i];
                nEntrant++;
            }
        }

        this.x = x;
        this.y = y;
        this.xNative = xN;
        this.yNative = yN;
        this.xMoyEntrant = 0;
        this.xMoySortant = 0;
        this.yMoyEntrant = 0;
        this.yMoySortant = 0;
        this.somEntrant = 0;
        this.somSortant = 0;

        int i = 0;
        while (i != 32) {

            this.somEntrant = this.somEntrant + branchesEntrantes[i];
            this.somSortant = this.somSortant + branchesSortantes[i];

            i = i + 2;

        }

        if (App.db.isArrow()) {
            this.status = Status.SELECTED;
        }
    }

    /*
     * draw() va dessiner chaque élément de l'oursin, les branches via drawArc(...) et le cercle central plus bas avec ellipse()
     * pour créer une flèche/vecteur moyen depuis un oursin il faut décommenter à partir de "if (Application.session.isArrow())"
     */
    public void draw() {
        PApplet p = App.db.getPApplet();
        Location l = new Location(xNative, yNative);
        float xy[] = App.db.getMap().getScreenPositionFromLocation(l);
        xMoyEntrant = 0;
        xMoySortant = 0;
        yMoyEntrant = 0;
        yMoySortant = 0;

        p.stroke(16, 91, 136, 220);
        p.fill(16, 91, 136, 220);
        for (int i = 0; i < 16; i++) {
            drawArc(i * PConstants.PI / 8 + PConstants.PI / 64, branchesEntrantes[i * 2], branchesEntrantes[i * 2 + 1], true);
        }
        p.stroke(182, 92, 96, 220);
        p.fill(182, 92, 96, 220);
        for (int i = 0; i < 16; i++) {
            drawArc(i * PConstants.PI / 8 - PConstants.PI / 64, branchesSortantes[i * 2], branchesSortantes[i * 2 + 1], false);
        }
        p.ellipseMode(PApplet.RADIUS);
        float rayon1 = PApplet.map(PApplet.max(somEntrant, somSortant), 0, 200, 0, (float) (p.width / 46.6730));
        p.strokeWeight(2);
        if (PApplet.max(somEntrant, somSortant) == somEntrant) {
            p.fill(16, 91, 136);
            p.stroke(10);
            //if (status.equals(statusNormal)) {
                //p.ellipse(xy[0], xy[1], 5, 5);
            //}
            p.noStroke();
        } else {
            p.fill(182, 92, 96);
            p.stroke(10);
            //if (status.equals(statusNormal)) {
                //p.ellipse(xy[0], xy[1], 5, 5);
            //}
            p.noStroke();
        }
        p.ellipseMode(PApplet.CENTER);
        p.fill(0);

        p.strokeWeight(2);

        // pour créer des flèches 
        if (App.db.isArrow()) {
            p.stroke(0);
            p.stroke(16, 91, 136);
            drawVectMoy(xy[0], xy[1], xMoyEntrant / nEntrant + xy[0], yMoyEntrant / nEntrant + xy[1], true);
            p.stroke(182, 92, 96);
            drawVectMoy(xy[0], xy[1], xMoySortant / nSortant + xy[0], yMoySortant / nSortant + xy[1], false);
        }

        p.stroke(0);

    }

    /*
     * Cette fonction transforme les infos entrantes/sortantes d'un oursin en flèche/vecteur_moyen entrante/sortante 
     * on a juste besoin des coordonnées ( en lat et lon ) du vecteur moyen (x1, y1, x2, y2) et de l'angle moyen
     * chaque nouvelle flèche est rangée dans la liste ArrowIN/ArrowOUT correspondante
     * c'est depuis ces listes que l'on accèdera alors aux flèches
     */
    public void drawVectMoy(float x1, float y1, float x2, float y2, boolean sens) {
        PApplet p = App.db.getPApplet();
        Location l = App.db.getMap().getLocationFromScreenPosition(x2, y2);
        float a = PApplet.atan2(y2 - y1, x2 - x1);
        
        float xi = Misc.distFrom(xNative, yNative, l.getLat(), yNative);
        float yi = Misc.distFrom(xNative, yNative, xNative, l.getLon());
        
        a = -a;
        if (a < 0) {
            a = 2 * PConstants.PI + a;
        }
        if (sens) {
            arrow = new Arrow(xNative, yNative, -a, l.getLat(), l.getLon(), xi, yi, somEntrant, sens);
            App.db.arrowsIN.add(arrow);

        } else {
            arrow = new Arrow(xNative, yNative, -a + PConstants.PI, l.getLat(), l.getLon(), xi, yi, somSortant, sens);
            App.db.arrowsOUT.add(arrow);
        }

        p.stroke(0);
    }

    /*
     * cette fonction dessine la branche de l'oursin voulue
     * au passage on complète les sommes x/yMoyEntrant/Sortant qui serviront à calculer le poids et l'angle moyen de l'oursin en vue de la création des flèches 
     */
    public void drawArc(float angle, float poids, float rayon, boolean sens) {
        PApplet p = App.db.getPApplet();
        Location l = new Location(xNative, yNative);
        float xy[] = App.db.getMap().getScreenPositionFromLocation(l);
        if ((poids != 0) || (rayon != 0)) {
            p.smooth();
            p.ellipseMode(PApplet.RADIUS);
            float x1 = (Misc.meter2Pixel(rayon) - poids) * PApplet.cos(angle) + xy[0];
            float y1 = (Misc.meter2Pixel(rayon) - poids) * PApplet.sin(angle) + xy[1];

            if (sens) {
                xMoyEntrant = xMoyEntrant + Misc.meter2Pixel(rayon) * PApplet.cos(angle);
                yMoyEntrant = yMoyEntrant + Misc.meter2Pixel(rayon) * PApplet.sin(angle);
            } else {
                xMoySortant = xMoySortant + Misc.meter2Pixel(rayon) * PApplet.cos(angle);
                yMoySortant = yMoySortant + Misc.meter2Pixel(rayon) * PApplet.sin(angle);
            }

            if (poids <= 0) {
                p.strokeWeight((float) 0.5);
            } else {
                p.strokeWeight(poids);
            }
            if (checkStatus(Status.UNSELECTED)) {
                p.line(xy[0], xy[1], x1, y1);
            }
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getYN() {
        return yNative;
    }

    public float getXN() {
        return xNative;
    }

    public float[] getEntrant() {
        return this.branchesEntrantes;
    }

    public float getEntrant(int i) {
        return this.branchesEntrantes[i];
    }

    public float[] getSortant() {
        return this.branchesSortantes;
    }

    public float getSortant(int i) {
        return this.branchesSortantes[i];
    }

    public Status getStatus() {
        return status;
    }
    public boolean checkStatus(Status st) {
        return status == st;
    }
    public void setStatus(Status stat) {
        this.status = stat;
    }
    public void setStatusSelected() {
        setStatus(Status.SELECTED); 
    }
    public void setStatusUnselected() {
        setStatus(Status.UNSELECTED); 
    }
    /*
     * afficher ou non un oursin
     */
    public void pressed() {
        if (checkStatus(Status.UNSELECTED)) { // si le status est normal alors celui devient selected
            setStatusSelected();
        } else { // si le status est selected alors celui ci devient normal 
            setStatusUnselected();
        }
    }

    public float getSomEntrant() {
        return somEntrant;
    }

    public float getSomSortant() {
        return somSortant;
    }
    
    
}
