package com.vladimir.ppm.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PwdGenSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer pwdLength = 8;
    private Boolean numbers = true;
    private Boolean symbols = false;

    public Integer getPwdLength() {
        return pwdLength;
    }

    public Boolean getNumbers() {
        return numbers;
    }

    public Boolean getSymbols() {
        return symbols;
    }

    public void setPwdLength(Integer pwdLength) {
        this.pwdLength = pwdLength;
    }

    public void setNumbers(Boolean numbers) {
        this.numbers = numbers;
    }

    public void setSymbols(Boolean symbols) {
        this.symbols = symbols;
    }
}
