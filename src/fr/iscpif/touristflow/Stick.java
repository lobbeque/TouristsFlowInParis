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
        PApplet p = App.db.getPApplet();
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
        PApplet p = App.db.getPApplet();
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
        PApplet p = App.db.getPApplet();
        p.textAlign(PConstants.CENTER);
        float temp = PApplet.map ( x - labelCourant, 0, longueur, debut, fin ); 
        p.text( temp, x, y - 3);
        curs = temp;
      }
      
      public void drawlabelStep () {
        PApplet p = App.db.getPApplet();
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
