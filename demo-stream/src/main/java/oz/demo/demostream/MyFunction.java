package oz.demo.demostream;

import java.util.function.Function;

public class MyFunction implements Function<String, String>{

	@Override
	public String apply(String t) {
		return t.toUpperCase();
	}

}
