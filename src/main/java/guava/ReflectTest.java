package guava;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

public class ReflectTest {

	@SuppressWarnings({ "unused", "serial" })
	@Test
	public void typeTokenTest() {
		TypeToken<String> stringTok = TypeToken.of(String.class);
		System.out.println(stringTok);
		System.out.println(stringTok.getRawType());
		TypeToken<Integer> intTok = TypeToken.of(Integer.class);
		System.out.println(intTok);
		TypeToken<List<String>> stringListTok = new TypeToken<List<String>>() {
		};
		System.out.println(stringListTok);
		TypeToken<Map<?, ?>> wildMapTok = new TypeToken<Map<?, ?>>() {
		};
		System.out.println(wildMapTok);

		TypeToken<Map<String, BigInteger>> mapToken = mapToken(TypeToken.of(String.class),
				TypeToken.of(BigInteger.class));
		System.out.println(mapToken);
		TypeToken<Map<Integer, Queue<String>>> complexToken = mapToken(TypeToken.of(Integer.class),
				new TypeToken<Queue<String>>() {
				});
		System.out.println(complexToken);
		System.out.println(Util.<String, BigInteger>incorrectMapToken());
		
		IKnowMyType type1 = new IKnowMyType<String>() {
		};
		System.out.println(type1.type.getType());
	}
	
	@Test
	public void resolveTypeTest() throws NoSuchMethodException, SecurityException {
		TypeToken<Function<Integer, String>> funToken = new TypeToken<Function<Integer, String>>() {};
		System.out.println(funToken);
		TypeToken<?> funResultToken = funToken.resolveType(Function.class.getTypeParameters()[1]);
		System.out.println(funResultToken);
		
		TypeToken<Map<String, Integer>> mapToken = new TypeToken<Map<String, Integer>>() {};
		System.out.println(mapToken);
		TypeToken<?> entrySetToken = mapToken.resolveType(Map.class.getMethod("entrySet").getGenericReturnType());
		System.out.println(entrySetToken);
	}

	@Test
	public void invokeTest() {
	}
	
	static <K, V> TypeToken<Map<K, V>> mapToken(TypeToken<K> keyToken, TypeToken<V> valueToken) {
		return new TypeToken<Map<K, V>>() {
		}.where(new TypeParameter<K>() {
		}, keyToken).where(new TypeParameter<V>() {
		}, valueToken);
	}
	
	abstract class IKnowMyType<T> {
		TypeToken<T> type = new TypeToken<T>(getClass()) {};
	}
}

class Util {
    static <K, V> TypeToken<Map<K, V>> incorrectMapToken() {
        return new TypeToken<Map<K, V>>() {};
    }
}

