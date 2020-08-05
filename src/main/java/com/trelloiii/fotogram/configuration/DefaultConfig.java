package com.trelloiii.fotogram.configuration;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.zip.CRC32;

@Configuration
@EnableR2dbcRepositories
public class DefaultConfig {


//    @Bean
//    public ConnectionFactory connectionFactory(){
//        String url = "mysql://root:root@127.0.0.1:3306/fotogram";
//        return new
//    }

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
