import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "../../../environments/environments";
import { StorageService } from "./storage.service";

const FORMS_API_CONTROLLER = `${environment.backendUrl}/api/forms`;
const FORMS_API_REQUESTS = `${environment.backendUrl}/api/form-requests`
const BASE_DIR = '/src/main/resources/requests';
const FILES_API = `${environment.backendUrl}/api/files`;

const httpOptions = {
    headers: new HttpHeaders({ "Content-Type": "application/json" }),
};

@Injectable({
    providedIn: "root",
})
export class FormsService {
    constructor(private readonly http: HttpClient, private readonly storageService: StorageService) { }

    getAllForms(): Observable<any> {
        return this.http.get(
            FORMS_API_CONTROLLER,
            {
                headers: httpOptions.headers,
                responseType: "json",
            }
        );
    }

    getFormFieldsById(id: number): Observable<any> {
        return this.http.get(
            `${FORMS_API_CONTROLLER}/${id}/fields`,
            {
                headers: httpOptions.headers,
                responseType: "json",
            }
        );
    }

    // getFormImage(id: number): Observable<Blob> {
    //     return this.http.get(`${FORMS_API}/${id}/image`, {
    //         headers: httpOptions.headers,
    //         responseType: "blob",
    //     });
    // }

    submitFormData(formId: number, fields: string[]): Observable<any> {
        const jwtToken = this.storageService.getUser().token;
        const payload = {
            jwtToken,
            formId,
            fields
        };
        return this.http.post(
            `${FORMS_API_REQUESTS}/create`,
            payload,
            {
                headers: this.addAuthorizationHeader(httpOptions.headers),
                responseType: "json",
            }
        );
    }

    getSubmittedRequests(): Observable<any> {
        return this.http.get(
            `${FORMS_API_REQUESTS}/submitted`,
            {
                headers: this.addAuthorizationHeader(httpOptions.headers),
                responseType: "json",
            }
        );
    }

    getAllSubmittedRequests(): Observable<any> {
        return this.http.get(
            `${FORMS_API_REQUESTS}/review-requests`,
            {
                headers: this.addAuthorizationHeader(httpOptions.headers),
                responseType: "json",
            }
        );
    }

    addAuthorizationHeader(headers: HttpHeaders): HttpHeaders {
        const jwtToken = this.storageService.getUser().token;
        return headers.set('Authorization', 'Bearer ' + jwtToken);
    }

    updateFormStatusById(id: number, status: string): Observable<any> {
        return this.http.patch(
            `${FORMS_API_REQUESTS}/${id}/status`,
            status,
            {
                headers: this.addAuthorizationHeader(
                    httpOptions.headers.set('Content-Type', 'text/plain')
                ),
                responseType: "json",
            }
        );
    }

    createForm(title: string, isActive: boolean): Observable<any> {
        const payload = { title, isActive };
        return this.http.post(
            FORMS_API_CONTROLLER,
            payload,
            {
                headers: this.addAuthorizationHeader(httpOptions.headers),
                responseType: "json",
            }
        );
    }

    downloadRequest(subPath: string, name: string): Observable<Blob> {
        let params = new HttpParams().set('file', name);
        if (subPath) params = params.set('subPath', subPath);
        return this.http.get(
            FILES_API + '/download' + BASE_DIR,
            {
                params, responseType: 'blob'
            },
        );
    }

    getFormRequestFieldsById(id: number): Observable<any> {
        return this.http.get(
            `${FORMS_API_REQUESTS}/${id}/fields`,
            {
                headers: httpOptions.headers,
                responseType: "json",
            }
        );
    }

    deleteFormRequest(id: number): Observable<any> {
        return this.http.delete(
            `${FORMS_API_REQUESTS}/${id}`,
            {
                headers: this.addAuthorizationHeader(httpOptions.headers),
                responseType: "json",
            }
        );
    }
}