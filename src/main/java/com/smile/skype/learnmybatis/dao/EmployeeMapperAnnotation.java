package com.smile.skype.learnmybatis.dao;

import com.smile.skype.learnmybatis.bean.Employee;
import org.apache.ibatis.annotations.Select;

/**
 * 通过接口的形式
 */
public interface EmployeeMapperAnnotation {

    @Select("select * from tbl_employee where id=#{id}")
    public Employee getEmpById(Integer id);
}
