package com.binarywang.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

/**
 * jar 操作工具类.
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 * @date 2021-02-01
 */
@UtilityClass
public class JarUtils {
    /**
     * @param jarPath    jar包所在绝对路径
     * @param destPath   配置文件在jar包里位置置config/sysconfig.properties
     * @param srcString  源字符串
     * @param destString 目标字符串，即被替换的字符串
     * @throws IOException .
     */
    public static void replaceStringInFile(String jarPath, String destPath, String srcString, String destString) throws IOException {
        File file = new File(jarPath);
        // 将jar文件名重命名为jarName_bak.jar
        final String bakJarPath = jarPath.substring(0, jarPath.lastIndexOf(File.separator)) + getJarName(jarPath) + "_bak.jar";
        File destFile = new File(bakJarPath);
        if (!file.renameTo(destFile)) {
            throw new RuntimeException("重命名文件失败");
        }

        try (JarFile jarFile = new JarFile(destFile);
             JarOutputStream out = new JarOutputStream(new FileOutputStream(file));) {
            jarFile.stream()
                    .filter(entry -> !destPath.equals(entry.getName()))
                    .forEach(entry -> {
                        try (InputStream inputStream = jarFile.getInputStream(entry);) {
                            out.putNextEntry(entry);
                            IOUtils.copy(inputStream, out);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            JarEntry jarEntry = new JarEntry(destPath);
            out.putNextEntry(jarEntry);

            final Optional<JarEntry> first = jarFile.stream().filter(entry -> destPath.equals(entry.getName())).findFirst();
            if (first.isPresent()) {
                final JarEntry replacedFile = first.get();
                try (InputStream in = jarFile.getInputStream(replacedFile)) {
                    IOUtils.write(IOUtils.toString(in, StandardCharsets.UTF_8).replace(srcString, destString),
                            out, StandardCharsets.UTF_8);
                }
            } else {
                throw new IllegalArgumentException("指定路径不对，请核实");
            }

            URL url = new URL("jar:file:" + bakJarPath + "!/" + destPath);
            JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
            try (InputStream in = jarConnection.getInputStream()) {
                IOUtils.write(IOUtils.toString(in, StandardCharsets.UTF_8).replace(srcString, destString),
                        out, StandardCharsets.UTF_8);
            }
        }

        //destFile.delete();
    }

    /**
     * @param jarPath    jar包所在绝对路径
     * @param sourcePath confPath配置文件绝对路径
     * @param destPath   配置文件jar包 位置config/sysconfig.properties
     * @throws IOException .
     */
    public static void replaceFile(String jarPath, String sourcePath, String destPath) throws IOException {
        File file = new File(jarPath);
        // 将jar文件名重命名为jarName_bak.jar
        File destFile = new File(jarPath.substring(0, jarPath.lastIndexOf(File.separator)) + getJarName(jarPath) + "_bak.jar");
        if (!file.renameTo(destFile)) {
            throw new RuntimeException("重命名文件失败");
        }

        try (JarFile jarFile = new JarFile(destFile);
             InputStream in = new FileInputStream(sourcePath);
             JarOutputStream out = new JarOutputStream(new FileOutputStream(file));) {
            jarFile.stream()
                    .filter(jarEntry -> !destPath.equals(jarEntry.getName()))
                    .forEach(jarEntry -> {
                        try (InputStream inputStream = jarFile.getInputStream(jarEntry);) {
                            out.putNextEntry(jarEntry);
                            copyFile(inputStream, out);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            JarEntry jarEntry = new JarEntry(destPath);
            out.putNextEntry(jarEntry);

            copyFile(in, out);
        }

        //destFile.delete();
    }

    private static String getJarName(String jarPath) {
        return jarPath.substring(jarPath.lastIndexOf(File.separator), jarPath.lastIndexOf("."));
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        int length = 2097152;
        byte[] buffer = new byte[length];
        int len = 0;
        while ((len = in.read(buffer)) > -1) {
            out.write(buffer, 0, len);
        }
    }
}
