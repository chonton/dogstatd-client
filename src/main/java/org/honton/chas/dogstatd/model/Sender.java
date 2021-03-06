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
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.UnsupportedAddressTypeException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Send formatted UDP messages to a DogStatD. 
 * Messages are added to a queue and sent in a background thread.
 * If message cannot be sent to local DogStatD agent, drop any additional messages within the
 * throttleInterval.
 */
@Slf4j
public class Sender {

  private static final int MTU = 1440;

  private static final long ONE_MINUTE = TimeUnit.MINUTES.toMillis(1);

  private final DatagramChannel clientChannel;
  private volatile long throttleEnd;
  private long throttleInterval;

  /**
   * Create a sender which sends to a DogStatD on port 8125 at the local loopback
   * address.  ThrottleInterval is one minute.
   */
  public Sender() {
    this(InetAddress.getLoopbackAddress());
  }

  /**
   * Create a sender which sends to a DogStatD on port 8125 at the specified 
   * address.  ThrottleInterval is one minute.
   *
   * @param address The address to send to.
   */
  public Sender(String address) {
    this(getAddress(address));
  }

  @SneakyThrows
  private static InetAddress getAddress(String address) {
    return InetAddress.getByName(address);
  }

  /**
   * Create a sender which sends to a DogStatD on port 8125 at the specified 
   * address.  ThrottleInterval is one minute.
   *
   * @param address The address to send to.
   */
  public Sender(InetAddress address) {
    this(new InetSocketAddress(address, 8125));
  }

  /**
   * Create a sender which sends to a DogStatD on the specified port and address.
   * ThrottleInterval is one minute.
   * @param socket The address and port to send to.
   */
  public Sender(InetSocketAddress socket) {
    this(socket, ONE_MINUTE);
  }

  /**
   * Create a sender which sends to a DogStatD on the specified port and address.
   * ThrottleInterval is specified in milliseconds.  If a message fails to be sent to the
   * local agent, any messages during the throttleInterval will be dropped.  Additional
   * messages will be attempted once the throttleInterval has expired.
   *
   * @param socket The address and port to send to.
   * @param throttleInterval The number of milliseconds to ignore messages.
   */
  @SneakyThrows
  public Sender(InetSocketAddress socket, long throttleInterval) {
    clientChannel = DatagramChannel.open(getProtocol(socket.getAddress()));
    clientChannel.configureBlocking(false);
    clientChannel.setOption(StandardSocketOptions.SO_SNDBUF, MTU);
    clientChannel.connect(socket);
    this.throttleInterval = throttleInterval;
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
   * Send message to collector daemon.  Messages are checked for validity and will be dropped if too
   * large or contain invalid values.  Message will also be dropped if this method is invoked within
   * the throttleInterval.
   *
   * @param message The message to send.
   * @return true, if message is valid and successfully queued for delivery; false, if message not
   * sent due to improper message formatting, delivery failure, or the throttleInterval is still active.
   */
  public boolean send(Message message) {
    if (throttleEnd != 0) {
      if (System.currentTimeMillis() < throttleEnd) {
        return false;
      }
      throttleEnd = 0;
    }

    if (!message.validate()) {
      return false;
    }

    CharBuffer chars = CharBuffer.allocate(1400);
    chars.clear();
    try {
      message.format(chars);
    }
    catch(BufferOverflowException boe) {
      log.info("too large of message {}", message);
      return false;
    }

    chars.flip();
    ByteBuffer bytes = StandardCharsets.UTF_8.encode(chars);

    try {
      clientChannel.write(bytes);
      return true;
    } catch (IOException pue) {
      throttleEnd = System.currentTimeMillis() + throttleInterval;
    }
    log.warn("failed to send message {}", message);
    return false;
  }

  void shutdown() throws IOException {
    clientChannel.close();
  }

  boolean isThrottled() {
    return throttleEnd != 0;
  }
}
