/**
 * Repository 层（数据访问层）。
 *
 * <p>
 * 本包负责与数据库进行交互，是领域对象与持久化层之间的抽象层。
 * Repository 的核心职责是：
 * <ul>
 *     <li>为实体（Entity）提供基础的 CRUD 能力；</li>
 *     <li>定义基于方法命名规则的查询接口；</li>
 *     <li>必要时，通过 @Query 编写自定义 SQL 或 JPQL；</li>
 *     <li>屏蔽底层数据库差异，使上层业务代码专注于业务逻辑。</li>
 * </ul>
 *
 * <h2>工作方式</h2>
 * <p>
 * 项目使用 Spring Data JPA，因此本包中的接口只需继承：
 * <pre>{@code
 * JpaRepository<EntityType, IdType>
 * }</pre>
 * 即可自动获得常用的持久化操作。
 * <br>
 * 除非出现复杂查询需求，否则不应在 Repository 中编写具体实现类。
 *
 * <h2>协作规范</h2>
 * <ul>
 *     <li>Repository 只负责数据读取与存储，不参与业务逻辑。</li>
 *     <li>业务逻辑应放在 Service 层，通过注入 Repository 调用。</li>
 *     <li>请避免在 Repository 直接操作事务，事务边界建议由 Service 控制。</li>
 *     <li>如需编写查询，请优先使用命名查询方法，其次使用 @Query 注解。</li>
 *     <li>尽量不要在 Repository 中返回与数据库结构无关的 DTO，DTO 转换应由 Service 完成。</li>
 * </ul>
 *
 * <h2>包的边界</h2>
 * <p>
 * <ul>
 *     <li><b>Entity 层（entity）：</b> 定义数据库表映射结构，由 Repository 直接消费。</li>
 *     <li><b>Service 层（service）：</b> 调用 Repository 实现业务规则与组合查询。</li>
 *     <li><b>Controller 层：</b> 不应直接访问 Repository，所有数据访问都必须通过 Service。</li>
 *     <li><b>Security 层：</b> 不直接访问 Repository（除非加载用户信息），其余与数据库无关。</li>
 * </ul>
 *
 * <h2>命名规范</h2>
 * <ul>
 *     <li>Repository 接口命名为 <code>XXXRepository</code>；</li>
 *     <li>查询方法遵循 Spring Data JPA 语义，例如：findByUserId、findAllByPostIdOrderByCreatedTimeDesc。</li>
 * </ul>
 *
 * 本包的目标是提供稳定、可维护、可复用的数据访问抽象，使得业务逻辑能够独立于数据库实现演进。
 */
package com.szu.afternoon5.softwareengineeringbackend.repository;