/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.nfe.dao.lfs;

import br.com.nfe.bean.lfs.LFS_Nfentrad_ManifestoBean;
import br.com.nfe.util.Database;
import java.lang.Exception;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author supervisor
 */
public class LFS_Nfentrad_ManifestoDAO {

          java.sql.Connection conn = null;
//          JdbcUtil j = null;

      public LFS_Nfentrad_ManifestoDAO() throws Exception{
//        j = new JdbcUtil();
//        conn = j.getConn();
        
        conn = Database.getConnection();

         if (conn == null) {
            throw new Exception(getClass().getName() + ": null connection passed.");
         }
            this.conn = conn;
         }
      
     

public int CountManifesto(LFS_Nfentrad_ManifestoBean bean) throws SQLException, Exception{
    
    if (bean.getCdpedidonfe() <= 0){
        throw new Exception ("Para consulta o CdpedidoNfe não pode ser 0 ou Nulo.");
    }
    String qry = "select count(id_manifesto) from lfs_nfentrad_manifesto where cdpedidonfe = ?";


    PreparedStatement stmt = conn.prepareStatement(qry);
    
    stmt.setInt(1, bean.getCdpedidonfe());

    ResultSet rs = stmt.executeQuery();

    int retorno = 0;    
    if (rs.next()) {
        retorno = rs.getInt(1);
    }

    rs.close();
    stmt.close();

    return retorno;

}
public int Pesquisa_Nfentrad_Manifesto() throws SQLException{
   String qry = "select cdpedidonfe from lfs_nfentrad_manifesto where cstat is null order by id_manifesto limit 1";


   PreparedStatement stmt = conn.prepareStatement(qry);


   ResultSet rs = stmt.executeQuery();

    //System.out.println(">>>Select....: " + stmt.toString());

    int retorno = 0;

    
    if (rs.next()) {
       retorno = rs.getInt("cdpedidonfe");
    }
    rs.close();
    stmt.close();

    //System.out.println(">>>Select....: " + retorno[1]);
    return retorno;

}
public void Update_Nfentrad_Manifesto(LFS_Nfentrad_ManifestoBean bean) throws SQLException{
   String qry = "update lfs_nfentrad_manifesto set cstat = ?, tpevento = ?, retorno = ? where cdpedidonfe = ?";

   PreparedStatement stmt = conn.prepareStatement(qry);

   stmt.setInt(1, bean.getCstat());
   stmt.setInt(2, bean.getTpevento());
   stmt.setString(3, bean.getRetorno());
   stmt.setInt(4, bean.getCdpedidonfe());

   System.out.println(">>>ManifestoDestinatario....:" + stmt);
   
   int retorno = stmt.executeUpdate();
   
   stmt.close();

}





}
