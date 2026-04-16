package com.volodymyr.ppm.provider;

import org.springframework.stereotype.Service;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import dev.samstevens.totp.util.Utils;

@Service
public class TfaProviderImpl implements TfaProvider {
	private final SecretGenerator generator = new DefaultSecretGenerator();
	private final TimeProvider timeProvider = new SystemTimeProvider();
	private final CodeGenerator codeGenerator = new DefaultCodeGenerator();
	private final CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);

	@Override
	public String generateTfaSecret() {
		return generator.generate();
	}
	
	@Override
	public String getTfaQrCode(String userName, String secretCode) throws QrGenerationException {
		QrData data = new QrData.Builder()
				.label(userName)
				.secret(secretCode)
				.digits(6)
				.issuer("PPM")
				.period(30)
				.build();
		QrGenerator qrGenerator = new ZxingPngQrGenerator();
		byte[] qrData = qrGenerator.generate(data);
		return Utils.getDataUriForImage(qrData, secretCode);
	}
	
	@Override
	public boolean isTfaCodeValid(String secretCode, String tfaCode) {
		return verifier.isValidCode(secretCode, tfaCode);
	}
}
