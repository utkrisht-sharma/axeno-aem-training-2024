package com.assignment.core.models;

import com.assignment.core.constants.InventoryProduct;
import com.assignment.core.constants.ProductType;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AddProductModel {

    @ValueMapValue
    private String productType;

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String fileReference;

    private String productName;

    @PostConstruct
    protected void init(){
         switch (productType) {
             case ProductType.TOP_WEAR:
                 productName = InventoryProduct.BOY_SHIRT;
                 break;
             case ProductType.BOTTOM_WEAR:
                 productName = InventoryProduct. DHOTI_STYLE_PLAZO;
                 break;
             case ProductType.ACCESSORIES:
                 productName = InventoryProduct.COW_BOY_HATS;
                 break;
             default:
                 productName = InventoryProduct.UNKNOWN;
         }
     }

    public String getProductName() {
        return productName;
    }

    public String getFileReference() {
        return fileReference;
    }

    public String getTitle(){
        return title;
    }

}
