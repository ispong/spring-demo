<h1 align="center">
    spring-demo
</h1>

<h4 align="center">
    更快，更直接，更有效的学习Spring相关后端技术！    
</h4>

#### 启动spring-demo项目

```bash
# 拉取代码
git clone https://github.com/ispong/spring-demo.git

# 安装项目依赖
cd spring-demo && mvn clean install

# 构建项目
cd spring-main && mvn clean package

# 指定端口号，启动项目
java -jar spring-demo.jar --server.port=8080
```

#### 调用spring-demo接口

- get请求样例

```bash
curl 'http://localhost:8080/hello/say'
```

- post请求样例

```bash
curl 'http://localhost:8080/clickhouse/testConnect' \
  -H 'Accept: application/json, text/plain, */*' \
  -H 'Accept-Language: zh-CN' \
  -H 'Content-Type: application/json;charset=UTF-8' \
  --data-raw '{"jdbcUrl": "jdbc:clickhouse://1.1.1.1:1/fanruan_db","username": "","password": ""}' 
```

#### 开发手册

- https://github.com/ispong/spring-demo/blob/main/CONTRIBUTING.md1