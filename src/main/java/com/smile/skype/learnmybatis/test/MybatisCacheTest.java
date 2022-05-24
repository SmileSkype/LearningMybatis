package com.smile.skype.learnmybatis.test;

import com.smile.skype.learnmybatis.bean.Department;
import com.smile.skype.learnmybatis.bean.Employee;
import com.smile.skype.learnmybatis.dao.DepartmentMapper;
import com.smile.skype.learnmybatis.dao.EmployeeDynamicMapper;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class MybatisCacheTest {

    private final static Logger logger = LoggerFactory.getLogger(MybatisCacheTest.class);

    public SqlSessionFactory getSqlSessionFactory() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        return sqlSessionFactory;
    }

    /**
     * 两级缓存：
     * 一级缓存：（本地缓存）：sqlSession级别的缓存。一级缓存是一直开启的；SqlSession级别的一个Map
     * 		与数据库同一次会话期间查询到的数据会放在本地缓存中。
     * 		以后如果需要获取相同的数据，直接从缓存中拿，没必要再去查询数据库；
     *
     * 		一级缓存失效情况（没有使用到当前一级缓存的情况，效果就是，还需要再向数据库发出查询）：
     * 		1、sqlSession不同。
     * 		2、sqlSession相同，查询条件不同.(当前一级缓存中还没有这个数据)
     * 		3、sqlSession相同，两次查询之间执行了增删改操作(这次增删改可能对当前数据有影响)
     * 		4、sqlSession相同，手动清除了一级缓存（缓存清空）
     *
     * 二级缓存：（全局缓存）：基于namespace级别的缓存：一个namespace对应一个二级缓存：
     * 		工作机制：
     * 		1、一个会话，查询一条数据，这个数据就会被放在当前会话的一级缓存中；
     * 		2、如果会话关闭；一级缓存中的数据会被保存到二级缓存中；新的会话查询信息，就可以参照二级缓存中的内容；
     * 		3、sqlSession===EmployeeMapper==>Employee
     * 						DepartmentMapper===>Department
     * 			不同namespace查出的数据会放在自己对应的缓存中（map）
     * 			效果：数据会从二级缓存中获取
     * 				查出的数据都会被默认先放在一级缓存中。
     * 				只有会话提交或者关闭以后，一级缓存中的数据才会转移到二级缓存中
     * 		使用：
     * 			1）、开启全局二级缓存配置：<setting name="cacheEnabled" value="true"/>
     * 			2）、去mapper.xml中配置使用二级缓存：
     * 				<cache></cache>
     * 			3）、我们的POJO需要实现序列化接口
     *
     * 和缓存有关的设置/属性：
     * 			1）、cacheEnabled=true：false：关闭缓存（二级缓存关闭）(一级缓存一直可用的)
     * 			2）、每个select标签都有useCache="true"：
     * 					false：不使用缓存（一级缓存依然使用，二级缓存不使用）
     * 			3）、【每个增删改标签的：flushCache="true"：（一级二级都会清除）】
     * 					增删改执行完成后就会清楚缓存；
     * 					测试：flushCache="true"：一级缓存就清空了；二级也会被清除；
     * 					查询标签：flushCache="false"：
     * 						如果flushCache=true;每次查询之后都会清空缓存；缓存是没有被使用的；
     * 			4）、sqlSession.clearCache();只是清楚当前session的一级缓存；
     * 			5）、localCacheScope：本地缓存作用域：（一级缓存SESSION）；当前会话的所有数据保存在会话缓存中；
     * 								STATEMENT：可以禁用一级缓存；
     *
     *第三方缓存整合：
     *		1）、导入第三方缓存包即可；
     *		2）、导入与第三方缓存整合的适配包；官方有；
     *		3）、mapper.xml中使用自定义缓存
     *		<cache type="org.mybatis.caches.ehcache.EhcacheCache"></cache>
     *
     * @throws IOException
     *
     */
    @Test
    public void testSecondLevelCache02() throws IOException{
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession openSession = sqlSessionFactory.openSession();
        SqlSession openSession2 = sqlSessionFactory.openSession();
        try{
            //1、
            DepartmentMapper mapper = openSession.getMapper(DepartmentMapper.class);
            DepartmentMapper mapper2 = openSession2.getMapper(DepartmentMapper.class);

            Department deptById = mapper.getDeptById(1);
            System.out.println(deptById);
            openSession.close();

            Department deptById2 = mapper2.getDeptById(1);
            System.out.println(deptById2);
            openSession2.close();
            //第二次查询是从二级缓存中拿到的数据，并没有发送新的sql

        }finally{

        }
    }

    /**
     * 二级缓存开启步骤
     * 1、<setting name="cacheEnabled" value="true"/>
     * 2、对应mapper.xml中写
     *  <cache/>
     * 二级缓存失效的几种情况
     * 1、前一个sqlSession事务没有提交
     */
    @Test
    public void testSecondLevelCache() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession1 = sqlSessionFactory.openSession();
        SqlSession sqlSession2 = sqlSessionFactory.openSession();
        try{
            /**
             * 1、sqlSession相同,并且查询条件相同,不会发起第二次请求
             */
            EmployeeMapper mapper1 = sqlSession1.getMapper(EmployeeMapper.class);
            EmployeeMapper mapper2 = sqlSession2.getMapper(EmployeeMapper.class);

            Employee emp01 = mapper1.getEmplById(1);
            System.out.println(emp01);
            // 第一个sqlSession不提交的话,二级缓存会失效
            sqlSession1.commit();

            // 导致二级缓存失效,命中率为0.0
//            mapper2.addEmp(new Employee(null,"test","test@foxmail.com","1",new Department(1)));
//            sqlSession2.commit();
            // 第二次查询是直接从二级缓存中拿到的数据,并没有发送新的sql

            try {
                // 睡眠一下,超过缓存有效期,会再次发起请求
//                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Employee emp02 = mapper2.getEmplById(1);
            System.out.println(emp02);
            // 结果是false,因为mybatis第二次返回的时候重新序列化了
            System.out.println(emp01 == emp02);
        }finally{
            sqlSession1.close();
            sqlSession2.close();
        }
    }

    /**
     * 测试一级缓存
     */
    @Test
    public void testFirstLevelCache() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession openSession = sqlSessionFactory.openSession();
        try{
            /**
             * 1、sqlSession相同,并且查询条件相同,不会发起第二次请求
             */
            EmployeeMapper mapper = openSession.getMapper(EmployeeMapper.class);
            Employee emp01 = mapper.getEmplById(1);
            System.out.println(emp01);

            /**
             * 2、sqlSession相同,两次查询之间有增删改查操作(可能对当前数据有影响)
             */
//            mapper.addEmp(new Employee(null,"testCache","cache","1",new Department(2)));
//            openSession.commit();

            // 3、sqlSession相同,手动清除了一级缓存
            openSession.clearCache();
            Employee emp02 = mapper.getEmplById(1);
            System.out.println(emp02);
            System.out.println(emp01==emp02);
            System.out.println("emp01==emp02================");

            /**
             * 4、sqlSession相同,查询条件不同
             */
            Employee emp03 = mapper.getEmplById(9);
            System.out.println(emp03);
            System.out.println(emp01 == emp03);
            System.out.println("emp03 ================");
            /**
             * 不同sqlSession,查询条件相同,会发起第二次请求
             */
            SqlSession openSession2 = sqlSessionFactory.openSession();
            EmployeeMapper mapper1 = openSession2.getMapper(EmployeeMapper.class);
            Employee empl = mapper1.getEmplById(1);
            System.out.println(empl);
            System.out.println("empl =================");
            openSession2.close();


            //xxxxx
            //1、sqlSession不同。
            //SqlSession openSession2 = sqlSessionFactory.openSession();
            //EmployeeMapper mapper2 = openSession2.getMapper(EmployeeMapper.class);

            //2、sqlSession相同，查询条件不同

            //3、sqlSession相同，两次查询之间执行了增删改操作(这次增删改可能对当前数据有影响)
            //mapper.addEmp(new Employee(null, "testCache", "cache", "1"));
            //System.out.println("数据添加成功");

            //4、sqlSession相同，手动清除了一级缓存（缓存清空）
            //openSession.clearCache();


            //openSession2.close();
        }finally{
            openSession.close();
        }
    }



}

