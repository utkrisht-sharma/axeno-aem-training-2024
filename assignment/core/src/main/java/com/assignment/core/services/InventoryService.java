package com.assignment.core.services;

/**
 * Service interface for checking inventory stock.
 */
public interface InventoryService {

    /**
     * Gets the available stock for a given product type.
     *
     * @return the available stock quantity
     */
    int getAvailableStock(String productType);
}

