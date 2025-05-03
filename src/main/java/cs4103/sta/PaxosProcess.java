package cs4103.sta;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class PaxosProcess {

    static protected AtomicInteger nextProcessId = new AtomicInteger();
    private final int processId;
    protected String processType;
    private float unreliability = 0.0f;
    protected int poolId;


    PaxosProcess(int poolId) {
        this.processId = nextProcessId.getAndIncrement();
        this.poolId = poolId;
    }

    abstract public boolean nextTask();

    public int getProcessId() {
        return processId;
    }


    public void setUnreliability(float unreliability) {
        this.unreliability = unreliability;
    }

    public void trySendMessage(Message message) {
        if (Math.random() > this.unreliability) {
            MessagePool.send(poolId, message);
        }
    }

    protected void log(Message msg) {
        System.out.println(this.processType + " " + this.getProcessId() + ": message <" + msg.getMessageType() + "> received from process " + msg.getFrom());
    }

    protected void log(String s) {
        System.out.println(this.processType + " " + this.getProcessId() + ": " + s);
    }
}
