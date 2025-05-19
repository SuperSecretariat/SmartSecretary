import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";

const FORMS_API_CONTROLLER = "http://localhost:8081/api/forms";
const FORMS_API_REQUESTS = "http://localhost:8081/api/forms-requests"

const httpOptions = {
  headers: new HttpHeaders({ "Content-Type": "application/json" }),
};

@Injectable({
  providedIn: "root",
})
export class FormsService {
    constructor(private readonly http: HttpClient) {}

    getAllForms(): Observable<any> {
        return this.http.get(FORMS_API_CONTROLLER, {
            headers: httpOptions.headers,
            responseType: "json",
        });
    }

    getFormFieldsById(id: number): Observable<any> {
        return this.http.get(`${FORMS_API_CONTROLLER}/${id}/fields`, {
            headers: httpOptions.headers,
            responseType: "json",
        });
    }

    // getFormImage(id: number): Observable<Blob> {
    //     return this.http.get(`${FORMS_API}/${id}/image`, {
    //         headers: httpOptions.headers,
    //         responseType: "blob",
    //     });
    // }

    submitFormData(jwtToken: string, formId: number, fields: string[]): Observable<any> {
    const payload = {
        jwtToken,
        formId,
        fields
    };
    console.log("Payload to be sent:", payload); // Log the payload to check its structure
    return this.http.post(`${FORMS_API_REQUESTS}`, payload, {
        headers: httpOptions.headers,
        responseType: "json",
    });
}
}