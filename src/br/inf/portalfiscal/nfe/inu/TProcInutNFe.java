//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.10.24 at 10:01:27 AM BRST 
//


package br.inf.portalfiscal.nfe.inu;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * Tipo Pedido de inutilza��o de n�mera��o de  NF-e processado
 * 
 * <p>Java class for TProcInutNFe complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TProcInutNFe">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="inutNFe" type="{http://www.portalfiscal.inf.br/nfe}TInutNFe"/>
 *         &lt;element name="retInutNFe" type="{http://www.portalfiscal.inf.br/nfe}TRetInutNFe"/>
 *       &lt;/sequence>
 *       &lt;attribute name="versao" use="required" type="{http://www.portalfiscal.inf.br/nfe}TVerInutNFe" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TProcInutNFe", propOrder = {
    "inutNFe",
    "retInutNFe"
})
public class TProcInutNFe {

    @XmlElement(required = true)
    protected TInutNFe inutNFe;
    @XmlElement(required = true)
    protected TRetInutNFe retInutNFe;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String versao;

    /**
     * Gets the value of the inutNFe property.
     * 
     * @return
     *     possible object is
     *     {@link TInutNFe }
     *     
     */
    public TInutNFe getInutNFe() {
        return inutNFe;
    }

    /**
     * Sets the value of the inutNFe property.
     * 
     * @param value
     *     allowed object is
     *     {@link TInutNFe }
     *     
     */
    public void setInutNFe(TInutNFe value) {
        this.inutNFe = value;
    }

    /**
     * Gets the value of the retInutNFe property.
     * 
     * @return
     *     possible object is
     *     {@link TRetInutNFe }
     *     
     */
    public TRetInutNFe getRetInutNFe() {
        return retInutNFe;
    }

    /**
     * Sets the value of the retInutNFe property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRetInutNFe }
     *     
     */
    public void setRetInutNFe(TRetInutNFe value) {
        this.retInutNFe = value;
    }

    /**
     * Gets the value of the versao property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersao() {
        return versao;
    }

    /**
     * Sets the value of the versao property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersao(String value) {
        this.versao = value;
    }

}
