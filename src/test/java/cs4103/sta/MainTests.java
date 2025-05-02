package cs4103.sta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;


/**
 * Unit test for simple App.
 */
public class MainTests
{



    @BeforeEach
    public void setup() {
        System.out.println("Preparing Test");
        MessagePool.setup();
        PaxosProcess.setup();
    }

    public static void run(List<Acceptor> acceptors, List<Proposer> proposers, List<Learner> learners, int maxProcessSteps) {

        List<PaxosProcess> paxosProcesses = new ArrayList<>();
        paxosProcesses.addAll(proposers);
        paxosProcesses.addAll(acceptors);
        paxosProcesses.addAll(learners);

        ParallelRunner ps = new ParallelRunner();

        try { 
            ps.runProcessesInParallel(paxosProcesses, maxProcessSteps,500,1500);
        } catch (Exception e) {
            System.err.println(e.getStackTrace());
        }
    }


    @Test
    public void testDefaultLearnerConsensus() {

        System.out.println("Test Default Learner Consensus");

        int noOfAcceptors = 3;
        int noOfProposers = 3;
        int noOfLearners = 2;
        int maxProcessSteps = 200;

        List<Acceptor> acceptors = new ArrayList<>();
        for (int i = 0; i < noOfAcceptors; i++) {
            acceptors.add(new Acceptor());
        }

        String[] proposals = {"first", "second", "third", "fourth", "fifth"};

        List<Proposer> proposers = new ArrayList<>();
        for (int i = 0; i < noOfProposers; i++) {
            proposers.add(new Proposer(scramble(acceptors), proposals[i], 1000 + i));
        }

        List<Learner> learners = new ArrayList<>();
        for (int i = 0; i < noOfLearners; i++) {
            learners.add(new Learner(scramble(acceptors)));
        }

        run(acceptors, proposers, learners, maxProcessSteps);

        // Check that all learners have the same consensus value
        String consensusValue = learners.get(0).getConsensusValue();

        assertTrue(consensusValue != null);

        for (Learner learner : learners) {
            assertTrue(consensusValue.equals(learner.getConsensusValue()));
        }

        // runProcessesInParallel doesn't return until the prescribed number of steps has been executed, so PaxosProcesses can be inspected here if desirable
        System.out.println("done");
    }


    @Test
    public void testSmallBrokenAcceptorConsensus() {

        System.out.println("Test Broken Acceptor Consensus (Small Size)");

        // This should still work as a majority of acceptors are working
        int noOfAcceptors = 3;
        int noOfBrokenAcceptors = 1;

        int noOfProposers = 3;
        int noOfLearners = 2;
        int maxProcessSteps = 500;

        List<Acceptor> acceptors = new ArrayList<>();
        for (int i = 0; i < noOfAcceptors; i++) {
            acceptors.add(new Acceptor());
        }

        for (int i = 0; i < noOfBrokenAcceptors; i++) {
            acceptors.get(i).setBroken(true);
        }

        String[] proposals = {"first", "second", "third", "fourth", "fifth"};

        List<Proposer> proposers = new ArrayList<>();
        for (int i = 0; i < noOfProposers; i++) {
            proposers.add(new Proposer(scramble(acceptors), proposals[i], 1000 + i));
        }

        List<Learner> learners = new ArrayList<>();
        for (int i = 0; i < noOfLearners; i++) {
            learners.add(new Learner(scramble(acceptors)));
        }

        run(acceptors, proposers, learners, maxProcessSteps);

        // Check that all learners have the same consensus value
        String consensusValue = learners.get(0).getConsensusValue();

        assertTrue(consensusValue != null);

        for (Learner learner : learners) {
            assertTrue(consensusValue.equals(learner.getConsensusValue()));
        }

        // runProcessesInParallel doesn't return until the prescribed number of steps has been executed, so PaxosProcesses can be inspected here if desirable
        System.out.println("done");
    }

    @Test
    public void testMediumBrokenAcceptorConsensus() {

        System.out.println("Test Broken Acceptor Consensus (Medium Size)");

        // This should still work as a majority of acceptors are working
        int noOfAcceptors = 5;
        int noOfBrokenAcceptors = 2;

        int noOfProposers = 5;
        int noOfLearners = 4;
        int maxProcessSteps = 500;

        List<Acceptor> acceptors = new ArrayList<>();
        for (int i = 0; i < noOfAcceptors; i++) {
            acceptors.add(new Acceptor());
        }

        for (int i = 0; i < noOfBrokenAcceptors; i++) {
            acceptors.get(i).setBroken(true);
        }

        String[] proposals = {"first", "second", "third", "fourth", "fifth"};

        List<Proposer> proposers = new ArrayList<>();
        for (int i = 0; i < noOfProposers; i++) {
            proposers.add(new Proposer(scramble(acceptors), proposals[i], 1000 + i));
        }

        List<Learner> learners = new ArrayList<>();
        for (int i = 0; i < noOfLearners; i++) {
            learners.add(new Learner(scramble(acceptors)));
        }

        run(acceptors, proposers, learners, maxProcessSteps);

        // Check that all learners have the same consensus value
        String consensusValue = learners.get(0).getConsensusValue();

        assertTrue(consensusValue != null);

        for (Learner learner : learners) {
            assertTrue(consensusValue.equals(learner.getConsensusValue()));
        }

        // runProcessesInParallel doesn't return until the prescribed number of steps has been executed, so PaxosProcesses can be inspected here if desirable
        System.out.println("done");
    } 
    
    @Test
    public void testMajorityBrokenAcceptorFailure() {

        System.out.println("Test Majority Broken Acceptor Failure");

        // This should not reach consensus as a majority of acceptors are failing
        int noOfAcceptors = 5;
        int noOfBrokenAcceptors = 3;

        int noOfProposers = 5;
        int noOfLearners = 4;
        int maxProcessSteps = 500;

        List<Acceptor> acceptors = new ArrayList<>();
        for (int i = 0; i < noOfAcceptors; i++) {
            acceptors.add(new Acceptor());
        }

        for (int i = 0; i < noOfBrokenAcceptors; i++) {
            acceptors.get(i).setBroken(true);
        }

        String[] proposals = {"first", "second", "third", "fourth", "fifth", "sixth"};

        List<Proposer> proposers = new ArrayList<>();
        for (int i = 0; i < noOfProposers; i++) {
            proposers.add(new Proposer(scramble(acceptors), proposals[i], 1000 + i));
        }

        List<Learner> learners = new ArrayList<>();
        for (int i = 0; i < noOfLearners; i++) {
            learners.add(new Learner(scramble(acceptors)));
        }

        run(acceptors, proposers, learners, maxProcessSteps);

        // Check that all learners have not reached consensus
        for (Learner learner : learners) {
            assertTrue(learner.getConsensusValue() == null);
        }

        // runProcessesInParallel doesn't return until the prescribed number of steps has been executed, so PaxosProcesses can be inspected here if desirable
        System.out.println("done");
    } 


    @Test
    public void testPartialProposerAwareness() {

        System.out.println("Test Partial Proposer Awareness");

        // This should reach consensus as a majority of acceptors are failing
        int noOfAcceptors = 5;
        int noOfBrokenAcceptors = 0;

        // int noOfProposers = 3;
        int noOfLearners = 2;
        int maxProcessSteps = 500;

        List<Acceptor> acceptors = new ArrayList<>();
        for (int i = 0; i < noOfAcceptors; i++) {
            acceptors.add(new Acceptor());
        }

        for (int i = 0; i < noOfBrokenAcceptors; i++) {
            acceptors.get(i).setBroken(true);
        }

        String[] proposals = {"first", "second", "third", "fourth", "fifth", "sixth"};

        List<Proposer> proposers = new ArrayList<>();
        
        // Add 3 proposers with >= 1 overlapping acceptors and a majority of acceptors
        proposers.add(new Proposer(acceptors.subList(0, 3), proposals[0], 1));
        proposers.add(new Proposer(acceptors.subList(1, 4), proposals[1], 2));
        proposers.add(new Proposer(acceptors.subList(2, 5), proposals[2], 5));

        List<Learner> learners = new ArrayList<>();
        for (int i = 0; i < noOfLearners; i++) {
            learners.add(new Learner(scramble(acceptors)));
        }

        run(acceptors, proposers, learners, maxProcessSteps);

        // Check that all learners have the same consensus value
        String consensusValue = learners.get(0).getConsensusValue();

        assertTrue(consensusValue != null);

        for (Learner learner : learners) {
            assertTrue(consensusValue.equals(learner.getConsensusValue()));
        }

        // runProcessesInParallel doesn't return until the prescribed number of steps has been executed, so PaxosProcesses can be inspected here if desirable
        System.out.println("done");
    } 

    @Test
    public void testUnreliableMessagePassing() {

        System.out.println("Test Unreliable Message Passing");

        // This should reach consensus as a majority of acceptors are failing
        int noOfAcceptors = 3;
        int noOfBrokenAcceptors = 0;

        int noOfProposers = 3;
        int noOfLearners = 2;
        int maxProcessSteps = 500;

        List<Acceptor> acceptors = new ArrayList<>();
        for (int i = 0; i < noOfAcceptors; i++) {
            Acceptor newAcceptor = new Acceptor();
            newAcceptor.setUnreliability(0.2f);
            acceptors.add(newAcceptor);
        }

        for (int i = 0; i < noOfBrokenAcceptors; i++) {
            acceptors.get(i).setBroken(true);
        }

        String[] proposals = {"first", "second", "third", "fourth", "fifth", "sixth"};

        List<Proposer> proposers = new ArrayList<>();
        
        for (int i = 0; i < noOfProposers; i++) {
            Proposer newProposer = new Proposer(scramble(acceptors), proposals[i], i);
            newProposer.setUnreliability(0.2f);
            proposers.add(newProposer);
        }

        List<Learner> learners = new ArrayList<>();
        for (int i = 0; i < noOfLearners; i++) {
            Learner newLearner = new Learner(scramble(acceptors));
            newLearner.setUnreliability(0.2f);
            learners.add(newLearner);
        }

        run(acceptors, proposers, learners, maxProcessSteps);

        // Check that all learners have the same consensus value
        String consensusValue = learners.get(0).getConsensusValue();

        assertTrue(consensusValue != null);

        for (Learner learner : learners) {
            assertTrue(consensusValue.equals(learner.getConsensusValue()));
        }

        // runProcessesInParallel doesn't return until the prescribed number of steps has been executed, so PaxosProcesses can be inspected here if desirable
        System.out.println("done");
    }

    private static final Random rand = new Random();

    private static <T> List<T> scramble(List<T> list) {
        List<T> copy = new ArrayList<>(list);
        List<T> res = new ArrayList<>();
        while (!copy.isEmpty()) {
            res.add(copy.remove(rand.nextInt(copy.size())));
        }
        return res;
    }

}
