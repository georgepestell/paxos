package cs4103.sta;

import java.util.ArrayList;
import java.util.List;

public class Acceptor extends PaxosProcess {

    // All valid ballot IDs are positive integers
    private int maxBallotId = -1;

    // Accepted value
    private String acceptedProposal = null;

    private boolean isBroken;

    // List of learners
    private List<Integer> learners = new ArrayList<>();

    public Acceptor(int poolId) {
      super(poolId);
        this.processType = "Acceptor";
    }

    public void setBroken(boolean isBroken) {
        this.isBroken = true;
    }

    @Override
    public boolean nextTask() {

        if (this.isBroken) {
            return false;
        }

        Message msg = MessagePool.receive(this.poolId, this.getProcessId());
        if (msg != null) {
            this.log(msg);
            Message.MessageType messageType = msg.getMessageType();
            switch (messageType) {
                case dummy: {
                    trySendMessage(new Message(this.getProcessId(), msg.getFrom(), Message.MessageType.dummy));
                }
                ;
                break;
                case prepare: {

                    // Ignore prepare requests with lower ballot IDs
                    if (msg.getBallotId() > this.maxBallotId) {
                            this.maxBallotId = msg.getBallotId();

                            // Send the promise, with the accepted value if any
                            if (this.acceptedProposal != null) {
                                trySendMessage(new Message(this.getProcessId(), msg.getFrom(), Message.MessageType.acknowledge, this.maxBallotId, this.acceptedProposal));
                            } else {
                                trySendMessage(new Message(this.getProcessId(), msg.getFrom(), Message.MessageType.acknowledge, this.maxBallotId));
                        }
                    } else {
                        // Send a rejection message to enable re-proposals
                        if (this.acceptedProposal != null) {
                            trySendMessage(new Message(this.getProcessId(), msg.getFrom(), Message.MessageType.reject_prepare, this.maxBallotId, this.acceptedProposal));
                        } else {
                            trySendMessage(new Message(this.getProcessId(), msg.getFrom(), Message.MessageType.reject_prepare, this.maxBallotId));
                        }
                    }
                }
                break;
                case accept: {
                    // If the ballot ID is higher than the current max acknowledged, accept the proposal
                    // Or, if the value is the same as the value already accepted, allowing proposers to stop earlier
                    if (msg.getBallotId() >= this.maxBallotId || msg.getConsensusValue().equals(this.acceptedProposal)) {
                        this.maxBallotId = Math.max(msg.getBallotId(), this.maxBallotId);
                        this.acceptedProposal = msg.getConsensusValue();
                        log("accepted value: " + this.acceptedProposal);

                        // Send the accepted message to all learners
                        for (Integer learner : learners) {
                            trySendMessage(new Message(this.getProcessId(), learner, Message.MessageType.value, this.maxBallotId, this.acceptedProposal));
                        }
                        this.learners.clear();

                        // Because proposers keep sending requests to prevent stagnation, an accept request should be sent back to allow the Proposer to stop sending messages
                        trySendMessage(new Message(this.getProcessId(), msg.getFrom(), Message.MessageType.accept, this.maxBallotId, this.acceptedProposal));

                    } else {
                        // Send reject message
                        trySendMessage(new Message(this.getProcessId(), msg.getFrom(), Message.MessageType.reject, msg.getBallotId(), this.acceptedProposal));
                        log("rejected value: " + msg.getConsensusValue());
                    }

                }
                break;
                case learn: {
                    // Add to the list of learners

                    // Send consensus if reached or add to queue
                    if (this.acceptedProposal != null) {
                        trySendMessage(new Message(this.getProcessId(), msg.getFrom(), Message.MessageType.value, this.maxBallotId, this.acceptedProposal));                        
                    } else {
                        learners.add(msg.getFrom());
                    }



                    
                };
                break;
                default: {
                    log("incorrect message <" + msg.getMessageType() + "> received");
                    return true;
                }
            }

        } else {
            // acceptor only sends messages in response to one received
        }

        return false;
    }
}
