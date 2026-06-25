export interface ICRSPayment {
    id: number;
    description: string;
    quantity: number;
    amount: number;
    tax: number;
    discount: number;
    grossAmount: number;
}
