package com.volodymyr.ppm.provider;

import org.springframework.stereotype.Service;

import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;

@Service
public class TfaProviderImpl implements TfaProvider {
	private final SecretGenerator generator = new DefaultSecretGenerator();

	@Override
	public String generateTfaSecret() {
		return generator.generate();
	}

}
