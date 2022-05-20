package com.smile.skype.learnmybatis.dao;

import com.smile.skype.learnmybatis.bean.Employee;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface EmployeeMapper {
    Employee getEmplById(Integer id);

    Employee getEmplById2(Integer id);

    boolean addEmp(Employee employee);

    Integer updateEmp(Employee employee);

    void deleteEmpById(Integer id);

    Employee getEmpByIdAndLastName(@Param("id") Integer id, @Param("lastName") String lastName);

    Employee getEmpByMap(Map<String, Object> map);

    List<Employee> getEmpsByLastNameLike(String lastName);

    // 返回一条记录的map,key就是列名,值就是对应的值
    public Map<String, Object> getEmpByIdReturnMap(Integer id);

    /**
     * 多条记录封装一个map,Map<String,Employee> 键是这条记录的主键
     * MapKey:告诉mabatis封装这个map使用那个属性作为map的key
     */
    @MapKey("id")
    Map<String, Employee> getEmpByLastNameLikeReturnMap(String lastName);

    List<Employee> getEmpsByIdList(List<Integer> idList);
}
