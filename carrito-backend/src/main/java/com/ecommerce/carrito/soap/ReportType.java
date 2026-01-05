package com.ecommerce.carrito.soap;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "ReportType", namespace = "http://ecommerce.com/soap/backoffice")
@XmlEnum
public enum ReportType {
    CURRENT_VIP,
    NEW_VIP,
    LOST_VIP,
    ALL_CHANGES;

    public String value() {
        return name();
    }

    public static ReportType fromValue(String v) {
        return valueOf(v);
    }
}
