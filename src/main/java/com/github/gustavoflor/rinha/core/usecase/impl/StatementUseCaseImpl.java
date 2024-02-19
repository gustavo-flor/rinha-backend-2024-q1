package com.github.gustavoflor.rinha.core.usecase.impl;

import com.github.gustavoflor.rinha.core.exception.NotFoundException;
import com.github.gustavoflor.rinha.core.service.CustomerService;
import com.github.gustavoflor.rinha.core.service.TransferService;
import com.github.gustavoflor.rinha.core.usecase.StatementUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import static com.github.gustavoflor.rinha.core.config.CacheConfig.GET_STATEMENT_KEY;

@Component
@RequiredArgsConstructor
public class StatementUseCaseImpl implements StatementUseCase {

    private final CustomerService customerService;
    private final TransferService transferService;

    @Override
    @Cacheable(value = GET_STATEMENT_KEY, key = "#input.customerId()", sync = true)
    public Output execute(final Input input) {
        final var customer = customerService.findById(input.customerId())
            .orElseThrow(NotFoundException::new);
        final var latestTransfers = transferService.findLatest(customer.getId());
        return new Output(customer, latestTransfers);
    }

}
