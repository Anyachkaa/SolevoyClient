package ru.itskekoff.utils;

import io.netty.channel.ChannelHandlerContext;

public class ChannelUtils {
    public static void close(ChannelHandlerContext ctx) {
        ctx.executor().shutdownGracefully();
        ctx.pipeline().close();
        ctx.channel().close();
        ctx.close();
    }
}
