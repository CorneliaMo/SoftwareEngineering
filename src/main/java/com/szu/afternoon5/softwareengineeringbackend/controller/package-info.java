/**
 * 提供公共与管理接口的 REST 控制器包。
 * <p>
 * 这里的控制器负责认证登录（如 {@link com.szu.afternoon5.softwareengineeringbackend.controller.AuthController}）、
 * 用户信息与个人中心（{@link com.szu.afternoon5.softwareengineeringbackend.controller.UserController}、
 * {@link com.szu.afternoon5.softwareengineeringbackend.controller.MeController}）、
 * 帖子发布与检索（{@link com.szu.afternoon5.softwareengineeringbackend.controller.PostController}、
 * {@link com.szu.afternoon5.softwareengineeringbackend.controller.SearchController}）、
 * 互动与管理端操作（{@link com.szu.afternoon5.softwareengineeringbackend.controller.InteractionController}、
 * {@link com.szu.afternoon5.softwareengineeringbackend.controller.AdminController}），
 * 并将校验通过的请求交给业务与持久层处理。
 */
package com.szu.afternoon5.softwareengineeringbackend.controller;

