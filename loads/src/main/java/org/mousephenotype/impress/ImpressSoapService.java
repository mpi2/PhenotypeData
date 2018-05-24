
package org.mousephenotype.impress;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "ImpressSoapService", targetNamespace = "http://www.mousephenotype.org/impress/soap/server", wsdlLocation = "https://www.mousephenotype.org/impress/soap/server?wsdlclients")
public class ImpressSoapService
    extends Service
{

    private final static URL IMPRESSSOAPSERVICE_WSDL_LOCATION;
    private final static WebServiceException IMPRESSSOAPSERVICE_EXCEPTION;
    private final static QName IMPRESSSOAPSERVICE_QNAME = new QName("http://www.mousephenotype.org/impress/soap/server", "ImpressSoapService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("https://www.mousephenotype.org/impress/soap/server?wsdlclients");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        IMPRESSSOAPSERVICE_WSDL_LOCATION = url;
        IMPRESSSOAPSERVICE_EXCEPTION = e;
    }

    public ImpressSoapService() {
        super(__getWsdlLocation(), IMPRESSSOAPSERVICE_QNAME);
    }

    public ImpressSoapService(WebServiceFeature... features) {
        super(__getWsdlLocation(), IMPRESSSOAPSERVICE_QNAME, features);
    }

    public ImpressSoapService(URL wsdlLocation) {
        super(wsdlLocation, IMPRESSSOAPSERVICE_QNAME);
    }

    public ImpressSoapService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, IMPRESSSOAPSERVICE_QNAME, features);
    }

    public ImpressSoapService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ImpressSoapService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns ImpressSoapPort
     */
    @WebEndpoint(name = "ImpressSoapPort")
    public ImpressSoapPort getImpressSoapPort() {
        return super.getPort(new QName("http://www.mousephenotype.org/impress/soap/server", "ImpressSoapPort"), ImpressSoapPort.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ImpressSoapPort
     */
    @WebEndpoint(name = "ImpressSoapPort")
    public ImpressSoapPort getImpressSoapPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://www.mousephenotype.org/impress/soap/server", "ImpressSoapPort"), ImpressSoapPort.class, features);
    }

    private static URL __getWsdlLocation() {
        if (IMPRESSSOAPSERVICE_EXCEPTION!= null) {
            throw IMPRESSSOAPSERVICE_EXCEPTION;
        }
        return IMPRESSSOAPSERVICE_WSDL_LOCATION;
    }

}