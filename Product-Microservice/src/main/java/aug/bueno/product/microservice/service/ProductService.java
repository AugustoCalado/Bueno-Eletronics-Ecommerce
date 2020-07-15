package aug.bueno.product.microservice.service;


import aug.bueno.product.microservice.domain.dto.ProductDTO;

import java.util.Optional;

public interface ProductService {
    Optional<ProductDTO> findById(long id);

    Iterable<ProductDTO> findAll();

    ProductDTO save(ProductDTO productDTO);

    boolean update(ProductDTO productDTO);

    boolean delete(long id);
}
