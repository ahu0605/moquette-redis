package sk.broker;

import java.util.Calendar;
import java.util.TimeZone;

import javax.xml.stream.Location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;

public class MqttServerHandler extends SimpleChannelInboundHandler<MqttMessage> {
	private static final Logger LOG = LoggerFactory.getLogger(MqttServerHandler.class);
    @Override
    public void channelRead0(ChannelHandlerContext ctx, MqttMessage mqttMessage) throws Exception {
        long currentTime = System.currentTimeMillis();
        LOG.info("Received a message of type {}", mqttMessage.fixedHeader().messageType().name());
        try {
            switch (mqttMessage.fixedHeader().messageType()) {
                case CONNECT:
                	ProtocolProcessor.connect(ctx,(MqttConnectMessage) mqttMessage);
                    break;
                case SUBSCRIBE:
                    //m_processor.processSubscribe(ctx.channel(), (MqttSubscribeMessage) msg);
                    break;
                case UNSUBSCRIBE:
                   // m_processor.processUnsubscribe(ctx.channel(), (MqttUnsubscribeMessage) msg);
                    break;
                case PUBLISH:
                   // m_processor.processPublish(ctx.channel(), (MqttPublishMessage) msg);
                    break;
                case PUBREC:
                   // m_processor.processPubRec(ctx.channel(), (MqttMessage) msg);
                    break;
                case PUBCOMP:
                   // m_processor.processPubComp(ctx.channel(), (MqttMessage) msg);
                    break;
                case PUBREL:
                    //m_processor.processPubRel(ctx.channel(), (MqttMessage) msg);
                    break;
                case DISCONNECT:
                   //m_processor.processDisconnect(ctx.channel());
                    break;
                case PUBACK:
                   // m_processor.processPubAck(ctx.channel(), (MqttPubAckMessage) msg);
                    break;
                case PINGREQ:
                    MqttMessage pingResp = new MqttMessage(new MqttFixedHeader(MqttMessageType.PINGRESP, 
                    															false, 
                    																MqttQoS.AT_MOST_ONCE, 
                    																	false, 
                    																		0),null);
                    ctx.writeAndFlush(pingResp);
                    break;
            }
        } catch (Exception ex) {
            LOG.error("Bad error in processing the message", ex);
            ctx.fireExceptionCaught(ex);
        }
        
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private static String toString(MqttMessage msg) {
        return msg.toString();
    }
}
