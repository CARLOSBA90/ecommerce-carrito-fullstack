import { Product } from '../../products/services/products.service';

export interface CartProduct {
    id: number;
    product: Product;
    quantity: number;
}
