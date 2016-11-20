package org.honton.chas.dogstatd.model;

import org.honton.chas.dogstatd.model.ServiceCheck.Status;
import org.junit.Test;

public class SendEventIT {
  
  @Test
  public void testSendEvent() throws Exception {
    Sender sender = new Sender();
    sender.send(new Event("title", "text"));
  }
  
  @Test
  public void testSendGuage() throws Exception {
    Sender sender = new Sender();
    sender.send(new Gauge("gauge", 14));
  }

  @Test
  public void testSendServiceCheck() throws Exception {
    Sender sender = new Sender();
    sender.send(new ServiceCheck("testService", Status.OK));
  }
}
