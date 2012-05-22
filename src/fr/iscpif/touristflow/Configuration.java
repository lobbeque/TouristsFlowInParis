/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.iscpif.touristflow;

import processing.core.PApplet;
import processing.core.PFont;

/**
 *
 * @author macbook
 */
public class Configuration {
    
    public class Geography {
          float earthRadius = 3958.75f;
    }
    public class Smoothing {
        float Dmax = 500;
        float DmaxSmooth = 500;
        float DmaxPas = 50; 
    }
    public class Exports {
        String screenshotsDirPath = "captures/touristflows31-March-2009-num_";
    }
    public class Imports {
        String graphDirPath = "/Users/macbook/Documents/these/donnees/roaming/gexf-24h-ss80/roam_ss80_03_31_";
    }
    
    public class Fonts {
        String font = "DejaVuSans-ExtraLight-";
        public PFont size_8 = new PFont();
        public PFont size_12 = new PFont();
        public PFont size_15 = new PFont();
        public PFont size_20 = new PFont();
        public PFont size_17 = new PFont();
        public Fonts() {
           
        }
        public void recreateUsing(PApplet p) {
            size_8 = p.createFont(font, 8);
            size_12 = p.createFont(font, 12);
            size_15 = p.createFont(font, 15);
            size_17 = p.createFont(font, 17);
            size_20 = p.createFont(font, 20);
            
        }
    }
    public Smoothing smoothing = new Smoothing();
    public Geography geo = new Geography();
    public Exports exports = new Exports();
    public Imports imports = new Imports();
    public Fonts fonts = new Fonts();
}
