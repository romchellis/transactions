package com.example.dao.incrementer;

import com.example.dao.hibernate.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HibernateMoneyIncrementer {

    private final UserRepository db;

    @SneakyThrows
    @Transactional(isolation = Isolation.DEFAULT)
    public void add(int recipientId, int amount) {
        try {
            var recipient = db.getReferenceById(recipientId);
            recipient.setBalance(recipient.getBalance() + amount);
            db.saveAndFlush(recipient);
        } catch (Exception e) {
            log.info(String.valueOf(e));
        }
    }
}
