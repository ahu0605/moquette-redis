/*
 * Copyright (c) 2012-2017 The original author or authorsgetRockQuestions()
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package sk.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;


/**
 *
 * @author andrea
 */
@Sharable
public class MQTTMessageLogger extends ChannelDuplexHandler {

    private static final Logger LOG = LoggerFactory.getLogger("messageLogger");

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {
        logMQTTMessage(ctx, message, "C->B");
        ctx.fireChannelRead(message);
    }

    private void logMQTTMessage(ChannelHandlerContext ctx, Object message, String direction) {
        if (!(message instanceof MqttMessage)) {
            return;
        }
        MqttMessage msg = (MqttMessage) message;
        String clientID = ctx.channel().toString();//NettyUtils.clientID(ctx.channel());
        switch (msg.fixedHeader().messageType()) {
            case CONNECT:
                MqttConnectMessage connect = (MqttConnectMessage) msg;
                LOG.info("{} CONNECT client <{}>", direction, connect.payload().clientIdentifier());
                break;
            case SUBSCRIBE:
                MqttSubscribeMessage subscribe = (MqttSubscribeMessage) msg;
                LOG.info("{} SUBSCRIBE <{}> to topics {}", direction, clientID, subscribe.payload().topicSubscriptions());
                break;
            case UNSUBSCRIBE:
                MqttUnsubscribeMessage unsubscribe = (MqttUnsubscribeMessage) msg;
                //TODO
                LOG.info("{} UNSUBSCRIBE <{}> to topics <{}>", direction, clientID, unsubscribe.payload().topics());
                break;
            case PUBLISH:
                MqttPublishMessage publish = (MqttPublishMessage) msg;
                LOG.info("{} PUBLISH <{}> to topics <{}>", direction, clientID, publish.variableHeader().topicName());
                break;
            case PUBREC:
            	MqttMessage pubrec = (MqttMessage) msg;
                LOG.info("{} PUBREC <{}> packetID <{}>", direction, clientID, ((MqttMessageIdVariableHeader)pubrec.variableHeader()).messageId());
                break;
            case PUBCOMP:
            	MqttMessage pubCompleted = (MqttMessage) msg;
                LOG.info("{} PUBCOMP <{}> packetID <{}>", direction, clientID, ((MqttMessageIdVariableHeader)pubCompleted.variableHeader()).messageId());
                break;
            case PUBREL:
            	MqttMessage pubRelease = (MqttMessage) msg;
                LOG.info("{} PUBREL <{}> packetID <{}>", direction, clientID,((MqttMessageIdVariableHeader)pubRelease.variableHeader()).messageId());
                break;
            case DISCONNECT:
                LOG.info("{} DISCONNECT <{}>", direction, clientID);
                break;
            case PUBACK:
                MqttPubAckMessage pubAck = (MqttPubAckMessage) msg;
                LOG.info("{} PUBACK <{}> packetID <{}>", direction, clientID, pubAck.variableHeader().messageId());
                break;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String clientID = ctx.channel().toString();//NettyUtils.clientID(ctx.channel());
        if (clientID != null && !clientID.isEmpty()) {
            LOG.info("Channel closed <{}>", clientID);
        }
        ctx.fireChannelInactive();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        logMQTTMessage(ctx, msg, "C<-B");
        ctx.write(msg, promise);
    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        if (cause instanceof CorruptedFrameException) {
//            //something goes bad with decoding
//            LOG.warn("Error decoding a packet, probably a bad formatted packet, message: " + cause.getMessage());
//        } else {
//            LOG.error("Ugly error on networking", cause);
//        }
//        ctx.close();
//    }
//
//    @Override
//    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
//        if (ctx.channel().isWritable()) {
//            m_processor.notifyChannelWritable(ctx.channel());
//        }
//        ctx.fireChannelWritabilityChanged();
//    }

}
