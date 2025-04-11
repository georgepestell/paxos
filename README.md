# PAXOS

A simple PAXOS implementation written in Java using message passing.

- Handles message passing failure through receipt messaging and timeouts
- Handles broken proposers and acceptors (as long the 3N + 1 rule holds)

# Running

To run the example (test) cases, run: 

```bash
$ mvn clean test
```

To compile and run the default setup, run:

```bash
$ mvn clean package -Dmaven.test.skip=true

$ java -jar ./target/p1-1.0.jar
```
