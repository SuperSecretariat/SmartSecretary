import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class navbarVisibilityService {
    private IsNavBarVisible = new BehaviorSubject<boolean>(false);
    isVisible$ = this.IsNavBarVisible.asObservable();
    
    showComponent(): void {
        this.IsNavBarVisible.next(true);
    }

    hideComponent(): void {
        this.IsNavBarVisible.next(false);
    }
}