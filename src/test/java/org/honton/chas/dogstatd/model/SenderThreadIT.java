package org.honton.chas.dogstatd.model;

import java.io.IOException;
import java.util.Random;

import org.junit.Test;

import lombok.SneakyThrows;

public class SenderThreadIT {

  static public final Sender METRICS = new Sender();

  private final Sender sender;
  private final Random random = new Random();

  public class SendThread extends Thread {
    SendThread(String name) {
      super(name);
      start();
    }

    @Override
    @SneakyThrows
    public void run() {
      for (int i = 0; i < 1000; ++i) {
        int slept = random.nextInt(50);
        sender.send(new Gauge("round", i, getName()));
        sleep(slept);
        sender.send(new Histogram("contention", slept, getName()));
      }
    }
  }

  public SenderThreadIT() throws IOException {
    sender = new Sender();
  }

  @Test
  public void testSendEvent() throws Exception {
    Thread threads[] = new Thread[100];
    for (int t = 0; t < threads.length; ++t) {
      threads[t] = new SendThread("thread-" + t);
    }
    for (int t = 0; t < threads.length; ++t) {
      threads[t].join();
    }
  }
}
