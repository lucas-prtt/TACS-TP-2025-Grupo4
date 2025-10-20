package org.middleware;

import org.exceptions.*;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ott.InvalidOneTimeTokenException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.utils.HttpErrorResponseBuilder;
import org.utils.I18nManager;

@ControllerAdvice
public class GlobalExceptionHandler {
    public ResponseEntity<?> basicResponse(String lang, String key, HttpStatus status){
        return HttpErrorResponseBuilder.simpleError(I18nManager.get(key, lang), status);
    }
    public String getLanguage(){
        return LocaleContextHolder.getLocale().getLanguage();
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex, WebRequest request) {
        return basicResponse(getLanguage(), "INTERNAL_SERVER_ERROR", HttpStatus.BAD_REQUEST);

    }
    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<?> hendleAccountAlreadyExists(AccountAlreadyExistsException ex, WebRequest request){
        return basicResponse(getLanguage(), "ERROR_USER_ALREADY_EXISTS", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<?> handleAccountNotFound(AccountNotFoundException ex, WebRequest request) {
        return basicResponse(getLanguage(), "ERROR_ACCOUNT_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(EventWithNullFieldsException.class)
    public ResponseEntity<?> handleEventWithNullFields(EventWithNullFieldsException ex, WebRequest request) {
        return HttpErrorResponseBuilder.simpleBadRequest(I18nManager.get("ERROR_EVENT_NULL_FIELDS", getLanguage(), String.join(", ", ex.getProblems().stream().map(p->I18nManager.get(p, getLanguage())).toList())));
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
        return HttpErrorResponseBuilder.simpleBadRequest(I18nManager.get("ERROR_WEAK_PASSWORD", getLanguage(), String.join("; ", ex.getProblems().stream().map(prob -> I18nManager.get(prob, getLanguage())).toList())));
    }
    @ExceptionHandler(InvalidOneTimeTokenException.class)
    public ResponseEntity<?> handleInvalidOneTimeCode(InvalidOneTimeTokenException ex, WebRequest request) {
        return basicResponse(getLanguage(), "ERROR_INVALID_ONE_TIME_CODE", HttpStatus.UNAUTHORIZED);
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
}
