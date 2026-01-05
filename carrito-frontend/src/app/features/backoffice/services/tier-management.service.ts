import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface TierEvaluationResponse {
    message: string;
    tiersUpdated: number;
}

@Injectable({
    providedIn: 'root'
})
export class TierManagementService {
    private readonly apiUrl: string;

    constructor(private http: HttpClient) {
        this.apiUrl = environment.apiUrl + '/backoffice/tiers/evaluate';
    }

    evaluateTiers(): Observable<TierEvaluationResponse> {
        return this.http.post<TierEvaluationResponse>(this.apiUrl, {});
    }
}
