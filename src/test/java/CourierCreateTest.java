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

import static org.hamcrest.CoreMatchers.equalTo;

public class CourierCreateTest {
    private int id;

    private CourierClient courierClient = new CourierClient();

    @DisplayName("Кейс проврки создания курьера")
    @Description("Должна вернуться статус код 200 и в теле сообщение ок : true")
    @Test
    public void courierShouldBeCreated() {
        CreateCourierRequest createCourierRequest = CourierProvider.getRandomCreateCourierRequest();
        courierClient.createCourier(createCourierRequest)
                .statusCode(SC_CREATED)
                .body("ok", Matchers.equalTo(true));

        LoginCourierRequest loginCourierRequest = LoginCourierRequest.from(createCourierRequest);
        id = courierClient.loginCourier(loginCourierRequest)
                .statusCode(SC_OK)
                .extract().jsonPath().get("id");
    }

    @DisplayName("Кейс проверки, что нельзя создать двух одинаковых курьеров")
    @Description("Должна вернуться ошибка 409 Этот логин уже используется.Попробуйте другой")
    @Test
    public void repeatedCourierShouldBeCreated() {
        CreateCourierRequest createCourierRequest = CourierProvider.getRandomCreateCourierRequest();
        courierClient.createCourier(createCourierRequest);
        courierClient.createCourier(createCourierRequest)
                .statusCode(SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @DisplayName("Создание курьера без логина")
    @Description("Должна вернуться ошибка 400 Недостаточно данных для создания учетной записи")
    @Test
    public void createCourierWithoutLogin() {
        CreateCourierRequest createCourierRequest = new CreateCourierRequest();
        createCourierRequest.setLogin("");
        createCourierRequest.setPassword("2311");
        createCourierRequest.setFirstName("вцйпйй");
        courierClient.createCourier(createCourierRequest)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @DisplayName("Создание курьера без пароля")
    @Description("Должна вернуться ошибка 400 Недостаточно данных для создания учетной записи")
    @Test
    public void createCourierWithoutPassword() {
        CreateCourierRequest createCourierRequest = new CreateCourierRequest();
        createCourierRequest.setLogin("TestLogin");
        createCourierRequest.setPassword("");
        createCourierRequest.setFirstName("вцйпйй");
        courierClient.createCourier(createCourierRequest)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @After
    public void tearDown() {
        if (id != 0) {
            courierClient.deleteCourier(id)
                    .statusCode(SC_OK);
        }
    }
}






