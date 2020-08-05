package com.trelloiii.fotogram.service;

import com.trelloiii.fotogram.dto.UserProfileDto;
import com.trelloiii.fotogram.model.User;
import com.trelloiii.fotogram.repository.UserRepository;
import com.trelloiii.fotogram.service.facade.UserServiceFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;
    private final UserServiceFacade userServiceFacade;

    public UserService(UserRepository userRepository, UserServiceFacade userServiceFacade) {
        this.userRepository = userRepository;
        this.userServiceFacade = userServiceFacade;
    }

    public Mono<UserProfileDto> getUserProfile(String username) {
        return userRepository.getUserWithCountOfAllSubs(username)
                .map(userEntity-> new UserProfileDto(userEntity.getEntity(),userEntity.getMetadata()));
//        return userServiceFacade.getUserAndHisPhotos(username,PageRequest.of(0,9, Sort.Direction.DESC,"id"))
//                .flatMap(tuple->{
//                    var container = tuple.getT1();
//                    var page = tuple.getT2();
//                    UserProfileDto userProfileDto = new UserProfileDto(
//                            container.getEntity(),
//                            page,
//                            container.getMetadata()
//                    );
//                    return Mono.just(userProfileDto);
//                });
    }

    @Override
    public Mono<UserDetails> findByUsername(String s) {
        return userRepository.findUserByUsername(s)
                .cast(UserDetails.class);
    }
    public Mono<User> getUser(String username) {
        return userRepository.findUserByUsername(username);
    }

    public Mono<Page<User>> getUserSubscribers(String username, Pageable pageable) {
        return userRepository.findUserSubscribers(username,pageable);
    }

    public Mono<Page<User>> getUserSubscriptions(String username, Pageable pageable) {
        return userRepository.findUserSubscriptions(username,pageable);
    }
}
