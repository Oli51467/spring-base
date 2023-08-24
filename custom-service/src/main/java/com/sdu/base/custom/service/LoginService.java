package com.sdu.base.custom.service;

public interface LoginService {

    String login(Long uid);

    boolean authenticate(String token);

    Long getUseridByToken(String token);
}
