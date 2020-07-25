package com.trelloiii.fotogram.dto;

import com.trelloiii.fotogram.exceptions.EntityNotFoundException;
import com.trelloiii.fotogram.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSubs {
    private User user;
    private Set<User> subscribers = new HashSet<>();
    private Set<User> subscriptions = new HashSet<>();

    public void addSubscriber(User u){
        subscribers.add(u);
    }
    public void addSubscription(User u){
        subscriptions.add(u);
    }
    public void addUserIfNotPresent(User u){
        if(Objects.isNull(user)){
            user = u;
        }
    }
}
