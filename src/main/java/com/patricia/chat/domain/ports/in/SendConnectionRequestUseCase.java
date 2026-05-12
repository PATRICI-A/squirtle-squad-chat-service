package com.patricia.chat.domain.ports.in;

import com.patricia.chat.domain.model.Connection;
import java.util.UUID;

public interface SendConnectionRequestUseCase {
    Connection sendRequest(UUID requesterId, UUID addresseeId);
}
