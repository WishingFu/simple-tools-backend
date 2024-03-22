package com.wishingfu.tools.fileshare;

import com.wishingfu.tools.fileshare.config.FileShareConfig;
import com.wishingfu.tools.fileshare.mapper.LocalFileMapper;
import com.wishingfu.tools.fileshare.model.LocalFile;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@Component
public class ShareFileManager {

    @Autowired
    private LocalFileMapper mapper;

    @Autowired
    private FileShareConfig config;

    public LocalFile saveFile(Path path) {
        var file = path.toFile();

        LocalFile localFile = new LocalFile();
        localFile.setId(UUID.randomUUID().toString());
        localFile.setFileSize(file.length());
        localFile.setOriginName(file.getName());
        localFile.setUploadTime(LocalDateTime.now());

        this.mapper.insertFile(localFile);

        return localFile;
    }

    public boolean deleteFile(Path path) {
        String name = path.toFile().getName();
        path.toFile().delete();
        return deleteFile(name);
    }

    public boolean deleteFile(String name) {
        return mapper.deleteFile(name) == 1;
    }

    public List<LocalFile> selectAllFiles() {
        return mapper.selectAll();
    }

    public void previewFile(String fileId, HttpServletResponse response) throws IOException {
        LocalFile localFile = mapper.selectById(fileId);
        File f = new File(config.getFolder(), localFile.getOriginName());

        downloadFile(f, response.getOutputStream(), 0, f.length());
    }

    public void downloadFile(String fileId, HttpServletResponse response) throws IOException {
        LocalFile localFile = mapper.selectById(fileId);
        File f = new File(config.getFolder(), localFile.getOriginName());

        response.setHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(localFile.getOriginName(), StandardCharsets.UTF_8));
        response.setHeader("Content-Length", String.valueOf(f.length()));
        downloadFile(f, response.getOutputStream(), 0, f.length());
    }

    private void downloadFile(File file, OutputStream stream, long startAt, long size) {
        byte[] buffer = new byte[16 * 1024];
        try (FileInputStream fis = new FileInputStream(file)) {
            for(long index = startAt; index < startAt + size; index += 16 * 1024) {
                int read = fis.read(buffer, 0, 16 * 1024);
                if(read != -1) {
                    stream.write(buffer, 0, read);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public LocalFile getFile(String fileId) {
        return mapper.selectById(fileId);
    }

    public String getFileId(String fileName) {
        return mapper.selectByName(fileName).getId();
    }

    public Long deleteFiles(List<String> fileNames) {
        return fileNames.stream()
                .map(name -> Path.of(config.getFolder(), name))
                .map(this::deleteFile)
                .filter(b -> b)
                .count();
    }
}
