package com.eltropy.banking.scheduler;

import com.eltropy.banking.constants.AccountStatus;
import com.eltropy.banking.controller.AccountController;
import com.eltropy.banking.entity.Account;
import com.eltropy.banking.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@EnableScheduling
public class InterestScheduler {

    public static final String CLASS_NAME = InterestScheduler.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(InterestScheduler.class);

    @Autowired
    AccountRepository accountRepository;

    @Value("${interest.rate:}")
    private Float interestRate;

//    Defaulted to run once in January
    @Scheduled(cron = "${interest.rate.calculation.cron:1 1 1 1 1 1}")
    public void addInterest() {

        logger.info("Interest calculation trigerred", CLASS_NAME);

        List<Account> accountList = accountRepository.findAllByStatus(AccountStatus.ACTIVE.name());
        for (Account account: accountList) {

            Long finalAmount = account.getAccountBalance() * (1 + (interestRate.longValue()/100));
            account.setAccountBalance(finalAmount);
            accountRepository.save(account);
        }

        logger.info("Interest calculation completed", CLASS_NAME);
    }
}
