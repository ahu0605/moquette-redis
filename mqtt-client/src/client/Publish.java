package client;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

public class Publish {
	public static SSLSocketFactory configureSSLSocketFactory() throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, KeyManagementException, CertificateException, IOException {
	    KeyStore ks = KeyStore.getInstance("JKS");
	    InputStream jksInputStream =Thread.currentThread().getContextClassLoader().getResourceAsStream("clientkeystore.jks");
	    ks.load(jksInputStream, "456789".toCharArray());

	    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
	    kmf.init(ks, "987654".toCharArray());

	    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
	    tmf.init(ks);

	    SSLContext sc = SSLContext.getInstance("TLS");
	    TrustManager[] trustManagers = tmf.getTrustManagers();
	    sc.init(kmf.getKeyManagers(), trustManagers, null);

	    SSLSocketFactory ssf = sc.getSocketFactory();
	    return ssf;
	}
	
	public static void main(String[] args) throws MqttException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
		
		Properties property = new Properties();
		
		property.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
		
		MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence("c:/tmpMqtt/");

		MqttClient client = new MqttClient("ssl://"+property.getProperty("host")+":"+property.getProperty("port"), "SSLClientTest", dataStore);
		SSLSocketFactory ssf = configureSSLSocketFactory();
		MqttConnectOptions options = new MqttConnectOptions();
		options.setSocketFactory(ssf);
		client.connect(options);	
		MqttMessage msg = new MqttMessage();
		msg.setPayload("test".getBytes());
		msg.setQos(1);
		client.publish("testTopic",msg);
		client.close();
	}
}
