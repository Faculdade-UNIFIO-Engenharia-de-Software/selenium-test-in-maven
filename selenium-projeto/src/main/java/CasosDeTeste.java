import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.BooleanSupplier;

public class CasosDeTeste {
    static final String LOGIN = "https://practicetestautomation.com/practice-test-login/"; 
    static final String FORM = "https://www.selenium.dev/selenium/web/web-form.html"; 
    static WebDriver driver; 
    static WebDriverWait wait;
    static int passou = 0, total = 0; 

    public static void main(String[] args) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);


        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); 

        try {
            executar("CT01 Login válido redireciona", CasosDeTeste::ct01); 
            executar("CT02 Login válido exibe boas-vindas", CasosDeTeste::ct02); 
            executar("CT03 Usuário inválido", CasosDeTeste::ct03); 
            executar("CT04 Senha inválida", CasosDeTeste::ct04); 
            executar("CT05 Logout", CasosDeTeste::ct05); 
            executar("CT06 Envio do formulário", CasosDeTeste::ct06); 
            executar("CT07 Campos bloqueados", CasosDeTeste::ct07); 
            executar("CT08 Combobox", CasosDeTeste::ct08); 
            executar("CT09 Checkboxes", CasosDeTeste::ct09); 
            executar("CT10 Radio buttons", CasosDeTeste::ct10); 
        } finally {
            driver.quit(); 
        }
        System.out.println("\nPlacar: " + passou + " de " + total + " casos passaram"); 
    }

    static void executar(String nome, BooleanSupplier caso) { 
        total++; 
        boolean ok; 
        try {
            ok = caso.getAsBoolean(); 
        } catch (Exception e) {
            System.out.println("  erro: " + e.getClass().getSimpleName() + " - " + e.getMessage()); 
            ok = false; 
        }
        if (ok) passou++; 
        System.out.println((ok ? "PASSOU - " : "FALHOU - ") + nome); 
    }

    static void fazerLogin(String usuario, String senha) { 
        driver.get(LOGIN); 
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys(usuario); 
        driver.findElement(By.id("password")).sendKeys(senha);
        driver.findElement(By.id("submit")).click();
    }

    static boolean ct01() {
        fazerLogin("student", "Password123"); 
        return wait.until(ExpectedConditions.urlContains("logged-in-successfully")); //[cite: 1, 2]
    }

    static boolean ct02() {
        fazerLogin("student", "Password123");
        WebElement titulo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1"))); 
        String textoPagina = driver.findElement(By.tagName("body")).getText();
        return titulo.getText().equals("Logged In Successfully") && textoPagina.contains("Congratulations student"); 
    }

    static boolean ct03() {
        fazerLogin("incorrectUser", "Password123");
        WebElement erro = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("error"))); 
        return erro.getText().equals("Your username is invalid!"); 
    }

    static boolean ct04() {
        fazerLogin("student", "incorrectPassword");
        WebElement erro = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("error"))); 
        return erro.getText().equals("Your password is invalid!"); 
    }

    static boolean ct05() {
        fazerLogin("student", "Password123");
        WebElement botaoLogout = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Log out"))); 
        botaoLogout.click(); 
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).isDisplayed(); 
    }

    static boolean ct06() {
        driver.get(FORM); 
        WebElement inputTexto = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("my-text-id")));
        inputTexto.sendKeys("Aluno UNIFIO");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement mensagem = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        return mensagem.getText().equals("Received!");
    }

    static boolean ct07() {
        driver.get(FORM); 
        WebElement disabledInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("my-disabled")));
        WebElement readonlyInput = driver.findElement(By.name("my-readonly"));

        boolean naoHabilitado = !disabledInput.isEnabled();
        boolean temReadonly = readonlyInput.getAttribute("readonly") != null;
        return naoHabilitado && temReadonly;
    }

    static boolean ct08() {
        driver.get(FORM); 
        WebElement selectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("my-select")));
        Select dropdown = new Select(selectElement);
        dropdown.selectByVisibleText("Two");

        return "2".equals(dropdown.getFirstSelectedOption().getAttribute("value"));
    }

    static boolean ct09() {
        driver.get(FORM); 
        WebElement check1 = wait.until(ExpectedConditions.elementToBeClickable(By.id("my-check-1")));
        WebElement check2 = driver.findElement(By.id("my-check-2"));

        boolean estadoInicialOk = check1.isSelected() && !check2.isSelected();

        check1.click();
        check2.click();

        boolean estadoFinalOk = !check1.isSelected() && check2.isSelected();
        return estadoInicialOk && estadoFinalOk;
    }

    static boolean ct10() {
        driver.get(FORM); 
        WebElement checkedRadio = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("my-radio-1")));
        WebElement defaultRadio = driver.findElement(By.id("my-radio-2"));

        defaultRadio.click();

        return defaultRadio.isSelected() && !checkedRadio.isSelected();
    }
}