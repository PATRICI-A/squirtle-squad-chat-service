package com.patricia.chat;

import com.patricia.chat.advice.GlobalExceptionHandler;
import com.patricia.chat.domain.exceptions.ConnectionAlreadyExistsException;
import com.patricia.chat.domain.exceptions.ConnectionNotFoundException;
import com.patricia.chat.domain.exceptions.MessageNotFoundException;
import com.patricia.chat.domain.exceptions.UnauthorizedChatAccessException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleUnauthorized_returnsForbidden() {
        UnauthorizedChatAccessException ex = new UnauthorizedChatAccessException("u","m");
        ResponseEntity response = handler.handleUnauthorized(ex);
        assertEquals(403, response.getStatusCodeValue());
        assertTrue(((java.util.Map)response.getBody()).get("message").toString().contains("m"));
    }

    @Test
    void handleDuplicate_returnsConflict() {
        ConnectionAlreadyExistsException ex = new ConnectionAlreadyExistsException("a","b");
        ResponseEntity response = handler.handleDuplicate(ex);
        assertEquals(409, response.getStatusCodeValue());
    }

    @Test
    void handleNotFound_and_handleMsgNotFound_returnNotFound() {
        ConnectionNotFoundException cex = new ConnectionNotFoundException(java.util.UUID.randomUUID());
        ResponseEntity rc = handler.handleNotFound(cex);
        assertEquals(404, rc.getStatusCodeValue());

        MessageNotFoundException mex = new MessageNotFoundException(java.util.UUID.randomUUID());
        ResponseEntity rm = handler.handleMsgNotFound(mex);
        assertEquals(404, rm.getStatusCodeValue());
    }

    @Test
    void handleValidation_buildsConcatenatedMessage() {
        BindingResult br = Mockito.mock(BindingResult.class);
        List<FieldError> errors = Arrays.asList(
                new FieldError("obj","field1","must not be null"),
                new FieldError("obj","field2","bad")
        );
        Mockito.when(br.getFieldErrors()).thenReturn(errors);
        MethodArgumentNotValidException ex = Mockito.mock(MethodArgumentNotValidException.class);
        Mockito.when(ex.getBindingResult()).thenReturn(br);

        ResponseEntity resp = handler.handleValidation(ex);
        assertEquals(400, resp.getStatusCodeValue());
        String msg = ((java.util.Map)resp.getBody()).get("message").toString();
        assertTrue(msg.contains("field1: must not be null"));
        assertTrue(msg.contains("field2: bad"));
    }

    @Test
    void handleGeneral_returnsInternalServerError() {
        ResponseEntity r = handler.handleGeneral(new RuntimeException("boom"));
        assertEquals(500, r.getStatusCodeValue());
        assertEquals("Error interno del servidor", ((java.util.Map)r.getBody()).get("message"));
    }
}
