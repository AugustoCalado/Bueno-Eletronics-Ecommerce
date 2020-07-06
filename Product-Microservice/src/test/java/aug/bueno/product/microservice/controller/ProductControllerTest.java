package aug.bueno.product.microservice.controller;

import aug.bueno.product.microservice.domain.dto.ProductDTO;
import aug.bueno.product.microservice.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    private ObjectMapper mapper;

    @MockBean
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mapper = new ObjectMapper();
    }

    @Test
    @DisplayName("GET /product/1 - Found")
    void testGetProductByIdPFound() throws Exception {

        ProductDTO mockProduct = new ProductDTO(1, 1, "Product Name", 10);

        doReturn(Optional.of(mockProduct)).when(productService).findById(1);

        mockMvc.perform(get("/product/{id}", 1))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Product Name")))
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.version", is(1)));
    }

    @Test
    @DisplayName("GET /product/1 - Not Found")
    void testGetProductByIdNotFound() throws Exception {
        doReturn(Optional.empty()).when(productService).findById(1);

        mockMvc.perform(get("/product/{id}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /product - Success")
    void testCreateProduct() throws Exception {
        ProductDTO postProduct = new ProductDTO("Product Name", 10);
        ProductDTO mockProduct = new ProductDTO(1, 1, "Product Name", 10);

        doReturn(mockProduct).when(productService).save(any());

        mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(postProduct)))

                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/product/1"))

                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Product Name")))
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.version", is(1)));
    }

    @Test
    @DisplayName("PUT /product/1 - success")
    void testProductPutSuccess() throws Exception {
        ProductDTO putProduct = new ProductDTO("Product Name", 10);
        ProductDTO mockProduct = new ProductDTO(1, 1,"Product Name", 10);

        doReturn(Optional.of(mockProduct)).when(productService).findById(1);

        doReturn(true).when(productService).update(any());

        mockMvc.perform(put("/product/{id}",1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.IF_MATCH, 1)
                .content(mapper.writeValueAsString(putProduct)))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(header().string(HttpHeaders.ETAG, "\"2\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/product/1"))

                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Product Name")))
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.version", is(2)));

    }

    @Test
    @DisplayName("PUT /product/1 - Version Mismatch")
    void testProductPutVersionMismatch() throws Exception {
        ProductDTO putProduct = new ProductDTO("Product Name", 10);
        ProductDTO mockProduct = new ProductDTO(1, 2,"Product Name", 10);

        doReturn(Optional.of(mockProduct)).when(productService).findById(1);

        doReturn(true).when(productService).update(any());

        mockMvc.perform(put("/product/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.IF_MATCH, 1)
                .content(mapper.writeValueAsString(putProduct)))

                .andExpect(status().isConflict());

    }
}
