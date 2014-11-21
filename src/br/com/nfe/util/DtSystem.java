/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.nfe.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author supervisor
 */
public class DtSystem {
    public static void main(String[] args) {
        DtSystem d = new DtSystem();
        System.out.println(d.getdhEvento());
    }
    public static String getDate(){
        SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss");
        df.format(new Date(System.currentTimeMillis()));
   
         return df.getCalendar().getTime().toString();
    }
    public static String getdhEvento(){
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ"); //você pode usar outras máscaras
        Date y = new Date();
        
        
        String dhEvento = sdf1.format(y);
        dhEvento = dhEvento.substring(0, 22);
        dhEvento = dhEvento.replace(" ", "T");
        dhEvento = dhEvento+":00";
        
        return dhEvento;
    }
    
}
