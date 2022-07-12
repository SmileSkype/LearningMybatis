package com.smile.skype.learnmybatis.test;

import com.smile.skype.learnmybatis.bean.Department;
import com.smile.skype.learnmybatis.bean.Employee;
import com.smile.skype.learnmybatis.dao.DepartmentMapper;
import com.smile.skype.learnmybatis.dao.EmployeeMapper;
import com.smile.skype.learnmybatis.dao.EmployeeMapperAnnotation;
import com.smile.skype.learnmybatis.dao.EmployeeMapperPlus;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 源码测试类
 */
public class MybatisSourceTest {

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
     * 1、获取sqlSessionFactory对象:
     * 		解析文件的每一个信息保存在Configuration中，返回包含Configuration的DefaultSqlSession；
     * 		注意：【MappedStatement】：代表一个增删改查的详细信息
     *
     * 2、获取sqlSession对象
     * 		返回一个DefaultSQlSession对象，包含Executor和Configuration;
     * 		这一步会创建Executor对象；
     *
     * 3、获取接口的代理对象（MapperProxy）
     * 		getMapper，使用MapperProxyFactory创建一个MapperProxy的代理对象
     * 		代理对象里面包含了，DefaultSqlSession（Executor）
     * 4、执行增删改查方法
     *
     * 总结：
     * 	1、根据配置文件（全局，sql映射）初始化出Configuration对象
     * 	2、创建一个DefaultSqlSession对象，
     * 		他里面包含Configuration以及
     * 		Executor（根据全局配置文件中的defaultExecutorType创建出对应的Executor）
     *  3、DefaultSqlSession.getMapper（）：拿到Mapper接口对应的MapperProxy；
     *  4、MapperProxy里面有（DefaultSqlSession）；
     *  5、执行增删改查方法：
     *  		1）、调用DefaultSqlSession的增删改查（Executor）；
     *  		2）、会创建一个StatementHandler对象。
     *  			（同时也会创建出ParameterHandler和ResultSetHandler）
     *  		3）、调用StatementHandler预编译参数以及设置参数值;
     *  			使用ParameterHandler来给sql设置参数
     *  		4）、调用StatementHandler的增删改查方法；
     *  		5）、ResultSetHandler封装结果
     *  注意：
     *  	四大对象每个创建的时候都有一个interceptorChain.pluginAll(parameterHandler);
     *
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        logger.info("test1 start ...");
        // 1、获取sqlSessionFactory对象
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        // 2、获取sqlSession对象
        /**
         *
         */
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            // 3、获取接口的实现类对象
            //会为接口自动的创建一个代理对象，代理对象去执行增删改查方法
            EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee employee = mapper.getEmplById(1);
            mapper.getEmpByIdAndLastName(1,"tom");
            System.out.println(employee);
            System.out.println(mapper.getClass());
        } finally {
            sqlSession.close();
        }
        logger.info("test1 end ...");
    }




}

