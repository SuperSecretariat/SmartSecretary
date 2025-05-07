import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Observable } from 'rxjs';
import { OnInit } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class NavBarVisibilityService implements OnInit {
    private isNavBarVisible: BehaviorSubject<boolean>;
    private isNavBarVisibleAsObservable: Observable<boolean>;
    
    constructor() {
        this.isNavBarVisible = new BehaviorSubject<boolean>(false);
        this.isNavBarVisibleAsObservable = this.isNavBarVisible.asObservable();
    }

    ngOnInit(): void {

        console.log('NavBarVisibilityService initialized');
    }

    switchVisibility(): void {
        this.isNavBarVisible.next(!this.isNavBarVisible.getValue());
        
        //console.log("Navbar changed visibility -> visible value = " + this.isNavBarVisible.getValue());
    }

    getVisibility(): Observable<boolean> {
        return this.isNavBarVisibleAsObservable;
    }
}