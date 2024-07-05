package com.becb.processnewpoint.service;

import com.becb.processnewpoint.domain.User;
import com.becb.processnewpoint.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(
        locations = {"classpath:application-test.properties"},
        properties = {"key=value"})
public class UserServiceTest {

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void testNoUserFound(){
        when(userRepository.findByUserByInstgram(any())).thenReturn(new ArrayList<>());

        User user = userService.getUserByInstagramId("any");
        Assertions.assertThat(user).isNull();

    }
}
