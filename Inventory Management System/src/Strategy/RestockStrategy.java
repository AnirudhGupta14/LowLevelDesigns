package Strategy;

import entities.Product;

public interface RestockStrategy {
    int restock(Product product);
}
