package org.honton.chas.dogstatd.model;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import lombok.SneakyThrows;

public class SenderTest {

  static class UdpThread extends Thread {

    private final CompletableFuture<String> future = new CompletableFuture<>();
    private final ByteBuffer buffer = ByteBuffer.allocate(2000);
    private final CountDownLatch waitForStart = new CountDownLatch(1);
    private final DatagramChannel server;

    UdpThread(String localHost) throws Exception {
      this(new InetSocketAddress(localHost, 8125));
    }

    UdpThread(InetSocketAddress socket) throws Exception {
      server = DatagramChannel.open();
      server.bind(socket);
      start();
      waitForStart.await();
      Thread.sleep(10);
    }

    @SneakyThrows
    @Override
    public void run() {
      waitForStart.countDown();
      server.receive(buffer);
      buffer.flip();
      CharBuffer cb = StandardCharsets.UTF_8.decode(buffer);
      future.complete(cb.toString());
      server.close();
    }

    public String getResult() throws Exception {
      return future.get(2, TimeUnit.SECONDS);
    }
  }

  @Test
  public void testSend() throws Exception {
    String localHost = InetAddress.getLocalHost().getHostName();
    UdpThread receiver = new UdpThread(localHost);

    Sender sender = new Sender(localHost);
    try {
      sender.send(new Event("title", "text"));
      Assert.assertEquals("_e{5,4}:title|text\n", receiver.getResult());
    }
    finally {
      sender.shutdown();
    }
  }

  @Test
  public void testNoSendInvalid() throws Exception {
    InetSocketAddress socket = new InetSocketAddress(InetAddress.getLocalHost(), 8125);
    UdpThread receiver = new UdpThread(socket);

    Sender sender = new Sender(socket);
    try {
      // try invalid message
      sender.send(new Event("", ""));
      // try buffer overflow
      sender.send(new Event("title", CharBuffer.allocate(2000).toString().replace('\0', 't')));
      // and a good message
      sender.send(new Event("title", "text"));
      Assert.assertEquals("_e{5,4}:title|text\n", receiver.getResult());
    }
    finally {
      sender.shutdown();
    }
  }

  @Test
  public void testNoListener() throws Exception {
    InetSocketAddress socket = new InetSocketAddress(InetAddress.getLocalHost(), 8125);

    Sender sender = new Sender(socket, TimeUnit.SECONDS.toMillis(1));
    Assert.assertTrue(sender.send(new Event("title", "text")));
    Assert.assertFalse(sender.isThrottled());
    Assert.assertFalse(sender.send(new ServiceCheck("title", ServiceCheck.Status.CRITICAL)));
    Assert.assertTrue(sender.isThrottled());
    Assert.assertFalse(sender.send(new Set("name", "value")));

    Thread.sleep(TimeUnit.SECONDS.toMillis(2));
    Assert.assertTrue(sender.send(new Gauge("fuel", 75.0)));
    Assert.assertFalse(sender.isThrottled());
    Assert.assertFalse(sender.send(new Set("name", "value")));
  }
}
