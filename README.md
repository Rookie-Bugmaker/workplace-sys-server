# scps-sys-server

## 1.简介

系统服务

由于基础服务我们不提供源代码，所以芜湖项目的基础服务源码位于公司服务器上，开发人员对源码的修改都只能在公司的gitlab上进行克隆。

## 2.部署操作

本地修改代码完成后，需要对服务进行打包操作，然后将打包完成的jar复制到底盘gitlab上对应的项目中即可。

打包命令：

```sh
# 开发环境
mvn clean package -P dev
# 测试环境
mvn clean package -P test
# 正式环境
mvn clean package -P product
```