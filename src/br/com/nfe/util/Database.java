package br.com.nfe.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Cleber 
 */
public class Database {

    //static GetParametros parametro;
//    private static String //	host = "jdbc:postgresql://localhost:5573/sief",
//            //	user = "postgres",
//            //	password = "123456",
//            //	classe = "org.postgresql.Driver";
//            host,
//            user,
//            password,
//            classe;
  private static String
	host = "jdbc:postgresql://192.168.1.10:5573/sief",
	user = "postgres",
	password = "K@lif@1",
	classe = "org.postgresql.Driver";
    private static Connection conn = null;

    /**
     * metodo criado para testar conexao...
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            Database.getConnection();
            System.out.println("Conexao Ok....");
        } catch (Exception exception) {
            System.out.println("Conexao Falhou...");
            exception.printStackTrace();
        }
    }

    /**
     * Faz a conexao com o banco de dados.
     */
    private static Connection createConnection() throws Exception {
//        parametro = new GetParametros();
//
//        host = parametro.getClasseConexao() + "//"
//                + parametro.getServidorBanco() + ":"
//                + parametro.getPortaBanco() + "/"
//                + parametro.getNomeBanco();
//
//        user = parametro.getUsuarioBanco();
//        password = parametro.getSenhaBanco();
//        classe = parametro.getDriverConexao();

        Class.forName(classe).newInstance();
        Connection c = DriverManager.getConnection(host, user, password);

        //System.out.println("Conexão.....: " + host);
        return c;
    }

    /**
     * Passa para a classe os valores necessarios para uma conexao com o Banco de Dados.
     */
    public static void setDatabase(String sHost, String sUser, String sPassword, String sClasse, String x) {
        host = sHost;
        user = sUser;
        password = sPassword;
        classe = sClasse;
    }

    /**
     * Retorna a conexao com o Banco de Dados.
     */
    public static Connection getConnection() throws Exception {
        if (conn == null) {
            conn = createConnection();
        }
        return conn;
    }

    /**
     * Fecha a conexao com o Banco de Dados.
     */
    public static void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
            conn = null;
        }
    }
}
