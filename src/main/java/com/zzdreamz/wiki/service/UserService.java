package com.zzdreamz.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zzdreamz.wiki.domain.User;
import com.zzdreamz.wiki.domain.UserExample;
import com.zzdreamz.wiki.exception.BusinessException;
import com.zzdreamz.wiki.exception.BusinessExceptionCode;
import com.zzdreamz.wiki.mapper.UserMapper;
import com.zzdreamz.wiki.req.UserQueryReq;
import com.zzdreamz.wiki.req.UserResetPasswordReq;
import com.zzdreamz.wiki.req.UserSaveReq;
import com.zzdreamz.wiki.resp.UserQueryResp;
import com.zzdreamz.wiki.resp.PageResp;
import com.zzdreamz.wiki.util.CopyUtil;
import com.zzdreamz.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> list(UserQueryReq req) {
        // 添加查询条件
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getLoginName())) {
            criteria.andLoginNameLike("%" + req.getLoginName() + "%");
        }
        // pageHelper分页查询
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<User> userList = userMapper.selectByExample(userExample);
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        List<UserQueryResp> list = CopyUtil.copyList(userList, UserQueryResp.class);

        // 使用PageResp来封装返回对象
        PageResp<UserQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    public void save(UserSaveReq req) {
        User user = CopyUtil.copy(req, User.class);

        User userDB = selectByLoginName(req.getLoginName());
        if (ObjectUtils.isEmpty(user.getId())) {
            // 新增
            if (ObjectUtils.isEmpty(userDB)) {
                user.setId(snowFlake.nextId());
                userMapper.insert(user);
            } else {
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }
        } else {
            // 修改
            if (ObjectUtils.isEmpty(userDB) || userDB.getId().equals(req.getId())) {
                req.setPassword(null);
                // Selective表示只会修改不为空的字段
                userMapper.updateByPrimaryKeySelective(user);
            } else {
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }
        }
    }

    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);
    }

    public User selectByLoginName(String loginName) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if (!ObjectUtils.isEmpty(loginName)) {
            criteria.andLoginNameEqualTo(loginName);
        }
        List<User> userList = userMapper.selectByExample(userExample);
        if (CollectionUtils.isEmpty(userList)) {
            return null;
        } else {
            return userList.get(0);
        }
    }

    public void resetPassword(UserResetPasswordReq req) {
        User user = CopyUtil.copy(req, User.class);
        userMapper.updateByPrimaryKeySelective(user);
    }
}
