package com.wangfugui.apprentice;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.wangfugui.apprentice.dao.domain.User;
import com.wangfugui.apprentice.dao.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

@SpringBootTest
class ApprenticeApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    void contextLoads() {
        QueryWrapper<User> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.lambda().eq(User::getUsername,"fugui");
        List<User> user = userMapper.selectList(objectQueryWrapper);
        System.out.println(user);
    }

    @Test
    void test() {
        FastAutoGenerator.create("jdbc:mysql://127.0.0.1:3306/apprentice?serverTimezone=PRC", "root", "123456")
                .globalConfig(builder -> {
                    builder.author("masiyi") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .disableOpenDir()
                            .commentDate("yyyy-MM-dd")
                            .outputDir("E:\\msyWorkspace\\Programmer-Apprentice\\apprentice\\src\\main\\java"); // 指定输出目录

                })
                .packageConfig(builder -> {
                    builder.parent("com.wangfugui") // 设置父包名
                            .moduleName("apprentice") // 设置父包模块名
                            .entity("dao.domain")
                            .service("service")
                            .serviceImpl("service.impl")
                            .mapper("dao.mapper")
                            .controller("controller")
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "E:\\msyWorkspace\\Programmer-Apprentice\\apprentice\\src\\main\\resources\\mapper")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("user_copy1"); // 设置需要生成的表名
                })
                .execute();
    }



}
