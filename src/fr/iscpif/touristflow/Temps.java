/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.iscpif.touristflow;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import processing.core.*; 
/**
 *
 * @author guest
 */
public class Temps {
    
    // Nous ne calculerons les modifs que sur une journée

    public static String firstHourStamp = "2009033100";// année - mois - jour - heure début interval
    public static String lastHourStamp = "2009033124";
    public static String nowHour;

    public static final long MILLIS_PER_4HOURS = 4 * 60 * 60 * 1000; // nombre de millisecondes dans un interval de 4 heures


    public static int hourCount; // nb d'intervals en une journée
    public static int hourIndex; // interval courrant 

    public static int minHourIndex = 0; // les min et les max seront plus utiles sur un grand jeu de données
    public static int maxHourIndex;

    // transforme une date de 2009032900 en 00h 29 Mars 2009 
    public static DateFormat stampFormat = new SimpleDateFormat("yyyyMMddHH");
    // inversement 
    public static DateFormat prettyFormat = new SimpleDateFormat("d MMMM yyyy HH");

    public static String[] hourStamp;
    public static String[] hourPretty;

    public static void setupDates() {
      try {
        Date firstHour = stampFormat.parse(firstHourStamp);
        long firstHourMillis = firstHour.getTime( );
        Date lastHour = stampFormat.parse(lastHourStamp);
        long lastHourMillis = lastHour.getTime( );

        // calcul du nombre d'interval en une journée ( peu se faire à la main, mais bon comme ca c'est fait pour de plus grands jeux )
        hourCount = (int)((lastHourMillis - firstHourMillis) / MILLIS_PER_4HOURS + 1);
        // hourCount = 6;
        maxHourIndex = hourCount;
        hourStamp = new String[hourCount];
        hourPretty = new String[hourCount];

        nowHour = PApplet.year() + PApplet.nf(PApplet.month( ), 2) + PApplet.nf(PApplet.day( ), 2) + PApplet.nf(PApplet.hour( ), 2);

        for (int i = 0; i < hourCount; i++) {
          Date date = new Date(firstHourMillis + MILLIS_PER_4HOURS*i);
          hourPretty[i] = prettyFormat.format(date);
          hourStamp[i] = stampFormat.format(date);
        }
      }catch (ParseException e) { }
    }

    // affichage de la date sous forme d'une barre de sélection 

    static int hourSelectorX;
    static int hourSelectorY = 30;

    // affichage de la barre de sélection
    static public void drawDateSelector( ) {
      PApplet p = Application.session.getPApplet();
        
      hourSelectorX = (p.width - hourCount*2) / 2;
      p.strokeWeight(1);
      for (int i = 0; i < hourCount; i++) {
        int x = hourSelectorX + i*(p.width/140);
        // surligner la ligne courante
        if (i == hourIndex) {
          p.stroke(0);
          p.fill( 0 );
          p.line(x, 0, x, (float)(p.height/75.307));
          p.textAlign(PConstants.CENTER, PConstants.TOP);
          p.text(hourPretty[hourIndex] + "h", p.width/2, (float)(p.height/65.266));
        } else {
          // noircir les dates selectionnables ( pour une générlisation du modèle )
          if ((i >= minHourIndex) && (i <= maxHourIndex)) {
            p.stroke(128); 
          } else {
            p.stroke(204); 
          }
          p.line(x, 0, x, (float)(p.height/139.857));
        }
      }
      p.fill(190, 201, 186,200);
      p.stroke(128);
      p.triangle((float)(p.width/2.44), (float)(p.height/39.16), (float)(p.width/2.35), (float)(p.height/65.266), (float)(p.width/2.35), (float)(p.height/27.97));
      p.triangle((float)(p.width/1.707), (float)(p.height/39.16), (float)(p.width/1.75), (float)(p.height/65.266), (float)(p.width/1.75), (float)(p.height/27.97));
    }


    // mettre à jour la valeure de l'interval courant et en passant de l'interpolator
    public static void setHour(int i) {
      hourIndex = i;
      if ( hourIndex > hourCount - 1 ) hourIndex = 0; 
      if ( hourIndex < 0 ) hourIndex = hourCount - 1;
      Application.session.setIndex(hourIndex);
    }

    public static int getHourIndex() {
        return hourIndex;
    }
    
    public static int getHourCount() {
        return hourCount;
    }
}
