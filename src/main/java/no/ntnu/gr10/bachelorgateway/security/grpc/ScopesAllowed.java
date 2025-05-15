package no.ntnu.gr10.bachelorgateway.security.grpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import no.ntnu.gr10.bachelorgateway.security.Scope;


/**
 * Annotation for enforcing scope-based authorization on gRPC service methods in the gateway.
 *
 * <p>When placed on a method, the {@link JwtAuthInterceptor} will ensure that the authenticated
 * user’s token contains all the specified scopes before allowing the RPC to proceed.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 *    @ScopesAllowed(Scope.FISHERY_ACTIVITY)
 *    public void listFisheryActivities(...) { … }
 * }
 * </pre>
 *
 * @author Daniel Neset
 * @version 15.05.2025
 * @see Scope
 * @see JwtAuthInterceptor
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScopesAllowed {
  /**
   * The set of {@link Scope} that the caller must possess in
   * their JWT to invoke the annotated method.
   *
   * @return an array of required scopes
   */
  Scope[] value();
}
