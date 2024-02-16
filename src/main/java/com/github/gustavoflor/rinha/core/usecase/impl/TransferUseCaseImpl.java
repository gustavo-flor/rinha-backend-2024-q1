package com.github.gustavoflor.rinha.core.usecase.impl;

import com.github.gustavoflor.rinha.core.exception.NotFoundException;
import com.github.gustavoflor.rinha.core.exception.ServiceUnavailableException;
import com.github.gustavoflor.rinha.core.repository.TransferRepository;
import com.github.gustavoflor.rinha.core.service.CustomerService;
import com.github.gustavoflor.rinha.core.service.LockService;
import com.github.gustavoflor.rinha.core.usecase.TransferUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;

import static java.text.MessageFormat.format;

@Component
@RequiredArgsConstructor
public class TransferUseCaseImpl implements TransferUseCase {

    private static final String TRANSFER_LOCK_KEY_TEMPLATE = "do.transfer.{0}";
    private static final Duration TRY_LOCK_DURATION = Duration.ofMillis(60000);

    private final TransactionTemplate transactionTemplate;
    private final CustomerService customerService;
    private final TransferRepository transferRepository;
    private final LockService lockService;

    @Override
    public Output execute(final Input input) {
        final var lockKey = format(TRANSFER_LOCK_KEY_TEMPLATE, input.customerId());
        try {
            return lockService.tryLock(lockKey, TRY_LOCK_DURATION, () -> transfer(input));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceUnavailableException(e);
        }
    }

    private Output transfer(final Input input) {
        return transactionTemplate.execute(status -> {
            final var customer = customerService.findById(input.customerId()).orElseThrow(NotFoundException::new);
            final var transfer = input.transfer();

            transfer.apply(customer);

            customerService.save(customer);
            transferRepository.save(transfer);

            return new Output(customer);
        });
    }

}
