package com.szu.afternoon5.softwareengineeringbackend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 个人中心控制器，用于获取和更新当前登录用户的信息。
 * <p>
 * 后续可以加入头像上传、账号绑定、个人配置等接口，并结合安全上下文自动识别用户身份。
 */
@RestController
@RequestMapping("/me")
public class MeController {
}
