package dev.kabka.api.exception;

import dev.kabka.core.exception.GroupNotAssignedException;
import dev.kabka.core.exception.InvalidOffsetException;
import dev.kabka.core.exception.InvalidPartitionException;
import dev.kabka.core.exception.KabkaException;
import dev.kabka.core.exception.TopicNotFoundException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "dev.kabka.api.controller")
public class KabkaExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(KabkaExceptionHandler.class);

	@ExceptionHandler({TopicNotFoundException.class, GroupNotAssignedException.class})
	public ResponseEntity<Map<String, Object>> handleNotFound(KabkaException e) {
		return build(HttpStatus.NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler({InvalidPartitionException.class, InvalidOffsetException.class})
	public ResponseEntity<Map<String, Object>> handleBadRequest(KabkaException e) {
		return build(HttpStatus.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(KabkaException.class)
	public ResponseEntity<Map<String, Object>> handleKabkaException(KabkaException e) {
		logger.error("Unhandled Kabka exception", e);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleUnexpected(Exception e) {
		logger.error("Unexpected error handling request", e);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
	}

	private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message) {
		return ResponseEntity.status(status)
				.body(Map.of("status", status.value(), "error", status.getReasonPhrase(), "message", message));
	}
}
