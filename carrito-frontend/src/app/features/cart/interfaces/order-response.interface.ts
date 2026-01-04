export interface OrderResponse {
    id: number;
    customerId: number;
    totalAmount: number;
    totalDiscount: number;
    totalItems: number;
    cartType: string;
    generatedAt: string;
    items: OrderItemResponse[];
}

export interface OrderItemResponse {
    id: number;
    productId: number;
    productName: string;
    quantity: number;
    unitPrice: number;
    discountApplied: number;
    finalLinePrice: number;
}
