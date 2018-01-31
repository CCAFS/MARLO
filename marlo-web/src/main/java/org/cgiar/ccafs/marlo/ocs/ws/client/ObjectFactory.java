
package org.cgiar.ccafs.marlo.ocs.ws.client;


import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the org.cgiar.ccafs.marlo.ocs.ws.client package.
 * <p>
 * An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups. Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

  private final static QName _GetMarloPlaResponse_QNAME =
    new QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloPlaResponse");
  private final static QName _GetMarloPlaCountry_QNAME =
    new QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloPlaCountry");
  private final static QName _GetMarloAgreementsResponse_QNAME =
    new QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloAgreementsResponse");
  private final static QName _GetMarloAgreeCountry_QNAME =
    new QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloAgreeCountry");
  private final static QName _GetMarloAgreements_QNAME =
    new QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloAgreements");
  private final static QName _GetMarloPlaCountryResponse_QNAME =
    new QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloPlaCountryResponse");
  private final static QName _GetMarloAgreeCountryResponse_QNAME =
    new QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloAgreeCountryResponse");
  private final static QName _GetMarloAgreeCrp_QNAME =
    new QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloAgreeCrp");
  private final static QName _GetMarloPla_QNAME = new QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloPla");
  private final static QName _GetMarloAgreeCrpResponse_QNAME =
    new QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloAgreeCrpResponse");
  private final static QName _GetMarloResStudiesResponse_QNAME =
    new QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloResStudiesResponse");
  private final static QName _GetMarloResourceInformationResponse_QNAME =
    new QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloResourceInformationResponse");
  private final static QName _GetMarloListValues_QNAME =
    new QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloListValues");
  private final static QName _GetMarloListValuesResponse_QNAME =
    new QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloListValuesResponse");
  private final static QName _GetMarloServiceStatus_QNAME =
    new QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloServiceStatus");
  private final static QName _GetMarloResourceInformation_QNAME =
    new QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloResourceInformation");
  private final static QName _GetMarloServiceStatusResponse_QNAME =
    new QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloServiceStatusResponse");
  private final static QName _GetMarloResStudies_QNAME =
    new QName("http://logic.control.abw.ciat.cgiar.org/", "getMarloResStudies");

  /**
   * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package:
   * org.cgiar.ccafs.marlo.ocs.ws.client
   */
  public ObjectFactory() {
  }

  /**
   * Create an instance of {@link GetMarloAgreeCountry }
   */
  public GetMarloAgreeCountry createGetMarloAgreeCountry() {
    return new GetMarloAgreeCountry();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link GetMarloAgreeCountry }{@code >}}
   */
  @XmlElementDecl(namespace = "http://logic.control.abw.ciat.cgiar.org/", name = "getMarloAgreeCountry")
  public JAXBElement<GetMarloAgreeCountry> createGetMarloAgreeCountry(GetMarloAgreeCountry value) {
    return new JAXBElement<GetMarloAgreeCountry>(_GetMarloAgreeCountry_QNAME, GetMarloAgreeCountry.class, null, value);
  }

  /**
   * Create an instance of {@link GetMarloAgreeCountryResponse }
   */
  public GetMarloAgreeCountryResponse createGetMarloAgreeCountryResponse() {
    return new GetMarloAgreeCountryResponse();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link GetMarloAgreeCountryResponse }{@code >}}
   */
  @XmlElementDecl(namespace = "http://logic.control.abw.ciat.cgiar.org/", name = "getMarloAgreeCountryResponse")
  public JAXBElement<GetMarloAgreeCountryResponse>
    createGetMarloAgreeCountryResponse(GetMarloAgreeCountryResponse value) {
    return new JAXBElement<GetMarloAgreeCountryResponse>(_GetMarloAgreeCountryResponse_QNAME,
      GetMarloAgreeCountryResponse.class, null, value);
  }

  /**
   * Create an instance of {@link GetMarloAgreeCrp }
   */
  public GetMarloAgreeCrp createGetMarloAgreeCrp() {
    return new GetMarloAgreeCrp();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link GetMarloAgreeCrp }{@code >}}
   */
  @XmlElementDecl(namespace = "http://logic.control.abw.ciat.cgiar.org/", name = "getMarloAgreeCrp")
  public JAXBElement<GetMarloAgreeCrp> createGetMarloAgreeCrp(GetMarloAgreeCrp value) {
    return new JAXBElement<GetMarloAgreeCrp>(_GetMarloAgreeCrp_QNAME, GetMarloAgreeCrp.class, null, value);
  }

  /**
   * Create an instance of {@link GetMarloAgreeCrpResponse }
   */
  public GetMarloAgreeCrpResponse createGetMarloAgreeCrpResponse() {
    return new GetMarloAgreeCrpResponse();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link GetMarloAgreeCrpResponse }{@code >}}
   */
  @XmlElementDecl(namespace = "http://logic.control.abw.ciat.cgiar.org/", name = "getMarloAgreeCrpResponse")
  public JAXBElement<GetMarloAgreeCrpResponse> createGetMarloAgreeCrpResponse(GetMarloAgreeCrpResponse value) {
    return new JAXBElement<GetMarloAgreeCrpResponse>(_GetMarloAgreeCrpResponse_QNAME, GetMarloAgreeCrpResponse.class,
      null, value);
  }

  /**
   * Create an instance of {@link GetMarloAgreements }
   */
  public GetMarloAgreements createGetMarloAgreements() {
    return new GetMarloAgreements();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link GetMarloAgreements }{@code >}}
   */
  @XmlElementDecl(namespace = "http://logic.control.abw.ciat.cgiar.org/", name = "getMarloAgreements")
  public JAXBElement<GetMarloAgreements> createGetMarloAgreements(GetMarloAgreements value) {
    return new JAXBElement<GetMarloAgreements>(_GetMarloAgreements_QNAME, GetMarloAgreements.class, null, value);
  }

  /**
   * Create an instance of {@link GetMarloAgreementsResponse }
   */
  public GetMarloAgreementsResponse createGetMarloAgreementsResponse() {
    return new GetMarloAgreementsResponse();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link GetMarloAgreementsResponse }{@code >}}
   */
  @XmlElementDecl(namespace = "http://logic.control.abw.ciat.cgiar.org/", name = "getMarloAgreementsResponse")
  public JAXBElement<GetMarloAgreementsResponse> createGetMarloAgreementsResponse(GetMarloAgreementsResponse value) {
    return new JAXBElement<GetMarloAgreementsResponse>(_GetMarloAgreementsResponse_QNAME,
      GetMarloAgreementsResponse.class, null, value);
  }

  /**
   * Create an instance of {@link GetMarloListValues }
   */
  public GetMarloListValues createGetMarloListValues() {
    return new GetMarloListValues();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link GetMarloListValues }{@code >}}
   */
  @XmlElementDecl(namespace = "http://logic.control.abw.ciat.cgiar.org/", name = "getMarloListValues")
  public JAXBElement<GetMarloListValues> createGetMarloListValues(GetMarloListValues value) {
    return new JAXBElement<GetMarloListValues>(_GetMarloListValues_QNAME, GetMarloListValues.class, null, value);
  }

  /**
   * Create an instance of {@link GetMarloListValuesResponse }
   */
  public GetMarloListValuesResponse createGetMarloListValuesResponse() {
    return new GetMarloListValuesResponse();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link GetMarloListValuesResponse }{@code >}}
   */
  @XmlElementDecl(namespace = "http://logic.control.abw.ciat.cgiar.org/", name = "getMarloListValuesResponse")
  public JAXBElement<GetMarloListValuesResponse> createGetMarloListValuesResponse(GetMarloListValuesResponse value) {
    return new JAXBElement<GetMarloListValuesResponse>(_GetMarloListValuesResponse_QNAME,
      GetMarloListValuesResponse.class, null, value);
  }

  /**
   * Create an instance of {@link GetMarloPla }
   */
  public GetMarloPla createGetMarloPla() {
    return new GetMarloPla();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link GetMarloPla }{@code >}}
   */
  @XmlElementDecl(namespace = "http://logic.control.abw.ciat.cgiar.org/", name = "getMarloPla")
  public JAXBElement<GetMarloPla> createGetMarloPla(GetMarloPla value) {
    return new JAXBElement<GetMarloPla>(_GetMarloPla_QNAME, GetMarloPla.class, null, value);
  }

  /**
   * Create an instance of {@link GetMarloPlaCountry }
   */
  public GetMarloPlaCountry createGetMarloPlaCountry() {
    return new GetMarloPlaCountry();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link GetMarloPlaCountry }{@code >}}
   */
  @XmlElementDecl(namespace = "http://logic.control.abw.ciat.cgiar.org/", name = "getMarloPlaCountry")
  public JAXBElement<GetMarloPlaCountry> createGetMarloPlaCountry(GetMarloPlaCountry value) {
    return new JAXBElement<GetMarloPlaCountry>(_GetMarloPlaCountry_QNAME, GetMarloPlaCountry.class, null, value);
  }

  /**
   * Create an instance of {@link GetMarloPlaCountryResponse }
   */
  public GetMarloPlaCountryResponse createGetMarloPlaCountryResponse() {
    return new GetMarloPlaCountryResponse();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link GetMarloPlaCountryResponse }{@code >}}
   */
  @XmlElementDecl(namespace = "http://logic.control.abw.ciat.cgiar.org/", name = "getMarloPlaCountryResponse")
  public JAXBElement<GetMarloPlaCountryResponse> createGetMarloPlaCountryResponse(GetMarloPlaCountryResponse value) {
    return new JAXBElement<GetMarloPlaCountryResponse>(_GetMarloPlaCountryResponse_QNAME,
      GetMarloPlaCountryResponse.class, null, value);
  }

  /**
   * Create an instance of {@link GetMarloPlaResponse }
   */
  public GetMarloPlaResponse createGetMarloPlaResponse() {
    return new GetMarloPlaResponse();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link GetMarloPlaResponse }{@code >}}
   */
  @XmlElementDecl(namespace = "http://logic.control.abw.ciat.cgiar.org/", name = "getMarloPlaResponse")
  public JAXBElement<GetMarloPlaResponse> createGetMarloPlaResponse(GetMarloPlaResponse value) {
    return new JAXBElement<GetMarloPlaResponse>(_GetMarloPlaResponse_QNAME, GetMarloPlaResponse.class, null, value);
  }

  /**
   * Create an instance of {@link GetMarloResourceInformation }
   */
  public GetMarloResourceInformation createGetMarloResourceInformation() {
    return new GetMarloResourceInformation();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link GetMarloResourceInformation }{@code >}}
   */
  @XmlElementDecl(namespace = "http://logic.control.abw.ciat.cgiar.org/", name = "getMarloResourceInformation")
  public JAXBElement<GetMarloResourceInformation> createGetMarloResourceInformation(GetMarloResourceInformation value) {
    return new JAXBElement<GetMarloResourceInformation>(_GetMarloResourceInformation_QNAME,
      GetMarloResourceInformation.class, null, value);
  }

  /**
   * Create an instance of {@link GetMarloResourceInformationResponse }
   */
  public GetMarloResourceInformationResponse createGetMarloResourceInformationResponse() {
    return new GetMarloResourceInformationResponse();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link GetMarloResourceInformationResponse }{@code >}}
   */
  @XmlElementDecl(namespace = "http://logic.control.abw.ciat.cgiar.org/", name = "getMarloResourceInformationResponse")
  public JAXBElement<GetMarloResourceInformationResponse>
    createGetMarloResourceInformationResponse(GetMarloResourceInformationResponse value) {
    return new JAXBElement<GetMarloResourceInformationResponse>(_GetMarloResourceInformationResponse_QNAME,
      GetMarloResourceInformationResponse.class, null, value);
  }

  /**
   * Create an instance of {@link GetMarloResStudies }
   */
  public GetMarloResStudies createGetMarloResStudies() {
    return new GetMarloResStudies();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link GetMarloResStudies }{@code >}}
   */
  @XmlElementDecl(namespace = "http://logic.control.abw.ciat.cgiar.org/", name = "getMarloResStudies")
  public JAXBElement<GetMarloResStudies> createGetMarloResStudies(GetMarloResStudies value) {
    return new JAXBElement<GetMarloResStudies>(_GetMarloResStudies_QNAME, GetMarloResStudies.class, null, value);
  }

  /**
   * Create an instance of {@link GetMarloResStudiesResponse }
   */
  public GetMarloResStudiesResponse createGetMarloResStudiesResponse() {
    return new GetMarloResStudiesResponse();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link GetMarloResStudiesResponse }{@code >}}
   */
  @XmlElementDecl(namespace = "http://logic.control.abw.ciat.cgiar.org/", name = "getMarloResStudiesResponse")
  public JAXBElement<GetMarloResStudiesResponse> createGetMarloResStudiesResponse(GetMarloResStudiesResponse value) {
    return new JAXBElement<GetMarloResStudiesResponse>(_GetMarloResStudiesResponse_QNAME,
      GetMarloResStudiesResponse.class, null, value);
  }

  /**
   * Create an instance of {@link GetMarloServiceStatus }
   */
  public GetMarloServiceStatus createGetMarloServiceStatus() {
    return new GetMarloServiceStatus();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link GetMarloServiceStatus }{@code >}}
   */
  @XmlElementDecl(namespace = "http://logic.control.abw.ciat.cgiar.org/", name = "getMarloServiceStatus")
  public JAXBElement<GetMarloServiceStatus> createGetMarloServiceStatus(GetMarloServiceStatus value) {
    return new JAXBElement<GetMarloServiceStatus>(_GetMarloServiceStatus_QNAME, GetMarloServiceStatus.class, null,
      value);
  }

  /**
   * Create an instance of {@link GetMarloServiceStatusResponse }
   */
  public GetMarloServiceStatusResponse createGetMarloServiceStatusResponse() {
    return new GetMarloServiceStatusResponse();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link GetMarloServiceStatusResponse }{@code >}}
   */
  @XmlElementDecl(namespace = "http://logic.control.abw.ciat.cgiar.org/", name = "getMarloServiceStatusResponse")
  public JAXBElement<GetMarloServiceStatusResponse>
    createGetMarloServiceStatusResponse(GetMarloServiceStatusResponse value) {
    return new JAXBElement<GetMarloServiceStatusResponse>(_GetMarloServiceStatusResponse_QNAME,
      GetMarloServiceStatusResponse.class, null, value);
  }

  /**
   * Create an instance of {@link TWsAgldimvalue }
   */
  public TWsAgldimvalue createTWsAgldimvalue() {
    return new TWsAgldimvalue();
  }

  /**
   * Create an instance of {@link TWsAgldimvalueId }
   */
  public TWsAgldimvalueId createTWsAgldimvalueId() {
    return new TWsAgldimvalueId();
  }

  /**
   * Create an instance of {@link TWsMarloAgree }
   */
  public TWsMarloAgree createTWsMarloAgree() {
    return new TWsMarloAgree();
  }

  /**
   * Create an instance of {@link TWsMarloAgreeCountry }
   */
  public TWsMarloAgreeCountry createTWsMarloAgreeCountry() {
    return new TWsMarloAgreeCountry();
  }

  /**
   * Create an instance of {@link TWsMarloAgreeCountryId }
   */
  public TWsMarloAgreeCountryId createTWsMarloAgreeCountryId() {
    return new TWsMarloAgreeCountryId();
  }

  /**
   * Create an instance of {@link TWsMarloAgreeCrp }
   */
  public TWsMarloAgreeCrp createTWsMarloAgreeCrp() {
    return new TWsMarloAgreeCrp();
  }

  /**
   * Create an instance of {@link TWsMarloAgreeCrpId }
   */
  public TWsMarloAgreeCrpId createTWsMarloAgreeCrpId() {
    return new TWsMarloAgreeCrpId();
  }

  /**
   * Create an instance of {@link TWsMarloPla }
   */
  public TWsMarloPla createTWsMarloPla() {
    return new TWsMarloPla();
  }

  /**
   * Create an instance of {@link TWsMarloPlaCountry }
   */
  public TWsMarloPlaCountry createTWsMarloPlaCountry() {
    return new TWsMarloPlaCountry();
  }

  /**
   * Create an instance of {@link TWsMarloPlaCountryId }
   */
  public TWsMarloPlaCountryId createTWsMarloPlaCountryId() {
    return new TWsMarloPlaCountryId();
  }

  /**
   * Create an instance of {@link TWsMarloResourceInfo }
   */
  public TWsMarloResourceInfo createTWsMarloResourceInfo() {
    return new TWsMarloResourceInfo();
  }

  /**
   * Create an instance of {@link TWsMarloResStudies }
   */
  public TWsMarloResStudies createTWsMarloResStudies() {
    return new TWsMarloResStudies();
  }

  /**
   * Create an instance of {@link TWsMarloResStudiesId }
   */
  public TWsMarloResStudiesId createTWsMarloResStudiesId() {
    return new TWsMarloResStudiesId();
  }

}
