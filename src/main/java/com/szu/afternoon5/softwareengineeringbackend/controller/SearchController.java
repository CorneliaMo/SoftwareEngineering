package com.szu.afternoon5.softwareengineeringbackend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 搜索接口控制器，负责统一的检索入口。
 * <p>
 * 后续可对接全文搜索或标签过滤能力，并支持排序、分页等查询参数的扩展。
 */
@RestController
@RequestMapping("/search")
public class SearchController {
}
