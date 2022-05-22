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
 * 1、接口式编程
 * 	原生：		Dao		====>  DaoImpl
 * 	mybatis：	Mapper	====>  xxMapper.xml
 *
 * 2、SqlSession代表和数据库的一次会话；用完必须关闭；
 * 3、SqlSession和connection一样她都是非线程安全。每次使用都应该去获取新的对象。
 * 4、mapper接口没有实现类，但是mybatis会为这个接口生成一个代理对象。
 * 		（将接口和xml进行绑定）
 * 		EmployeeMapper empMapper =	sqlSession.getMapper(EmployeeMapper.class);
 * 5、两个重要的配置文件：
 * 		mybatis的全局配置文件：包含数据库连接池信息，事务管理器信息等...系统运行环境信息
 * 		sql映射文件：保存了每一个sql语句的映射信息：
 * 					将sql抽取出来。
 */
public class MybatisTest {

    private final static Logger logger = LoggerFactory.getLogger(MybatisTest.class);

    public SqlSessionFactory getSqlSessionFactory() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        return sqlSessionFactory;
    }

    /**
     * 1、根据xml配置文件（全局配置文件）创建一个SqlSessionFactory对象 有数据源一些运行环境信息
     * 2、sql映射文件；配置了每一个sql，以及sql的封装规则等。
     * 3、将sql映射文件注册在全局配置文件中
     * 4、写代码：
     * 		1）、根据全局配置文件得到SqlSessionFactory；
     * 		2）、使用sqlSession工厂，获取到sqlSession对象使用他来执行增删改查
     * 			一个sqlSession就是代表和数据库的一次会话，用完关闭
     * 		3）、使用sql的唯一标志来告诉MyBatis执行哪个sql。sql都是保存在sql映射文件中的。
     *
     * @throws IOException
     */
    @Test
    public void test1 () throws IOException {
        // 2、获取sqlSession实例，能直接执行已经映射的sql语句
        // sql的唯一标识：statement Unique identifier matching the statement to use.
        // 执行sql要用的参数：parameter A parameter object to pass to the statement.
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            Employee employee = sqlSession.selectOne("com.smile.skype.learnmybatis.dao.EmployeeMapper.selectEmp", 1);
            System.out.println(employee);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void test2() throws IOException {
        logger.info("test2 start ...");
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
        logger.info("test2 end ...");
    }

    /**
     * 测试查询Oracle
     * @throws IOException
     */
    @Test
    public void test3() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee empl = mapper.getEmplById2(100);
            System.out.println(empl);
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 测试注解形式
     * 这种形式只需要写一个接口类，在具体方法上面直接写sql语句，但是需要注册到mybatis中
     * 例如：<mapper class="com.smile.skype.learnmybatis.dao.EmployeeMapperAnnotation" />
     */
    @Test
    public void test4() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            EmployeeMapperAnnotation mapper = sqlSession.getMapper(EmployeeMapperAnnotation.class);
            Employee empl = mapper.getEmpById(1);
            System.out.println(empl);
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 测试增删改
     * 1、mybatis允许增删改直接定义以下类型返回值
     *  Integer Long Boolean void
     * 2、我们需要手动提交数据
     *  sqlSessionFactory.openSession(); ==》手动提交
     *  sqlSessionFactory.openSession(true); ==》自动提交
     */
    @Test
    public void test5() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
            // 测试添加
            Employee employee = new Employee(null,"smileskye1","smileskype1@foxmail.com","1");
            boolean total = mapper.addEmp(employee);
            sqlSession.commit();
            System.out.println("执行结果： " + total + "  employee id is " + employee.getId());
            // 测试修改
//            Employee employee = new Employee(3,"smileskye",null,"1");
//            Integer i = mapper.updateEmp(employee);
//            System.out.println(i);
            // 测试删除
//            mapper.deleteEmpById(3);
            // 手动提交数据
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 测试各种条件的查询
     *     就是mybatis的参数传递
     * @throws IOException
     */
    @Test
    public void test6() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee ep = mapper.getEmpByIdAndLastName(1,"tom");
            System.out.println(ep);
            System.out.println("ep -----------------");
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("id",1);
            map.put("lastName","tom");
            map.put("tableName","tbl_employee");
            Employee employee = mapper.getEmpByMap(map);
            System.out.println(employee);
            System.out.println("employee -----------------");
            List<Employee> list = mapper.getEmpsByLastNameLike("%smile%");
            list.stream().forEach(emp -> System.out.println(emp));
            System.out.println("list -----------------");

            Map<String,Object> empMap = mapper.getEmpByIdReturnMap(1);
            System.out.println(empMap);
            System.out.println("empMap -----------------");
            Map<String,Employee> employeeMap = mapper.getEmpByLastNameLikeReturnMap("%smile%");
            System.out.println(employeeMap);
            System.out.println("employeeMap -----------------");

            List<Integer> idList = new ArrayList<>();
            idList.add(1);
            idList.add(4);
            List<Employee> idResultList = mapper.getEmpsByIdList(idList);
            System.out.println(idResultList);
            System.out.println("idResultList -----------------");
        } finally {
            sqlSession.close();
        }
    }


    /**
     * 测试员工和部门相关内容
     * 1、在没有配置lazyLoadTriggerMethods的情况下
     * 1.1 打印pojo类,调用toString()方法会触发懒加载
     * 1.2 debug模式悬浮在上面的时候也会触发拦截在
     * 2、在配置<setting name="lazyLoadTriggerMethods" value=""/>时
     * 2.1 无法时打印pojo类,还是debug悬浮,都不会触发懒加载
     */
    @Test
    public void test7() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            EmployeeMapperPlus mapper = sqlSession.getMapper(EmployeeMapperPlus.class);
//            Employee ep = mapper.getEmpById(1);
//            System.out.println(ep);
//
//            // 查询员工的同时,也将部门查询出来
//            Employee empAndDept = mapper.getEmpAndDeptById(1);
//            System.out.println(empAndDept);
//            System.out.println(empAndDept.getDept());
            // 分步查询
            Employee empAndDeptStep = mapper.getEmpAndDeptStepById(1);
            System.out.println(empAndDeptStep.getLastName());
            System.out.println(empAndDeptStep);
//            System.out.println(empAndDeptStep.getDept());
            System.out.println("---------------");
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 测试部门相关内容
     */
    @Test
    public void test8() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            DepartmentMapper mapper = sqlSession.getMapper(DepartmentMapper.class);
            Department department = mapper.getDeptByIdPlus(1);
            System.out.println(department);
            System.out.println("---------------");
            // 分步
            Department dept = mapper.getDeptByIdStep(1);
            System.out.println(dept);
            System.out.println(dept.getEmps().stream().map(Employee::getId).collect(Collectors.toList()));
        } finally {
            sqlSession.close();
        }
    }


}

