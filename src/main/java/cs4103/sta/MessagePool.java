package cs4103.sta;

import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import java.util.concurrent.atomic.AtomicInteger;

public class MessagePool {

    private static final Map<Integer, Set<Message>> messages = Collections.synchronizedMap(new HashMap<Integer, Set<Message>>());

    private static AtomicInteger nextPoolId = new AtomicInteger();

    public static int newPool() {
      int poolId = nextPoolId.getAndIncrement();
      messages.put(poolId, Collections.synchronizedSet(new HashSet<>()));
      return poolId;
    }

    public static void send(int poolId, Message msg) {
        messages.get(poolId).add(msg);
    }

    public static Message receive(int poolId, int to) {
        Message messageFound = null;
        for (Message m : messages.get(poolId)) {
            if (m.getTo() == to) {
                messageFound = m;
            }
        }
        // need to do this outside the iterator loop
        if (messageFound != null) {
            messages.get(poolId).remove(messageFound);
        }
        // returns null if no message in set is destined for this process
        return messageFound;
    }

}
