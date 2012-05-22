/*

Copyright : UMR Géographie Cités - Quentin Lobbé (2012)

Authors : 
Quentin Lobbé <quentin.lobbe@gmail.com>
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
import de.fhpotsdam.unfolding.geo.Location;

/**
 *
 * @author Quentin lobbé
 */
public class HeatMap {
    
   public static void drawHeatMap( int x, int y, int radius, int degree ) {
      PApplet p = App.db.getPApplet();
      if ( ( x < p.width ) && ( y < p.height ) && ( x > 0 ) && ( y > 0 ) ) {
      App.db.getMyPoints().loadPixels();
      PImage buffer = App.db.getMyPoints(); // nous travaillons sur un buffer, une copie de l'image d'origine pour plus de souplesse
      buffer.loadPixels(); // afin de les modifier il faut charger tous les pixels de l'image
      float percent = PApplet.norm( degree, App.db.getNodeMin(), App.db.getNodeMax());
      //int from = p.color (189, 73, 50);
      //int to = p.color(255, 255, 255);
      int from = p.color (255, 255, 255);
      int to = p.color(189, 73, 50);
      int seuil = p.lerpColor(   from, to, percent ); // couleur entre le rouge et le blanc 
        for ( int i = 0; i < App.db.getMyPoints().width ; i++) {
          for ( int j = 0; j < App.db.getMyPoints().height; j++) {  
            float d = PApplet.dist ( App.db.getMyPoints().width/2, App.db.getMyPoints().height/2, i, j);   
            int k = i + ( j * App.db.getMyPoints().width );
            float percent2 = PApplet.norm( d, 0, App.db.getMyPoints().width/2);
            int c3 = p.lerpColor( seuil, from, percent2);

            //float[] hsb = java.awt.Color.RGBtoHSB((int)red(myPoint.pixels[i]), (int)green(myPoint.pixels[i]), (int)blue(myPoint.pixels[i]), null);
            //java.awt.Color rgb = java.awt.Color.getHSBColor(0.3, hsb[1],hsb[2]);
            buffer.pixels[k] = p.color( p.red(c3), p.green(c3), p.blue(c3), p.alpha(App.db.getMyPoints().pixels[k]));
          }
        }
        buffer.updatePixels();
        p.imageMode(PConstants.CENTER);
        float zoom = App.db.getMap().getZoom();
        p.image(buffer, x, y, radius*7*PApplet.exp(zoom/20000), radius*7*PApplet.exp(zoom/20000));
        p.imageMode(PConstants.CORNER);
      }
      
   }
   
      public int GetDegree(float x, float y){
       int Degree = 0;
       Location location = App.db.getMap().getLocationFromScreenPosition(x,y);
       for ( int i = 0; i < App.db.getMatEdge().length; i ++){
           if ((( location.getLat() == App.db.getMatEdge(0, i)) && ( location.getLon() == App.db.getMatEdge(1, i))) || (( location.getLat() == App.db.getMatEdge(2, i)) && ( location.getLon() == App.db.getMatEdge(3, i)))){
               Degree ++;
           }
           
       }
       return Degree;
      }
      
    }
    

