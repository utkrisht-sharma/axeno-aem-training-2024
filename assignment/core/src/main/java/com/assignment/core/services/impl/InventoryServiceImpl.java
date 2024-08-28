package com.assignment.core.services.impl;

import com.assignment.core.constants.InventoryProduct;
import com.assignment.core.constants.InventoryStock;
import com.assignment.core.services.InventoryService;
import org.osgi.service.component.annotations.Component;

/**
 * Implementation of InventoryService to check available stock.
 */
@Component(service = InventoryService.class)
public class InventoryServiceImpl implements InventoryService {

    @Override
    public int getAvailableStock(String productType) {
        int availableStock;


        switch (productType) {
            case InventoryProduct.BOY_SHIRT:
                availableStock = InventoryStock.BOY_SHIRT_STOCK;
                break;
            case InventoryProduct.DHOTI_STYLE_PLAZO:
                availableStock = InventoryStock.DHOTI_PLAZO_STOCK;
                break;
            case InventoryProduct.COW_BOY_HATS:
                availableStock = InventoryStock.COW_BOY_HATS_STOCK;
                break;
            default:
                availableStock = 0;
                break;
        }

        return availableStock;
    }
}
