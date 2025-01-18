import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Provider from './provider';
import ProviderDetail from './provider-detail';
import ProviderUpdate from './provider-update';
import ProviderDeleteDialog from './provider-delete-dialog';

const ProviderRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Provider />} />
    <Route path="new" element={<ProviderUpdate />} />
    <Route path=":id">
      <Route index element={<ProviderDetail />} />
      <Route path="edit" element={<ProviderUpdate />} />
      <Route path="delete" element={<ProviderDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ProviderRoutes;
