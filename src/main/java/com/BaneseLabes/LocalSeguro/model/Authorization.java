package com.BaneseLabes.LocalSeguro.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Authorization {
    private boolean registerVirtualCard;
    private BigDecimal loan;
    private BigDecimal pix;
    private BigDecimal ted;
    private BigDecimal banksplit;
    private boolean changePassword;

    public Authorization() {
    }

    public boolean canRegisterVirtualCard() {
        return this.registerVirtualCard;
    }

    public boolean canChangePassword() {
        return this.changePassword;
    }

    public boolean pixHasLimit() {
        return this.pix.compareTo(BigDecimal.valueOf(-1)) != 0;
    }

    public boolean loanHasLimit() {
        return this.loan.compareTo(BigDecimal.valueOf(-1)) != 0;
    }

    public boolean tedHasLimit() {
        return this.ted.compareTo(BigDecimal.valueOf(-1)) != 0;
    }

    public boolean bankSplitHasLimit() {
        return this.banksplit.compareTo(BigDecimal.valueOf(-1)) != 0;
    }

    public boolean canMakePix() {
        return this.pix.compareTo(BigDecimal.ZERO) != 0;
    }

    public boolean canMakeTed() {
        return this.ted.compareTo(BigDecimal.ZERO) != 0;

    }

    public boolean canMakeBankSplit() {
        return this.banksplit.compareTo(BigDecimal.ZERO) != 0;
    }

    public boolean canMakeLoan() {
        return this.loan.compareTo(BigDecimal.ZERO) != 0;
    }
}

