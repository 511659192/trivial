package com.ym.orika;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.impl.generator.JavassistCompilerStrategy;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class OrikaTest {

    public static void main(String[] args) {
        DefaultMapperFactory.Builder builder = new DefaultMapperFactory.Builder();
        MapperFactory mapperFactory = builder.build();
        ClassMapBuilder classMapBuilder = mapperFactory.classMap(User.class, UserA.class);
        classMapBuilder = classMapBuilder
//                .field("id", "id")
//                .field("name", "name")
                .field("list", "list");
        classMapBuilder.byDefault();
        classMapBuilder.register();
        User<String, Runnable> user = new User<>();
        user.setId(123L);
        user.setName("小明");
        List<Integer> listInt = new ArrayList<>();
        listInt.add(1);
        user.setList2(listInt);
        List<String> listStr = new ArrayList<>();
        listStr.add("listStr");
        user.setList(listStr);
        user.setE("e");
        MapperFacade mapper = mapperFactory.getMapperFacade();
        UserA userA = mapper.map(user, UserA.class);
        System.out.println(JSON.toJSONString(userA));

    }

    @Test
    public void testFieldAndDefault() {
        DefaultMapperFactory.Builder builder = new DefaultMapperFactory.Builder();
        MapperFactory mapperFactory = builder.build();
        ClassMapBuilder classMapBuilder = mapperFactory.classMap(BasicPerson.class, BasicPersonDto.class);
        classMapBuilder
//                .field("name", "fullName")
                .fieldAToB("name", "fullName")
                .field("age", "currentAge")
                .field("nameList[0]", "firstNameFromList")
                .field("nameList[1]", "lastNameFromList")
                .field("nameMap['first']", "firstNameFromMap")
                .field("nameMap['second']", "lastNameFromMap")
                .field("nameOfName.first", "firstName")
                .byDefault()
                .register();

        BasicPerson person = new BasicPerson();
        person.setAge(1);
        person.setBirthDate(new Date());
        person.setName("person");
        person.setNameList(Lists.newArrayList("first", "second"));
        Map<String, String> nameMap = Maps.newHashMap();
        nameMap.put("first", "name1");
        nameMap.put("second", "name2");
        person.setNameMap(nameMap);

        Name name = new Name();
        name.setFirst("firstOfName");
        name.setLast("lastOfName");
        name.setFullName("first last");
        person.setNameOfName(name);

        BasicPersonDto dto = mapperFactory.getMapperFacade().map(person, BasicPersonDto.class);
        System.out.println(JSON.toJSONString(dto, SerializerFeature.PrettyFormat));

        BasicPerson person2 = mapperFactory.getMapperFacade().map(dto, BasicPerson.class);
        System.out.println(JSON.toJSONString(person2, SerializerFeature.PrettyFormat));
    }

    @Test
    public void test() {
        DefaultMapperFactory.Builder builder = new DefaultMapperFactory.Builder();
        MapperFactory mapperFactory = builder.build();
        mapperFactory.classMap(Person.class, PersonDto.class)
                .field("names{fullName}", "personalNames{key}")
                .field("names{first}", "personalNames{value}")
                .register();

        Name name = new Name();
        name.setFirst("firstOfName");
        name.setLast("lastOfName");
        name.setFullName("first last");

        Person person = new Person();
        person.setNames(new ArrayList<Name>());
        person.getNames().add(name);

        PersonDto dto = mapperFactory.getMapperFacade().map(person, PersonDto.class);
        System.out.println(JSON.toJSONString(dto, SerializerFeature.PrettyFormat));

//        Person person2 = mapperFactory.getMapperFacade().map(dto, Person.class);
//        System.out.println(JSON.toJSONString(person2, SerializerFeature.PrettyFormat));
    }

    @Test
    public void test2() {
        DefaultMapperFactory.Builder builder = new DefaultMapperFactory.Builder();
        MapperFactory mapperFactory = builder.build();
        String employmentDef =
                "employment:{getAttribute('employment')|setAttribute('employment', %s)|type=ma.glasnost.orika.test.property.PropertyResolverTestCase.Element}";
        String jobTitleDef =
                "jobTitle:{getAttribute(\"job's Title\")|setAttribute(\"job's Title\", %s)|type=List<String>}";
        String salaryDef =
                "salary:{getAttribute(\"'salary'\")|setAttribute(\"'salary'\", %s)|type=java.lang.Long}";

        String nameDef =
                "name:{getAttribute('name')|setAttribute('name',%s)|type=ma.glasnost.orika.test.property.PropertyResolverTestCase.Element}";
        String firstNameDef = "first:{getAttribute('first')|setAttribute('first', %s)|type=java.lang.String}";
        String lastNameDef = "last:{getAttribute('last')|setAttribute('last', %s)|type=java.lang.String}";

        mapperFactory.classMap(Element.class, Person.class)
                .field(employmentDef + "." + jobTitleDef, "jobTitles")
                .field("employment." + salaryDef, "salary") // reuse the in-line declaration of 'employment' property
                .field(nameDef + "." + firstNameDef, "firstName")
                .field("name." + lastNameDef, "lastName") // reuses the in-line declaration of 'name' property
                .register();

        Element element = new Element();
        element.setAttribute("name", "value");

        Person person = mapperFactory.getMapperFacade().map(element, Person.class);
        System.out.println(JSON.toJSONString(person, SerializerFeature.PrettyFormat));
    }
}

