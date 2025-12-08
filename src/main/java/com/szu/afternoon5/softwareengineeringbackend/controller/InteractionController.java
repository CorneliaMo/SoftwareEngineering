package com.szu.afternoon5.softwareengineeringbackend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户互动相关接口控制器，涵盖点赞、收藏、评论等行为。
 * <p>
 * 后续可在此实现幂等的点赞接口、评论发布与删除、行为统计等能力，并结合实体模型完善参数校验。
 */
@RestController
@RequestMapping("/interactions")
public class InteractionController {
}
