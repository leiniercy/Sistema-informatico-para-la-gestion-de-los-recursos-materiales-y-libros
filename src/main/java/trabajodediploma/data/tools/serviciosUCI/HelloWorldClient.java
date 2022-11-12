/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.data.tools.serviciosUCI;

import https.autenticacion2_uci_cu.v7.AutenticarUsuario;
import https.autenticacion2_uci_cu.v7.AutenticarUsuarioResponse;
import https.autenticacion2_uci_cu.v7.ObjectFactory;
import https.autenticacion2_uci_cu.v7.ObtenerPersonaDadoUsuario;
import https.autenticacion2_uci_cu.v7.ObtenerPersonaDadoUsuarioResponse;
import https.autenticacion2_uci_cu.v7.Persona;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 *
 * @author mapa
 */
@Component
public class HelloWorldClient {

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    public Persona sayHello(String usuario, String clave) {
        ObjectFactory factory = new ObjectFactory();
        AutenticarUsuario person = factory.createAutenticarUsuario();
//        person.setClave("Lis@2033.");
//        person.setUsuario("leiniercy");
//        person.setClave("wish11.gotfr*");
//        person.setUsuario("frankavl");
        person.setUsuario(usuario);
        person.setClave(clave);

        ObtenerPersonaDadoUsuario dadoUsuario = new ObtenerPersonaDadoUsuario();
//        dadoUsuario.setUsuario("arianlb");

        AceptAllSSLCertificate();

//        ObtenerPersonaDadoUsuarioResponse usuarioResponse = (ObtenerPersonaDadoUsuarioResponse) webServiceTemplate.marshalSendAndReceive(dadoUsuario);
        AutenticarUsuarioResponse res = (AutenticarUsuarioResponse) webServiceTemplate.marshalSendAndReceive(person);

        return res.getPersona();
//    return usuarioResponse.getPersona();
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
