import { TestBed } from '@angular/core/testing';

import { ServiceMgmtService } from './service-mgmt.service';

describe('ServiceMgmtService', () => {
  let service: ServiceMgmtService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ServiceMgmtService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
