package com.patricia.chat.rest.controller;

import com.patricia.chat.application.dto.request.ConnectionRequestDto;
import com.patricia.chat.application.dto.request.RespondConnectionDto;
import com.patricia.chat.application.dto.response.ConnectionResponse;
import com.patricia.chat.application.dto.response.MessageResponse;
import com.patricia.chat.application.mapper.ConnectionMapper;
import com.patricia.chat.application.mapper.MessageMapper;
import com.patricia.chat.domain.model.Connection;
import com.patricia.chat.domain.model.ConnectionStatus;
import com.patricia.chat.domain.model.Message;
import com.patricia.chat.domain.model.MessageType;
import com.patricia.chat.domain.ports.in.GetConnectionsUseCase;
import com.patricia.chat.domain.ports.in.GetMessageHistoryUseCase;
import com.patricia.chat.domain.ports.in.RespondConnectionRequestUseCase;
import com.patricia.chat.domain.ports.in.SendConnectionRequestUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRestControllersTest {

    @Mock SendConnectionRequestUseCase sendConnectionRequestUseCase;
    @Mock RespondConnectionRequestUseCase respondConnectionRequestUseCase;
    @Mock GetConnectionsUseCase getConnectionsUseCase;
    @Mock ConnectionMapper connectionMapper;

    @Mock GetMessageHistoryUseCase getMessageHistoryUseCase;
    @Mock MessageMapper messageMapper;

    @InjectMocks ConnectionController connectionController;
    @InjectMocks MessageController messageController;

    private Authentication auth(UUID userId) {
        Authentication a = mock(Authentication.class);
        when(a.getName()).thenReturn(userId.toString());
        return a;
    }

    @Test
    void sendRequest_returnsCreated() {
        UUID requesterId = UUID.randomUUID();
        UUID addresseeId = UUID.randomUUID();

        ConnectionRequestDto dto = new ConnectionRequestDto();
        dto.setAddresseeId(addresseeId);

        Connection connection = new Connection();
        connection.setId(UUID.randomUUID());
        ConnectionResponse response = new ConnectionResponse();
        response.setId(connection.getId());

        when(sendConnectionRequestUseCase.sendRequest(requesterId, addresseeId)).thenReturn(connection);
        when(connectionMapper.toResponse(connection)).thenReturn(response);

        ResponseEntity<ConnectionResponse> result = connectionController.sendRequest(dto, auth(requesterId));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isSameAs(response);
        verify(sendConnectionRequestUseCase).sendRequest(requesterId, addresseeId);
    }

    @Test
    void respond_returnsOk() {
        UUID connectionId = UUID.randomUUID();
        UUID addresseeId = UUID.randomUUID();

        RespondConnectionDto dto = new RespondConnectionDto();
        dto.setStatus(ConnectionStatus.ACCEPTED);

        Connection connection = new Connection();
        connection.setId(connectionId);
        ConnectionResponse response = new ConnectionResponse();
        response.setId(connectionId);

        when(respondConnectionRequestUseCase.respond(connectionId, addresseeId, ConnectionStatus.ACCEPTED)).thenReturn(connection);
        when(connectionMapper.toResponse(connection)).thenReturn(response);

        ResponseEntity<ConnectionResponse> result = connectionController.respond(connectionId, dto, auth(addresseeId));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isSameAs(response);
    }

    @Test
    void getConnections_returnsMappedList() {
        UUID userId = UUID.randomUUID();

        Connection connection = new Connection();
        connection.setId(UUID.randomUUID());
        ConnectionResponse response = new ConnectionResponse();
        response.setId(connection.getId());

        when(getConnectionsUseCase.getConnections(userId)).thenReturn(List.of(connection));
        when(connectionMapper.toResponse(connection)).thenReturn(response);

        ResponseEntity<List<ConnectionResponse>> result = connectionController.getConnections(auth(userId));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactly(response);
    }

    @Test
    void getPending_returnsMappedList() {
        UUID userId = UUID.randomUUID();

        Connection connection = new Connection();
        connection.setId(UUID.randomUUID());
        ConnectionResponse response = new ConnectionResponse();
        response.setId(connection.getId());

        when(getConnectionsUseCase.getPendingRequests(userId)).thenReturn(List.of(connection));
        when(connectionMapper.toResponse(connection)).thenReturn(response);

        ResponseEntity<List<ConnectionResponse>> result = connectionController.getPending(auth(userId));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactly(response);
    }

    @Test
    void getHistory_returnsPagedMessages() {
        UUID parcheId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();

        Message message = new Message();
        message.setId(UUID.randomUUID());
        message.setType(MessageType.TEXT);

        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setType(MessageType.TEXT);

        Page<Message> page = new PageImpl<>(List.of(message), PageRequest.of(0, 20), 1);
        when(getMessageHistoryUseCase.getHistory(eq(parcheId), eq(requesterId), any(Pageable.class))).thenReturn(page);
        when(messageMapper.toResponse(message)).thenReturn(response);

        ResponseEntity<Page<MessageResponse>> result = messageController.getHistory(parcheId, 0, 20, auth(requesterId));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getContent()).containsExactly(response);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(getMessageHistoryUseCase).getHistory(eq(parcheId), eq(requesterId), captor.capture());
        assertThat(captor.getValue().getPageNumber()).isEqualTo(0);
        assertThat(captor.getValue().getPageSize()).isEqualTo(20);
        assertThat(captor.getValue().getSort().getOrderFor("sentAt")).isNotNull();
    }
}

