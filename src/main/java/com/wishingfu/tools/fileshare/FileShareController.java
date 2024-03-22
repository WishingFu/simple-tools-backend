package com.wishingfu.tools.fileshare;

import com.wishingfu.tools.common.Result;
import com.wishingfu.tools.fileshare.model.LocalFile;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

@RestController
@RequestMapping("/api/file-share")
public class FileShareController {

    @Autowired
    private ShareFileManager manager;

    @GetMapping("/file/list")
    public Result<List<LocalFile>> fileList() {
        return Result.success(manager.selectAllFiles());
    }

    @GetMapping("/file/{fileId}")
    public Result<LocalFile> file(@PathVariable("fileId") String fileId) {
        return Result.success(manager.getFile(fileId));
    }

    @GetMapping(value = "/preview/{fileId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void previewFile(@PathVariable("fileId") String fileId, HttpServletResponse response) throws IOException {
        manager.previewFile(fileId, response);
    }

    @GetMapping(value = "/download/{fileId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadFile(@PathVariable("fileId") String fileId, HttpServletResponse response) throws IOException {
        manager.downloadFile(fileId, response);
    }

    @DeleteMapping("/files")
    public Result<Integer> deleteFiles(@RequestBody List<String> fileNames) {
        return Result.success(manager.deleteFiles(fileNames).intValue());
    }
}
