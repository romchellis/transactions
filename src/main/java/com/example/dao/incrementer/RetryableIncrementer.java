package com.example.dao.incrementer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RetryableIncrementer {

    private final HibernateMoneyIncrementer incrementer;

    @Retryable(maxAttempts = 100)
    public void add(int recipientId, int amount) {

        // select * from users;
        // update users set balance = 150 where id = 1 and where version = 1;
        // 0 rows affected
        incrementer.add(recipientId, amount);
    }
}
