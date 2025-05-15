package no.ntnu.gr10.bachelor_gateway.dto;

/**
 * Represents an error response payload returned by REST controllers in case of failures.
 *
 * <p>This DTO encapsulates a single error message describing what went wrong. It is used
 * in HTTP responses with appropriate status codes to inform clients of errors.</p>
 *
 * @param message human-readable description of the error
 */
public record ErrorResponse(String message) {}
