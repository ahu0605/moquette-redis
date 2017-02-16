package sk.broker;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.ssl.SslContext;

public class MqttServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public MqttServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }

//        p.addLast(new ProtobufVarint32FrameDecoder());
//        p.addLast(new ProtobufDecoder(WorldClockProtocol.Locations.getDefaultInstance()));
//
//        p.addLast(new ProtobufVarint32LengthFieldPrepender());
//        p.addLast(new ProtobufEncoder());
//
//        p.addLast(new WorldClockServerHandler());
        
	      p.addLast(new MqttDecoder());
	      p.addLast(MqttEncoder.INSTANCE);
	      p.addLast(new MqttServerHandler());
	      
    }
}
