import { Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { NavBarComponent } from './components/nav-bar/nav-bar.component';

export const routes: Routes = [
    { path: 'home', component: AppComponent },
    { path: '**', redirectTo: 'home'}
];
