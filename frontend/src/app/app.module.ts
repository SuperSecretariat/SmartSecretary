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
import { PubbleChatComponent } from './components/pubble-chat/pubble-chat.component';
import { ViewFormComponent } from './components/view-form/view-form.component';
import { BoardAdminLlmFilesComponent } from './pages/board-admin/board-admin-llm-files/board-admin-llm-files.component';
import { StudentCalendarComponent } from './components/student-calendar/student-calendar.component';
import { UploadCalendarComponent } from './pages/board-secretary/upload-calendar/upload-calendar.component';
import { BoardSecretaryChangeFormsComponent } from './pages/board-secretary/board-secretary-change-forms/board-secretary-change-forms.component';
import { TicketCardComponent } from './components/ticket-card/ticket-card.component';
import { TicketChatModalComponent } from './components/ticket-chat-modal/ticket-chat-modal.component';
import { TicketCreateModalComponent } from './components/ticket-create-modal/ticket-create-modal.component';
import { StudentTicketsComponent } from './pages/board-student/student-tickets/student-tickets.component';
import { SecretaryTicketsComponent } from './pages/board-secretary/secretary-tickets/secretary-tickets.component';
import { StudentYearlyCalendarComponent } from './components/student-yearly-calendar/student-yearly-calendar.component';
import { StudentExamsCalendarComponent } from './components/student-exams-calendar/student-exams-calendar.component';
import { ModifyFormComponent } from './components/modify-form/modify-form.component';
import { ViewRequestsComponent } from './pages/board-secretary/view-requests/view-requests.component';
import { ReviewRequestComponent } from './pages/board-secretary/review-request/review-request.component';
import { ManageNewsComponent } from './pages/board-secretary/manage-news/manage-news.component';
import { AddNewsComponent } from './pages/board-secretary/add-news/add-news.component';

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
    StudentCalendarComponent,
    UploadCalendarComponent,
    BoardSecretaryChangeFormsComponent,
    BoardSecretaryChangeFormsComponent,
    TicketCardComponent,
    TicketChatModalComponent,
    TicketCreateModalComponent,
    StudentTicketsComponent,
    SecretaryTicketsComponent,
    StudentYearlyCalendarComponent,
    StudentExamsCalendarComponent,
    ModifyFormComponent,
    ViewRequestsComponent,
    ReviewRequestComponent,
    ManageNewsComponent,
    AddNewsComponent,
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
],
  providers: [
    httpInterceptorProviders,
    provideHttpClient(withInterceptorsFromDi())
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
