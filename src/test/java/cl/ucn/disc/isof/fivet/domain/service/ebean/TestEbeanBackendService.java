package cl.ucn.disc.isof.fivet.domain.service.ebean;

import cl.ucn.disc.isof.fivet.domain.model.Paciente;
import cl.ucn.disc.isof.fivet.domain.model.Persona;
import cl.ucn.disc.isof.fivet.domain.service.BackendService;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.junit.rules.Timeout;
import org.junit.runners.MethodSorters;

import java.util.List;

/**
 * Clase de testing del {@link BackendService}.
 */
@Slf4j
@FixMethodOrder(MethodSorters.DEFAULT)
public class TestEbeanBackendService {

    /**
     * Todos los test deben terminar antes de 60 segundos.
     */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(60);

    /**
     * Configuracion de la base de datos:  h2, hsql, sqlite
     * WARN: hsql no soporta ENCRYPT
     */
    private static final String DB = "h2";

    /**
     * Backend
     */
    private BackendService backendService;

    /**
     * Cronometro
     */
    private Stopwatch stopWatch;

    /**
     * Antes de cada test
     */
    @Before
    public void beforeTest() {

        stopWatch = Stopwatch.createStarted();
        log.debug("Initializing Test Suite with database: {}", DB);

        backendService = new EbeanBackendService(DB);
        backendService.initialize();
    }

    /**
     * Despues del test
     */
    @After
    public void afterTest() {

        log.debug("Test Suite done. Shutting down the database ..");
        backendService.shutdown();

        log.debug("Test finished in {}", stopWatch.toString());
    }

    /**
     * Test de la persona
     */
    @Test
    public void testPersona() {

        final String rut = "1-1";
        final String nombre = "Yo soy";

        // Insert into backend
        {
            final Persona persona = Persona.builder()
                    .nombre(nombre)
                    .rut(rut)
                    .password("durrutia123")
                    .tipo(Persona.Tipo.CLIENTE)
                    .build();

            persona.insert();

            log.debug("Persona to insert: {}", persona);
            Assert.assertNotNull("Objeto sin id", persona.getId());
        }

        // Get from backend v1
        {
            final Persona persona = backendService.getPersona(rut);
            log.debug("Persona founded: {}", persona);
            Assert.assertNotNull("Can't find Persona", persona);
            Assert.assertNotNull("Objeto sin id", persona.getId());
            Assert.assertEquals("Nombre distintos!", nombre, persona.getNombre());
            Assert.assertNotNull("Pacientes null", persona.getPacientes());
            Assert.assertTrue("Pacientes != 0", persona.getPacientes().size() == 0);

            // Update nombre
            persona.setNombre(nombre);
            persona.update();
        }

        // Get from backend v2
        {
            final Persona persona = backendService.getPersona(rut);
            log.debug("Persona founded: {}", persona);
            Assert.assertNotNull("Can't find Persona", persona);
            Assert.assertEquals("Nombres distintos!", nombre, persona.getNombre());
        }

    }

    /**
     * Test asociacion
     */
    @Test
    public void testPersonaPaciente() {

        {
            final Persona persona = Persona.builder()
                    .nombre("Ricardo")
                    .rut("190")
                    .password("a1b2c3")
                    .tipo(Persona.Tipo.CLIENTE)
                    .build();
            persona.insert();

            log.debug("Persona inserted: {}", persona);
            Assert.assertNotNull("Objeto sin id", persona.getId());

            String nombrePaciente1 = "Cachupin";
            Integer numeroFicha1 = 002;
            final Paciente paciente1 = Paciente.builder()
                    .nombre(nombrePaciente1)
                    .numero(numeroFicha1)
                    .build();
            paciente1.insert();

            String nombrePaciente2 = "Sheyla";
            Integer numeroFicha2 = 003;
            final Paciente paciente2 = Paciente.builder()
                    .nombre(nombrePaciente2)
                    .numero(numeroFicha2)
                    .build();
            paciente2.insert();

            log.debug("Paciente ingresado: {}", paciente1);
            persona.add(paciente1);

            log.debug("Paciente ingresado: {}", paciente2);
            persona.add(paciente2);

            persona.update();
            log.debug("Pacientes se agregaron a personas");

            List<Paciente> listaPacientes = backendService.getPacientes();
            Assert.assertNotNull("La lista distinto de null", listaPacientes);
            Assert.assertEquals("Cantidad de pacientes erroneos", 2, listaPacientes.size());

            for (int i = 0; i < listaPacientes.size(); i++){
                Assert.assertNotNull("Paciente sin nombre", listaPacientes.get(i).getNombre());
                Assert.assertNotNull("Paciente sin numero", listaPacientes.get(i).getNumero());
            }

            log.debug("Pruebas de obtencion de pacientes completada!!");
        }
    }

    /**
     * Testing de metodo getPacientesVeterinario
     */
    @Test
    public void testGetPaciente(){
        final Integer numeroPaciente = 001;
        final String nombre = "tacha";

        final Paciente paciente = Paciente.builder()
                .numero(numeroPaciente)
                .nombre(nombre)
                .build();
        paciente.insert();
        log.debug("creando mascota");

        Paciente pruebaPaciente = backendService.getPaciente(numeroPaciente);

        Assert.assertNotNull("Paciente null", pruebaPaciente);
        Assert.assertEquals("Nombres Distintos", nombre, pruebaPaciente.getNombre());
        Assert.assertEquals("Numeros Distintos", numeroPaciente,pruebaPaciente.getNumero());
    }
}
