/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.iscpif.touristflow;
import processing.core.*; 
import de.fhpotsdam.unfolding.geo.Location;

/**
 *
 * @author guest
 */
public class HeatMap {
    
   public static void drawHeatMap( int x, int y, int radius, int poids ) {
      PApplet p = Application.session.getPApplet();
      if ( ( x < p.width ) && ( y < p.height ) && ( x > 0 ) && ( y > 0 ) ) {
      Application.session.getMyPoints().loadPixels();
      PImage buffer = Application.session.getMyPoints(); // nous travaillons sur un buffer, une copie de l'image d'origine pour plus de souplesse
      buffer.loadPixels(); // afin de les modifier il faut charger tous les pixels de l'image
      float percent = PApplet.norm( poids, Application.session.getNodeMin(), Application.session.getNodeMax());
      int from = p.color (189, 73, 50);
      int to = p.color(255, 255, 255);
      int c = p.lerpColor(   from, to, percent ); // couleur entre le rouge et le blanc 
        for ( int i = 0; i < Application.session.getMyPoints().width ; i++) {
          for ( int j = 0; j < Application.session.getMyPoints().height; j++) {  
            float d = PApplet.dist ( Application.session.getMyPoints().width/2, Application.session.getMyPoints().height/2, i, j);   
            int k = i + ( j * Application.session.getMyPoints().width );
            float percent2 = PApplet.norm( d, 0, Application.session.getMyPoints().width/2);
            int c3 = p.lerpColor(   c, from, percent2);

            //float[] hsb = java.awt.Color.RGBtoHSB((int)red(myPoint.pixels[i]), (int)green(myPoint.pixels[i]), (int)blue(myPoint.pixels[i]), null);
            //java.awt.Color rgb = java.awt.Color.getHSBColor(0.3, hsb[1],hsb[2]);
            buffer.pixels[k] = p.color( p.red(c3), p.green(c3), p.blue(c3), p.alpha(Application.session.getMyPoints().pixels[k]));
          }
        }
        buffer.updatePixels();
        p.imageMode(PConstants.CENTER);
        float zoom = Application.session.getMap().getZoom();
        p.image(buffer, x, y, radius*5*PApplet.exp(zoom/20000), radius*5*PApplet.exp(zoom/20000));
        p.imageMode(PConstants.CORNER);
      }
      
   }
   
      public int GetDegree(float x, float y){
       int Degree = 0;
       Location location = Application.session.getMap().getLocationFromScreenPosition(x,y);
       for ( int i = 0; i < Application.session.getMatEdge().length; i ++){
           if ((( location.getLat() == Application.session.getMatEdge(0, i)) && ( location.getLon() == Application.session.getMatEdge(1, i))) || (( location.getLat() == Application.session.getMatEdge(2, i)) && ( location.getLon() == Application.session.getMatEdge(3, i)))){
               Degree ++;
           }
       }
       return Degree;
      }
      
    }
    

