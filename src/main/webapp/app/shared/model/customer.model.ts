import dayjs from 'dayjs';
import { CustomerType } from 'app/shared/model/enumerations/customer-type.model';
import { CustomerStatus } from 'app/shared/model/enumerations/customer-status.model';

export interface ICustomer {
  id?: number;
  registerDate?: dayjs.Dayjs;
  customerType?: keyof typeof CustomerType;
  firstName?: string;
  lastName?: string | null;
  primaryEmail?: string;
  primaryPhone?: string | null;
  status?: keyof typeof CustomerStatus;
}

export const defaultValue: Readonly<ICustomer> = {};
