package am.itspace.photoshootprojectmanagementweb.exceptionHandler;

import am.itspace.photoshootprojectmanagementcommon.exception.InvalidBookingException;
import am.itspace.photoshootprojectmanagementcommon.exception.UserExistException;
import am.itspace.photoshootprojectmanagementweb.security.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
@ControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(value = {EntityNotFoundException.class,
            UnexpectedTypeException.class,
            NoResourceFoundException.class})
    public ModelAndView defaultErrorHandler(RuntimeException ex,
                                            @AuthenticationPrincipal CurrentUser currentUser) {

        ModelAndView mav = new ModelAndView();

        mav.addObject("exceptionMsg", ex.getMessage());
        mav.setViewName("pages-404");

        log.error("Method was called by {} ", currentUser.getUser().getEmail(), ex);

        return mav;
    }

    @ExceptionHandler(value = {UserExistException.class})
    public ModelAndView defaultExistErrorHandler(RuntimeException ex) {
        ModelAndView mav = new ModelAndView();

        mav.addObject("exceptionMsg", ex.getMessage());
        mav.setViewName("pages-409");

        log.error(String.valueOf(ex));

        return mav;
    }

    @ExceptionHandler(value = InvalidBookingException.class)
    public ModelAndView defaultErrorHandler(InvalidBookingException ex,
                                            @AuthenticationPrincipal CurrentUser currentUser) {
        ModelAndView mav = new ModelAndView();

        mav.addObject("exceptionMsg", ex.getMessage());
        mav.setViewName("pages-400");

        log.error("Method was called by {} ", currentUser.getUser().getEmail(), ex);

        return mav;
    }

    @ExceptionHandler(value = {NullPointerException.class, IOException.class,
            ConstraintViolationException.class, MethodArgumentNotValidException.class,
            FileNotFoundException.class})
    public ModelAndView default500ErrorHandler(InvalidBookingException ex,
                                            @AuthenticationPrincipal CurrentUser currentUser) {
        ModelAndView mav = new ModelAndView();

        mav.addObject("exceptionMsg", ex.getMessage());
        mav.setViewName("pages-500");

        log.error("Method was called by {} ", currentUser.getUser().getEmail(), ex);

        return mav;
    }
}
