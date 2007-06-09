package de.kuriositaet.injection.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(value = { BinderTest.class, InjectorTest.class, MatcherTest.class, MultiPropertiesTest.class })
public class AllTests {

}
