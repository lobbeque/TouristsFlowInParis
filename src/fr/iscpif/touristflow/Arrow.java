/*

Copyright Quentin Lobbé (2012)
Author : Quentin Lobbé <quentin.lobbe@gmail.com>
Contributor : Julian Bilcke

This file is a part of TouristsFlowInParis Project

Build with Processing ( Ben Fry, Casey Reas ) ( GNU GPL )
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

import de.fhpotsdam.unfolding.geo.Location;
import processing.core.*;
import static java.lang.System.*;
/**
 *
 * @author Quentin Lobbé
 */
public class Arrow {

    // coordonnées de la source de la flèche
    public float x, y;
    // cordonnées de la cible de la flèche
    public float _x, _y;
    // taille en xi et en yi
    public float xi, yi;
    // taille de la flèche, fonction du niveau de zoom 
    public float size;
    // angle de la flèche 
    public float angle;
    // true = entrant , false = sortant
    boolean sens;
    // utilisé seulement dans le cadre d'une flèche simple
    float taille = 0;
    
    public float poids = 0;

    /*
     * Il existe 2 types de flêches :
     *    les flêches "classiques" ( celles des listes ArrowIN/OUT ) crées directement depuis les oursins ou tirées des csv préparés
     *    les flêches "simples", elles demandent moins de calculs à l'affichage et sont utilisées pour le champ de flèches
     */
    
    // flèches "classiques"
    public Arrow(float x, float y, float angle, float _x, float _y, float xi, float yi, float poids, boolean sens) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this._x = _x;
        this._y = _y;
        this.sens = sens;
        this.xi = xi;
        this.yi = yi;
        this.size = PApplet.map(poids,0,200, (float)0, (float)4);
    }
    
    // flèches "lissee"
    public Arrow(float x, float y, float taille, float angle, boolean sens) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.taille = taille;
        this.sens = sens;

    }
    
   /* public Arrow(float x, float y, float angle, float taille, boolean sens) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.taille = taille;
        this.sens = sens;
    }*/
    

    // cette fonction dessine, oriente et grossit la flèche "classique" 
    public void update() {
        Location l = new Location(x, y);
        float xy[] = Application.session.getMap().getScreenPositionFromLocation(l);
        PApplet p = Application.session.getPApplet();
        p.pushMatrix();
        p.translate(xy[0], xy[1]);
        p.rotate(angle);
        p.strokeWeight((float) 0.5);
        p.stroke(0);       
        p.scale(size);
        if (sens) {
            p.fill(16, 91, 136);
            p.beginShape();
            p.vertex(15, 1);
            p.vertex(15, -1);
            p.vertex(5, -1);
            p.vertex(5, -4);
            p.vertex(0, 0);
            p.vertex(5, 4);
            p.vertex(5, 1);
            p.endShape(PConstants.CLOSE);
        } else {
            p.fill(182, 92, 96);
            p.beginShape();
            p.vertex(0, 1);
            p.vertex(0, -1);
            p.vertex(-10, -1);
            p.vertex(-10, -4);
            p.vertex(-15, 0);
            p.vertex(-10, 4);
            p.vertex(-10, 1);
            p.endShape(PConstants.CLOSE);
        }
        p.popMatrix();
        p.noFill();
    }
    
    // cette fonction dessine, oriente et grossit la flèche "simple" 
    public void updateLight(){
        Location l = new Location(x, y);
        float xy[] = Application.session.getMap().getScreenPositionFromLocation(l);
        PApplet p = Application.session.getPApplet();
        p.pushMatrix();
        p.translate(xy[0], xy[1]);  
        p.strokeWeight((float) 0.5);
        p.stroke(0);
        float t = PApplet.map(taille, 0, 5, 0, Application.session.getArrowsMax());  
        p.scale(t);
        if (sens) {
            p.rotate(- angle + PConstants.PI);
            p.fill(16, 91, 136);
            p.beginShape();
            p.vertex(15, 1);
            p.vertex(15, -1);
            p.vertex(5, -1);
            p.vertex(5, -4);
            p.vertex(0, 0);
            p.vertex(5, 4);
            p.vertex(5, 1);
            p.endShape(PConstants.CLOSE);
        } else {
            p.rotate(- angle + 2*PConstants.PI);
            p.fill(182, 92, 96);
            p.beginShape();
            p.vertex(0, 1);
            p.vertex(0, -1);
            p.vertex(-10, -1);
            p.vertex(-10, -4);
            p.vertex(-15, 0);
            p.vertex(-10, 4);
            p.vertex(-10, 1);
            p.endShape(PConstants.CLOSE);
        }
        p.popMatrix();
        p.noFill();
    }
    
    // fonction de dessin pour les flèches de la légende
    public void updateLightBis(){
        PApplet p = Application.session.getPApplet();
        p.pushMatrix();
        p.translate(x, y);  
        p.rotate(angle);
        p.strokeWeight((float) 0.5);
        p.stroke(0);
        p.scale(taille);
        if (sens) {
            p.fill(16, 91, 136);
            p.beginShape();
            p.vertex(15, 1);
            p.vertex(15, -1);
            p.vertex(5, -1);
            p.vertex(5, -4);
            p.vertex(0, 0);
            p.vertex(5, 4);
            p.vertex(5, 1);
            p.endShape(PConstants.CLOSE);
        } else {
            p.fill(182, 92, 96);
            p.beginShape();
            p.vertex(0, 1);
            p.vertex(0, -1);
            p.vertex(-10, -1);
            p.vertex(-10, -4);
            p.vertex(-15, 0);
            p.vertex(-10, 4);
            p.vertex(-10, 1);
            p.endShape(PConstants.CLOSE);
        }
        p.popMatrix();
        p.noFill();
    }
    
    public float getSize(){
        return size;
    }
    
    public float getX(){
        return x;
    }
    
    public float getY(){
        return y;
    }
    
    public float getAngle(){
        return angle;
    }
    
    public float get_X(){
        return _x;
    }
    
    public float get_Y(){
        return _y;
    }
    
    public boolean getSens(){
        return sens;
    }

    public float getXi() {
        return xi;
    }

    public float getYi() {
        return yi;
    }

    public float getPoids() {
        return poids;
    }
    
    public float getTaille() {
        return taille;
    }
    
    
   
}
