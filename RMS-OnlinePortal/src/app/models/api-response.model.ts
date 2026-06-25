export interface ApiResponse<T> {
    header: {
        requestTimestamp: string;
        responseTimestamp: string;
        statusCode: string;
        message: string;
    };
    data: T;
}