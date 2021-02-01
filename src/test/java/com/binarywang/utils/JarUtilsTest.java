package com.binarywang.utils;


import org.testng.annotations.Test;

import java.io.IOException;

/**
 * 单元测试.
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 * @date 2021-02-01
 */
public class JarUtilsTest {

    @Test
    void replaceFile() throws IOException {
        String jarPath = "D:\\config.jar";
        String sourcePath = "config/application-dev.yml";
        String destPath = "BOOT-INF/classes/config/application-dev.yml";
        JarUtils.replaceFile(jarPath, sourcePath, destPath);
    }

    @Test
    void replaceStringInFile() throws IOException {
        String jarPath = "D:\\config.jar";
        String destPath = "BOOT-INF/classes/config/application-dev.yml";
        JarUtils.replaceStringInFile(jarPath, destPath, "10.23.23.104", "99999999999999999999");
    }
}
