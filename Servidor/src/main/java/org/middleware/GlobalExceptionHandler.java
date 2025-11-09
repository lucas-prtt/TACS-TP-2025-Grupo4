package org.middleware;

import org.exceptions.*;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.utils.HttpErrorResponseBuilder;
import org.utils.I18nManager;

@ControllerAdvice
public class GlobalExceptionHandler {
    public ResponseEntity<?> basicResponse(String lang, String key, HttpStatus status) {
        return HttpErrorResponseBuilder.simpleError(I18nManager.get(key, lang), key, status);
    }

    public String getLanguage() {
        return LocaleContextHolder.getLocale().getLanguage();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex, WebRequest request) {
        ex.printStackTrace(System.err);
        return basicResponse(getLanguage(), "INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<?> hendleAccountAlreadyExists(AccountAlreadyExistsException ex, WebRequest request) {
        return basicResponse(getLanguage(), "ERROR_USER_ALREADY_EXISTS", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<?> handleAccountNotFound(AccountNotFoundException ex, WebRequest request) {
        return basicResponse(getLanguage(), "ERROR_ACCOUNT_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EventWithNullFieldsException.class)
    public ResponseEntity<?> handleEventWithNullFields(EventWithNullFieldsException ex, WebRequest request) {
        return HttpErrorResponseBuilder.simpleBadRequest(I18nManager.get("ERROR_EVENT_NULL_FIELDS", getLanguage(),  String.join(", ", ex.getProblems().stream().map(p -> I18nManager.get(p, getLanguage())).toList())), "ERROR_EVENT_NULL_FIELDS");
    }

    @ExceptionHandler(InvalidEventUrlException.class)
    public ResponseEntity<?> handleInvalidURL(InvalidEventUrlException ex, WebRequest request) {
        return basicResponse(getLanguage(), "ERROR_INVALID_URL", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<?> handleInvalidLogin(InvalidLoginException ex, WebRequest request) {
        return basicResponse(getLanguage(), "ERROR_LOGIN", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NullPageInfoException.class)
    public ResponseEntity<?> handleInvalidPagination(NullPageInfoException ex, WebRequest request) {
        return basicResponse(getLanguage(), "ERROR_INVALID_PAGINATION", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WeakPasswordException.class)
    public ResponseEntity<?> handleWeakPassword(WeakPasswordException ex, WebRequest request) {
        return HttpErrorResponseBuilder.simpleBadRequest(I18nManager.get("ERROR_WEAK_PASSWORD", getLanguage(), String.join("; ", ex.getProblems().stream().map(prob -> I18nManager.get(prob, getLanguage())).toList())), "ERROR_WEAK_PASSWORD");
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<?> handleEventNotFound(EventNotFoundException ex, WebRequest request) {
        return basicResponse(getLanguage(), "ERROR_EVENT_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrganizerRegisterException.class)
    public ResponseEntity<?> handleOrganizerRegister(OrganizerRegisterException ex, WebRequest request) {
        return basicResponse(getLanguage(), "ERROR_ORGANIZER_REGISTER", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyRegisteredException.class)
    public ResponseEntity<?> handleAlreadyRegistered(AlreadyRegisteredException ex, WebRequest request) {
        return basicResponse(getLanguage(), "ERROR_DOUBLE_REGISTER", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EventRegistrationsClosedException.class)
    public ResponseEntity<?> handleRegistrationsClosed(EventRegistrationsClosedException ex, WebRequest request) {
        return basicResponse(getLanguage(), "ERROR_REGISTRATIONS_CLOSED", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<?> handleSecurityException(SecurityException ex, WebRequest request) {
        return basicResponse(getLanguage(), "ERROR_SECURITY_EXCEPTION", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RegistrationNotFoundException.class)
    public ResponseEntity<?> handleRegistrationNotFound(RegistrationNotFoundException ex, WebRequest request) {
        return basicResponse(getLanguage(), "ERROR_REGISTRATION_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RegistrationOfDifferentUserException.class)
    public ResponseEntity<?> handleRegistrationDifferentUser(RegistrationOfDifferentUserException ex, WebRequest request) {
        return basicResponse(getLanguage(), "ERROR_REGISTRATION_DIFFERENT_USER", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AlreadyCanceledException.class)
    public ResponseEntity<?> handleAlreadyCanceled(AlreadyCanceledException ex, WebRequest request) {
        return basicResponse(getLanguage(), "ERROR_ALREADY_CANCELED", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyInWaitlistException.class)
    public ResponseEntity<?> handleAlreadyInWaitlist(AlreadyInWaitlistException ex, WebRequest request) {
        return basicResponse(getLanguage(), "ERROR_ALREADY_IN_WAITLIST", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyParticipantException.class)
    public ResponseEntity<?> handleAlreadyParticipant(AlreadyParticipantException ex, WebRequest request) {
        return basicResponse(getLanguage(), "ERROR_ALREADY_PARTICIPANT", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongOneTimeCodeException.class)
    public ResponseEntity<?> handleWrongOneTimeCode(WrongOneTimeCodeException ex, WebRequest request) {
        return basicResponse(getLanguage(), "ERROR_INVALID_ONE_TIME_CODE", HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParameter(MissingServletRequestParameterException ex, WebRequest request) {
        System.err.println(ex.getMessage());
        return basicResponse(getLanguage(), "ERROR_MISSING_PARAMETER", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<?> handleCategoriaNotFound(CategoryNotFoundException ex, WebRequest request) {
        return basicResponse(getLanguage(), "ERROR_CATEGORY_NOT_FOUND", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<?> handleCategoriaAlreadyExists(CategoryAlreadyExistsException ex, WebRequest request) {
        return basicResponse(getLanguage(), "ERROR_CATEGORY_ALREADY_EXISTS", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        System.err.println(ex.getMessage());
        return basicResponse(getLanguage(), "ERROR_WRONG_ARGUMENT_TYPE", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        System.err.println(ex.getMessage());
        return basicResponse(getLanguage(), "ERROR_MESSAGE_NOT_READABLE", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        System.err.println(ex.getMessage());
        return basicResponse(getLanguage(), "ERROR_REQUEST_METHOD_NOT_SUPPORTED", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<?> handleMissingPathVariable(MissingPathVariableException ex, WebRequest request) {
        System.err.println(ex.getMessage());
        return basicResponse(getLanguage(), "ERROR_MISSING_PATH_VARIABLE", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        System.err.println(ex.getMessage());
        return basicResponse(getLanguage(), "ERROR_DATA_INTEGRITY_VIOLATION", HttpStatus.CONFLICT);
    }
}
