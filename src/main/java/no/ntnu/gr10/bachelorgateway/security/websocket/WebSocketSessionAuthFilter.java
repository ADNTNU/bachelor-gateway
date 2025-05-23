package no.ntnu.gr10.bachelorgateway.security.websocket;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;


/**
 * WebSocketSessionAuthFilter is a Spring Cloud Gateway filter that authenticates WebSocket
 * sessions based on a session token passed as a query parameter. It checks if the session token is
 * valid and if the user has the necessary scopes to access the requested WebSocket endpoint.
 */
@Component
public class WebSocketSessionAuthFilter
        extends AbstractGatewayFilterFactory<WebSocketSessionAuthFilter.Config> {

  private final WebSocketSessionService sessionService;

  /**
   * Constructor for WebSocketSessionAuthFilter.
   *
   * @param sessionService the service to resolve WebSocket sessions
   */
  public WebSocketSessionAuthFilter(WebSocketSessionService sessionService) {
    super(Config.class);
    this.sessionService = sessionService;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      String path = exchange.getRequest().getPath().value();

      if (!path.startsWith("/ws/data/")) {
        return chain.filter(exchange); // pass through
      }

      String sessionToken = exchange.getRequest().getQueryParams().getFirst("session");
      if (sessionToken == null) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
      }

      return Mono.justOrEmpty(sessionService.resolveSession(sessionToken))
              .flatMap(session -> {
                String entity = path.substring("/ws/data/".length());

                if (!session.getScopes().contains(entity)) {
                  exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                  return exchange.getResponse().setComplete();
                }

                // Append companyId as query param using UriComponentsBuilder
                String companyId = URLEncoder.encode(
                        session.getCompanyId().toString(), StandardCharsets.UTF_8
                );
                URI originalUri = exchange.getRequest().getURI();
                URI newUri = UriComponentsBuilder.fromUri(originalUri)
                        .replaceQuery("companyId=" + companyId)
                        .build(true)
                        .toUri();
                ServerWebExchange mutated = exchange.mutate()
                        .request(builder -> builder.uri(newUri))
                        .build();

                return chain.filter(mutated);
              })
              .switchIfEmpty(Mono.defer(() -> {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
              }));
    };
  }

  /**
   * Configuration class for WebSocketSessionAuthFilter.
   */
  @SuppressWarnings("unused")
  public static class Config {
    // Empty config class to satisfy AbstractGatewayFilterFactory
  }
}

