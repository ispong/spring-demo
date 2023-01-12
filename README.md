<h1 align="center">
    spring-demo
</h1>

<h4 align="center">
    Demo For Spring
</h4>


```bash
git clone https://github.com/ispong/spring-demo.git
cd spring-demo && mvn clean install
cd spring-main && mvn clean package
java -jar target/spring-demo.jar
```

```http request
wget http://localhost:8080/hello/say
```