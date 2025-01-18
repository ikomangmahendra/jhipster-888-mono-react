package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.ProviderAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Provider;
import com.mycompany.myapp.repository.ProviderRepository;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProviderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProviderResourceIT {

    private static final String DEFAULT_PROVIDER = "AAAAAAAAAA";
    private static final String UPDATED_PROVIDER = "BBBBBBBBBB";

    private static final String DEFAULT_ABN = "AAAAAAAAAA";
    private static final String UPDATED_ABN = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT_PERSON = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_PERSON = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/providers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProviderMockMvc;

    private Provider provider;

    private Provider insertedProvider;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Provider createEntity() {
        return new Provider().provider(DEFAULT_PROVIDER).abn(DEFAULT_ABN).contactPerson(DEFAULT_CONTACT_PERSON);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Provider createUpdatedEntity() {
        return new Provider().provider(UPDATED_PROVIDER).abn(UPDATED_ABN).contactPerson(UPDATED_CONTACT_PERSON);
    }

    @BeforeEach
    public void initTest() {
        provider = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedProvider != null) {
            providerRepository.delete(insertedProvider);
            insertedProvider = null;
        }
    }

    @Test
    @Transactional
    void createProvider() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Provider
        var returnedProvider = om.readValue(
            restProviderMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(provider)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Provider.class
        );

        // Validate the Provider in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertProviderUpdatableFieldsEquals(returnedProvider, getPersistedProvider(returnedProvider));

        insertedProvider = returnedProvider;
    }

    @Test
    @Transactional
    void createProviderWithExistingId() throws Exception {
        // Create the Provider with an existing ID
        provider.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProviderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(provider)))
            .andExpect(status().isBadRequest());

        // Validate the Provider in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProviders() throws Exception {
        // Initialize the database
        insertedProvider = providerRepository.saveAndFlush(provider);

        // Get all the providerList
        restProviderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(provider.getId().intValue())))
            .andExpect(jsonPath("$.[*].provider").value(hasItem(DEFAULT_PROVIDER)))
            .andExpect(jsonPath("$.[*].abn").value(hasItem(DEFAULT_ABN)))
            .andExpect(jsonPath("$.[*].contactPerson").value(hasItem(DEFAULT_CONTACT_PERSON)));
    }

    @Test
    @Transactional
    void getProvider() throws Exception {
        // Initialize the database
        insertedProvider = providerRepository.saveAndFlush(provider);

        // Get the provider
        restProviderMockMvc
            .perform(get(ENTITY_API_URL_ID, provider.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(provider.getId().intValue()))
            .andExpect(jsonPath("$.provider").value(DEFAULT_PROVIDER))
            .andExpect(jsonPath("$.abn").value(DEFAULT_ABN))
            .andExpect(jsonPath("$.contactPerson").value(DEFAULT_CONTACT_PERSON));
    }

    @Test
    @Transactional
    void getNonExistingProvider() throws Exception {
        // Get the provider
        restProviderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProvider() throws Exception {
        // Initialize the database
        insertedProvider = providerRepository.saveAndFlush(provider);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the provider
        Provider updatedProvider = providerRepository.findById(provider.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProvider are not directly saved in db
        em.detach(updatedProvider);
        updatedProvider.provider(UPDATED_PROVIDER).abn(UPDATED_ABN).contactPerson(UPDATED_CONTACT_PERSON);

        restProviderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProvider.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedProvider))
            )
            .andExpect(status().isOk());

        // Validate the Provider in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProviderToMatchAllProperties(updatedProvider);
    }

    @Test
    @Transactional
    void putNonExistingProvider() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        provider.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProviderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, provider.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(provider))
            )
            .andExpect(status().isBadRequest());

        // Validate the Provider in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProvider() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        provider.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProviderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(provider))
            )
            .andExpect(status().isBadRequest());

        // Validate the Provider in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProvider() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        provider.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProviderMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(provider)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Provider in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProviderWithPatch() throws Exception {
        // Initialize the database
        insertedProvider = providerRepository.saveAndFlush(provider);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the provider using partial update
        Provider partialUpdatedProvider = new Provider();
        partialUpdatedProvider.setId(provider.getId());

        partialUpdatedProvider.contactPerson(UPDATED_CONTACT_PERSON);

        restProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProvider.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProvider))
            )
            .andExpect(status().isOk());

        // Validate the Provider in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProviderUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProvider, provider), getPersistedProvider(provider));
    }

    @Test
    @Transactional
    void fullUpdateProviderWithPatch() throws Exception {
        // Initialize the database
        insertedProvider = providerRepository.saveAndFlush(provider);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the provider using partial update
        Provider partialUpdatedProvider = new Provider();
        partialUpdatedProvider.setId(provider.getId());

        partialUpdatedProvider.provider(UPDATED_PROVIDER).abn(UPDATED_ABN).contactPerson(UPDATED_CONTACT_PERSON);

        restProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProvider.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProvider))
            )
            .andExpect(status().isOk());

        // Validate the Provider in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProviderUpdatableFieldsEquals(partialUpdatedProvider, getPersistedProvider(partialUpdatedProvider));
    }

    @Test
    @Transactional
    void patchNonExistingProvider() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        provider.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, provider.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(provider))
            )
            .andExpect(status().isBadRequest());

        // Validate the Provider in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProvider() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        provider.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(provider))
            )
            .andExpect(status().isBadRequest());

        // Validate the Provider in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProvider() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        provider.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProviderMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(provider)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Provider in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProvider() throws Exception {
        // Initialize the database
        insertedProvider = providerRepository.saveAndFlush(provider);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the provider
        restProviderMockMvc
            .perform(delete(ENTITY_API_URL_ID, provider.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return providerRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Provider getPersistedProvider(Provider provider) {
        return providerRepository.findById(provider.getId()).orElseThrow();
    }

    protected void assertPersistedProviderToMatchAllProperties(Provider expectedProvider) {
        assertProviderAllPropertiesEquals(expectedProvider, getPersistedProvider(expectedProvider));
    }

    protected void assertPersistedProviderToMatchUpdatableProperties(Provider expectedProvider) {
        assertProviderAllUpdatablePropertiesEquals(expectedProvider, getPersistedProvider(expectedProvider));
    }
}
