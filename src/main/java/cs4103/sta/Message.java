package cs4103.sta;

public class Message {
    private final int from;
    private final int to;
    private final MessageType messageType;
    private int ballotId;
    private String consensusValue;

    public enum MessageType {dummy, value, acknowledge, prepare, accept, learn, reject_prepare, reject};

    Message(int from, int to, MessageType messageType) {
        this.from = from;
        this.to = to;
        this.messageType = messageType;
    }

    Message(int from, int to, MessageType messageType, int ballotId) {
        this.from = from;
        this.to = to;
        this.messageType = messageType;
        this.ballotId = ballotId;
    }

    Message(int from, int to, MessageType messageType, int ballotId, String consensusValue) {
        this.from = from;
        this.to = to;
        this.messageType = messageType;
        this.ballotId = ballotId;
        this.consensusValue = consensusValue;
    }

    public int getFrom() {
        return this.from;
    }

    public int getTo() {
        return this.to;
    }

    public MessageType getMessageType() {
        return this.messageType;
    }

    public int getBallotId() {
        return this.ballotId;
    }

    public void setBallotId(int ballotId) {
        this.ballotId = ballotId;
    }

    public String getConsensusValue() {
        return this.consensusValue;
    }

    public void setConsensusValue(String consensusValue) {
        this.consensusValue = consensusValue;
    }
}
