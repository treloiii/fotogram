package com.trelloiii.fotogram.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("user_refresh_token")
public class UserRefreshToken {
    @Id
    private Long id;
    private Long user_id;
    private String token;
}
