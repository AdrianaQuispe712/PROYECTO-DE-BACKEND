package com.hotel.reservas.login.config; 
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.hotel.reservas.login.model.Rol;
import com.hotel.reservas.login.model.Rol.NombreRol;
import com.hotel.reservas.login.model.Usuario;
import com.hotel.reservas.login.repository.RolRepository;
import com.hotel.reservas.login.repository.UsuarioRepository;
import com.hotel.reservas.model.Cliente;
import com.hotel.reservas.service.ClienteService;


@Component
// Esta clase se encarga de inicializar la base de datos con roles y un usuario administrador por defecto
// al iniciar la aplicación. Implementa CommandLineRunner para ejecutar código al inicio de la aplicación.
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Este método se ejecuta al iniciar la aplicación y se encarga de inicializar los roles y el usuario administrador
    // por defecto si no existen en la base de datos.
    @Autowired
    private ClienteService clienteService;

    @Override
    public void run(String... args) throws Exception {
        // Inicializar roles si no existen
        inicializarRoles();

        // Crear usuario administrador por defecto si no existe
        crearAdminPorDefecto();

        // Crear usuario cliente de prueba si no existe
        crearClientePrueba();
    }

    // Método para inicializar los roles en la base de datos
    // Si no existen, se crean los roles: ROL_ADMIN, ROL_CLIENTE y ROL_Cliente
   private void inicializarRoles() {
    if (rolRepository.count() == 0) {
        Rol rolAdmin = new Rol();
        rolAdmin.setNombre(NombreRol.ROL_ADMIN);
        rolRepository.save(rolAdmin);

        Rol rolCliente = new Rol();
        rolCliente.setNombre(NombreRol.ROL_CLIENTE);
        rolRepository.save(rolCliente);


        System.out.println("Roles inicializados en la base de datos");
    }
}

    
    // Método para crear un usuario administrador por defecto si no existe
    // Este usuario tendrá el username "admin", password "admin123" y el rol de administrador
    private void crearAdminPorDefecto() {
        if (!usuarioRepository.existsByUsername("admin4")) {
            Usuario admin = new Usuario();
            admin.setUsername("admin4");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin4@hotel.com");
            admin.setNombre("Administrador");
            admin.setApellido("Sistema");
            admin.setActivo(true);
            
            Set<Rol> roles = new HashSet<>();
            Rol rolAdmin = rolRepository.findByNombre(NombreRol.ROL_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
            roles.add(rolAdmin);
            admin.setRoles(roles);
            
            usuarioRepository.save(admin);
            
            System.out.println("Usuario administrador creado: admin / admin123");
        }
    }

    // Método para crear un usuario cliente de prueba
    private void crearClientePrueba() {
        if (!usuarioRepository.existsByUsername("cliente")) {
            // Crear usuario
            Usuario usuario = new Usuario();
            usuario.setUsername("cliente");
            usuario.setPassword(passwordEncoder.encode("cliente123"));
            usuario.setEmail("cliente@hotel.com");
            usuario.setNombre("Cliente");
            usuario.setApellido("Prueba");
            usuario.setActivo(true);

            // Asignar rol de cliente
            Set<Rol> roles = new HashSet<>();
            Rol rolCliente = rolRepository.findByNombre(NombreRol.ROL_CLIENTE)
                    .orElseThrow(() -> new RuntimeException("Error: Rol de cliente no encontrado."));
            roles.add(rolCliente);
            usuario.setRoles(roles);

            // Guardar usuario
            Usuario usuarioGuardado = usuarioRepository.save(usuario);
            System.out.println("Usuario cliente creado: cliente / cliente123");

            // Crear perfil de cliente asociado al usuario
            Cliente cliente = new Cliente();
            cliente.setNombre("Cliente Prueba");
            cliente.setEmail("cliente@hotel.com");
            cliente.setTelefono("555-1234");
            cliente.setUsuario(usuarioGuardado);

            // Guardar cliente
            Cliente clienteGuardado = clienteService.save(cliente);
            System.out.println("Perfil de cliente creado con ID: " + clienteGuardado.getId() + " para usuario ID: " + usuarioGuardado.getId());
        }
    }
    
}
