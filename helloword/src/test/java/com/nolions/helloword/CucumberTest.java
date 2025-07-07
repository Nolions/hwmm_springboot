package com.nolions.helloword;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.nolions.helloword",
        plugin = {"pretty"}
)
public class CucumberTest {
}
