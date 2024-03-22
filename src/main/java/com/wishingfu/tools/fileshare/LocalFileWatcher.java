package com.wishingfu.tools.fileshare;

import com.wishingfu.tools.fileshare.config.FileShareConfig;
import com.wishingfu.tools.fileshare.model.FileEvent;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static java.nio.file.StandardWatchEventKinds.*;

@Component
@Slf4j
public class LocalFileWatcher {

    @Autowired
    private FileShareConfig config;

    @Autowired
    private ShareFileManager manager;

    @Autowired
    private FileShareWebsocket websocket;

    private final WatchService watchService;

    private final Executor watchExecutor = Executors.newVirtualThreadPerTaskExecutor();

    public LocalFileWatcher() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            log.error("文件监听服务创建失败", e);
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    private void initFileWatcher() throws IOException {
        String folder = config.getFolder();
        Path path = Paths.get(folder);

        File watchFolder = path.toFile();
        if(!watchFolder.exists()) {
            watchFolder.mkdirs();
        }

        path.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        watchExecutor.execute(this::watchFolder);
    }

    private void watchFolder() {
        while(true) {
            WatchKey key = null;
            try {
                key = watchService.take();
                for(var event: key.pollEvents()) {
                    switch (event.kind().name()) {
                        case "ENTRY_CREATE" -> this.handleCreate((Path) event.context());
                        case "ENTRY_MODIFY" -> this.handleModify((Path) event.context());
                        case "ENTRY_DELETE" -> this.handleDelete((Path) event.context());
                        default -> log.info("Not processed event, kind: {}, context: {}, count: {}", event.kind(), event.context(), event.count());
                    }
                }
            } catch (InterruptedException e) {
                log.error("Watch service interrupted.");
                Thread.interrupted();
            } finally {
                if(key != null) {
                    key.reset();
                }
            }
        }
    }

    private void handleCreate(Path path) {
        log.info("New file crated, {}", path);
        manager.saveFile(path);
    }

    private void handleModify(Path path) {
        log.info("File modified, {}", path);
        // 暂时忽略
        websocket.sendEvent(FileEvent.modifiedEvent(manager.getFileId(path.toString())));
    }

    private void handleDelete(Path path) {
        log.info("File deleted, {}", path);
        manager.deleteFile(path);
    }
}
