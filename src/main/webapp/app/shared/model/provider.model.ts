export interface IProvider {
  id?: number;
  provider?: string | null;
  abn?: string | null;
  contactPerson?: string | null;
}

export const defaultValue: Readonly<IProvider> = {};
