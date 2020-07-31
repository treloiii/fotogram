package com.trelloiii.fotogram.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.zip.CRC32;

@Configuration
public class DefaultConfig {


    @Bean("crcEncoder")
    public PasswordEncoder crcEncoder(){
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence charSequence) {
                CRC32 crc32 = new CRC32();
                crc32.update(charSequence.toString().getBytes());
                return String.valueOf(crc32.getValue());
            }

            @Override
            public boolean matches(CharSequence charSequence, String s) {
                CRC32 crc32 = new CRC32();
                crc32.update(charSequence.toString().getBytes());
                long hash = crc32.getValue();
                return hash == Long.parseLong(s);
            }
        };
    }

}
