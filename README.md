# **MyBatis-Agent**

### **MyBatis自适应多数据源代理组件，已接入Springboot starter。**

## 特性

1.接入SpringBoot Starter，实现自动配置；

2.多数据源切换，支持任意数量数据源，兼容不同MySQL版本、不同驱动；

3.注解式声明所用数据源，注解支持类、类方法

4.自适应数据源，可在不配置数据源的情况下自动识别应该查询的数据源

5.支持自定义第三方拦截器

## 版本

| 版本号 | 特性 |
| --- | --- |
| 0.0.1-SNAPSHOT | 初始化版本，具备上述基础功能 |
|  |  |

### 用法-Maven

1.在pom文件中加入Springboot starter依赖

```xml
<dependency>
    <groupId>boylen.agent</groupId>
    <artifactId>MyBatis-Agent-SpringBoot-Starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

2.在Spring配置文件中进行配置

```bash
# 配置数据源一
agent.datasource.jdbcUrl[0]=jdbc:mysql://host1:port1/tableName1?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&autoReconnect=true
agent.datasource.driverClassName[0]=com.mysql.cj.jdbc.Driver
agent.datasource.username[0]=username1
agent.datasource.password[0]=password1

# 配置数据源二
agent.datasource.jdbcUrl[1]=jdbc:mysql://host2:port2/tableName2?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&autoReconnect=true
agent.datasource.driverClassName[1]=com.mysql.cj.jdbc.Driver
agent.datasource.username[1]=username2
agent.datasource.password[1]=password2

# 是否打印SQL语句
agent.properties.printSql=true
# mapper位置
agent.properties.mapperLocations=classpath*:/mapper/*Mapper.xml
```

3.在Mapper接口中添加注解`@SourceAgent(database = "数据表名")`，注解可以添加到方法上，也可以添加到接口类上，抑或不添加。如果不添加任何注解，MyBatis-Agent将会自适应匹配对应的数据源。

```java
// 添加到方法上
@SourceAgent(database = "tableName1")
Integer countGoodsNum();

// 添加到接口类上
@Repository
@SourceAgent(database = "tableName2")
public interface GoodsMapper {
    Integer countGoodsNum();
}
```