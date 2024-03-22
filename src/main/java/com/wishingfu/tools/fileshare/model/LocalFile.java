package com.wishingfu.tools.fileshare.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LocalFile {

    private String id;

    private String originName;

    private Long fileSize;

    private String sha256;

    private LocalDateTime uploadTime;

}
