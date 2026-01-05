import { CartProduct } from "./cart-product.interface";

export interface AppliedDiscount {
    code: string;
    name: string;
    discountType: string;
    discountAmount: number;
}

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
    totalDiscounts: number;
    totalAmount: number;
    appliedDiscounts: AppliedDiscount[];
}
