package practise;

import com.google.common.reflect.TypeToken;

public class Test {

	public static void main(String[] args) {
		TypeToken<String> stringTok = TypeToken.of(String.class);
		TypeToken<Integer> intTok = TypeToken.of(Integer.class);
	}
}
