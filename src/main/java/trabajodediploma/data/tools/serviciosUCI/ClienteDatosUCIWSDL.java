/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.data.tools.serviciosUCI;

import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import services.Autenticar;
import services.AutenticarResponse;
import services.ObjectFactory;
import services.ObtenerEstructura;
import services.ObtenerEstructuraResponse;
import services.ObtenerPersonaDadoUsuario;
import services.ObtenerPersonaDadoUsuarioResponse;
import services.ObtenerPersonasDadoIdEstructura;
import services.ObtenerPersonasDadoIdEstructuraCredencial;
import services.ObtenerPersonasDadoIdEstructuraCredencialResponse;
import services.ObtenerPersonasDadoIdEstructuraResponse;
/**
 *
 * @author mapa
 */
public class ClienteDatosUCIWSDL extends WebServiceGatewaySupport {

    public ClienteDatosUCIWSDL() {
        super();
        AceptAllSSLCertificate();
    }
    
    public AutenticarResponse autenticar(String usuario, String pass) {
        
        ObjectFactory factory = new ObjectFactory();
        Autenticar autenticar = factory.createAutenticar();
        
        autenticar.setPassword(factory.createAutenticarPassword(pass));
        autenticar.setUser(factory.createAutenticarUser(usuario));
        
        AutenticarResponse response = (AutenticarResponse) getWebServiceTemplate().marshalSendAndReceive(autenticar);
        return response;
    }
    
    public ObtenerPersonaDadoUsuarioResponse obtenerPersonaDadoUsuario(String usuario) {
        
        ObjectFactory factory = new ObjectFactory();
        ObtenerPersonaDadoUsuario p = factory.createObtenerPersonaDadoUsuario();
        
        p.setUser(factory.createObtenerPersonaDadoUsuarioUser(usuario));
        
        ObtenerPersonaDadoUsuarioResponse response = (ObtenerPersonaDadoUsuarioResponse) getWebServiceTemplate().marshalSendAndReceive(p);
        return response;
    }
    public ObtenerPersonasDadoIdEstructuraCredencialResponse ObtenerPersonasDadoIdEstructuraCredencialResponse(Integer i) {
        
        ObjectFactory factory = new ObjectFactory();
        ObtenerPersonasDadoIdEstructuraCredencial p = factory.createObtenerPersonasDadoIdEstructuraCredencial();
        
        p.setIdEstructuraCredencial(i);
        
        
        ObtenerPersonasDadoIdEstructuraCredencialResponse response = (ObtenerPersonasDadoIdEstructuraCredencialResponse) getWebServiceTemplate().marshalSendAndReceive(p);
        return response;
    }
    
    public ObtenerEstructuraResponse obtenerEstructura() {
        
        ObjectFactory factory = new ObjectFactory();
        ObtenerEstructura e = factory.createObtenerEstructura();
        
        ObtenerEstructuraResponse response = (ObtenerEstructuraResponse) getWebServiceTemplate().marshalSendAndReceive(e);
        return response;
    }
    
    public ObtenerPersonasDadoIdEstructuraResponse personasDadoArea(Integer idEstructura) {
        ObjectFactory factory = new ObjectFactory();
        ObtenerPersonasDadoIdEstructura e = factory.createObtenerPersonasDadoIdEstructura();
        e.setIdEstructura(idEstructura);
        
        ObtenerPersonasDadoIdEstructuraResponse response = (ObtenerPersonasDadoIdEstructuraResponse) getWebServiceTemplate().marshalSendAndReceive(e);
        
        return response;
    }
 

    private static void AceptAllSSLCertificate() {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

// Install the all-trusting trust manager
        try {

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

// Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
