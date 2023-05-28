import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import ru.yandex.praktikum.client.CourierClient;
import ru.yandex.praktikum.dataprovider.CourierProvider;
import ru.yandex.praktikum.pojo.CreateCourierRequest;
import ru.yandex.praktikum.pojo.LoginCourierRequest;
import static org.apache.http.HttpStatus.*;

public class LoginTest {
    CourierClient courierClient = new CourierClient();
    private Integer id;

    @DisplayName("Кейс проверки,что курьер может залогинится")
    @Description("Должна вернуться статус код 200 и должен вернуться id курьера")
    @Test
    public void courierShouldBeLogin() {
        CreateCourierRequest createCourierRequest = new CourierProvider().getRandomCreateCourierRequest();
        courierClient.createCourier(createCourierRequest);

        LoginCourierRequest loginCourierRequest = LoginCourierRequest.from(createCourierRequest);
        id = courierClient.loginCourier(loginCourierRequest)
                .statusCode(SC_OK)
                .extract().jsonPath().get("id");

    }

    @DisplayName("Кейс проверки что курьер не может залогинится с невалидным паролем")
    @Description("Должна вернуться ошибка 404 Учетная запись не найдена")
    @Test
    public void courierNoShouldBeLoginInvalidPassword() {
        CreateCourierRequest createCourierRequest = new CourierProvider().getRandomCreateCourierRequest();
        LoginCourierRequest loginCourierRequest = LoginCourierRequest.from(createCourierRequest);
        courierClient.createCourier(createCourierRequest);
        createCourierRequest.setPassword("invalid_parol");
        courierClient.loginCourier(LoginCourierRequest.from(createCourierRequest))
                .statusCode(SC_NOT_FOUND)
                .body("message", Matchers.equalTo("Учетная запись не найдена"));
    }

    @DisplayName("Кейс проверки что курьер не может залогинится с неправильным логином или несуществующий пользователь")
    @Description("Должна вернуться ошибка 404 Учетная запись не найдена")
    @Test
    public void courierNoShouldBeLoginInvalidLogin() {
        CreateCourierRequest createCourierRequest = new CourierProvider().getRandomCreateCourierRequest();
        LoginCourierRequest loginCourierRequest = LoginCourierRequest.from(createCourierRequest);
        courierClient.createCourier(createCourierRequest);
        createCourierRequest.setLogin("invalid_login");
        courierClient.loginCourier(LoginCourierRequest.from(createCourierRequest))
                .statusCode(SC_NOT_FOUND)
                .body("message", Matchers.equalTo("Учетная запись не найдена"));
    }

    @DisplayName("Кейс проверки что запрос возвращает ошибку если логинится без логина")
    @Description("Должна вернуться ошибка 400 Недостаточно данных для входа")
    @Test
    public void courierNoShouldBeLoginNoLogin() {
        CreateCourierRequest createCourierRequest = new CourierProvider().getRandomCreateCourierRequest();
        LoginCourierRequest loginCourierRequest = LoginCourierRequest.from(createCourierRequest);
        courierClient.createCourier(createCourierRequest);
        createCourierRequest.setLogin("");
        courierClient.loginCourier(LoginCourierRequest.from(createCourierRequest))
                .statusCode(SC_BAD_REQUEST)
                .body("message", Matchers.equalTo("Недостаточно данных для входа"));
    }

    @DisplayName("Кейс проверки что запрос возвращает ошибку если логинится без пароля")
    @Description("Должна вернуться ошибка 400 Недостаточно данных для входа")
    @Test
    public void courierNoShouldBeLoginNoPassword() {
        CreateCourierRequest createCourierRequest = new CourierProvider().getRandomCreateCourierRequest();
        LoginCourierRequest loginCourierRequest = LoginCourierRequest.from(createCourierRequest);
        courierClient.createCourier(createCourierRequest);
        createCourierRequest.setPassword("");
        courierClient.loginCourier(LoginCourierRequest.from(createCourierRequest))
                .statusCode(SC_BAD_REQUEST)
                .body("message", Matchers.equalTo("Недостаточно данных для входа"));
    }

    @After
    public void tearDown() {
        if (id != null) {
            courierClient.deleteCourier(id)
                    .statusCode(SC_OK);
        }
    }
}