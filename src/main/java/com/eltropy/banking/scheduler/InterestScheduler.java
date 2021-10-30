package com.eltropy.banking.scheduler;

import com.eltropy.banking.constants.AccountStatus;
import com.eltropy.banking.constants.ErrorConstants;
import com.eltropy.banking.entity.Account;
import com.eltropy.banking.repository.AccountRepository;
import com.eltropy.banking.util.TransferUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    TransferUtil transferUtil;

//    Defaulted to run once in January
    @Scheduled(cron = "${interest.rate.calculation.cron:1 1 1 1 1 1}")
    public void addInterest() {

        logger.info(ErrorConstants.INTEREST_CALCULATION_TRIGERRED, CLASS_NAME);

        List<Account> accountList = accountRepository.findAllByStatus(AccountStatus.ACTIVE.name());
        for (Account account: accountList) {
            transferUtil.calculateAndIncrement(account);
        }

        logger.info(ErrorConstants.INTEREST_CALCULATION_COMPLETED, CLASS_NAME);
    }


}
