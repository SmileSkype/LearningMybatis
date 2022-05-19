package com.smile.skype.learnmybatis.dao;

import com.smile.skype.learnmybatis.bean.Employee;

public interface EmployeeMapper {
    Employee getEmplById(Integer id);

    Employee getEmplById2(Integer id);

    boolean addEmp(Employee employee);

    Integer updateEmp(Employee employee);

    void deleteEmpById(int id);
}
