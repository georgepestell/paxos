package cs4103.sta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Learner extends PaxosProcess {

    private final List<Acceptor> acceptors;

    private String consensusValue = null;

    private final int timeout = 20;
    private int counter = 0;

    private Map<Integer, String> learnedValues = new HashMap<>();

    public Learner(List<Acceptor> acceptors) {
        this.processType = "Learner";
        this.acceptors = acceptors;

        // Send learn request to all acceptors
        sendLearnRequests();

    }

    public void sendLearnRequests() {
        for (Acceptor acceptor : this.acceptors) {
            trySendMessage(new Message(this.getProcessId(), acceptor.getProcessId(), Message.MessageType.learn));
        }
    }

    public String getConsensusValue() {
        return this.consensusValue;
    }

    @Override
    public boolean nextTask() {
        Message msg = MessagePool.receive(this.getProcessId());
        if (msg != null) {
            this.log(msg);
            Message.MessageType messageType = msg.getMessageType();
            switch (messageType) {
                case dummy: {


                }
                ;
                break;
                case value: {
                    this.learnedValues.put(msg.getFrom(), msg.getConsensusValue());

                    Map<String, Integer> values = new HashMap<>();
                    for (String value : this.learnedValues.values()) {
                        if (values.containsKey(value)) {
                            values.put(value, values.get(value) + 1);
                        } else {
                            values.put(value, 1);
                        }
                    }

                    for (String value : values.keySet()) {
                        if (values.get(value) >= this.acceptors.size() / 2 + 1) {
                            this.consensusValue = value;
                            log("consensus value: " + this.consensusValue);
                            return true;    

                        }
                    }

                };
                break;
                default: {
                    log("incorrect message <" + msg.getMessageType() + "> received");
                    return true;
                }
            }
            this.counter = 0;

        } else {
            // No message received; do nothing
            this.counter++;
            if (this.counter > this.timeout) {
                this.counter = 0;
                sendLearnRequests();
            }
        }
        return false;
    }
}
