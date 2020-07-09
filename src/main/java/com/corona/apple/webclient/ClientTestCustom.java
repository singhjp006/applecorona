package com.corona.apple.webclient;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ClientTestCustom {

  WebClient webClient;

  // In  the constructor of WebClientAutoConfiguration, you see the injection of
  // ObjectProvider<WebClientCustomizer>. This ObjectProvider is capable of returning all object
  // instances of the WebClientCustomizer interface to customize the WebClient.Builder. Hence out
  // custom components implementing WebClientCustomizer will also be picked up

  public ClientTestCustom(ObjectProvider<WebClientCustomizer> customizerProvider) {
    WebClient.Builder builder = WebClient.builder();

    customizerProvider.orderedStream().forEach((customizer) -> customizer.customize(builder));
    webClient = builder.baseUrl("https://jsonplaceholder.typicode.com/").build();
  }

  public String getGoogle() {
    return webClient.get().uri("/todos").retrieve().bodyToMono(String.class).block();
  }
}
