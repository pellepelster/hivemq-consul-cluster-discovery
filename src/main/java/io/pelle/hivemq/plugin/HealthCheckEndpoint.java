package io.pelle.hivemq.plugin;

import com.hivemq.spi.callback.CallbackPriority;
import com.hivemq.spi.callback.events.broker.OnBrokerReady;
import com.hivemq.spi.callback.events.broker.OnBrokerStart;
import com.hivemq.spi.callback.events.broker.OnBrokerStop;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class HealthCheckEndpoint implements OnBrokerReady, OnBrokerStart, OnBrokerStop {

    private enum BROKER_STATUS { STARTING, READY, STOPPING, UNKNOWN };

    private BROKER_STATUS status = BROKER_STATUS.UNKNOWN;

    private class HealthCheckHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {

            String response = status.toString();
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }


    @Override
    public int priority() {
        return CallbackPriority.MEDIUM;
    }

    public HealthCheckEndpoint() {
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(9080), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.createContext("/healthcheck", new HealthCheckHandler());
        server.setExecutor(null);
        server.start();
    }


    @Override
    public void onBrokerReady() {
        this.status = BROKER_STATUS.READY;
    }

    @Override
    public void onBrokerStart() {
        this.status = BROKER_STATUS.STARTING;
    }

    @Override
    public void onBrokerStop() {
        this.status = BROKER_STATUS.STOPPING;
    }
}