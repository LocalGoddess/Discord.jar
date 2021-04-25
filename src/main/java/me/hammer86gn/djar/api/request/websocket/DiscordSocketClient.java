package me.hammer86gn.djar.api.request.websocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class DiscordSocketClient extends WebSocketClient {
    private final String token;

    private String sessionID;
    private long keepAlive;
    private boolean connectionAlive;

    public DiscordSocketClient(URI serverUri, String token) {
        super(serverUri);
        this.token = token;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        identify();



        connectionAlive = true;
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received Message: " + message);

        getKeepAlive(JsonParser.parseString(message).getAsJsonObject());

        heartbeat(JsonParser.parseString(message).getAsJsonObject());

    }

    private void getKeepAlive(JsonObject message) {
        if (message.get("t").toString().equals("null")) {
            keepAlive = message.get("d").getAsJsonObject().get("heartbeat_interval").getAsLong();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection Closed\n " +
                "{\n" +
                "Close Code: " + code +
                "\n" +
                "Close Reason: " + reason +
                "\nClosed By Remote: " + remote +
                "\n}" );

        connectionAlive = false;
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    private void identify() {
        JsonObject identifyPacket = new JsonObject();
        identifyPacket.addProperty("op",2);

        JsonObject identifyDetails = new JsonObject();
        identifyDetails.addProperty("token",token);
        identifyDetails.addProperty("intents",513); //Intents that Discord uses to allow you to do things found here: https://discord.com/developers/docs/topics/gateway#gateway-intents

        JsonObject detailsProperties = new JsonObject();
        detailsProperties.addProperty("$os","linux");
        detailsProperties.addProperty("$browser","djar");
        detailsProperties.addProperty("$device","djar");

        identifyDetails.add("properties",detailsProperties);
        identifyPacket.add("d",identifyDetails);

        this.send(identifyPacket.toString());
    }

    private void heartbeat(JsonObject message) {
        if (message.get("t").toString().equals("READY")) {
            sessionID = message.get("d").getAsJsonObject().get("user").getAsJsonObject().get("session_id").toString();

            new Thread(() -> {

                    JsonObject heartbeat = new JsonObject();

                    heartbeat.addProperty("op",1);
                    heartbeat.addProperty("d",System.currentTimeMillis() * System.currentTimeMillis());


                    this.send(heartbeat.toString());

                    try {
                        Thread.sleep(keepAlive);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

            },"CONNECTION-HEARTBEAT").start();
        }
    }

}