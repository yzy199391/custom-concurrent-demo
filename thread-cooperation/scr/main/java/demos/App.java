package demos;

/**
 * desc
 *
 * @author yanzy
 * @date 2020-09-03 17:23
 */
public class App {
    public static void main(String[] args) {
        final AlarmAgent alarmAgent = AlarmAgent.getInstance();
        alarmAgent.init();
        for (int i = 0; i < 100; i++) {
            new Thread(new Runnable() {
                String message = Thread.currentThread().getName();
                public void run() {
                    try {
                        alarmAgent.sentAlarmMessage(message);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }
}


