import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegisterComponent } from './components/register/register.component';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { CreateFormComponent } from './components/create-form/create-form.component';
import { SubmittedFormsComponent } from './components/submitted-forms/submitted-forms.component';
import { AccountComponent } from './components/account/account.component';
import { NewsfeedComponent } from './components/newsfeed/newsfeed.component';
import { AuthGuard } from './components/_helpers/auth.guard';
import { BoardAdminComponent } from './pages/board-admin/board-admin.component';
import { BoardAdminAddComponent } from './pages/board-admin/board-admin-add/board-admin-add.component';
import { BoardAdminShowAuthKeyComponent } from './pages/board-admin/board-admin-show-auth-key/board-admin-show-auth-key.component';
import {BoardSecretaryAddComponent} from './pages/board-secretary/board-secretary-add/board-secretary-add.component';
import { BoardSecretaryComponent } from './pages/board-secretary/board-secretary.component';
import { TicketStudentComponent } from './components/ticket-student/ticket-student.component';
import { ViewTicketsSecretaryComponent } from './pages/board-secretary/view-tickets-secretary/view-tickets.component';
import { SentTicketsSecretaryComponent } from './pages/board-secretary/board-secretary-add/sent-tickets-secretary/sent-tickets.component';


const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'home', component: HomeComponent },

  {
    path: 'student',
    children: [
      { path: 'home', component: HomeComponent },
      { path: 'create-form', component: CreateFormComponent },
      { path: 'submitted-forms', component: SubmittedFormsComponent },
      { path: 'account', component: AccountComponent, canActivate: [AuthGuard] },
      { path: 'newsfeed', component: NewsfeedComponent },
      { path: 'ticket-student' , component: TicketStudentComponent },
    ]
  },
  {
    path: 'admin',
    children: [
      { path: 'home', component: HomeComponent },
      { path: 'account', component: AccountComponent, canActivate: [AuthGuard] },
      { path: 'dashboard', component: BoardAdminComponent },
      { path: 'newsfeed', component: NewsfeedComponent },
      { path: 'dashboard/add', component: BoardAdminAddComponent },
      // { path: 'dashboard/createPost', component: BoardAdminCreatePost },
      { path: 'dashboard/showKey', component: BoardAdminShowAuthKeyComponent },
    ]
  },
  {
    path: 'secretary',
    children: [
      { path: 'home', component: HomeComponent },
      { path: 'dashboard', component: BoardSecretaryComponent },
      { path: 'account', component: AccountComponent, canActivate: [AuthGuard] },
      { path: 'newsfeed', component: NewsfeedComponent },
      { path: 'dashboard/add', component: BoardSecretaryAddComponent },
      { path: 'dashboard/viewTickets', component: ViewTicketsSecretaryComponent },
      { path: 'dashboard/sentTickets', component: SentTicketsSecretaryComponent }
    ]
  },
  //{ path: 'home', component: HomeComponent },
  //{ path: 'profile', component: ProfileComponent },
  //{ path: 'create-form', component: CreateFormComponent },
  ///{ path: 'submitted-forms', component: SubmittedFormsComponent },
  //{ path: 'account', component: AccountComponent },
  //{ path: 'newsfeed', component: NewsfeedComponent },
  //{ path: 'student', component: BoardStudentComponent },
  //{ path: 'mod', component: BoardSecretaryComponent },
  //{ path: 'admin', component: BoardAdminComponent },
  //{ path: '', redirectTo: 'home', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
