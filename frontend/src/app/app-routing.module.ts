import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { RegisterComponent } from './components/register/register.component';
import { LoginComponent } from './components/login/login.component';
import { CreateFormComponent } from './components/create-form/create-form.component';
import { SubmittedFormsComponent } from './components/submitted-forms/submitted-forms.component';
import { AccountComponent } from './components/account/account.component';
import { NewsfeedComponent } from './components/newsfeed/newsfeed.component';
import { AuthGuard } from './components/_helpers/auth.guard';
import { BoardAdminComponent } from './pages/board-admin/board-admin.component';
import { BoardAdminAddComponent } from './pages/board-admin/board-admin-add/board-admin-add.component';
import {BoardAdminShowAuthKeyComponent} from './pages/board-admin/board-admin-show-auth-key/board-admin-show-auth-key.component';
import { BoardAdminDeleteUserComponent } from './pages/board-admin/board-admin-delete-user/board-admin-delete-user.component';
import {BoardSecretaryAddComponent} from './pages/board-secretary/board-secretary-add/board-secretary-add.component';
import { BoardSecretaryComponent } from './pages/board-secretary/board-secretary.component';
import { CompleteFormComponent } from './components/complete-form/complete-form.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { PubbleChatComponent } from './components/pubble-chat/pubble-chat.component';
import { ViewFormComponent } from './components/view-form/view-form.component';
import { BoardAdminLlmFilesComponent } from './pages/board-admin/board-admin-llm-files/board-admin-llm-files.component';
import { BoardSecretaryChangeFormsComponent } from './pages/board-secretary/board-secretary-change-forms/board-secretary-change-forms.component';
import { StudentTicketsComponent } from './pages/board-student/student-tickets/student-tickets.component';
import { SecretaryTicketsComponent } from './pages/board-secretary/secretary-tickets/secretary-tickets.component';
import { ModifyFormComponent } from './components/modify-form/modify-form.component';
import { ViewRequestsComponent } from './pages/board-secretary/view-requests/view-requests.component';
import { ReviewRequestComponent } from './pages/board-secretary/review-request/review-request.component';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },

  {
    path: 'student',
    children: [
      { path: 'create-form', component: CreateFormComponent },
      { path: 'submitted-forms', component: SubmittedFormsComponent },
      { path: 'account', component: AccountComponent, canActivate: [AuthGuard] },
      { path: 'newsfeed', component: NewsfeedComponent },
      { path: 'tickets', component: StudentTicketsComponent },
      { path: 'complete-form/:id', component: CompleteFormComponent },
      { path: 'pubble', component: PubbleChatComponent },
      { path: 'view-form/:id', component: ViewFormComponent},
      { path: 'modify-form/:id/:formId', component: ModifyFormComponent }
    ]
  },
  {
    path: 'admin',
    children: [
      { path: 'account', component: AccountComponent, canActivate: [AuthGuard] },
      { path: 'dashboard', component: BoardAdminComponent },
      { path: 'newsfeed', component: NewsfeedComponent },
      { path: 'dashboard/add', component: BoardAdminAddComponent },
      // { path: 'dashboard/createPost', component: BoardAdminCreatePost },
      { path: 'dashboard/showKey', component: BoardAdminShowAuthKeyComponent },
      { path: 'dashboard/llmFiles', component: BoardAdminLlmFilesComponent},
      { path: 'dashboard/delete', component: BoardAdminDeleteUserComponent },
    ]
  },
  {
    path: 'secretary',
    children: [
      { path: 'dashboard', component: BoardSecretaryComponent },
      { path: 'account', component: AccountComponent, canActivate: [AuthGuard] },
      { path: 'newsfeed', component: NewsfeedComponent },
      { path: 'tickets', component: SecretaryTicketsComponent },
      { path: 'dashboard/add', component: BoardSecretaryAddComponent },
      { path: 'dashboard/changeForms', component: BoardSecretaryChangeFormsComponent },
      { path: 'dashboard/viewTickets', component: ViewRequestsComponent },
      { path: 'dashboard/view-form/:id', component: ReviewRequestComponent}
    ]
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
