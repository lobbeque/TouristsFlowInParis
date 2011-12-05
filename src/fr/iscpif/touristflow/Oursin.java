/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.iscpif.touristflow;

import static java.lang.System.*;
import de.fhpotsdam.unfolding.geo.Location;
import processing.core.*;

/**
 *
 * @author Quentin lobbé
 */
public class Oursin {

    // les coordonnées du centre de l'oursin dans le plan
    public float x;
    public float y;
    // les coordonnées du centre de l'oursin dans l'espace géographique ( ne changent pas )
    public float xNative;
    public float yNative;
    public float[] branchesSortantes;
    public float[] branchesEntrantes;
    private String statusNormal = "normal"; // 2 status possibles pour un oursin
    private String statusSelected = "selected";
    public String status = statusNormal;
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
    Oursin(float[] pointsCardinauxEntrant, float[] pointsCardinauxSortant, float x, float y, float xN, float yN) {
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

    }

    public void draw() {
        PApplet p = Application.session.getPApplet();
        Location l = new Location(xNative, yNative);
        float xy[] = Application.session.getMap().getScreenPositionFromLocation(l);
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
        //float rayon1 = PApplet.map(PApplet.log(PApplet.max(somEntrant, somSortant)), 0, PApplet.log(Application.session.getNodeMax()), 0, 15);
        //float rayon2 = PApplet.map(PApplet.log(PApplet.min(somEntrant, somSortant)), 0, PApplet.log(Application.session.getNodeMax()), 0, 15);
        float rayon1 = PApplet.map(PApplet.max(somEntrant, somSortant), 0, 200, 0, (float) (p.width / 46.6730));
        //float rayon2 = PApplet.map(PApplet.min(somEntrant, somSortant), 0, 200, 0, (float) (p.width / 46.6730));
        p.strokeWeight(2);
        if (PApplet.max(somEntrant, somSortant) == somEntrant) {
            p.fill(16, 91, 136);
            p.stroke(10);
            if (status.equals(statusNormal)) {
                p.ellipse(xy[0], xy[1], rayon1, rayon1);
            }
            p.noStroke();
            //p.fill(182, 92, 96);
            //p.ellipse(xy[0], xy[1], rayon2, rayon2);
        } else {
            p.fill(182, 92, 96);
            p.stroke(10);
            if (status.equals(statusNormal)) {
                p.ellipse(xy[0], xy[1], rayon1, rayon1);
            }
            p.noStroke();
            //p.fill(16, 91, 136);
            //p.ellipse(xy[0], xy[1], rayon2, rayon2);
        }
        p.ellipseMode(PApplet.CENTER);
        p.fill(0);

        p.strokeWeight(2);
        p.stroke(0);
        p.stroke(16, 91, 136);
        p.line(xy[0], xy[1], xMoyEntrant / nEntrant + xy[0], yMoyEntrant / nEntrant + xy[1]);
        p.stroke(182, 92, 96);
        p.line(xy[0], xy[1], xMoySortant / nSortant + xy[0], yMoySortant / nSortant + xy[1]);
        p.stroke(0);
        p.stroke(16, 91, 136);
        drawVectMoy(xy[0], xy[1], xMoyEntrant / nEntrant + xy[0], yMoyEntrant / nEntrant + xy[1]);
        p.stroke(182, 92, 96);
        drawVectMoy(xy[0], xy[1], xMoySortant / nSortant + xy[0], yMoySortant / nSortant + xy[1]);
        p.stroke(0);

    }

    public void drawVectMoy(float x1, float y1, float x2, float y2) {
        PApplet p = Application.session.getPApplet();
        float a = PApplet.atan2(y2 - y1, x2 - x1);
        a = -a;
        if (a < 0) {
            a = 2 * PConstants.PI + a;
        }
        float d = PApplet.dist(x2, y2, x1, y1);
        d = PApplet.map(d, 0, 10, 0, 100);
        float x = d * PApplet.cos(-a);
        float y = d * PApplet.sin(-a);

        p.line(x1, y1, x + x1, y + y1);
    }

    public void drawArc(float angle, float poids, float rayon, boolean sens) {
        PApplet p = Application.session.getPApplet();
        Location l = new Location(xNative, yNative);
        float xy[] = Application.session.getMap().getScreenPositionFromLocation(l);
        if ((poids != 0) || (rayon != 0)) {
            p.smooth();
            p.ellipseMode(PApplet.RADIUS);
            float x1 = (Bibliotheque.meter2Pixel(rayon) - poids) * PApplet.cos(angle) + xy[0];
            float y1 = (Bibliotheque.meter2Pixel(rayon) - poids) * PApplet.sin(angle) + xy[1];
            //float x2 = Bibliotheque.meter2Pixel(rayon) * PApplet.cos(angle) + xy[0];
            //float y2 = Bibliotheque.meter2Pixel(rayon) * PApplet.sin(angle) + xy[1];

            if (sens) {
                xMoyEntrant = xMoyEntrant + Bibliotheque.meter2Pixel(rayon) * PApplet.cos(angle);
                yMoyEntrant = yMoyEntrant + Bibliotheque.meter2Pixel(rayon) * PApplet.sin(angle);
            } else {
                xMoySortant = xMoySortant + Bibliotheque.meter2Pixel(rayon) * PApplet.cos(angle);
                yMoySortant = yMoySortant + Bibliotheque.meter2Pixel(rayon) * PApplet.sin(angle);
            }

            if (poids <= 0) {
                p.strokeWeight((float) 0.5);
            } else {
                p.strokeWeight(poids);
            }
            if (status.equals(statusNormal)) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String stat) {
        this.status = stat;
    }

    public void pressed() {
        if (status.equals(statusNormal)) { // si le status est normal alors celui devient selected
            status = statusSelected;
        } else if (status.equals(statusSelected)) { // si le status est selected alors celui ci devient normal 
            status = statusNormal;
        }
    }
}
