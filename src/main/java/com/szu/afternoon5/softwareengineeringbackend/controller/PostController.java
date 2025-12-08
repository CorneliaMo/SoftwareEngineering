package com.szu.afternoon5.softwareengineeringbackend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 帖子内容控制器，负责帖子创建、查询与更新等操作。
 * <p>
 * 未来可添加分页查询、媒体上传、草稿保存等接口，并与 {@code Post}、{@code PostMedia} 等实体联动。
 */
@RestController
@RequestMapping("/posts")
public class PostController {
}
