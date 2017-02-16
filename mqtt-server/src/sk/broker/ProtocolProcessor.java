package sk.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;

public class ProtocolProcessor {
	private static final Logger LOG = LoggerFactory.getLogger(ProtocolProcessor.class);
	
	public static final int VERSION_3_1 = 3;
			
	public static final int VERSION_3_1_1 = 4; 
	
	public static void connect(ChannelHandlerContext ctx,MqttConnectMessage connectMessage){
		
		LOG.info("CONNECT for client <{}>", connectMessage.payload().clientIdentifier());
		MqttFixedHeader header = new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.EXACTLY_ONCE, false, 0);
		MqttConnAckVariableHeader varaibleHeader = null;
		MqttConnAckMessage ackMessage = null;
		String randomIdentifier = null;
		final String clientId;
		
		if(!validate(connectMessage.variableHeader().version())){
			
			varaibleHeader = new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION,false);
			ackMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(header, varaibleHeader, null);
		
		}
		if(validate(connectMessage
						.payload()
							.clientIdentifier())){
			// TODO
			if(!connectMessage.variableHeader().isCleanSession()) {
				
				varaibleHeader = new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED,false);
				ackMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(header, varaibleHeader, null);
  
            }
			
			randomIdentifier = Long.toHexString(BrokerServer.getIdWorker().nextId());
			
		}
		//LOGIN
		if(randomIdentifier != null)
			clientId = randomIdentifier;
		else 
			clientId = connectMessage.payload().clientIdentifier();
		
	}
	
	public static void disConnect(Channel channel,MqttConnectMessage mqttMessage){
		
	}
	
	public static boolean validate(int version){
		
		if(version != VERSION_3_1 && VERSION_3_1 != version)
			return false;
		else
			return true;
		
	}
	
	public static boolean validate(String clientIdentifier){
		
		if(clientIdentifier != null && clientIdentifier.length() != 0){
			return true;
		}else{
			return false;
		}
		
	}
	
}
