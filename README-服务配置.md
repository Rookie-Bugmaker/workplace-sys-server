# 多环境服务地址配置说明

## 需求背景

需要配置三套环境（开发、测试、生产）的 Skywalking 链路追踪和 Kibana 日志管理地址，通过后台接口根据服务类型返回对应的服务地址。

## 环境地址配置

| 环境   | Skywalking                          | Kibana                                  |
|--------|-------------------------------------|-----------------------------------------|
| 开发   | http://10.151.255.79:28080/         | http://10.151.255.79:5601/app/home#/    |
| 测试   | http://10.151.255.79:28080/         | http://10.151.255.79:5601/app/home#/    |
| 生产   | http://10.151.255.79:28080/         | http://10.151.255.79:5601/app/home#/    |

> 注：目前三套环境地址相同，如后续环境独立部署，只需修改对应环境的 filters 配置文件中的地址。

## 配置方式

### 配置文件结构

采用 `application.yml` + `filters/*.properties` 的方式：

```
application.yml (通过占位符引用)
    ↓
filters/*.properties (按环境区分)
    ├── dev.properties      # 开发环境
    ├── test.properties     # 测试环境
    └── product.properties  # 生产环境
```

### filters 配置文件

**src/main/resources/filters/dev.properties**
```properties
#Skywalking 链路追踪地址
skywalking.url=http://10.151.255.79:28080/
#Kibana 日志管理地址
kibana.url=http://10.151.255.79:5601/app/home#/
```

**src/main/resources/filters/test.properties**
```properties
#Skywalking 链路追踪地址
skywalking.url=http://10.151.255.79:28080/
#Kibana 日志管理地址
kibana.url=http://10.151.255.79:5601/app/home#/
```

**src/main/resources/filters/product.properties**
```properties
#Skywalking 链路追踪地址
skywalking.url=http://10.151.255.79:28080/
#Kibana 日志管理地址
kibana.url=http://10.151.255.79:5601/app/home#/
```

### application.yml 配置

```yaml
# 服务地址配置（从 filters 配置文件读取）
service:
  address:
    skywalking: @skywalking.url@
    kibana: @kibana.url@
```

### 打包方式

```bash
# 开发环境
mvn clean package -P dev -DskipTests

# 测试环境
mvn clean package -P test -DskipTests

# 生产环境
mvn clean package -P product -DskipTests
```

Maven 打包时会根据 `-P` 参数选择对应的 filters 配置文件，将占位符 `@xxx@` 替换为实际值。

## 后台接口

### 接口说明

**接口地址：** `GET /api/service-config/address`

**请求参数：**

| 参数名      | 类型   | 必填 | 说明                           |
|-------------|--------|------|--------------------------------|
| serviceType | String | 是   | 服务类型：skywalking 或 kibana |

### 请求示例

**1. 获取 Skywalking 地址**

```bash
curl -X GET "http://localhost:11501/api/service-config/address?serviceType=skywalking"
```

**响应：**

```json
{
  "success": true,
  "serviceType": "skywalking",
  "address": "http://10.151.255.79:28080/",
  "message": "获取 skywalking 地址成功"
}
```

**2. 获取 Kibana 地址**

```bash
curl -X GET "http://localhost:11501/api/service-config/address?serviceType=kibana"
```

**响应：**

```json
{
  "success": true,
  "serviceType": "kibana",
  "address": "http://10.151.255.79:5601/app/home#/",
  "message": "获取 kibana 地址成功"
}
```

## 代码文件说明

| 文件路径 | 说明 |
|----------|------|
| `src/main/java/com/crrc/sys/config/EnvironmentConfig.java` | 配置类，读取服务地址配置 |
| `src/main/java/com/crrc/sys/controller/ServiceConfigController.java` | Controller 接口，提供 REST API |
| `src/main/resources/application.yml` | 主配置文件，通过占位符引用 filters 配置 |
| `src/main/resources/filters/dev.properties` | 开发环境本地配置 |
| `src/main/resources/filters/test.properties` | 测试环境本地配置 |
| `src/main/resources/filters/product.properties` | 生产环境本地配置 |

## 注意事项

1. **环境切换：** 通过 Maven 打包时的 `-P` 参数切换环境，打包后配置即确定
2. **配置修改：** 修改 filters 下的配置文件后，需要重新打包才能生效
3. **服务类型：** 接口参数 `serviceType` 不区分大小写（skywalking/SKYWORKING 均可）
4. **地址变更：** 如三套环境地址不同，需在对应的 filters 配置文件中修改
