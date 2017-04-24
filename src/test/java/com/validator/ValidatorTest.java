package com.validator;


import com.alibaba.fastjson.JSON;
import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Test;

import javax.validation.*;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.util.Set;

/**
 * @Author yangmeng44
 * @Date 2017/4/12
 */
public class ValidatorTest {

    public static void main(String[] args) {
        System.out.println(Integer.MAX_VALUE);
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        Validator validator = factory.getValidator();
//        UserModel userModel = new UserModel();
//        Set<ConstraintViolation<UserModel>> set = validator.validate(userModel, Default.class);
//        System.out.println(JSON.toJSONString(set));
//        set = validator.validate(userModel, First.class);
//        System.out.println(set.size());
    }

    @Test
    public void test() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        User user = new User();
        Set<ConstraintViolation<User>> set = validator.validate(user);
        set = validator.validate(user, Group.class);
        System.out.println(set.size());
    }
}
class UserModel {

    @NotNull(message = "id2", groups = { First.class })
    private int id2;

    @NotNull(message = "id", groups = { First.class })
    private int id;

    @NotNull(message = "username", groups = { First.class, Second.class })
    private String username;

    @NotNull(message = "content", groups = { First.class, Second.class })
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
interface First {
}

interface Second {
}

interface GroupA {
}

interface GroupB {
}

@GroupSequence( { Default.class, GroupA.class, GroupB.class })
interface Group {
}

class User {
    @NotEmpty(message = "firstname may be empty")
    private String firstname;

    @NotEmpty(message = "middlename may be empty", groups = Default.class)
    private String middlename;

    @NotEmpty(message = "lastname may be empty", groups = GroupA.class)
    private String lastname;

    @NotEmpty(message = "country may be empty", groups = GroupB.class)
    private String country;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}