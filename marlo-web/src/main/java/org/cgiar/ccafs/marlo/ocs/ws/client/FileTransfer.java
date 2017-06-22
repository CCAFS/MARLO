/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/


package org.cgiar.ccafs.marlo.ocs.ws.client;

public class FileTransfer implements java.io.Serializable {

  // Type metadata
  private static org.apache.axis.description.TypeDesc typeDesc =
    new org.apache.axis.description.TypeDesc(FileTransfer.class, true);

  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "fileTransfer"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("docBytes");
    elemField.setXmlName(new javax.xml.namespace.QName("", "docBytes"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("docName");
    elemField.setXmlName(new javax.xml.namespace.QName("", "docName"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
  }

  /**
   * Get Custom Deserializer
   */
  public static org.apache.axis.encoding.Deserializer getDeserializer(java.lang.String mechType,
    java.lang.Class _javaType, javax.xml.namespace.QName _xmlType) {
    return new org.apache.axis.encoding.ser.BeanDeserializer(_javaType, _xmlType, typeDesc);
  }

  /**
   * Get Custom Serializer
   */
  public static org.apache.axis.encoding.Serializer getSerializer(java.lang.String mechType, java.lang.Class _javaType,
    javax.xml.namespace.QName _xmlType) {
    return new org.apache.axis.encoding.ser.BeanSerializer(_javaType, _xmlType, typeDesc);
  }


  /**
   * Return type metadata object
   */
  public static org.apache.axis.description.TypeDesc getTypeDesc() {
    return typeDesc;
  }


  private byte[] docBytes;


  private java.lang.String docName;


  private java.lang.Object __equalsCalc = null;

  private boolean __hashCodeCalc = false;

  public FileTransfer() {
  }

  public FileTransfer(byte[] docBytes, java.lang.String docName) {
    this.docBytes = docBytes;
    this.docName = docName;
  }

  @Override
  public synchronized boolean equals(java.lang.Object obj) {
    if (!(obj instanceof FileTransfer)) {
      return false;
    }
    FileTransfer other = (FileTransfer) obj;
    if (obj == null) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    boolean _equals;
    _equals = true
      && ((this.docBytes == null && other.getDocBytes() == null)
        || (this.docBytes != null && java.util.Arrays.equals(this.docBytes, other.getDocBytes())))
      && ((this.docName == null && other.getDocName() == null)
        || (this.docName != null && this.docName.equals(other.getDocName())));
    __equalsCalc = null;
    return _equals;
  }

  /**
   * Gets the docBytes value for this FileTransfer.
   * 
   * @return docBytes
   */
  public byte[] getDocBytes() {
    return docBytes;
  }

  /**
   * Gets the docName value for this FileTransfer.
   * 
   * @return docName
   */
  public java.lang.String getDocName() {
    return docName;
  }

  @Override
  public synchronized int hashCode() {
    if (__hashCodeCalc) {
      return 0;
    }
    __hashCodeCalc = true;
    int _hashCode = 1;
    if (this.getDocBytes() != null) {
      for (int i = 0; i < java.lang.reflect.Array.getLength(this.getDocBytes()); i++) {
        java.lang.Object obj = java.lang.reflect.Array.get(this.getDocBytes(), i);
        if (obj != null && !obj.getClass().isArray()) {
          _hashCode += obj.hashCode();
        }
      }
    }
    if (this.getDocName() != null) {
      _hashCode += this.getDocName().hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }

  /**
   * Sets the docBytes value for this FileTransfer.
   * 
   * @param docBytes
   */
  public void setDocBytes(byte[] docBytes) {
    this.docBytes = docBytes;
  }

  /**
   * Sets the docName value for this FileTransfer.
   * 
   * @param docName
   */
  public void setDocName(java.lang.String docName) {
    this.docName = docName;
  }

}
