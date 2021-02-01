package com.binarywang.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 入口
 *
 * @author Binary Wang
 */
@Slf4j
public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            log.error("参数个数不对，请核实");
            return;
        }

        JarUtils.replaceStringInFile(args[0], args[1], args[2], args[3]);
    }

}
