import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import ru.yandex.praktikum.client.CourierClient;
import ru.yandex.praktikum.dataprovider.CourierProvider;
import ru.yandex.praktikum.pojo.CreateCourierRequest;
import ru.yandex.praktikum.pojo.LoginCourierRequest;

public class LoginTest {
    CourierClient courierClient = new CourierClient();
    private Integer id;

    @DisplayName("Кейс проверки,что курьер может залогинится")
    @Description("Должна вернуться статус код 200 и должен вернуться id курьера")
    @Test
    public void courierShouldBeLogin() {
        CreateCourierRequest createCourierRequest = new CourierProvider().getRandomCreateCourierRequest();
        courierClient.create(createCourierRequest);

        LoginCourierRequest loginCourierRequest = LoginCourierRequest.from(createCourierRequest);
        id = courierClient.login(loginCourierRequest)
                .statusCode(200)
                .extract().jsonPath().get("id");

        courierClient.deleteCourier(id)
                .statusCode(200);
    }

    @DisplayName("Кейс проверки что курьер не может залогинится с невалидным паролем")
    @Description("Должна вернуться ошибка 404 Учетная запись не найдена")
    @Test
    public void courierNoShouldBeLoginInvalidPassword() {
        CreateCourierRequest createCourierRequest = new CourierProvider().getRandomCreateCourierRequest();
        LoginCourierRequest loginCourierRequest = LoginCourierRequest.from(createCourierRequest);
        courierClient.create(createCourierRequest);
        createCourierRequest.setPassword("invalid_parol");
        courierClient.login(LoginCourierRequest.from(createCourierRequest))
                .statusCode(404)
                .body("message", Matchers.equalTo("Учетная запись не найдена"));
    }

    @DisplayName("Кейс проверки что курьер не может залогинится с неправильным логином или несуществующий пользователь")
    @Description("Должна вернуться ошибка 404 Учетная запись не найдена")
    @Test
    public void courierNoShouldBeLoginInvalidLogin() {
        CreateCourierRequest createCourierRequest = new CourierProvider().getRandomCreateCourierRequest();
        LoginCourierRequest loginCourierRequest = LoginCourierRequest.from(createCourierRequest);
        courierClient.create(createCourierRequest);
        createCourierRequest.setLogin("invalid_login");
        courierClient.login(LoginCourierRequest.from(createCourierRequest))
                .statusCode(404)
                .body("message", Matchers.equalTo("Учетная запись не найдена"));
    }

    @DisplayName("Кейс проверки что запрос возвращает ошибку если логинится без логина")
    @Description("Должна вернуться ошибка 400 Недостаточно данных для входа")
    @Test
    public void courierNoShouldBeLoginNoLogin() {
        CreateCourierRequest createCourierRequest = new CourierProvider().getRandomCreateCourierRequest();
        LoginCourierRequest loginCourierRequest = LoginCourierRequest.from(createCourierRequest);
        courierClient.create(createCourierRequest);
        createCourierRequest.setLogin("");
        courierClient.login(LoginCourierRequest.from(createCourierRequest))
                .statusCode(400)
                .body("message", Matchers.equalTo("Недостаточно данных для входа"));
    }

    @DisplayName("Кейс проверки что запрос возвращает ошибку если логинится без пароля")
    @Description("Должна вернуться ошибка 400 Недостаточно данных для входа")
    @Test
    public void courierNoShouldBeLoginNoPassword() {
        CreateCourierRequest createCourierRequest = new CourierProvider().getRandomCreateCourierRequest();
        LoginCourierRequest loginCourierRequest = LoginCourierRequest.from(createCourierRequest);
        courierClient.create(createCourierRequest);
        createCourierRequest.setPassword("");
        courierClient.login(LoginCourierRequest.from(createCourierRequest))
                .statusCode(400)
                .body("message", Matchers.equalTo("Недостаточно данных для входа"));
    }
}