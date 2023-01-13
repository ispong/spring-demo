#### 开发手册

1. 创建模块,模块名为xxx-demo，注意: package路径必须为`com.isxcode.demo`

![20230113144805](https://img.isxcode.com/picgo/20230113144805.png)

![20230113144838](https://img.isxcode.com/picgo/20230113144838.png)

![20230113145328](https://img.isxcode.com/picgo/20230113145328.png)

2. 创建测试接口

```java
@RestController
@RequestMapping("/xxx")
public class XXXController {

    @GetMapping("/test")
    public String test() {
        
        return "执行成功";
    }
}
```

3. 修改spring-main的pom文件

```bash
vim spring-main/pom.xml
```

> 根据自己的demo测试，注释和添加对应的demo依赖即可。

```xml
<dependencies>
    <dependency>
        <groupId>com.isxcode.demo</groupId>
        <artifactId>xxx-demo</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
</dependencies>
```

4. 调用接口开始测试

- http://localhost:8080/xxx/test