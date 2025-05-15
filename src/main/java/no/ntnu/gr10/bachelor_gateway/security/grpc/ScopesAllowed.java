package no.ntnu.gr10.bachelor_gateway.security.grpc;

import no.ntnu.gr10.bachelor_gateway.security.Scopes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation for enforcing scope-based authorization on gRPC service methods in the gateway.
 * <p>
 * When placed on a method, the {@link JwtAuthInterceptor} will ensure that the authenticated
 * user’s token contains all of the specified scopes before allowing the RPC to proceed.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre><code>
 * @ScopesAllowed(Scopes.FISHERY_ACTIVITY)
 * public void listFisheryActivities(...) { … }
 * </code></pre>
 *
 * @author Daniel Neset
 * @version 15.05.2025
 * @see Scopes
 * @see JwtAuthInterceptor
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScopesAllowed {
  /**
   * The set of {@link Scopes} that the caller must possess in their JWT to invoke the annotated method.
   *
   * @return an array of required scopes
   */
  Scopes[] value();
}
