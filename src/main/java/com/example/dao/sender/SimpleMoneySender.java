package com.example.dao.sender;

import com.example.MoneySender;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import static org.example.transactions.jooq.Tables.USERS;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleMoneySender implements MoneySender {

    private final DSLContext db;

    @Override
    @SneakyThrows
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void send(int sender, int recipient, int amount) {
        int execute = db.update(USERS)
                .set(USERS.BALANCE,
                        DSL.case_()
                                .when(USERS.ID.eq(sender), USERS.BALANCE.minus(amount))
                                .when(USERS.ID.eq(recipient), USERS.BALANCE.plus(amount))
                )
                .where(USERS.ID.in(sender, recipient))
                .execute();
        log.info("Update count {}", execute);
    }
}
