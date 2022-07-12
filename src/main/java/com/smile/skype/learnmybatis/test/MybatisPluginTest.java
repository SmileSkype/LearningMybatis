package com.smile.skype.learnmybatis.test;

import com.smile.skype.learnmybatis.bean.Employee;
import com.smile.skype.learnmybatis.dao.EmployeeMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * 插件相关测试内容
 */
public class MybatisPluginTest {
    private final static Logger logger = LoggerFactory.getLogger(MybatisSourceTest.class);

    /**
     * 获取SqlSessionFactory
     * 1、获取mybatis-config.xml以流的形式,创建SqlSessionFactoryBuilder对象
     * 2、通过SqlSessionFactoryBuilder的build方法,创建解析器XMLConfigBuilder(实际上就是将Mybatis-config.xml解析为一颗dom树)
     * 3、从parser中获取到configuration根节点
     * 4、以根节点开始,来逐个解析xml中的标签,来填充到configuration对象中
     *  settings  properties  typeAliases plugins plugins objectFactory ... environments databaseIdProvider mappers
     *  4.1 解析mappers中的mapper.xml用的是XMLMapperBuilder,一个mapper.xml对应一个XMLMapperBuilder
     *  4.2 解析mapper中的select|update|insert|delete用的是XMLStatementBuilder,最终将每个 增删改查语句组装成一个MappedStatement对象
     * 5、创建一个DefaultSqlSessionFactory对象,并且将configuration对象当作参数传入,然后生成SqlSessionFactory
     */
    public SqlSessionFactory getSqlSessionFactory() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        return sqlSessionFactory;
    }


    /**
     * 插件开发步骤
     * 1、编写Interceptor的实现类
     * 2、使用@Intercepts注解实现插件签名
     * 3、将写好的插件注册到全局配置文件中
     */
    @Test
    public void test01() throws IOException {
        logger.info("test1 start ...");
        // 1、获取sqlSessionFactory对象
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        // 2、获取sqlSession对象
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            // 3、获取接口的实现类对象
            //会为接口自动的创建一个代理对象，代理对象去执行增删改查方法
            EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee employee = mapper.getEmplById(1);
            System.out.println(employee);
            System.out.println(mapper.getClass());
        } finally {
            sqlSession.close();
        }
        logger.info("test1 end ...");
    }
}
