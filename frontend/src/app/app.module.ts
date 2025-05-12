import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { HomeComponent } from './components/home/home.component';
import { ProfileComponent } from './components/profile/profile.component';
import { BoardAdminComponent } from './pages/board-admin/board-admin.component';
import { CreateFormComponent } from './components/create-form/create-form.component';
import { SubmittedFormsComponent } from './components/submitted-forms/submitted-forms.component';
import { AccountComponent } from './components/account/account.component';
import { NewsfeedComponent } from './components/newsfeed/newsfeed.component';

import { RouterModule } from '@angular/router';
import { httpInterceptorProviders } from './components/_helpers/http.interceptor';
import { BoardSecretaryComponent } from './pages/board-secretary/board-secretary.component';
import { HeaderComponent } from "./components/header/header.component";
import { NavBarComponent } from "./components/nav-bar/nav-bar.component";
import { DashboardComponent } from "./components/dashboard/dashboard.component";
import { FooterComponent } from "./components/footer/footer.component";
import { AdminNavBarComponent } from "./components/admin-nav-bar/admin-nav-bar.component";
import { BoardAdminAddComponent} from "./pages/board-admin/board-admin-add/board-admin-add.component";
import { BoardAdminShowAuthKeyComponent } from './pages/board-admin/board-admin-show-auth-key/board-admin-show-auth-key.component';
import { SecretaryNavBarComponent } from './components/secretary-nav-bar/secretary-nav-bar.component';
import { BoardSecretaryAddComponent } from './pages/board-secretary/board-secretary-add/board-secretary-add.component';
import { TicketStudentComponent } from './components/ticket-student/ticket-student.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    ProfileComponent,
    BoardAdminComponent,
    BoardSecretaryComponent,
    AccountComponent,
    SecretaryNavBarComponent,
    BoardAdminAddComponent,
    BoardAdminShowAuthKeyComponent,
    BoardSecretaryAddComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    AdminNavBarComponent,
    FormsModule,
    HeaderComponent,
    NavBarComponent,
    DashboardComponent,
    FooterComponent,
    CreateFormComponent,
    SubmittedFormsComponent,
    NewsfeedComponent,
    TicketStudentComponent
    CommonModule,
    HomeComponent,
    AccountComponent, 
    RouterModule
],
  providers: [
    httpInterceptorProviders,
    provideHttpClient(withInterceptorsFromDi())
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
