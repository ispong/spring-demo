<h1 align="center">
    spring-demo
</h1>

<h4 align="center">
    Demo For Spring
</h4>


```bash
git clone https://github.com/ispong/spring-demo.git
cd spring-demo && mvn clean install
cd spring-main && mvn clean package && cd target
java -jar spring-demo.jar --server.port=8080
```

- GET请求

```bash
curl 'http://localhost:8080/hello/say'
```

- POST请求

```bash
curl 'http://localhost:8080/oracle/testConnect' \
  -H 'Accept: application/json, text/plain, */*' \
  -H 'Accept-Language: zh-CN' \
  -H 'Content-Type: application/json;charset=UTF-8' \
  --data-raw '{"jdbcUrl": "jdbc:oracle:thin:@isxcode:XE","username": "ispong","password": "ispong123"}' 
```