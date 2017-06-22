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

public class MarloPortBindingStub extends org.apache.axis.client.Stub
  implements org.cgiar.ccafs.marlo.ocs.ws.client.WSMarlo {

  static org.apache.axis.description.OperationDesc[] _operations;

  static {
    _operations = new org.apache.axis.description.OperationDesc[6];
    _initOperationDesc1();
  }

  private static void _initOperationDesc1() {
    org.apache.axis.description.OperationDesc oper;
    org.apache.axis.description.ParameterDesc param;
    oper = new org.apache.axis.description.OperationDesc();
    oper.setName("getMarloAgreements");
    param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "agreementId"),
      org.apache.axis.description.ParameterDesc.IN,
      new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false,
      false);
    param.setOmittable(true);
    oper.addParameter(param);
    oper.setReturnType(new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "tWsMarloAgree"));
    oper.setReturnClass(org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgree[].class);
    oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
    oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
    oper.setUse(org.apache.axis.constants.Use.LITERAL);
    _operations[0] = oper;

    oper = new org.apache.axis.description.OperationDesc();
    oper.setName("getMarloAgreeCrp");
    param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "agreementId"),
      org.apache.axis.description.ParameterDesc.IN,
      new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false,
      false);
    param.setOmittable(true);
    oper.addParameter(param);
    oper.setReturnType(new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "tWsMarloAgreeCrp"));
    oper.setReturnClass(org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCrp[].class);
    oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
    oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
    oper.setUse(org.apache.axis.constants.Use.LITERAL);
    _operations[1] = oper;

    oper = new org.apache.axis.description.OperationDesc();
    oper.setName("getMarloAgreeCountry");
    param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "agreementId"),
      org.apache.axis.description.ParameterDesc.IN,
      new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false,
      false);
    param.setOmittable(true);
    oper.addParameter(param);
    oper
      .setReturnType(new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "tWsMarloAgreeCountry"));
    oper.setReturnClass(org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCountry[].class);
    oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
    oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
    oper.setUse(org.apache.axis.constants.Use.LITERAL);
    _operations[2] = oper;

    oper = new org.apache.axis.description.OperationDesc();
    oper.setName("getMarloPla");
    param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "agreementId"),
      org.apache.axis.description.ParameterDesc.IN,
      new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false,
      false);
    param.setOmittable(true);
    oper.addParameter(param);
    oper.setReturnType(new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "tWsMarloPla"));
    oper.setReturnClass(org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPla[].class);
    oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
    oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
    oper.setUse(org.apache.axis.constants.Use.LITERAL);
    _operations[3] = oper;

    oper = new org.apache.axis.description.OperationDesc();
    oper.setName("getMarloPlaCountry");
    param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "plaId"),
      org.apache.axis.description.ParameterDesc.IN,
      new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false,
      false);
    param.setOmittable(true);
    oper.addParameter(param);
    oper.setReturnType(new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "tWsMarloPlaCountry"));
    oper.setReturnClass(org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPlaCountry[].class);
    oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
    oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
    oper.setUse(org.apache.axis.constants.Use.LITERAL);
    _operations[4] = oper;

    oper = new org.apache.axis.description.OperationDesc();
    oper.setName("getMarloAgreeDocument");
    param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "agreementId"),
      org.apache.axis.description.ParameterDesc.IN,
      new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false,
      false);
    param.setOmittable(true);
    oper.addParameter(param);
    oper.setReturnType(new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "fileTransfer"));
    oper.setReturnClass(org.cgiar.ccafs.marlo.ocs.ws.client.FileTransfer[].class);
    oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
    oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
    oper.setUse(org.apache.axis.constants.Use.LITERAL);
    _operations[5] = oper;

  }

  private java.util.Vector cachedSerClasses = new java.util.Vector();

  private java.util.Vector cachedSerQNames = new java.util.Vector();

  private java.util.Vector cachedSerFactories = new java.util.Vector();

  private java.util.Vector cachedDeserFactories = new java.util.Vector();

  public MarloPortBindingStub() throws org.apache.axis.AxisFault {
    this(null);
  }

  public MarloPortBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service)
    throws org.apache.axis.AxisFault {
    this(service);
    super.cachedEndpoint = endpointURL;
  }

  public MarloPortBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
    if (service == null) {
      super.service = new org.apache.axis.client.Service();
    } else {
      super.service = service;
    }
    ((org.apache.axis.client.Service) super.service).setTypeMappingVersion("1.2");
    java.lang.Class cls;
    javax.xml.namespace.QName qName;
    javax.xml.namespace.QName qName2;
    java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
    java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
    java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
    java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
    java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
    java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
    java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
    java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
    java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
    java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
    qName = new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "fileTransfer");
    cachedSerQNames.add(qName);
    cls = org.cgiar.ccafs.marlo.ocs.ws.client.FileTransfer.class;
    cachedSerClasses.add(cls);
    cachedSerFactories.add(beansf);
    cachedDeserFactories.add(beandf);

    qName = new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "tWsMarloAgree");
    cachedSerQNames.add(qName);
    cls = org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgree.class;
    cachedSerClasses.add(cls);
    cachedSerFactories.add(beansf);
    cachedDeserFactories.add(beandf);

    qName = new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "tWsMarloAgreeCountry");
    cachedSerQNames.add(qName);
    cls = org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCountry.class;
    cachedSerClasses.add(cls);
    cachedSerFactories.add(beansf);
    cachedDeserFactories.add(beandf);

    qName = new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "tWsMarloAgreeCountryId");
    cachedSerQNames.add(qName);
    cls = org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCountryId.class;
    cachedSerClasses.add(cls);
    cachedSerFactories.add(beansf);
    cachedDeserFactories.add(beandf);

    qName = new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "tWsMarloAgreeCrp");
    cachedSerQNames.add(qName);
    cls = org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCrp.class;
    cachedSerClasses.add(cls);
    cachedSerFactories.add(beansf);
    cachedDeserFactories.add(beandf);

    qName = new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "tWsMarloAgreeCrpId");
    cachedSerQNames.add(qName);
    cls = org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCrpId.class;
    cachedSerClasses.add(cls);
    cachedSerFactories.add(beansf);
    cachedDeserFactories.add(beandf);

    qName = new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "tWsMarloPla");
    cachedSerQNames.add(qName);
    cls = org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPla.class;
    cachedSerClasses.add(cls);
    cachedSerFactories.add(beansf);
    cachedDeserFactories.add(beandf);

    qName = new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "tWsMarloPlaCountry");
    cachedSerQNames.add(qName);
    cls = org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPlaCountry.class;
    cachedSerClasses.add(cls);
    cachedSerFactories.add(beansf);
    cachedDeserFactories.add(beandf);

    qName = new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "tWsMarloPlaCountryId");
    cachedSerQNames.add(qName);
    cls = org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPlaCountryId.class;
    cachedSerClasses.add(cls);
    cachedSerFactories.add(beansf);
    cachedDeserFactories.add(beandf);

  }

  protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
    try {
      org.apache.axis.client.Call _call = super._createCall();
      if (super.maintainSessionSet) {
        _call.setMaintainSession(super.maintainSession);
      }
      if (super.cachedUsername != null) {
        _call.setUsername(super.cachedUsername);
      }
      if (super.cachedPassword != null) {
        _call.setPassword(super.cachedPassword);
      }
      if (super.cachedEndpoint != null) {
        _call.setTargetEndpointAddress(super.cachedEndpoint);
      }
      if (super.cachedTimeout != null) {
        _call.setTimeout(super.cachedTimeout);
      }
      if (super.cachedPortName != null) {
        _call.setPortName(super.cachedPortName);
      }
      java.util.Enumeration keys = super.cachedProperties.keys();
      while (keys.hasMoreElements()) {
        java.lang.String key = (java.lang.String) keys.nextElement();
        _call.setProperty(key, super.cachedProperties.get(key));
      }
      // All the type mapping information is registered
      // when the first call is made.
      // The type mapping information is actually registered in
      // the TypeMappingRegistry of the service, which
      // is the reason why registration is only needed for the first call.
      synchronized (this) {
        if (this.firstCall()) {
          // must set encoding style before registering serializers
          _call.setEncodingStyle(null);
          for (int i = 0; i < cachedSerFactories.size(); ++i) {
            java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
            javax.xml.namespace.QName qName = (javax.xml.namespace.QName) cachedSerQNames.get(i);
            java.lang.Object x = cachedSerFactories.get(i);
            if (x instanceof Class) {
              java.lang.Class sf = (java.lang.Class) cachedSerFactories.get(i);
              java.lang.Class df = (java.lang.Class) cachedDeserFactories.get(i);
              _call.registerTypeMapping(cls, qName, sf, df, false);
            } else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
              org.apache.axis.encoding.SerializerFactory sf =
                (org.apache.axis.encoding.SerializerFactory) cachedSerFactories.get(i);
              org.apache.axis.encoding.DeserializerFactory df =
                (org.apache.axis.encoding.DeserializerFactory) cachedDeserFactories.get(i);
              _call.registerTypeMapping(cls, qName, sf, df, false);
            }
          }
        }
      }
      return _call;
    } catch (java.lang.Throwable _t) {
      throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
    }
  }

  @Override
  public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCountry[] getMarloAgreeCountry(java.lang.String agreementId)
    throws java.rmi.RemoteException {
    if (super.cachedEndpoint == null) {
      throw new org.apache.axis.NoEndPointException();
    }
    org.apache.axis.client.Call _call = this.createCall();
    _call.setOperation(_operations[2]);
    _call.setUseSOAPAction(true);
    _call.setSOAPActionURI("");
    _call.setEncodingStyle(null);
    _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
    _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
    _call.setOperationName(
      new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloAgreeCountry"));

    this.setRequestHeaders(_call);
    this.setAttachments(_call);
    try {
      java.lang.Object _resp = _call.invoke(new java.lang.Object[] {agreementId});

      if (_resp instanceof java.rmi.RemoteException) {
        throw (java.rmi.RemoteException) _resp;
      } else {
        this.extractAttachments(_call);
        try {
          return (org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCountry[]) _resp;
        } catch (java.lang.Exception _exception) {
          return (org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCountry[]) org.apache.axis.utils.JavaUtils
            .convert(_resp, org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCountry[].class);
        }
      }
    } catch (org.apache.axis.AxisFault axisFaultException) {
      throw axisFaultException;
    }
  }

  @Override
  public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCrp[] getMarloAgreeCrp(java.lang.String agreementId)
    throws java.rmi.RemoteException {
    if (super.cachedEndpoint == null) {
      throw new org.apache.axis.NoEndPointException();
    }
    org.apache.axis.client.Call _call = this.createCall();
    _call.setOperation(_operations[1]);
    _call.setUseSOAPAction(true);
    _call.setSOAPActionURI("");
    _call.setEncodingStyle(null);
    _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
    _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
    _call
      .setOperationName(new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloAgreeCrp"));

    this.setRequestHeaders(_call);
    this.setAttachments(_call);
    try {
      java.lang.Object _resp = _call.invoke(new java.lang.Object[] {agreementId});

      if (_resp instanceof java.rmi.RemoteException) {
        throw (java.rmi.RemoteException) _resp;
      } else {
        this.extractAttachments(_call);
        try {
          return (org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCrp[]) _resp;
        } catch (java.lang.Exception _exception) {
          return (org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCrp[]) org.apache.axis.utils.JavaUtils.convert(_resp,
            org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCrp[].class);
        }
      }
    } catch (org.apache.axis.AxisFault axisFaultException) {
      throw axisFaultException;
    }
  }

  @Override
  public org.cgiar.ccafs.marlo.ocs.ws.client.FileTransfer[] getMarloAgreeDocument(java.lang.String agreementId)
    throws java.rmi.RemoteException {
    if (super.cachedEndpoint == null) {
      throw new org.apache.axis.NoEndPointException();
    }
    org.apache.axis.client.Call _call = this.createCall();
    _call.setOperation(_operations[5]);
    _call.setUseSOAPAction(true);
    _call.setSOAPActionURI("");
    _call.setEncodingStyle(null);
    _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
    _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
    _call.setOperationName(
      new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloAgreeDocument"));

    this.setRequestHeaders(_call);
    this.setAttachments(_call);
    try {
      java.lang.Object _resp = _call.invoke(new java.lang.Object[] {agreementId});

      if (_resp instanceof java.rmi.RemoteException) {
        throw (java.rmi.RemoteException) _resp;
      } else {
        this.extractAttachments(_call);
        try {
          return (org.cgiar.ccafs.marlo.ocs.ws.client.FileTransfer[]) _resp;
        } catch (java.lang.Exception _exception) {
          return (org.cgiar.ccafs.marlo.ocs.ws.client.FileTransfer[]) org.apache.axis.utils.JavaUtils.convert(_resp,
            org.cgiar.ccafs.marlo.ocs.ws.client.FileTransfer[].class);
        }
      }
    } catch (org.apache.axis.AxisFault axisFaultException) {
      throw axisFaultException;
    }
  }

  @Override
  public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgree[] getMarloAgreements(java.lang.String agreementId)
    throws java.rmi.RemoteException {
    if (super.cachedEndpoint == null) {
      throw new org.apache.axis.NoEndPointException();
    }
    org.apache.axis.client.Call _call = this.createCall();
    _call.setOperation(_operations[0]);
    _call.setUseSOAPAction(true);
    _call.setSOAPActionURI("");
    _call.setEncodingStyle(null);
    _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
    _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
    _call.setOperationName(
      new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloAgreements"));

    this.setRequestHeaders(_call);
    this.setAttachments(_call);
    try {
      java.lang.Object _resp = _call.invoke(new java.lang.Object[] {agreementId});

      if (_resp instanceof java.rmi.RemoteException) {
        throw (java.rmi.RemoteException) _resp;
      } else {
        this.extractAttachments(_call);
        try {
          return (org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgree[]) _resp;
        } catch (java.lang.Exception _exception) {
          return (org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgree[]) org.apache.axis.utils.JavaUtils.convert(_resp,
            org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgree[].class);
        }
      }
    } catch (org.apache.axis.AxisFault axisFaultException) {
      throw axisFaultException;
    }
  }

  @Override
  public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPla[] getMarloPla(java.lang.String agreementId)
    throws java.rmi.RemoteException {
    if (super.cachedEndpoint == null) {
      throw new org.apache.axis.NoEndPointException();
    }
    org.apache.axis.client.Call _call = this.createCall();
    _call.setOperation(_operations[3]);
    _call.setUseSOAPAction(true);
    _call.setSOAPActionURI("");
    _call.setEncodingStyle(null);
    _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
    _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
    _call.setOperationName(new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloPla"));

    this.setRequestHeaders(_call);
    this.setAttachments(_call);
    try {
      java.lang.Object _resp = _call.invoke(new java.lang.Object[] {agreementId});

      if (_resp instanceof java.rmi.RemoteException) {
        throw (java.rmi.RemoteException) _resp;
      } else {
        this.extractAttachments(_call);
        try {
          return (org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPla[]) _resp;
        } catch (java.lang.Exception _exception) {
          return (org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPla[]) org.apache.axis.utils.JavaUtils.convert(_resp,
            org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPla[].class);
        }
      }
    } catch (org.apache.axis.AxisFault axisFaultException) {
      throw axisFaultException;
    }
  }

  @Override
  public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPlaCountry[] getMarloPlaCountry(java.lang.String plaId)
    throws java.rmi.RemoteException {
    if (super.cachedEndpoint == null) {
      throw new org.apache.axis.NoEndPointException();
    }
    org.apache.axis.client.Call _call = this.createCall();
    _call.setOperation(_operations[4]);
    _call.setUseSOAPAction(true);
    _call.setSOAPActionURI("");
    _call.setEncodingStyle(null);
    _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
    _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
    _call.setOperationName(
      new javax.xml.namespace.QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloPlaCountry"));

    this.setRequestHeaders(_call);
    this.setAttachments(_call);
    try {
      java.lang.Object _resp = _call.invoke(new java.lang.Object[] {plaId});

      if (_resp instanceof java.rmi.RemoteException) {
        throw (java.rmi.RemoteException) _resp;
      } else {
        this.extractAttachments(_call);
        try {
          return (org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPlaCountry[]) _resp;
        } catch (java.lang.Exception _exception) {
          return (org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPlaCountry[]) org.apache.axis.utils.JavaUtils
            .convert(_resp, org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPlaCountry[].class);
        }
      }
    } catch (org.apache.axis.AxisFault axisFaultException) {
      throw axisFaultException;
    }
  }

}
