# Cucunber 

## 添加相關依賴

pom檔中加上下面套件依賴

```xml
<dependencies>
  <dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>7.14.0</version>
    <scope>test</scope>
  </dependency>
  
  <dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit</artifactId>
    <version>7.14.0</version>
    <scope>test</scope>
  </dependency>
  
  <dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-spring</artifactId>
    <version>7.14.0</version>
    <scope>test</scope>
  </dependency>
  
  <dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.24.2</version>
    <scope>test</scope>
  </dependency>
</dependencies>
```

## 建立 Cucumber 測試啟動器

### 建立 CucumberTest.java

```java
@RunWith(Cucumber.class)
@CucumberOptions(
  features = "src/test/resources/features",
  glue = "com.nolions.helloword",
  plugin = {"pretty"}
)
public class CucumberTest {

}
```

> ps1. @RunWith(Cucumber.class) 啟動 Cucumber JUnit 集成。 <br>
> ps2. @CucumberOptions 指定 feature 路徑與 glue package。<br>
> ps3. 因為Cucumber 會自動尋找並執行對應步驟定義和配置，所以class中不需要進行實作


### 建立 CucumberSpringConfiguration.java

```java
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class CucumberSpringConfiguration {

}
```

> ps1. 這邊使用了`SpringBootTest.WebEnvironment.RANDOM_POR`，表示程式會以隨機可用埠啟動。<br>
> ps2. Cucumber 只需要此配置類以啟動 Spring Context，所以class中不需要進行實作。<br>
> ps3. Cucumber 會從 glue 指定的 package（及其子 package）中掃描 Step Definitions 和配置類。因此，CucumberSpringConfiguration.java 與 必須與下面步驟建立的`StepDef`類別 類保持在相同的package以確保 Cucumber 正確發現它們。


## 建立 Step Definitions 類別

```gherkin
Feature: Hello API

  Scenario: 呼叫 Hello 路由
    Given server ruunning
    When call "hello" API
    Then I should get response "Hello, world!"
```

## 建立 Step Definitions 類別

根據上面步驟的gherkin檔去建立相關Step Definitions class 

```java
public class HellowordStepDefs {
    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<String> response;

    @LocalServerPort
    private int port;


    @Given("server running")
    public void serviceRunning() {
        // 預設 SpringBootTest 會啟動應用程式
    }

    @When("call {string} API")
    public void callApi(String path) {
        String baseUrl = "http://localhost:" + port + "/" + path;
        response = restTemplate.getForEntity(baseUrl, String.class);
    }

    @Then("get {string} response")
    public void verifyResponse(String expected) {
        assertThat(response.getBody()).isEqualTo(expected);
    }
}
```

在 Step 定義類別中的常用註解：

| 註解        | 作用                                               |
|-----------|--------------------------------------------------|
| @Given    | 定義前置條件(step)，對應 Gherkin 的 Given 語句               |
| @When     | 定義執行動作(step)，對應 Gherkin 的 When 語句                |
| @Then     | 定義驗證結果(step)，對應 Gherkin 的 Then 語句                |
| @And/@But | 定義補充步驟，對應 Gherkin 的 And 或 But，可以緊接著@When或@Then使用 |


> `@LocalServerPort` 在 `SpringBootTest(webEnvironment = RANDOM_PORT)` 模式下，應用程式會以隨機可用埠啟動，所以必須使用`@LocalServerPort`來取得正確的port號

## 相關專案架構如下

| 檔案                                                   | 說明           |
|------------------------------------------------------|--------------|
| `src/test/resources/features/*.feature`              | Gherkin 測試情境 |
| `src/test/java/.../CucumberTest.java`                | Cucumber 啟動器 |
| `src/test/java/.../CucumberSpringConfiguration.java` | Spring 測試配置  |
| `src/test/java/.../steps/*StepDefs.java`             | Step 定義      |

## Other

1. IntelliJ 只支援 regex，不支援 Expression escape，所以如果在gherkin中使用了`/`可能會導致IntelliJ誤判Step Definitions不存在，所以建議避免使用 `/`。
