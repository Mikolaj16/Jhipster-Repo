import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ITutor } from 'app/shared/model/tutor.model';

type EntityResponseType = HttpResponse<ITutor>;
type EntityArrayResponseType = HttpResponse<ITutor[]>;

@Injectable({ providedIn: 'root' })
export class TutorService {
  public resourceUrl = SERVER_API_URL + 'api/tutors';

  constructor(protected http: HttpClient) {}

  create(tutor: ITutor): Observable<EntityResponseType> {
    return this.http.post<ITutor>(this.resourceUrl, tutor, { observe: 'response' });
  }

  update(tutor: ITutor): Observable<EntityResponseType> {
    return this.http.put<ITutor>(this.resourceUrl, tutor, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITutor>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITutor[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
