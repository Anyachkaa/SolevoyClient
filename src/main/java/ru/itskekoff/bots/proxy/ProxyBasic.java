package ru.itskekoff.bots.proxy;

import lombok.Data;

import java.net.InetSocketAddress;

@Data
public class ProxyBasic {
    private ProxyType type;
    private InetSocketAddress address;
    private String host;
    private int port;
    private String username, password;

    public ProxyBasic(ProxyType type, String host, int port, String username, String password) {
        this.type = type;
        this.address = new InetSocketAddress(host, port);
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public ProxyBasic(ProxyType type, String host, int port) {
        this.type = type;
        this.address = new InetSocketAddress(host, port);
        this.host = host;
        this.port = port;
        this.username = null;
        this.password = null;
    }

    public ProxyBasic(ProxyType type) {
        this.type = type;
        this.host = null;
        this.port = 0;
        this.address = null;
        this.username = null;
        this.password = null;
    }
}
