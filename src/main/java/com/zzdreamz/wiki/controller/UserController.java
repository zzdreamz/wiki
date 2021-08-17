package com.zzdreamz.wiki.controller;

import com.alibaba.fastjson.JSONObject;
import com.zzdreamz.wiki.req.UserLoginReq;
import com.zzdreamz.wiki.req.UserQueryReq;
import com.zzdreamz.wiki.req.UserResetPasswordReq;
import com.zzdreamz.wiki.req.UserSaveReq;
import com.zzdreamz.wiki.resp.CommonResp;
import com.zzdreamz.wiki.resp.PageResp;
import com.zzdreamz.wiki.resp.UserLoginResp;
import com.zzdreamz.wiki.resp.UserQueryResp;
import com.zzdreamz.wiki.service.UserService;
import com.zzdreamz.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SnowFlake snowFlake;

    @GetMapping("/list")
    public CommonResp list(@Valid UserQueryReq req) {
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>();
        PageResp<UserQueryResp> pageResp = userService.list(req);
        resp.setContent(pageResp);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody UserSaveReq req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        userService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        userService.delete(id);
        return resp;
    }

    @PostMapping("/reset-password")
    public CommonResp resetPassword(@Valid @RequestBody UserResetPasswordReq req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        userService.resetPassword(req);
        return resp;
    }

    @PostMapping("/login")
    public CommonResp login(@Valid @RequestBody UserLoginReq req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<UserLoginResp> resp = new CommonResp<>();
        UserLoginResp userLoginResp = userService.login(req);

        // 登陆成功后需要将登录的用户信息放入到redis中
        Long token = snowFlake.nextId();
        redisTemplate.opsForValue().set(token.toString(), JSONObject.toJSONString(userLoginResp), 3600 * 24, TimeUnit.SECONDS);
        LOG.info("登陆成功，生成token: {} => 登录用户信息: {}", token,userLoginResp);

        resp.setContent(userLoginResp);
        return resp;
    }
}
