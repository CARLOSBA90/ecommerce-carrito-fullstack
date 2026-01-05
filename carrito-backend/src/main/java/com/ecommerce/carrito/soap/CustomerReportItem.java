package com.ecommerce.carrito.soap;

import lombok.Data;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.math.BigDecimal;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerReportItem", namespace = "http://ecommerce.com/soap/backoffice", propOrder = {
        "customerId",
        "fullName",
        "tierFrom",
        "tierTo",
        "dateOfChange",
        "totalSpent",
        "totalOrders"
})
public class CustomerReportItem {

    @XmlElement(required = true, namespace = "http://ecommerce.com/soap/backoffice")
    protected Long customerId;

    @XmlElement(required = true, namespace = "http://ecommerce.com/soap/backoffice")
    protected String fullName;

    @XmlElement(namespace = "http://ecommerce.com/soap/backoffice")
    protected String tierFrom;

    @XmlElement(namespace = "http://ecommerce.com/soap/backoffice")
    protected String tierTo;

    @XmlElement(namespace = "http://ecommerce.com/soap/backoffice")
    protected String dateOfChange;

    @XmlElement(required = true, namespace = "http://ecommerce.com/soap/backoffice")
    protected BigDecimal totalSpent;

    @XmlElement(required = true, namespace = "http://ecommerce.com/soap/backoffice")
    protected int totalOrders;
}
