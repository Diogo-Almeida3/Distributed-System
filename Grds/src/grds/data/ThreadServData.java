package grds.data;

public class ThreadServData extends Thread {
    private boolean exit = false;
    private Integer numTimeouts;
    private boolean timeout = false;

    public ThreadServData(Integer numTimeouts) {
        this.numTimeouts = numTimeouts;
    }

    public boolean isTimeout() {
        return timeout;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }

    @Override
    public void run() {
        while (!exit) {
            try {
                Thread.sleep(20 * 1000);
            } catch (InterruptedException e) {}
            synchronized (numTimeouts) {numTimeouts++;}
            if (numTimeouts >= 3) {
                timeout = true;
                break;
            }
        }
    }
}
