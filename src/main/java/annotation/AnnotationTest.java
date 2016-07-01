package annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import org.junit.Test;

public class AnnotationTest {
	
	@Test
	public void testAnnotation() {
		// 从使用注解的Apple类中获取注解信息  
        FruitInfoUtil.getFruitInfo(Apple.class);  
	}
}

/** 
 * *********注解处理器************** 
 */  
class FruitInfoUtil {  
  
    public static void getFruitInfo(Class<?> clazz) {  
        String strFruitName = " 水果名称：";  
        String strFruitColor = " 水果颜色：";  
        String strFruitProvicer = "供应商信息：";  
        Field[] fields = clazz.getDeclaredFields();  // 得到类的所有成员变量  
  
        for (Field field : fields) {              
            if (field.isAnnotationPresent(FruitName.class)) {  // 如果成员变量上有FruitName注解  
                FruitName fruitName = (FruitName) field.getAnnotation(FruitName.class);  // 得到注解  
                strFruitName = strFruitName + fruitName.value();  // 得到注解的参数值  
                System.out.println(strFruitName);  
            } else if (field.isAnnotationPresent(FruitColor.class)) {  // 获取FruitColor注解  
                FruitColor fruitColor = (FruitColor) field.getAnnotation(FruitColor.class);  
                strFruitColor = strFruitColor + fruitColor.fruitColor().toString();  
                System.out.println(strFruitColor);  
            } else if (field.isAnnotationPresent(FruitProvider.class)) {  // 获取FruitProvider注解  
                FruitProvider fruitProvider = (FruitProvider) field.getAnnotation(FruitProvider.class);  
                strFruitProvicer = " 供应商编号：" + fruitProvider.id() + " 供应商名称：" + fruitProvider.name() +   
                        " 供应商地址：" + fruitProvider.address();  
                System.out.println(strFruitProvicer);  
            }  
        }  
    }  
}  
  

/**
 * *********注解使用**************
 */
class Apple {

	@FruitName("Apple")
	private String appleName;

	@FruitColor(fruitColor = FruitColor.Color.RED)
	private String appleColor;

	@FruitProvider(id = 1, name = "陕西红富士集团", address = "陕西省西安市延安路89号红富士大厦")
	private String appleProvider;

	public void setAppleColor(String appleColor) {
		this.appleColor = appleColor;
	}

	public String getAppleColor() {
		return appleColor;
	}

	public void setAppleName(String appleName) {
		this.appleName = appleName;
	}

	public String getAppleName() {
		return appleName;
	}

	public void setAppleProvider(String appleProvider) {
		this.appleProvider = appleProvider;
	}

	public String getAppleProvider() {
		return appleProvider;
	}

	public void displayName() {
		System.out.println("水果的名字是：苹果");
	}
}

@Target(ElementType.FIELD)  
@Retention(RetentionPolicy.RUNTIME)  
@Documented  
@interface FruitName {  
    String value() default "";  
}  

@Target(ElementType.FIELD)  
@Retention(RetentionPolicy.RUNTIME)  
@Documented  
@interface FruitColor {  
    /** 
     * 颜色枚举 
     * @author peida 
     * 
     */  
    public enum Color{ BULE,RED,GREEN};  
      
    /** 
     * 颜色属性 
     * @return 
     */  
    Color fruitColor() default Color.GREEN;  
}  

/** 
 * 水果供应者注解 
 * @author peida 
 * 
 */  
@Target(ElementType.FIELD)  
@Retention(RetentionPolicy.RUNTIME)  
@Documented  
@interface FruitProvider {  
    /** 
     * 供应商编号 
     * @return 
     */  
    public int id() default -1;  
      
    /** 
     * 供应商名称 
     * @return 
     */  
    public String name() default "";  
      
    /** 
     * 供应商地址 
     * @return 
     */  
    public String address() default "";  
}  