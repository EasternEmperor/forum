package com.sunlight.forum.controller;

import com.sunlight.forum.dto.AccessTokenDTO;
import com.sunlight.forum.dto.GithubUser;
import com.sunlight.forum.provider.GithubProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizeController {
    @Autowired
    GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;


    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletRequest request
                           ) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        String token = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser user = githubProvider.getUser(token);
        System.out.println(user.getLogin() + " login success!");
        if (user != null) {
            // 登录成功，写cookie和session
            request.getSession().setAttribute("user", user);
        } else {
            // 登录失败
            request.getSession().invalidate();
        }
        return "redirect:/";
    }
}
