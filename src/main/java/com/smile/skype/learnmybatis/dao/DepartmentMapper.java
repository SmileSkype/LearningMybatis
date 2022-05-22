package com.smile.skype.learnmybatis.dao;

import com.smile.skype.learnmybatis.bean.Department;

public interface DepartmentMapper {
    public Department getDeptById(Integer id);

    Department getDeptByIdPlus(Integer id);

    Department getDeptByIdStep(Integer id);
}
