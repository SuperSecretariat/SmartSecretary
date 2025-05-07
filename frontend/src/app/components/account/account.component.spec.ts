import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AccountComponent } from './account.component';
import { Router } from '@angular/router';

describe('AccountComponent', () => {
  let component: AccountComponent;
  let fixture: ComponentFixture<AccountComponent>;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, AccountComponent], 
    }).compileComponents();

    fixture = TestBed.createComponent(AccountComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should clear requests and reset user ID on logout', () => {
    component.requests = [
      { id: 1, formName: 'Formular1', status: 'În așteptare' }
    ];
    component.currentUserId = 'user123';
    spyOn(localStorage, 'removeItem');
    spyOn(router, 'navigate'); 

    component.logout();
    fixture.detectChanges();

    expect(component.requests.length).toBe(0);
    expect(component.currentUserId).toBe('');
    expect(localStorage.removeItem).toHaveBeenCalledWith('requests_user123');
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });
});