package com.volodymyr.ppm.provider;

import dev.samstevens.totp.exceptions.QrGenerationException;

public interface TfaProvider {
	String generateTfaSecret();
	String getTfaQrCode(String userName, String secretCode) throws QrGenerationException;
	boolean isTfaCodeValid(String secretCode, String tfaCode);
}
