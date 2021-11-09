package soma.test.waggle.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import soma.test.waggle.dto.MemberListDto;
import soma.test.waggle.jwt.TokenProvider;
import soma.test.waggle.service.MemberService;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest()
@Import(MemberController.class)
@ContextConfiguration(classes = {TokenProvider.class})
@AutoConfigureMockMvc
public class MemberControllerTest {

    @Autowired MockMvc mvc;

    private String jwt = " Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMSIsImF1dGgiOiJST0xFX1VTRVIiLCJleHAiOjE2MzYzMDQ4Njl9.C7_W4uaCkEARowkx2vVKVl0caFHwLAE1Q-VwkMWB48_hJSEcKIuKOXUOH0RNDEJ1eJ19F7fBokb48a5cBuuEqA";

    @MockBean private MemberService memberService;

    @WithMockUser(username = "1")
    @Test
    public void 팔로우_멤버_조회() throws Exception {
        // given
        MemberListDto memberListDto = MemberListDto.builder()
                .size(0)
                .members(new ArrayList<>())
                .build();

        given(memberService.getWhoIsFollower(1L))
                .willReturn(memberListDto);

        // when
        final ResultActions actions = mvc.perform(get("/member/1/follower")
                .header("Authorization", jwt)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(0)))
                .andDo(print());
    }
}
