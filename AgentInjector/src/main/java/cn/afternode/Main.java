package cn.afternode;

import com.sun.tools.attach.*;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;

public class Main {
    // 我是孙笑川
    public static void main(String[] args) {
        Logger logger = new Logger();

        boolean mainFatal = false;
        int code = 1;

        try {
            code = main0(args, logger);
        } catch (Throwable t) {
            mainFatal = true;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintWriter pw = new PrintWriter(baos);
            t.printStackTrace(pw);

            t.printStackTrace();

            String stack = baos.toString();
            logger.println("[Fatal] Uncaught exception " + t.getClass().getName() + ": " + t.getMessage());
            logger.println(stack);
        }

        try {
            logger.println("[INFO] 主函数退出码为 " + code);

            File log = new File("injector.log");
            logger.writeToFile(log);

            if (mainFatal) System.exit(1);

            System.exit(code);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(100);
        }
    }

    public static int main0(String[] args, Logger log) {
        if (args.length != 1) {
            System.exit(-1);
        }

        // 获取参数
        String agentPath = args[0];

        File agent = new File(agentPath);
        if (!agent.exists() || !agent.isFile()) {
            log.println("[ERROR] 找不到Agent文件");
            return -2;
        }

        boolean injected = false;
        log.println("[INFO] 找到以下虚拟机");
        for (VirtualMachineDescriptor desc: VirtualMachine.list()) {
            log.println("[INFO] " + desc.displayName());
            if (desc.displayName().startsWith("net.minecraft.")) {
                try {
                    VirtualMachine vm = desc.provider().attachVirtualMachine(desc);
                    vm.loadAgentPath(agent.getAbsolutePath());
                } catch (Throwable t) {
                    log.println("[ERROR] 注入失败：" + t.getClass().getName() + ": " + t.getMessage());
                    return 200;
                }
            }
        }

        if (!injected) {
            log.println("[ERROR] 注入失败，找不到Minecraft所在的JVM");
            return 2;
        }

        return 0;
    }
}