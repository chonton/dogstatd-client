package org.honton.chas.dogstatd.model;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ProtocolFamily;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.BufferOverflowException;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.UnsupportedAddressTypeException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import lombok.extern.slf4j.Slf4j;

/**
 * Send formatted UDP messages to a DogStatD. 
 * Messages are added to a queue and sent in a background thread.
 */
@Slf4j
public class Sender {

  private static final int MTU = 1440;

  private final CharBuffer chars = CharBuffer.allocate(1400);
  private final DatagramChannel clientChannel;
  private final BlockingQueue<Message> queue;

  /**
   * Create a sender which sends to a DogStatD on port 8125 at the specified 
   * address.  A maximum of 1000 messages will be queued before dropping any
   * incoming messages.
   * 
   * @param address The address to send to.
   * @throws IOException
   */
  public Sender(String address) throws IOException {
    this(InetAddress.getByName(address));
  }

  /**
   * Create a sender which sends to a DogStatD on port 8125 at the specified 
   * address. A maximum of 1000 messages will be queued before dropping any
   * incoming messages.
   * 
   * @param address The address to send to.
   * @throws IOException
   */
  public Sender(InetAddress address) throws IOException {
    this(new InetSocketAddress(address, 8125), 1000);
  }

  /**
   * Create a sender which sends to a DogStatD on the specified port and address.
   * 
   * @param socket The address and port to send to.
   * @param queueSize The maximum unsent messages before dropping messages.
   * @throws IOException
   */
  public Sender(InetSocketAddress socket, int maxUnsentMessages) throws IOException {
    queue = new ArrayBlockingQueue<>(maxUnsentMessages);
    clientChannel = DatagramChannel.open(getProtocol(socket.getAddress()));
    clientChannel.configureBlocking(false);
    clientChannel.setOption(StandardSocketOptions.SO_SNDBUF, MTU);
    clientChannel.bind(socket);

    new Thread("dogstatd") {
      {
        setDaemon(true);
        start();
      }

      @Override
      public void run() {
        try {
          pump();
        } catch (InterruptedException ie) {
          log.info("interrupt shut down dogstatd pump");
          return;
        }
        log.error("Unexpected RuntimeException shut down dogstatd pump");
      }
    };
  }

  private ProtocolFamily getProtocol(InetAddress host) {
    if (host instanceof Inet4Address) {
      return StandardProtocolFamily.INET;
    }
    if (host instanceof Inet6Address) {
      return StandardProtocolFamily.INET6;
    }
    throw new UnsupportedAddressTypeException();
  }

  /**
   * Send message to collector daemon.
   * 
   * @param message The message to send.
   */
  public void send(Message message) {
    if (!queue.offer(message)) {
      log.info("dropped message {}", message);
    }
  }

  private void pump() throws InterruptedException {
    for (;;) {
      Message message = queue.take();
      if (!message.validate()) {
        continue;
      }
      chars.clear();
      try {
        message.format(chars);
      }
      catch(BufferOverflowException boe) {
        log.info("too large of message {}", message);
        continue;
      }
      try {
        clientChannel.write(StandardCharsets.UTF_8.encode(chars));
      } catch (IOException ignore) {
        log.warn("failed to send {}", chars.toString());
      }
    }
  }
}
