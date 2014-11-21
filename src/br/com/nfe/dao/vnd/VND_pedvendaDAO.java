/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.nfe.dao.vnd;

import br.com.nfe.util.Database;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author supervisor
 */
public class VND_pedvendaDAO {

     java.sql.Connection conn = null;

      public VND_pedvendaDAO() throws Exception{
         conn = Database.getConnection();

         if (conn == null) {
            throw new Exception(getClass().getName() + ": null connection passed.");
         }
            this.conn = conn;
         }

public Boolean atualiza_pedvenda(int cdpedido, int situacao) throws SQLException{
    String qry = "select vnd_atualiza_pedvenda(?, ?)";

    PreparedStatement stmt = conn.prepareStatement(qry);

    stmt.setInt(1, cdpedido);
    stmt.setInt(2, situacao);

    ResultSet rs = stmt.executeQuery();

    //System.out.println(">>>Select....: " + stmt.toString());

    Boolean retorno = false;
    
    if (rs.next()) {
       //retorno = rs.getString("vnd_atualiza_pedvenda");
        retorno = true;
    }
    rs.close();
    stmt.close();

    return retorno;

}
public String select_idnfe(int cdpedido) throws SQLException{
    String qry = "select idnfe from vnd_pedvenda where cdpedido = ?";

    PreparedStatement stmt = conn.prepareStatement(qry);

    stmt.setInt(1, cdpedido);

    ResultSet rs = stmt.executeQuery();

    //System.out.println(">>>Select....: " + stmt.toString());

    String retorno = "";
    if (rs.next()) {
       retorno = rs.getString("idnfe");
    }
    rs.close();
    stmt.close();

    return retorno;

}

}
