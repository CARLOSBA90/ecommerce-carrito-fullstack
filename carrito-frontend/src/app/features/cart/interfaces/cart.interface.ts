import { CartProduct } from "./cart-product.interface";

export interface Cart {
    id: string;
    userId: string;
    products: CartProduct[];
}

