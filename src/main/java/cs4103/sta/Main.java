package cs4103.sta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class    Main {
    private static final Random rand = new Random();

    public static void main(String[] args) throws Exception {

        int noOfAcceptors = 3;
        int noOfProposers = 3;
        int noOfLearners = 2;
        int maxProcessSteps = 1000;

        int poolId = MessagePool.newPool();

        List<Acceptor> acceptors = new ArrayList<>();
        for (int i = 0; i < noOfAcceptors; i++) {
            acceptors.add(new Acceptor(poolId));
        }

        String[] proposals = {"first", "second", "third", "fourth", "fifth"};


        List<Proposer> proposers = new ArrayList<>();
        for (int i = 0; i < noOfProposers; i++) {
            proposers.add(new Proposer(poolId, scramble(acceptors), proposals[i], 1000 + i));
        }

        List<Learner> learners = new ArrayList<>();
        for (int i = 0; i < noOfLearners; i++) {
            learners.add(new Learner(poolId, scramble(acceptors)));
        }

        List<PaxosProcess> paxosProcesses = new ArrayList<>();
        paxosProcesses.addAll(proposers);
        paxosProcesses.addAll(acceptors);
        paxosProcesses.addAll(learners);

        ParallelRunner ps = new ParallelRunner();
        ps.runProcessesInParallel(paxosProcesses, maxProcessSteps,500,1500);

        // runProcessesInParallel doesn't return until the prescribed number of steps has been executed, so PaxosProcesses can be inspected here if desirable
        System.out.println("done");
    }

    private static <T> List<T> scramble(List<T> list) {
        List<T> copy = new ArrayList<>(list);
        List<T> res = new ArrayList<>();
        while (!copy.isEmpty()) {
            res.add(copy.remove(rand.nextInt(copy.size())));
        }
        return res;
    }
}
