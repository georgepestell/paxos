package cs4103.sta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelRunner {

    public void runProcessesInParallel(List<PaxosProcess> processes, int maxSteps, int minSleepTime, int maxSleepTime) throws Exception {

        int numThreads = processes.size();

        ThreadPoolExecutor executorService = new ThreadPoolExecutor(numThreads, numThreads, 60L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r);
                    }
                });

        executorService.allowCoreThreadTimeOut(true);
        List<Future<?>> futures = new ArrayList<>();
        AtomicInteger stepsTaken = new AtomicInteger();
        AtomicBoolean allFinish = new AtomicBoolean(false);

        try {
            for (PaxosProcess process : processes) {
                futures.add(executorService.submit(() -> {
                    Random rand = new Random();
                    boolean processFinished = false;
                    while (!processFinished && !allFinish.get()) {
                        try {
                            Thread.sleep(minSleepTime + rand.nextInt(maxSleepTime - minSleepTime));
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        int allSteps = stepsTaken.incrementAndGet();
                        processFinished = process.nextTask();

                        if ((allSteps >= maxSteps || consensusReached(processes)) && !allFinish.get()) {
                            System.out.println("executed " + allSteps + " steps, processes terminating");
                            allFinish.set(true);
                        }
                    }
                }));
            }

            System.out.println("threads set up");

            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (ExecutionException e) {
                    throw new Exception("An exception was thrown by one of the threads.", e.getCause());
                }
            }
        } finally {
            executorService.shutdown();
        }

    }

    private boolean consensusReached(List<PaxosProcess> processes) {
      List<Learner> learners = new ArrayList<>();
      for (PaxosProcess p : processes) {
        if (p instanceof Learner) {
          learners.add((Learner) p);
        }
      } 

      if (learners.isEmpty()) {
        return true;
      }

      String firstConsensus = ((Learner) learners.get(0)).getConsensusValue();
      if (firstConsensus == null) {
        return false;
      }

      // Check if all learners have the same consensus value
      for (Learner learner: learners) {
        String value = learner.getConsensusValue();
        if (value == null || !value.equals(firstConsensus)) {
          return false;
        } 
      }

      return true;


    }

}
