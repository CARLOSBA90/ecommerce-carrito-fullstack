package com.ecommerce.carrito.soap;

import lombok.Data;
import jakarta.xml.bind.annotation.*;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "reportType",
        "month",
        "year"
})
@XmlRootElement(name = "getCustomerReportRequest", namespace = "http://ecommerce.com/soap/backoffice")
public class GetCustomerReportRequest {

    @XmlElement(required = true, namespace = "http://ecommerce.com/soap/backoffice")
    protected ReportType reportType;

    @XmlElement(required = true, namespace = "http://ecommerce.com/soap/backoffice")
    protected int month;

    @XmlElement(required = true, namespace = "http://ecommerce.com/soap/backoffice")
    protected int year;
}
