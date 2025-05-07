import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { CreateFormComponent } from './create-form.component';

describe('CreateFormComponent', () => {
  let component: CreateFormComponent;
  let fixture: ComponentFixture<CreateFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreateFormComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(CreateFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the list of current requests', () => {
    component.requests = [
      { id: 1, formName: 'Formular1', status: 'În așteptare' },
      { id: 2, formName: 'Formular2', status: 'În așteptare' }
    ];
    fixture.detectChanges();

    const requestItems = fixture.debugElement.queryAll(By.css('.request-item'));
    expect(requestItems.length).toBe(2);
    expect(requestItems[0].nativeElement.textContent).toContain('Formular1');
    expect(requestItems[1].nativeElement.textContent).toContain('Formular2');
  });

  it('should add a new request when a form is selected and the button is clicked', () => {
    component.forms = ['Formular1', 'Formular2', 'Formular3'];
    fixture.detectChanges();

    const formItem = fixture.debugElement.query(By.css('.form-list li')).nativeElement;
    formItem.click(); 
    fixture.detectChanges();

    const addButton = fixture.debugElement.query(By.css('button')).nativeElement;
    addButton.click();
    fixture.detectChanges();

    expect(component.requests.length).toBe(1);
    expect(component.requests[0].formName).toBe('Formular1');
    expect(component.requests[0].status).toBe('În așteptare');
  });

  it('should save current requests to localStorage and reset the list when submitted', () => {
    component.requests = [
      { id: 1, formName: 'Formular1', status: 'În așteptare' }
    ];
    spyOn(localStorage, 'setItem'); 

    const submitButton = fixture.debugElement.query(By.css('button')).nativeElement;
    submitButton.click();
    fixture.detectChanges();

    expect(localStorage.setItem).toHaveBeenCalledWith(
      `requests_${component.currentUserId}`,
      JSON.stringify([{ id: 1, formName: 'Formular1', status: 'În așteptare' }])
    );

    expect(component.requests.length).toBe(0);
  });
});