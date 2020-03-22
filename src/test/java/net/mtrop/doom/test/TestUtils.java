/*******************************************************************************
 * Copyright (c) 2015-2020 Matt Tropiano
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.test;

import java.io.PrintStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Small testing suite. 
 * Modeled after JUnit, kinda.
 * @author Matthew Tropiano
 */
public final class TestUtils
{
	/**
	 * Classes annotated with this execute these test classes before this class's test.
	 */
	@Target({ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface TestDependsOn
	{
		Class<?>[] value() default {};
	}

	/**
	 * Static methods annotated with this are executed before the class's tests are executed.
	 */
	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface BeforeAllTests
	{
		// Empty
	}

	/**
	 * Static methods annotated with this are executed after the class's tests are executed.
	 */
	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface AfterAllTests
	{
		// Empty
	}

	/**
	 * Methods annotated with this are executed before every test.
	 */
	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface BeforeEachTest
	{
		// Empty
	}

	/**
	 * Methods annotated with this are executed after every test.
	 */
	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface AfterEachTest
	{
		// Empty
	}

	/**
	 * Methods annotated with this are executed as a test.
	 * Can have an optional different name.
	 */
	@Target({ElementType.METHOD, ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Test
	{
		/** The name of this test. */
		String value() default "";
	}

	/**
	 * Test methods annotated with this are ignored.
	 */
	@Target({ElementType.METHOD, ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Ignore
	{
		/** Reason. */
		String value() default "";
	}

	/**
	 * Full testing result.
	 */
	public static class TestResults
	{
		private int total;
		private int passed;
		private int failed;
		private int ignored;
		
		private TestResults()
		{
			this.total = 0;
			this.passed = 0;
			this.failed = 0;
			this.ignored = 0;
		}
		
		private void add(TestResults results)
		{
			this.total += results.total;
			this.passed += results.passed;
			this.failed += results.failed;
			this.ignored += results.ignored;
		}
		
		public int getTotal()
		{
			return total;
		}
		
		public int getFailed()
		{
			return failed;
		}
		
		public int getIgnored()
		{
			return ignored;
		}
		
		public int getPassed()
		{
			return passed;
		}
		
	}
	
	/**
	 * Generic assertion failure exception.
	 */
	public static class AssertionFailureException extends AssertionError
	{
		private static final long serialVersionUID = -1613162997554486037L;
		
		public AssertionFailureException(String message)
		{
			super(message);
		}
	
		public AssertionFailureException(String message, Throwable cause)
		{
			super(message, cause);
		}
	}

	/**
	 * Equality assertion failure exception.
	 */
	public static class AssertionEqualityFailureException extends AssertionFailureException
	{
		private static final long serialVersionUID = 9007166559374438508L;
	
		public AssertionEqualityFailureException(Object expected, Object got)
		{
			super("Assertion failed. Expected " + String.valueOf(expected) + ", got " + String.valueOf(got));
		}
	}

	/**
	 * Throwable assertion failure exception.
	 */
	public static class AssertionCauseException extends AssertionFailureException
	{
		private static final long serialVersionUID = -951937796798865772L;
	
		public AssertionCauseException(Throwable cause)
		{
			super("Assertion failed.", cause);
		}
	}

	/**
	 * Performs tests on a test class.
	 * @param <C> the class type.
	 * @param cls the class to test.
	 * @param out the output stream.
	 * @param err the error stream.
	 * @param verbose if true, verbose output.
	 * @return true on failure, false on pass.
	 */
	public static <C> TestResults performTestOn(Class<C> cls, PrintStream out, PrintStream err, boolean verbose)
	{
		TestDependsOn anno;
		TestResults result = new TestResults();
		if ((anno = cls.getAnnotation(TestDependsOn.class)) != null)
		{
			for (Class<?> c : anno.value())
				result.add(performTestOn(c, out, err, verbose));
		}
		
		(new TestClass<>(cls)).call(result, out, err, verbose);
		return result;
	}
	
	/**
	 * Asserts that the result of a callable is true.
	 * @param boolFunction the boolean callable to call.
	 * @throws AssertionEqualityFailureException if assertion fails or the callable throws an exception.
	 */
	public static void assertTest(Callable<Boolean> boolFunction)
	{
		assertEqual(true, boolFunction);
	}

	/**
	 * Asserts that the result of a callable is equal to a value.
	 * @param expected the expected value.
	 * @param callable the callable to call.
	 * @param <C> the common type.
	 * @throws AssertionEqualityFailureException if assertion fails or the callable throws an exception.
	 */
	public static <C> void assertEqual(C expected, Callable<C> callable)
	{
		try {assertEqual(callable.call(), expected);} catch (Exception e)
		{
			throw new AssertionCauseException(e);
		}
	}

	/**
	 * Asserts that two values are equal.
	 * @param result the result to test.
	 * @param expected the expected value.
	 * @param <C> the common type.
	 * @throws AssertionEqualityFailureException if assertion fails.
	 */
	public static <C> void assertEqual(C result, C expected)
	{
		if (!areEqual(result, expected))
			throw new AssertionEqualityFailureException(expected, result);
	}

	/**
	 * Asserts that the result of a runnable causes an exception.
	 * @param runnable the runnable to call.
	 * @param <E> the Throwable type.
	 * @throws AssertionFailureException if assertion fails.
	 */
	public static <E extends Throwable> void assertException(Runnable runnable)
	{
		assertExceptionType(Throwable.class, runnable);
	}

	/**
	 * Asserts that the result of a runnable causes an exception.
	 * @param exceptionClass the expected exception class.
	 * @param runnable the runnable to call.
	 * @param <E> the Throwable type.
	 * @throws AssertionFailureException if assertion fails.
	 */
	public static <E extends Throwable> void assertExceptionType(Class<E> exceptionClass, Runnable runnable)
	{
		try {
			runnable.run();
			throw new AssertionFailureException("Expected exception type: " + exceptionClass.getCanonicalName());
		} catch (Throwable t) {
			if (!exceptionClass.isAssignableFrom(t.getClass()))
				throw new AssertionFailureException("Expected exception type: " + exceptionClass.getCanonicalName() + ", got " + t.getClass().getCanonicalName());
		}
	}

	/**
	 * A test class created by scanning a class for tests.
	 */
	private static class TestClass<C>
	{
		private Class<C> type;
		private String name;
		private List<Method> beforeAll;
		private List<Method> afterAll;
		private List<Method> beforeEach;
		private List<Method> afterEach;
		private List<TestMethod> testMethods;
		
		private TestClass(Class<C> cls)
		{
			this.type = cls;
			this.beforeAll = new LinkedList<>();
			this.afterAll = new LinkedList<>();
			this.beforeEach = new LinkedList<>();
			this.afterEach = new LinkedList<>();
			
			Test ta;
			if ((ta = cls.getAnnotation(Test.class)) != null && !isStringEmpty(ta.value()))
				this.name = ta.value();
			else
				this.name = cls.getSimpleName();
			
			this.testMethods = new LinkedList<>();
			
			for (Method m : cls.getMethods())
			{
				// check for viable statics.
				if (isPublicStaticMemberMethod(cls, m))
				{
					if (m.isAnnotationPresent(BeforeAllTests.class))
						this.beforeAll.add(m);
					if (m.isAnnotationPresent(AfterAllTests.class))
						this.afterAll.add(m);
					continue;
				}
					
				// check for viable before/after methods.
				if (isPublicMethod(m))
				{
					if (m.isAnnotationPresent(BeforeEachTest.class))
						this.beforeEach.add(m);
					if (m.isAnnotationPresent(AfterEachTest.class))
						this.afterEach.add(m);
				}

				// check for viable test methods.
				if (isPublicMemberMethod(cls, m))
				{
					if (m.isAnnotationPresent(Test.class))
						this.testMethods.add(new TestMethod(m));
				}
			}
		}
		
		private boolean isPublicStaticMemberMethod(Class<C> cls, Method m)
		{
			return m.getDeclaringClass() == cls 
				&& (m.getModifiers() & (Modifier.PUBLIC | Modifier.STATIC)) == (Modifier.PUBLIC | Modifier.STATIC)
				;
		}

		private boolean isPublicMemberMethod(Class<C> cls, Method m)
		{
			return m.getDeclaringClass() == cls 
				&& (m.getModifiers() & (Modifier.PUBLIC)) == (Modifier.PUBLIC)
				;
		}
		
		private boolean isPublicMethod(Method m)
		{
			return (m.getModifiers() & (Modifier.PUBLIC)) == (Modifier.PUBLIC)
				;
		}
		
		private void call(TestResults results, PrintStream out, PrintStream err, boolean verbose)
		{
			long millis = System.currentTimeMillis();
			if (verbose) out.println(name+": Start test.");
			
			results.total++;
			
			if (beforeAll != null)
			{
				long beforeAllTime = System.currentTimeMillis();
				if (verbose) out.println(name+": BeforeAll Start.");
				for (Method ba : beforeAll) try {invokeBlind(ba, null);} catch (Throwable t)
				{
					err.println(name+": BeforeAll Exception.");
					t.printStackTrace(err);
					results.failed++;
					return;
				}
				if (verbose) out.println(name+": BeforeAll End.");
				if (verbose) out.println(name+": BeforeAll: " + (System.currentTimeMillis() - beforeAllTime) / 1000.0 + " secs");
			}
			
			boolean pass = true;
			if (testMethods.isEmpty())
				out.println(name+": No tests.");
			else for (TestMethod tm : testMethods)
				tm.call(create(type), results, out, err, verbose);

			if (afterAll != null)
			{
				long afterAllTime = System.currentTimeMillis();
				if (verbose) out.println(name+": AfterAll Start.");
				for (Method aa : afterAll) try {invokeBlind(aa, null);} catch (Throwable t)
				{
					err.println(name+": AfterAll Exception.");
					t.printStackTrace(err);
					results.failed++;
					return;
				}
				if (verbose) out.println(name+": AfterAll End.");
				if (verbose) out.println(name+": AfterAll: " + (System.currentTimeMillis() - afterAllTime) / 1000.0 + " secs");
			}
			
			out.println(name+": " + (pass?"PASS":"FAIL") + " " + (System.currentTimeMillis() - millis) / 1000.0 + " secs");
			
			if (pass)
				results.passed++;
			else
				results.failed++;
			return;
		}
		
		/** A single method. */
		private class TestMethod
		{
			private String testName;
			private Method method;
			private boolean ignore;
			private String ignoreMessage;
			
			private TestMethod(Method m)
			{
				this.method = m;
				
				Test ta;
				if ((ta = m.getAnnotation(Test.class)) != null && !isStringEmpty(ta.value()))
					this.testName = ta.value();
				else
					this.testName = m.getName();
				
				Ignore ia;
				if ((ia = m.getAnnotation(Ignore.class)) != null)
				{
					this.ignore = true;
					if (!isStringEmpty(ia.value()))
						this.ignoreMessage = ia.value();
				}
			}
			
			private void call(C instance, TestResults results, PrintStream out, PrintStream err, boolean verbose)
			{
				results.total++;
				
				String fullName = name + "/" + testName;
				if (ignore)
				{
					if (ignoreMessage != null)
						out.println(fullName + ": Ignored: " + ignoreMessage);
					else
						out.println(fullName + ": Ignored");
					results.ignored++;
					return;
				}
				
				long millis = System.currentTimeMillis();
				boolean pass = true;

				long beforeTime = System.currentTimeMillis();
				if (verbose) out.println(fullName+": BeforeEach Start");
				for (Method be : beforeEach) try {invokeBlind(be, instance);} catch (Throwable t)
				{
					if (t instanceof AssertionFailureException)
						err.println(fullName + ": Exception: " + t.getMessage());
					else
					{
						err.println(fullName + ": Exception");
						t.printStackTrace(err);
					}
					pass = false;
				}
				if (verbose) out.println(fullName + ": BeforeEach End" + " " + (System.currentTimeMillis() - beforeTime) / 1000.0 + " secs");
				
				if (pass)
				{
					long testTime = System.currentTimeMillis();
					if (verbose) out.println(fullName+": Start");
					try {invokeBlind(method, instance);} catch (Throwable t)
					{
						if (t instanceof AssertionFailureException)
							err.println(fullName + ": Exception: " + t.getMessage());
						else
						{
							err.println(fullName + ": Exception");
							t.printStackTrace(err);
						}
						pass = false;
					}
					if (verbose) out.println(fullName + ": End" + " " + (System.currentTimeMillis() - testTime) / 1000.0 + " secs");
				}
				
				long afterTime = System.currentTimeMillis();
				if (verbose) out.println(fullName+": AfterEach Start");
				for (Method ae : afterEach) try {invokeBlind(ae, instance);} catch (Throwable t)
				{
					if (t instanceof AssertionFailureException)
						err.println(fullName + ": Exception: " + t.getMessage());
					else
					{
						err.println(fullName + ": Exception");
						t.printStackTrace(err);
					}
					pass = false;
				}
				if (verbose) out.println(fullName + ": AfterEach End" + " " + (System.currentTimeMillis() - afterTime) / 1000.0 + " secs");
				
				out.println(fullName + ": " + (pass?"PASS":"FAIL") + " " + (System.currentTimeMillis() - millis) / 1000.0 + " secs");
				
				if (pass)
					results.passed++;
				else
					results.failed++;
				return;
			}
		}
	}

	private static Object invokeBlind(Method method, Object instance) throws AssertionError
	{
		Object out = null;
		try {
			out = method.invoke(instance);
		} catch (ClassCastException ex) {
			throw ex;
		} catch (InvocationTargetException ex) {
			if (ex.getTargetException() instanceof AssertionError)
				throw (AssertionError)ex.getTargetException();
			else
				throw new RuntimeException(ex);
		} catch (IllegalArgumentException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
		return out;
	}

	private static boolean isStringEmpty(String obj)
	{
		if (obj == null)
			return true;
		else
			return obj.trim().length() == 0;
	}

	private static <T> T create(Class<T> clazz)
	{
		Object out = null;
		try {
			out = clazz.getDeclaredConstructor().newInstance();
		} catch (NoSuchMethodException ex) {
			throw new RuntimeException(ex);
		} catch (SecurityException ex) {
			throw new RuntimeException(ex);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		
		return clazz.cast(out);
	}
	
	private static boolean areEqual(Object a, Object b)
	{
		if (a == null)
			return b == null;
		else if (b == null)
			return false;
		else
			return a.equals(b);
	}

	/**
	 * Main method for running tests.
	 * ARG0: The fully-qualified class name.
	 * ARG1: If verbose, "--verbose".
	 * @param args the arguments.
	 */
	public static void main(String[] args)
	{
		if (args.length < 1)
		{
			System.err.println("ERROR: Expected class name.");
			System.exit(1);
			return;
		}
		
		Class<?> resolvedClass; 
		try {
			resolvedClass = Class.forName(args[0]);
		} catch (ExceptionInInitializerError e) {
			System.err.println("ERROR: Class could not initialize.");
			e.printStackTrace(System.err);
			System.exit(1);
			return;
		} catch (LinkageError e) {
			System.err.println("ERROR: Class could not link. May have other dependencies.");
			e.printStackTrace(System.err);
			System.exit(1);
			return;
		} catch (ClassNotFoundException e) {
			System.err.println("ERROR: Class was not found.");
			System.exit(1);
			return;
		}
		
		long time = System.currentTimeMillis();
		TestResults results = performTestOn(
			resolvedClass, 
			System.out, 
			System.err, 
			args.length > 1 ? args[1].equalsIgnoreCase("--verbose") : false
		);

		System.out.printf("TESTS DONE. %.3f secs.", (System.currentTimeMillis() - time) / 1000.0);
		System.out.printf(" %d total,", results.total);
		if (results.ignored > 0)
			System.out.printf(" %d ignored,", results.ignored);
		if (results.failed > 0)
			System.out.printf(" %d failed,", results.failed);
		if (results.passed > 0)
			System.out.printf(" %d passed.", results.passed);
		System.out.printf("\n");
		System.exit(results.failed > 0 ? 1 : 0);
	}

}
