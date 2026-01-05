package com.ecommerce.carrito.controller;

import com.ecommerce.carrito.soap.GetCustomerReportRequest;
import com.ecommerce.carrito.soap.GetCustomerReportResponse;
import com.ecommerce.carrito.soap.CustomerReportItem;
import com.ecommerce.carrito.service.TierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;

@Endpoint
public class CustomerReportController {

    private static final String NAMESPACE_URI = "http://ecommerce.com/soap/backoffice";

    private final TierService tierService;

    @Autowired
    public CustomerReportController(TierService tierService) {
        this.tierService = tierService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCustomerReportRequest")
    @ResponsePayload
    public GetCustomerReportResponse getCustomerReport(@RequestPayload GetCustomerReportRequest request) {
        System.out.println("SOAP Request received:");
        System.out.println("  ReportType: " + request.getReportType());
        System.out.println("  Month: " + request.getMonth());
        System.out.println("  Year: " + request.getYear());

        GetCustomerReportResponse response = new GetCustomerReportResponse();

        List<CustomerReportItem> items = tierService.getCustomerReport(
                request.getReportType(),
                request.getMonth(),
                request.getYear());

        response.getCustomers().addAll(items);

        return response;
    }
}
