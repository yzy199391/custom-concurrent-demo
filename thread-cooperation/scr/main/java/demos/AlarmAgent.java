package demos;


import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * 以客户端向服务器报警类总结对
 * wait()/notify()
 *
 * @author yanzy
 * @date 2020-09-03 14:34
 */
@Data
public class AlarmAgent {

    public static final Logger log = (Logger) LoggerFactory.getLogger(AlarmAgent.class);

    /**
     * 单例
     */
    private static final AlarmAgent INSTANCE = new AlarmAgent();
    /**
     * 保护条件
     */
    private boolean isConnectedService = false;
    /**
     * 守护线程 - 心跳检测
     */
    private CustomHeartBeatThread heartBeatThread = new CustomHeartBeatThread();

    public static AlarmAgent getInstance() {
        return INSTANCE;
    }

    public void init() {
        connectToServer();
        heartBeatThread.setDaemon(true);
        heartBeatThread.start();
    }

    private void connectToServer() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    doConnect();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void doConnect() throws InterruptedException {
        Thread.sleep(500);
        synchronized (this) {
            isConnectedService = true;
            this.notify();
        }
    }

    public void sentAlarmMessage(String message) throws InterruptedException {
        synchronized (this) {
            while (!isConnectedService) {
                this.wait();
            }
        }
        //真实操作
        System.out.println("发送报警信息成功");
    }

    public class CustomHeartBeatThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                while (true) {
                    if (checkConnection()) {
                        isConnectedService = true;
                    } else {
                        isConnectedService = false;
                        log.info("链接断开");
                        connectToServer();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 模拟网络不稳定状态
     *
     * @return
     */
    private boolean checkConnection() {
        final Random random = new Random();
        return random.nextInt(100) > 50;
    }
}
