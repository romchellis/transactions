package com.example.dao.sender;

import com.example.MoneySender;
import com.example.dao.hibernate.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HibernateMoneySender implements MoneySender {

    private final UserRepository db;

    @Override
    @SneakyThrows
    @Transactional(isolation = Isolation.DEFAULT)
    public void send(int senderId, int recipientId, int amount) {
        try {
            var sender = db.getReferenceById(senderId);
            var recipient = db.getReferenceById(recipientId);
            sender.setBalance(sender.getBalance() - amount);
            recipient.setBalance(recipient.getBalance() + amount);
            db.saveAllAndFlush(List.of(sender, recipient));
        } catch (Exception e) {
            log.info(String.valueOf(e));
        }
    }
}
