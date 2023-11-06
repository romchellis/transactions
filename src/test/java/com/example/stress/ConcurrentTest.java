package com.example.stress;

import com.example.dao.incrementer.RetryableIncrementer;
import com.example.dao.sender.HibernateLockedMoneySender;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.example.stress.ConcurrentSimulator.doConcurrently;
import static org.assertj.core.api.Assertions.assertThat;
import static org.example.transactions.jooq.Tables.USERS;
import static org.jooq.impl.DSL.asterisk;

@Slf4j
@SpringBootTest
public class ConcurrentTest {

    @Autowired
    private RetryableIncrementer moneyIncrementer;

    @Autowired
    private HibernateLockedMoneySender moneySender;

    @Autowired
    private DSLContext dslContext;

    @BeforeEach
    void setUp() {
        dslContext.update(USERS)
                .set(USERS.BALANCE, 0)
                .set(USERS.VERSION, 0)
                .execute();
    }


    @RepeatedTest(100)
    @SneakyThrows
    void Balance_should_keep_consistent_using_thousand_parallel_sends() {
        doConcurrently(10, this::sendMoney);

        var sum = dslContext.select(DSL.sum(USERS.BALANCE))
                .from(USERS).fetchOneInto(Integer.class);
        var balances = dslContext.select(USERS.BALANCE)
                .from(USERS).fetchInto(Integer.class);

        assertThat(sum).isZero();
        assertThat(balances).containsExactlyInAnyOrder(1000, -1000);
    }

    @Test
    @SneakyThrows
    void Balance_should_keep_consistent_using_incrementals() {
        doConcurrently(10, this::increment);

        var balance = dslContext.select(asterisk()).from(USERS)
                .where(USERS.ID.eq(1))
                .fetchOne();

        assertThat(balance.get(USERS.BALANCE)).isEqualTo(1000);
    }

    private void increment(Integer integer) {
        moneyIncrementer.add(1, 100);
    }

    private void sendMoney(int index) {
        if (index % 2 == 0) {
            moneySender.send(1, 2, 100);
        } else {
            moneySender.send(2, 1, -100);
        }
    }

}
