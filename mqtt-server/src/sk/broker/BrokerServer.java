package sk.broker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Properties;
import javax.net.ssl.KeyManagerFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import sk.broker.uitls.IdWorker;

public class BrokerServer {
  
    private static final KeyManagerFactory kmf;
    static final String  res;
    static final Properties properties = new Properties();
    static final boolean SSL = System.getProperty("ssl") != null;
    static final IdWorker IDWORKER = IdWorker.getIntance(); 
    
    static{
    	
    	res = System.getProperty("res");
    	InputStream is;
		try {
			is = new FileInputStream(new File(res));
			properties.load(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 try{
	         KeyStore ks = KeyStore.getInstance("JKS");
	         ks.load(new FileInputStream(properties.getProperty("jks_path")), properties.getProperty("key_store_password").toCharArray());
//	            KeyStore tks = KeyStore.getInstance("JKS");
//	            tks.load(new FileInputStream(properties.getProperty("jks_path")), properties.getProperty("key_store_password").toCharArray());

	            // Set up key manager factory to use our key store
	          kmf = KeyManagerFactory.getInstance("SunX509");
//	            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
	          kmf.init(ks, properties.getProperty("key_manager_password").toCharArray());
//	            tmf.init(tks);

	            // Initialize the SSLContext to work with our key managers.
	            // SERVER_CONTEXT = SSLContext.getInstance(PROTOCOL);
	            // SERVER_CONTEXT.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
	      }catch (Exception e) {
	            throw new Error("Failed to initialize the server-side SSLContext", e);
	      }
    
    }
    
    public static Properties getConfig(){
    	return properties;
    }
    
    public static IdWorker getIdWorker(){
    	return IDWORKER;
    }
    
    public static void main(String[] args) throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (!SSL) {
            sslCtx = null;
        }else{
        	sslCtx = SslContextBuilder.forServer(kmf).build();
        }
        
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new MqttServerInitializer(sslCtx));

             b.bind(Integer.parseInt(properties.getProperty("ssl_port"))).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
