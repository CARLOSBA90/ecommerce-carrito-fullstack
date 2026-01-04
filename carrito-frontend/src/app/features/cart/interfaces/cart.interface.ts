import { CartProduct } from "./cart-product.interface";

export interface Cart {
    id: number;
    sessionId: string | null;
    customerId: number | null;
    customerName: string | null;
    items: CartProduct[];
    type: 'SESSION' | 'CUSTOMER';
    creationDate: string;
    totalProductCount: number;
    subtotal: number;
}
