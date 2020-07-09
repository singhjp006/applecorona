package com.corona.apple.controller;

import com.corona.apple.webclient.ClientTest;
import com.corona.apple.webclient.ClientTestCustom;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.Summary;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrometheusController {

  // always increments
  Counter counter;

  // can increment or decrement both as memory used
  Gauge gauge;

  // calculates freq of items within a given range
  Histogram histogram;

  Summary summary;
  @Autowired ClientTest clientTest;

  @Autowired
  ClientTestCustom clientTestCustom;

  public PrometheusController(CollectorRegistry collectorRegistry) {

    counter =
        Counter.build()
            .name("request_count")
            .help("No of get google request")
            .register(collectorRegistry);

    gauge = Gauge.build().name("queue_size").help("size of the quueue").register(collectorRegistry);

    histogram =
        Histogram.build()
            .name("request_duration")
            .help("Request duration bucket")
            .register(collectorRegistry);

    summary =
        Summary.build()
            .name("request_duration_summary")
            .help("Request duration Summary")
            .quantile(0.95, .01)
            .register(collectorRegistry);
  }

  @GetMapping(path = "/google")
  public @ResponseBody String getGoogle() {

    Histogram.Timer timer = histogram.startTimer();
    Summary.Timer summaryTimer = summary.startTimer();

    counter.inc();

    timer.observeDuration();

    summaryTimer.observeDuration();

    return clientTest.getGoogle();
  }

  @GetMapping(path = "/push")
  public @ResponseBody String push() {

    gauge.inc();

    return clientTest.getGoogle();
  }

  @GetMapping(path = "/pop")
  public @ResponseBody String pop() {

    gauge.dec();
    return clientTest.getGoogle();
  }














 // ###########################################################################################################################

  @GetMapping(path = "/google2")
  public @ResponseBody String getGoogleCustom() {


    return clientTestCustom.getGoogle();
  }

}
