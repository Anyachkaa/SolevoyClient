package ru.itskekoff.bots;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
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
import io.netty.handler.timeout.*;
import ru.itskekoff.bots.chunks.CachedChunk;
import ru.itskekoff.bots.inventory.InventoryContainer;
import ru.itskekoff.bots.proxy.ProxyBasic;
import ru.itskekoff.client.SolevoyClient;
import ru.itskekoff.client.commands.impl.bot.bypass.BotBypassController;
import ru.itskekoff.client.module.impl.bots.BotBypass;
import ru.itskekoff.client.module.impl.bots.BotMain;
import ru.itskekoff.client.module.impl.bots.BotRegister;
import ru.itskekoff.event.EventManager;
import ru.itskekoff.event.impl.EventCaptchaReceived;
import ru.itskekoff.protocol.codec.PacketCodec;
import ru.itskekoff.protocol.codec.VarInt21FrameCodec;
import ru.itskekoff.protocol.connection.ServerPinger;
import ru.itskekoff.protocol.data.ConnectionState;
import ru.itskekoff.protocol.data.ServerData;
import ru.itskekoff.protocol.data.Session;
import ru.itskekoff.protocol.packet.Packet;
import ru.itskekoff.protocol.packet.PacketDirection;
import ru.itskekoff.protocol.packet.impl.client.HandshakePacket;
import ru.itskekoff.protocol.packet.impl.client.login.ClientLoginStartPacket;
import ru.itskekoff.protocol.packet.impl.client.play.*;
import ru.itskekoff.protocol.packet.impl.server.login.ServerLoginDisconnectPacket;
import ru.itskekoff.protocol.packet.impl.server.login.ServerLoginSetCompressionPacket;
import ru.itskekoff.protocol.packet.impl.server.login.ServerLoginSuccessPacket;
import ru.itskekoff.protocol.packet.impl.server.play.*;
import ru.itskekoff.utils.ChatUtil;
import ru.itskekoff.utils.ThreadUtils;
import ru.itskekoff.utils.notification.NotificationRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BotConnection {
    private static final EventLoopGroup group = Epoll.isAvailable() ?
            new EpollEventLoopGroup(6) :
            new NioEventLoopGroup(6);
    private static final Class<? extends Channel> channelClass = Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class;

    private final Bot bot;
    private final SolevoyClient client = SolevoyClient.getInstance();
    private boolean active = false;

    public BotConnection(Bot bot) {
        this.bot = bot;
    }

    public void connect(String ip, int port, ProxyBasic proxy) {
        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(channelClass)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        if (BotMain.proxy.getCurrentMode().equals("Private")) {
                            switch (proxy.getType()) {
                                case SOCKS4 ->
                                        pipeline.addFirst(new Socks4ProxyHandler(proxy.getAddress(), proxy.getUsername()));
                                case SOCKS5 ->
                                        pipeline.addFirst(new Socks5ProxyHandler(proxy.getAddress(), proxy.getUsername(), proxy.getPassword()));
                                case HTTP ->
                                        pipeline.addFirst(new HttpProxyHandler(proxy.getAddress(), proxy.getUsername(), proxy.getPassword()));
                            }
                        } else if (BotMain.proxy.getCurrentMode().equals("Public")) {
                            switch (proxy.getType()) {
                                case SOCKS4 -> pipeline.addFirst(new Socks4ProxyHandler(proxy.getAddress()));
                                case SOCKS5 -> pipeline.addFirst(new Socks5ProxyHandler(proxy.getAddress()));
                                case HTTP -> pipeline.addFirst(new HttpProxyHandler(proxy.getAddress()));
                            }
                        }
                        pipeline.addFirst("timer", new ReadTimeoutHandler(10));
                        pipeline.addLast("frameCodec", new VarInt21FrameCodec());
                        pipeline.addLast("packetCodec", new PacketCodec(ConnectionState.LOGIN, PacketDirection.CLIENTBOUND, false));
                        pipeline.addLast("handler", new SimpleChannelInboundHandler<Packet>() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) {
                                try {
                                    if (!client.isConnected()) {
                                        ctx.close();
                                        return;
                                    }
                                } catch (Exception ignored) {
                                    ctx.close();
                                    return;
                                }

                                Session session = new Session(ctx.channel());
                                session.setConnectionState(ConnectionState.LOGIN);
                                session.setUsername(bot.getName());
                                session.setProtocolID(340);
                                ServerData serverData = new ServerData(ip, port, proxy);
                                bot.setServerData(serverData);
                                bot.setSession(session);
                                bot.setBypassController(new BotBypassController(bot));
                                bot.getSession().sendPackets(
                                        new HandshakePacket(bot.getSession().getProtocolID(), ip, port, 2),
                                        new ClientLoginStartPacket(bot.getName())
                                );
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, Packet p) {
                                if (!client.isConnected()) {
                                    ctx.close();
                                    return;
                                }

                                if (p instanceof ServerJoinGamePacket || p instanceof ServerRespawnPacket) {
                                    bot.getOwnChunks().clear();
                                }

                                if (p instanceof ServerJoinGamePacket packet) {
                                    bot.setEntityID(packet.getEntityId());
                                    bot.getSession().sendPackets(
                                            new ClientSettingsPacket("ru_ru", (byte) 8, (byte) 0, true, (byte) 127),
                                            new ClientCustomPayloadPacket("MC|Brand", "vanilla".getBytes())
                                    );
                                } else if (p instanceof ServerPlayerPosLookPacket positionRotation) {

                                    bot.setX(positionRotation.getX());
                                    bot.setY(positionRotation.getY());
                                    bot.setZ(positionRotation.getZ());

                                    bot.setPitch(positionRotation.getPitch());
                                    bot.setYaw(positionRotation.getYaw());

                                    if (bot.isAreaLoaded(bot.getFloorX(), bot.getFloorY(), bot.getFloorZ())) {
                                        bot.getSession().sendPacket(new ClientPacketTeleportConfirm(positionRotation.getTeleport()));

                                        if (!active) {
                                            active = true;

                                            bot.getSession().sendPacket(new ClientPlayerPositionPacket(bot.getX(), bot.getY(), bot.getZ(), bot.isOnGround()));
                                            client.getBots().add(bot);

                                            if (BotMain.connect.isToggled())
                                                ChatUtil.sendChatMessage("[&b" + bot.getName() + "&f] Подключился.", true);
                                            NotificationRenderer.queue(bot.getName(), "Connected", 1);
                                            client.getBots().add(bot);
                                        }
                                    }
                                } else if (p instanceof ServerPacketChunkData) {
                                    ServerPacketChunkData packet = (ServerPacketChunkData) p;
                                    CachedChunk chunk = new CachedChunk(packet.getColumn());

                                    if (!bot.getClient().getCachedChunks().contains(chunk)) {
                                        bot.getClient().getCachedChunks().add(chunk);
                                    }

                                    if (!bot.ownChunks.contains(chunk)) {
                                        bot.ownChunks.add(bot.getClient().getCachedChunks().get(bot.getClient().getCachedChunks().indexOf(chunk)));
                                        bot.getClient().getCachedChunks().get(bot.getClient().getCachedChunks().indexOf(chunk)).getUsages().add(bot);
                                    }
                                } else if (p instanceof ServerPacketBlockChange) {
                                    ServerPacketBlockChange packet = (ServerPacketBlockChange) p;
                                    Position pos = packet.getRecord().getPosition();

                                    bot.setBlockAtPos(pos.getX(), pos.getY(), pos.getZ(), packet.getRecord().getBlock());
                                } else if (p instanceof ServerPacketMultiBlockChange) {
                                    ServerPacketMultiBlockChange packet = (ServerPacketMultiBlockChange) p;

                                    for (BlockChangeRecord record : packet.getRecords()) {
                                        Position pos = record.getPosition();
                                        bot.setBlockAtPos(pos.getX(), pos.getY(), pos.getZ(), record.getBlock());
                                    }
                                } else if (p instanceof ServerPacketChunkUnload) {
                                    ServerPacketChunkUnload unload = (ServerPacketChunkUnload) p;
                                    CachedChunk chunk = new CachedChunk(bot.getChunkAtPos(unload.getX(), unload.getZ()));

                                    bot.ownChunks.remove(chunk);
                                    bot.getClient().getCachedChunks().get(bot.getClient().getCachedChunks().indexOf(chunk)).getUsages().remove(bot);
                                } else if (p instanceof ServerChatPacket) {
                                    String message = ((ServerChatPacket) p).getMsg().getFullText();
                                    if (BotBypass.chat.isToggled() && !bot.getBypassController().isSolved()) {
                                        bot.getBypassController().solveCaptcha(message);
                                        bot.getBypassController().solveMath(message);
                                    }
                                    client.getBots().add(bot);
                                    if (!bot.isRegistered() && (message.contains("/reg") || message.contains("/login")
                                            || message.contains("авторизируйтесь") || message.contains("зарегистрируйтесь") || message.contains("войдите"))) {
                                        if (client.getModuleManager().getModuleByClass(BotRegister.class).isToggled()) {
                                            client.getTaskExecutor().execute(() -> {
                                                ThreadUtils.sleep(100L);
                                                bot.getSession().sendPacket(new ClientChatPacket(String.format("/register %s %1$s", BotRegister.botPassword.getCurrentText())));
                                                ThreadUtils.sleep(100L);
                                                bot.getSession().sendPacket(new ClientChatPacket(String.format("/login %s", BotRegister.botPassword.getCurrentText())));
                                                bot.setRegistered(true);

                                            });
                                        }

                                        return;
                                    }

                                    if (message.toLowerCase().contains("пройдена") || message.toLowerCase().contains("прошли")) {
                                        bot.captchaTries = 0;
                                    }

                                    if (BotMain.chat.isToggled())
                                        ChatUtil.sendChatMessage("[&b" + bot.getName() + "&f] Чат: &r" + message, true);
                                } else if (p instanceof ServerMapDataPacket) {
                                    EventCaptchaReceived eventCaptchaReceived = new EventCaptchaReceived((ServerMapDataPacket) p, bot);
                                    EventManager.call(eventCaptchaReceived);
                                } else if (p instanceof ServerLoginSetCompressionPacket) {
                                    bot.getSession().setCompressionThreshold(((ServerLoginSetCompressionPacket) p).getThreshold());
                                } else if (p instanceof ServerLoginSuccessPacket) {

                                    bot.getSession().setConnectionState(ConnectionState.PLAY);
                                } else if (p instanceof ServerDisconnectPacket) {
                                    if (BotMain.disconnect.isToggled())
                                        ChatUtil.sendChatMessage("[&b" + bot.getName() + "&f] Отключён (disconnect): &r" + ((ServerDisconnectPacket) p).getMessage().getFullText(), true);
                                    disconnectBot();
                                } else if (p instanceof ServerLoginDisconnectPacket) {
                                    if (BotMain.disconnect.isToggled())
                                        ChatUtil.sendChatMessage("[&b" + bot.getName() + "&f] Отключён (login disconnect): &r" + ((ServerLoginDisconnectPacket) p).getMessage().getFullText(), true);
                                    disconnectBot();
                                } else if (p instanceof ServerKeepAlivePacket) {
                                    bot.getSession().sendPacket(new ClientKeepAlivePacket(((ServerKeepAlivePacket) p).getKeepaliveId()));
                                    bot.setLastKeepAlive(System.currentTimeMillis());
                                } else if (p instanceof ServerOpenWindowPacket packet) {
                                    bot.setOpenContainer(new InventoryContainer(packet.getWindowId(), new ArrayList<>(packet.getSlots()), packet.getName()));
                                } else if (p instanceof ServerWindowItemsPacket packet) {
                                    if (packet.getWindowId() == 0) {
                                        bot.setInventory(new InventoryContainer(0, Arrays.stream(packet.getItemStacks()).collect(Collectors.toList()), "inventory"));
                                    }

                                    if (bot.getOpenContainer() != null && packet.getWindowId() == bot.getOpenContainer().getWindowID()) {
                                        bot.getOpenContainer().getItems().addAll(Arrays.stream(packet.getItemStacks()).toList());
                                        if (BotBypass.item.isToggled() && !bot.getBypassController().isSolved())
                                            bot.getBypassController().solveItemName(BotBypass.itemName.getCurrentText());
                                        if (BotBypass.differentItem.isToggled() && !bot.getBypassController().isSolved())
                                            bot.getBypassController().solveDifferent();
                                    }
                                } else if (p instanceof ServerSetSlotPacket packet) {

                                    if (packet.getWindowId() == 0) {
                                        bot.getInventory().getItems().set(packet.getSlot(), packet.getItem());
                                    }

                                    if (bot.getOpenContainer() != null && bot.getOpenContainer().getItems().size() < packet.getSlot() && packet.getWindowId() == bot.getOpenContainer().getWindowID()) {
                                        bot.getOpenContainer().getItems().set(packet.getSlot(), packet.getItem());
                                    }
                                } else if (p instanceof ServerCloseWindowPacket packet) {

                                    if (bot.getOpenContainer() != null && packet.getWindowId() == bot.getOpenContainer().getWindowID()) {
                                        bot.setOpenContainer(null);
                                    }
                                } else if (p instanceof ServerPacketExplosion packet) {
                                    if ((bot.getX() != packet.getMotionX() || bot.getX() != packet.getMotionY() || bot.getX() != packet.getMotionZ())) {
                                        bot.setMotionX(bot.getMotionX() + packet.getMotionX());
                                        bot.setMotionY(bot.getMotionY() + packet.getMotionY());
                                        bot.setMotionZ(bot.getMotionZ() + packet.getMotionZ());
                                    }
                                } else if (p instanceof ServerEntityVelocityPacket packet) {
                                    if (packet.getEntityID() == bot.getEntityID()) {
                                        bot.setMotionX(packet.getMotionX() / 8000D);
                                        bot.setMotionY(packet.getMotionY() / 8000D);
                                        bot.setMotionZ(packet.getMotionZ() / 8000D);
                                    }
                                }
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) {
                                ctx.close();
                                disconnectBot();
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                ctx.close();
                                if (client.getBots().contains(bot)) {
                                    client.getBots().remove(bot);
                                    if (BotMain.disconnect.isToggled())
                                        System.out.println(bot.getOwnChunks().size());
                                    ChatUtil.sendChatMessage("[&b" + bot.getName() + "&f] Отключён (Netty Exception): &c" + cause.getMessage().replace(" ", " &c"), true);
                                    disconnectBot();
                                }
                            }
                        });
                    }
                });

        if (BotBypass.ping.isToggled()) {
            ServerPinger pinger = new ServerPinger();
            pinger.connect(bot, ip, port, proxy);
            ThreadUtils.sleep(1000L);
        }
        bootstrap.connect(ip, port);
    }


    private void disconnectBot() {
        if (BotBypass.rejoin.isToggled()) {
            if (bot.getSession().isDisconnectedManually()) return;
            if (!bot.isRejoined()) {
                ServerData serverData = bot.getServerData();
                ThreadUtils.sleep((long) BotBypass.rejoinDelay.getCurrent() * 1000); // convert to sec & sleep
                connect(serverData.getIp(), serverData.getPort(), serverData.getProxy());
                bot.setRejoined(true);
            }
        }
        bot.onDisconnect();
    }

}