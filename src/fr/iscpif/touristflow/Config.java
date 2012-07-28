/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.iscpif.touristflow;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import org.openide.util.Exceptions;
import org.yaml.snakeyaml.Yaml;
import processing.core.PApplet;
import processing.core.PFont;

/**
 *
 * @author jbilcke
 */
public final class Config {

    public class Fonts {
        String font = "DejaVuSans-ExtraLight-";
        public PFont size_8 = new PFont();
        public PFont size_10 = new PFont();        
        public PFont size_12 = new PFont();
        public PFont size_15 = new PFont();
        public PFont size_20 = new PFont();
        public PFont size_17 = new PFont();
     
        public Fonts() {
           
        }
        public void recreateUsing(PApplet p) {
            size_8 = p.createFont(font, 8);
            size_10 = p.createFont(font, 10);            
            size_12 = p.createFont(font, 12);
            size_15 = p.createFont(font, 15);
            size_17 = p.createFont(font, 17);
            size_20 = p.createFont(font, 20);
            
        }
    }
    
    public final Fonts fonts = new Fonts();
    public final String inputGraphPrefix;
    public final String nbRoamingBTSmoy;
    public final String heatMapSprite;
    public final String arrowsPrefix;
    public final String capturesPrefix;
    public final String writeArrowsPrefix;
    public final Boolean useProxy;
    public final String proxyHost;
    public final String proxyPort;
    public final Float initialLat;
    public final Float initialLon;
    public final Integer initialZoom;
 
    public final Integer screenWidth;
    
    public final Integer screenHeight;
    
    public final Boolean useTiles;
    
    public final String pathToTiles;
    
    
    public Config(String path) {
        Map map = new HashMap();
        try {
            map = loadFromStream(path);
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (map.containsKey("inputGraphPrefix")) {
            inputGraphPrefix = (String) map.get("inputGraphPrefix");
        } else {
            inputGraphPrefix = "";
        }
         
        if (map.containsKey("nbRoamingBTSmoy")) {
            nbRoamingBTSmoy = (String) map.get("nbRoamingBTSmoy");
        } else {
            nbRoamingBTSmoy = "";
        }
        
        if (map.containsKey("heatMapSprite")) {
            heatMapSprite = (String) map.get("heatMapSprite");
        } else {
            heatMapSprite = "";
        }
        
        if (map.containsKey("arrowsPrefix")) {
            arrowsPrefix = (String) map.get("arrowsPrefix");
        } else {
            arrowsPrefix = "";
        }
        
       if (map.containsKey("capturesPrefix")) {
            capturesPrefix = (String) map.get("capturesPrefix");
        } else {
            capturesPrefix = "";
        }
        
        
        if (map.containsKey("writeArrowsPrefix")) {
            writeArrowsPrefix = (String) map.get("writeArrowsPrefix");
        } else {
            writeArrowsPrefix = "";
        }
        
             
        if (map.containsKey("useProxy")) {
            useProxy = (Boolean) map.get("useProxy");
        } else {
            useProxy = false;
        }
        
        
              
        if (map.containsKey("proxyHost")) {
            proxyHost = (String) map.get("proxyHost");
        } else {
            proxyHost = "";
        }
        
        
              
        if (map.containsKey("proxyPort")) {
            proxyPort = (String) map.get("proxyPort");
        } else {
            proxyPort = "";
        }
        
        
                     
        if (map.containsKey("initialLat")) {
            initialLat = ((Double) map.get("initialLat")).floatValue();
        } else {
            initialLat = 48.866f;
        }
        
              
         if (map.containsKey("initialLon")) {
            initialLon = ((Double) map.get("initialLon")).floatValue();
        } else {
            initialLon = 2.359f;
        }
        
                
                     
        if (map.containsKey("initialZoom")) {
            initialZoom = (Integer) map.get("initialZoom");
        } else {
            initialZoom = 10;
        }
        
        
        if (map.containsKey("screenWidth")) {
            screenWidth = (Integer) map.get("screenWidth");
        } else {
            screenWidth = 800;
        }
        
        
        
        if (map.containsKey("screenHeight")) {
            screenHeight = (Integer) map.get("screenHeight");
        } else {
            screenHeight = 600;
        }
        
        if (map.containsKey("useTiles")) {
            useTiles = (Boolean) map.get("useTiles");
        } else {
            useTiles = false;
        }
        
        if (map.containsKey("pathToTiles")) {
            pathToTiles = (String) map.get("pathToTiles");
        } else {
            pathToTiles = "jdbc:sqlite:./ressources/idf_light.mbtiles";
        }
        
        
        
    }

    public Map loadFromStream(String path) throws FileNotFoundException {
        InputStream input = new FileInputStream(new File(path));
        Yaml yaml = new Yaml();

        return (Map) yaml.load(input);

    }
}
