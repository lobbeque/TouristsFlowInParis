/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.iscpif.touristflow;

import de.fhpotsdam.unfolding.geo.Location;
import processing.core.*;
import static java.lang.System.*;
/**
 *
 * @author guest
 */
public class Arrow {

    // coordonnées de la source de la flèche
    public float x, y;
    // cordonnées de la cible de la flèche
    public float _x, _y;
    // taille de la flèche, fonction du niveau de zoom 
    public float size;
    // angle de la flèche 
    public float angle;
    // true = entrant , false = sortant
    boolean sens;
    // utilisé seulement dans le cadre d'une flèche simple
    float l = 0;

    /*
     * Il existe 2 types de flêches :
     *    les flêches "classiques" ( celles des listes ArrowIN/OUT ) crées directement depuis les oursins ou tirées des csv préparés
     *    les flêches "simples", elles demandent moins de calculs à l'affichage et sont utilisées pour le champ de flèches
     */
    
    // flèches "classiques"
    public Arrow(float x, float y, float angle, float _x, float _y, boolean sens) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this._x = _x;
        this._y = _y;
        this.sens = sens;
        calculeSize();
    }
    
    // flèches "simples"
    public Arrow(float x, float y, float angle, float l, boolean sens) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.l = l;
        this.sens = sens;
    }
    
    // cette fonction calcule à la volée le coef de grossissement des flêches classiques en fonction du niveau de zoom
    public void calculeSize() {
        Location l = new Location(x, y);
        float xy[] = Application.session.getMap().getScreenPositionFromLocation(l);
        Location l1 = new Location(_x, _y);
        float xy1[] = Application.session.getMap().getScreenPositionFromLocation(l1);
        PApplet p = Application.session.getPApplet();
        size = PApplet.dist(xy1[0], xy1[1], xy[0], xy[1]);
        size = PApplet.map(size, 0, 5, 0, 5);
    }

    // cette fonction dessine, oriente et grossit la flèche "classique" 
    public void update() {
        calculeSize();
        Location l = new Location(x, y);
        float xy[] = Application.session.getMap().getScreenPositionFromLocation(l);
        Location l1 = new Location(_x, _y);
        float xy1[] = Application.session.getMap().getScreenPositionFromLocation(l1);
        PApplet p = Application.session.getPApplet();
        //size = PApplet.dist(t1, t2, xy[0], xy[1]);
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
        PApplet p = Application.session.getPApplet();
        p.pushMatrix();
        p.translate(x, y);  
        p.rotate(angle);
        p.strokeWeight((float) 0.5);
        p.stroke(0);
        l = p.map(l, 0, 5, 0, Application.session.getArrowsMax());
       
        p.scale(l);
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
    
    // fonction de dessin pour les flèches de la légende
    public void updateLightBis(){
        PApplet p = Application.session.getPApplet();
        p.pushMatrix();
        p.translate(x, y);  
        p.rotate(angle);
        p.strokeWeight((float) 0.5);
        p.stroke(0);
        p.scale(l);
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
}
