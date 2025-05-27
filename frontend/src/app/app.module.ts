import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ProfileComponent } from './components/profile/profile.component';
import { BoardAdminComponent } from './pages/board-admin/board-admin.component';
import { CreateFormComponent } from './components/create-form/create-form.component';
import { SubmittedFormsComponent } from './components/submitted-forms/submitted-forms.component';
import { AccountComponent } from './components/account/account.component';
import { NewsfeedComponent } from './components/newsfeed/newsfeed.component';

import { httpInterceptorProviders } from './components/_helpers/http.interceptor';
import { BoardSecretaryComponent } from './pages/board-secretary/board-secretary.component';
import { HeaderComponent } from "./components/header/header.component";
import { NavBarComponent } from "./components/nav-bar/nav-bar.component";
import { DashboardComponent } from "./components/dashboard/dashboard.component";
import { FooterComponent } from "./components/footer/footer.component";
import { AdminNavBarComponent } from "./components/admin-nav-bar/admin-nav-bar.component";
import { BoardAdminAddComponent} from "./pages/board-admin/board-admin-add/board-admin-add.component";
import { BoardAdminShowAuthKeyComponent } from './pages/board-admin/board-admin-show-auth-key/board-admin-show-auth-key.component';
import { BoardAdminDeleteUserComponent} from "./pages/board-admin/board-admin-delete-user/board-admin-delete-user.component";
import { SecretaryNavBarComponent } from './components/secretary-nav-bar/secretary-nav-bar.component';
import { BoardSecretaryAddComponent } from './pages/board-secretary/board-secretary-add/board-secretary-add.component';
import { CompleteFormComponent } from './components/complete-form/complete-form.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { TicketStudentComponent } from './components/ticket-student/ticket-student.component';
import { ViewTicketsSecretaryComponent } from './pages/board-secretary/view-tickets-secretary/view-tickets.component';
import { SentTicketsComponent } from './pages/board-secretary/sent-tickets-secretary/sent-tickets.component';
import { TicketModalComponent } from './components/ticket-modal/ticket-modal.component';
import { PubbleChatComponent } from './components/pubble-chat/pubble-chat.component';
import { ViewFormComponent } from './components/view-form/view-form.component';
import { BoardAdminLlmFilesComponent } from './pages/board-admin/board-admin-llm-files/board-admin-llm-files.component';
import { BoardSecretaryChangeFormsComponent } from './pages/board-secretary/board-secretary-change-forms/board-secretary-change-forms.component';
import { BoardSecretaryViewTicketsComponent } from './pages/board-secretary/board-secretary-view-tickets/board-secretary-view-tickets.component';
import { BoardSecretaryReviewTicketComponent } from './pages/board-secretary/board-secretary-review-ticket/board-secretary-review-ticket.component';


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
    BoardAdminDeleteUserComponent,
    BoardSecretaryAddComponent,
    CompleteFormComponent,
    ForgotPasswordComponent,
    ResetPasswordComponent,
    PubbleChatComponent,
    ViewFormComponent,
    BoardAdminLlmFilesComponent,
    BoardSecretaryChangeFormsComponent,
    BoardSecretaryViewTicketsComponent,
    BoardSecretaryReviewTicketComponent
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
    TicketStudentComponent,
],
  providers: [
    httpInterceptorProviders,
    provideHttpClient(withInterceptorsFromDi())
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
