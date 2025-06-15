package com.chairpick.ecommerce.config;

import com.chairpick.ecommerce.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(DomainValidationException.class)
    public ResponseEntity<ErrorResponse> handleDomainValidationException(DomainValidationException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleEntityNotFoundException(EntityNotFoundException e) {
        ModelAndView view = new ModelAndView();
        view.setViewName("notFound.html");
        view.addObject("message", e.getMessage());
        return view;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNoHandlerFoundException(NoHandlerFoundException e) {
        ModelAndView view = new ModelAndView();
        view.setViewName("notFound.html");
        view.addObject("message", e.getMessage());
        return view;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e, HttpServletRequest request, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());


        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Unauthenticated or Unauthorized"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ModelAndView handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ModelAndView view = new ModelAndView();
        view.setViewName("notFound.html");
        view.addObject("message", e.getMessage());
        return view;
    }

    @ExceptionHandler(AmountExceedsException.class)
    public ResponseEntity<ErrorResponse> handleAmountExceedsException(AmountExceedsException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView handleNoResourceFoundException(NoResourceFoundException e) {
        ModelAndView view = new ModelAndView();
        view.setViewName("notFound.html");
        view.addObject("message", e.getMessage());
        return view;
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidRequestException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }
}

record ErrorResponse(String message) {
}
