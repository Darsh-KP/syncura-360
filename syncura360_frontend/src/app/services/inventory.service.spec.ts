import { TestBed } from '@angular/core/testing';

import { inventoryService } from './inventory.service';

describe('inventoryService', () => {
  let service: inventoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(inventoryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
