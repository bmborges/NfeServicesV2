/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.nfe.dao.par;

import br.com.nfe.dao.adm.*;
import br.com.nfe.util.Database;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import javax.swing.JOptionPane;


/**
 *
 * @author supervisor
 */
public class PAR_ParceiroDAO {
    
    java.sql.Connection conn = null;
   
    
     public PAR_ParceiroDAO() throws Exception{
        
        conn = Database.getConnection();

         if (conn == null) {
            throw new Exception(getClass().getName() + ": null connection passed.");
         }
            this.conn = conn;
            
     }
     
public String pesquisa_email(String idnfe) throws SQLException {
   
   String qry = "select email from par_parceiro"
           + " where idparceiro = (select idparceiro from vnd_pedvenda where idnfe = ?)"
           + " and position('@' in email) > 0";
           //+ " where idparceiro = 21023";
   
   PreparedStatement stmt = conn.prepareStatement(qry);
   
   stmt.setString(1, idnfe);
   
   ResultSet rs = stmt.executeQuery();
   
   String retorno = "";
   if (rs.next()) {
       retorno = rs.getString("email");
   }

   rs.close();
   stmt.close();

   return retorno;

}


    
}
