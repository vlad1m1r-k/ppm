package com.vladimir.ppm.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ValidatorServiceImpl.class)
public class ValidatorServiceImplTest {
    @Autowired
    private ValidatorService validatorService;

    @Test
    public void validatePwdSpecialCharTest() {
        assertTrue(validatorService.validatePwdSpecialChar("^%&*!"));
        assertTrue(validatorService.validatePwdSpecialChar("1#23"));
        assertFalse(validatorService.validatePwdSpecialChar("123"));
    }

    @Test
    public void validatePwdComplexityTest() {
        assertFalse(validatorService.validatePwdComplexity("aaaaa"));
        assertFalse(validatorService.validatePwdComplexity("aaBBaa"));
        assertFalse(validatorService.validatePwdComplexity("aa33aa"));
        assertTrue(validatorService.validatePwdComplexity("a33E"));
    }

    @Test
    public void validatePwdLoginIncludedTest() {
        assertTrue(validatorService.validatePwdLoginIncluded("aswert", "wsr"));
        assertFalse(validatorService.validatePwdLoginIncluded("aswert", "wer"));
        assertFalse(validatorService.validatePwdLoginIncluded("wer12t", "wer"));
        assertFalse(validatorService.validatePwdLoginIncluded("as34wer", "wer"));
    }

    @Test
    public void validatePwdRepeatCharsTest() {
        assertTrue(validatorService.validatePwdRepeatChars("asd123%^&"));
        assertTrue(validatorService.validatePwdRepeatChars("asdfgh"));
        assertTrue(validatorService.validatePwdRepeatChars("12345"));
        assertFalse(validatorService.validatePwdRepeatChars("asdfffqw"));
        assertFalse(validatorService.validatePwdRepeatChars("111fffqw"));
        assertFalse(validatorService.validatePwdRepeatChars("asdqw****"));
        assertFalse(validatorService.validatePwdRepeatChars("asdq5555wfgh"));
        assertFalse(validatorService.validatePwdRepeatChars("5555wfgh"));
    }
}