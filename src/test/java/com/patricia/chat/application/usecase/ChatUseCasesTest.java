package com.patricia.chat.application.usecase;

import com.patricia.chat.domain.exceptions.ConnectionAlreadyExistsException;
import com.patricia.chat.domain.exceptions.ConnectionNotFoundException;
import com.patricia.chat.domain.exceptions.UnauthorizedChatAccessException;
import com.patricia.chat.domain.model.Connection;
import com.patricia.chat.domain.model.ConnectionStatus;
import com.patricia.chat.domain.model.Message;
import com.patricia.chat.domain.model.MessageType;
import com.patricia.chat.domain.ports.out.ConnectionRepositoryPort;
import com.patricia.chat.domain.ports.out.MessageRepositoryPort;
import com.patricia.chat.domain.ports.out.ParcheServicePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatUseCasesTest {

    @Mock ConnectionRepositoryPort connectionRepository;
    @Mock MessageRepositoryPort messageRepository;
    @Mock ParcheServicePort parcheService;

    @InjectMocks SendConnectionRequestUseCaseImpl sendConnectionRequestUseCase;
    @InjectMocks RespondConnectionRequestUseCaseImpl respondConnectionRequestUseCase;
    @InjectMocks GetConnectionsUseCaseImpl getConnectionsUseCase;
    @InjectMocks SendMessageUseCaseImpl sendMessageUseCase;
    @InjectMocks GetMessageHistoryUseCaseImpl getMessageHistoryUseCase;

    @Test
    void sendConnectionRequest_createsAndSavesConnection() {
        UUID requesterId = UUID.randomUUID();
        UUID addresseeId = UUID.randomUUID();

        when(connectionRepository.existsBetween(requesterId, addresseeId)).thenReturn(false);
        when(connectionRepository.save(any(Connection.class))).thenAnswer(inv -> inv.getArgument(0));

        Connection result = sendConnectionRequestUseCase.sendRequest(requesterId, addresseeId);

        assertThat(result.getRequesterId()).isEqualTo(requesterId);
        assertThat(result.getAddresseeId()).isEqualTo(addresseeId);
        assertThat(result.getStatus()).isEqualTo(ConnectionStatus.PENDING);
        verify(connectionRepository).existsBetween(requesterId, addresseeId);
        verify(connectionRepository).save(any(Connection.class));
    }

    @Test
    void sendConnectionRequest_throwsWhenAlreadyExists() {
        UUID requesterId = UUID.randomUUID();
        UUID addresseeId = UUID.randomUUID();

        when(connectionRepository.existsBetween(requesterId, addresseeId)).thenReturn(true);

        assertThatThrownBy(() -> sendConnectionRequestUseCase.sendRequest(requesterId, addresseeId))
                .isInstanceOf(ConnectionAlreadyExistsException.class)
                .hasMessageContaining(requesterId.toString())
                .hasMessageContaining(addresseeId.toString());

        verify(connectionRepository, never()).save(any());
    }

    @Test
    void respondConnectionRequest_acceptsConnection() {
        UUID connectionId = UUID.randomUUID();
        UUID addresseeId = UUID.randomUUID();

        Connection connection = new Connection();
        connection.setId(connectionId);
        connection.setAddresseeId(addresseeId);
        connection.setStatus(ConnectionStatus.PENDING);

        when(connectionRepository.findById(connectionId)).thenReturn(Optional.of(connection));
        when(connectionRepository.save(any(Connection.class))).thenAnswer(inv -> inv.getArgument(0));

        Connection result = respondConnectionRequestUseCase.respond(connectionId, addresseeId, ConnectionStatus.ACCEPTED);

        assertThat(result.getStatus()).isEqualTo(ConnectionStatus.ACCEPTED);
        verify(connectionRepository).save(connection);
    }

    @Test
    void respondConnectionRequest_rejectsConnection() {
        UUID connectionId = UUID.randomUUID();
        UUID addresseeId = UUID.randomUUID();

        Connection connection = new Connection();
        connection.setId(connectionId);
        connection.setAddresseeId(addresseeId);
        connection.setStatus(ConnectionStatus.PENDING);

        when(connectionRepository.findById(connectionId)).thenReturn(Optional.of(connection));
        when(connectionRepository.save(any(Connection.class))).thenAnswer(inv -> inv.getArgument(0));

        Connection result = respondConnectionRequestUseCase.respond(connectionId, addresseeId, ConnectionStatus.REJECTED);

        assertThat(result.getStatus()).isEqualTo(ConnectionStatus.REJECTED);
        verify(connectionRepository).save(connection);
    }

    @Test
    void respondConnectionRequest_throwsWhenNotFound() {
        UUID connectionId = UUID.randomUUID();
        UUID addresseeId = UUID.randomUUID();

        when(connectionRepository.findById(connectionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> respondConnectionRequestUseCase.respond(connectionId, addresseeId, ConnectionStatus.ACCEPTED))
                .isInstanceOf(ConnectionNotFoundException.class)
                .hasMessageContaining(connectionId.toString());

        verify(connectionRepository, never()).save(any());
    }

    @Test
    void respondConnectionRequest_throwsWhenUnauthorized() {
        UUID connectionId = UUID.randomUUID();
        UUID addresseeId = UUID.randomUUID();
        UUID otherUser = UUID.randomUUID();

        Connection connection = new Connection();
        connection.setId(connectionId);
        connection.setAddresseeId(otherUser);
        connection.setStatus(ConnectionStatus.PENDING);

        when(connectionRepository.findById(connectionId)).thenReturn(Optional.of(connection));

        assertThatThrownBy(() -> respondConnectionRequestUseCase.respond(connectionId, addresseeId, ConnectionStatus.ACCEPTED))
                .isInstanceOf(UnauthorizedChatAccessException.class)
                .hasMessageContaining(addresseeId.toString())
                .hasMessageContaining(connectionId.toString());

        verify(connectionRepository, never()).save(any());
    }

    @Test
    void getConnections_returnsBothLists() {
        UUID userId = UUID.randomUUID();
        Connection c1 = new Connection();
        c1.setId(UUID.randomUUID());
        Connection c2 = new Connection();
        c2.setId(UUID.randomUUID());

        when(connectionRepository.findByUserId(userId)).thenReturn(List.of(c1));
        when(connectionRepository.findPendingByAddresseeId(userId)).thenReturn(List.of(c2));

        assertThat(getConnectionsUseCase.getConnections(userId)).containsExactly(c1);
        assertThat(getConnectionsUseCase.getPendingRequests(userId)).containsExactly(c2);
    }

    @Test
    void sendMessage_savesTextMessage() {
        UUID parcheId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();

        when(parcheService.isMember(parcheId, senderId)).thenReturn(true);
        when(messageRepository.save(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));

        Message result = sendMessageUseCase.sendMessage(parcheId, senderId, "Ana", "Hola", null);

        assertThat(result.getType()).isEqualTo(MessageType.TEXT);
        assertThat(result.getImageUrl()).isNull();
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void sendMessage_savesImageMessage() {
        UUID parcheId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();

        when(parcheService.isMember(parcheId, senderId)).thenReturn(true);
        when(messageRepository.save(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));

        Message result = sendMessageUseCase.sendMessage(parcheId, senderId, "Ana", "Foto", "https://img.test/a.png");

        assertThat(result.getType()).isEqualTo(MessageType.IMAGE);
        assertThat(result.getImageUrl()).isEqualTo("https://img.test/a.png");
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void sendMessage_throwsWhenUserIsNotMember() {
        UUID parcheId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();

        when(parcheService.isMember(parcheId, senderId)).thenReturn(false);

        assertThatThrownBy(() -> sendMessageUseCase.sendMessage(parcheId, senderId, "Ana", "Hola", null))
                .isInstanceOf(UnauthorizedChatAccessException.class)
                .hasMessageContaining(senderId.toString())
                .hasMessageContaining(parcheId.toString());

        verify(messageRepository, never()).save(any());
    }

    @Test
    void getHistory_returnsPageWhenUserIsMember() {
        UUID parcheId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 20);

        Message message = new Message();
        message.setId(UUID.randomUUID());
        Page<Message> page = new PageImpl<>(List.of(message), pageable, 1);

        when(parcheService.isMember(parcheId, requesterId)).thenReturn(true);
        when(messageRepository.findByParcheId(parcheId, pageable)).thenReturn(page);

        Page<Message> result = getMessageHistoryUseCase.getHistory(parcheId, requesterId, pageable);

        assertThat(result.getContent()).containsExactly(message);
        verify(messageRepository).findByParcheId(parcheId, pageable);
    }

    @Test
    void getHistory_throwsWhenUserIsNotMember() {
        UUID parcheId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 20);

        when(parcheService.isMember(parcheId, requesterId)).thenReturn(false);

        assertThatThrownBy(() -> getMessageHistoryUseCase.getHistory(parcheId, requesterId, pageable))
                .isInstanceOf(UnauthorizedChatAccessException.class)
                .hasMessageContaining(requesterId.toString())
                .hasMessageContaining(parcheId.toString());

        verify(messageRepository, never()).findByParcheId(any(), any());
    }
}
