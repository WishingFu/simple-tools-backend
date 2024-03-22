package com.wishingfu.tools.fileshare;

import com.wishingfu.tools.fileshare.model.FileEvent;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
@ServerEndpoint("/ws/file-share")
public class FileShareWebsocket {

    private final List<Session> clients = new CopyOnWriteArrayList<>();

    public void sendEvent(FileEvent event) {
        clients.forEach(s -> {
            try {
                s.getBasicRemote().sendText(event.toJsonString());
            } catch (IOException ignored) {
            }
        });
    }

    @OnOpen
    public void onOpen(Session session) {
        log.info("Websocket connected.");
        this.clients.add(session);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("On message, {}", message);
    }

    @OnClose
    public void onClose(Session session) {
        log.info("Websocket connected closed.");
        this.clients.remove(session);
    }
}
