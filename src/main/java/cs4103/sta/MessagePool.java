package cs4103.sta;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MessagePool {

    private static final Set<Message> messages = Collections.synchronizedSet(new HashSet<>());

    public static void setup() {
        messages.clear();
    }

    public static void send(Message msg) {
        messages.add(msg);
    }

    public static Message receive(int to) {
        Message messageFound = null;
        for (Message m : messages) {
            if (m.getTo() == to) {
                messageFound = m;
            }
        }
        // need to do this outside the iterator loop
        if (messageFound != null) {
            messages.remove(messageFound);
        }
        // returns null if no message in set is destined for this process
        return messageFound;
    }

}
