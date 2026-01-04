export interface LoginRequest {
    username: string;
    password: string;
}

export interface LoginResponse {
    token: string;
    type: string;
    customerId: number;
    username: string;
    roles: string[];
}

export interface AuthUser {
    customerId: number;
    username: string;
    roles: string[];
    token: string;
}
