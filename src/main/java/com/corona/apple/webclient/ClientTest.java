package com.corona.apple.webclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ClientTest {

  WebClient webClient;

  public ClientTest(@Autowired WebClient.Builder webClient) {
    this.webClient = webClient.baseUrl("https://www.google.com/search?q=ss&oq=ss&aqs=chrome..69i57j69i59j69i60l3j69i65j69i60.1576j0j7&sourceid=chrome&ie=UTF-8").build();
  }

  public String getGoogle() {
    return webClient.get().retrieve().bodyToMono(String.class).block();
  }
}
