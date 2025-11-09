package com.promoit.finance.finance_manager.cli;

import com.promoit.finance.finance_manager.service.FinanceService;
import com.promoit.finance.finance_manager.service.UserService;
import org.springframework.shell.standard.ShellComponent;
import java.util.UUID;

@ShellComponent
public class FinanceCLI {

    private final FinanceService financeService;
    private final UserService userService;
    private UUID currentWalletId;
    private String currentUsername;

    public FinanceCLI(FinanceService financeService, UserService userService) {
        this.financeService = financeService;
        this.userService = userService;
    }


}