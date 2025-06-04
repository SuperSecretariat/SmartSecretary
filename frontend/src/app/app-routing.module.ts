import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { RegisterComponent } from './components/register/register.component';
import { LoginComponent } from './components/login/login.component';
import { CreateFormComponent } from './components/create-form/create-form.component';
import { SubmittedFormsComponent } from './components/submitted-forms/submitted-forms.component';
import { AccountComponent } from './components/account/account.component';
import { NewsfeedComponent } from './components/newsfeed/newsfeed.component';
import { AuthGuard } from './components/_helpers/auth.guard';
import { AdminGuard } from './components/_helpers/admin.guard';
import { StudentGuard } from './components/_helpers/student.guard';
import { SecretaryGuard } from './components/_helpers/secretary.guard';
import { BoardAdminComponent } from './pages/board-admin/board-admin.component';
import { BoardAdminAddComponent } from './pages/board-admin/board-admin-add/board-admin-add.component';
import { BoardAdminShowAuthKeyComponent } from './pages/board-admin/board-admin-show-auth-key/board-admin-show-auth-key.component';
import { BoardAdminDeleteUserComponent } from './pages/board-admin/board-admin-delete-user/board-admin-delete-user.component';
import { BoardSecretaryAddComponent} from './pages/board-secretary/board-secretary-add/board-secretary-add.component';
import { BoardSecretaryComponent } from './pages/board-secretary/board-secretary.component';
import { CompleteFormComponent } from './components/complete-form/complete-form.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { PubbleChatComponent } from './components/pubble-chat/pubble-chat.component';
import { ViewFormComponent } from './components/view-form/view-form.component';
import { BoardAdminLlmFilesComponent } from './pages/board-admin/board-admin-llm-files/board-admin-llm-files.component';
import { StudentCalendarComponent } from './components/student-calendar/student-calendar.component';
import { UploadCalendarComponent} from './pages/board-secretary/upload-calendar/upload-calendar.component';
import { BoardSecretaryChangeFormsComponent } from './pages/board-secretary/board-secretary-change-forms/board-secretary-change-forms.component';
import { StudentTicketsComponent } from './pages/board-student/student-tickets/student-tickets.component';
import { SecretaryTicketsComponent } from './pages/board-secretary/secretary-tickets/secretary-tickets.component';
import { ModifyFormComponent } from './components/modify-form/modify-form.component';
import { ViewRequestsComponent } from './pages/board-secretary/view-requests/view-requests.component';
import { ReviewRequestComponent } from './pages/board-secretary/review-request/review-request.component';
import { AddNewsComponent } from './pages/board-secretary/add-news/add-news.component';
import { ManageNewsComponent } from './pages/board-secretary/manage-news/manage-news.component';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },

  {
    path: 'student',
    children: [
      { path: 'create-form', component: CreateFormComponent, canActivate: [AuthGuard, StudentGuard] },
      { path: 'submitted-forms', component: SubmittedFormsComponent, canActivate: [AuthGuard, StudentGuard] },
      { path: 'account', component: AccountComponent, canActivate: [AuthGuard, StudentGuard] },
      { path: 'newsfeed', component: NewsfeedComponent, canActivate: [AuthGuard, StudentGuard] },
      { path: 'tickets', component: StudentTicketsComponent, canActivate: [AuthGuard, StudentGuard] },
      { path: 'complete-form/:id', component: CompleteFormComponent, canActivate: [AuthGuard, StudentGuard] },
      { path: 'pubble', component: PubbleChatComponent, canActivate: [AuthGuard, StudentGuard] },
      { path: 'view-form/:id', component: ViewFormComponent},
      { path: 'modify-form/:id/:formId', component: ModifyFormComponent, canActivate: [AuthGuard, StudentGuard] },
      { path: 'calendar', component: StudentCalendarComponent, canActivate: [AuthGuard, StudentGuard] },
    ]
  },
  {
    path: 'admin',
    children: [
      { path: 'account', component: AccountComponent, canActivate: [AuthGuard, AdminGuard] },
      { path: 'dashboard', component: BoardAdminComponent, canActivate: [AuthGuard, AdminGuard] },
      { path: 'newsfeed', component: NewsfeedComponent, canActivate: [AuthGuard, AdminGuard] },
      { path: 'dashboard/add', component: BoardAdminAddComponent, canActivate: [AuthGuard, AdminGuard] },
      { path: 'dashboard/showKey', component: BoardAdminShowAuthKeyComponent, canActivate: [AuthGuard, AdminGuard] },
      { path: 'dashboard/llmFiles', component: BoardAdminLlmFilesComponent, canActivate: [AuthGuard, AdminGuard]},
      { path: 'dashboard/delete', component: BoardAdminDeleteUserComponent, canActivate: [AuthGuard, AdminGuard] },
    ]
  },
  {
    path: 'secretary',
    children: [
      { path: 'dashboard', component: BoardSecretaryComponent, canActivate: [AuthGuard, SecretaryGuard] },
      { path: 'account', component: AccountComponent, canActivate: [AuthGuard, SecretaryGuard] },
      { path: 'newsfeed', component: NewsfeedComponent, canActivate: [AuthGuard, SecretaryGuard] },
      { path: 'tickets', component: SecretaryTicketsComponent, canActivate: [AuthGuard, SecretaryGuard] },
      { path: 'dashboard/add', component: BoardSecretaryAddComponent, canActivate: [AuthGuard, SecretaryGuard] },
      { path: 'dashboard/upload-calendar', component: UploadCalendarComponent, canActivate: [AuthGuard, SecretaryGuard]},
      { path: 'dashboard/changeForms', component: BoardSecretaryChangeFormsComponent, canActivate: [AuthGuard, SecretaryGuard] },
      { path: 'dashboard/viewTickets', component: ViewRequestsComponent, canActivate: [AuthGuard, SecretaryGuard] },
      { path: 'dashboard/view-form/:id', component: ReviewRequestComponent, canActivate: [AuthGuard, SecretaryGuard]},
      { path: 'dashboard/addNews', component: AddNewsComponent, canActivate: [AuthGuard, SecretaryGuard] },
      { path: 'dashboard/manageNews', component: ManageNewsComponent, canActivate: [AuthGuard, SecretaryGuard] },
    ]
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
