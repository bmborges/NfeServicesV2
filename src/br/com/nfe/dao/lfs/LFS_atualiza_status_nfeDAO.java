/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.nfe.dao.lfs;

import br.com.nfe.util.Database;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author bruno
 */
public class LFS_atualiza_status_nfeDAO {

    java.sql.Connection conn = null;

  public LFS_atualiza_status_nfeDAO() throws Exception {
    conn = Database.getConnection();
    
    if (conn == null) {
      throw new Exception(getClass().getName() + ": null connection passed.");
    }
    this.conn = conn;
  }
/*
public int set_atualiza_status_nfe(LFS_atualiza_status_nfeBean bean) throws SQLException {
    String qry = "select lfs_atualiza_status_nfe(?, ?)";

   
    PreparedStatement stmt = con.prepareStatement(qry);

    stmt.setString(1, bean.getNmarquivo());
    stmt.setString(2, bean.getConteudo());

    
   ResultSet rs = stmt.executeQuery();

    System.out.println(">>>Select....: " + stmt.toString());

           
    int retorno = 0;

    if (rs.next()) {
      // retorno = rs.getInt("lfs_atualiza_status_nfe");
    }
    rs.close();
    stmt.close();

    return retorno;
  }
*/
public int set_atualiza_status_nfe(String nmArquivo, String dados) throws SQLException {
    String qry = "select lfs_atualiza_status_nfe(?, ?)";


    PreparedStatement stmt = conn.prepareStatement(qry);

    stmt.setString(1, nmArquivo);
    stmt.setString(2, dados);


   ResultSet rs = stmt.executeQuery();

   SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss");
   df.format(new Date(System.currentTimeMillis()));
   
    System.out.println(df.getCalendar().getTime() + " >>>AtualizaStatusNfe....: " + stmt.toString());

    int retorno = 0;

    if (rs.next()) {
       retorno = rs.getInt("lfs_atualiza_status_nfe");
    }
    rs.close();
    stmt.close();

    return retorno;
  }

 

}
