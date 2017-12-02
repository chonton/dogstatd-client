/**
 * A simple client to push UDP messages to a local
 * <a href= "https://docs.datadoghq.com/guides/dogstatsd/">dogstatd agent</a>.
 * <ul>
 * <li>Create a {@link org.honton.chas.dogstatd.model.Sender} with the address of the agent.</li>
 * <li>Call {@link org.honton.chas.dogstatd.model.Sender#send(Message)}
 *  to send DataDog a message.</li>
 * <li>Only a single instance of Sender is required.  Sender is thread safe and the send method does not block the caller.</li>
 * </ul>
 * <pre>
 * static public final Sender METRICS = new Sender();
 *
 * public void workSamples() {
 *
 *    METRICS.send(new Histogram("histogram.name", latency);
 *
 *    METRICS.send(new Gauge("round", i, tag));
 *
 *    METRICS.send(new Gauge("round", i, tag));
 *
 *    METRICS.send(new Counter("pi", 3.14));
 *
 *    METRICS.send(new Event("title", "message", "tag1", "tag2"));
 * }
 * </pre>
 */
package org.honton.chas.dogstatd.model;
