package com.patricia.chat.application.usecase;

import com.patricia.chat.domain.exceptions.ConnectionAlreadyExistsException;
import com.patricia.chat.domain.model.Connection;
import com.patricia.chat.domain.ports.in.SendConnectionRequestUseCase;
import com.patricia.chat.domain.ports.out.ConnectionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SendConnectionRequestUseCaseImpl implements SendConnectionRequestUseCase {

    private final ConnectionRepositoryPort connectionRepository;

    public SendConnectionRequestUseCaseImpl(ConnectionRepositoryPort connectionRepository) {
        this.connectionRepository = connectionRepository;
    }

    @Override
    public Connection sendRequest(UUID requesterId, UUID addresseeId) {
        if (connectionRepository.existsBetween(requesterId, addresseeId)) {
            throw new ConnectionAlreadyExistsException(
                    requesterId.toString(), addresseeId.toString());
        }
        return connectionRepository.save(new Connection(requesterId, addresseeId));
    }
}
