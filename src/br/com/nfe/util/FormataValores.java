/*
 * FormataValores.java
 * 
 * Created on 13/07/2007, 11:29:00
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.nfe.util;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.StringTokenizer;
import javax.swing.JTextField;

/**
 *
 * @author supervisor
 */
public class FormataValores {
  private static java.text.DecimalFormat formatter = new java.text.DecimalFormat("000000");
  //java.util.Locale locale = java.util.Locale.GERMAN;
    
  private static java.util.Locale locale = new java.util.Locale("pt", "BR");
  private static NumberFormat iFormatado = NumberFormat.getNumberInstance(locale);
  private static long iNumero;
  
  public FormataValores() {
    
  }

  public static String formataVr_Float(String valor) {
    String retorno = "";
      
    try {    
      Number number = NumberFormat.getNumberInstance(locale).parse(valor);
	
      if (number instanceof Long) {
          retorno = number+""; // Long value
      } else {
          retorno = number+""; // Double value
      }
    } catch (ParseException e) {
      //main.mensagem.mensagemErro("Erro na Formatação do Numero: " + valor,  e, main, true);
    }
    
    return retorno;
  }

  public static String formataVr_Float(Double valor, String pattern) {
    JTextField jtf = new JTextField();

    String str = valor.toString();
    str = str.trim();

    //--- Se estiver formatado deve retornar --//
    boolean bln = str.indexOf(",") >= 0;  // bln true?
    if (bln) return str;


    if (str.equals("")) {
       return str; //-- sair se estiver em branco --//
    }

    if (validaFormatado(str)){
      return str;
    }

    jtf.setText(str);

    DecimalFormat  formatador  = new DecimalFormat();   ///======== Formatador =========///
    formatador.applyPattern(pattern);

    double floatW = 0;

    try {
      floatW = Double.parseDouble(jtf.getText());
    } catch (Exception e) {
      Toolkit.getDefaultToolkit().beep();
      //System.out.println("Erro na Formatacao do Valor: "+jtf.getText());
      return jtf.getText();
    }

    jtf.setText( formatador.format(floatW) );
    
    for (int i = jtf.getText().length(); i < pattern.length(); i++) {  
        jtf.setText(jtf.getText().concat("0"));
    }

//    formatado = true;
//    desformatado = false;
    return jtf.getText().replace(",", ".");

  }  // formataVr_Float

  
  /*  formataNumero                     *
   *  formata numero inteiro separando  *
   *  digitos com ponto de tres em tres */
  public static String formataNumero(String str) {
    if (str.trim().equals(""))
      return str;

    if (validaFormatado(str)){
      return str;
    }
    
    long iNumero = Integer.parseInt(str);
    NumberFormat iFormatado = NumberFormat.getNumberInstance( new java.util.Locale("pt", "BR"));
    

    try {
      Number n = iFormatado.parse(str);

      if(n!=null) {
        iNumero = n.intValue();
        iFormatado = NumberFormat.getIntegerInstance( new java.util.Locale("pt", "BR"));
        str = iFormatado.format(iNumero);
      }
    } catch (Exception ex) {
      Toolkit.getDefaultToolkit().beep();
      return str;
    }

     return str;

    }
    
  private static boolean validaFormatado(String jtf){
    boolean bRet = false, bForV = false, bForP = false;
    String stVar = jtf ;
    int contV = 0, contP = 0;

    for(int i = stVar.trim().length(); i >=1; i--){
      if (stVar.charAt(i-1) == ',') bForV = true;
      if (stVar.charAt(i-1) == '.') bForP = true;
      if (!bForV) contV++;
      if (!bForP) contP++;
    }

    if (contV == 2 || (contP == 3 && contV >=3)) bRet = true;

    return bRet;
  }
  
/*  formataVr_Float                          *
 *  formata valor colocando pontos e virgula *
 *  em um valor com dois decimais            */
  public static JTextField formataVr_Float(JTextField jtf, String pattern) {

   if (jtf == null) {
      return jtf; //-- sair se estiver null --//
   }
    
    //--- Se estiver formatado deve retornar --//
   boolean bln = jtf.getText().indexOf(",") > 0;  // bln true?
   if (bln) return jtf;
   
   if (jtf.getText().trim().equals("")) {
      return jtf; //-- sair se estiver em branco --//
   }

   if (validaFormatado(jtf)){
     return jtf;
   }

    DecimalFormat  formatador  = new DecimalFormat();   ///======== Formatador =========///
    formatador.applyPattern(pattern);

    float floatW = 0;

    try {
      floatW = Float.parseFloat(jtf.getText());
    } catch (Exception e) {
      e.printStackTrace();
      return jtf;
    }

    jtf.setText( formatador.format(floatW) );
    return jtf;
  }  // formataVr_Float


  public static String desformataValor(String str) {
    if (str.equals("")) return str; //-- sair se estiver em branco --//

    //--- Se nao estiver formatado deve retornar --//
    boolean bln = str.indexOf(",") >= 0;  // bln true?
    if (!bln) return str;

    StringTokenizer st = new StringTokenizer(str, ".");
    String semVirgula = "";
    while ( st.hasMoreTokens() ) {
      semVirgula += st.nextToken();
    }
    semVirgula = semVirgula.replace(',', '.');
    double numero = Double.parseDouble( semVirgula );

    str=( semVirgula );

//    formatado = false;
//    desformatado = true;
   return str;
  }

/*  desformataValor                             *
 *  retira a formatacao de valor do JTextField  *
 *  retirando os pontos (.) e substituido       *
 *  virgula (,) por ponto.                       */
  public static JTextField desformataValor(JTextField jtf) {

    //--- Se nao estiver formatado deve retornar --//

//    if (desformatado){
//      return jtf;
//    }
    if (jtf.getText().trim().equals(""))
      return jtf;

    boolean bln = jtf.getText().indexOf(",") >= 0;  // bln true?
    if (!bln) return jtf;

    StringTokenizer st = new StringTokenizer(jtf.getText(), ".");
    String semVirgula = "";
    while ( st.hasMoreTokens() ) {
      semVirgula += st.nextToken();
    }
    semVirgula = semVirgula.replace(',', '.');
    double numero = Double.parseDouble( semVirgula );

    jtf.setText( semVirgula );

//    formatado = false;
//    desformatado = true;
    return jtf;
  }
  
  public static float get_Multiplica_BigDec(float float_A, float float_B) {
    float fRetorno = 0;

    BigDecimal vrA = new BigDecimal(String.valueOf(float_A));
    BigDecimal vrB = new BigDecimal(String.valueOf(float_B));

    vrA = vrA.multiply(vrB);

    fRetorno = Float.parseFloat( vrA.toString() );

    return fRetorno;
  }

  public static float get_Divide_BigDec(float float_A, float float_B) {
    float fRetorno = 0;

    BigDecimal vrA = new BigDecimal(String.valueOf(float_A)+"");
    BigDecimal vrB = new BigDecimal(String.valueOf(float_B)+"");

    vrA = vrA.divide(vrB, 3, BigDecimal.ROUND_HALF_UP);
    fRetorno = Float.parseFloat( vrA.toString() );

    return fRetorno;
  }
  
  /*  formataVr_Float                          *
   *  formata valor colocando pontos e virgula *
   *  em um valor com dois decimais            */
  public static JTextField formataVr_Float_PDV(JTextField jtf, String pattern) {
      if (jtf.getText().equals("")) {
        jtf.setText("0");
      }

      DecimalFormat  formatador  = new DecimalFormat();   
      formatador.applyPattern(pattern);

      float floatW = 0;

      try {
        floatW = Float.parseFloat(jtf.getText());
      } catch (Exception e) {
        Toolkit.getDefaultToolkit().beep();
        return jtf;
      }

      jtf.setText( formatador.format(floatW) );
      return jtf;
    }   
  
/*  desformataValor                             *
 *  retira a formatacao de valor do JTextField  *
 *  retirando os pontos (.) e substituido       *
 *  virgula (,) por ponto.                       */
  public static float getVrFloat(JTextField jtf) {
    if (jtf.getText().trim().equals(""))
      return 0.0f;

    boolean bln = jtf.getText().indexOf(",") >= 0;  // bln true?
    if (!bln) return 0.0f;

    StringTokenizer st = new StringTokenizer(jtf.getText(), ".");
    String semVirgula = "";
    while ( st.hasMoreTokens() ) {
      semVirgula += st.nextToken();
    }
    
    semVirgula = semVirgula.replace(',', '.');
    float numero = Float.parseFloat( semVirgula );

    return numero;
  }

  public static float get_Com2decimais(float fVr) {
    String sRet = "";
    String sVr = fVr + "";

    StringTokenizer st = new StringTokenizer(sVr, ".");
    String sInteiro = "";
    String sDecimal = "";

    if (st.countTokens() == 1) {
      sInteiro = st.nextToken();
    }

    if (st.countTokens() == 2) {
      sInteiro = st.nextToken(); //Estava Dando Problema - Netou colocou isto aqui 19/01/2005
      sDecimal = st.nextToken();

      if (sDecimal.length() > 2)
        sDecimal = sDecimal.substring(0, 2);
    }

    sRet = sInteiro + "." + sDecimal;

    float f = Float.parseFloat(sRet);

    return f;
  }

  public static float get_Adiciona_BigDec(float float_A, float float_B) {
    float fRetorno = 0;

    BigDecimal vrA = new BigDecimal(String.valueOf(float_A));
    BigDecimal vrB = new BigDecimal(String.valueOf(float_B));

    vrA = vrA.add(vrB);

    fRetorno = Float.parseFloat( vrA.toString() );

    return fRetorno;
  }

  public static float get_Subtrai_BigDec(float float_A, float float_B) {
    float fRetorno = 0;

    BigDecimal vrA = new BigDecimal(String.valueOf(float_A));
    BigDecimal vrB = new BigDecimal(String.valueOf(float_B));

    vrA = vrA.subtract(vrB);

    fRetorno = Float.parseFloat( vrA.toString() );

    return fRetorno;
  }
  
/*  consistirValor                          *
 *  testa digitacao de valores, permitindo  *
 *  digitar somente numeros, "." e "-"      *
 *  e restringe a quantidade de decimais    */
  public static void consistirValor(KeyEvent evt, JTextField tf, int qtdDecimais) {
    char c = evt.getKeyChar();
    boolean bln = false;
    int pos = tf.getCaretPosition();

    if (c != evt.VK_BACK_SPACE) {   //--- Permite ditacao de Backspace ---//
      //--- Permite a digitacao de Teclas Numericas ---//
      if (!((c >= '0' && c <= '9') || c == ',' || c == '-')) {
//System.out.println("====----->>>> Consumiu: " + c);
        evt.consume();
      }

      if (c == ',') {
//System.out.println("====----->>>> Modificou: " + c);
        evt.setKeyChar('.');  //-- substituindo, por .
      }


      //--- Restringe a digitacao de uma quantia de caracteres ---//
      if (tf.getText().trim().length() >= 9) {
        evt.consume();
      }

      //--- Permitir digitar apenas um "." --//
      bln = tf.getText().indexOf(".") >= 0;  // bln true?


      if (bln) {
        if (c == '.') {
          //System.out.println("====----->>>> Consumiu-2: " + c);
          evt.consume();
        }


        StringTokenizer st = new StringTokenizer(tf.getText(), ".");
        String sInteiro = "";
        String sDecimal = "";

        if (st.countTokens() == 1) {
          sInteiro = st.nextToken();
          if (sInteiro.length() > 7){ //Estava Dando Problema - Netou colocou isto aqui 19/01/2005
            evt.consume();
          }
        }

        if (st.countTokens() == 2) {
          sInteiro = st.nextToken(); //Estava Dando Problema - Netou colocou isto aqui 19/01/2005
          sDecimal = st.nextToken();
          if (sDecimal.length() > qtdDecimais){
            evt.consume();
          }
        }

      }

      //--- Permitir digitar "." so se digitar "0." antes --//
      bln = tf.getText().trim().length() == 0;  // bln true?
      if (bln) {
        if (c == '.') {
          evt.consume();
        }
      }

//cleber... testar se o usuario nao esta deixando so .

      //--- Permitir digitar apenas "n" decimais apos o "." --//
//      bln = (tf.getText().indexOf(".") >= 0) && (tf.getSelectedText().length() == 0);  // bln true?
      bln = tf.getText().indexOf(".") >= 0;  // bln true?
      if (bln) {
        int posicaoDoPonto = tf.getText().indexOf(".");
        if (tf.getText().length() > posicaoDoPonto+qtdDecimais) {
          if (tf.getCaretPosition() > posicaoDoPonto){
            evt.consume(); //--cleber
          }
        }
      }

      bln = false;
      bln = tf.getText().indexOf("-") >= 0;  // bln true?

      if (bln) {
        if (c == '-') {
          evt.consume();
        }
      }
    }

    try {
      if (tf.getSelectedText().length() > 0) {
        tf.setText("");
      }
    } catch (Exception ex) {
    }

    if (tf.getText().trim().equals("") && c == ',') {
      tf.setText("0");
    }

//    tf.setCaretPosition(pos);


    acionarEnter(evt);
  }  // consistirValor
  
  public static void acionarEnter(KeyEvent e) {
    char c = e.getKeyChar();

    if (c == e.VK_ENTER) {
      try {
        Robot mane = new Robot();

        // Simulate a key press
        mane.keyPress(KeyEvent.VK_TAB);
        mane.keyRelease(KeyEvent.VK_TAB);
      } catch (AWTException x) {}
    }
  }  
  
/*  desformataNumero                 *
 *  desfaz a formatacao de um numero *
 *  inteiro retirando os pontos (.)  */
  public static JTextField desformataNumero(JTextField jtf) {
    if (jtf.getText().trim().equals(""))
      return jtf;

    boolean bln = jtf.getText().indexOf(",") >= 0;  // bln true?
    if (bln) return jtf;

    NumberFormat iFormatado = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
  
    jtf.setText(jtf.getText().trim());
    int tamanho = jtf.getText().length();
    if (tamanho>2) {
      if (jtf.getText().substring( (tamanho - 3), tamanho).equals(".0"))
        jtf.setText(jtf.getText().substring( 0, (tamanho - 3)));
    }
    try {
      Number n = iFormatado.parse(jtf.getText().trim());
      jtf.setText(n.toString());

    } catch (Exception ex) {
      jtf.setText("");
    }
    return jtf;
  }
  
/*  consistirNumero                       *
 *  testa digitacao de Numeros Inteiros,  *
 *  permitindo digitar somente numeros.   */
  public static void consistirNumero(KeyEvent evt, JTextField tf) {
    char c = evt.getKeyChar();
    boolean bln = false;

    if (c != evt.VK_BACK_SPACE) {   //--- Permite ditacao de Backspace ---//
      //--- Permite a digitacao de Teclas Numericas ---//
      if (!(c >= '0' && c <= '9')) {
        evt.consume();
      }
      //--- Restringe a digitacao de uma quantia de caracteres ---//
      if (tf.getText().length() >= 8) {
        evt.consume();
      }
    }

    acionarEnter(evt);
  }  
  
  public static String formataVr_String(String str, String pattern, int qtdDecimais) {

    str = str.trim();

    //--- Se estiver formatado deve retornar --//
    boolean bln = str.indexOf(",") >= 0;  // bln true?
    if (bln) return str;

    if (str.equals("")) return str; //-- sair se estiver em branco --//

    if (validaFormatado(str)){
      return str;
    }

    DecimalFormat  formatador  = new DecimalFormat();   ///======== Formatador =========///
    formatador.applyPattern(pattern);
    str = formatador.format(new java.math.BigDecimal( str ));

    return str;
  }  
  
  public static void consistirValor_noEnter(KeyEvent evt, JTextField tf, int qtdDecimais) {
    char c = evt.getKeyChar();
    boolean bln = false;
    int pos = tf.getCaretPosition();

    if (c != evt.VK_BACK_SPACE) {   //--- Permite ditacao de Backspace ---//
      //--- Permite a digitacao de Teclas Numericas ---//
      if (!((c >= '0' && c <= '9') || c == ',' || c == '-')) {
        evt.consume();
      }

      if (c == ',') {
        evt.setKeyChar('.');  //-- substituindo, por .
      }

      //--- Restringe a digitacao de uma quantia de caracteres ---//
      if (tf.getText().trim().length() >= 9) {
        evt.consume();
      }

      //--- Permitir digitar apenas um "." --//
      bln = tf.getText().indexOf(".") >= 0;  // bln true?

      if (bln) {
        if (c == '.') {
          evt.consume();
        }

        StringTokenizer st = new StringTokenizer(tf.getText(), ".");
        String sInteiro = "";
        String sDecimal = "";

        if (st.countTokens() == 1) {
          sInteiro = st.nextToken();
          if (sInteiro.length() > 7){ //Estava Dando Problema - Netou colocou isto aqui 19/01/2005
            evt.consume();
          }
        }

        if (st.countTokens() == 2) {
          sInteiro = st.nextToken(); //Estava Dando Problema - Netou colocou isto aqui 19/01/2005
          sDecimal = st.nextToken();
          if (sDecimal.length() > qtdDecimais){
            evt.consume();
          }
        }

      }

      //--- Permitir digitar "." so se digitar "0." antes --//
      bln = tf.getText().trim().length() == 0;  // bln true?
      if (bln) {
        if (c == '.') {
          evt.consume();
        }
      }

      //--- Permitir digitar apenas "n" decimais apos o "." --//
      bln = tf.getText().indexOf(".") >= 0;  // bln true?
      if (bln) {
        int posicaoDoPonto = tf.getText().indexOf(".");
        if (tf.getText().length() > posicaoDoPonto+qtdDecimais) {
          if (tf.getCaretPosition() > posicaoDoPonto){
            evt.consume(); //--cleber
          }
        }
      }

      bln = false;
      bln = tf.getText().indexOf("-") >= 0;  // bln true?

      if (bln) {
        if (c == '-') {
          evt.consume();
        }
      }
    }

    try {
      if (tf.getSelectedText().length() > 0) {
        tf.setText("");
      }
    } catch (Exception ex) {
    }

    if (tf.getText().trim().equals("") && c == ',') {
      tf.setText("0");
    }

    //acionarEnter(evt);
  }  
  
  public static String desformataNumero(String str) {
    NumberFormat iFormatado = NumberFormat.getNumberInstance(new java.util.Locale("pt", "BR"));
    
//    if (desformatado){
//      return str;
//    }
    if (str.trim().equals(""))
      return str;

    boolean bln = str.indexOf(",") >= 0;  // bln true?
    if (bln) return str;

    JTextField jtf = new JTextField();

    jtf.setText(str);
    jtf.setText(jtf.getText().trim());
    int tamanho = jtf.getText().length();
    if (tamanho>2) {
      if (jtf.getText().substring( (tamanho - 3), tamanho).equals(".0"))
        jtf.setText(jtf.getText().substring( 0, (tamanho - 3)));
    }
    try {
      Number n = iFormatado.parse(str.trim());
      str = (n.toString());

    } catch (Exception ex) {
      jtf.setText("");
    }

//    formatado = false;
//    desformatado = true;
    return str;
  }  // desformataNumero

  /**
   *  formataNumero
   *  formata numero inteiro separando
   *  digitos com ponto de tres em tres
   */
  public static JTextField formataNumero(JTextField jtf) {
    jtf.setText(jtf.getText().trim());  //--cleber--

    if (jtf.getText().trim().equals(""))
      return jtf;

    if (validaFormatado(jtf)){
      return jtf;
    }

    //--cleber-- para parar de dar erro na m. da formatacao de nr float.
    int tamanho = jtf.getText().trim().length();

    String Lixo = "";
    Lixo = jtf.getText().trim();

    if (tamanho>2) {
      Lixo = Lixo.substring( (tamanho - 2), tamanho);

      if (Lixo.equals(".0")) {
        System.out.println("......" + Lixo);
      }

      if (jtf.getText().substring( (tamanho - 2), tamanho).equals(".0")) {
        jtf.setText(jtf.getText().substring(0, (tamanho - 2)));
      }
    }

    String s = jtf.getText();

    iNumero = Long.parseLong(s);

  try {
    Number n = iFormatado.parse(s);

    if(n!=null) {
      iNumero = n.intValue();
      iFormatado = NumberFormat.getIntegerInstance(locale);
      String ns = iFormatado.format(iNumero);
      jtf.setText(ns);
    }
    else {
      jtf.setText("Parse Error: " + s);
    }
  } catch (Exception ex) {
    return jtf;
  }

//  formatado = true;
//  desformatado = false;
  return jtf;
}  

  private static boolean validaFormatado(JTextField jtf){
    boolean bRet = false, bForV = false, bForP = false;
    String stVar = jtf.getText() ;
    int contV = 0, contP = 0;

    for(int i = stVar.trim().length(); i >=1; i--){
      if (stVar.charAt(i-1) == ',') bForV = true;
      if (stVar.charAt(i-1) == '.') bForP = true;
      if (!bForV) contV++;
      if (!bForP) contP++;
    }

//    System.out.println("Quanto que tem Antes de Virgula: " + contV);
//    System.out.println("Quanto que tem Antes do Ponto: " + contP);


    if (contV == 2 || (contP == 3 && contV >=3)) bRet = true;

    return bRet;
  }

  /**
   * Retornar valor float com valor truncado no formato definido pelo parametro pattern que devera
   * conter apenas "." para designar a parte decimal ("," nao deve ser usado).
   * @param valorFloat float
   * @param pattern String
   * @return float
   */
  public static float get_Float_NoRound(float valorFloat, String pattern) {
    float fRetorno = 0.00f;
    DecimalFormat df = new DecimalFormat(pattern);
    String s = df.format(valorFloat);

    s = s.replace(',', '.');

    try {
      fRetorno = Float.parseFloat(s);
    } catch (Exception ex) {
    }

    return fRetorno;
  }  
  
/*  consistirNumero                       *
 *  testa digitacao de Numeros Inteiros,  *
 *  permitindo digitar somente numeros.   */
  public static void consistirTamanho(KeyEvent evt, String st, int tamanho) {
    char c = evt.getKeyChar();
    boolean bln = false;

    //--- Restringe a digitacao de uma quantia de caracteres ---//
    if (c != evt.VK_BACK_SPACE && c != evt.VK_BACK_SPACE) {   //--- Permite ditacao de Backspace ---//

      if (st.length() >= tamanho) {
        evt.consume();
      }
    }

    acionarEnter(evt);
  }  // consistirNumero

  public static void consistirTamanho_SemEnter(KeyEvent evt, String st, int tamanho) {
    char c = evt.getKeyChar();
    boolean bln = false;

    //--- Restringe a digitacao de uma quantia de caracteres ---//
    if (c != evt.VK_BACK_SPACE && c != evt.VK_BACK_SPACE) {   //--- Permite ditacao de Backspace ---//

      if (st.length() >= tamanho) {
        evt.consume();
      }
    }
  }
  
}
