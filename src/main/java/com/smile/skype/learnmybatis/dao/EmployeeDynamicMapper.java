package com.smile.skype.learnmybatis.dao;

import com.smile.skype.learnmybatis.bean.Employee;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface EmployeeDynamicMapper {

    List<Employee> getEmpsTestInnerParameter(Employee searchEmpParam);

    void addEmpsMysql(@Param("emps")List<Employee> emps);

    List<Employee> getEmpsByConditionIf(Employee employee);

    List<Employee> getEmpsByConditionTrim(Employee employee);

    List<Employee> getEmpsByConditionChoose(Employee employee);

    void updateEmp(Employee employee);
    //查询员工id'在给定集合中的
    List<Employee> getEmpsByConditionForeach(@Param("ids")List<Integer> ids);
}
