package grds.data;

public class ThreadServData extends Thread {
    private boolean exit = false;
    private int numTimeouts = 0;
    private boolean timeout = false;


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
                numTimeouts++;
            } catch (InterruptedException e) { numTimeouts = 0; }
            if (numTimeouts >= 3) {
                timeout = true;
                break;
            }
        }
    }

    public int getNumTimeouts() {
        return numTimeouts;
    }
}
