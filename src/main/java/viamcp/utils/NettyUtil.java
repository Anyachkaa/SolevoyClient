package viamcp.utils;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import viamcp.handler.CommonTransformer;

public class NettyUtil {
    public static void decodeEncodePlacement(ChannelPipeline instance, String base, String newHandler, ChannelHandler handler) {
        switch (base) {
            case "decoder" -> {
                if (instance.get(CommonTransformer.HANDLER_DECODER_NAME) != null) {
                    base = CommonTransformer.HANDLER_DECODER_NAME;
                }

            }
            case "encoder" -> {
                if (instance.get(CommonTransformer.HANDLER_ENCODER_NAME) != null) {
                    base = CommonTransformer.HANDLER_ENCODER_NAME;
                }

            }
        }

        instance.addBefore(base, newHandler, handler);
    }
}
