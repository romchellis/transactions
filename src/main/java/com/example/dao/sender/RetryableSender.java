package com.example.dao.sender;

import com.example.MoneySender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetryableSender implements MoneySender {

    private final HibernateMoneySender origin;

    @Override
    @Retryable(maxAttempts = 100)
    public void send(int sender, int recipient, int sum) {
        log.info("Sending {} from {} to {}", sender, recipient, sum);
        origin.send(sender, recipient, sum);
    }

}
