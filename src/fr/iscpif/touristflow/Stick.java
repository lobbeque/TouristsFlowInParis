/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.iscpif.touristflow;
import processing.core.*; 
/**
 *
 * @author Quentin lobbé
 */
public class Stick {
      public float x;
      public float y;
      public float size;
      public float label;
      public float labelCourant;
      public float longueur;
      public float curs;

      public Stick ( float size, float x, float y, float label, float longueur ){
        this.size = size;
        this.x = x + longueur/3;
        this.y = y;
        this.label = label;
        this.labelCourant = x;
        this.longueur = longueur;
      }

      public void setX(float tempx){
        if ( tempx <= labelCourant ) {
          this.x = labelCourant;
        } else if ( tempx >= labelCourant + longueur ) {
          this.x = labelCourant + longueur;
        } else {
          this.x = tempx;
        }
      }

      public float getCurs(){
        return this.curs;
      }

      public void draw () {
        PApplet p = Application.session.getPApplet();
        p.stroke(255);
        p.strokeWeight(2);
        PFont font1 = p.createFont("DejaVuSans-ExtraLight-", 10);
        p.textFont(font1); 
        p.textAlign(PConstants.CENTER);
        p.line(x, y, x, y+ size);
        p.line(labelCourant, y + 5, x, y + 5);
        p.stroke(10, 150);
        p.line(labelCourant, y, labelCourant, y+size);
        p.text( 0, labelCourant, y + 20 );
        p.line(labelCourant + longueur, y, labelCourant + longueur, y+size);
        p.text(3*label + "  ", labelCourant + longueur, y + 20); 
        p.stroke(255);
        drawlabel();
      }

      public void drawlabel () {
        PApplet p = Application.session.getPApplet();
        p.textAlign(PConstants.CENTER);
        float temp = PApplet.map ( x - labelCourant, 0, longueur, 0, 3*label ); 
        p.text( temp, x, y - 3);
        curs = temp;
      }

      public void dragged(float tempx, float tempy) {
        if( PApplet.dist(tempx, tempy, x, y + size / 2) < this.size) {
         setX(tempx);  
        }
      }
}
