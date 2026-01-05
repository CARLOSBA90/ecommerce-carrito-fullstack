package com.ecommerce.carrito.soap;

import lombok.Data;
import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "customers"
})
@XmlRootElement(name = "getCustomerReportResponse", namespace = "http://ecommerce.com/soap/backoffice")
public class GetCustomerReportResponse {

    @XmlElement(name = "customers", namespace = "http://ecommerce.com/soap/backoffice")
    protected List<CustomerReportItem> customers = new ArrayList<>();

    public List<CustomerReportItem> getCustomers() {
        if (customers == null) {
            customers = new ArrayList<>();
        }
        return this.customers;
    }
}
