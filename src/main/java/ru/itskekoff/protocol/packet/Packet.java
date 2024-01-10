package ru.itskekoff.protocol.packet;

import lombok.Data;
import ru.itskekoff.protocol.Protocol;
import ru.itskekoff.protocol.codec.PacketBuffer;
import ru.itskekoff.protocol.packet.impl.CustomPacket;
import ru.itskekoff.utils.PacketUtil;
import ru.itskekoff.utils.ReflectionUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Packet implements Serializable {

    public abstract void write(PacketBuffer out, int protocol) throws Exception;

    public abstract void read(PacketBuffer in, int protocol) throws Exception;

    public abstract Protocol getProtocol();

    @Override
    public String toString() {
        return ReflectionUtil.objectToString(this);
    }

    @Data
    public static class Builder {
        private final List<CustomData> data = new ArrayList<>();

        public Builder init(CustomData... data) {
            this.data.addAll(Arrays.asList(data));

            return this;
        }

        public Builder add(DataType type, Object value) {
            data.add(new CustomData(type, value));

            return this;
        }

        public CustomPacket build(int id) {
            PacketBuffer buffer = PacketUtil.createEmptyPacketBuffer();

            for (CustomData customData : data) {
                switch (customData.getType()) {
                    case VARINT -> buffer.writeVarInt((Integer) customData.getValue());
                    case INT -> buffer.writeInt((Integer) customData.getValue());
                    case LONG -> buffer.writeLong((Long) customData.getValue());
                    case DOUBLE -> buffer.writeDouble((Double) customData.getValue());
                    case FLOAT -> buffer.writeFloat((Float) customData.getValue());
                    case BYTE -> buffer.writeByte((Byte) customData.getValue());
                    case SHORT -> buffer.writeShort((Short) customData.getValue());
                    case BOOLEAN -> buffer.writeBoolean((Boolean) customData.getValue());
                    case STRING -> buffer.writeString((String) customData.getValue());
                    case BYTES -> buffer.writeBytes((byte[]) customData.getValue());
                }
            }

            return new CustomPacket(id, buffer.readByteArray());
        }

        public enum DataType {
            VARINT, INT, LONG, DOUBLE, FLOAT, BYTE, SHORT, BOOLEAN, STRING, BYTES
        }

        @Data
        public static class CustomData {
            private final DataType type;
            private final Object value;
        }
    }
}