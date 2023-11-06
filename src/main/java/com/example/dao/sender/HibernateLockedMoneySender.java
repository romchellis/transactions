package com.example.dao.sender;

import com.example.MoneySender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import java.util.concurrent.locks.Lock;

/**
 * Using distributed locks
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class HibernateLockedMoneySender implements MoneySender {

    private final HibernateMoneySender origin;
    private final RedissonClient redissonClient;

    @Override
    public void send(int sender, int recipient, int sum) {
        Lock lockSender = redissonClient.getLock(String.valueOf(Math.max(sender,recipient)));
        Lock lockRecipient = redissonClient.getLock(String.valueOf(Math.min(sender,recipient)));

        lockSender.lock();
        lockRecipient.lock();

        origin.send(sender, recipient, sum);

        lockSender.unlock();
        lockRecipient.unlock();
    }
}
