package com.okstatelibrary.redbud.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex) {
        return "redirect:/error-page";  // Must match your route
    }

//    @ExceptionHandler(ResourceNotFoundException.class)
//    public String handleNotFound(ResourceNotFoundException ex) {
//        return "redirect:/404";
//    }
}
