package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.JApplicationApp;
import com.mycompany.myapp.domain.Tutor;
import com.mycompany.myapp.repository.TutorRepository;
import com.mycompany.myapp.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static com.mycompany.myapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link TutorResource} REST controller.
 */
@SpringBootTest(classes = JApplicationApp.class)
public class TutorResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_AGE = 1;
    private static final Integer UPDATED_AGE = 2;

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restTutorMockMvc;

    private Tutor tutor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TutorResource tutorResource = new TutorResource(tutorRepository);
        this.restTutorMockMvc = MockMvcBuilders.standaloneSetup(tutorResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tutor createEntity(EntityManager em) {
        Tutor tutor = new Tutor()
            .name(DEFAULT_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .age(DEFAULT_AGE);
        return tutor;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tutor createUpdatedEntity(EntityManager em) {
        Tutor tutor = new Tutor()
            .name(UPDATED_NAME)
            .lastName(UPDATED_LAST_NAME)
            .age(UPDATED_AGE);
        return tutor;
    }

    @BeforeEach
    public void initTest() {
        tutor = createEntity(em);
    }

    @Test
    @Transactional
    public void createTutor() throws Exception {
        int databaseSizeBeforeCreate = tutorRepository.findAll().size();

        // Create the Tutor
        restTutorMockMvc.perform(post("/api/tutors")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(tutor)))
            .andExpect(status().isCreated());

        // Validate the Tutor in the database
        List<Tutor> tutorList = tutorRepository.findAll();
        assertThat(tutorList).hasSize(databaseSizeBeforeCreate + 1);
        Tutor testTutor = tutorList.get(tutorList.size() - 1);
        assertThat(testTutor.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTutor.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testTutor.getAge()).isEqualTo(DEFAULT_AGE);
    }

    @Test
    @Transactional
    public void createTutorWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = tutorRepository.findAll().size();

        // Create the Tutor with an existing ID
        tutor.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTutorMockMvc.perform(post("/api/tutors")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(tutor)))
            .andExpect(status().isBadRequest());

        // Validate the Tutor in the database
        List<Tutor> tutorList = tutorRepository.findAll();
        assertThat(tutorList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllTutors() throws Exception {
        // Initialize the database
        tutorRepository.saveAndFlush(tutor);

        // Get all the tutorList
        restTutorMockMvc.perform(get("/api/tutors?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tutor.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].age").value(hasItem(DEFAULT_AGE)));
    }
    
    @Test
    @Transactional
    public void getTutor() throws Exception {
        // Initialize the database
        tutorRepository.saveAndFlush(tutor);

        // Get the tutor
        restTutorMockMvc.perform(get("/api/tutors/{id}", tutor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tutor.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.age").value(DEFAULT_AGE));
    }

    @Test
    @Transactional
    public void getNonExistingTutor() throws Exception {
        // Get the tutor
        restTutorMockMvc.perform(get("/api/tutors/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTutor() throws Exception {
        // Initialize the database
        tutorRepository.saveAndFlush(tutor);

        int databaseSizeBeforeUpdate = tutorRepository.findAll().size();

        // Update the tutor
        Tutor updatedTutor = tutorRepository.findById(tutor.getId()).get();
        // Disconnect from session so that the updates on updatedTutor are not directly saved in db
        em.detach(updatedTutor);
        updatedTutor
            .name(UPDATED_NAME)
            .lastName(UPDATED_LAST_NAME)
            .age(UPDATED_AGE);

        restTutorMockMvc.perform(put("/api/tutors")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedTutor)))
            .andExpect(status().isOk());

        // Validate the Tutor in the database
        List<Tutor> tutorList = tutorRepository.findAll();
        assertThat(tutorList).hasSize(databaseSizeBeforeUpdate);
        Tutor testTutor = tutorList.get(tutorList.size() - 1);
        assertThat(testTutor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTutor.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testTutor.getAge()).isEqualTo(UPDATED_AGE);
    }

    @Test
    @Transactional
    public void updateNonExistingTutor() throws Exception {
        int databaseSizeBeforeUpdate = tutorRepository.findAll().size();

        // Create the Tutor

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTutorMockMvc.perform(put("/api/tutors")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(tutor)))
            .andExpect(status().isBadRequest());

        // Validate the Tutor in the database
        List<Tutor> tutorList = tutorRepository.findAll();
        assertThat(tutorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteTutor() throws Exception {
        // Initialize the database
        tutorRepository.saveAndFlush(tutor);

        int databaseSizeBeforeDelete = tutorRepository.findAll().size();

        // Delete the tutor
        restTutorMockMvc.perform(delete("/api/tutors/{id}", tutor.getId())
            .accept(TestUtil.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Tutor> tutorList = tutorRepository.findAll();
        assertThat(tutorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
