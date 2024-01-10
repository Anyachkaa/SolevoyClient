package ru.itskekoff.protocol.data;

import lombok.Data;
import lombok.Getter;
import ru.itskekoff.bots.proxy.ProxyBasic;
import ru.itskekoff.bots.proxy.ProxyType;

import java.net.Proxy;

@Data
public class ServerData {
    private final String ip;
    private final String host;

    private ProxyBasic proxy;
    private final int port;


    public ServerData(String ip, int port) {
        this.host = ip + ":" + port;
        this.ip = ip;
        this.port = port;
        this.proxy = new ProxyBasic(ProxyType.NO_PROXY);
    }

    public ServerData(String ip, int port, ProxyBasic proxy) {
        this.host = ip + ":" + port;
        this.ip = ip;
        this.port = port;
        this.proxy = proxy;
    }
}