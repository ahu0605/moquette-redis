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

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

public class Starter {
	
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
		
		MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(property.getProperty("tmpDir"));

		final MqttClient client = new MqttClient("ssl://"+property.getProperty("host")+":"+property.getProperty("port"), "SSLClientTest", dataStore);
		SSLSocketFactory ssf = configureSSLSocketFactory();
		MqttConnectOptions options = new MqttConnectOptions();
		options.setSocketFactory(ssf);
		client.connect(options);	
		System.out.println("client connect success");
		client.subscribe("testTopic");
		client.setCallback(new MqttCallback(){

			@Override
			public void connectionLost(Throwable arg0) {
				// TODO Auto-generated method stub
				System.out.println("had losted");
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void messageArrived(String arg0, MqttMessage arg1
					
					) throws Exception {
				// TODO Auto-generated method stub
				System.out.println(new String(arg1.getPayload()));
			}
			
		});
		
//		new Thread(){
//			public void run(){
//				while(true){
//					
//					MqttMessage msg = new MqttMessage();
//					msg.setPayload("test".getBytes());
//					msg.setQos(1);
//					try {
//						client.publish("testTopic",msg);
//					} catch (MqttPersistenceException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (MqttException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				  try {
//					Thread.currentThread().sleep(2000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				}
//			}
//		}.start();
		
		
		
	}
}
