package com.smile.skype.learnmybatis.test;

import com.smile.skype.learnmybatis.bean.Employee;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class ClassTest {
    public static void main(String[] args) {
        Class clazz = Employee.class;
        Constructor[] constructors = clazz.getConstructors();
        for (Constructor constructor : constructors) {
            // 实际上打印的是构造函数的参数个数
            System.out.println(constructor.getParameterTypes().toString() + "   " + constructor.getParameterTypes().length);
        }

        Field[] fields = PropertyTest.class.getDeclaredFields();
        for (Field field:fields) {
            System.out.println("name:" + field.getName() + " AnnotatedType:" + field.getAnnotatedType() + " GenericType:" + field.getGenericType() + " Modifiers"+field.getModifiers());
        }

        System.out.println("================");
        String str = "12345";
        int i = str.indexOf("2");
        System.out.println(i);
        System.out.println(str.substring(0,i));
        System.out.println(str.substring(i,str.length()));
    }
}

class PropertyTest{
    public PropertyTest() {

    }

    private PropertyTest(String param1) {

    }

    private String age;
    public String name;

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
