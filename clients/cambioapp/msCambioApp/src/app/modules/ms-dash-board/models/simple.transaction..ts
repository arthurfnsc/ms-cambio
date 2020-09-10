import { Time } from '@angular/common';
import { ModelPagination } from 'src/app/share/pagination.model.';

export class SimpleTransaction extends ModelPagination {
    hour: Time;
    action: String;
    financialInstitution: String;
    value: String;
    rate: Number;
    currency: String;
}