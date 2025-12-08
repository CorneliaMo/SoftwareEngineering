package com.szu.afternoon5.softwareengineeringbackend.error;

/**
 * 统一错误码定义。
 * <p>
 * 约定：
 * - 5 位整数，前三位为 HTTP 状态码，后两位为业务细分码。
 * - 2xx 一般只保留 20000 作为成功码，其余通过 err_msg 描述。
 * - 4xx 表示客户端请求或权限问题。
 * - 5xx 表示服务端异常。
 */
public enum ErrorCode {

    // 2xx 成功
    SUCCESS(20000, "成功"),

    // 4xx：客户端错误

    /** 请求参数不合法（通用参数错误 / 业务校验失败） */
    BAD_REQUEST(40000, "请求参数错误"),

    /** 参数格式、范围等校验失败（比 BAD_REQUEST 略具体，但仍不指向某个字段） */
    VALIDATION_FAILED(40001, "参数校验失败"),

    /** 认证失败（账号或密码错误、登录失败等） */
    INVALID_CREDENTIALS(40002, "账号或密码错误"),

    /** 资源已存在（如用户名已占用、重复操作等） */
    DUPLICATE_RESOURCE(40003, "资源已存在"),

    // 401：未认证 / 登录态问题
    /** 未登录或 token 缺失/无效 */
    UNAUTHORIZED(40100, "未认证或认证已失效"),

    /** 认证凭证无效（JWT 解析失败、签名错误等） */
    TOKEN_INVALID(40101, "认证凭证无效"),

    /** 认证凭证过期 */
    TOKEN_EXPIRED(40102, "认证凭证已过期"),

    // 403：权限不足
    /** 拒绝访问（例如普通用户访问管理员接口；管理员尝试删除自己等） */
    FORBIDDEN(40300, "没有访问该资源的权限"),

    // 404：资源不存在
    /** 通用资源不存在（用户不存在、帖子不存在、评论不存在等统一使用） */
    NOT_FOUND(40400, "资源不存在"),

    // 409：状态冲突
    /** 当前资源状态不允许进行该操作（如重复评分、状态机冲突等） */
    CONFLICT(40900, "当前状态不允许执行该操作"),

    // 429：限流
    /** 请求过于频繁（例如 /me/comments 使用场景） */
    TOO_MANY_REQUESTS(42900, "请求过于频繁，请稍后再试"),

    // 5xx：服务端错误
    /** 未预期的服务端异常 */
    INTERNAL_ERROR(50000, "服务器内部错误"),

    /** 下游依赖不可用、服务降级等 */
    SERVICE_UNAVAILABLE(50300, "服务暂不可用，请稍后再试");

    private final int code;
    private final String defaultMessage;

    ErrorCode(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public int code() {
        return code;
    }

    public String defaultMessage() {
        return defaultMessage;
    }
}