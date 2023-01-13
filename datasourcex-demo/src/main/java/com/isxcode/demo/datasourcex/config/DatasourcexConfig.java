package com.isxcode.demo.datasourcex.config;

import com.dtstack.dtcenter.loader.client.ClientCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatasourcexConfig {

    @Bean
    public void init() {
        System.out.println("加载Datasourcex");
    }

    @Bean
    public void initDatasourcex() {

        ClientCache.setUserDir("D:\\definesys\\DatasourceX\\core\\pluginLibs");
    }
}
