package cn.afternode;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Logger {
    private final StringBuilder sb = new StringBuilder();

    public void println(String msg) {
        System.out.println(msg);
        sb.append(msg).append("\n");
    }

    public void writeToFile(File tgt) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(tgt.toPath()));
        bos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        bos.close();
    }
}
