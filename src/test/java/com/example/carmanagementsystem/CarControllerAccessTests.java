package com.example.carmanagementsystem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CarControllerAccessTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarRepository carRepository;

    // 1. USER can GET cars
    @Test
    @WithMockUser(roles = "USER")
    void userCanGetCars() throws Exception {
        int actual = mockMvc.perform(get("/cars"))
                .andReturn()
                .getResponse()
                .getStatus();

        assertEquals(200, actual);
    }

    // 2. USER cannot POST cars
    @Test
    @WithMockUser(roles = "USER")
    void userCannotCreateCar() throws Exception {
        int actual = mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"brand":"Toyota","model":"Camry","year":2022}
                                """))
                .andReturn()
                .getResponse()
                .getStatus();

        assertEquals(403, actual);
    }

    // 3. USER cannot PUT cars
    @Test
    @WithMockUser(roles = "USER")
    void userCannotUpdateCar() throws Exception {
        int actual = mockMvc.perform(put("/cars/test-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"brand":"Toyota","model":"Camry","year":2023}
                                """))
                .andReturn()
                .getResponse()
                .getStatus();

        assertEquals(403, actual);
    }

    // 4. USER cannot DELETE cars
    @Test
    @WithMockUser(roles = "USER")
    void userCannotDeleteCar() throws Exception {
        int actual = mockMvc.perform(delete("/cars/test-id"))
                .andReturn()
                .getResponse()
                .getStatus();

        assertEquals(403, actual);
    }

    // 5. ADMIN can GET cars
    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanGetCars() throws Exception {
        int actual = mockMvc.perform(get("/cars"))
                .andReturn()
                .getResponse()
                .getStatus();

        assertEquals(200, actual);
    }

    // 6. ADMIN can POST cars
    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateCar() throws Exception {
        when(carRepository.save(any(Car.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        int actual = mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"brand":"BMW","model":"X5","year":2022}
                                """))
                .andReturn()
                .getResponse()
                .getStatus();

        assertEquals(201, actual);
    }

    // 7. ADMIN can PUT cars
    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanUpdateCar() throws Exception {
        when(carRepository.existsById("test-id")).thenReturn(true);
        when(carRepository.save(any(Car.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        int actual = mockMvc.perform(put("/cars/test-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"brand":"Audi","model":"A6","year":2024}
                                """))
                .andReturn()
                .getResponse()
                .getStatus();

        assertEquals(200, actual);
    }

    // 8. ADMIN cannot DELETE cars
    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCannotDeleteCar() throws Exception {
        int actual = mockMvc.perform(delete("/cars/test-id"))
                .andReturn()
                .getResponse()
                .getStatus();

        assertEquals(403, actual);
    }

    // 9. SUPERUSER can DELETE cars
    @Test
    @WithMockUser(roles = "SUPERUSER")
    void superuserCanDeleteCar() throws Exception {
        when(carRepository.existsById("test-id")).thenReturn(true);

        int actual = mockMvc.perform(delete("/cars/test-id"))
                .andReturn()
                .getResponse()
                .getStatus();

        assertEquals(204, actual);
    }

    // 10. Anonymous user cannot GET cars
    @Test
    void anonymousUserCannotGetCars() throws Exception {
        int actual = mockMvc.perform(get("/cars"))
                .andReturn()
                .getResponse()
                .getStatus();

        assertEquals(401, actual);
    }
}