/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.data.tools.serviciosUCI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 *
 * @author mapa
 */
@Configuration
public class ClienteUCIWSDLConfig {

    private Jaxb2Marshaller jaxb2Marshaller(String url) {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setContextPath(url);

        return jaxb2Marshaller;
    }
    
    @Bean
    public ClienteDatosUCIWSDL clienteDatosUCIWSDL() {
        Jaxb2Marshaller marshaller = jaxb2Marshaller("services");
        ClienteDatosUCIWSDL client = new ClienteDatosUCIWSDL();
        client.setDefaultUri("http://10.0.0.195:9763/services/PasarelaWS.PasarelaWSHttpSoap11Endpoint/");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
    
    @Bean
    public ClienteAutenticacionUCIWSDL clienteAutenticacionUCIWSDL() {
        Jaxb2Marshaller marshaller = jaxb2Marshaller("https.autenticacion2_uci_cu.v7");
        ClienteAutenticacionUCIWSDL client = new ClienteAutenticacionUCIWSDL();
        client.setDefaultUri("https://autenticacion2.uci.cu/v7/");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
    
}
