import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './provider.reducer';

export const ProviderDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const providerEntity = useAppSelector(state => state.provider.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="providerDetailsHeading">Provider</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{providerEntity.id}</dd>
          <dt>
            <span id="provider">Provider</span>
          </dt>
          <dd>{providerEntity.provider}</dd>
          <dt>
            <span id="abn">Abn</span>
          </dt>
          <dd>{providerEntity.abn}</dd>
          <dt>
            <span id="contactPerson">Contact Person</span>
          </dt>
          <dd>{providerEntity.contactPerson}</dd>
        </dl>
        <Button tag={Link} to="/provider" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/provider/${providerEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProviderDetail;
