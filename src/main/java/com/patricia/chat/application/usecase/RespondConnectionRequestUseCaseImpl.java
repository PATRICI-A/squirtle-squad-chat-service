package com.patricia.chat.application.usecase;

import com.patricia.chat.domain.exceptions.ConnectionNotFoundException;
import com.patricia.chat.domain.exceptions.UnauthorizedChatAccessException;
import com.patricia.chat.domain.model.Connection;
import com.patricia.chat.domain.model.ConnectionStatus;
import com.patricia.chat.domain.ports.in.RespondConnectionRequestUseCase;
import com.patricia.chat.domain.ports.out.ConnectionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RespondConnectionRequestUseCaseImpl implements RespondConnectionRequestUseCase {

    private final ConnectionRepositoryPort connectionRepository;

    public RespondConnectionRequestUseCaseImpl(ConnectionRepositoryPort connectionRepository) {
        this.connectionRepository = connectionRepository;
    }

    @Override
    public Connection respond(UUID connectionId, UUID addresseeId, ConnectionStatus decision) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new ConnectionNotFoundException(connectionId));

        // Solo el destinatario puede responder
        if (!connection.getAddresseeId().equals(addresseeId)) {
            throw new UnauthorizedChatAccessException(addresseeId.toString(), connectionId.toString());
        }

        if (decision == ConnectionStatus.ACCEPTED) {
            connection.accept();
        } else {
            connection.reject();
        }

        return connectionRepository.save(connection);
    }
}
