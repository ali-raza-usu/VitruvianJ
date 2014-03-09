package vitruvianJ.serialization;
import java.lang.reflect.*;

public class RunTests {
	public static void main(String[] args) throws Exception {
		int passed = 0, failed = 0;
		String arg = "serialization.Foo";
		for (Method m : Class.forName(arg).getMethods()) {
			if (m.isAnnotationPresent(Test.class)) {
				try {
					m.invoke(null);
					passed++;
				} catch (Exception ex) {
					System.out
							.printf("Test %s failed: %s %n", m, ex.getCause());
					failed++;
				}
			}
		}
		System.out.printf("Passed: %d, Failed %d%n", passed, failed);
	}
}