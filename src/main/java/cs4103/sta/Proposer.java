package cs4103.sta;

import java.util.List;

public class Proposer extends PaxosProcess {

    private String proposal;
    private final int ballotId;
    private final List<Acceptor> acceptors;

    private int acks = 0;
    private int rejections = 0;

    private int counter = 0;
    private final int timeout = 20;

    private int maxBallotId;

    Proposer(int poolId, List<Acceptor> acceptors, String proposal, int ballotId) {
      super(poolId);
        this.processType = "Proposer";
        this.acceptors = acceptors;
        this.proposal = proposal;
        this.ballotId = ballotId;
        this.maxBallotId = ballotId;

        // Send a prepare message to all acceptors
        this.sendNewProposal();
        
    }

    public void increaseBallotId() {
        this.maxBallotId = this.maxBallotId * 100 + this.ballotId;
    }

    public void sendNewProposal() {
        this.acks = 0;
        this.rejections = 0;

        // Send the new prepare message
        for (Acceptor acceptor : this.acceptors) {
            trySendMessage(new Message(this.getProcessId(), acceptor.getProcessId(), Message.MessageType.prepare, this.maxBallotId));
        }
    }

    @Override
    public boolean nextTask() {
        Message msg = MessagePool.receive(this.poolId, this.getProcessId());
        if (msg != null) {
            this.log(msg);
            Message.MessageType messageType = msg.getMessageType();
            switch (messageType) {
                case dummy: {
                    trySendMessage(new Message(this.getProcessId(), this.acceptors.get(0).getProcessId(), Message.MessageType.dummy));
                }
                ;
                break;
                case acknowledge: {

                    // Update the proposal if necessary
                    if (msg.getBallotId() > this.maxBallotId) {
                        this.maxBallotId = msg.getBallotId();
                    }

                    if (msg.getConsensusValue() != null) {
                        this.proposal = msg.getConsensusValue();
                    }

                    // Add the acknolwedgement to the list of acknowledgements
                    this.acks++;
                    
                    // Send accept once a majority of acceptors have acknowledged
                    if (this.acks >= acceptors.size() / 2 + 1) {
                        for (Acceptor acceptor : acceptors) {
                            trySendMessage(new Message(this.getProcessId(), acceptor.getProcessId(), Message.MessageType.accept, this.maxBallotId, this.proposal));
                        }
                        // return true; // TODO: add back later return true
                    }
                };
                break;
                case reject_prepare: {

                    if (msg.getConsensusValue() != null) {
                        this.proposal = msg.getConsensusValue();
                    }

                    if (msg.getBallotId() > this.maxBallotId) {
                        this.maxBallotId = msg.getBallotId();
                    }

                    // Increment the rejection counter
                    this.rejections++;
                    if (this.rejections >= acceptors.size() / 2 + 1) {
                        
                        // Increase the maxBallotID
                        increaseBallotId();
                        sendNewProposal();
                        
                    }
                };
                break;
                case reject: {

                    if (msg.getConsensusValue() != null){
                        this.proposal = msg.getConsensusValue();
                    } 

                    if (msg.getBallotId() > this.maxBallotId) {
                        this.maxBallotId = msg.getBallotId();
                    }

                    increaseBallotId();
                    sendNewProposal();

                };
                break;
                case accept: {
                    // Our proposal was accepted, we can stop
                    log("accepted, stopping");
                    return true;
                }
                default: {
                    log("incorrect message <" + msg.getMessageType() + "> received");
                    return true;
                }
            }

            // Reset the timeout counter
            counter = 0;

        } else {
            // no message received;
            // add a counter to prevent message failure breakage
            this.counter++;

            if (++this.counter > this.timeout) {
                log("Timeout! sending new proposal");
                this.counter = 0;
                increaseBallotId();
                sendNewProposal();
            }

        }
        return false;

    }
}
