import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, map, catchError, throwError } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface CustomerReportItem {
    customerId: number;
    fullName: string;
    tierFrom?: string;
    tierTo?: string;
    dateOfChange?: string;
    totalSpent: number;
    totalOrders: number;
}

export type ReportType = 'ALL_CHANGES' | 'NEW_VIP' | 'LOST_VIP' | 'CURRENT_VIP';

@Injectable({
    providedIn: 'root'
})
export class CustomerReportService {
    private readonly soapUrl: string;

    constructor(private http: HttpClient) {
        this.soapUrl = environment.apiUrl.replace(/\/api$/, '') + '/ws';
    }

    getCustomerReport(reportType: ReportType, month: number, year: number): Observable<CustomerReportItem[]> {
        const soapBody = this.buildSoapRequest(reportType, month, year);
        const headers = new HttpHeaders({
            'Content-Type': 'text/xml',
            'Accept': 'text/xml'
        });

        return this.http.post(this.soapUrl, soapBody, { headers, responseType: 'text' })
            .pipe(
                map(xmlResponse => this.parseXmlResponse(xmlResponse)),
                catchError(err => {
                    const errorMessage = this.extractSoapFault(err);
                    return throwError(() => ({ ...err, parsedMessage: errorMessage }));
                })
            );
    }

    private buildSoapRequest(reportType: ReportType, month: number, year: number): string {
        return `
      <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:back="http://ecommerce.com/soap/backoffice">
         <soapenv:Header/>
         <soapenv:Body>
            <back:getCustomerReportRequest>
               <back:reportType>${reportType}</back:reportType>
               <back:month>${month}</back:month>
               <back:year>${year}</back:year>
            </back:getCustomerReportRequest>
         </soapenv:Body>
      </soapenv:Envelope>
    `;
    }

    private parseXmlResponse(xml: string): CustomerReportItem[] {
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(xml, 'text/xml');
        const customerNodes = xmlDoc.getElementsByTagNameNS('*', 'customers');
        const result: CustomerReportItem[] = [];

        for (let i = 0; i < customerNodes.length; i++) {
            const node = customerNodes[i];
            result.push({
                customerId: Number(this.getNodeValue(node, 'customerId')),
                fullName: this.getNodeValue(node, 'fullName'),
                tierFrom: this.getNodeValue(node, 'tierFrom'),
                tierTo: this.getNodeValue(node, 'tierTo'),
                dateOfChange: this.getNodeValue(node, 'dateOfChange'),
                totalSpent: Number(this.getNodeValue(node, 'totalSpent')),
                totalOrders: Number(this.getNodeValue(node, 'totalOrders')),
            });
        }

        return result;
    }

    private getNodeValue(parent: Element, tagName: string): string {
        const elements = parent.getElementsByTagNameNS('*', tagName);
        return elements.length > 0 && elements[0].textContent ? elements[0].textContent : '';
    }

    private extractSoapFault(err: any): string {
        if (err.error && typeof err.error === 'string') {
            const parser = new DOMParser();
            const xmlDoc = parser.parseFromString(err.error, 'text/xml');
            const faultString = xmlDoc.getElementsByTagName('faultstring')[0];
            if (faultString?.textContent) {
                return faultString.textContent;
            }
        }
        return 'Error desconocido al consultar el servicio SOAP';
    }
}
