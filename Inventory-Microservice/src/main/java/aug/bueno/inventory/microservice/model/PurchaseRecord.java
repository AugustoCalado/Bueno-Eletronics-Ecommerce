package aug.bueno.inventory.microservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRecord {

    private Long productId;
    private Integer quantityPurchased;

}
