import { TestBed } from '@angular/core/testing';

import { MsSimpleTransactionServiceService } from './ms-simple-transaction-service.service';

describe('MsSimpleTransactionServiceService', () => {
  let service: MsSimpleTransactionServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MsSimpleTransactionServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
