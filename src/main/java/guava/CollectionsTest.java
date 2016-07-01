package guava;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class CollectionsTest {

	private Date date = new Date();
	private List<Dog> dogs = ImmutableList.of(new Dog("Jeff", 0.6f, 1, date),
			new Dog("Vivian", 1f, 0, date),
			new Dog("aa", 1f, 0, date),
			new Dog("bb", 1f, 0, date));// 初始化数据

	@Test
	public void transformTest() {
		Collection<String> collection = Collections2.transform(dogs, new Function<Dog, String>() {
			public String apply(Dog dog) {
				return dog.getName();
			}
		});
		System.out.println(dogs);
		System.out.println(JSON.toJSON(collection));
		Collection<List<Dog>> lists = Collections2.permutations(dogs);
		System.out.println(JSON.toJSON(lists.toString()));
	}

	@Test
	public void fileterTest() {
		Collection<Dog> collection = Collections2.filter(dogs, new Predicate<Dog>() {
			public boolean apply(Dog dog) {
				return dog.getAge() > 0.7f;
			}
		});
		System.out.println(JSON.toJSON(collection));
	}

	@Test
	public void tryFindTest() {
		Optional<Dog> optional = Iterables.tryFind(dogs, new Predicate<Dog>() {
			public boolean apply(Dog dog) {
				return dog.getBirthday() == date;
			}
		});
		System.out.println(JSON.toJSON(optional));
		if (optional.isPresent()) {
			System.out.println(JSON.toJSON(optional.get()));
		}
	}

}

class Dog {
	private String name;
	private float age;
	private Integer sex;
	private Date birthday;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public float getAge() {
		return age;
	}

	public void setAge(float age) {
		this.age = age;
	}

	public Dog(String name, float age, Integer sex, Date birthday) {
		super();
		this.name = name;
		this.age = age;
		this.sex = sex;
		this.birthday = birthday;
	}
}
