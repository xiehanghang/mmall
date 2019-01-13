package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Enzo Cotter on 2019/1/13.
 */
public interface IFileService {
    String upload(MultipartFile file, String path);
}
