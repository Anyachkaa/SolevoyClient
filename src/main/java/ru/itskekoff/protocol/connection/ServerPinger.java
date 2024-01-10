package ru.itskekoff.protocol.connection;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.Data;
import ru.itskekoff.bots.Bot;
import ru.itskekoff.bots.proxy.ProxyBasic;
import ru.itskekoff.client.SolevoyClient;
import ru.itskekoff.client.module.impl.bots.BotMain;
import ru.itskekoff.protocol.codec.PacketCodec;
import ru.itskekoff.protocol.codec.VarInt21FrameCodec;
import ru.itskekoff.protocol.data.ConnectionState;
import ru.itskekoff.protocol.data.Session;
import ru.itskekoff.protocol.packet.Packet;
import ru.itskekoff.protocol.packet.PacketDirection;
import ru.itskekoff.utils.ChatUtil;
import ru.itskekoff.protocol.packet.impl.client.HandshakePacket;
import ru.itskekoff.protocol.packet.impl.client.status.ClientStatusRequestPacket;
import ru.itskekoff.protocol.packet.impl.server.status.ServerStatusResponsePacket;

import java.util.concurrent.TimeUnit;

public @Data class ServerPinger {
    private static final EventLoopGroup group = Epoll.isAvailable() ?
            new EpollEventLoopGroup(0) :
            new NioEventLoopGroup(0);
    private static final Class<? extends Channel> channelClass = Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class;
    private Session session;

    public void connect(Bot bot, String host, int port, ProxyBasic proxy) {
        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(channelClass)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        final ChannelPipeline pipeline = socketChannel.pipeline();
                        if (proxy.getUsername() != null) {
                            switch (proxy.getType()) {
                                case SOCKS4 ->
                                        pipeline.addFirst(new Socks4ProxyHandler(proxy.getAddress(), proxy.getUsername()));
                                case SOCKS5 ->
                                        pipeline.addFirst(new Socks5ProxyHandler(proxy.getAddress(), proxy.getUsername(), proxy.getPassword()));
                                case HTTP ->
                                        pipeline.addFirst(new HttpProxyHandler(proxy.getAddress(), proxy.getUsername(), proxy.getPassword()));
                            }
                        } else {
                            switch (proxy.getType()) {
                                case SOCKS4 -> pipeline.addFirst(new Socks4ProxyHandler(proxy.getAddress()));
                                case SOCKS5 -> pipeline.addFirst(new Socks5ProxyHandler(proxy.getAddress()));
                                case HTTP -> pipeline.addFirst(new HttpProxyHandler(proxy.getAddress()));
                            }
                        }
                        pipeline.addLast("timer", new ReadTimeoutHandler(30));
                        pipeline.addLast("frameCodec", new VarInt21FrameCodec());
                        pipeline.addLast("packetCodec", new PacketCodec(ConnectionState.STATUS, PacketDirection.CLIENTBOUND, false));
                        pipeline.addLast("handler", new SimpleChannelInboundHandler<Packet>() {
                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) {
                                session.getChannel().close();
                            }
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                if (BotMain.connect.isToggled())
                                    ChatUtil.sendChatMessage("[&b" + bot.getName() + "&f] Пингую сервер...", true);
                                TimeUnit.MILLISECONDS.sleep(150);
                                session.sendPacket(new HandshakePacket(session.getProtocolID(), host, port, 1));
                                session.sendPacket(new ClientStatusRequestPacket());
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
                                if (packet instanceof ServerStatusResponsePacket) {
                                    if (BotMain.chat.isToggled())
                                        ChatUtil.sendChatMessage("[&b" + bot.getName() + "&f] Пинганул сервер.", true);
                                    session.getChannel().close();
                                }
                            }
                        });
                    }
                });
        session = new Session(bootstrap.connect(host, port).syncUninterruptibly().channel());
        session.setProtocolID(340);
    }
}