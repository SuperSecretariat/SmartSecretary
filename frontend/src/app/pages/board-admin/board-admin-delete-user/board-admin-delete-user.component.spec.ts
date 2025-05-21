import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';

import { BoardAdminDeleteUserComponent } from './board-admin-delete-user.component';
import { StorageService } from '../../../components/_services/storage.service';

describe('BoardAdminDeleteUserComponent', () => {
  let component: BoardAdminDeleteUserComponent;
  let fixture: ComponentFixture<BoardAdminDeleteUserComponent>;
  let storageServiceSpy: jasmine.SpyObj<StorageService>;

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('StorageService', ['deleteUserAccount']);
    
    await TestBed.configureTestingModule({
      imports: [
        FormsModule
      ],
      declarations: [BoardAdminDeleteUserComponent],
      providers: [
        { provide: StorageService, useValue: spy },
        provideHttpClientTesting()
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BoardAdminDeleteUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});