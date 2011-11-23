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
      public float div;
      public float debut;
      public float fin;

      public Stick ( float size, float x, float y, float label, float longueur, float debut, float fin, float div  ){
        this.size = size;
        this.y = y;
        this.div = div;
        this.label = label;
        this.labelCourant = x;
        this.longueur = longueur;
        this.debut = debut;
        this.fin = fin;
        this.longueur = longueur; 
        this.x = x + longueur*div;
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
        // ligne horizontale de base
        p.line(labelCourant, y + 5, labelCourant + longueur, y + 5);
        
        PFont font1 = p.createFont("DejaVuSans-ExtraLight-", 10);
        p.textFont(font1); 
        p.stroke(10);
        p.textAlign(PConstants.CENTER);
        // barre verticale selection
        p.line(x, y, x, y+ size);
        // ligne horizontale selection
        p.line(labelCourant, y + 5, x, y + 5);
        // barre verticale gauche
        p.line(labelCourant, y, labelCourant, y+size);
        // barre verticale droite
        p.line(labelCourant + longueur, y, labelCourant + longueur, y+size);
        p.stroke(10);
        p.text( debut, labelCourant, y + 20 );
        p.text(fin + "  ", labelCourant + longueur, y + 20); 
        p.stroke(255);
        drawlabel();
      }
      
      public void drawStep () {
        PApplet p = Application.session.getPApplet();
        p.stroke(255);
        p.strokeWeight(2);
        // ligne horizontale de base
        p.line(labelCourant, y + 5, labelCourant + longueur, y + 5);
        
        PFont font1 = p.createFont("DejaVuSans-ExtraLight-", 10);
        p.textFont(font1); 
        p.stroke(10);
        p.textAlign(PConstants.CENTER);
        // barre verticale selection
        p.line(x, y, x, y+ size);
        // ligne horizontale selection
        p.line(labelCourant, y + 5, x, y + 5);
        // barre verticale gauche
        p.line(labelCourant, y, labelCourant, y+size);
        // barre verticale droite
        p.line(labelCourant + longueur, y, labelCourant + longueur, y+size);
        p.stroke(10);
        p.text( (int)debut, labelCourant, y + 20 );
        p.text((int)fin + "  ", labelCourant + longueur, y + 20); 
        p.stroke(255);
        drawlabelStep();
      }

      public void drawlabel () {
        PApplet p = Application.session.getPApplet();
        p.textAlign(PConstants.CENTER);
        float temp = PApplet.map ( x - labelCourant, 0, longueur, debut, fin ); 
        p.text( temp, x, y - 3);
        curs = temp;
      }
      
      public void drawlabelStep () {
        PApplet p = Application.session.getPApplet();
        p.textAlign(PConstants.CENTER);
        float temp = PApplet.map ( x - labelCourant, 0, longueur, debut, fin ); 
        p.text( (int)temp, x, y - 3);
        curs = (int)temp;
      }

      public void dragged(float tempx, float tempy) {
        if( PApplet.dist(tempx, tempy, x, y + size / 2) < this.size) {
         setX(tempx);  
        }
      }
}
