package guava;

import org.junit.Test;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

public class ClassToInstanceMapTest {
	@Test
	public void ClassToInstanceMapTest0() {
		ClassToInstanceMap<String> classToInstanceMapString = MutableClassToInstanceMap.create();
		ClassToInstanceMap<Person> classToInstanceMap = MutableClassToInstanceMap.create();
		Person person = new Person("peida", 20);
		System.out.println("person name :" + person.name + " age:" + person.age);
		classToInstanceMapString.put(String.class, "peida");
		System.out.println("string:" + classToInstanceMapString.getInstance(String.class));

		classToInstanceMap.putInstance(Person.class, person);
		Person person1 = classToInstanceMap.getInstance(Person.class);
		System.out.println("person1 name :" + person1.name + " age:" + person1.age);
	}
}

class Person {
	public String name;
	public int age;

	Person(String name, int age) {
		this.name = name;
		this.age = age;
	}
}